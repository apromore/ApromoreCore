/* Previously copied from the top-level site.properties file at compile time (which is bad idea since site.properties can change at runtime); now a constant */
if (!Apromore) {
    var Apromore = {};
}
if (!Apromore.CONFIG) {
    Apromore.CONFIG = {};
}
Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX =  '/bpmneditor';
Apromore.CONFIG.EDITOR_PATH = "/editor";
