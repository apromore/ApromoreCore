var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  extensionElementsEntry = require('bpmn-js-properties-panel/lib/provider/camunda/parts/implementation/ExtensionElements'),
  suppressValidationError = require('../../../../helper/ValidationErrorHelper').suppressValidationError,
  SequenceFlowHelper = require('../../../../helper/SequenceFlowHelper'),
  validationErrorHelper = require('../../../../helper/ValidationErrorHelper'),
  CaseAttributeHelper = require('../../../../helper/CaseAttributeHelper'),
  getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject;

module.exports = function (element, bpmnFactory, elementRegistry, translate, options, sequenceFlow) {

  let clause;
  let outgoingElementId = options.outgoingElementId;
  let gateway = options.gateway;
  let title = options.title;

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
      if (!isCaseAttributeExist()) {
        Ap.common.notify(translate('gateway.caseAttribute.notfound.message'), 'error');
        return cmdHelper.addElementsTolist(element, sequenceFlow, 'values', []);
      }

      if (isGatewayProbabilityExist()) {
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
      createClauseCategoryError(outgoingElementId, title);
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
      if (expression.values.length == 1) {
        if (sequenceFlow && sequenceFlow.values) {
          return cmdHelper.removeElementsFromList(element, sequenceFlow, 'values',
            null, [expression]);
        }
      }

      return cmdHelper.removeElementsFromList(element, expression, 'values',
        null, [selectedClause]);
    },

    getExtensionElements: function (_element) {
      let expression = getExpression();
      validateCurrentCondition();
      return expression && expression.values || [];
    },

    setOptionLabelValue: function (element, _node, option, _property, _value, idx) {
      option.text = 'Clause ' + (idx + 1);
    }
  });

  function isCaseAttributeExist() {
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

  function createClauseCategoryError(outgoingElementId, title) {
    validationErrorHelper.createValidationError(bpmnFactory, elementRegistry, {
      id: 'clause-category-' + outgoingElementId,
      elementId: title,
      message: translate('invalid.empty.category')
    });
  }

  function isGatewayConditionExist() {
    let exist = undefined;
    gateway && gateway.outgoing && gateway.outgoing.forEach(function (outElement) {
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
      if (seqFlow) {
        delete seqFlow.executionProbability;
        delete seqFlow.rawExecutionProbability;
      }
      validationErrorHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: 'probability-field-' + outElement.id });
    });
  }

  function validateCurrentCondition() {
    let sequenceFlows = SequenceFlowHelper.getSequenceFlows(bpmnFactory, elementRegistry);
    if (!isGatewayConditionExist()) {
      gateway && gateway.outgoing && gateway.outgoing.forEach(function (outElement) {
        let seqFlow = SequenceFlowHelper.getSequenceFlowById(bpmnFactory, elementRegistry, outElement.id);
        if (seqFlow && !seqFlow.hasOwnProperty('executionProbability')) {
          seqFlow.executionProbability = '';
          seqFlow.rawExecutionProbability = '';
        }
        validationErrorHelper.validateGatewayProbabilities(bpmnFactory, elementRegistry, translate, {
          probability: '',
          sequenceFlowsElement: sequenceFlows,
          outgoingElement: outElement,
          gateway: gateway,
          elementId: getLabelFromOutElement(outElement),
          id: 'probability-field-' + outElement.id,
          description: translate('gateway.probability')
        });

        suppressValidationError(bpmnFactory, elementRegistry, { id: 'clause-category-' + outElement.id });

      });
    } else {
      gateway && gateway.outgoing && gateway.outgoing.forEach(function (outElement) {
        let seqFlow = SequenceFlowHelper.getSequenceFlowById(bpmnFactory, elementRegistry, outElement.id);
        let invalid = false;
        if (seqFlow && seqFlow.values && seqFlow.values.length > 0) {
          let expression = seqFlow.values[0];
          if (expression && expression.values && expression.values.length > 0) {
            expression.values.forEach(function (clause) {
              if (clause && isENUM(clause)) {
                if (clause.hasOwnProperty('variableEnumValue') && clause.variableEnumValue.trim() === '') {
                  createClauseCategoryError(outElement.id, getLabelFromOutElement(outElement));
                  invalid = true;
                }
              }
            });
          }
        }
        if (!invalid) {
          suppressValidationError(bpmnFactory, elementRegistry, { id: 'clause-category-' + outElement.id });
        }
      });
    }
  }

  function getLabelFromOutElement(outElement) {
    return outElement.targetRef.name ? outElement.targetRef.name :
      getBusinessObject(outElement).name ? getBusinessObject(outElement).name :
        outElement.targetRef.id;

  }

  function isENUM(clause) {
    var variables = CaseAttributeHelper.getVariables(bpmnFactory, elementRegistry);
    let selectedCaseAttribute = variables && variables.values && variables.values.filter(function (variable) {
      return variable.name === clause.variableName;
    });
    if (selectedCaseAttribute && selectedCaseAttribute.length > 0) {
      return selectedCaseAttribute[0].type && selectedCaseAttribute[0].type === 'ENUM';
    }
    return false;
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

  function getClauseCount(element, node) {
    let expression = getExpression();
    return expression && expression.values && expression.values.length || 0;
  }

  return {
    entries: variableEntry,
    getClauseCount: getClauseCount,
    getSelectedClause: getSelectedClause
  };
};