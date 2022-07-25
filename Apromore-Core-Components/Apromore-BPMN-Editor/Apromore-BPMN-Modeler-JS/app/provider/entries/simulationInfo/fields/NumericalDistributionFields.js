var NumericalDistributionEntries = require('./NumericalDistributionEntries');

module.exports = function(element,bpmnFactory, elementRegistry, translate,options) {
  return NumericalDistributionEntries(element,bpmnFactory, elementRegistry, translate, {
    id: 'Numerical-Distribution',
    elementName: 'Numerical Distribution',
    elementId : 'Numerical Distribution',
    label:translate('general.cases.numrical.distribution.label'),
    getSelectedVariable: options.getSelectedVariable
  });
};