var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject,
    is = require('bpmn-js/lib/util/ModelUtil').is,
    getFlowElementsByType = require('../../helper/FlowElementsHelper'),
    createGatewayEntries = require('../entries/gateway/GatewayEntries');

module.exports = function(element, bpmnFactory, elementRegistry, translate) {

  var exclusiveGateway = getFlowElementsByType(element, 'bpmn:ExclusiveGateway'),
      inclusiveGateway = getFlowElementsByType(element, 'bpmn:InclusiveGateway');

  var gateways = exclusiveGateway.concat(inclusiveGateway);

  gateways = gateways.filter(function(gateway) {
    return !gateway.toBeRemoved && gateway.outgoing && gateway.outgoing.length > 1;
  });

  function gatewayToGroup(el) {
    var gateway = getBusinessObject(el),
        groupId = ['gateway', gateway.get('id'), 'group'].join('-'),
        groupLabel = ((gateway.name ? gateway.name : translate('N/A')) + ' ' +
        (is(gateway, 'bpmn:ExclusiveGateway') ? translate('gateway.exclusive') : translate('gateway.inclusive')));

    return {
      id: groupId,
      label: function(_element, _node) {
        return groupLabel;
      },
      entries: createGatewayEntries(bpmnFactory, elementRegistry, translate, { gateway: gateway })
    };
  }

  return gateways.map(function(el) {
    return gatewayToGroup(el);
  });
};