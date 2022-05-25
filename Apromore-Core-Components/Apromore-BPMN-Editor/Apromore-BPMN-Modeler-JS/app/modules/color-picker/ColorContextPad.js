import Color from 'color';
import { is, getDi } from 'bpmn-js/lib/util/ModelUtil';

const palette = [
  '#000000',
  '#84c7e3',
  '#bb3a50',
  '#34AD61',
  '#E98C2D',
  '#bf80ff',
  '#6666ff',
  '#ff80ff'
];

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
    return di.get('color:border-color') || di.get('bioc:stroke') ||  'black';
  } else {
    return di.get('color:background-color') || di.get('bioc:fill');
  }
}

const colors = palette.map(
  (color) => (
      {
          stroke: 'black',
          fill: lighten(color),
          key: color.toLowerCase()
      }
  )
);
const colorMap = colors.reduce((acc, color) => { acc[color.key.toLowerCase()] = color; return acc; }, {});

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
              fill: lighten(colorCode)
            }
          }
          if (
            is(element, 'bpmn:SequenceFlow') ||
            is(element, 'bpmn:Association') ||
            is(element, 'bpmn:DataInputAssociation') ||
            is(element, 'bpmn:DataOutputAssociation') ||
            is(element, 'bpmn:MessageFlow')
          ) {
            color.stroke = darken(colorCode);
            color.fill = darken(colorCode);
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
