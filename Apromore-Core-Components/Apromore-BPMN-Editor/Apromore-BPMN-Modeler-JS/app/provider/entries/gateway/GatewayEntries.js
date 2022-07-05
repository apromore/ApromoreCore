var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject,
  entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
  cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
  SequenceFlowHelper = require('../../../helper/SequenceFlowHelper');

var validationErrorHelper = require('../../../helper/ValidationErrorHelper');
var fixNumber = require('../../../utils/Utils').fixNumber;
var Toggle= require('./fields/Toggle');
var ClauseWrapper = require('./fields/ClauseWrapper');


module.exports = function (bpmnFactory, elementRegistry, translate, options, element) {
  var sequenceFlows = SequenceFlowHelper.getSequenceFlows(bpmnFactory, elementRegistry);

  var entries = [];

  let toggle = Toggle(bpmnFactory, elementRegistry, translate,  {groupId: options.groupId,gateway: (options.gateway && options.gateway.outgoing && options.gateway.outgoing)});
  

  options.gateway && options.gateway.outgoing && options.gateway.outgoing.forEach(function (outgoingElement) {
    var sequenceFlow = SequenceFlowHelper.getSequenceFlowById(bpmnFactory, elementRegistry, outgoingElement.id);

    var textFieldDefault = entryFactory.textField(translate, {
      id: 'probability-field-' + outgoingElement.id,

      label: outgoingElement.targetRef.name ? outgoingElement.targetRef.name :
        getBusinessObject(outgoingElement).name ? getBusinessObject(outgoingElement).name :
          outgoingElement.targetRef.id,

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
          id: validationId,
          description: this.description
        });

        if (!error.message) {
          validationErrorHelper.suppressValidationError(bpmnFactory, elementRegistry, { id: validationId });
        }

        return { probability: error.message };
      }
    });

    if(!SequenceFlowHelper.getProbalityByGroup(options.groupId)){
      textFieldDefault.cssClasses.push(options.groupId);
      entries.push(textFieldDefault);

   
    }else{   
      let title = outgoingElement.targetRef.name ? outgoingElement.targetRef.name :
      getBusinessObject(outgoingElement).name ? getBusinessObject(outgoingElement).name :
        outgoingElement.targetRef.id;
        let cluaseWrapper = ClauseWrapper(bpmnFactory, elementRegistry, translate, {groupId: options.groupId , outgoingElementId:outgoingElement.id ,title:title ,getConditionChecked : toggle.getConditionChecked}, element);
        if (cluaseWrapper) {
          entries = entries.concat(cluaseWrapper);
        }
    }

  });

  entries.push(toggle.checkboxEntry);
  return entries;
};