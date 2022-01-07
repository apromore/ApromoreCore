var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject;
var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');
var escapeHTML = require('bpmn-js-properties-panel/lib/Utils').escapeHTML;
var domify = require('min-dom').domify;
var domEvent = require('min-dom').event;
var domQuery = require('min-dom').query;
var { ensureNotNull, setDefaultParameters } = require('../../common');

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
  ],
  [
    "z-icon-times"
  ]
];

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
        if (this.id === 'z-icon-times') {
          input.value = "";
        } else {
          input.value = this.id;
        }
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
