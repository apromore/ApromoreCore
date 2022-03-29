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
/**
 *  Utility functions for uploading log data. This include character encoding detection
 */
Ap.uploadFileSelected = function (evt) {
    let maxUploadSize;

    try {
       maxUploadSize= parseInt(jq(zk.$("$uploadButton")).data("max-upload-size"));
    } catch (e) {
       maxUploadSize = 100000000; // default
    }

    try {
        let files = evt.target.files;
        let file = files[0];

        if (file.size > maxUploadSize) {
            Ap.common.notify('File size exceeds the allowable limit', 'error');
            zAu.send(new zk.Event(zk.Widget.$('$uploadButton'), 'onSizeCheck', 1));
            return;
        } else {
            zAu.send(new zk.Event(zk.Widget.$('$uploadButton'), 'onSizeCheck', 0));
        }
        if (!file || !file.name.endsWith('csv')) {
            return;
        }
        let reader = new FileReader();
        reader.onload = function(ev) {
            let encoding = JSON.stringify(jschardet.detect(ev.target.result));
            localStorage.setItem('ap.csv-importer.encoding', encoding);
        };
        reader.readAsBinaryString(file.slice(0, 10000000)); // sampled for encoding detection
    } catch (e) {
        // pass
    }
};

Ap.uploadBtnClick = function () {
    const CHANGE = 'change';
    let target = $('.ap-importer-chooser input[type="file"]')[0];

    // ensure handler is called
    target.removeEventListener(CHANGE, Ap.uploadFileSelected);
    target.addEventListener(CHANGE, Ap.uploadFileSelected);
    zk.$('$okButtonImport').setDisabled(true);
};

Ap.encodingDetectSeed = -1;

Ap.encodingDetect = function () {
    let wgt = zk.Widget.$('$ap-encoding-idx');
    // set dummy to trigger onChange
    wgt.setValue("" + Ap.encodingDetectSeed);
    wgt.fireOnChange({ toServer: true });
    Ap.encodingDetectSeed--;

    let encoding = localStorage.getItem('ap.csv-importer.encoding');

    if (encoding) {
        try {
            let { confidence: conf, encoding: enc } = JSON.parse(encoding);
            if (conf > 0.8) {
                let options = $('.z-comboitem-text', jq(zk.$("$setEncoding")))
                    .toArray()
                    .reduce(
                        (a, x, index) => {
                            let opt = $(x).text()
                            a[opt.split(/[ \xa0]/)[0].toLowerCase()] = {
                                value: opt,
                                index
                            }
                            return a
                        },
                        {}
                    );
                enc = enc.toLowerCase();
                if (enc === 'ascii') {
                    enc = 'utf-8';
                }
                let option = options[enc];
                if (option) {
                    // Do it via proxy
                    setTimeout(function () {
                        Ap.common.notify('Encoding is detected as ' + option.value, 'info');
                        wgt.setValue("" + option.index);
                        wgt.fireOnChange({ toServer: true });
                    }, 200)
                    return; // success
                }
            }
        } catch(e) {
            // pass
        }
    }
    Ap.common.notify('Encoding cannot be automatically detected', 'error');
};