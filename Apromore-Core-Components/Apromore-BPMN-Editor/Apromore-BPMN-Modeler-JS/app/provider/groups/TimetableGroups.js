var createTimetableEntry = require('../entries/timetable/TimetableEntry'),
    createTimetableDetailEntries = require('../entries/timetable/TimetableDetailEntries'),
    createTimeslotDetailEntries = require('../entries/timeslot/TimeslotDetailEntries');

module.exports = function(element, bpmnFactory, elementRegistry, translate) {

  var timetableEntry = createTimetableEntry(element, bpmnFactory, elementRegistry, translate);

  var timetableGroup = {
    id: 'timetableGroup',
    label: '',
    entries: timetableEntry.entries
  };

  var getSelectedTimetable = timetableEntry.getSelectedTimetable;

  var timetableDetailEntries = createTimetableDetailEntries(element, bpmnFactory, elementRegistry, translate,
    { getSelectedTimetable: getSelectedTimetable });

  var timetableDetailsGroup = {
    id: 'selectedTimetableDetails',
    entries: timetableDetailEntries.entries,
    enabled: function(element, node) {
      return getSelectedTimetable(element, node);
    },
    label: translate('timetable.details')
  };

  var getSelectedTimeslot = timetableDetailEntries.getSelectedTimeslot;

  var timeslotDetailEntries = createTimeslotDetailEntries(bpmnFactory, elementRegistry, translate, {
    getSelectedTimetable: getSelectedTimetable,
    getSelectedTimeslot: getSelectedTimeslot
  });

  var timeslotDetailsGroups = {
    id: 'timeslot-details',
    entries: timeslotDetailEntries,
    enabled: function(element, node) {
      var timetable = getSelectedTimetable(element, node);
      return getSelectedTimeslot(element, node, timetable);
    },
    label: translate('timeslot.details')
  };

  return [
    timetableGroup,
    timetableDetailsGroup,
    timeslotDetailsGroups
  ];
};