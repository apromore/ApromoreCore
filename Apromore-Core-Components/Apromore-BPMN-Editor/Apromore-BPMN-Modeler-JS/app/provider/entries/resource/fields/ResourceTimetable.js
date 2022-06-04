var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory'),
    cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper'),
    TimetableHelper = require('../../../../helper/TimetableHelper');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {

  var getSelectedResource = options.getSelectedResource;
  const LOG_TIMETABLE='Log timetable';

  return entryFactory.selectBox(translate, {
    id: 'resource-timetable',
    label: translate('resource.timetable'),
    modelProperty: 'timetableId',

    selectOptions: function(_element, _inputNode) {
      var timetableOptions = [];

      var timetables = TimetableHelper.getTimetables(bpmnFactory, elementRegistry).values || [];

      var timetablesWithNoEmptyName = timetables.filter(function(timetable) {
        return timetable.name;
      });

      timetablesWithNoEmptyName.forEach(function(timetable) {
        timetableOptions.push({
          name: timetable.name == LOG_TIMETABLE ? translate('logtimetable.name') : timetable.name,
          value: timetable.id
        });
      });

      return timetableOptions;
    },

    get: function(element, node) {
      var resource = getSelectedResource(element, node);
      return {
        timetableId: resource && resource.timetableId
      };
    },

    set: function(element, values, node) {
      var resource = getSelectedResource(element, node);
      return cmdHelper.updateBusinessObject(element, resource, {
        timetableId: values.timetableId
      });
    }
  });
};