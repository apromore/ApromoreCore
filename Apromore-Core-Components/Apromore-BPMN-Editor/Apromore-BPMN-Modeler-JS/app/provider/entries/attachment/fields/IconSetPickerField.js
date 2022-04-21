var xss = require("xss");
var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject;
var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');
var elementHelper = require('bpmn-js-properties-panel/lib/helper/ElementHelper');
var escapeHTML = require('bpmn-js-properties-panel/lib/Utils').escapeHTML;
var domify = require('min-dom').domify;
var domEvent = require('min-dom').event;
var domQuery = require('min-dom').query;
var { ensureNotNull, setDefaultParameters } = require('../../common');
var { AUX_PROPS, getAux, refreshOverlay } = require('../../../../modules/attachment/common');

var ICONS = [
  [
    "z-icon-fire",
    "z-icon-lock",
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
    "ap-bpmn-icon-schedule",
    "ap-bpmn-icon-dollar",
    "ap-bpmn-icon-euro"
  ],
  [
    "ap-bpmn-icon-pound",
    "ap-bpmn-icon-yen",
    "ap-bpmn-icon-bitcoin",
    "ap-bpmn-icon-coins",
    "ap-bpmn-icon-notes",
    "ap-bpmn-icon-card1",
    "ap-bpmn-icon-card2",
    "ap-bpmn-icon-caseutilisation",
    "ap-bpmn-icon-hour-glass",
    "ap-bpmn-icon-palette",
    "ap-bpmn-icon-gold",
    "ap-bpmn-icon-timer"
  ],
  [
    "ap-bpmn-icon-overview",
    "ap-bpmn-icon-user-g",
    "ap-bpmn-icon-eventattribute",
    "ap-bpmn-icon-flag",
    "z-icon-book",
    "ap-bpmn-icon-process-performance",
    "ap-icon-computer",
    "z-icon-ban"
  ]
];

const ICON_NOT_SET = 'z-icon-ban';

function selectIcon(iconName) {
  var container = document.getElementById('ap-bpmn-icon-set-picker');
  var selectedIconEl = document.querySelector('i.selected');
  if (selectedIconEl) {
    selectedIconEl.classList.remove('selected');
  }
  if (!iconName || !iconName.length) {
    iconName = ICON_NOT_SET;
  }
  var iconEl = document.querySelector(`#${iconName}`);
  if (iconEl) {
    iconEl.classList.add('selected');
  }
}

const SET_PICKER_SEL = '#ap-bpmn-icon-set-picker';

function updateObjects(element, icons, bpmnFactory, bpmnjs, eventBus) {
  icons.values = [];
  let setContainer = $('#ap-bpmn-icon-set');
  $('.icon-item', setContainer).each((index, itemEl) => {
    let urlEl = $('.icon-url', itemEl);
    let textEl = $('.icon-text', itemEl);
    let nameEl = $('.icon-name', itemEl);
    let iconUrl = xss(urlEl.val()) || '';
    let iconText = xss(textEl.val()) || '';
    let iconName = nameEl.data('icon-name') || '';
    if (iconName === ICON_NOT_SET) {
      iconName = '';
    }
    if (iconUrl.length || iconText.length || iconName.length) {
      let iconEl = elementHelper.createElement(
        'ap:Icon',
        {
          [AUX_PROPS.ICON_URL]: iconUrl,
          [AUX_PROPS.ICON_TEXT]: iconText,
          [AUX_PROPS.ICON_NAME]: iconName
        },
        icons,
        bpmnFactory
      );
      icons.values.push(iconEl);
    }
  })
  eventBus.fire("elements.changedAux", [element])
  // update overlays
  refreshOverlay(bpmnjs, element);
  return icons.values;
}

function updateIndices() {
  let setContainer = $('#ap-bpmn-icon-set');
  $('.icon-item', setContainer).each((index, itemEl) => {
    //Update DOM so the updated data-icon-index can be used as a selector
    $(itemEl).attr('data-icon-index', index);
    $('.icon-url', itemEl).attr('data-icon-index', index);
    $('.icon-text', itemEl).attr('data-icon-index', index);
    $('.icon-name', itemEl).attr('data-icon-index', index);
    $('.remove', itemEl).attr('data-icon-index', index);
  })
}

let currentIndex = -1;

