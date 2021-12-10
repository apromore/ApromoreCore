var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    validationErrorHelper = require('../../../../helper/ValidationErrorHelper');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {

  var getSelectedTimetable = options.getSelectedTimetable;

  return entryFactory.textField(translate, {
    id: 'timetable-name',
    label: translate('timetable.name'),
    modelProperty: 'name',

    get: function(element, node) {
      var timetable = getSelectedTimetable(element, node);
      return { name: timetable && timetable.name };
    },

    set: function(element, values, node) {
      var timetable = getSelectedTimetable(element, node);

      return cmdHelper.updateBusinessObject(element, timetable,
        { name: values.name || undefined });
    },

    validate: function(element, values, node) {

      var selectedTimetable = getSelectedTimetable(element, node);

      if (selectedTimetable) {
        var validationId = selectedTimetable.id + this.id;

        var error = validationErrorHelper.validateTimetableName(bpmnFactory, elementRegistry, translate,
          {
            id: validationId,
            name: values.name,
            timetable: selectedTimetable,
          });

        if (!error.message) {
          validationErrorHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });
        }

        return { name: error.message };
      }
    }
  });
};