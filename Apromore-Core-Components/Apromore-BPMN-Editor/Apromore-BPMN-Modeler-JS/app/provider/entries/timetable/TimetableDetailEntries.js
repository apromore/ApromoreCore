var TimetableNameField = require('./fields/TimetableNameField'),
    createTimeslotEntry = require('../timeslot/TimeslotEntry');

module.exports = function(element, bpmnFactory, elementRegistry, translate, options) {

  var entries = [];

  var getSelectedTimetable = options.getSelectedTimetable;

  entries.push(TimetableNameField(bpmnFactory, elementRegistry, translate,
    { getSelectedTimetable : getSelectedTimetable }));

  var timeslotEntry = createTimeslotEntry(element, bpmnFactory, elementRegistry, translate,
    { getSelectedTimetable: getSelectedTimetable });

  entries = entries.concat(timeslotEntry.entries);

  return {
    entries: entries,
    getSelectedTimeslot : timeslotEntry.getSelectedTimeslot
  };
};
