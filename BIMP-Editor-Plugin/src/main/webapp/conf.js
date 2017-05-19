bimp.requests.protocol = "http://";
//bimp.requests.host =  window.location.hostname + ":8080";
bimp.requests.host = "bimp.cs.ut.ee:8080";
bimp.requests.url = "/qbp-simulator/rest/Simulation";
bimp.requests.authtoken = "ZnJlZTpmcmVl";

//var scriptHost = window.location.hostname + window.location.pathname + "/..";
var scriptHost = "bimp.cs.ut.ee";
// Full URL to the VSDX to BPMN .xsl file
bimp.visio.xslFilename = "http://" + scriptHost + "/components/com_qbp/vsdx2bpmn.xsl";
// Full URL to the BPMN viewer index.html file
//bimp.bpmnviewerurl = "http://" + scriptHost + "/components/com_qbp/bpmnviewer/index.html";
bimp.bpmnviewerurl = "http://" + window.location.hostname + ":" + window.location.port + window.location.pathname + "/../bpmnviewer.html";

