/**
 * PlayActionControl groups buttons controlling the animation such as start, pause, fast foward, etc.
 *
 * @author Bruce Nguyen
 */
export default class PlayActionControl{
    /**
     * @param {LogAnimation} animation
     * @param {String} buttonsContainerId: containing div id
     * @param {String} playClassName: CSS class name of the play state
     * @param {String} pauseClassName: CSS class name of the pause state
     */
    constructor(animation, buttonsContainerId, playClassName, pauseClassName) {
        this.animation = animation;
        this.gotoStartButton = $j('#' + buttonsContainerId + "-start");
        this.pauseButton = $j('#' + buttonsContainerId + "-pause");
        this.forwardButton = $j('#' + buttonsContainerId + "-forward");
        this.backwardButton = $j('#' + buttonsContainerId + "-backward");
        this.gotoEndButton = $j('#' + buttonsContainerId + "-end");

        this.gotoStartButton.on('click', animation.gotoStart.bind(animation));
        this.gotoEndButton.on('click', animation.gotoEnd.bind(animation));
        this.pauseButton.on('click', animation.playPause.bind(animation));
        this.forwardButton.on('click', animation.fastForward.bind(animation));
        this.backwardButton.on('click', animation.fastBackward.bind(animation));

        this.PLAY_CLS = playClassName;
        this.PAUSE_CLS = pauseClassName;
    }

    /**
     * @param {Boolean} changeToPlay: true means setting the button to a Play shape, false: set it to a Pause shape.
     */
    setPlayPauseButton(changeToPlay) {
        if (typeof changeToPlay === 'undefined') {
            changeToPlay = !this.animation.isPlaying();
        }
        if (changeToPlay) {
            this.pauseButton.removeClass(this.PAUSE_CLS).addClass(this.PLAY_CLS);
        } else {
            this.pauseButton.removeClass(this.PLAY_CLS).addClass(this.PAUSE_CLS);
        }
    }

    freezeControls() {
        this.gotoStartButton.css('pointer-events','none');
        this.gotoEndButton.css('pointer-events','none');
        this.pauseButton.css('pointer-events','none');
        this.forwardButton.css('pointer-events','none');
        this.backwardButton.css('pointer-events','none');
    }

    unFreezeControls() {
        this.gotoStartButton.css('pointer-events','auto');
        this.gotoEndButton.css('pointer-events','auto');
        this.pauseButton.css('pointer-events','auto');
        this.forwardButton.css('pointer-events','auto');
        this.backwardButton.css('pointer-events','auto');
    }
}