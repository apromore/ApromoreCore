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
    Categories = require('./fields/Categories'),
    CategoriesEntry= require('./fields/CategoryEntries');

module.exports = function(element,bpmnFactory, elementRegistry, translate) {

  var entries = [];
 

  entries = entries.concat(DistributionFields(bpmnFactory, elementRegistry, translate));

  entries.push(ProcessInstancesField(bpmnFactory, elementRegistry, translate));
 
  let caseAttributesData = CaseAttributes(element,bpmnFactory,elementRegistry,translate);
  let caseAttributeEntries = CaseAttributeEntries(bpmnFactory, elementRegistry, translate,
    { getSelectedVariable: caseAttributesData.getSelectedVariable });
  let categories = Categories(element,bpmnFactory, elementRegistry, translate,
      { getSelectedVariable: caseAttributesData.getSelectedVariable });  
  let categoryDetailEntries = CategoriesEntry(bpmnFactory, elementRegistry, translate,
        { getSelectedCategory: categories.getSelectedCategory,getAllCategories: categories.getAllCategories });    

  entries = entries.concat(caseAttributesData.entries);
  entries = entries.concat(caseAttributeEntries);  
  entries = entries.concat(categories.entries);  
  entries = entries.concat(categoryDetailEntries); 

  entries.push(
    StartDateField(bpmnFactory, elementRegistry, translate),
    StartTimeField(bpmnFactory, elementRegistry, translate),
    TimeTableField(bpmnFactory, elementRegistry, translate),
    TrimStartField(bpmnFactory, elementRegistry, translate),
    TrimEndField(bpmnFactory, elementRegistry, translate),
    CurrencySelectBox(bpmnFactory, elementRegistry, translate)
  );

  return entries;
};