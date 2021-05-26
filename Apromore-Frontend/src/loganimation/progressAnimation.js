import * as SVG from "@svgdotjs/svg.js";
import Sortable from 'sortablejs';

/**
 * ProgressAnimation manages the progress indicator of the animation.
 *
 * @author Bruce Nguyen
 */
export default class ProgressAnimation {
    /**
     * @param {LogAnimation} animation
     * @param {String} uiTopContainerId: id of the container div element
     * @param {String} uiPopupContainerId: id of the popup div element
     * @param {Array} logStartFrameIndexes: array of log start frame indexes
     * @param {Array} logEndFrameIndexes: array of log end frame indexes
     */
    constructor(animation, logStartFrameIndexes, logEndFrameIndexes, uiTopContainerId, uiPopupContainerId, colorPalette) {
        this._SVG_NS = "http://www.w3.org/2000/svg";
        this._containerId = uiTopContainerId;
        this._FULLPROGRESSPATH = 126;
        this._animationController = animation;
        this._animationContext = animation.getAnimationContext();
        this._logStartFrameIndexes = logStartFrameIndexes;
        this._logEndFrameIndexes = logEndFrameIndexes;
        this._colorPalette = colorPalette;
        this._svgProgressPaths = this._createProgressIndicators(animation.getLogSummaries(), uiTopContainerId, 1.0);
        this._createLogInfoPopups(animation.getLogSummaries(), uiTopContainerId, uiPopupContainerId);
        this._currentSpeedLevel = 1.0;
        this._sortable = null; // sortable object
        this.initSortable();
    }

    destroy() {
        $j('#' + this._containerId).empty();
    }

    updateProgress(frameIndex) {
        for (let logIndex = 0; logIndex < this._animationController.getLogSummaries().length; logIndex++) {
            let startFrameIndex = this._logStartFrameIndexes[logIndex];
            let endFrameIndex = this._logEndFrameIndexes[logIndex];
            let totalLogFrames = endFrameIndex - startFrameIndex + 1;
            let numberOfFrames = frameIndex > startFrameIndex ? (frameIndex - startFrameIndex) : 0;
            let progress =  numberOfFrames >=  totalLogFrames ? this._FULLPROGRESSPATH : this._FULLPROGRESSPATH*numberOfFrames/totalLogFrames;
            let svgPathEl = this._svgProgressPaths[logIndex];
            svgPathEl.style.strokeDashoffset = progress;
        }
    }

    /**
     * Set up sortable and set a hook to propagate the reordering to timeline positions
     */
    initSortable() {
        let me = this;
        let el = document.getElementById('ap-la-progress');

        this._sortable = Sortable.create(el, {
          orientation: 'horizontal',
          onEnd: function () {
            let items = jq('.progress-c', el);
            let logOrder = jq.map(items, (item) => parseInt(jq(item).data('idx')))
            me._animationController.setLogOrder(logOrder)
          },
        });
    }

    /**
     * Create log enable/disable
     */
    _createLogToggle(logIndex) {
        let me = this;
        let toggle = $j(`<input type="checkbox" class="ap-la-enable" id="ap-la-enable-${logIndex}" data-idx="${logIndex}" checked>`);
        toggle.change(
            (function (idx) {
                return function (e) {
                    e.stopPropagation();
                    me._animationController.setLogEnabled(idx, $j(`#ap-la-enable-${idx}`)[0].checked);
                };
            })(logIndex)
        );
        return toggle;
    }

    /**
     * Create log label and picker
     */
    _createLogLabelAndPicker(logIndex, filename, color) {
        let me = this;
        let toggle = me._createLogToggle(logIndex);
        let name = $j(`<span class="ap-la-logname"> ${filename}</span>`);
        let picker = $j(`<input class="ap-la-cpicker" id="ap-la-cpicker-${logIndex}" value="${color}" />`);
        let label = $j(`<div class="label"></div>`)
            .append(toggle)
            .append(name)
            .append(picker);
        return label;
    }

    /**
     * Create progress SVG elements with structure as follows, 0..1: index for logs
     *  <div id="ap-la-progress">
     *      <svg id="progressbar-0">
     *          <g id="ap-la-progress-0">
     *              <path ""/> this is the circular path to indicate the progress
     *          </g>
     *      </svg>
     *      <svg id="progressbar-1">
     *          <g id="ap-la-progress-1">
     *              <path ""/> this is the circular path to indicate the progress
     *          </g>
     *      </svg>
     *  </div>
     * @param {Array} logSummaries
     * @param {HTMLDivElement} uiTopContainerId
     * @param {Number} speedRatio
     * @return {SVGElement[]}
     * @private
     */
    _createProgressIndicators(logSummaries, uiTopContainerId, speedRatio) {
        let me = this;
        let log;
        let color;
        let svgProgress, svgProgressPaths = [];
        let progressTopContainer = $j('#'+ uiTopContainerId);
        progressTopContainer.empty();
        for (let logIndex = 0; logIndex < logSummaries.length; logIndex++) {
            log = logSummaries[logIndex];
            color = this._colorPalette.getSelectedColor(logIndex);
            svgProgress = $j(`<svg id="progressbar-${logIndex}" xmlns="${this._SVG_NS}" viewBox="-10 0 20 40"></svg>`);
            let svgProgressPathGroup = this._createProgressIndicatorsForLog(uiTopContainerId, logIndex, log, speedRatio);
            svgProgress.append(svgProgressPathGroup);
            svgProgressPaths.push(svgProgressPathGroup.firstChild); // get the progress path element
            let label = me._createLogLabelAndPicker(logIndex, log.filename, color);
            progressTopContainer.append(
                $j(`<div class="progress-c" id="progress-c-${logIndex}" data-idx="${logIndex}"></div>`)
                    .append(svgProgress)
                    .append(label)
            );
        }
        return svgProgressPaths;
    }

