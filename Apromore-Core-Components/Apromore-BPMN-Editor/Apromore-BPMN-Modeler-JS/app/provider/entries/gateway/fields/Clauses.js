var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  extensionElementsEntry = require('bpmn-js-properties-panel/lib/provider/camunda/parts/implementation/ExtensionElements'),
  suppressValidationError = require('../../../../helper/ValidationErrorHelper').suppressValidationError,
  SequenceFlowHelper = require('../../../../helper/SequenceFlowHelper');

module.exports = function (element, bpmnFactory, elementRegistry, translate, options, sequenceFlow) {

  let clause;
  let outgoingElementId = options.outgoingElementId;
  let variableEntry = extensionElementsEntry(element, bpmnFactory, {
    id: 'clauses-' + outgoingElementId,
    label: translate('gateway.expression.clauses.label'),
    modelProperty: 'variableName',
    idGeneration: false,
    onSelectionChange: function (element, node, event, scope) {
      let selection = (variableEntry && variableEntry.getSelected(element, node)) || {
        idx: -1
      };
      let expression = getExpression();
      clause = expression && expression.values && expression.values[selection.idx];
      if (clause) {
        SequenceFlowHelper.storeClauseCurrentSelection(outgoingElementId, clause);
      }
    },
    
    createExtensionElement: function (element, extensionElements, _value) {
      let expression = getExpression();
      if (!expression) {
        sequenceFlow = SequenceFlowHelper.createExpression(bpmnFactory, elementRegistry, sequenceFlow, true);
        expression = sequenceFlow && sequenceFlow.values && sequenceFlow.values[0];
      }
      clause = SequenceFlowHelper.createClause(bpmnFactory, elementRegistry, sequenceFlow, true);
      let cmd;
      if (clause) {
        SequenceFlowHelper.storeClauseCurrentSelection(outgoingElementId, clause);
        cmd = cmdHelper.addElementsTolist(element, expression, 'values', [clause]);
      }
      return cmd;
    },

    removeExtensionElement: function (element, _extensionElements, value, idx) {
      let expression = getExpression();
      let selectedClause = expression.values[idx];
      if (!expression || !selectedClause) {
        return;
      }
      SequenceFlowHelper.removeClauseSelection(outgoingElementId);
      suppressValidationError(bpmnFactory, elementRegistry, { elementId: this.id });
      return cmdHelper.removeElementsFromList(element, expression, 'values',
        null, [selectedClause]);
    },

    getExtensionElements: function (_element) {
      let expression = getExpression();
      return expression && expression.values || [];
    },

    setOptionLabelValue: function (element, _node, option, _property, _value, idx) {
      option.text = 'Clause ' + (idx + 1);
    }
  });

  function getExpression() {
    if (!sequenceFlow || !sequenceFlow.values || sequenceFlow.values.length == 0) {
      return;
    }
    return sequenceFlow.values[0];
  }

  function getSelectedClause(element, node) {
    let selection = (variableEntry && variableEntry.getSelected(element, node)) || {
      idx: -1
    };
    let expression = getExpression();
    clause = expression && expression.values && expression.values[selection.idx];
    if (!clause) {
      clause = SequenceFlowHelper.getClauseCurrentSelection(outgoingElementId);
    }
    return clause;

  }

  return {
    entries: variableEntry,
    getSelectedClause: getSelectedClause
  };
};