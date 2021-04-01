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
        processMapController.loadProcessModel(animationModelContainerId, bpmnXML, function() {});
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
        let me = this;
        setTimeout(function() {
           me.initialize(setupDataJSON);
        }, 1000);
    }
}