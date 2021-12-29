import $ from 'jquery';
import { getBusinessObject } from 'bpmn-js/lib/util/ModelUtil';
import extensionElementsHelper from 'bpmn-js-properties-panel/lib/helper/ExtensionElementsHelper';
import { AUX_PROPS } from './common';

const TYPE = 'aux';
const defer = function (fn) {
  setTimeout(fn, 0);
}

export default function Aux(eventBus, overlays, bpmnjs) {

  function toggleCollapse(element) {
    var o = overlays.get({ element: element, type: TYPE })[0];
    var $overlay = o && o.html;

    if ($overlay) {
      var expanded = $overlay.is('.expanded');
      eventBus.fire('aux.toggle', { element: element, active: !expanded });
      if (expanded) {
        $overlay.removeClass('expanded');
      } else {
        $overlay.addClass('expanded');
      }
    }
  }

  function getProp(bo, prop) {
    if (bo && bo[prop] && bo[prop].length) {
      return bo[prop];
    }
    return null;
  }

  function createAux(element) {
    var bo = getBusinessObject(element);
    var url = bo.get(AUX_PROPS.LINK_URL);
    var urlText = bo.get(AUX_PROPS.LINK_TEXT);
    var img = (extensionElementsHelper.getExtensionElements(bo, 'ap:Img') || [])[0];
    var icon = (extensionElementsHelper.getExtensionElements(bo, 'ap:Icon') || [])[0];
    var top = -10;

    if ((!url || !url.length) && !img && !icon) {
      return;
    }
    try {
      overlays.remove({ element, type: TYPE });
    } catch(e) {
      // pass
    }

    var $overlay = $(Aux.OVERLAY_HTML);
    var $content = $overlay.find('.content');
    var imgUrl = getProp(img, AUX_PROPS.IMG_URL);
    var imgSrc = getProp(img, AUX_PROPS.IMG_SRC);
    if (imgSrc || imgUrl) {
      top -= 100;
      $content.append($('<div class="aux-image" style="height: 100px;"><img src="' + (imgUrl || imgSrc) + '" /></div>'));
    }
    var iconName = getProp(icon, AUX_PROPS.ICON_NAME);
    if (url || iconName) {
      top -= 20;
      var $bar = $('<div class="aux-bar" style="height: 20px;"></div>');
      $content.append($bar);
      if (icon) {
        $bar.append($('<i class="aux-icon ' + iconName + '" />'));
      }
      if (url) {
        if (!urlText || !urlText.length) {
          urlText = url;
        }
        $bar.append($(`<a class="aux-link" title="${urlText}" href="${url}">${urlText}</a>`));
      }
    }

    $overlay.find('.toggle').click(function(e) {
      toggleCollapse(element);
    });

    // attach overlay
    overlays.add(element, TYPE, {
      position: {
        top: top,
        left: 0
      },
      html: $overlay
    });
  }

  eventBus.on('shape.added', function(event) {
    var element = event.element;

    if (element.labelTarget || !element.businessObject.$instanceOf('bpmn:FlowNode')) {
      return;
    }
    defer(function() {
      createAux(element);
    });
  });


  this.createAux = function (element) {
    createAux(element);
  }

  this.collapseAll = function() {
    overlays.get({ type: TYPE }).forEach(function(c) {
      var html = c.html;
      if (html.is('.expanded')) {
        toggleCollapse(c.element);
      }
    });
  };

  this.expandAll = function() {
    overlays.get({ type: TYPE }).forEach(function(c) {
      var html = c.html;
      if (!html.is('.expanded')) {
        toggleCollapse(c.element);
      }
    });
  };
}

Aux.$inject = [ 'eventBus', 'overlays', 'bpmnjs' ];

Aux.OVERLAY_HTML =
  '<div class="aux-overlay">' +
    '<div class="toggle">' +
      '<span class="icon-aux"></span>' +
    '</div>' +
    '<div class="content"></div>' +
  '</div>';
