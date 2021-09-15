import LogAnimation from "./logAnimation";
import BPMNModelWrapper from "../processmap/bpmnModelWrapper";

/**
 * This object is a LogAnimation where the model editor is BPMN.io.
 * @author Bruce Nguyen
 */
export default class LogAnimationBpmn extends LogAnimation {
    constructor (bpmnXML,
                 setupDataJSON,
                  pluginExecutionId,
                  animationModelContainerId,
                  timelineContainerId,
                  speedControlContainerId,
                  progressContainerId,
                  logInfoContainerId,
                  clockContainerId,
                  buttonsContainerId,
                  playClassName,
                  pauseClassName) {
        let processMapController = new BPMNModelWrapper();
        super(pluginExecutionId,
            processMapController,
            animationModelContainerId,
            timelineContainerId,
            speedControlContainerId,
            progressContainerId,
            logInfoContainerId,
            clockContainerId,
            buttonsContainerId,
            playClassName,
            pauseClassName);
    }

    async init(bpmnXML, setupDataJSON, animationModelContainerId) {
        await this.processMapController.loadProcessModel(animationModelContainerId, bpmnXML, function() {});
        //Leaving timeout to wait for Ajax response from loading plugins
        //TODO: await the Ajax response instead of setting a timer
        let me = this;
        setTimeout(function() {
           me.initialize(setupDataJSON);
        }, 1000);
    }
}
