var CaseAttributes = require('./CaseAttributes'),
    CaseAttributeEntries = require('./CaseAttributeEntries'),
    TableEntry = require('./TableEntry'),
    LabelAttribute = require('./LabelAttribute'),
    CustomErrorLabel = require('./CustomErrorLabel'),
    ToggleCategoryNumeric = require('./ToggleCategoryNumeric'),
    NumericalDistributionFields = require('./NumericalDistributionFields');


module.exports = function (element, bpmnFactory, elementRegistry, translate) {

    var entries = [];

    let caseAttributesData = CaseAttributes(element, bpmnFactory, elementRegistry, translate);
    let caseAttributeEntries = CaseAttributeEntries(bpmnFactory, elementRegistry, translate,
        { getSelectedVariable: caseAttributesData.getSelectedVariable });

    entries = entries.concat(caseAttributesData.entries);
    entries.push(CustomErrorLabel({ id: 'variable-error-message', doValidation: caseAttributesData.doValidation }));

    //entries.push(LabelAttribute(bpmnFactory, elementRegistry, translate, { labelText: translate('general.caseAttribute.details'), id: 'categorical_case_attribute_title', getSelectedVariable: caseAttributesData.getSelectedVariable }));
    entries.push(ToggleCategoryNumeric(bpmnFactory, elementRegistry, translate,
        { labelText: translate('general.caseAttribute.details'), getSelectedVariable: caseAttributesData.getSelectedVariable }).toggleSwitch);

    entries = entries.concat(caseAttributeEntries);
    let tableEntry = TableEntry(bpmnFactory, elementRegistry, translate, { getSelectedVariable: caseAttributesData.getSelectedVariable });
    entries.push(tableEntry.tableEntry);
    entries.push(CustomErrorLabel({ id: 'category-error-message', doValidation: tableEntry.doValidation, getSelectedVariable: caseAttributesData.getSelectedVariable }));

    
    entries = entries.concat(NumericalDistributionFields(element,bpmnFactory, elementRegistry, translate,{getSelectedVariable: caseAttributesData.getSelectedVariable, getCurrentSelectionCaseAttribute:caseAttributesData.getCurrentSelectionCaseAttribute}));

    return entries;
};
