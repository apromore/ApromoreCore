/**
 * All events used in the log animation
 *
 * @author Bruce Nguyen
 */

export class AnimationEventType {
    static get FRAMES_NOT_AVAILABLE() {
        return 1;
    }
    static get FRAMES_AVAILABLE() {
        return 2;
    }
    static get END_OF_ANIMATION() {
        return 3;
    }
    static get MODEL_CANVAS_MOVING() {
        return 4;
    }
    static get MODEL_CANVAS_MOVED() {
        return 5;
    }
    static get TIMELINE_CURSOR_MOVING() {
        return 6;
    }
}

export class AnimationEvent {
    /**
     *
     * @param {Number} eventType
     * @param {Object} eventData
     */
    constructor(eventType, eventData) {
        this._eventType = eventType
        this._eventData = eventData;
    }

    getEventType() {
        return this._eventType;
    }

    getEventData() {
        return this._eventData;
    }
}