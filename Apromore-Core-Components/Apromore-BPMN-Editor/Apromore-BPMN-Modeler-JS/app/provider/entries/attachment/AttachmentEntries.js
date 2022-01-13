var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');
var entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory');
var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject;
var elementHelper = require('bpmn-js-properties-panel/lib/helper/ElementHelper');
var extensionElementsHelper = require('bpmn-js-properties-panel/lib/helper/ExtensionElementsHelper');
var IconPickerField = require('./fields/IconPickerField');
var ImagePickerField = require('./fields/ImagePickerField');
var { AUX_PROPS } = require('../../../modules/attachment/common');

function getExtensionElements(element, bpmnFactory) {
  var extensionElements = element.extensionElements;

  if (!extensionElements || !extensionElements.values) {
    extensionElements = elementHelper.createElement('bpmn:ExtensionElements',
      { values: [] }, element, bpmnFactory);
    element.extensionElements = extensionElements;
  }

  return extensionElements;
}

function getImg(element, bpmnFactory) {
  var extensionElements = getExtensionElements(element, bpmnFactory);

  var img = (extensionElementsHelper.getExtensionElements(element,
    'ap:Img') || [])[0];

  if (!img) {
    img = elementHelper.createElement(
      'ap:Img',
      {
        src: '',
      }, element, bpmnFactory
     );
    extensionElements.values.push(img);
  }

  return img;
}

function getAux(element, bpmnFactory, type, init) {
  var extensionElements = getExtensionElements(element, bpmnFactory);
  var aux = (extensionElementsHelper.getExtensionElements(element,
    type) || [])[0];

  if (!aux) {
    aux = elementHelper.createElement(type, init, element, bpmnFactory);
    extensionElements.values.push(aux);
  }

  return aux;
}

function selectIcon(iconName) {
  var container = document.getElementById('ap-bpmn-icon-picker');
  var selectedIconEl = document.querySelector('i.selected');
  if (selectedIconEl) {
    selectedIconEl.classList.remove('selected');
  }
  if (!iconName || !iconName.length) {
    iconName = 'z-icon-times';
  }
  var iconEl = document.querySelector(`#${iconName}`);
  if (iconEl) {
    iconEl.classList.add('selected');
  }
}

function showImgPreview(data) {
  if (!data || !data.length) {
    data = "";
  }
  const preview = document.querySelector('.aux-img-preview img');
  if (preview) {
    preview.src = data;
  }
}

function refreshOverlay(bpmnjs, element) {
  setTimeout(function () {
    var auxModule = bpmnjs.get('aux');
    auxModule.createAux(element);
  }, 500);
}

module.exports = function(element, bpmnFactory, elementRegistry, translate, bpmnjs) {

  var bo = getBusinessObject(element);
  var img = getImg(bo, bpmnFactory);
  var icon = getAux(bo, bpmnFactory, 'ap:Icon', { elIconName: '' });

  return [
    entryFactory.textField(translate, {
      id : AUX_PROPS.LINK_URL,
      label : 'Link URL',
      modelProperty : AUX_PROPS.LINK_URL,
      get: function(_element, _node) {
        return { [AUX_PROPS.LINK_URL]: bo[AUX_PROPS.LINK_URL] };
      },
      set: function(element, values, _node) {
        var url = values[AUX_PROPS.LINK_URL];
        if (!url.startsWith('http') && url.length) {
          url = 'http://' + url;
        }
        refreshOverlay(bpmnjs, element);
        return cmdHelper.updateBusinessObject(element, bo, {
          [AUX_PROPS.LINK_URL]: url
        });
      }
    }),
    entryFactory.textField(translate, {
      id : AUX_PROPS.LINK_TEXT,
      label : 'Link text',
      modelProperty : AUX_PROPS.LINK_TEXT,
      get: function(_element, _node) {
        return { [AUX_PROPS.LINK_TEXT]: bo[AUX_PROPS.LINK_TEXT] };
      },
      set: function(element, values, _node) {
        refreshOverlay(bpmnjs, element);
        return cmdHelper.updateBusinessObject(element, bo, {
          [AUX_PROPS.LINK_TEXT]: values[AUX_PROPS.LINK_TEXT]
        });
      }
    }),
    entryFactory.textField(translate, {
      id: AUX_PROPS.IMG_SRC,
      label: "Image Link",
      modelProperty : AUX_PROPS.IMG_SRC,
      get: function(_element, _node) {
        return { [AUX_PROPS.IMG_SRC]: img[AUX_PROPS.IMG_SRC] };
      },
      set: function(element, values, _node) {
        var src = values[AUX_PROPS.IMG_SRC];
        if (!src.startsWith('http') && src.length) {
          src = 'http://' + src;
        }
        refreshOverlay(bpmnjs, element);
        return cmdHelper.updateBusinessObject(element, img, {
          [AUX_PROPS.IMG_SRC]: src
        });
      }
    }),
    ImagePickerField({
      id: AUX_PROPS.IMG_URL,
      label: "Image Upload",
      modelProperty : AUX_PROPS.IMG_URL,
      get: function(_element, _node) {
        showImgPreview(img[AUX_PROPS.IMG_URL]);
        return { [AUX_PROPS.IMG_URL]: img[AUX_PROPS.IMG_URL] };
      },
      set: function(element, values, _node) {
        refreshOverlay(bpmnjs, element);
        return cmdHelper.updateBusinessObject(element, img, {
          [AUX_PROPS.IMG_URL]: values[AUX_PROPS.IMG_URL]
        });
      }
    }),
    IconPickerField(
      {
        id: AUX_PROPS.ICON_NAME,
        label: "Icon",
        modelProperty: AUX_PROPS.ICON_NAME,
        get: function(_element, _node) {
          selectIcon(icon[AUX_PROPS.ICON_NAME]);
          return { [AUX_PROPS.ICON_NAME]: icon[AUX_PROPS.ICON_NAME] };
        },
        set: function(element, values, _node) {
          refreshOverlay(bpmnjs, element);
          return cmdHelper.updateBusinessObject(element, icon, {
            [AUX_PROPS.ICON_NAME]: values[AUX_PROPS.ICON_NAME]
          });
        }
      }
    )
  ];
}
