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

import CONFIG from './../config';

/**
 * The attachment plugin provide toggle attachment functionality
 *
 * @class Attachment
 * @param {Object} facade The editor facade for plugins.
 */
export default class Attachment {

    constructor (facade) {
        this.facade = facade;
        this.btnId = 'ap-id-editor-attachment-btn';
        this.state = true;

        this.facade.offer({
            'btnId': this.btnId,
            'name': Apromore.I18N.Attachment.attachment,
            'functionality': this.toggleAttachment.bind(this),
            'icon': this.getIcon(),
            'description': this.getDescription(),
            'index': 1,
            'groupOrder': 5
        });

    }

    toggleAttachment(factor) {
            if (Apromore.BPMNEditor.Plugins.Attachment.toggle) {
                this.state = !this.state;
                Apromore.BPMNEditor.Plugins.Attachment.toggle();
                this.onToggle();
            }
    }

    onToggle() {
        let title = this.getDescription();
        let icon = this.getIcon();

        $('#' + this.btnId + ' button').prop('title', title);
        $('#' + this.btnId + ' button').css('background-image', 'url(' + icon + ')');
    }

    getDescription() {
        return this.state ? window.Apromore.I18N.Attachment.hideDesc : window.Apromore.I18N.Attachment.showDesc;
    }

    getIcon() {
        let iconUrl = this.state ? "images/ap/attachment-on.svg" : "images/ap/attachment-off.svg";
        return CONFIG.PATH + iconUrl;
    }
};
