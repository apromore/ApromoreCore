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
 * The publish model plugin offers functionality to publish a link which can be used
 * to view a model in view-only mode.
 *
 * @class PublishModel
 * @param {Object} facade The editor facade for plugins.
 */
export default class PublishModel {

    constructor(facade) {
        this.facade = facade;
        this.btnId = 'ap-id-editor-publish-model-btn';

        /* Register publish model */
        this.facade.offer({
            'btnId': this.btnId,
            'name': window.Apromore.I18N.Share.publish,
            'functionality': this.publishModel.bind(this),
            'group': window.Apromore.I18N.Share.group,
            'description': this.getDescription(facade.isPublished),
            'index': 2,
            'groupOrder': 5,
            'icon': this.getIcon(facade.isPublished)
        });
    };

    publishModel() {
        if (Apromore.BPMNEditor.Plugins.PublishModel.apromorePublishModel) {
            Apromore.BPMNEditor.Plugins.PublishModel.apromorePublishModel();
        }
    }

    /**
     * Notified by the EditorApp about the changes in the publish state.
     * Update the publish model button style based on the publish state.
     * @param isPublished: true if the model is currently published.
     */
    onPublishStateUpdate(isPublished) {
        let title = this.getDescription(isPublished);
        let icon = this.getIcon(isPublished);

        $('#' + this.btnId + ' button').prop('title', title);
        $('#' + this.btnId + ' button').css('background-image', 'url(' + icon + ')');
    }

    getDescription(isPublished) {
        return isPublished ? window.Apromore.I18N.Share.unpublishDesc : window.Apromore.I18N.Share.publishDesc;
    }

    getIcon(isPublished) {
        let iconUrl = isPublished ? "images/ap/link.svg" : "images/ap/unlink.svg";
        return CONFIG.PATH + iconUrl;
    }

};
