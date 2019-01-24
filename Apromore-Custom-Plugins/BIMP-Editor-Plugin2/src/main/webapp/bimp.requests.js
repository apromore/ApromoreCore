bimp.requests = {
    protocol: "https://",
    host: "bimp.cs.ut.ee:8443",  //"localhost:8443",
    url: "/qbp-simulator/rest/Simulation",
    apiNs: "http://www.qbp-simulator.com/ApiSchema201212",
    simulationId: "",
    authtoken: "",
    startSimulationUrl: function () {
        return this.protocol + this.host + this.url;
    },
    getSimulationStatusUrl: function (id) {
        return this.protocol + this.host + this.url + "/" + id;
    },
    getSimulationResultsUrl: function (id) {
        return this.protocol + this.host + this.url + "/" + id + "/KPI";
    },
    getResultsDistributionUrl: function (id) {
        return this.protocol + this.host + this.url + "/" + id + "/Distribution/";
    },
    getSimulationResultsMxmlUrl: function (id) {
        return this.protocol + $.base64.decode(this.authtoken) + "@" + this.host + this.url + "/" + id + "/MXML";
    },
    getSimulationResultsCsvUrl: function (id) {
        return this.protocol + $.base64.decode(this.authtoken) + "@" + this.host + this.url + "/" + id + "/CSV";
    },
    startSimulationRequest: function () {
        this._xmlns = bimp.requests.apiNs;
        this._generateMXML = false;
        this.modelData = {};
        this.modelData.__cdata = "";
    },

    startSimulation: function (requestObj, callback, errorCallback) {
        var req = {
            StartSimulationRequest: requestObj
        };
        var data = x2js.json2xml_str(req);
        $.ajax({
            url: this.startSimulationUrl(),
            type: "POST",
            headers: {
                "Authorization": "Basic " + bimp.requests.authtoken
            },
            data: data,
            contentType: "application/xml; charset=utf-8",
            dataType: "xml",
            success: callback,
            error: errorCallback
        });
    },
    getSimulationStatus: function (successCallback, errorCallback) {
        $.ajax({
            url: this.getSimulationStatusUrl(this.simulationId),
            type: "GET",
            headers: {
                "Authorization": "Basic " + bimp.requests.authtoken
            },
            dataType: "xml",
            success: successCallback,
            error: errorCallback
        });
    },
    getSimulationResults: function (successCallback, errorCallback) {
        $.ajax({
            url: this.getSimulationResultsUrl(this.simulationId),
            type: "GET",
            headers: {
                "Authorization": "Basic " + bimp.requests.authtoken
            },
            dataType: "xml",
            success: successCallback,
            error: errorCallback
        });
    },
    getSimulationResultsMxml: function () {
        window.open(this.getSimulationResultsMxmlUrl(this.simulationId), '_blank');
    },
    getSimulationResultsCsv: function () {
        window.open(this.getSimulationResultsCsvUrl(this.simulationId), '_blank');
    },
    getProcessDurations: function (successCallback) {
        $.ajax({
            url: this.getResultsDistributionUrl(this.simulationId) + "ProcessDuration?bars=10",
            type: "GET",
            headers: {
                "Authorization": "Basic " + bimp.requests.authtoken
            },
            dataType: "xml",
            success: function (data) {
                successCallback(x2js.xml2json(data).SimulationHistogramResponse);
            }
        });
    },
    getProcessCycleTimes: function (successCallback) {
        $.ajax({
            url: this.getResultsDistributionUrl(this.simulationId) + "CycleTime?bars=10",
            type: "GET",
            headers: {
                "Authorization": "Basic " + bimp.requests.authtoken
            },
            dataType: "xml",
            success: function (data) {
                successCallback(x2js.xml2json(data).SimulationHistogramResponse);
            }
        });
    },
    getProcessWaitingTimes: function (successCallback) {
        $.ajax({
            url: this.getResultsDistributionUrl(this.simulationId) + "ProcessWaitingTime?bars=10",
            type: "GET",
            headers: {
                "Authorization": "Basic " + bimp.requests.authtoken
            },
            dataType: "xml",
            success: function (data) {
                successCallback(x2js.xml2json(data).SimulationHistogramResponse);
            }
        });
    },
    getProcessCosts: function (successCallback) {
        $.ajax({
            url: this.getResultsDistributionUrl(this.simulationId) + "ProcessCost?bars=10",
            type: "GET",
            headers: {
                "Authorization": "Basic " + bimp.requests.authtoken
            },
            dataType: "xml",
            success: function (data) {
                successCallback(x2js.xml2json(data).SimulationHistogramResponse);
            }
        });
    }
};
