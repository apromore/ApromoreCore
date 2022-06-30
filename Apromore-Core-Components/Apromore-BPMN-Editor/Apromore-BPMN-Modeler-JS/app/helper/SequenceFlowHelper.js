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
          rawExecutionProbability: ''
        }, sequenceFlows, bpmnFactory
      );
    } else {
      sequenceFlow = elementHelper.createElement(
        'qbp:SequenceFlow',
        {
          elementId: id,
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

  let expression = sequenceFlow.expression;
  if (!expression) {
    expression = elementHelper.createElement(
      'qbp:Expression',
      {
        operator: '',
      }, sequenceFlow, bpmnFactory
    );
  }
  return expression;
};

SequenceFlowHelper.createClause = function (bpmnFactory, elementRegistry, outgoingElementId, conditional) {
  let sequenceFlow = SequenceFlowHelper.getSequenceFlowById(bpmnFactory, elementRegistry, outgoingElementId, conditional);

  if (!sequenceFlow || !sequenceFlow.expression || !conditional) {
    return;
  }

  let expression = sequenceFlow.expression;
  if (expression) {
    elementHelper.createElement(
      'qbp:Clause',
      {
        operator: '',
        variableName: '',
        variableEnumValue: '',
      }, expression, bpmnFactory
    );
  }
  expression.values.push(clause);

  return expression.values;
};


module.exports = SequenceFlowHelper;