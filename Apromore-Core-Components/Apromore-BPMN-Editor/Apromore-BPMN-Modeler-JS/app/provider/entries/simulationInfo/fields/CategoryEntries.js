var CategoryNameField = require('./CategoryName');
const CategoryValueField = require('./CategoryValue');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {
  var entries = [];
  var getSelectedCategory = options.getSelectedCategory;

  entries.push(CategoryNameField(bpmnFactory, elementRegistry, translate,
    { getSelectedCategory: getSelectedCategory }));
  entries.push(CategoryValueField(bpmnFactory, elementRegistry, translate,
      { getSelectedCategory: getSelectedCategory }));  

  return entries;
};