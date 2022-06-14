/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

import CONFIG from './../config';

/**
 * The share plugin provide share functionality
 *
 * @class Share
 * @param {Object} facade The editor facade for plugins.
 */
export default class Share {

    constructor (facade) {
        this.facade = facade;

        /* Register share */
        this.facade.offer({
            'btnId': 'ap-id-editor-share-btn',
            'name': Apromore.I18N.Share.share,
            'functionality': this.share.bind(this),
            'icon': CONFIG.PATH + "images/ap/share.svg",
            'description': Apromore.I18N.Share.shareDesc,
            'index': 1,
            'groupOrder': 5
        });

    }

    share(factor) {
            if (Apromore.BPMNEditor.Plugins.Share.shareExt) {
                Apromore.BPMNEditor.Plugins.Share.shareExt();
            }
    }

};
