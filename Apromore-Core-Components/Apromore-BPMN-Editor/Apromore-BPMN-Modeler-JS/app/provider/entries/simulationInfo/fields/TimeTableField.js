var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
  TimetableHelper = require('../../../../helper/TimetableHelper');

module.exports = function (bpmnFactory, elementRegistry, translate) {

  var timetables;
  var selectedDefault;
  const LOG_TIMETABLE='Log timetable';

  return entryFactory.selectBox(translate, {
    id: 'arrivalTimetable',
    label: translate('arrivalTimetable.name'),
    modelProperty: 'arrivalTimetable',
    selectOptions: function (_element, _inputNode) {
      let timetableOptions = [];
      timetables = TimetableHelper.getTimetables(bpmnFactory, elementRegistry).values || [];
      let currentDefault = getCurrentDefaultItem();
      selectedDefault = currentDefault ? currentDefault.id : '';

      let timetablesWithNoEmptyName = timetables.filter(function (timetable) {
        return timetable.name;
      });

      timetablesWithNoEmptyName.forEach(function (timetable) {
        timetableOptions.push({
           name: timetable.name == LOG_TIMETABLE ? translate('logtimetable.name') : timetable.name,
           value: timetable.id
        });
      });

      return timetableOptions;
    },

    get: function (element, node) {
      return { arrivalTimetable: selectedDefault };
    },

    set: function (element, values, node) {
      selectedDefault = values.arrivalTimetable;

      let currentTimetable = getCurrentDefaultItem();
      let updateTimetable = getNewtDefaultItem(values.arrivalTimetable);

      if (currentTimetable && updateTimetable && currentTimetable.id === updateTimetable.id) {
        return;
      }

      if (currentTimetable && currentTimetable.default) {
        delete currentTimetable.default;
      }
      if (updateTimetable) {
        updateTimetable.default = 'true';
      }
    }

  });



  function getCurrentDefaultItem() {
    if (!timetables || timetables.length == 0) {
      return [];
    }
    let currentDefault = timetables.filter(function (timetable) {
      return timetable.default;
    });

    if (currentDefault && currentDefault.length > 0) {
      return currentDefault[0];
    }

  }

  function getNewtDefaultItem(newTimetableId) {
    if (!timetables || timetables.length == 0) {
      return [];
    }

    let newDefault = timetables.filter(function (timetable) {
      return timetable.id === newTimetableId;
    });

    if (newDefault && newDefault.length > 0) {
      return newDefault[0];
    }

  }
};
