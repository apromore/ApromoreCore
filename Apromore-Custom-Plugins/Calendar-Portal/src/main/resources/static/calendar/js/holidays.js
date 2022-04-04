/*
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
Ap.calendar = Ap.calendar || {};

zk.afterMount(function() {

  let BEGIN_YEAR = 2000;
  let YEAR = parseInt(new Date().getFullYear());
  let params = {};
  let selects = {};

  Ap.calendar.initHolidays = function () {

    function wrapper(id) {
      return $(`#ap-holidays-${id}-wrapper`);
    }

    function hide(id) {
      wrapper(id).hide();
    }

    function show(id) {
      wrapper(id).show();
    }

    function setupYear () {
      let options = [];
      for (let i = BEGIN_YEAR; i < YEAR + 10; i++) {
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
          hide('state');
        } else {
          show('state');
        }
        hide('region');
      },
      state: function (selected) {
        params['state'] = selected;
        let hd = new Holidays();
        let options = hd.getRegions(params.country, params.state);
        options = genOptions(options);
        populate('region', options);
        if (!options) {
          hide('region');
        } else {
          show('region');
        }
      },
      region: function (selected) {
        params['region'] = selected;
      },
      year: function (selected) {
        params['year'] = selected;
      }
    };

    ['year', 'country', 'state', 'region'].forEach(function (id) {
      let $select =
          $('#' + id).selectize({
            valueField: 'id',
            labelField: 'title',
            searchField: 'title',
            dropdownParent: 'body',
            create: false,
            onChange: (function (id) {
              return function (selected) {
                if (onChange[id]) {
                  onChange[id](selected);
                }
              };
            })(id)
          });
      selects[id] = $select[0].selectize;
    });
    show('year');
    show('country');
    hide('state');
    hide('region');
    setupYear();
    setupCountry('AU');
  }

  Ap.calendar.submitHolidays = function () {
    let hd = new Holidays(params.country, params.state, params.region)
    let holidays = hd.getHolidays(params.year).map((holiday) => {
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