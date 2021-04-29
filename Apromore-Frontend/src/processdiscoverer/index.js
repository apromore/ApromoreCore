import graph from './graph';
import util from './util';
import search from './search';

let PD = function(pluginExecutionId,
                  processModelContainerId,
                  animationPanelContainerId,
                  timelineContainerId,
                  speedControlContainerId,
                  progressContainerId,
                  logInfoContainerId,
                  clockContainerId,
                  buttonsContainerId,
                  playClassName,
                  pauseClassName) {
    this._private = {
        'cy': undefined,
        'pluginExecutionId': pluginExecutionId,
        'processModelContainerId': processModelContainerId,
        'animationPanelContainerId': animationPanelContainerId,
        'timelineContainerId': timelineContainerId,
        'speedControlContainerId': speedControlContainerId,
        'progressContainerId': progressContainerId,
        'logInfoContainerId': logInfoContainerId,
        'clockContainerId': clockContainerId,
        'buttonsContainerId': buttonsContainerId,
        'playClassName': playClassName,
        'pauseClassName': pauseClassName,
        'logAnimation': undefined,
        'logAnimationMapController': undefined
    }
}

let pdfn = PD.prototype;
[   graph,
    util,
    search
].forEach(function(props) {
    Object.assign(pdfn, props);
});

export default PD;