var ToggleCustomFactory = require('./ToggleCustomFactory');
var NumericalDistributionHelper = require('../../../../helper/NumericalDistributionHelper');
var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');
var SequenceFlowHelper = require('../../../../helper/SequenceFlowHelper');


module.exports = function (bpmnFactory, elementRegistry, translate, options) {
    var getSelectedVariable = options.getSelectedVariable;
    let labelText = options.labelText;

    let toggleSwitch = ToggleCustomFactory(translate, {
        id: 'toggle_category_numeric',
        label: 'Switch to enter categorical or numerical attribute details',
        modelProperty: 'isNumeric',
        labelText: labelText,
        isOn: function () {
            return true;
        },
        hidden: function (element, node) {
            let selectedVariable = getSelectedVariable(element, node);
            return !selectedVariable;
        },
        get: function (element, node) {
            let selectedVariable = getSelectedVariable(element, node);
            return { isNumeric: selectedVariable && selectedVariable.type == 'NUMERIC' };
        },
        set: function (element, values, node) {
            let selectedVariable = getSelectedVariable(element, node);
            if(checkCaseAttributeNameAlreadyExist(selectedVariable)){
                Ap.common.notify(translate('general.attribute.used.in.gateway.switch'), 'error');
                return ;
            }
            if (selectedVariable) {
                if (selectedVariable.type && selectedVariable.type === 'NUMERIC' || selectedVariable.numeric) {
                    delete selectedVariable.numeric;
                }
                if (selectedVariable.type && selectedVariable.type === 'ENUM' || selectedVariable.enum) {
                    delete selectedVariable.values;
                }
                selectedVariable.type = (values && values.isNumeric) ? 'NUMERIC' : 'ENUM';
                if (selectedVariable.type === 'NUMERIC') {
                    selectedVariable.numeric = NumericalDistributionHelper.createNumericalAttribute(bpmnFactory, elementRegistry, { selectedVariable: selectedVariable })
                }
                NumericalDistributionHelper.storeCurrentCaseAttribute(selectedVariable);

            }
        }
    
    
    });

    function checkCaseAttributeNameAlreadyExist(selectedVariable) {
        let sequenceFlows = SequenceFlowHelper.getSequenceFlows(bpmnFactory, elementRegistry);
        let found = false;
        !found && sequenceFlows && sequenceFlows.values && sequenceFlows.values.forEach(sequenceFlow => {
          if (sequenceFlow && sequenceFlow.values && sequenceFlow.values[0]) {
            let expression = sequenceFlow.values[0];
            if (expression && expression.values) {
              !found && expression.values.forEach(clause => {
                if (clause && selectedVariable.name == clause.variableName) {
                  found =  true;
                }
              });
            }
          }
        });
        return found;
    }
    return {
        toggleSwitch: toggleSwitch
    }

}