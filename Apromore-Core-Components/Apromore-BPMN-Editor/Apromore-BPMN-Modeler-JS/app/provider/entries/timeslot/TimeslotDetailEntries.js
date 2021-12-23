var TimeslotNameField = require('./fields/TimeslotNameField'),
    FromWeekDaySelectBox = require('./fields/FromWeekDaySelectBox'),
    ToWeekDaySelectBox = require('./fields/ToWeekDaySelectBox'),
    FromTimeField = require('./fields/FromTimeField'),
    ToTimeField = require('./fields/ToTimeField');

module.exports = function(bpmnFactory, elementRegistry, translate, options) {

  var entries = [];

  var getSelectedTimetable = options.getSelectedTimetable,
      getSelectedTimeslot = options.getSelectedTimeslot;

  entries.push(TimeslotNameField(bpmnFactory, elementRegistry, translate, {
    getSelectedTimetable: getSelectedTimetable,
    getSelectedTimeslot: getSelectedTimeslot
  }));


  entries.push(FromWeekDaySelectBox(bpmnFactory, elementRegistry, translate, {
    getSelectedTimetable: getSelectedTimetable,
    getSelectedTimeslot: getSelectedTimeslot
  }));

  entries.push(ToWeekDaySelectBox(bpmnFactory, elementRegistry, translate, {
    getSelectedTimetable: getSelectedTimetable,
    getSelectedTimeslot: getSelectedTimeslot
  }));

  entries.push(FromTimeField(bpmnFactory, elementRegistry, translate, {
    getSelectedTimetable: getSelectedTimetable,
    getSelectedTimeslot: getSelectedTimeslot
  }));

  entries.push(ToTimeField(bpmnFactory, elementRegistry, translate, {
    getSelectedTimetable: getSelectedTimetable,
    getSelectedTimeslot: getSelectedTimeslot
  }));

  return entries;
};