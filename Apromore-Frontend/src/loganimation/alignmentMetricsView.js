/**
 * @Deprecated
 */
export default class AlignmentMetricsView {
    /**
     *
     * @param {LogAnimation} animation
     * @param {HTMLTableElement} uiContainer: the div container
     */
    constructor(animation, uiContainer) {
        this._animationController = animation;
        this._createMetricTables(this._animationController.getLogSummaries(), uiContainer);
    }

    /**
     * @param {Array} logSummaries
     * @param {HTMLTableElement} metricsTable
     * @private
     */
    _createMetricTables(logSummaries, metricsTable) {
        // Show metrics for every log
        for (let i = 0; i < logSummaries.length; i++) {
            let row = metricsTable.insertRow(i + 1);
            let cellLogNo = row.insertCell(0);
            let cellLogName = row.insertCell(1);
            let cellTotalCount = row.insertCell(2);
            let cellPlayCount = row.insertCell(3);
            let cellReliableCount = row.insertCell(4);
            let cellExactFitness = row.insertCell(5);

            cellLogNo.innerHTML = i + 1;
            cellLogNo.style.backgroundColor = logSummaries[i].color;
            cellLogNo.style.textAlign = 'center';

            if (logSummaries[i].filename.length > 50) {
                cellLogName.innerHTML = logSummaries[i].filename.substr(0, 50) + '...';
            } else {
                cellLogName.innerHTML = logSummaries[i].filename;
            }
            cellLogName.title = logSummaries[i].filename;
            cellLogName.style.font = '1em monospace';
            //cellLogName.style.backgroundColor = logSummaries[i].color;

            cellTotalCount.innerHTML = logSummaries[i].total;
            cellTotalCount.style.textAlign = 'center';
            cellTotalCount.style.font = '1em monospace';

            cellPlayCount.innerHTML = logSummaries[i].play;
            cellPlayCount.title = logSummaries[i].unplayTraces;
            cellPlayCount.style.textAlign = 'center';
            cellPlayCount.style.font = '1em monospace';

            cellReliableCount.innerHTML = logSummaries[i].reliable;
            cellReliableCount.title = logSummaries[i].unreliableTraces;
            cellReliableCount.style.textAlign = 'center';
            cellReliableCount.style.font = '1em monospace';

            cellExactFitness.innerHTML = logSummaries[i].exactTraceFitness;
            cellExactFitness.style.textAlign = 'center';
            cellExactFitness.style.font = '1em monospace';
        }
    }
}