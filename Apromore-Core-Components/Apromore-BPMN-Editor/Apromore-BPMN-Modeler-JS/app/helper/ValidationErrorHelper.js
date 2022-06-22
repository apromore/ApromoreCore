var elementHelper = require('bpmn-js-properties-panel/lib/helper/ElementHelper'),
    ProcessSimulationHelper = require('./ProcessSimulationHelper'),
    isDigit = require('../utils/Utils').isDigit,
    isValidNumber = require('../utils/Utils').isValidNumber;

var ValidationErrorHelper = {};

ValidationErrorHelper.getErrors = function(bpmnFactory, elementRegistry) {

  var processSimulationInfo = ProcessSimulationHelper.getProcessSimulationInfo(bpmnFactory, elementRegistry);

  var validationErrors = processSimulationInfo.validationErrors;

  if (!validationErrors) {
    validationErrors = elementHelper.createElement('qbp:Errors',
      { errors: [] }, processSimulationInfo, bpmnFactory);

    processSimulationInfo.validationErrors = validationErrors;
  }

  // Left-over gateway error fixer
  // Since validation is done as needed during user input,
  // it fails to remove flow error when element is deleted
  try {
    var flows = processSimulationInfo.sequenceFlows.values;
    var flowMap = {};
    flows.forEach(function(f) {
      flowMap[f.elementId] = true;
    });
    var toCull = {};
    validationErrors.errors.forEach(function(error) {
      if (error.id.startsWith('probability-field-')) {
        var flowId = error.id.substr(18);
        if (!(flowId in flowMap)) { // missing flow, deleted earlier
          toCull[error.id] = true;
        }
      }
    });

    validationErrors.errors = (validationErrors.errors || []).filter(function(error) {
      return !(error.id in toCull);
    });
  } catch (e) {
    // pass
  }

  return validationErrors;
};

ValidationErrorHelper.createValidationError = function(bpmnFactory, elementRegistry, options) {
  var id = options.id,
      elementId = options.elementId,
      elementName = options.elementName,
      message = options.message;

  var validationErrors = ValidationErrorHelper.getErrors(bpmnFactory, elementRegistry);

  var idx = validationErrors.errors.map(function(error) {
    return error.id;
  }).indexOf(id);

  if (idx < 0) {
    var processInstancesError = elementHelper.createElement('qbp:Error', {
      id: id,
      elementId: elementId,
      elementName: elementName,
      message: message
    }, validationErrors, bpmnFactory);

    validationErrors.errors.push(processInstancesError);
  } else {
    validationErrors.errors[idx].elementId = elementId;
    validationErrors.errors[idx].elementName = elementName;
    validationErrors.errors[idx].message = message;
  }
};

ValidationErrorHelper.suppressValidationError = function(bpmnFactory, elementRegistry, options) {
  var id = options.id,
      elementId = options.elementId;

  var validationErrors = ValidationErrorHelper.getErrors(bpmnFactory, elementRegistry);

  validationErrors.errors = (validationErrors.errors || []).filter(function(error) {
    var isId = error.id && id ? error.id !== id : true;
    var isElementId = error.elementId && elementId ? error.elementId !== elementId : true;

    return isId && isElementId;
  });
};

ValidationErrorHelper.validateProcessInstances = function(bpmnFactory, elementRegistry, translate, options) {
  var id = options.id,
      label = options.label,
      processInstances = options.processInstances,
      message;

  if (!processInstances || processInstances.trim() === '') {
    message = translate('invalid.empty {element}', { element: label });
  } else if (!isValidNumber(processInstances)) {
    message = translate('invalid.notDigit {element}', { element: label });
  } else if (parseInt(processInstances).toString() !== processInstances) {
    message = translate('invalid.notInteger {element}', { element: label });
  }

  if (message) {
    this.createValidationError(bpmnFactory, elementRegistry, {
      id: id,
      elementId: label,
      message: message
    });
  }

  return { message: message };
};

ValidationErrorHelper.validateTrimStartProcessInstances = function(bpmnFactory, elementRegistry, translate, options) {
  var id = options.id,
      label = options.label,
      trimStartProcessInstances = options.trimStartProcessInstances,
      message;

  if (trimStartProcessInstances !== undefined && !isValidNumber(trimStartProcessInstances)) {
    message = translate('invalid.notDigit {element}', { element: label });
  } else if (trimStartProcessInstances < 0 || trimStartProcessInstances > 40) {
    message = translate('startExclude.invalid.message');
  }

  if (message) {
    this.createValidationError(bpmnFactory, elementRegistry, { id: id, message: message });
  }

  return { message: message };
};

