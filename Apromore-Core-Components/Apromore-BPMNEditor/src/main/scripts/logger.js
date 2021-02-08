/**
 * Copyright (c) 2006
 * Martin Czuchra, Nicolas Peters, Daniel Polak, Willi Tscheschner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/

if(!ORYX) var ORYX = {};

/**
 * The ORYX.Log logger.
 */
ORYX.Log = {
    // oryx constants.
    ORYX_LOGLEVEL: 5,
    ORYX_LOGLEVEL_TRACE: 5,
	ORYX_LOGLEVEL_DEBUG: 4,
	ORYX_LOGLEVEL_INFO: 3,
	ORYX_LOGLEVEL_WARN: 2,
	ORYX_LOGLEVEL_ERROR: 1,
	ORYX_LOGLEVEL_FATAL: 0,
	ORYX_CONFIGURATION_DELAY: 100,
	ORYX_CONFIGURATION_WAIT_ATTEMPTS: 10,

    __appenders: [
        { append: function(message) {
                console.log(message); }}
    ],

	trace: function() {	if(ORYX.Log.ORYX_LOGLEVEL >= ORYX.Log.ORYX_LOGLEVEL_TRACE)
        ORYX.Log.__log('TRACE', arguments); },
    debug: function() { if(ORYX.Log.ORYX_LOGLEVEL >= ORYX.Log.ORYX_LOGLEVEL_DEBUG)
        ORYX.Log.__log('DEBUG', arguments); },
    info: function() { if(ORYX.Log.ORYX_LOGLEVEL >= ORYX.Log.ORYX_LOGLEVEL_INFO)
        ORYX.Log.__log('INFO', arguments); },
    warn: function() { if(ORYX.Log.ORYX_LOGLEVEL >= ORYX.Log.ORYX_LOGLEVEL_WARN)
        ORYX.Log.__log('WARN', arguments); },
    error: function() { if(ORYX.Log.ORYX_LOGLEVEL >= ORYX.Log.ORYX_LOGLEVEL_ERROR)
        ORYX.Log.__log('ERROR', arguments); },
    fatal: function() { if(ORYX.Log.ORYX_LOGLEVEL >= ORYX.Log.ORYX_LOGLEVEL_FATAL)
        ORYX.Log.__log('FATAL', arguments); },

    __log: function(prefix, messageParts) {

        messageParts[0] = (new Date()).getTime() + " "
            + prefix + " " + messageParts[0];
        var message = this.printf.apply(null, messageParts);

        ORYX.Log.__appenders.forEach(function(appender) {
            appender.append(message);
        });
    },

    addAppender: function(appender) {
        ORYX.Log.__appenders.push(appender);
    },

    printf: function() {
		var result = arguments[0];
		for (var i=1; i<arguments.length; i++)
			result = result.replace('%' + (i-1), arguments[i]);
		return result;
	}
};



