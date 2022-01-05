module.exports = {
  'simulationTab.label': 'General',
  'taskTab.label': 'Tasks',
  'timetableTab.label': 'Timetables',
  'resourceTab.label': 'Resources',
  'gatewayTab.label': 'Gateways',

  'scenarioGroup.label': 'Scenario Specification',
  'scenarioGroup.distribution.label': 'Inter arrival time',
  'scenarioGroup.processInstances.label': 'Total number of cases',
  'scenarioGroup.startDate.label': 'Scenario start date',
  'scenarioGroup.startTime.label': 'Scenario start time',
  'scenarioGroup.startExclude.label': '% to exclude from stats at the start',
  'scenarioGroup.endExclude.label': '% to exclude from stats at the end',
  'scenarioGroup.currency.label': 'Currency',

  'timetableEntry.label': 'Timetables',
  'arrivalTimetable.name': 'Arrival timetable',
  'timetable': 'Timetable',
  'timetable.name': 'Timetable name',
  'timetable.details': 'Timetable details',

  'timeslotEntry.label': 'Timeslots',
  'timeslot.name': 'Timeslot name',
  'timeslot.beginDay': 'Begin day',
  'timeslot.beginTime': 'Begin time',
  'timeslot.endDay': 'End day',
  'timeslot.endTime': 'End time',
  'timeslot.details': 'Timeslot details',

  'resources.label': 'Resources',
  'defaultResource.name': 'Default resource',
  'resource': 'Resource',
  'resource.timetable': 'Resource timetable',
  'resource.name': 'Resource name',
  'resource.totalAmount': 'Number of resources',
  'resource.costPerHour': 'Cost per hour',

  'task.distribution': 'Duration distribution',

  'gateway.exclusive': 'Exclusive (XOR)',
  'gateway.inclusive': 'Inclusive (OR)',
  'gateway.probability': 'Probability',

  'intermediateAndBoundaryEventsTab.label': 'Intermediate Catch and Boundary Events',

  'distribution.mean': 'Mean',
  'distribution.arg1': 'Agr1',
  'distribution.arg2': 'Agr2',
  'distribution.value': 'Value',
  'distribution.stdDeviation': 'Std deviation',
  'distribution.min': 'Minimum',
  'distribution.max': 'Maximum',
  'distribution.mode': 'Mode',
  'distribution.variance': 'Variance',
  'distribution.fixed': 'Fixed',
  'distribution.normal': 'Normal',
  'distribution.exponential': 'Exponential',
  'distribution.uniform': 'Uniform',
  'distribution.triangular': 'Triangular',
  'distribution.logNormal': 'Log-Normal',
  'distribution.gamma': 'Gamma',

  'timeUnit': 'Time unit',
  'seconds': 'Seconds',
  'minutes': 'Minutes',
  'hours': 'Hours',
  'days': 'Days',
  'monday': 'Monday',
  'tuesday': 'Tuesday',
  'wednesday': 'Wednesday',
  'thursday': 'Thursday',
  'friday': 'Friday',
  'saturday': 'Saturday',
  'sunday': 'Sunday',

  'invalid.empty {element}': '{element} must not be empty',
  'invalid.notDigit {element}': '{element} must be a valid positive number',
  'invalid.notInteger {element}': '{element} must be a positive integer',
  'invalid.exceed100% {element}': '{element} must not exceed 100%',
  'startExclude.invalid.message': '% to exclude from stats must be between 0 and 40',
  'endExclude.invalid.message': '% to exclude from stats must be between 0 and 40',
  'distribution.invalid.greaterMin {element}': '{element} must be greater than or equal to Minimum value',
  'distribution.invalid.lessMax {element}': '{element} must be less than or equal to Maximum value',
  'distribution.invalid.lessMode {element}': '{element} must be less than or equal to Mode value',
  'distribution.invalid.greaterMode {element}': '{element} must be greater than or equal to Mode value',
  'probability.invalid.sum': 'Sum of exclusive gateway probabilities must be equal to 100%',
  'invalid.endWeekDay {beginDay}': 'Invalid end day: select {beginDay} or any day after',
  'invalid.fromTime {endTime}': 'Invalid begin time: select time before {endTime}',
  'invalid.endTime {beginTime}': 'Invalid end time: select time after {beginTime}',

  'details': 'Details',
  'N/A': 'N/A',

  'properties': 'Properties',
  'metadata.properties': 'Metadata Properties',
  'attachments': 'Attachments'
};