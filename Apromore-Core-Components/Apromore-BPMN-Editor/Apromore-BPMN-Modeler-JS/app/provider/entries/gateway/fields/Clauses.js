var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  extensionElementsEntry = require('bpmn-js-properties-panel/lib/provider/camunda/parts/implementation/ExtensionElements'),
  suppressValidationError = require('../../../../helper/ValidationErrorHelper').suppressValidationError,
  SequenceFlowHelper = require('../../../../helper/SequenceFlowHelper'),
  validationErrorHelper = require('../../../../helper/ValidationErrorHelper'),
  CaseAttributeHelper = require('../../../../helper/CaseAttributeHelper');

module.exports = function (element, bpmnFactory, elementRegistry, translate, options, sequenceFlow) {

  let clause;
  let outgoingElementId = options.outgoingElementId;
  let gateway = options.gateway ;

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
      if(!isCaseAttributeExist()){
        Ap.common.notify(translate('gateway.caseAttribute.notfound.message'), 'error');
        return cmdHelper.addElementsTolist(element, sequenceFlow, 'values', []);
      }

      if(isGatewayProbabilityExist()){
        Ap.common.notify(translate('gateway.probability.to.condition.switch.constraint'), 'error');
        return cmdHelper.addElementsTolist(element, sequenceFlow, 'values', []);
      }

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
      suppressProbabilityErrorIfAny();
      createClauseCategoryError(outgoingElementId);
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
      suppressValidationError(bpmnFactory, elementRegistry, { elementId:  'clause-category-'+outgoingElementId });
      if (expression.values.length == 1) {
        if (sequenceFlow && sequenceFlow.values) {
          return cmdHelper.removeElementsFromList(element, sequenceFlow, 'values',
            null, [expression]);
        }
      }
      validateCurrentCondition();
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

  function isCaseAttributeExist(){
    let variables = CaseAttributeHelper.getAllVariables(bpmnFactory, elementRegistry);
    if (variables && variables.length > 0) {
      return true;
    }
    return false;
  }
  function isGatewayProbabilityExist() {
    let exist = undefined;
    gateway && gateway.outgoing && gateway.outgoing.forEach(function (outElement) {
      let seqFlow = SequenceFlowHelper.getSequenceFlowById(bpmnFactory, elementRegistry, outElement.id);
      if (seqFlow && seqFlow.executionProbability) {
        exist = true;
      }
    });
    return exist;
  }

  function createClauseCategoryError(outgoingElementId){
    validationErrorHelper.createValidationError(bpmnFactory, elementRegistry, {
      id: 'clause-category-' + outgoingElementId,
      elementId: 'clause-category-' + outgoingElementId,
      message: translate('invalid.empty.category')
    });
  }

  function isGatewayConditionExist() {
    let exist = undefined;
    gateway && gateway.outgoing &&  gateway.outgoing.forEach(function (outElement) {
      let seqFlow = SequenceFlowHelper.getSequenceFlowById(bpmnFactory, elementRegistry, outElement.id);
      if (seqFlow && seqFlow.values && seqFlow.values.length > 0) {
        exist = true;
      }
    });
    return exist;
  }

  function suppressProbabilityErrorIfAny() {
    gateway && gateway.outgoing && gateway.outgoing.forEach(function (outElement) {
      let seqFlow = SequenceFlowHelper.getSequenceFlowById(bpmnFactory, elementRegistry, outElement.id);
      if(seqFlow){
        delete seqFlow.executionProbability;
        delete seqFlow.rawExecutionProbability;
      }
      validationErrorHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: 'probability-field-'+outElement.id });
    });
  }

  function validateCurrentCondition(){
    let sequenceFlows = SequenceFlowHelper.getSequenceFlows(bpmnFactory, elementRegistry);
      if(!isGatewayConditionExist()){
      gateway && gateway.outgoing && gateway.outgoing.forEach(function (outElement) {
      validationErrorHelper.validateGatewayProbabilities(bpmnFactory, elementRegistry, translate, {
        probability: '',
        sequenceFlowsElement: sequenceFlows,
        outgoingElement: outElement,
        gateway: gateway,
        id: 'probability-field-' + outElement.id,
        description: translate('gateway.probability')
      });
    });
    }
  }  

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