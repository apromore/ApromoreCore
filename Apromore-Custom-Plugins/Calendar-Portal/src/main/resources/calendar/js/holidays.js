Ap.calendar = Ap.calendar || {};

zk.afterMount(function() {

  let YEAR = 2020;
  let params = {};
  let selects = {};

  Ap.calendar.initHolidays = function () {

    function setupYear () {
      let options = [];
      for (let i = YEAR; i < YEAR + 10; i++) {
        options.push({
          id: i + "",
          title: i + ""
        });
      }
      populate('year', options, YEAR);
    }

    function genOptions(options) {
      if (!options) { return null; }
      return Object.keys(options).map(function (key) {
        return {
          id: key,
          title: options[key]
        }
      });
    }

    function populate (id, options, selected) {
      let select = selects[id];
      select.clearOptions();
      if (options) {
        select.addOption(options);
      }
      select.clear();
      selected = selected || null;
      params[id] = selected;
      select.addItem(selected);
    }

    function setupCountry (selected) {
      let hd = new Holidays();
      let options = hd.getCountries();
      options = genOptions(options);
      populate('country', options, selected);
    }

    const onChange = {
      country: function (selected) {
        params['country'] = selected;
        let hd = new Holidays();
        let options = hd.getStates(params.country);
        options = genOptions(options);
        populate('state', options);
        if (!options) {
          $('#state').next().hide();
        } else {
          $('#state').next().show();
        }
        $('#region').next().hide();
      },
      state: function (selected) {
        params['state'] = selected;
        let hd = new Holidays();
        let options = hd.getRegions(params.country, params.state);
        options = genOptions(options);
        populate('region', options);
        if (!options) {
          $('#region').next().hide();
        } else {
          $('#region').next().show();
        }
      },
      region: function (selected) {
        params['region'] = selected;
      }
    };

    ['country', 'state', 'region'].forEach(function (id) {
      let $select =
          $('#' + id).selectize({
            valueField: 'id',
            labelField: 'title',
            searchField: 'title',
            dropdownParent: 'body',
            maxOptions: 20,
            create: false,
            onChange: (function (id) {
              return function (selected) {
                onChange[id](selected);
              };
            })(id)
          });
      selects[id] = $select[0].selectize;
    });
    $('#state').next().hide();
    $('#region').next().hide();

    // setupYear();
    setupCountry('AU');
  }

  Ap.calendar.submitHolidays = function () {
    let hd = new Holidays(params.country, params.state, params.region)
    let holidays = hd.getHolidays(YEAR).map((holiday) => {
      return {
        date: holiday.date.substring(0, 10),
        name: holiday.name
      }
    });
    if (holidays) {
      zAu.send(new zk.Event(zk.Widget.$('$saveBtn'), 'onSubmit', holidays));
    }
  }

  Ap.calendar.initHolidays();

});