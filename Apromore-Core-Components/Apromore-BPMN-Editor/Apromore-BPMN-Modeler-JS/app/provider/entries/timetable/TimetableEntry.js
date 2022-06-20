var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    extensionElementsEntry = require('bpmn-js-properties-panel/lib/provider/camunda/parts/implementation/ExtensionElements');

var suppressValidationError = require('../../../helper/ValidationErrorHelper').suppressValidationError;

var TimetableHelper = require('../../../helper/TimetableHelper');

module.exports = function(element, bpmnFactory, elementRegistry, translate) {

  var entries = [];

  TimetableHelper.createDefaultTimetable(bpmnFactory, elementRegistry, translate);

  var timetableEntry = extensionElementsEntry(element, bpmnFactory, {
    id: 'timetableEntry',
    label: translate('timetableEntry.label'),
    modelProperties: 'name',
    idGeneration: false,

    createExtensionElement: function(element, extensionElements, _value) {
      var timetables = TimetableHelper.getTimetables(bpmnFactory, elementRegistry),
          timetable = TimetableHelper.createTimetable(bpmnFactory, elementRegistry);

      return cmdHelper.addElementsTolist(element, timetables,
        'values', [timetable]);
    },

    removeExtensionElement: function(element, _extensionElements, value, idx) {
      var timetables = TimetableHelper.getTimetables(bpmnFactory, elementRegistry);

      var selectedTimetable = timetables.values[idx];

      if (!selectedTimetable) {
        return {};
      }

      if (selectedTimetable.get('default') === 'true') {
        if (window.Ap) {
          Ap.common.notify('The default timetable cannot be deleted.', 'error');
        }
        return cmdHelper.removeElementsFromList(element, timetables, 'values', null, []);
      }

      suppressValidationError(bpmnFactory, elementRegistry, { elementId: selectedTimetable.id });
      selectedTimetable.get('rules').values.forEach(function(rule) {
        suppressValidationError(bpmnFactory, elementRegistry, { id: rule.id });
      });

      return cmdHelper.removeElementsFromList(element, timetables, 'values',
        null, [selectedTimetable]);
    },

    getExtensionElements: function(_element) {
      return TimetableHelper.getTimetables(bpmnFactory, elementRegistry).values || [];
    },

    setOptionLabelValue: function(element, _node, option, _property, _value, idx) {
      var timetables = TimetableHelper.getTimetables(bpmnFactory, elementRegistry);

      var selectedTimetable = timetables.values[idx];

      var selectedTimetableName = selectedTimetable && selectedTimetable.name || translate('N/A');

      option.text = selectedTimetableName;
    }
  });

  var getSelectedTimetable = function(element, node) {
    var selection = (timetableEntry && timetableEntry.getSelected(element, node)) || {
      idx: -1
    };

    var timetables = TimetableHelper.getTimetables(bpmnFactory, elementRegistry);

    return timetables.values[selection.idx];
  };

  entries.push(timetableEntry);

  return {
    entries: entries,
    getSelectedTimetable: getSelectedTimetable
  };
};