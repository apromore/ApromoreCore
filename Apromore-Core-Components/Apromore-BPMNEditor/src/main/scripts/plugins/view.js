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
 * @namespace Apromore name space for plugins
 * @name Apromore.Plugins
 */
if (!Apromore.Plugins)
    Apromore.Plugins = new Object();

/**
 * The view plugin offers all of zooming functionality accessible over the
 * tool bar. This are zoom in, zoom out, zoom to standard, zoom fit to model.
 *
 * @class Apromore.Plugins.View
 * @extends Clazz
 * @param {Object} facade The editor facade for plugins.
 */
Apromore.Plugins.View = Clazz.extend({
    /** @lends Apromore.Plugins.View.prototype */
    facade: undefined,

    construct: function (facade) {
        this.facade = facade;

        /* Register zoom in */
        this.facade.offer({
            'name': window.Apromore.I18N.View.zoomIn,
            'functionality': this.zoomIn.bind(this),
            'group': window.Apromore.I18N.View.group,
            'icon': Apromore.PATH + "images/ap/zoom-in.svg",
            'description': window.Apromore.I18N.View.zoomInDesc,
            'index': 1,
            'minShape': 0,
            'maxShape': 0});

        /* Register zoom out */
        this.facade.offer({
            'name': window.Apromore.I18N.View.zoomOut,
            'functionality': this.zoomOut.bind(this),
            'group': window.Apromore.I18N.View.group,
            'icon': Apromore.PATH + "images/ap/zoom-out.svg",
            'description': window.Apromore.I18N.View.zoomOutDesc,
            'index': 2,
            'minShape': 0,
            'maxShape': 0});

        /* Register zoom fit to model */
        this.facade.offer({
            'name': window.Apromore.I18N.View.zoomFitToModel,
            'functionality': this.zoomFitToModel.bind(this),
            'group': window.Apromore.I18N.View.group,
            'icon': Apromore.PATH + "images/ap/zoom-to-fit.svg",
            'description': window.Apromore.I18N.View.zoomFitToModelDesc,
            'index': 4,
            'minShape': 0,
            'maxShape': 0 });
    },

    zoomIn: function (factor) {
        this.facade.getEditor().zoomIn();
    },

    zoomOut: function (factor) {
        this.facade.getEditor().zoomOut();
    },



    /**
     * It calculates the zoom level to fit whole model into the visible area
     * of the canvas. Than the model gets zoomed and the position of the
     * scroll bars are adjusted.
     *
     */
    zoomFitToModel: function () {
        this.facade.getEditor().zoomFitToModel();
    }
});


