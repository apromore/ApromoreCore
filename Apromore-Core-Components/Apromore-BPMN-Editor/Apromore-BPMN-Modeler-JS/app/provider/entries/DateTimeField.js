var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject;

var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');

var escapeHTML = require('bpmn-js-properties-panel/lib/Utils').escapeHTML;

function ensureNotNull(prop) {
  if (!prop) {
    throw new Error(prop + ' must be set.');
  }

  return prop;
}

function setDefaultParameters(options) {

  // default method to fetch the current value of the input field
  var defaultGet = function(element) {
    var bo = getBusinessObject(element),
        res = {},
        prop = ensureNotNull(options.modelProperty);
    res[prop] = bo.get(prop);

    return res;
  };

  // default method to set a new value to the input field
  var defaultSet = function(element, values) {
    var res = {},
        prop = ensureNotNull(options.modelProperty);
    if (values[prop] !== '') {
      res[prop] = values[prop];
    } else {
      res[prop] = undefined;
    }

    return cmdHelper.updateProperties(element, res);
  };

  // default validation method
  var defaultValidate = function() {
    return {};
  };

  return {
    id: options.id,
    description: (options.description || ''),
    get: (options.get || defaultGet),
    set: (options.set || defaultSet),
    validate: (options.validate || defaultValidate),
    html: '',
    type: options.type === 'date' ? 'date' : 'time'
  };
}

module.exports = function(options) {

  var resource = setDefaultParameters(options),
      label = options.label || resource.id;

  resource.html =
    '<label for="camunda-' + escapeHTML(resource.id) + '" >' + escapeHTML(label) + '</label><br>' +
    '<input id="camunda-' + escapeHTML(resource.id) + '" type="' + escapeHTML(options.type) + '" name="' +
    escapeHTML(options.modelProperty) + '" />' +
    '</div>';

  resource.cssClasses = ['bpp-textbox'];

  return resource;
};
