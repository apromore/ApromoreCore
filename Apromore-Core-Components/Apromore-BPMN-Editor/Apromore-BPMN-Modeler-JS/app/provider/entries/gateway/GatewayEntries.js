var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject,
  entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
  cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  SequenceFlowHelper = require('../../../helper/SequenceFlowHelper');

var validationErrorHelper = require('../../../helper/ValidationErrorHelper');
var fixNumber = require('../../../utils/Utils').fixNumber;
var ClauseWrapper = require('./fields/ClauseWrapper');
var ToggleSwitch = require('./fields/ToggleSwitch');

module.exports = function (bpmnFactory, elementRegistry, translate, options, element) {
  var sequenceFlows = SequenceFlowHelper.getSequenceFlows(bpmnFactory, elementRegistry);

  var entries = [];

  let toggle = ToggleSwitch(bpmnFactory, elementRegistry, translate, { groupId: options.groupId, gateway: (options.gateway && options.gateway.outgoing && options.gateway.outgoing) });
  let gateway = (options.gateway && options.gateway.outgoing && options.gateway.outgoing);

  options.gateway && options.gateway.outgoing && options.gateway.outgoing.forEach(function (outgoingElement) {
    let sequenceFlow = SequenceFlowHelper.getSequenceFlowById(bpmnFactory, elementRegistry, outgoingElement.id);

    let textFieldDefault = entryFactory.textField(translate, {
      id: 'probability-field-' + outgoingElement.id,

      label: getLabelFromOutElement(outgoingElement),

      description: translate('gateway.probability'),
      modelProperty: 'probability',

      get: function (_element, _node) {
        if (sequenceFlow.rawExecutionProbability) {
          return { probability: sequenceFlow.rawExecutionProbability };
        }
        if (isNaN(sequenceFlow.executionProbability) || sequenceFlow.executionProbability === '') {
          return { probability: sequenceFlow.executionProbability };
        }

        return { probability: (+(Math.round(sequenceFlow.executionProbability + 'e+4') + 'e-2')).toString() };
      },

      set: function (_element, properties, _node) {
        var probability = fixNumber(properties.probability);
        if (isGatewayConditionExist() && probability) {
          Ap.common.notify(translate('gateway.condition.to.probability.switch.constraint'), 'error');
          return;
        }
        sequenceFlow.rawExecutionProbability = probability;
        return cmdHelper.updateBusinessObject(_element, sequenceFlow, {
          executionProbability: (isNaN(properties.probability) || properties.probability === '') ? properties.probability :
            (+(Math.round(probability + 'e+2') + 'e-4')).toString()
        });
      },

      validate: function (_element, values, _node) {
        var validationId = this.id;

        var error = validationErrorHelper.validateGatewayProbabilities(bpmnFactory, elementRegistry, translate, {
          probability: values.probability,
          sequenceFlowsElement: sequenceFlows,
          outgoingElement: outgoingElement,
          gateway: options.gateway,
          elementId : getLabelFromOutElement(outgoingElement),
          id: validationId,
          description: this.description
        });

        if (!error.message) {
          validationErrorHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });
        } else {
          if (isGatewayConditionExist()) {
            validationErrorHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });
            error.message = undefined;
          }

        }

        return { probability: error.message };
      }
    });

    if (!SequenceFlowHelper.getProbalityByGroup(options.groupId)) {
      entries.push(textFieldDefault);
    }
    else {
      let title = getLabelFromOutElement(outgoingElement);
      entries = entries.concat(ClauseWrapper(bpmnFactory, elementRegistry, translate, { groupId: options.groupId, outgoingElementId: outgoingElement.id, title: title, getConditionChecked: toggle.getConditionChecked, gateway:options.gateway }, element));
    }

  });

  function getLabelFromOutElement(outElement) {
    return outElement.targetRef.name ? outElement.targetRef.name :
      getBusinessObject(outElement).name ? getBusinessObject(outElement).name :
        outElement.targetRef.id;

  }

  function isGatewayConditionExist() {
    let exist = undefined;
    gateway && gateway.forEach(function (outElement) {
      let seqFlow = SequenceFlowHelper.getSequenceFlowById(bpmnFactory, elementRegistry, outElement.id);
      if (seqFlow && seqFlow.values && seqFlow.values.length > 0) {
        exist = true;
      }
    });
    return exist;
  }

  entries.push(toggle.toggleSwitch);
  return entries;
};