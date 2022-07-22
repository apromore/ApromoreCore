
var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject;
var SequenceFlowHelper = require('../../../../helper/SequenceFlowHelper');
var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory');
var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');
var ToggleCustomFactory=require('./ToggleCustomFactory')

module.exports = function (bpmnFactory, elementRegistry, translate, options) {

    let toggleSwitch = ToggleCustomFactory(translate, {
        id: 'toggle_switch_' + options.groupId,
        label: ' Condition/Probability',
        modelProperty: 'isCondition',
        isOn: function () {
            return true;
        },
        get: function (element, node) {
            return { isCondition: SequenceFlowHelper.getProbalityByGroup(options.groupId) };
        }
        ,
        set: function (element, values, node) {
            options.gateway && options.gateway.forEach(function (outgoingElement) {
                let sequenceFlow = SequenceFlowHelper.getSequenceFlowByElementId(bpmnFactory, elementRegistry, outgoingElement.id, true);
                if (sequenceFlow) {
                    if (!values) {
                        if (sequenceFlow.values)
                            delete sequenceFlow.values;
                    } else {
                        if (!sequenceFlow.values || !sequenceFlow.values[0]) {
                            sequenceFlow = SequenceFlowHelper.createExpression(bpmnFactory, elementRegistry, outgoingElement, true);
                        }
                    }
                }

            });
            SequenceFlowHelper.storeProbalityByGroup(options.groupId, values.isCondition);
        }

    });

    toggleSwitch.cssClasses.push('apromore-toggle-switch');

    function getConditionChecked() {
        return SequenceFlowHelper.getProbalityByGroup(options.groupId);
    }
    return {
        toggleSwitch: toggleSwitch,
        getConditionChecked: getConditionChecked
    }

}