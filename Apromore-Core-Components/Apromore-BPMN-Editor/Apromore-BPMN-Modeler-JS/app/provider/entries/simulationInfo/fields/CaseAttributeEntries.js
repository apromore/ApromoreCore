var VariableNameField = require('./VariableName');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {
  var entries = [];
  var getSelectedVariable = options.getSelectedVariable;

  entries.push(VariableNameField(bpmnFactory, elementRegistry, translate,
    { getSelectedVariable: getSelectedVariable }));

  return entries;
};