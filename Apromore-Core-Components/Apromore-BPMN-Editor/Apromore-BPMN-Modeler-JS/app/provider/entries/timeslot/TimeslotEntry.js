var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    extensionElementsEntry = require('bpmn-js-properties-panel/lib/provider/camunda/parts/implementation/ExtensionElements'),
    RuleHelper = require('../../../helper/RuleHelper'),
    suppressValidationError = require('../../../helper/ValidationErrorHelper').suppressValidationError;

module.exports = function(element, bpmnFactory, elementRegistry, translate, options) {

  var entries = [];

  var getSelectedTimetable = options.getSelectedTimetable;

  var timeslotEntry = extensionElementsEntry(element, bpmnFactory, {
    id: 'timeslotEntry',
    label: translate('timeslotEntry.label'),
    modelProperties: 'name',
    idGeneration: false,

    createExtensionElement: function(element, _extensionElements, _value, node) {
      var selectedTimetable = getSelectedTimetable(element, node),
          rules = RuleHelper.getTimetableRulesElement(selectedTimetable),
          rule = RuleHelper.createRule(bpmnFactory);

      return cmdHelper.addElementsTolist(element, rules, 'values', [rule]);
    },

    removeExtensionElement: function(element, _extensionElements, _value, idx, node) {
      var selectedTimetable = getSelectedTimetable(element, node),
          rules = RuleHelper.getTimetableRules(selectedTimetable),
          selectedRule = rules[idx];

      if (selectedRule && rules.length <= 1) {
        return [];
      }

      suppressValidationError(bpmnFactory, elementRegistry, { elementId: selectedRule.id });

      return cmdHelper.removeElementsFromList(element, RuleHelper.getTimetableRulesElement(selectedTimetable),
        'values', null, [selectedRule]);
    },

    getExtensionElements: function(element, node) {
      var selectedTimetable = getSelectedTimetable(element, node);
      return RuleHelper.getTimetableRules(selectedTimetable);
    },

    setOptionLabelValue: function(element, node, option, _property, _value, idx) {
      var selectedTimetable = getSelectedTimetable(element, node);
      var timeslot = RuleHelper.getTimetableRules(selectedTimetable)[idx];

      option.text = timeslot && timeslot.name || translate('N/A');
    }
  });

  var getSelectedTimeslot = function(element, node, timetable) {
    var selection = (timeslotEntry && timeslotEntry.getSelected(element, node)) || {
      idx: -1
    };

    return RuleHelper.getTimetableRules(timetable)[selection.idx];
  };

  entries.push(timeslotEntry);

  return {
    entries: entries,
    getSelectedTimeslot: getSelectedTimeslot
  };
};