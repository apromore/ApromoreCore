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
    type = type || 'error';
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
        notification.fadeOut(2000, function() {
            notification.remove();
        });
    }, 2000);
}

Ap.common.setCookie = (name, value, days) => {
    let d = new Date();
    days = days || 90;
    d.setTime(d.getTime() + (days * 24 * 3600000));
    let expires = "expires="+ d.toUTCString();
    document.cookie = name + "=" + value + ";" + expires + ";path=/";
}