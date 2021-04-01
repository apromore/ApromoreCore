import 'jquery-ui-bundle';
import 'jquery-ui-slider-pips-npm';

/**
 * SpeedControl controls the speed with a slider.
 */
export default class SpeedControl{
    /**
     * @param {LogAnimation} animation
     * @param {String} uiContainerId: id of the containing div
     */
    constructor(animation, uiContainerId) {
        this._animation = animation;
        this._containerId = uiContainerId;
        let speedControl = $j('#' + uiContainerId);
        this._speedSlider = speedControl.slider({
            orientation: "horizontal",
            step: 1,
            min: 1,
            max: 10,
            value: 5
        });

        let STEP_VALUES = [10, 20, 30, 40, 60, 70, 80, 90, 120, 240];
        this._speedSlider.slider("float", {
            handle: true,
            pips: true,
            labels: true,
            prefix: "",
            suffix: ""
        });

        let lastSliderValue = speedControl.slider("value");
        speedControl.on("slidechange", function(event, ui) {
            animation.setSpeedLevel(STEP_VALUES[ui.value - 1]);
            lastSliderValue = ui.value;
        });
    }

    freezeControls() {
        this._speedSlider.css('pointer-events','none');
    }

    unFreezeControls() {
        this._speedSlider.css('pointer-events','auto');
    }

    destroy() {
        this._speedSlider.slider("float", "destroy");
    }
}