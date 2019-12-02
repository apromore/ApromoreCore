Ap.uploadFileSelected = function (evt) {
    try {
        let files = evt.target.files;
        let file = files[0];
        if (!file || !file.name.endsWith('csv')) {
            return
        }

        let reader = new FileReader();
        reader.onload = function(ev) {
            let encoding = JSON.stringify(jschardet.detect(ev.target.result))
            localStorage.setItem('ap.csv-importer.encoding', encoding);
        };
        reader.readAsBinaryString(file.slice(0, 10000000));
    } catch (e) {
        // pass
    }
}

Ap.uploadBtnClick = function () {
    const CHANGE = 'change'
    let target = $('.ap-csv-import-chooser input[type="file"]')[0]

    target.removeEventListener(CHANGE, Ap.uploadFileSelected);
    target.addEventListener(CHANGE, Ap.uploadFileSelected);
}

Ap.encodingDetectSeed = -1

Ap.encodingDetect = function () {
    let wgt = zk.Widget.$('$ap-encoding-idx');
    // set dummy to trigger onChange
    wgt.setValue("" + Ap.encodingDetectSeed);
    wgt.fireOnChange({ toServer: true })
    Ap.encodingDetectSeed--;

    let encoding = localStorage.getItem('ap.csv-importer.encoding');
    if (encoding) {
        try {
            let { confidence: conf, encoding: enc } = JSON.parse(encoding)
            if (conf > 0.8) {
                let options = $('.ap-csv-importer .z-comboitem-text')
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
                    )

                enc = enc.toLowerCase()
                let option = options[enc]
                if (option) {
                    // Do it via proxy
                    // console.log('fire', option.index)
                    setTimeout(function () {
                        wgt.setValue("" + option.index);
                        wgt.fireOnChange({ toServer: true })
                    }, 200)
                }
            }
        } catch(e) {
            // pass
        }
    }
}