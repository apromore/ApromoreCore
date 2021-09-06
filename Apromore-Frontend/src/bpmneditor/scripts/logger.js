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

/**
 * The BPMNEditor.Log logger.
 */
let Log = {
    // Apromore constants.
    Apromore_LOGLEVEL: 5,
    Apromore_LOGLEVEL_TRACE: 5,
    Apromore_LOGLEVEL_DEBUG: 4,
    Apromore_LOGLEVEL_INFO: 3,
    Apromore_LOGLEVEL_WARN: 2,
    Apromore_LOGLEVEL_ERROR: 1,
    Apromore_LOGLEVEL_FATAL: 0,
    Apromore_CONFIGURATION_DELAY: 100,
    Apromore_CONFIGURATION_WAIT_ATTEMPTS: 10,

    __appenders: [
        { append: function(message) {
                console.log(message); }}
    ],

    trace: function() { if(this.Apromore_LOGLEVEL >= this.Apromore_LOGLEVEL_TRACE)
        this.__log('TRACE', arguments); },
    debug: function() { if(this.Apromore_LOGLEVEL >= this.Apromore_LOGLEVEL_DEBUG)
        this.__log('DEBUG', arguments); },
    info: function() { if(this.Apromore_LOGLEVEL >= this.Apromore_LOGLEVEL_INFO)
        this.__log('INFO', arguments); },
    warn: function() { if(this.Apromore_LOGLEVEL >= this.Apromore_LOGLEVEL_WARN)
        this.__log('WARN', arguments); },
    error: function() { if(this.Apromore_LOGLEVEL >= this.Apromore_LOGLEVEL_ERROR)
        this.__log('ERROR', arguments); },
    fatal: function() { if(this.Apromore_LOGLEVEL >= this.Apromore_LOGLEVEL_FATAL)
        this.__log('FATAL', arguments); },

    __log: function(prefix, messageParts) {

        messageParts[0] = (new Date()).getTime() + " "
            + prefix + " " + messageParts[0];
        var message = this.printf.apply(null, messageParts);

        this.__appenders.forEach(function(appender) {
            appender.append(message);
        });
    },

    addAppender: function(appender) {
        this.__appenders.push(appender);
    },

    printf: function() {
        var result = arguments[0];
        for (var i=1; i<arguments.length; i++)
            result = result.replace('%' + (i-1), arguments[i]);
        return result;
    }
};

export default Log;
