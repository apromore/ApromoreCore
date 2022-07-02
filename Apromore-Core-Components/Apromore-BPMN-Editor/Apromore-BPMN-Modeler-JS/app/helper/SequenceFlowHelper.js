var elementHelper = require('bpmn-js-properties-panel/lib/helper/ElementHelper'),
  ProcessSimulationHelper = require('./ProcessSimulationHelper');

var SequenceFlowHelper = {};

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
        operator: '',
        values: []
      }, sequenceFlow, bpmnFactory
    );

    sequenceFlow.values.push(expression);
  }
  return sequenceFlow;
};

SequenceFlowHelper.createClause = function (bpmnFactory, elementRegistry, outgoingElementId, conditional) {
  let sequenceFlow = SequenceFlowHelper.getSequenceFlowById(bpmnFactory, elementRegistry, outgoingElementId, conditional);

  if (!sequenceFlow || !sequenceFlow.values || !conditional) {
    return;
  }

  let expression = sequenceFlow && sequenceFlow.values && sequenceFlow.values[0];
  if (!expression) {
    return;
  }
  var clause = elementHelper.createElement(
    'qbp:Clause',
    {
      operator: '',
      variableName: '',
      variableEnumValue: '',
    }, expression, bpmnFactory
  );
  expression.values.push(clause);
  return expression.values;
};


module.exports = SequenceFlowHelper;