import * as testFactory from "./testFactory";

describe('After creating an initial Buffer', function () {
    /** @type Buffer */
    let frameBuffer;
    beforeEach(function() {
        frameBuffer = testFactory.createEmptyFrameBuffer();
        frameBuffer.startOps();
        spyOn(frameBuffer, '_loopRequestData').and.stub();
        spyOn(frameBuffer, '_loopCleanup').and.stub();
    });

    it('It sets up initial state correctly', function() {
        expect(frameBuffer.getFirstIndex()).toEqual(-1);
        expect(frameBuffer.getLastIndex()).toEqual(-1);
        expect(frameBuffer.getCurrentIndex()).toEqual(-1);
        expect(frameBuffer.getUsedStockLevel()).toEqual(0);
        expect(frameBuffer.getUnusedStockLevel()).toEqual(0);
        expect(frameBuffer.isSequentialMode()).toBeTrue();
        expect(frameBuffer.isStockAvailable()).toBeFalse();
        expect(frameBuffer.isSafetyStock()).toBeFalse();
        expect(frameBuffer.isEmpty()).toBeTrue();
        expect(frameBuffer.isObsoleteStock()).toBeFalse();
        expect(frameBuffer.isOutOfSupply()).toBeFalse();
    });
});




