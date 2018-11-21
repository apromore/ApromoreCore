/**
 * Copyright (c) 2008
 * Willi Tscheschner
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
 * @namespace Oryx name space for different utility methods
 * @name ORYX.Utils
*/
ORYX.Utils = {
    /**
     * General helper method for parsing a param out of current location url
     * @example
     * // Current url in Browser => "http://oryx.org?param=value"
     * ORYX.Utils.getParamFromUrl("param") // => "value"
     * @param {Object} name
     */
    getParamFromUrl: function(name) {
        name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
        var regexS = "[\\?&]" + name + "=([^&#]*)";
        var regex = new RegExp(regexS);
        var results = regex.exec(window.location.href);
        if (results == null) {
            return null;
        } else {
            return results[1];
        }
    },
    isGlossaryEntry: function (a) {
        return typeof a === "string" && (this.isOldGlossarySchema(a) || a.startsWith("/glossary/"))
    },
    isOldGlossarySchema: function (a) {
        return typeof a === "string" && !! a.match(/\glossary\:\/\/.+\/[\w\W]+\;\;/g)
    },
    getGlossaryId: function (a) {
        if (ORYX.Utils.isOldGlossarySchema(a)) {
            return (a || "").split(";;").invoke("replace", /\glossary\:\/\//g, "").invoke("replace", /\/[\w\W]+/g, "").first()
        }
        return ""
    },
    glossaryTitle: function (a) {
        if (ORYX.Utils.isOldGlossarySchema(a)) {
            return (a || "").replace(/;;$/, "").replace(/\glossary\:\/\/.+?\//g, "")
        }
        return ""
    },
    getInGlossarySchema: function (b, a) {
        b = (b || "").replace(/([\/]|glossary)/g, "");
        return "glossary://" + b + "/" + a + ";;"
    },
    isIPad: function () {
        return !!Ext.isIPad;
    },
    getBrowserVersion: function () {
        var agent = window.navigator.userAgent.toLowerCase();
        var version = "";
        if (Ext.isIE) {
            version = window.navigator.userAgent.toLowerCase().match(/msie.+?;/g).first().replace(/[^0-9\.]/g, "");
        } else {
            version = window.navigator.userAgent.toLowerCase().split(/\//g).last().split(/\s/g).first().replace(/[^0-9\.]/g, "");
        }
        version = (version.match(/[0-9]+\.[0-9]+/) || ["xxx"]).first();
        return version;
    },
    isMetaKey: function (a) {
        return !!(a.shiftKey || a.ctrlKey || a.metaKey);
    },
    buildGetColorForLabel: function (a) {
        return ORYX.Utils.createGenericPropertyCheck(a, "color", "value", "");
    },
    buildIsRichtextForLabel: function (a) {
        return ORYX.Utils.createGenericPropertyCheck(a, "string", "richtextEnabled", false);
    },
    createGenericPropertyCheck: function (e, d, c, a) {
        var b = e.properties().select(function (f) {
            return f.type().toLowerCase() === d.toLowerCase()
        });
        if (b.length === 0) {
            return function () {
                return a;
            }
        }
        return function (f) {
            var h = f.id;
            var g = b.find(function (k) {
                return k.refToView().find(function (m) {
                    return h.endsWith(m);
                })
            });
            if (g && g[c] instanceof Function) {
                return g[c]()
            }
            return a;
        }
    },
    rgbToHsl: function (a, k, n) {
        a /= 255;
        k /= 255;
        n /= 255;
        var o = Math.max(a, k, n),
            e = Math.min(a, k, n);
        var f, p, c = (o + e) / 2;
        if (o == e) {
            f = p = 0
        } else {
            var m = o - e;
            p = c > 0.5 ? m / (2 - o - e) : m / (o + e);
            switch (o) {
                case a:
                    f = (k - n) / m + (k < n ? 6 : 0);
                    break;
                case k:
                    f = (n - a) / m + 2;
                    break;
                case n:
                    f = (a - k) / m + 4;
                    break
            }
            f /= 6
        }
        return [f, p, c]
    },
    hslToRgb: function (k, o, f) {
        var a, m, n;
        if (o == 0) {
            a = m = n = f;
        } else {
            function e(h, g, b) {
                if (b < 0) {
                    b += 1;
                }
                if (b > 1) {
                    b -= 1;
                }
                if (b < 1 / 6) {
                    return h + (g - h) * 6 * b;
                }
                if (b < 1 / 2) {
                    return g;
                }
                if (b < 2 / 3) {
                    return h + (g - h) * (2 / 3 - b) * 6;
                }
                return h;
            }
            var c = f < 0.5 ? f * (1 + o) : f + o - f * o;
            var d = 2 * f - c;
            a = e(d, c, k + 1 / 3);
            m = e(d, c, k);
            n = e(d, c, k - 1 / 3)
        }
        return [Math.round(a * 255), Math.round(m * 255), Math.round(n * 255)]
    },
    adjustLightness: function (d, k) {
        if (!d) {
            return "";
        } else {
            if (k === 1) {
                return d;
            } else {
                if (k === 0) {
                    return "#ffffff";
                }
            }
        }
        d = d.length === 7 && d[0] === "#" ? d : "#ffffff";
        var a = Math.min(255, parseInt(d[1] + d[2], 16));
        var e = Math.min(255, parseInt(d[3] + d[4], 16));
        var h = Math.min(255, parseInt(d[5] + d[6], 16));
        var m = ORYX.Utils.rgbToHsl(a, e, h);
        var c = m[2];
        m[2] = 0.15 * Math.tan(2.4 * m[2] + 1.8) + 0.8;
        var f = ORYX.Utils.hslToRgb(m[0], m[1], (m[2] > 1 ? c : m[2]));
        var n = [parseInt(Math.min(255, f[0])), parseInt(Math.min(255, f[1])), parseInt(Math.min(255, f[2]))].map(function (b) {
            return (b < 16 ? "0" : "") + b.toString(16)
        });
        return "#" + n.join("")
    },
	adjustGradient: function(gradient, reference){
        if (!gradient) {
            return
        }
        var c = reference.getAttributeNS(null, "stop-color") || "#ffffff";
        c = c.length === 7 && c[0] === "#" ? c : "#ffffff";
        var a = Math.min(255, parseInt(c[1] + c[2], 16));
        var f = Math.min(255, parseInt(c[3] + c[4], 16));
        var k = Math.min(255, parseInt(c[5] + c[6], 16));
        var e = ORYX.Utils.rgbToHsl(a, f, k);
        e[2] = Math.min(e[2] + (0.9 * e[2]) + (e[2] <= 0.2 ? (2 - e[2]) * 0.2 : 0), 1);
        var h = ORYX.Utils.hslToRgb(e[0], e[1], e[2]);
        var n = function (g) {
            var b = [Math.min(255, h[0]), Math.min(255, h[1]), Math.min(255, h[2])].map(function (o) {
                return (o < 16 ? "0" : "") + o.toString(16)
            });
            return "#" + b.join("")
        };
        $A(gradient.getElementsByTagName("stop")).each(function (b, g) {
            if (g == b || (g.getAttributeNS(null, "class") || "").include("ignore-adjust")) {
                return
            }
            g.setAttributeNS(null, "stop-color", n("#FFFFFF"))
        }.bind(this, reference));

	}
};


