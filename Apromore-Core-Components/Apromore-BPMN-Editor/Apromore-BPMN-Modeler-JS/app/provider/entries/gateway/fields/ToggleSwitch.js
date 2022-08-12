
var SequenceFlowHelper = require('../../../../helper/SequenceFlowHelper');
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