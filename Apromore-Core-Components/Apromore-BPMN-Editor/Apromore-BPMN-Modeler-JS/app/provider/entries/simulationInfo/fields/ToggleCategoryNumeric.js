var ToggleCustomFactory = require('./ToggleCustomFactory');
var NumericalDistributionHelper = require('../../../../helper/NumericalDistributionHelper');
var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');


module.exports = function (bpmnFactory, elementRegistry, translate, options) {
    var getSelectedVariable = options.getSelectedVariable;
    let labelText = options.labelText;

    let toggleSwitch = ToggleCustomFactory(translate, {
        id: 'toggle_category_numeric',
        label: 'Category/Numeric',
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
    return {
        toggleSwitch: toggleSwitch
    }

}