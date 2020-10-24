Ap.calendar = Ap.calendar || {};

zk.afterMount(function() {

  let binder = zkbind.$('$binder');

  binder.after('updateStartTime', function () {
    console.log('after updateStartTime');
    // setup();
  });

  binder.after('updateEndTime', function () {
    console.log('after updateEndTime');
    // setup();
  });

  binder.after('doItemListChange', function () {
    console.log('after doItemListChange');
    // setup();
  });

  Ap.calendar.updateAssignee = (rowGuid, name, access) => {
    zAu.send(new zk.Event(zk.Widget.$('$editBtn'), 'onUpdate', { rowGuid, name, access }));
  };

  Ap.calendar.changeStartDate = () => {
    console.log('Ap.calendar.changeStartDate', arguments);
  };

  Ap.calendar.updateStartTime = (item) => {
    binder.command('updateStartTimeExt', {item });
  };

  Ap.calendar.updateRow = (row, startHour, endHour) => {
    setup();
  };

  Ap.calendar.rebuild = () => {
    setup();
  };

  let startRow = null;
  let startHour = null;
  let startSelected = false;

  function getCell(row, hour) {
    return $(`.hour[id="${row}-${hour}"]`);
  }

  function selectCells(row, hour, selected) {
    let cell = getCell(row, hour);
    if (selected) {
      cell.addClass('selected');
    } else {
      cell.removeClass('selected');
    }
  }

  function selectRange(row, startHour, currentHour, selected) {
    for (let hour = startHour; hour <= currentHour; hour++) {
      selectCells(row, hour, selected);
    }
  }

  function initAll(row, startHour, endHour) {
    for (let hour = 0; hour < 24; hour++) {
      let selected = hour >= startHour && hour <= endHour;
      selectCells(row, hour, selected);
    }
  }

  function setup() {
    const wrapper = $('.ap-period-picker');
    const startTimes = $('.start-time input');
    const endTimes = $('.end-time input');

    console.log('setup');
    wrapper.each(function(row, el) {
      $(el).empty();
      for (hour = 0; hour < 24; hour++) {
        let hourEl = $(`<div class="hour" id="${row}-${hour}" data-row="${row}" data-hour="${hour}">` + ('0' + hour).slice(-2) + '</div>');
        hourEl.mousedown(function(event) {
          let cell = $(event.target);
          startRow = parseInt(cell.data('row'));
          startHour = parseInt(cell.data('hour'));
          startSelected = cell.hasClass('selected');
          selectCells(startRow, startHour, !startSelected);
        });
        hourEl.mouseover(function(event) {
          if (startRow === null) {
            return;
          }
          let cell = $(event.target);
          let currentHour = parseInt(cell.data('hour'));
          selectRange(startRow, startHour, currentHour, !startSelected);
        })
        hourEl.mouseup(function(event) {
          let cell = $(event.target);
          let endHour = parseInt(cell.data('hour'));
          selectRange(startRow, startHour, endHour, !startSelected);
          let prevSelectedHour = null;
          startHour = null;
          endHour = null;
          for (let hour = 0; hour < 24; hour++) {
            let cell = getCell(startRow, hour);
            if (cell.hasClass('selected')) {
              if (startHour === null) {
                startHour = hour;
              } else {
                if (endHour !== null || hour - prevSelectedHour !== 1) {
                  cell.removeClass('selected');
                }
              }
              prevSelectedHour = hour;
            } else {
              if (startHour !== null && endHour === null) {
                endHour = hour - 1;
              }
            }
          }
          if (startHour === null || endHour === null) {
            startHour = 0;
            endHour = 0;
          }
          binder.command('dummy');
          binder.command('updateItem', {
            row, // row index
            startHour,
            startMin: 0,
            endHour,
            endMin: 0
          });
          startRow = null;
          startHour = null;
        })
        $(el).append(hourEl);
      }
      let initStartHour = parseInt($(startTimes[row]).val());
      let initEndHour = parseInt($(endTimes[row]).val());
      initAll(row, initStartHour, initEndHour);
    });
  }

  setup();

  /*
  // init full calendar configs here
  calConfig.header = {
    left: 'prev, next today',
    center: 'title',
    right: 'month, agendaWeek, agendaDay'
  };

  calConfig.timezone = 'local';

  // set the theme option to true;
  calConfig.theme = true;

  // set the event color
  calConfig.eventColor = '#5687a8';

  // day click handler
  calConfig.dayClick = function(data, jsEvent, view) {
    var popOffset = [jsEvent.clientX, jsEvent.clientY];

    binder.command('doDayClicked', {dateClicked: data.toDate().getTime()})
    .after(function() {
      var newPop = zk.$('$newEventPop');
      newPop.open(newPop, popOffset);
    });
  };

  // event click handler
  calConfig.eventClick = function(event, jsEvent, view) {
    var popOffset = [jsEvent.clientX, jsEvent.clientY];
    binder.command('doEventClicked', {evtId: event.id})
    .after(function() {
      var modPop = zk.$('$modifyEventPop');
      modPop.open(modPop, popOffset);
    });
  }

  // event drop handler and event resize handler
  calConfig.eventResize = calConfig.eventDrop =
      function(event, delta, revertFunc, jsEvent, ui, view) {
        var startTime = event.start ?
            event.start.toDate().getTime() : 0,
            endTime = event.end ? event.end.toDate().getTime() : 0;

        binder.command('doEventChanged', {evtId: event.id, startTime: startTime, endTime: endTime});
      }

  $('#cal').fullCalendar(calConfig);

  // the event handler of after 'doCommandChange' from server
  binder.after('doEventsChange', function(events) {
    $('#cal').fullCalendar('removeEvents');
    $('#cal').fullCalendar('addEventSource', events);
    $('#cal').fullCalendar('rerenderEvents');
  });*/

});


