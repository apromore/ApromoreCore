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
 * The publish model plugin offers functionality to publish a link which can be used
 * to view a model in view-only mode.
 *
 * @class PublishModel
 * @param {Object} facade The editor facade for plugins.
 */
export default class PublishModel {

    constructor(facade) {
        this.facade = facade;

        /* Register publish model */
        this.facade.offer({
            'btnId': 'ap-id-editor-publish-model-btn',
            'name': window.Apromore.I18N.Share.publish,
            'functionality': this.publishModel.bind(this),
            'group': window.Apromore.I18N.Share.group,
            'description': window.Apromore.I18N.Share.publishDesc,
            'index': 2,
            'groupOrder': 4,
            'icon': CONFIG.PATH + "images/ap/link.svg",
        });
    };

    publishModel() {
        if (Apromore.BPMNEditor.Plugins.PublishModel.apromorePublishModel) {
            Apromore.BPMNEditor.Plugins.PublishModel.apromorePublishModel();
        }
    }
};
