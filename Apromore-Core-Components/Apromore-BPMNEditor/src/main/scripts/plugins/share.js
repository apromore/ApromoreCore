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
 * @namespace Apromore name space for plugins
 * @name Apromore.Plugins
 */
if (!Apromore.Plugins) {
    Apromore.Plugins = new Object();
}

/**
 * The share plugin provide share functionality
 *
 * @class Apromore.Plugins.Share
 * @extends Clazz
 * @param {Object} facade The editor facade for plugins.
 */
Apromore.Plugins.Share = Clazz.extend({
    facade: undefined,

    construct: function (facade) {
        this.facade = facade;

        /* Register share */
        this.facade.offer({
            'name': Apromore.I18N.Share.share,
            'functionality': this.share.bind(this),
            'group': Apromore.I18N.Share.group,
            'icon': Apromore.PATH + "images/xforms_export.png",
            'description': Apromore.I18N.Share.shareDesc,
            'index': 1,
            'minShape': 0,
            'maxShape': 0});

    },

    share: function (factor) {
            if (Apromore.Plugins.Share.shareExt) {
                Apromore.Plugins.Share.shareExt();
            }
    },

});


