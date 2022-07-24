var simulationParametersEntries = require('../entries/simulationInfo/SimulationParametersEntries');

module.exports = function(element, bpmnFactory, elementRegistry, translate, shown, config) {

  var scenarioSpecificationGroup = {
    id: 'scenarioSpecificationGroup',
    label: translate('scenarioGroup.label'),
    entries: simulationParametersEntries(bpmnFactory, elementRegistry, translate, config),
    enabled: function(element) {
      return shown(element);
    }
  };

  return [
    scenarioSpecificationGroup
  ];
};