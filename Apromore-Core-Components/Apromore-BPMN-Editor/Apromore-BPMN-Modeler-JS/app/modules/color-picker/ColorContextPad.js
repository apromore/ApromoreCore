import Color from 'color';

const palette = [
  '#84c7e3',
  '#bb3a50',
  '#34AD61',
  '#E98C2D',
  '#bf80ff',
  '#6666ff',
  '#ff80ff'
];

const lighten = function (colorCode) {
  const color = Color(colorCode);
  const start = color.lightness();
  const lightness = start + 0.8 * (100 - start);
  return color.lightness(lightness).hex();
};

const colors = palette.map((color) => ({ stroke: color, fill: lighten(color) }))
const colorMap = colors.reduce((acc, color) => { acc[color.stroke.toLowerCase()] = color; return acc; }, {})

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

    function launchPalette(event, element) {
      let el = $j(`.ap-editor-set-color`)
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
        change: function (newColor) {
          let colorCode = newColor.toHexString();
          let color;
          if (colorMap[colorCode]) {
            color = colorMap[colorCode]
          } else {
            color = {
              stroke: colorCode,
              fill: lighten(colorCode)
            }
          }
          _modeling.setColor(element, color);
          el.spectrum('hide');
        }
      });
      setTimeout(function () {
        el.spectrum('show');
      }, 300);
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
