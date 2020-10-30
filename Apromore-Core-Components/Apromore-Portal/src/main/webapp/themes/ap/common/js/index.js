/**
 *  The main of common JavaScript  resources
 */

/**
 * Set main and module namespace to avoid conflicts
 *
 */
window.Ap = window.Ap || {
  pd: {}, // Process Discoverer
  lf: {}, // Log filter
  portal: {}, // Portal
  login: {}, // Login
  dash: {}, // Dashboard
  la: {}, // Log Animation
  common: {}, // Common
};

const NOTIFICATION_DELAY = 3000; // Delay to hide notification

/**
 * Custom-made notification
 *
 * This could be deprecated after moving to ZK-9
 *
 * @param message {String} Message to be shown
 * @param type {String} info or error
 */
Ap.common.notify = (message, type) => {
  const FADE_IN_MS = 400;
  const FADE_OUT_MS = 3000;
  type = type || 'error';
  let notification = $(`<div class="ap-notification ap-notification-${type}">${message}</div>`);
  let close = $('<span class="ap-notification-close"></span>');
  close.appendTo(notification);
  close.click(() => {
    try {
      notification.remove();
    } catch (e) {
      console.log(e);
    }
  });
  notification.appendTo('body');
  notification.fadeIn(FADE_IN_MS);
  setTimeout(() => {
    notification.fadeOut(FADE_OUT_MS, function() {
      notification.remove();
    });
  }, NOTIFICATION_DELAY);
};

/**
 * Set Cookie
 *
 * @param name {String} Cookie name
 * @param value {String} Cookie value
 * @param days {Number} Days of expiration
 */
Ap.common.setCookie = (name, value, days) => {
  let date = new Date();
  days = days || 90;
  date.setTime(date.getTime() + days * 24 * 3600000);
  let expires = 'expires=' + date.toUTCString();
  document.cookie = name + '=' + value + ';' + expires + ';path=/';
};
