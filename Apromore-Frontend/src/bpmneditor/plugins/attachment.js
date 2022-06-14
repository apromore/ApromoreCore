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
 * The attachment plugin provide toggle attachment functionality
 *
 * @class Attachment
 * @param {Object} facade The editor facade for plugins.
 */
export default class Attachment {

    constructor (facade) {
        this.facade = facade;
        this.attachmentBtnId = 'ap-id-editor-attachment-btn';
        this.commentBtnId = 'ap-id-editor-comment-btn';
        this.attachmentState = true;
        this.commentState = true;

        this.facade.offer({
            'btnId': this.attachmentBtnId,
            'name': Apromore.I18N.Attachment.attachment,
            'functionality': this.toggleAttachment.bind(this),
            'icon': this.getAttachmentIcon(),
            'description': this.getAttachmentDescription(),
            'index': 2,
            'groupOrder': 3
        });

        this.facade.offer({
            'btnId': this.commentBtnId,
            'name': Apromore.I18N.Attachment.comment,
            'functionality': this.toggleComment.bind(this),
            'icon': this.getCommentIcon(),
            'description': this.getCommentDescription(),
            'index': 3,
            'groupOrder': 3
        });

    }

    toggleAttachment(factor) {
        if (Apromore.BPMNEditor.Plugins.Attachment.toggleAttachment) {
            this.attachmentState = !this.attachmentState;
            Apromore.BPMNEditor.Plugins.Attachment.toggleAttachment();
            this.onAttachmentToggle();
        }
    }

    onAttachmentToggle() {
        let title = this.getAttachmentDescription();
        let icon = this.getAttachmentIcon();

        $('#' + this.attachmentBtnId + ' button').prop('title', title);
        $('#' + this.attachmentBtnId + ' button').css('background-image', 'url(' + icon + ')');
    }

    toggleComment(factor) {
        if (Apromore.BPMNEditor.Plugins.Attachment.toggleComment) {
            this.commentState = !this.commentState;
            Apromore.BPMNEditor.Plugins.Attachment.toggleComment();
            this.onCommentToggle();
        }
    }

    onCommentToggle() {
        let title = this.getCommentDescription();
        let icon = this.getCommentIcon();

        $('#' + this.commentBtnId + ' button').prop('title', title);
        $('#' + this.commentBtnId + ' button').css('background-image', 'url(' + icon + ')');
    }

    getAttachmentDescription() {
        return this.attachmentState ? window.Apromore.I18N.Attachment.hideDesc : window.Apromore.I18N.Attachment.showDesc;
    }

    getAttachmentIcon() {
        let iconUrl = this.attachmentState ? "images/ap/attachment-on.svg" : "images/ap/attachment-off.svg";
        return CONFIG.PATH + iconUrl;
    }

    getCommentDescription() {
        return this.commentState ? window.Apromore.I18N.Attachment.hideComments : window.Apromore.I18N.Attachment.showComments;
    }

    getCommentIcon() {
        let iconUrl = this.commentState ? "images/ap/comments-on.svg" : "images/ap/comments-off.svg";
        return CONFIG.PATH + iconUrl;
    }
};
