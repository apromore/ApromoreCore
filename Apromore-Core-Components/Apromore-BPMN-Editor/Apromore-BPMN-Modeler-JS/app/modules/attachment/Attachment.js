import $ from 'jquery';
import { getBusinessObject } from 'bpmn-js/lib/util/ModelUtil';
import cmdHelper from 'bpmn-js-properties-panel/lib/helper/CmdHelper';
import extensionElementsHelper from 'bpmn-js-properties-panel/lib/helper/ExtensionElementsHelper';
import { AUX_PROPS } from './common';
import { isNil } from 'min-dash';
import interact from 'interactjs';
import xss from 'xss';

const TYPE = 'aux';
const defer = function (fn) {
  setTimeout(fn, 0);
}
const ICON_ITEM_HEIGHT = 26;

const fixUrl = (url) => {
  if (url && url.length && !url.startsWith('http')) {
    return 'http://' + url;
  }
  return url
};

export default function Aux(eventBus, bpmnFactory, elementRegistry, overlays, bpmnjs) {

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
      return xss(bo[prop]);
    }
    return null;
  }

  function getNumber(bo, prop) {
    var num = bo && bo[prop];
    if (!isNil(num)) {
      return Math.round(parseFloat(num));
    }
    return null;
  }

  function createAux(element) {
    var bo = getBusinessObject(element);
    var url = xss(bo.get(AUX_PROPS.LINK_URL));
    var text = xss(bo.get(AUX_PROPS.LINK_TEXT));
    var img = (extensionElementsHelper.getExtensionElements(bo, 'ap:Img') || [])[0];
    var icons = (extensionElementsHelper.getExtensionElements(bo, 'ap:Icons') || [])[0];
    var dtop = -10;
    var dleft = 0;
    var dwidth = 120;
    var dheight = 120;
    var urlText;
    var contentCount = 0;

    if ((!url || !url.length) && !img && !icons) {
      return;
    }
    try {
      overlays.remove({ element, type: TYPE });
    } catch(e) {
      // pass
    }

    url = fixUrl(url);
    var $overlay = $(Aux.OVERLAY_HTML);
    var imgUrl = getProp(img, AUX_PROPS.IMG_URL);
    var imgSrc = getProp(img, AUX_PROPS.IMG_SRC);
    if (imgSrc || imgUrl) {
      dtop -= 120;
      let imgEl = '<img src="' + (imgUrl || imgSrc) + '" />';
      if (url) {
        if (!text || !text.length) {
          urlText = url;
        } else {
          urlText = text;
        }
        imgEl = `<div><a target="_blank" title="${urlText}" href="${url}">${imgEl}</a></div>`;
      }
      if (text && text.length) {
        if (url) {
          imgEl += `<div class="caption"><a target="_blank" href="${url}">${text}</a></div>`;
        } else {
          imgEl += `<div class="caption">${text}<div>`;
        }
      }
      $overlay.append($(`<div class="aux-image">${imgEl}</div>`));
      contentCount++;
    }
    if (icons && icons.values && icons.values.length) {
      let iconEls = '';
      let iconCount = 0;
      var $footer = $('<div class="aux-footer"></div>');
      icons.values.forEach((iconItem) => {
        var iconUrl = getProp(iconItem, AUX_PROPS.ICON_URL);
        var iconText = getProp(iconItem, AUX_PROPS.ICON_TEXT) || iconUrl;
        var iconName = getProp(iconItem, AUX_PROPS.ICON_NAME);
        // if (iconName === 'z-icon-ban') {
        //  iconName = null;
        // }
        iconUrl = fixUrl(iconUrl);
        if (iconName || iconUrl || iconText) {
          dtop -= ICON_ITEM_HEIGHT;
          iconCount++;
          let $item = $('<div class="aux-icon-item"></div>');
          iconName = iconName || ''
          $item.append($(`<i class="aux-icon"><span class="${iconName}" /></i>`));
          if (iconUrl) {
            $item.append($(`<div class="aux-icon-link"><a target="_blank" title="${iconText}" href="${iconUrl}">${iconText}</a></div>`));
          } else if (iconText) {
            $item.append($(`<div class="aux-icon-link">${iconText}</div>`));
          }
          $item.css('height', ICON_ITEM_HEIGHT + 'px');
          $footer.append($item);
        }
      })
      $footer.css('height', (iconCount * ICON_ITEM_HEIGHT) + 'px');
      $overlay.append($footer);
      contentCount++;
    }

    $overlay.find('.toggle').click(function(e) {
      toggleCollapse(element);
    });

    function readProperties(bo) {
      let left = getNumber(bo, AUX_PROPS.LEFT);
      let top = getNumber(bo, AUX_PROPS.TOP);
      let width = getNumber(bo, AUX_PROPS.WIDTH);
      let height = getNumber(bo, AUX_PROPS.HEIGHT);
      return { left, top, width, height };
    }

    function saveProperties(bo, change) {
      if (!isNil(change.left)) {
        bo[AUX_PROPS.LEFT] = change.left;
      }
      if (!isNil(change.top)) {
        bo[AUX_PROPS.TOP] = change.top;
      }
      if (!isNil(change.width)) {
        bo[AUX_PROPS.WIDTH] = change.width;
      }
      if (!isNil(change.height)) {
        bo[AUX_PROPS.HEIGHT] = change.height;
      }
    }

    var sx, sy;
    var cx, cy;

    function updateLocation(overlayId) {
      var overlay = overlays.get(overlayId);
      sx = overlay.position.left;
      sy = overlay.position.top;
      cx = event.pageX;
      cy = event.pageY;
    }

    function getDelta(event) {
      var scale = overlays._canvas.viewbox().scale;
      var dx = Math.round((event.pageX - cx) / scale);
      var dy = Math.round((event.pageY - cy) / scale);
      return { dx, dy };
    }

    function updateDragMove(event) {
      var { dx, dy } = getDelta(event);
      var left = sx + dx;
      var top = sy + dy;
      Object.assign(event.target.style, {
        left: left + 'px', top: top + 'px'
      })
      return { left, top };
    }

    function updateResizeMove(event) {
      var scale = overlays._canvas.viewbox().scale;
      var { dx, dy } = getDelta(event);
      var left = sx;
      var top = sy;
      if (event.edges.left) {
        left += dx;
      }
      if (event.edges.top) {
        top += dy;
      }
      var calcHeight = $('.aux-wrapper > div > *', event.target).map((x, y) => y.clientHeight)
        .toArray().reduce((x,y) => x + y, 0) + 24 + 8
      var width = event.rect.width / scale;
      var height = event.rect.height / scale;
      if (height < calcHeight) {
        height = calcHeight;
      }

      Object.assign(event.target.style, {
        width: `${width}px`,
        height: `${height}px`,
        left: left + 'px', top: top + 'px'
      })
      return {
        left, top, width, height
      }
    }

    function getBOFromEvent(event) {
      var elId = $(event.target).data("ap-el-id");
      var el = elementRegistry.get(elId);
      return getBusinessObject(element);
    }

    dheight = -10 - dtop; // update default height
    let { left, top, width, height } = readProperties(bo);
    if (isNil(left)) {
      left = dleft;
    }
    if (isNil(top)) {
      top = dtop;
    }
    if (isNil(width)) {
      width = dwidth;
    }
    if (isNil(height)) {
      height = dheight;
    }

    // attach overlay
    overlays.add(element, TYPE, {
      position: { left, top },
      html: $overlay
    });
    var auxCls = "ap-aux-" + element.id;
    $overlay.parent().data("ap-el-id", element.id);
    $overlay.parent().addClass(auxCls);
    if (!contentCount) {
        $overlay.parent().addClass('empty');
    } else {
        $overlay.parent().removeClass('empty');
    }

    $overlay.parent().css('width', width + 'px');
    $overlay.parent().css('height', height + 'px');

    interact('.' + auxCls)
      .draggable({
        listeners: {
          start (event) {
            updateLocation(event.target.dataset.overlayId);
          },
          move (event) {
            updateDragMove(event);
          },
          end (event) {
            var overlay = overlays.get(event.target.dataset.overlayId);
            var { left, top } = updateDragMove(event);
            overlay.position.left = left;
            overlay.position.top = top;
            saveProperties(getBOFromEvent(event), { left, top });
            eventBus.fire('aux.moved', { left, top });
          }
        }
      })
      .resizable({
        margin: 4,
        edges: { top: true, left: true, bottom: true, right: true },
        listeners: {
          start (event) {
            updateLocation(event.target.dataset.overlayId);
          },
          move: function (event) {
            updateResizeMove(event);
          },
          end (event) {
            var overlay = overlays.get(event.target.dataset.overlayId);
            var { left, top, width, height } = updateResizeMove(event);
            overlay.position.left = left;
            overlay.position.top = top;
            saveProperties(getBOFromEvent(event), { left, top, width, height });
            eventBus.fire('aux.moved', { left, top, width, height });
          }
        }
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

Aux.$inject = [
  'eventBus',
  'bpmnFactory',
  'elementRegistry',
  'overlays',
  'bpmnjs'
];

Aux.OVERLAY_HTML =
  '<div class="aux-wrapper">' +
  '</div>';
