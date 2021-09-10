import * as testFactory from "./testFactory";

describe('Test Simple Animation Controller', function () {
    let animation;
    beforeEach(async function() {
        animation = await testFactory.createSimpleLogAnimation();
    });

    it('It loads data successfully and in the pause state', function() {
        expect(animation).not.toEqual(null);
    });

    it('It establishes the right animation context', function() {
        let animContext = animation.getAnimationContext();
        expect(animContext.getTotalNumberOfFrames()).toEqual(36000);
        expect(animContext.getRecordingFrameRate()).toEqual(60);
        expect(animContext.getLogicalTimelineMax()).toEqual(600);
        expect(animContext.getPluginExecutionId()).toEqual('101');
    });

    it('It is at the right initial state', function() {
        expect(animation.isPlaying()).toBeFalse();
        expect(animation.isAtStart()).toBeTrue();
        expect(animation.isAtEnd()).toBeFalse();
    });

    it('It can switch back and forth between play and pause', function() {
        animation.playPause();
        expect(animation.isPlaying()).toBeTrue();
        animation.playPause();
        expect(animation.isPlaying()).toBeFalse();
    });


});