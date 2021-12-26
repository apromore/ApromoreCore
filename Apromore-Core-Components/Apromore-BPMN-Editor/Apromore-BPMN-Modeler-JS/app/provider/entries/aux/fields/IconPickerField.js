var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject;
var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');
var escapeHTML = require('bpmn-js-properties-panel/lib/Utils').escapeHTML;
var domify = require('min-dom').domify,
    domEvent = require('min-dom').event,
    domQuery = require('min-dom').query;

var ICONS = [
  [
    "ap-bpmn-icon-info-circle",
    "ap-bpmn-icon-cube",
    "ap-bpmn-icon-cubes",
    "ap-bpmn-icon-ap-road",
    "ap-bpmn-icon-ap-flag",
    "ap-bpmn-icon-casevari",
    "ap-bpmn-icon-ap-cube",
    "ap-bpmn-icon-rangearrow",
    "ap-bpmn-icon-user",
    "ap-bpmn-icon-users"
  ],
  [
    "ap-bpmn-icon-ap-duration",
    "ap-bpmn-icon-averageduration",
    "ap-bpmn-icon-starttime",
    "ap-bpmn-icon-endtime",
    "ap-bpmn-icon-medianitem",
    "ap-bpmn-icon-waittime",
    "ap-bpmn-icon-processtime",
    "ap-bpmn-icon-anyattribute",
    "ap-bpmn-icon-eventscase",
    "ap-bpmn-icon-schedule"
  ],
  [
    "ap-bpmn-icon-dollar",
    "ap-bpmn-icon-euro",
    "ap-bpmn-icon-pound",
    "ap-bpmn-icon-yen",
    "ap-bpmn-icon-bitcoin",
    "ap-bpmn-icon-coins",
    "ap-bpmn-icon-notes",
    "ap-bpmn-icon-card1",
    "ap-bpmn-icon-card2",
    "ap-bpmn-icon-caseutilisation"
  ],
  [
    "ap-bpmn-icon-hour-glass",
    "ap-bpmn-icon-palette",
    "ap-bpmn-icon-gold",
    "ap-bpmn-icon-timer",
    "ap-bpmn-icon-overview",
    "ap-bpmn-icon-user-g",
    "ap-bpmn-icon-eventattribute",
    "ap-bpmn-icon-flag",
    "z-icon-book",
    "ap-bpmn-icon-process-performance"
  ]
];

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
    type: 'text'
  };
}

module.exports = function(options) {

  var resource = setDefaultParameters(options),
      label = options.label || resource.id;

  var pickerEl = domify('<div id="ap-bpmn-icon-picker"></div>');
  ICONS.forEach((line) => {
    var lineEl = domify('<div></div>');
    line.forEach((iconName) => {
      var icoEl = domify(`<i id="${iconName}" class="${iconName}"></i>`);
      domEvent.bind(icoEl, 'click', function () {
        var input = document.getElementById('camunda-' + escapeHTML(resource.id));
        input.value = this.id;
        var ev = new Event('change', { 'bubbles': true });
        input.dispatchEvent(ev);
      })
      lineEl.appendChild(icoEl);
    });
    pickerEl.appendChild(lineEl);
  })

  resource.html = domify(
    '<label for="camunda-' + escapeHTML(resource.id) + '" >' + escapeHTML(label) + '</label><br>' +
    '<input id="camunda-' + escapeHTML(resource.id) + '" type="text" name="' +
    escapeHTML(options.modelProperty) + '" />'
  );
  resource.html.appendChild(pickerEl);
  resource.cssClasses = ['bpp-textbox'];

  return resource;
};
