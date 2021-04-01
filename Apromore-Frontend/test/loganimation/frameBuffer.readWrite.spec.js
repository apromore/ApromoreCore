import * as testFactory from "./testFactory";

describe('After writing frames to a Buffer', function () {
    /** @type Buffer */
    let frameBuffer;
    beforeEach(function() {
        frameBuffer = testFactory.createEmptyFrameBuffer();
        frameBuffer.startOps();
        jasmine.getFixtures().fixturesPath = 'base/test/loganimation/fixtures';
        spyOn(frameBuffer, '_loopRequestData').and.stub();
        spyOn(frameBuffer, '_loopCleanup').and.stub();
    });

    it('First frame chunk is added correctly', function() {
        let chunkRaw = require('./fixtures/chunk1.txt');
        let frames = JSON.parse(chunkRaw.default);
        frameBuffer.write(frames, 0);

        expect(frameBuffer.getFirstIndex()).toEqual(0);
        expect(frameBuffer.getLastIndex()).toEqual(299);
        expect(frameBuffer.getCurrentIndex()).toEqual(0);
        expect(frameBuffer.getUsedStockLevel()).toEqual(0);
        expect(frameBuffer.getUnusedStockLevel()).toEqual(300);
        expect(frameBuffer.isSequentialMode()).toBeTrue();
        expect(frameBuffer.isStockAvailable()).toBeTrue();
        expect(frameBuffer.isSafetyStock()).toBeFalse();
        expect(frameBuffer.isEmpty()).toBeFalse();
        expect(frameBuffer.isObsoleteStock()).toBeFalse();
        expect(frameBuffer.isOutOfSupply()).toBeFalse();
    });

    it('Next frame chunk is added sequentially', function() {
        let chunkRaw = require('./fixtures/chunk1.txt');
        let frames = JSON.parse(chunkRaw.default);
        frameBuffer.write(frames, 0);

        chunkRaw = require('./fixtures/chunk2.txt');
        frames = JSON.parse(chunkRaw.default);
        frameBuffer.write(frames, 0);

        expect(frameBuffer.getFirstIndex()).toEqual(0);
        expect(frameBuffer.getLastIndex()).toEqual(599);
        expect(frameBuffer.getCurrentIndex()).toEqual(0);
        expect(frameBuffer.getUsedStockLevel()).toEqual(0);
        expect(frameBuffer.getUnusedStockLevel()).toEqual(600);
        expect(frameBuffer.isSequentialMode()).toBeTrue();
        expect(frameBuffer.isStockAvailable()).toBeTrue();
        expect(frameBuffer.isSafetyStock()).toBeFalse();
        expect(frameBuffer.isEmpty()).toBeFalse();
        expect(frameBuffer.isObsoleteStock()).toBeFalse();
        expect(frameBuffer.isOutOfSupply()).toBeFalse();
    });

    it('First frame chunk is read correctly', function() {
        let chunkRaw = require('./fixtures/chunk1.txt');
        let frames = JSON.parse(chunkRaw.default);
        frameBuffer.write(frames, 0);

        frameBuffer.readNextChunk();
        expect(frameBuffer.getFirstIndex()).toEqual(0);
        expect(frameBuffer.getLastIndex()).toEqual(299);
        expect(frameBuffer.getCurrentIndex()).toEqual(300);
        expect(frameBuffer.getUsedStockLevel()).toEqual(300);
        expect(frameBuffer.getUnusedStockLevel()).toEqual(0);
        expect(frameBuffer.isSequentialMode()).toBeTrue();
        expect(frameBuffer.isStockAvailable()).toBeFalse();
        expect(frameBuffer.isSafetyStock()).toBeFalse();
        expect(frameBuffer.isEmpty()).toBeFalse();
        expect(frameBuffer.isObsoleteStock()).toBeFalse();
        expect(frameBuffer.isOutOfSupply()).toBeFalse();
    });

    it('Frame chunks are read correctly until the buffer is empty', function() {
        let chunkRaw = require('./fixtures/chunk1.txt');
        let frames = JSON.parse(chunkRaw.default);
        frameBuffer.write(frames, 0);

        chunkRaw = require('./fixtures/chunk2.txt');
        frames = JSON.parse(chunkRaw.default);
        frameBuffer.write(frames, 0);

        frameBuffer.readNextChunk();
        frameBuffer.readNextChunk();
        expect(frameBuffer.getFirstIndex()).toEqual(0);
        expect(frameBuffer.getLastIndex()).toEqual(599);
        expect(frameBuffer.getCurrentIndex()).toEqual(600);
        expect(frameBuffer.getUsedStockLevel()).toEqual(600);
        expect(frameBuffer.getUnusedStockLevel()).toEqual(0);
        expect(frameBuffer.isSequentialMode()).toBeTrue();
        expect(frameBuffer.isStockAvailable()).toBeFalse();
        expect(frameBuffer.isSafetyStock()).toBeFalse();
        expect(frameBuffer.isEmpty()).toBeFalse();
        expect(frameBuffer.isObsoleteStock()).toBeFalse();
        expect(frameBuffer.isOutOfSupply()).toBeFalse();
    });

});