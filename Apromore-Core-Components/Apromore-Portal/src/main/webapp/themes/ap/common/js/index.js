/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
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
  let notification = jQuery(`<div class="ap-notification ap-notification-${type}">${message}</div>`);
  let close = jQuery('<span class="ap-notification-close"></span>');
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

Ap.common.pullClientTimeZone = function () {
  const pad = (num) => ('0' + num).slice(-2);

  let tz = '';
  let offset = new Date().getTimezoneOffset();
  let sign = offset > 0 ? '-' : '+';
  offset = Math.abs(offset);
  const hours = pad(Math.floor(offset / 60));
  const minutes = pad(offset % 60);
  offset = 'GMT' + sign + hours + ':' + minutes;

  try {
    tz = Intl.DateTimeFormat().resolvedOptions().timeZone;
  } catch (e) {
    // pass
  }
  setTimeout(function () {
    zAu.send(new zk.Event(zk.Widget.$('$setTimeZone'), 'onClientUpdate', { offset, tz } ));
  }, 200);
}

/**
 * Inject additional global class to body
 *
 * @param klass {String} An additional CSS class
 */
Ap.common.injectGlobalClass = function (klass) {
    jQuery('body')[0].classList.add(klass);
}