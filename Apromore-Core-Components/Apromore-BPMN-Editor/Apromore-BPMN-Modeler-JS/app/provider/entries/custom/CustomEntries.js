var LinkedSubprocessField = require('./fields/LinkedSubprocessField');

module.exports = function(element, bpmnFactory, elementRegistry, translate) {

  var entries = [];

  entries.push(
    LinkedSubprocessField(element, bpmnFactory, elementRegistry, translate)
  );

  return entries;
};