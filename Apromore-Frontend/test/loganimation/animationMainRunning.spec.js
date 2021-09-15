import * as testFactory from "./testFactory";
import {createFullDataFrameBuffer} from "./testFactory";

describe('Test while the animation is running', function () {
    let animation, tokenAnimation, buffer;
    beforeEach(async function(done) {
        animation = await testFactory.createFullDataLogAnimation();
        buffer = createFullDataFrameBuffer();
        spyOn(buffer, '_loopRequestData').and.stub(); // turn off
        spyOn(buffer, '_loopCleanup').and.stub(); // turn off
        tokenAnimation = animation.getTokenAnimation();
        spyOn(tokenAnimation, '_loopBufferRead').and.stub(); // turn off
        tokenAnimation.setFrameBuffer(buffer);
        tokenAnimation.addFrames(buffer.readNextChunk());
        tokenAnimation.addFrames(buffer.readNextChunk());
        animation.playPause(); //start
        console.log(tokenAnimation._frameQueue);
        setTimeout((function() {
            done();
        }), 2000);
    });

    it('It can play the animation at a correct speed', function(done) {
        animation.playPause(); //pause
        expect(animation.isPlaying()).toBeFalse();
        expect(animation.getCurrentLogicalTime()).toBeCloseTo(2, 0);
        done();
    });

    it('It can jump backward to the START position', function(done) {
        expect(animation.isPlaying()).toBeTrue();
        animation.gotoStart();
        expect(animation.isAtStart()).toBeTrue();
        expect(animation.isAtEnd()).toBeFalse();
        expect(animation.isPlaying()).toBeFalse();
        done();
    });

    it('It can jump forward to the END position', function(done) {
        console.log('Frame queue: ', tokenAnimation._frameQueue);
        console.log('Frame queue size: ', tokenAnimation._frameQueue.length);
        expect(animation.isPlaying()).toBeTrue();
        animation.gotoEnd();
        expect(animation.isAtStart()).toBeFalse();
        expect(animation.isAtEnd()).toBeTrue();
        expect(animation.isPlaying()).toBeFalse();
        done();
    });
});