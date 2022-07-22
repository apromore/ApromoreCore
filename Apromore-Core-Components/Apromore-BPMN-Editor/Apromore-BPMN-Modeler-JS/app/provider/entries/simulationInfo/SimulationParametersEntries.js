var ProcessInstancesField = require('./fields/ProcessInstancesField'),
    StartDateField = require('./fields/StartDateField'),
    StartTimeField = require('./fields/StartTimeField'),
    TimeTableField= require('./fields/TimeTableField'),
    TrimStartField = require('./fields/TrimStartField'),
    TrimEndField = require('./fields/TrimEndField'),
    DistributionFields = require('./fields/DistributionFields'),
    CurrencySelectBox = require('./fields/CurrencySelectBox'),
    CaseAttributes = require('./fields/CaseAttributes'),
    CaseAttributeEntries = require('./fields/CaseAttributeEntries'),
    TableEntry =require('./fields/TableEntry'),
    LabelAttribute =require('./fields/LabelAttribute'),
    CustomErrorLabel = require ('./fields/CustomErrorLabel');

module.exports = function(element,bpmnFactory, elementRegistry, translate) {

  var entries = [];
 

  entries = entries.concat(DistributionFields(bpmnFactory, elementRegistry, translate));

  entries.push(ProcessInstancesField(bpmnFactory, elementRegistry, translate));
 
  let caseAttributesData = CaseAttributes(element,bpmnFactory,elementRegistry,translate);
  let caseAttributeEntries = CaseAttributeEntries(bpmnFactory, elementRegistry, translate,
    { getSelectedVariable: caseAttributesData.getSelectedVariable });

  entries = entries.concat(caseAttributesData.entries);
  entries.push(CustomErrorLabel({id:'variable-error-message', doValidation:caseAttributesData.doValidation}));

  entries.push(LabelAttribute(bpmnFactory, elementRegistry, translate,{ labelText: translate('general.caseAttribute.details') , id: 'categorical_case_attribute_title',getSelectedVariable: caseAttributesData.getSelectedVariable }));
  entries = entries.concat(caseAttributeEntries);
  let tableEntry = TableEntry(bpmnFactory, elementRegistry, translate,{ getSelectedVariable: caseAttributesData.getSelectedVariable });
  entries.push(tableEntry.tableEntry);
  entries.push(CustomErrorLabel({id:'category-error-message', doValidation:tableEntry.doValidation}));
  entries.push(
    StartDateField(bpmnFactory, elementRegistry, translate),
    StartTimeField(bpmnFactory, elementRegistry, translate),
    TimeTableField(bpmnFactory, elementRegistry, translate),
    TrimStartField(bpmnFactory, elementRegistry, translate),
    TrimEndField(bpmnFactory, elementRegistry, translate),
    CurrencySelectBox(bpmnFactory, elementRegistry, translate),
   
  );

  return entries;
};