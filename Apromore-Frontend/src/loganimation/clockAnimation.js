/**
 * ClockAnimation manages the clock on the animation page.
 * It uses setInterval and javascript to create a clock and sync
 * with the main token animation.
 *
 */
export default class ClockAnimation {
    /**
     * @param {LogAnimation} animationController
     * @param {String} clockContainerId: id of the containing div
     */
    constructor(animationController, clockContainerId) {
        this._animationController = animationController;
        this._animationContext = animationController.getAnimationContext();
        this._dateElement = $j('#' + clockContainerId + "-date")[0];
        this._timeElement = $j('#' + clockContainerId + "-time")[0];
    }

    setClockTime(time) {
        let dateEl = this._dateElement;
        let timeEl = this._timeElement;
        let locales = 'en-GB';
        let date = new Date();
        date.setTime(time);

        if (window.Intl) {
            dateEl.innerHTML = new Intl.DateTimeFormat(locales, {
                year: 'numeric',
                month: 'short',
                day: 'numeric',
                timeZone: this._animationContext.getTimezone()
            }).format(date);
            timeEl.innerHTML = new Intl.DateTimeFormat(locales, {
                hour12: false,
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit',
                timeZone: this._animationContext.getTimezone()
            }).format(date);
        } else {
            // Fallback for browsers that don't support Intl (e.g. Safari 8.0)
            dateEl.innerHTML = date.toDateString();
            timeEl.innerHTML = date.toTimeString();
        }
    }
}