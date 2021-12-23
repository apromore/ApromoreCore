var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    validationErrorHelper = require('../../../../helper/ValidationErrorHelper');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {

  var getSelectedTimetable = options.getSelectedTimetable;
  var getSelectedTimeslot = options.getSelectedTimeslot;

  return entryFactory.textField(translate, {
    id: 'timeslot-name',
    label: translate('timeslot.name'),
    modelProperty: 'name',

    get: function(element, node) {

      var selectedTimetable = getSelectedTimetable(element, node),
          selectedTimeslot = getSelectedTimeslot(element, node, selectedTimetable);

      return { name: selectedTimeslot && selectedTimeslot.name };
    },

    set: function(element, values, node) {

      var selectedTimetable = getSelectedTimetable(element, node),
          selectedTimeslot = getSelectedTimeslot(element, node, selectedTimetable);

      return cmdHelper.updateBusinessObject(element, selectedTimeslot, { name: values.name });
    },

    validate: function(element, values, node) {

      var selectedTimetable = getSelectedTimetable(element, node),
          selectedTimeslot = getSelectedTimeslot(element, node, selectedTimetable);

      if (selectedTimeslot) {
        var validationId = selectedTimeslot.id + this.id;

        var error = validationErrorHelper.validateTimeslotName(bpmnFactory, elementRegistry, translate, {
          id: validationId,
          name: values.name,
          timeslot: selectedTimeslot
        });

        if (!error.message) {
          validationErrorHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });
        }

        return { name: error.message };
      }
    }
  });
};