ValidationErrorHelper.validateTrimEndProcessInstances = function(bpmnFactory, elementRegistry, translate, options) {
  var id = options.id,
      label = options.label,
      trimEndProcessInstances = options.trimEndProcessInstances,
      message;

  if (trimEndProcessInstances !== undefined && !isValidNumber(trimEndProcessInstances)) {
    message = translate('invalid.notDigit {element}', { element: label });
  } else if (trimEndProcessInstances < 0 || trimEndProcessInstances > 40) {
    message = translate('endExclude.invalid.message');
  }

  if (message) {
    this.createValidationError(bpmnFactory, elementRegistry, { id: id, message: message });
  }

  return { message: message };
};

ValidationErrorHelper.validateDistributionMean = function(bpmnFactory, elementRegistry, translate, options) {
  var id = options.id,
      label = options.label,
      elementId = options.elementId,
      distribution = options.distribution,
      mean = options.mean,
      rawMean = options.rawMean,
      message;

  if (!rawMean || rawMean.trim() === '') {
    message = translate('invalid.empty {element}', { element: label });
  } else if (!isValidNumber(rawMean)) {
     message = translate('invalid.notDigit {element}', { element: label });
  } else if (distribution.type === 'TRIANGULAR') {
    if (parseFloat(mean) > parseFloat(distribution.arg2)) {
      message = translate('distribution.invalid.lessMax {element}', { element: label });
    }
  }

  if (message) {
    this.createValidationError(bpmnFactory, elementRegistry, {
      id: id,
      elementId: elementId,
      message: message
    });
  }

  return { message: message };
};

ValidationErrorHelper.validateDistributionArg1 = function(bpmnFactory, elementRegistry, translate, options) {
  var id = options.id,
      label =options.label,
      elementId = options.elementId,
      distribution = options.distribution,
      rawArg1 = options.rawArg1,
      arg1 = options.arg1,
      message;

  if (!rawArg1 || rawArg1.trim() === '') {
      message = translate('invalid.empty {element}', { element: label });
  } else if (!isValidNumber(rawArg1)) {
    message = translate('invalid.notDigit {element}', { element: label });
  } else if (distribution.type === 'TRIANGULAR') {
    if (parseFloat(arg1) > parseFloat(distribution.arg2)) {
      message = translate('distribution.invalid.lessMax {element}', { element: label });
    } else if (parseFloat(arg1) > parseFloat(distribution.mean)) {
      message = translate('distribution.invalid.lessMode {element}', { element: label });
    }
  }

  if (message) {
    this.createValidationError(bpmnFactory, elementRegistry, {
      id: id,
      elementId: elementId,
      message: message
    });
  }

  return { message: message };
};

ValidationErrorHelper.validateDistributionArg2 = function(bpmnFactory, elementRegistry, translate, options) {
  var id = options.id,
      label = options.label,
      elementId = options.elementId,
      distribution = options.distribution,
      rawArg2 = options.rawArg2,
      arg2 = options.arg2,
      message;

  if (!rawArg2 || rawArg2.trim() === '') {
    message = translate('invalid.empty {element}', { element: label });
  } else if (!isValidNumber(rawArg2)) {
    message = translate('invalid.notDigit {element}', { element: label });
  } else if (distribution.type === 'UNIFORM' && parseFloat(distribution.arg1) > parseFloat(arg2)) {
    message = translate('distribution.invalid.greaterMin {element}', { element: label });
  }

  if (message) {
    this.createValidationError(bpmnFactory, elementRegistry, {
      id: id,
      elementId: elementId,
      message: message
    });
  }

  return { message: message };
};

ValidationErrorHelper.validateTimetableName = function(bpmnFactory, elementRegistry, translate, options) {
  var id = options.id,
      name = options.name,
      timetable = options.timetable,
      message;

  if (!name || name.trim() === '') {
    message = translate('invalid.empty {element}', { element: translate('timetable.name') });
  }

  if (timetable && message) {
    this.createValidationError(bpmnFactory, elementRegistry, {
      id: id,
      elementId: timetable.id,
      message: message
    });
  }

  return { message: message };

};

ValidationErrorHelper.validateTimeslotName = function(bpmnFactory, elementRegistry, translate, options) {
  var id = options.id,
      name = options.name,
      timeslot = options.timeslot,
      message;

  if (!name || name.trim() === '') {
    message = translate('invalid.empty {element}', { element: translate('timeslot.name') });
  }

  if (message) {
    timeslot && this.createValidationError(bpmnFactory, elementRegistry, {
      id: id,
      elementId: timeslot.id,
      message: message
    });
  }

  return { message: message };
};

ValidationErrorHelper.validateResourceName = function(bpmnFactory, elementRegistry, translate, options) {
  var id = options.id,
      name = options.name,
      label = options.label,
      resource = options.resource,
      message;

  if (!name || name.trim() === '') {
    message = translate('invalid.empty {element}', { element: label });
  }

  if (resource && message) {
    this.createValidationError(bpmnFactory, elementRegistry, {
      id: id,
      elementId: resource.id,
      message: message
    });
  }

  return { message : message };
};

