var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    dateTimeField = require('../../DateTimeField');


module.exports = function(bpmnFactory, elementRegistry, translate, options) {

  var getSelectedTimetable = options.getSelectedTimetable;
  var getSelectedTimeslot = options.getSelectedTimeslot;

  return dateTimeField({
    id: 'timeslot-fromTime',
    type: 'time',
    label: translate('timeslot.beginTime'),
    modelProperty: 'fromTime',

    get: function(element, node) {

      var selectedTimetable = getSelectedTimetable(element, node),
          selectedTimeslot = getSelectedTimeslot(element, node, selectedTimetable);

      return { fromTime: selectedTimeslot && selectedTimeslot.fromTime && selectedTimeslot.fromTime.slice(0, 5) };
    },

    set: function(element, values, node) {

      var selectedTimetable = getSelectedTimetable(element, node),
          selectedTimeslot = getSelectedTimeslot(element, node, selectedTimetable);

      var fromTime = values.fromTime ? values.fromTime + ':00.000+00:00' : undefined;

      return cmdHelper.updateBusinessObject(element, selectedTimeslot, { fromTime: fromTime });
    }
  });
};