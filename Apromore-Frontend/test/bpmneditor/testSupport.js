
export async function clickButton(buttonId) {
    let element = Ext.getCmp(buttonId);
    element.handler.call(element.scope);
}
