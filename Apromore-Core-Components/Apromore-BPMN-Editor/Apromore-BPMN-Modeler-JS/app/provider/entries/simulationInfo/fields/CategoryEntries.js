var CategoryNameField = require('./CategoryName');
const CategoryValueField = require('./CategoryValue');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {
  let entries = [];
  let getSelectedCategory = options.getSelectedCategory;
  let getAllCategories = options.getAllCategories;

  entries.push(CategoryNameField(bpmnFactory, elementRegistry, translate,
    { getSelectedCategory: getSelectedCategory }));
  entries.push(CategoryValueField(bpmnFactory, elementRegistry, translate,
      { getSelectedCategory: getSelectedCategory, getAllCategories: getAllCategories }));  

  return entries;
};