Ap.calendar = Ap.calendar || {};
Ap.calendar.ONE_RANGE_ONLY = true;

zk.afterMount(function() {

  /**
   * Update a specific range
   * Triggered after dialog box
   *
   * @param updatedRange A range that has been updated from dialog box
   */
  Ap.calendar.updateRange = function (dow, index, startHour, startMin, endHour, endMin) {

    let ranges = dayOfWeek[dow].ranges;
    let newRanges = [];
    let range, nextRange;
    let startMins, endMins;
    let len = ranges.length;
    let i = 0;
    ranges[index] = {
      startHour: startHour,
      startMin: startMin,
      endHour: endHour,
      endMin: endMin,
    }
    ranges.sort(function(range1, range2) {
      let mins1 = toMins(range1.startHour, range1.startMin);
      let mins2 = toMins(range2.startHour, range2.startMin);
      if (mins1 < mins2) {
        return -1;
      }
      return 1;
    })
    if (len === 1) {
      addToRanges(newRanges, ranges[i]);
    } else {
      let prev = ranges[i];
      let current, merged;
      for (let i = 1; i < ranges.length; i++) {
        current = ranges[i];
        curStart = toMins(current.startHour, current.startMin);
        curEnd = toMins(current.endHour, current.endMin);
        prevEnd = toMins(prev.endHour, prev.endMin);
        if (prevEnd >= curStart) {
          if (prevEnd >= curEnd) {
            merged = {
              startHour: prev.startHour,
              startMin: prev.startMin,
              endHour: prev.endHour,
              endMin: prev.endMin,
            }
          } else {
            merged = {
              startHour: prev.startHour,
              startMin: prev.startMin,
              endHour: current.endHour,
              endMin: current.endMin,
            }
          }
          prev = merged;
        } else {
          newRanges.push(prev);
          prev = current;
        }
      }
      newRanges.push(prev);
    }
    newRanges.forEach((range, index) => { range.index = index; });
    dayOfWeek[dow].ranges = newRanges;
    updateRow(dow);
    sendCalendarEvent('onUpdateRanges', { dow, ranges: newRanges });
  };

  Ap.calendar.updateRanges = function (dow, newRanges) {
    if (typeof newRanges === 'string') {
      newRanges = JSON.parse(newRanges);
    }
    newRanges.forEach((range, index) => { range.index = index; });
    dayOfWeek[dow].ranges = newRanges;
    updateRow(dow);
  };

  Ap.calendar.deleteRange = function (dow, index) {
    let ranges = dayOfWeek[dow].ranges;
    ranges = ranges.splice(index, 1);
    ranges.forEach((range, index) => { range.index = index; });
    dayOfWeek[dow].ranges = ranges;
    updateRow(dow);
    sendCalendarEvent('onUpdateRanges', { dow, ranges: ranges });
  };

  const TEMPLATES = {
    '9to5': {
      index: 0,
      startHour: 9,
      startMin: 0,
      endHour: 17,
      endMin: 0,
    },
    '24h': {
      index: 0,
      startHour: 0,
      startMin: 0,
      endHour: 24,
      endMin: 0,
    }
  };

  Ap.calendar.applyTemplate = function(name) {
    let template = TEMPLATES[name]

    if (template) {
      for (let i = 1; i < 6; i++) {
        dayOfWeek[i].ranges = [Object.assign({}, template)];
      }
      for (let i = 6; i < 8; i++) {
        dayOfWeek[i].workday = false;
      }
    }
    reset();
  };

  Ap.calendar.toggleWorkday = function(dow) {
    let workday = !dayOfWeek[dow].workday;
    dayOfWeek[dow].workday = workday;
    // Avoid propagate changes until the very end
    sendCalendarEvent('onUpdateWorkday', { dow, workday });
  };

  Ap.calendar.buildRow = function(dow) {
    buildRow(dow);
    updateRow(dow);
  };

  Ap.calendar.rebuild = function() {
    build();
    reset();
  };

  Ap.calendar.syncRows = function() {
    console.log('syncRows requested');
    setTimeout(
        () => { sendCalendarEvent('onSyncRows', {}); },
        600
    )
  };

  function sendCalendarEvent(event, params) {
    zAu.send(new zk.Event(zk.Widget.$('$actionBridge'), event, params));
  }

  function addToRanges(ranges, range) {
    ranges.push({
      index: ranges.length,
      startHour: range.startHour,
      startMin: range.startMin,
      endHour: range.endHour,
      endMin: range.endMin
    })
  }

  const BACKGROUND = 'ap-cal-bg';
  const TICK = 'ap-cal-tick';
  const INTERACT = 'ap-cal-x';
  const MINS = [0, 30];

  let mouseDown = false;
  let currentDOW = null;
  let startHour = null;
  let startMin = null;
  let startSelected = false;
  let currentHour = null;
  let currentMin = null;
  let endHour = null;
  let endMin = null;
  let tippies = {};
  let dayOfWeek = {};
  for (let i = 1; i < 8; i++) {
    dayOfWeek[i] = {
      ranges: [],
      workday: true
    };
    tippies[i] = {};
  }

  function digit2(num) {
    return ('0' + num).slice(-2);
  }

  function toMins(hour, min) {
    return hour * 60 + min;
  }

  function get(cell, attr) {
    let val = parseInt(cell.data(attr));
    if (isNaN(val)) {
      val = null;
    }
    return val;
  }

  function cellId(type, dow, hour, min) {
    return `${type}-${dow}-${hour}-${min}`;
  }

  function find(type, dow, hour, min) {
    let id = cellId(type, dow, hour, min)
    return $(`#${id}`);
  }

  function select(dow, hour, min, selected) {
    let cell = find(BACKGROUND, dow, hour, min);
    if (selected) {
      cell.addClass('selected');
    } else {
      cell.removeClass('selected');
    }
  }

  function isSelected(dow, hour, min) {
    let cell = find(BACKGROUND, dow, hour, min);
    return cell && cell.hasClass('selected');
  }

  /*
  Select cells based on precise hour and min
  */
  function selectCells(dow, startHour, startMin, toHour, toMin, selected) {
    for (let hour = startHour; hour <= toHour; hour++) {
      MINS.forEach(function (min) {
        let mins = toMins(hour, min);
        if (mins >= toMins(startHour, startMin) && mins <= toMins(toHour, toMin)) {
          select(dow, hour, min, selected);
        }
      });
    }
  }

  function isInRange(range, hour, min) {
    let mins = toMins(hour, min);
    const { startHour, startMin, endHour, endMin } = range;
    return mins >= toMins(startHour, startMin) && mins < toMins(endHour, endMin);
  }

  function clearTippies(dow) {
    if (dow) {
      let tips = tippies[dow];
      for (let key in tips) {
        try {
          tips[key].destroy();
        } catch (e) {
          // pass
        }
      }
      tippies[dow] = {};
    } else {
      for (let dow in tippies) {
        clearTippies(dow);
      }
    }
  }

  function selectRange(dow, range) {
    const { startHour, startMin, endHour, endMin } = range;
    const content = digit2(startHour) + ":" + digit2(startMin) + " - " + digit2(endHour) + ":" + digit2(endMin);

    for (let hour = startHour; hour <= endHour; hour++) {
      MINS.forEach(function (min) {
        let mins = toMins(hour, min);
        if (isInRange(range, hour, min)) {
          select(dow, hour, min, true);
          let id = cellId(INTERACT, dow, hour, min)
          const tip = tippy(document.getElementById(id), {
            theme: 'ap-calendar',
          });
          tip.setContent(content);
          tippies[dow][id] = tip;
        }
      });
    }
  }

  function findRange(dow, hour, min) {
    let ranges = dayOfWeek[dow].ranges;
    return ranges.find(function(range) {
      return isInRange(range, hour, min);
    });
  }

  function rangeTip(dow, hour, min) {
    let range = findRange(dow, hour, min);
    if (range) {
      console.log(JSON.stringify(range, null, 2));
      let { startHour, startMin, endHour, endMin } = range;
      const tip = digit2(startHour) + ":" + digit2(startMin) + " - " + digit2(endHour) + ":" + digit2(endMin);
      return tip;
    }
    return null;
  }

  function editRange(dow, hour, min) {
    let range = findRange(dow, hour, min);
    if (range) {
      sendCalendarEvent('onEditRange', { dow, ... range });
    }
  }

  function collectRanges(dow) {
    let ranges = [];
    let startHour = null;
    let startMin = null;
    let endHour = null;
    let endMin = null;

    for (let hour = 0; hour < 24; hour++) {
      MINS.forEach(function(min) {
        if (isSelected(currentDOW, hour, min)) {
          if (startHour === null) {
            startHour = hour;
            startMin = min;
          }
        } else {
          if (startHour !== null) {
            endHour = hour;
            endMin = min;
            addToRanges(ranges, {
              startHour,
              startMin,
              endHour,
              endMin
            });
            startHour = null;
          }
        }
      });
    }
    if (startHour !== null) {
        endHour = 24;
        endMin = 0;
        addToRanges(ranges, {
          startHour,
          startMin,
          endHour,
          endMin
        });
    }
    dayOfWeek[dow].ranges = ranges;
    updateRow(dow);
    return ranges;
  }

  function updateRow(dow) {
    clearTippies(dow);
    const wrapper = $(`.ap-period-picker[data-dow='${dow}']`);
    let ranges = dayOfWeek[dow].ranges;
    let workday = dayOfWeek[dow].workday;
    if (!workday) {
      ranges = dayOfWeek[dow].ranges = [];
      wrapper.addClass('offday');
    } else {
      wrapper.removeClass('offday');
    }
    wrapper.show();
    selectCells(dow, 0, 0, 23, 30, false);
    return ranges.forEach(function(range) {
      selectRange(dow, range);
    });
  }

  function reset() {
    clearTippies();
    mouseDown = false;
    currentDOW = null;
    startHour = null;
    startMin = null;
    startSelected = false;
    currentHour = null;
    currentMin = null;
    endHour = null;
    endMin = null;

    Object.keys(dayOfWeek).forEach(
        function(dow) {
          updateRow(dow);
        }
    );
  }

  function buildRowComps(dow, container, type) {
    for (hour = 0; hour < 24; hour++) {
      let el;
      let id = `${type}-${dow}-${hour}`;
      let klass = `class="${type}"`;
      let data = `data-dow="${dow}" data-hour="${hour}"`;
      if (type === TICK) {
        el = $(`<div id="${id}" ${klass} ${data}>` + digit2(hour) + `</div>`);
        $(container).append(el);
      } else if (type === BACKGROUND || type === INTERACT) {
        MINS.forEach(function (min) {
          el = $(`<div id="${id}-${min}" data-min="${min}" ${klass} ${data}></div>`);
          if (type === INTERACT) {
            el.mousedown(function(event) {
              let cell = $(event.target);
              mouseDown = true;
              setTimeout(function() {
                if (!mouseDown) {
                  currentDOW = null;
                  return;
                }
                currentDOW = get(cell, 'dow');
                startHour = get(cell, 'hour');
                startMin = get(cell, 'min');
                startSelected = isSelected(currentDOW, startHour, startMin);
                select(currentDOW, startHour, startMin, !startSelected);
              }, 200);
            });
            el.click(function(event) {
              currentDOW  = null;
              mouseDown = false;
              let cell = $(event.target);
              clickDOW = get(cell, 'dow');
              clickHour = get(cell, 'hour');
              clickMin = get(cell, 'min');
              editRange(clickDOW, clickHour, clickMin);
            })
            el.mouseout(function() {
              // if (currentTip) {
              //   currentTip.hide();
              //   currentTip = null;
              // }
            })
            el.mouseover(function(event) {
              let cell = $(event.target);
              let overDOW = get(cell, 'dow');
              currentHour = get(cell, 'hour');
              currentMin = get(cell, 'min');
              if (currentDOW !== null) {
                selectCells(currentDOW, startHour, startMin, currentHour, currentMin, !startSelected);
              }
            });
            el.mouseup(function(event) {
              mouseDown = false;
              if (currentDOW === null) {
                return;
              }
              let cell = $(event.target);
              let endHour = get(cell, 'hour');
              let endMin = get(cell, 'min');
              if (Ap.calendar.ONE_RANGE_ONLY) {
                selectCells(currentDOW, 0, 0, 23, 30, false);
              }
              selectCells(currentDOW, startHour, startMin, endHour, endMin, !startSelected);
              let ranges = collectRanges(currentDOW);
              // send changes to the backend
              sendCalendarEvent('onUpdateRanges', { dow: currentDOW, ranges: ranges });
              currentDOW = null;
              startHour = null;
              currentHour = null;
            });
          }
          $(container).append(el);
        })
      }
      $(container).append(el);
    }
  }

  function buildRow (dow) {
    const wrapper = $(`.ap-period-picker[data-dow='${dow}']`);
    $(wrapper).empty();
    [BACKGROUND, TICK, INTERACT].forEach(function(type) {
      let container = $(`<div class="ap-cal-dow-container" id="${type}-${dow}-container"></div>`);
      buildRowComps(dow, container, type);
      $(wrapper).append(container);
    })
  }

  window.addEventListener("mouseup", function(event) {
    let cell = $(event.target);
    let upDOW = get(cell, 'dow');
    if (upDOW !== null) {
      return;
    }
    mouseDown = false;
    currentDOW = null;
    startHour = null;
    currentHour = null;
  });

  function build() {
    const wrapper = $('.ap-period-picker');
    wrapper.each(function(index, el) {
      const dow = index + 1;
      buildRow(dow);
    });
  }

  zAu.send(new zk.Event(zk.Widget.$('$actionBridge'), 'onLoaded'));
});
