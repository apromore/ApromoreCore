import * as testFactory from "./testFactory";
import DataRequester from "../../src/loganimation/dataRequester";

describe('After writing frames to a Buffer', function () {
    /** @type Buffer */
    let frameBuffer;

    beforeEach(function() {
        frameBuffer = testFactory.createEmptyFrameBuffer();
        jasmine.clock().uninstall();
        jasmine.clock().install();
    });

    afterEach(function() {
        jasmine.clock().uninstall();
    });

    it('Buffer won\'t do cleanUp when the history threshold has not been reached', function() {
        let chunkRaw = require('./fixtures/chunk1.txt');
        let frames = JSON.parse(chunkRaw.default);
        frameBuffer.write(frames, 0);
        expect(frameBuffer.size()).toEqual(300);
        expect(frameBuffer.isObsoleteStock()).toBeFalse();

        spyOn(frameBuffer, '_loopRequestData').and.stub(); // turn off the request data loop
        frameBuffer.startOps();

        jasmine.clock().tick(2000);
        expect(frameBuffer.size()).toEqual(300); // buffer is unchanged.
        expect(frameBuffer.isObsoleteStock()).toBeFalse();
    });

    it('Buffer does cleanUp when the history threshold has been reached', function() {
        let chunkRaw = require('./fixtures/chunk1.txt');
        let frames = JSON.parse(chunkRaw.default);
        frameBuffer.write(frames, 0);
        frameBuffer.write(frames, 0);
        frameBuffer.write(frames, 0);
        frameBuffer.write(frames, 0);
        expect(frameBuffer.getUnusedStockLevel()).toEqual(1200);

        frameBuffer.readNextChunk();
        frameBuffer.readNextChunk();
        frameBuffer.readNextChunk();
        expect(frameBuffer.getUsedStockLevel()).toEqual(900);
        expect(frameBuffer.isObsoleteStock()).toBeTrue(); // threshold = 600

        spyOn(frameBuffer, '_loopRequestData').and.stub(); // turn off the request data loop to test cleanup
        frameBuffer.startOps();

        jasmine.clock().tick(2000);
        expect(frameBuffer.getUsedStockLevel()).toEqual(600); // 300 frames exceeding the threshold were removed
        expect(frameBuffer.isObsoleteStock()).toBeFalse();
    });

});