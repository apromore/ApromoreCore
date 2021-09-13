import * as testFactory from "./testFactory";
import DataRequester from "../../src/loganimation/dataRequester";

describe('After writing frames to a Buffer', function () {
    /** @type Buffer */
    let frameBuffer;

    beforeEach(function() {
        jasmine.clock().uninstall();
        jasmine.clock().install();
    });

    afterEach(function() {
        jasmine.clock().uninstall();
    });

    it('Buffer requests data to fill up frames when stock safety level has not been reached', function() {
        frameBuffer = testFactory.createEmptyFrameBuffer();
        let dataRequester = new DataRequester('101');
        frameBuffer.setDataRequester(dataRequester);
        expect(frameBuffer.size()).toEqual(0);
        expect(frameBuffer.isSafetyStock()).toBeFalse();

        spyOn(frameBuffer, '_loopCleanup').and.stub(); // turn off the clean up loop to test data request
        let spy = spyOn(dataRequester, 'requestData');
        frameBuffer.startOps();

        jasmine.clock().tick(2000);
        expect(spy).toHaveBeenCalled();
    });

    it('Buffer doesn\'t request data to fill up frames when stock safety level has been reached', function() {
        frameBuffer = testFactory.createEmptyFrameBuffer();
        let dataRequester = new DataRequester('101');
        frameBuffer.setDataRequester(dataRequester);

        let chunkRaw = require('./fixtures/chunk1.txt');
        let frames = JSON.parse(chunkRaw.default);
        frameBuffer.write(frames, 0);
        frameBuffer.write(frames, 0);
        frameBuffer.write(frames, 0); // reach stock safety level
        expect(frameBuffer.size()).toEqual(900);
        expect(frameBuffer.isSafetyStock()).toBeTrue();

        spyOn(frameBuffer, '_loopCleanup').and.stub(); // turn off the clean up loop to test data request
        let spy = spyOn(dataRequester, 'requestData');
        frameBuffer.startOps();

        jasmine.clock().tick(2000);
        expect(spy).not.toHaveBeenCalled();
    });

});