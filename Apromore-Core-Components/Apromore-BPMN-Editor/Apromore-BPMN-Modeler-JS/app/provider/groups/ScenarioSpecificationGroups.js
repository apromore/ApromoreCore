var simulationParametersEntries = require('../entries/simulationInfo/SimulationParametersEntries');

module.exports = function(element, bpmnFactory, elementRegistry, translate, shown) {

  var scenarioSpecificationGroup = {
    id: 'scenarioSpecificationGroup',
    label: translate('scenarioGroup.label'),
    entries: simulationParametersEntries(bpmnFactory, elementRegistry, translate),
    enabled: function(element) {
      return shown(element);
    }
  };

  return [
    scenarioSpecificationGroup
  ];
};