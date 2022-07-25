import Color from 'color';
import { is, getDi } from 'bpmn-js/lib/util/ModelUtil';

const darken = function (colorCode) {
  const color = Color(colorCode);
  const start = color.lightness();
  const lightness = start * 0.8;
  return color.lightness(lightness).hex();
};

const lighten = function (colorCode) {
  const color = Color(colorCode);
  const start = color.lightness();
  const lightness = start + 0.8 * (100 - start);
  return color.lightness(lightness).hex();
};

let palette = [
    '#E7E7E7', '#E6F4F9', '#F2D7DB', '#D3F2DF', '#FBE8D5', '#F2E6FF', '#E0E0FF', '#FFE6FF',
    '#888888', '#84c7e3', '#bb3a50', '#34AD61', '#E98C2D', '#bf80ff', '#6666ff', '#ff80ff',
    '#000000', '#4AACD6', '#962E40', '#2A8A4E', '#C97015', '#9833FF', '#1F1FFF', '#FF33FF'
];

const getCurrentColor = function (element){
  if (!element) {
    return;
  }
  let di = getDi(element);
  if (element.type === 'label') {
    return di.label.color;
  }
  if (
    is(element, 'bpmn:SequenceFlow') ||
    is(element, 'bpmn:Association') ||
    is(element, 'bpmn:DataInputAssociation') ||
    is(element, 'bpmn:DataOutputAssociation') ||
    is(element, 'bpmn:MessageFlow')
  ) {
    return di.get('color:border-color') || di.get('bioc:stroke');
  } else {
    return di.get('color:background-color') || di.get('bioc:fill');
  }
}

const colors = palette.map(
  (color) => (
      {
          stroke: 'black',
          fill: color,
          key: color.toLowerCase()
      }
  )
);
const colorMap = colors.reduce((acc, color) => { acc[color.key] = color; return acc; }, {});

let drawOverride = false;

export default class ColorContextPad {
  constructor(config, modeling, contextPad, canvas, translate) {
    this._config = config;
    this._modeling = modeling;
    this._contextPad = contextPad;
    this._canvas = canvas;
    this._translate = translate;
    contextPad.registerProvider(this);
  }

  getContextPadEntries(element) {
    const { _canvas, _contextPad, _colorPalette, _modeling } = this;

    if (is(element, 'bpmn:TextAnnotation')) {
      return;
    }

    function launchPalette(event, element) {
      let el = $j(`.ap-editor-set-color`)
      let currentColor = getCurrentColor(element)
      el.spectrum({
        type: "color",
        showInput: true,
        showInitial: true,
        showAlpha: false,
        allowEmpty: false,
        showButtons: true,
        hideAfterPaletteSelect: true,
        containerClassName: 'ap-editor-cpicker-wrapper',
        palette,
        color: currentColor,
        change: function (newColor) {
          let colorCode = newColor.toHexString();
          let color;
          if (colorMap[colorCode]) {
            color = {
              stroke: 'black',
              fill: colorMap[colorCode].fill
            }
          } else {
            color = {
              stroke: 'black',
              fill: colorCode
            }
          }
          if (
            is(element, 'bpmn:SequenceFlow') ||
            is(element, 'bpmn:Association') ||
            is(element, 'bpmn:DataInputAssociation') ||
            is(element, 'bpmn:DataOutputAssociation') ||
            is(element, 'bpmn:MessageFlow')
          ) {
            color.stroke = colorCode;
            color.fill = colorCode;
          }
          _modeling.setColor(element, color);
          el.spectrum('hide');
        }
      });
      setTimeout(function () {
        el.spectrum('show');
      }, 500);
    }

    return {
      "colorize": {
        group: "edit",
        className: "ap-editor-set-color",
        title: "Color",
        action: {
          click: launchPalette
        }
      }
    };
  }
}

ColorContextPad.$inject = [
  'config',
  'modeling',
  'contextPad',
  'canvas',
  'translate'
];