ValidationErrorHelper.validateResourceNumber = function(bpmnFactory, elementRegistry, translate, options) {
  var id = options.id,
      label = options.label,
      resource = options.resource,
      totalAmount = options.totalAmount,
      message;

  if (!totalAmount || totalAmount.trim() === '') {
    message = translate('invalid.empty {element}', { element: label });
  } else if (!isValidNumber(totalAmount)) {
    message = translate('invalid.notDigit {element}', { element: label });
  } else if (parseInt(totalAmount).toString() !== totalAmount) {
    message = translate('invalid.notInteger {element}', { element: label });
  }

  if (resource && message) {
    this.createValidationError(bpmnFactory, elementRegistry, {
      id: id,
      elementId: resource.id,
      message: message
    });
  }

  return { message: message };
};

ValidationErrorHelper.validateResourceCostPerHour = function(bpmnFactory, elementRegistry, translate, options) {
  var id = options.id,
      resource = options.resource,
      costPerHour = options.costPerHour,
      label = options.label;

  var message;

  if (costPerHour && !isValidNumber(costPerHour)) {
    message = translate('invalid.notDigit {element}', { element: label });

    resource && this.createValidationError(bpmnFactory, elementRegistry, {
      id: id,
      elementId: resource.id,
      message: message
    });
  }

  return { message: message };
};

ValidationErrorHelper.validateGatewayProbabilities = function(bpmnFactory, elementRegistry, translate, options) {
  var probability = options.probability,
      sequenceFlowsElement = options.sequenceFlowsElement,
      outgoingElement = options.outgoingElement,
      gateway = options.gateway,
      description = options.description,
      id = options.id,
      errorMessage;

  if (!probability || probability.trim() === '') {
    errorMessage = translate('invalid.empty {element}', { element: description });
  } else if (!isValidNumber(probability)) {
    errorMessage = translate('invalid.notDigit {element}', { element: description });
  } else if (probability < 0) {
    errorMessage = translate('invalid.notInteger {element}', { element: description });
  } else if (probability > 100) {
    errorMessage = translate('invalid.exceed100% {element}', { element: description });
  } else {
    var currentSequenceFlowElements = sequenceFlowsElement.get('values').filter(function(el) {
      return gateway.outgoing.map(function(el) {
        return el.id;
      }).indexOf(el.elementId) > -1;
    });

    var probabilitySum = gateway.outgoing.reduce(function(acc, innerOutgoingElement) {
      var sequenceFlow = (currentSequenceFlowElements.filter(function(el) {
        return el.elementId === innerOutgoingElement.id && el.elementId !== outgoingElement.id;
      }) || [])[0];

      return acc + ((sequenceFlow && (+sequenceFlow.executionProbability || 0)) || 0);
    }, 0) * 100;

    var nanProbabilityFlows = currentSequenceFlowElements.filter(function(el) {
      return el.elementId !== outgoingElement.id &&
        (el.executionProbability === '' || el.executionProbability === undefined || isNaN(el.executionProbability));
    });

    var EPSILON = 0.001;
    if (gateway.$type === 'bpmn:ExclusiveGateway' && nanProbabilityFlows.length === 0 &&
      (probabilitySum - 100 > EPSILON || Math.abs(probabilitySum + parseFloat(probability) - 100) > EPSILON)) {
      errorMessage = translate('probability.invalid.sum');
    }
  }

  if (errorMessage) {
    this.createValidationError(bpmnFactory, elementRegistry, {
      id: id,
      elementId: gateway.id,
      message: errorMessage
    });
  }

  return { message: errorMessage };
};

ValidationErrorHelper.validateWeekDays = function(bpmnFactory, elementRegistry, translate, options) {
  var fromWeekDay = options.fromWeekDay,
      toWeekDay = options.toWeekDay,
      timeslot = options.timeslot;

  var errorId = timeslot.id + 'day',
      message;

  if (fromWeekDay.index > toWeekDay.index) {
    message = translate('invalid.endWeekDay {beginDay}', { beginDay: fromWeekDay.name });
  }

  message && this.createValidationError(bpmnFactory, elementRegistry, {
    id: errorId,
    elementId: timeslot.id,
    message: message
  });

  return { id: errorId, message: message };
};

ValidationErrorHelper.validateToTime = function(bpmnFactory, elementRegistry, translate, options) {
  var timeslot = options.timeslot,
      fromTime = timeslot.fromTime.slice(0, 5),
      toTime = options.toTime;

  var errorId = timeslot.id + 'toTime',
      message;

  function getTimeAsNumberOfMinutes(time) {
    var timeParts = time.split(':');

    return (timeParts[0] * 60) + parseInt(timeParts[1]);
  }

  if (getTimeAsNumberOfMinutes(fromTime) > getTimeAsNumberOfMinutes(toTime)) {
    message = translate('invalid.endTime {beginTime}', { beginTime: fromTime });
  }

  message && this.createValidationError(bpmnFactory, elementRegistry, {
    id: errorId,
    elementId: timeslot.id,
    message: message
  });

  return { id: errorId, message: message };
};

module.exports = ValidationErrorHelper;