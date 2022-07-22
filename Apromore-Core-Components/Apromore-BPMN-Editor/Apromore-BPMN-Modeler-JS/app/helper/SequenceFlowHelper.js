var elementHelper = require('bpmn-js-properties-panel/lib/helper/ElementHelper'),
  ProcessSimulationHelper = require('./ProcessSimulationHelper'),
  createUUID = require('../utils/Utils').createUUID,
  CaseAttributeHelper = require('./CaseAttributeHelper');

var SequenceFlowHelper = {};
var probalityConditionMap = {};
var currentClauseSelection = {};

SequenceFlowHelper.getSequenceFlows = function (bpmnFactory, elementRegistry) {
  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);

  var sequenceFlows = processSimulationInfo.sequenceFlows;

  if (!sequenceFlows) {
    sequenceFlows = elementHelper.createElement('qbp:SequenceFlows',
      { values: [] }, processSimulationInfo, bpmnFactory
    );

    processSimulationInfo.sequenceFlows = sequenceFlows;
  }

  return sequenceFlows;
};

SequenceFlowHelper.getSequenceFlowById = function (bpmnFactory, elementRegistry, id, conditional) {
  var sequenceFlows = SequenceFlowHelper.getSequenceFlows(bpmnFactory, elementRegistry);

  var sequenceFlow = (sequenceFlows.get('values').filter(function (el) {
    return el.elementId === id;
  }) || [])[0];

  if (!sequenceFlow) {
    if (!conditional) {
      sequenceFlow = elementHelper.createElement(
        'qbp:SequenceFlow',
        {
          elementId: id,
          executionProbability: '',
          rawExecutionProbability: '',
          values: []
        }, sequenceFlows, bpmnFactory
      );
    } else {
      sequenceFlow = elementHelper.createElement(
        'qbp:SequenceFlow',
        {
          elementId: id,
          values: []
        }, sequenceFlows, bpmnFactory
      );

    }

    sequenceFlows.values.push(sequenceFlow);
  }

  return sequenceFlow;
};

SequenceFlowHelper.getSequenceFlowByElementId = function (bpmnFactory, elementRegistry, outgoingElementId, conditional) {
  let sequenceFlows = SequenceFlowHelper.getSequenceFlows(bpmnFactory, elementRegistry);

  let sequenceFlow = sequenceFlows && (sequenceFlows.get('values').filter(function (el) {
    return el.elementId === outgoingElementId;
  }) || [])[0];

  return sequenceFlow;
};


SequenceFlowHelper.getExpressionBySequenceFlowId = function (bpmnFactory, elementRegistry, outgoingElementId, conditional) {
  let sequenceFlow = SequenceFlowHelper.getSequenceFlowById(bpmnFactory, elementRegistry, outgoingElementId, conditional);

  if (!sequenceFlow || !conditional) {
    return;
  }

  let expression = sequenceFlow && sequenceFlow.values && sequenceFlow.values[0];
  if (!expression) {
    expression = elementHelper.createElement(
      'qbp:Expression',
      {
        operator: 'AND',
        values: []
      }, sequenceFlow, bpmnFactory
    );
    sequenceFlow.values = [expression];
  }
  return sequenceFlow;
};

SequenceFlowHelper.createExpression = function (bpmnFactory, elementRegistry, sequenceFlow, conditional) {
  if (!sequenceFlow || !conditional) {
    return;
  }

  let expression = sequenceFlow && sequenceFlow.values && sequenceFlow.values[0];
  if (!expression) {
    expression = elementHelper.createElement(
      'qbp:Expression',
      {
        operator: 'AND',
        values: []
      }, sequenceFlow, bpmnFactory
    );
    sequenceFlow.values = [expression];
  }
  return sequenceFlow;
};

SequenceFlowHelper.createClause = function (bpmnFactory, elementRegistry, sequenceFlow, conditional) {

  if (!sequenceFlow || !sequenceFlow.values || !conditional) {
    return;
  }

  let expression = sequenceFlow && sequenceFlow.values && sequenceFlow.values[0];
  if (!expression) {
    return;
  }

  let variables = CaseAttributeHelper.getAllVariables(bpmnFactory, elementRegistry);
  let defaultVariable = '';
  if (variables && variables.length > 0) {
    defaultVariable = variables[0].name;
  }

  let clause = elementHelper.createElement(
    'qbp:Clause',
    {
      operator: 'EQ',
      variableName: defaultVariable,
      variableEnumValue: '',
    }, expression, bpmnFactory
  );

  return clause;
};

SequenceFlowHelper.storeProbalityByGroup = function (groupId, isProbability) {
  probalityConditionMap[groupId] = isProbability && isProbability;
}

SequenceFlowHelper.getProbalityByGroup = function (groupId) {
  if (!probalityConditionMap || !probalityConditionMap[groupId]) {
    return false;
  }
  else {
    return probalityConditionMap[groupId];
  }
}

SequenceFlowHelper.storeClauseCurrentSelection = function (elementId, clause) {
  if (clause && elementId) {
    currentClauseSelection[elementId] = clause;
  }
}
SequenceFlowHelper.removeClauseSelection = function (elementId) {
  if (elementId) {
    currentClauseSelection[elementId] = undefined;
  }
}
SequenceFlowHelper.getClauseCurrentSelection = function (elementId) {
  if (!elementId || !currentClauseSelection[elementId]) {
    return;
  }
  else {
    return currentClauseSelection[elementId];
  }
}





SequenceFlowHelper.getExistingSequenceFlowById = function (bpmnFactory, elementRegistry, id, conditional) {
  var sequenceFlows = SequenceFlowHelper.getSequenceFlows(bpmnFactory, elementRegistry);
  var sequenceFlow = (sequenceFlows.get('values').filter(function (el) {
    return el.elementId === id;
  }) || [])[0];

  return sequenceFlow;
};

module.exports = SequenceFlowHelper;