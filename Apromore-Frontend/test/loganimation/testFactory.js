import LogAnimation from '../../src/loganimation/logAnimation';
import {AnimationContext} from "../../src/loganimation/animationContextState";
import FrameBuffer from "../../src/loganimation/frameBuffer";
import bpmnEditor from "../../src/bpmneditor/index";
import BPMNModelWrapper from "../../src/processmap/bpmnModelWrapper";

/**
 * @returns {LogAnimation}
 */
export async function createSimpleLogAnimation() {
    jasmine.getFixtures().fixturesPath = 'base/test/loganimation/fixtures';
    loadFixtures('logAnimationUI.html');
    let bpmn = require('./fixtures/simpleMap.bpmn');
    let setupData = require('./fixtures/setupData.txt');

    //Turn off loading plugins as not in scope of testing
    if (!Apromore.BPMNEditor) {
        Apromore.BPMNEditor = bpmnEditor;
    }
    Apromore.BPMNEditor.CONFIG.PLUGINS_ENABLED = false;
    let processMapController = new BPMNModelWrapper();
    await processMapController.loadProcessModel('editorcanvas', bpmn.default, function() {});
    let logAnimation = new LogAnimation(
        '101',
        processMapController,
        'editorcanvas',
        'timeline_svg',
        'speed-control',
        'ap-la-progress',
        'ap-la-info-tip',
        'ap-la-clock',
        'ap-la-buttons',
        'ap-mc-icon-play',
        'ap-mc-icon-pause');
    logAnimation.initialize(setupData.default);
    console.log(setupData.default);
    spyOn(logAnimation.getTokenAnimation(), '_loopDraw').and.stub();
    spyOn(logAnimation.getTokenAnimation(), '_loopBufferRead').and.stub();
    return logAnimation;
}

export async function createFullDataLogAnimation() {
    jasmine.getFixtures().fixturesPath = 'base/test/loganimation/fixtures';
    loadFixtures('logAnimationUI.html');
    let bpmn = require('./fixtures/simpleMap.bpmn');
    let setupData = require('./fixtures/setupData.txt');

    //Turn off loading plugins as not in scope of testing
    if (!Apromore.BPMNEditor) {
        Apromore.BPMNEditor = bpmnEditor;
    }
    Apromore.BPMNEditor.CONFIG.PLUGINS_ENABLED = false;
    let processMapController = new BPMNModelWrapper();
    await processMapController.loadProcessModel('editorcanvas', bpmn.default, function() {});
    let logAnimation = new LogAnimation(
        '101',
        processMapController,
        'editorcanvas',
        'timeline_svg',
        'speed-control',
        'ap-la-progress',
        'ap-la-info-tip',
        'ap-la-clock',
        'ap-la-buttons',
        'ap-mc-icon-play',
        'ap-mc-icon-pause');
    logAnimation.initialize(setupData.default);
    return logAnimation;
}

/**
 * @returns {FrameBuffer}
 */
export function createEmptyFrameBuffer() {
    jasmine.getFixtures().fixturesPath = 'base/test/loganimation/fixtures';
    let setupDataRaw = require('./fixtures/setupData.txt');
    let setupData = JSON.parse(setupDataRaw.default);
    let {recordingFrameRate} = setupData;
    let startMs = new Date(setupData.timeline.startDateLabel).getTime(); // Start date in milliseconds
    let endMs = new Date(setupData.timeline.endDateLabel).getTime(); // End date in milliseconds
    let startLogTime = new Date(setupData.timeline.startLogDateLabel).getTime(); // Start log date in milliseconds
    let endLogTime = new Date(setupData.timeline.endLogDateLabel).getTime(); // End log date in milliseconds
    let totalEngineS = setupData.timeline.totalEngineSeconds;
    let timelineSlots = setupData.timeline.timelineSlots;
    let animationContext = new AnimationContext('101', startMs, endMs, startLogTime, endLogTime, timelineSlots,
                                                totalEngineS, recordingFrameRate, 'Australia/Brisbane');

    let frameBuffer = new FrameBuffer(animationContext);
    return frameBuffer;
}

/**
 * @returns {FrameBuffer}
 */
export function createFullDataFrameBuffer() {
    let frameBuffer = createEmptyFrameBuffer();
    for (let i=1;i<=5;i++) {
        let chunkRaw = require('./fixtures/chunk' + i + '.txt');
        let frames = JSON.parse(chunkRaw.default);
        frameBuffer.write(frames, 0);
    }
    return frameBuffer;
}
