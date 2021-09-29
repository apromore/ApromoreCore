
export async function clickButton(buttonId) {
    let element = Ext.getCmp(buttonId);
    element.handler.call(element.scope);
}

// A simple way of mocking Ajax response for testing
// To be more sophisticated, can use jasmine-ajax
export function createMockAjaxResponseFunction(response, success) {
    return function (params) {
        if (success) {
            params.success(response);
        } else {
            params.error(response);
        }
    };
}
