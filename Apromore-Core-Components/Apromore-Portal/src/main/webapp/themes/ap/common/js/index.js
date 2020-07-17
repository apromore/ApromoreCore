window.Ap = window.Ap || {
    pd: {},
    lf: {},
    portal: {},
    login: {},
    dash: {},
    la: {},
    common: {},
}

Ap.common.notify = (message, type) => {
    type = type || 'info';
    let notification = $(`<div class="ap-notification ap-notification-${type}">${message}</div>`);
    let close = $('<span class="ap-notification-close"></span>')
    close.appendTo(notification);
    close.click(() => {
        try {
            notification.remove();
        } catch(e) {
            // pass
        }
    })
    notification.appendTo('body');
    notification.fadeIn(400);
    setTimeout(() => {
        notification.fadeOut(400, function() {
            notification.remove();
        });
    }, 2000);
}