function renderIconSet(element, icons, bpmnFactory, bpmnjs, translate, eventBus) {
  let item;
  let setContainer = $('#ap-bpmn-icon-set');
  let addButton = $('<div class="add">Add new</div>');

  addButton.on('click', () => {
    let newIndex = $('#ap-bpmn-icon-set .icon-item').length;
    item = addItem('', '', null, newIndex);
    setContainer.append(item);
    showPickerSetFor(item, newIndex);
  });
  setContainer.empty();
  setContainer.append(addButton);

  function showPickerSetFor(item, index) {
    $(SET_PICKER_SEL).detach().insertAfter(item);
    $(SET_PICKER_SEL).show();
    $(SET_PICKER_SEL).attr('data-icon-index', index);
    let currentIcon = $(`#ap-bpmn-icon-set .icon-name[data-icon-index=${index}]`);
    let currentIconName = currentIcon.data('icon-name')
    selectIcon(currentIconName);
    currentIndex = index;
  }

  function addItem(url, text, name, index) {
    if (!name || !name.length) {
      name = ICON_NOT_SET;
    }
    let item = $(`<div class="icon-item" data-icon-index="${index}"></div>`);
    let urlEl = $(`<input class="icon-url" placeholder="URL" data-icon-index="${index}" value="${url}" />`);
    let textEl = $(`<input class="icon-text" placeholder="Text" data-icon-index="${index}" value="${text}" />`);
    let iconEl = $(`<div class="icon-name" title="Click to select icon" data-icon-index="${index}" data-icon-name="${name}"><span class="${name}" /></div>`);
    let eraseEl = $(`<div class="remove" data-icon-index="${index}">Remove</div>`);
    urlEl.on('change', () => {
      updateObjects(element, icons, bpmnFactory, bpmnjs, eventBus);
    });
    textEl.on('change', () => {
      updateObjects(element, icons, bpmnFactory, bpmnjs, eventBus);
    });
    iconEl.on('click', () => {
      let index = $(event.currentTarget).attr('data-icon-index');
      if (currentIndex === index) {
        $(SET_PICKER_SEL).hide();
        currentIndex = -1;
        return;
      }
      let item = $(`#ap-bpmn-icon-set .icon-item[data-icon-index=${index}]`)
      showPickerSetFor(item, index);
    });
    eraseEl.on('click', () => {
      $(SET_PICKER_SEL).hide();
      let index = $(event.target).attr('data-icon-index');
      let row = $(`#ap-bpmn-icon-set .icon-item[data-icon-index=${index}]`);
      row.remove();
      updateIndices();
      updateObjects(element, icons, bpmnFactory, bpmnjs, eventBus);
    });
    item.append(urlEl);
    item.append(textEl);
    item.append(iconEl);
    item.append(eraseEl);
    return item;
  }

  if (icons.values && icons.values.length) {
    icons.values.forEach((icon, index) => {
      let url = icon[AUX_PROPS.ICON_URL];
      let text = icon[AUX_PROPS.ICON_TEXT];
      let name = icon[AUX_PROPS.ICON_NAME];
      item = addItem(url, text, name, index);
      setContainer.append(item);
    })
    item = addItem('', '', null, icons.values.length);
    setContainer.append(item);
  } else {
    item = addItem('', '', null, 0);
    setContainer.append(item);
  }
}

module.exports = function(options) {
  var resource = setDefaultParameters(options),
      label = options.label || resource.id;

  var bo = getBusinessObject(options.element);
  var icons = getAux(bo, options.bpmnFactory, 'ap:Icons', { values: [] });

  var iconSetEl = domify('<div id="ap-bpmn-icon-set"></div>');
  var pickerEl = domify('<div id="ap-bpmn-icon-set-picker" style="display: none;"></div>');
  ICONS.forEach((line) => {
    var lineEl = domify('<div></div>');
    line.forEach((iconName) => {
      var icoEl = (iconName.startsWith('ap-icon')) ?
        domify(`<i id="${iconName}"><span class="${iconName}" /></i>`) :
        domify(`<i id="${iconName}" class="${iconName}"></i>`);
      domEvent.bind(icoEl, 'click', function () {
        let newIconName;
        if (this.id === ICON_NOT_SET) {
          newIconName = "";
        } else {
          newIconName = this.id;
        }
        let index = $(SET_PICKER_SEL).attr('data-icon-index');
        let rowIcon = $(`#ap-bpmn-icon-set .icon-name[data-icon-index=${index}]`);
        rowIcon.data('icon-name', newIconName);
        let iconNameEl = $('span', rowIcon);
        iconNameEl.removeClass();
        iconNameEl.addClass(newIconName);
        selectIcon(newIconName);
        updateObjects(options.element, icons, options.bpmnFactory, options.bpmnjs, options.eventBus);
      })
      lineEl.appendChild(icoEl);
    });
    pickerEl.appendChild(lineEl);
  })

  resource.html = domify(
    '<div><label>' + escapeHTML(label) + '</label></div>'
  );
  resource.html.appendChild(iconSetEl);
  resource.html.appendChild(pickerEl);
  resource.cssClasses = ['bpp-iconset'];
  resource.get = function(_element, _node) {
    setTimeout(function() {
      renderIconSet(options.element, icons, options.bpmnFactory, options.bpmnjs, options.translate, options.eventBus);
    }, 100);
  }
  resource.set = function(element, values, _node) {
    // dummy function
  }
  return resource;
};

