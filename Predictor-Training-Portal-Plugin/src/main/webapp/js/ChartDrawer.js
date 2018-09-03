Chart.defaults.global.legend.display = false;

const graphContainer = "graph-container";
const canvas = "chart_canvas";
let chart = null;
let dataSet = null;

function prepareGraphContainer(isHeatMap) {
    let container = document.getElementById(graphContainer);
    while (container.firstChild) {
        container.removeChild(container.firstChild);
    }
    if (!isHeatMap) {
        let c = document.createElement("canvas");
        c.setAttribute("id", canvas);
        container.appendChild(c);
    }
}

const getCanvasContext = () => {
    let canvas = document.getElementById('chart_canvas');
    return canvas.getContext('2d');
};

const linerDataSetData = (payload, chart_label) => {
    const blue = 'rgba(0, 147, 249, 0.4)';
    dataSet = [{
        label: chart_label,
        data: JSON.parse(payload),
        borderColor: blue,
        backgroundColor: 'rgba(0, 147, 249, 0.2)',
        fill: false
    }];

    const elem = document.querySelector(`.c${chart_label}`);
    if (elem != null) {
        elem.style.backgroundColor = blue;
    }

    return dataSet
};

const scalesData = (xLabel, yLabel) => {
    return {
        xAxes: [{
            display: true,
            scaleLabel: {
                display: true,
                labelString: xLabel,
                fontSize: 18,
                fontStyle: 'bold'
            }
        }],
        yAxes: [{
            display: true,
            scaleLabel: {
                display: true,
                labelString: yLabel,
                fontSize: 18,
                fontStyle: 'bold'
            }
        }]
    }
};

const generateLabels = (n_of_events) => {
    let labels = [];
    for (let i = 1; i <= n_of_events; i++) {
        labels.push(i.toString())
    }
    return labels
};

function scatterPlot(payload, chart_label) {
    prepareGraphContainer(false);
    let ctx = getCanvasContext();
    chart = Chart.Scatter(ctx, {
        data: {
            datasets: linerDataSetData(payload, chart_label)
        },
        options: {
            scales: scalesData('Actual', 'Predicted'),
            tooltips: {
                callbacks: {
                    label: function (tooltipItem, chart) {
                        return 'Difference: ' + (tooltipItem.xLabel - tooltipItem.yLabel)
                    }
                }
            }
        }
    });
}

function addDataSet(label, payload) {
    const color = randomColor();

    const elem = document.querySelector(`.c${label}`);
    if (elem != null) {
        elem.style.backgroundColor = color;
    }

    chart.data.datasets.push({
        label: label,
        data: JSON.parse(payload),
        fill: false,
        borderColor: color,
        backgroundColor: color
    });
    chart.update()
}

function removeDataSet(label) {
    const data = chart.data.datasets;
    let index = -1;
    for (let i = 0; i < data.length; i++) {
        if (data[i].label === label) {
            index = i;
        }
    }
    if (index !== -1) {
        data.splice(index, 1);
    }

    const elem = document.querySelector(`.c${label}`);
    if (elem != null) {
        elem.style.backgroundColor = 'white';
    }

    chart.update()
}

function lineChart(payload, chart_label, n_of_events, axis_label) {
    prepareGraphContainer(false);
    let ctx = getCanvasContext();
    chart = Chart.Line(ctx, {
        data: {
            datasets: linerDataSetData(payload, chart_label),
            labels: generateLabels(n_of_events)
        },
        options: {
            elements: {
                line: {
                    tension: 0
                }
            },
            scales: scalesData('Number of events', axis_label),
            tooltips: {
                enabled: true,
                callbacks: {
                    title: function (items) {
                        return `${axis_label}: ${items[0].yLabel}`;
                    },

                    label: function (items) {
                        return `Number of events: ${items.xLabel}`
                    }
                }
            }
        }
    })
}

function barChart(payload, chart_label, labels) {
    prepareGraphContainer(false);
    let ctx = getCanvasContext();
    chart = new Chart(ctx, {
        type: 'horizontalBar',
        data: {
            labels: JSON.parse(labels),
            datasets: [
                {
                    label: chart_label,
                    data: JSON.parse(payload),
                    backgroundColor: 'rgba(0, 147, 249, 0.4)',
                    borderColor: 'rgba(0, 147, 249, 0.2)',
                    borderWidth: 1
                }
            ]
        },

        options: {
            elements: {
                rectangle: {
                    borderWidth: 2
                }
            },
            responsive: true
        }
    })
}

const textStyle = () => {
    return {
        fontSize: '21px',
        fontWeight: 'bold'
    }
};

const labelsConfig = (datalenght) => {
    let fontSize;
    if (datalenght <= 10) {
        fontSize = '16px'
    } else if (datalenght <= 20) {
        fontSize = '12px'
    } else {
        fontSize = '8px'
    }

    return {
        style: {
            fontSize: fontSize
        }
    }
};

function heatMap(payload, title, xLabels, yLabels) {
    const data = JSON.parse(payload);
    prepareGraphContainer(true);
    Highcharts.chart(graphContainer, {
        chart: {
            type: 'heatmap',
            plotBorderWidth: 1
        },

        title: {
            text: null,
        },

        xAxis: {
            categories: JSON.parse(xLabels),
            labels: labelsConfig(data.length),
            title: {
                text: 'Predicted',
                style: textStyle()
            }
        },

        yAxis: {
            categories: JSON.parse(yLabels),
            labels: labelsConfig(data.length),
            title: {
                text: 'Actual',
                style: textStyle()
            }
        },

        colorAxis: {
            min: 0,
            minColor: '#FFFFFF',
            maxColor: Highcharts.getOptions().colors[0]
        },

        legend: {
            align: 'right',
            layout: 'vertical',
            margin: 0,
            verticalAlign: 'top',
            y: 25,
            symbolHeight: 280
        },

        tooltip: {
            formatter: function () {
                return `actual <b>${this.series.yAxis.categories[this.point.y]}</b><br/>predicted ${this.series.xAxis.categories[this.point.x]}<b></b><br/><b>${this.point.value}</b> times`
            }
        },

        series: [{
            name: title,
            borderWidth: 1,
            data: data,
            dataLabels: {
                enabled: true,
                color: '#000',
                style: {
                    fontSize: '19px'
                }
            },
            states: {
                hover: {
                    enabled: false
                }
            }
        }]
    });
}

const randomColor = () => {
    const r = randomInRange();
    const g = randomInRange();
    const b = randomInRange();
    return `rgb(${r},${g},${b})`
};

const randomInRange = () => Math.floor(Math.random() * 255);