    /*
     * Create progress indicator for one log
     * log: the log object (name, color, traceCount, progress, tokenAnimations)
     * x,y: the coordinates to draw the progress bar
     */
    /**
     * Create progress indicator for one log
     * @param uiTopContainerId: id of the container DIV
     * @param logIndex: ordinal number of one log
     * @param log: log summary data
     * @param speedRatio
     * @returns {*}
     * @private
     */
    _createProgressIndicatorsForLog(uiTopContainerId, logIndex, log, speedRatio) {
        speedRatio = speedRatio || 1;
        let color = this._colorPalette.getSelectedColor(logIndex);
        let progress = new SVG.G().attr({
            id: uiTopContainerId + '-' + logIndex,
        }).node;

        let path = 'M ' + 0 + ',' + 0 + ' m 0, 0 a 20,20 0 1,0 0.00001,0';
        let pie = new SVG.Path().plot(path).attr({
            fill: color,
            'fill-opacity': 0.5,
            stroke: color,
            'stroke-width': '5',
            'stroke-dasharray': `0 ${this._FULLPROGRESSPATH} ${this._FULLPROGRESSPATH} 0`,
            'stroke-dashoffset': '1',
        }).node;
        progress.appendChild(pie);
        return progress;
    }

    /**
     * Create a popup window when hovering the mouse over the progress indicator.
     * @param {Array} logSummaries
     * @param {String} uiTopContainerId
     * @param {String} uiPopupContainerId
     * @private
     */
    _createLogInfoPopups(logSummaries, uiTopContainerId, uiPopupContainerId) {
        let logInfo = $j('#' + uiPopupContainerId);
        let props = [
            {
                id: 'info-log',
                key: 'filename',
            },
            {
                id: 'info-traces',
                key: 'total',
            },
            {
                id: 'info-replayed',
                key: 'play',
                title: 'unplayTraces',
            },
            {
                id: 'info-reliable',
                key: 'reliable',
                title: 'unreliableTraces',
            },
            {
                id: 'info-fitness',
                key: 'exactTraceFitness',
            },
        ];

        function getProps(log) {
            props.forEach(function(prop) {
                $j('#' + prop.id).text(log[prop.key]).attr('title', log[prop.title || prop.key]);
            });
        }

        let me = this;
        for (let logIndex = 0; logIndex < logSummaries.length; logIndex++) {
            let pId = '#' + uiTopContainerId + '-' + logIndex; // this element is created in _createProgressIndicatorsForLog()
            $j(`#ap-la-cpicker-${logIndex}`).spectrum({
                type: "color",
                showInput: true,
                showInitial: true,
                showAlpha: false,
                allowEmpty: false,
                showButtons: true,
                hideAfterPaletteSelect: true,
                containerClassName: 'ap-la-cpicker-wrapper',
                palette: me._colorPalette.getPalette(),
                change: (function (idx) {
                    return function (color) {
                        let colorCode = color.toHexString();
                        me._colorPalette.matchAndSelectColor(idx, colorCode);
                        $j(`#ap-la-cpicker-${idx}`).spectrum('hide');
                        // Update progress
                        let progress = $j(`#ap-la-progress-${idx} path`)[0];
                        progress.style.fill = colorCode;
                        progress.style.stroke = colorCode;
                        // Update timeline
                        let timeline = $j(`#ap-la-timeline-${idx}`)[0];
                        timeline.style.stroke = colorCode;
                    };
                })(logIndex)
            });
            $j(pId).click(
                (function (idx) {
                    return function (e) {
                        e.stopPropagation();
                        $j(`#ap-la-cpicker-${idx}`).spectrum('show');
                    };
                })(logIndex)
            );
            $j(pId).hover(
                (function(idx) {
                    let log = logSummaries[idx - 1];
                    return function() {
                        let color = me._colorPalette.getSelectedColor(idx - 1);
                        getProps(log);
                        let {top, left} = $j(pId).offset();
                        let bottom = `calc(100vh - ${top - 10}px)`;
                        left += 20;
                        logInfo.attr('data-log-idx', idx);
                        logInfo.css({bottom, left});
                        logInfo.css('background-color', color);
                        $j('.tip-arrow', logInfo).css('border-top-color', color);
                        logInfo.show();
                    };
                })(logIndex + 1),
                function() {
                    logInfo.hide();
                },
            );
        }
    }
}