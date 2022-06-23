var is = require('bpmn-js/lib/util/ModelUtil').is;

var createUUID = function() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
};

var isDigit = function(value) {
  return /^\d+(\.\d+)?$/.test(value);
};

var isValidNumber = function(value) {
  if (!value) { return false }
  if (!value.length) { return false }
  if (isNaN(value)) { return false }
  if (value.charAt(value.length - 1) === '.') { return false }
  return /^\d+(\.\d+)?$/.test(value);
};

function getRoot(elementRegistry) {
  return elementRegistry.filter(function(element) {
    return is(element, 'bpmn:Collaboration') || is(element, 'bpmn:Process');
  })[0].businessObject;
}

/**
 * @param {String} value Value to be fixed
 * @returns {String} Fixed value
 */
var fixNumber = function(value) {
  var dot = value.indexOf('.');
  return (isNaN(value) || value === '') ? value : (
    dot >= 0 && dot < value.length - 1 ? (Math.floor(+(value + 'e+2')) / 100).toString() : value
  );
};

/**
 * @param {double} value Value to be rounded up
 * @returns {String} rounded up value
 */
var roundUp = function(value) {
  return (+(Math.round(value + 'e+2') + 'e-2')).toString();
};

/**
 * @param {String | number} value Value to be rounded up
 * @returns {String} rounded up value
 */
var normalizeNumber = function(value) {
  return (+(Math.round(value + 'e+3') + 'e-3')).toString();
};

module.exports = {
  createUUID: createUUID,
  fixNumber: fixNumber,
  isDigit: isDigit,
  getRoot: getRoot,
  roundUp: roundUp,
  normalizeNumber: normalizeNumber,
  isValidNumber: isValidNumber
};