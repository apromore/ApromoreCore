import * as testFactory from "./testFactory";
import {createFullDataFrameBuffer} from "./testFactory";
import FrameBuffer from "../../src/loganimation/frameBuffer";
import TokenAnimation from "../../src/loganimation/tokenAnimation";
import {AnimationContext} from "../../src/loganimation/animationContextState";

describe('Test Token Animation', function () {
    /** @type TokenAnimation */
    let tokenAnimation;
    /** @type AnimationContext */
    let animationContext;
    /** @type FrameBuffer */
    let buffer;
    beforeEach(async function() {
        let animation = await testFactory.createFullDataLogAnimation();
        tokenAnimation = animation.getTokenAnimation();
        buffer = await createFullDataFrameBuffer();
        tokenAnimation.setFrameBuffer(buffer);
        animationContext = animation.getAnimationContext();
    });

    it('It is paused after creation', function() {
        expect(tokenAnimation.isInProgress()).toBeTrue();
        expect(tokenAnimation.isPausing()).toBeTrue();
    });

    it('It can switch back and forth between play and pause', function() {
        tokenAnimation.startEngine();
        tokenAnimation.doUnpause();
        expect(tokenAnimation.isInProgress()).toBeTrue();
        expect(tokenAnimation.isPausing()).toBeFalse();
        tokenAnimation.doPause();
        expect(tokenAnimation.isInProgress()).toBeTrue();
        expect(tokenAnimation.isPausing()).toBeTrue();
    });

    it('It can go back to start', function() {
        tokenAnimation.startEngine();
        expect(tokenAnimation.isInProgress()).toBeTrue();
        tokenAnimation.doGoto(0);
        expect(tokenAnimation.isPausing()).toBeTrue();
        expect(tokenAnimation.isInProgress()).toBeTrue();
    });

    it('It can jump to end', function() {
        tokenAnimation.startEngine(); // at pause state
        tokenAnimation.doUnpause(); // start playing
        expect(tokenAnimation.isInProgress()).toBeTrue();
        tokenAnimation.doGoto(animationContext.getLogicalTimelineMax());
        expect(tokenAnimation.isPausing()).toBeFalse();
        expect(tokenAnimation.isInProgress()).toBeTrue();
        expect(tokenAnimation.isAtEndFrame()).toBeTrue();
        expect(tokenAnimation.isAtStartFrame()).toBeFalse();
    });
});