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
if (!Apromore.Plugins) {
    Apromore.Plugins = new Object();
}

// Logo's viewBox is "0 0 629.8 126.7"
const logo = '<g transform="translate(${xx yy}) scale(0.2 0.2)">' +
  '<path fill="#4E4E4F" d="M206.5,36.4v52.9h-11.4v-7.6c-4,5.6-10.3,9-18.5,9c-14.4,0-26.3-12.2-26.3-27.8S162.2,35,176.6,35 c8.3,0,14.5,3.4,18.5,8.9v-7.5H206.5z M195.1,62.8c0-9.8-7.2-16.9-16.7-16.9s-16.7,7.1-16.7,16.9s7.2,16.9,16.7,16.9 S195.1,72.7,195.1,62.8z"/>' +
  '<path fill="#4E4E4F" d="M276.1,62.8c0,15.8-12,27.8-26.4,27.8c-8.2,0-14.5-3.4-18.5-8.9v28.7h-11.4V36.4h11.4V44c4-5.6,10.3-9,18.5-9 C264.2,35,276.1,47.2,276.1,62.8z M264.7,62.8c0-9.8-7.2-16.9-16.7-16.9s-16.7,7.1-16.7,16.9s7.2,16.9,16.7,16.9 C257.5,79.8,264.7,72.7,264.7,62.8z"/>' +
  '<path fill="#4E4E4F" d="M314.2,35.4v12.4c-7.5-0.5-16.3,3-16.3,14.8v26.7h-11.4V36.4H298v8.9C301.1,38.1,307.6,35.4,314.2,35.4z"/>' +
  '<path fill="#4E4E4F" d="M317.3,62.8c-0.1-15.4,12.4-28,27.8-28c15.4-0.1,28,12.4,28,27.8c0.1,15.4-12.4,28-27.8,28c0,0-0.1,0-0.1,0 c-15.2,0.2-27.8-12-27.9-27.3C317.3,63.2,317.3,63,317.3,62.8z M361.8,62.8c0.3-9.1-6.8-16.8-15.9-17.1s-16.8,6.8-17.1,15.9 c0,0.4,0,0.8,0,1.1c0,9.6,7.2,16.7,16.5,16.7c9,0.1,16.4-7,16.5-16C361.8,63.3,361.8,63.1,361.8,62.8z"/>' +
  '<path fill="#4E4E4F" d="M461.3,56.5v32.8h-11.4V57.2c0-7.4-4-11.5-10.3-11.5c-6.8,0-11.5,4.3-11.5,14v29.6h-11.4V57.2 c0-7.4-3.7-11.5-10-11.5c-6.5,0-11.7,4.3-11.7,14v29.6h-11.4V36.4H395v6.3c3.4-5.2,8.6-7.7,15.1-7.7c6.9,0,12,3.1,15,8.4 c3.5-5.5,9.1-8.4,16.2-8.4C453.3,35,461.3,43.3,461.3,56.5z"/>' +
  '<path fill="#4E4E4F" d="M470.6,62.8c-0.1-15.4,12.4-28,27.8-28c15.4-0.1,28,12.4,28,27.8s-12.4,28-27.8,28c0,0-0.1,0-0.1,0 c-15.3,0.2-27.8-12-27.9-27.3C470.6,63.2,470.6,63,470.6,62.8z M515.1,62.8c0.3-9.1-6.8-16.8-15.9-17.1s-16.8,6.8-17.1,15.9 c0,0.4,0,0.8,0,1.1c-0.3,9,6.8,16.5,15.8,16.7c0.2,0,0.5,0,0.7,0C507.9,79.5,515.1,72.5,515.1,62.8z"/>' +
  '<path fill="#4E4E4F" d="M564.6,35.4v12.4c-7.5-0.5-16.3,3-16.3,14.8v26.7h-11.4V36.4h11.4v8.9C551.5,38.1,557.9,35.4,564.6,35.4z"/>' +
  '<path fill="#4E4E4F" d="M596.5,80.2c6.3,0,11.1-2.9,13.6-6.6l9.4,5.5c-4.9,7.2-12.8,11.5-23.2,11.5c-17.5,0-28.8-12-28.8-27.8 c0-15.7,11.4-27.8,27.9-27.8c15.8,0,26.6,12.7,26.6,27.9c0,1.6-0.2,3.2-0.4,4.8h-42.1C581.4,76,588.1,80.2,596.5,80.2z M610.6,58.4 c-1.6-9.1-8.3-13-15.1-13c-8.6,0-14.4,5.1-16,13H610.6z"/>' +
  '<path fill="#85C7E2" d="M64.5,108.2v-3.8c22.4,0,40.6-18.2,40.7-40.6h3.8C108.9,88.3,89,108.2,64.5,108.2z"/>' +
  '<circle fill="#F26424" cx="64.4" cy="108" r="13.9"/>' +
  '<path fill="#C7E1ED" d="M23.8,63.8h-3.8c0-24.5,19.9-44.4,44.4-44.4v3.8C42,23.2,23.9,41.4,23.8,63.8z"/>' +
  '<circle fill="#BC3C51" cx="64.3" cy="63.8" r="20"/>' +
  '<circle fill="#FAA624" cx="64.4" cy="19.6" r="13.9"/>' +
  '<circle fill="#85C7E2" cx="108.6" cy="63.8" r="13.9"/>' +
  '<circle fill="#C7E1ED" cx="20.2" cy="63.8" r="13.9"/>' +
  '<path fill="#F26424" d="M64.5,106.3L64.5,106.3c0-1-0.8-1.8-1.8-1.9c-14-0.6-26.6-8.4-33.5-20.6c-0.5-0.9-1.6-1.2-2.5-0.7l0,0 c-0.9,0.5-1.3,1.6-0.8,2.6c0,0,0,0,0,0c7.5,13.3,21.4,21.7,36.6,22.5C63.5,108.2,64.4,107.4,64.5,106.3 C64.5,106.3,64.5,106.3,64.5,106.3z"/>' +
  '<path fill="#FAA624" d="M64.5,21.3L64.5,21.3c0,1,0.8,1.8,1.8,1.9c13.5,0.6,25.8,7.9,32.9,19.5c0.5,0.8,1.6,1.1,2.5,0.7l0,0 c0.9-0.5,1.3-1.6,0.8-2.6c0,0,0-0.1-0.1-0.1C94.7,28,81.2,20.1,66.5,19.4C65.4,19.4,64.5,20.2,64.5,21.3 C64.5,21.2,64.5,21.3,64.5,21.3z"/>' +
'</g>'

Apromore.Plugins.File = Clazz.extend({

    facade: undefined,

    construct: function(facade){
        this.facade = facade;

        this.facade.offer({
            'name': window.Apromore.I18N.File.pdf,
            'functionality': this.exportPDF.bind(this),
            'group': window.Apromore.I18N.File.group,
            'icon': Apromore.PATH + "images/ap/export-pdf.svg",
            'description': window.Apromore.I18N.File.pdfDesc,
            'index': 5,
            'minShape': 0,
            'maxShape': 0
        });
    },

    exportPDF: function() {
        var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Please wait..."});
        myMask.show();

        var resource = location.href;

        // Get the serialized svg image source
        var svgClone = this.facade.getEditor().getSVG();
        //var svgDOM = DataManager.serialize(svgClone);

        // Expand margin and insert a logo
        var xy = null, width, height;

        // viewBox regex
        var viewBoxRegex = /(.*<svg.+?viewBox=")(.+?)(".+)/m;
        function viewBoxReplacer(match, p1, p2, p3) {
          xy = p2.split(" ").map(function (x) { return parseInt(x); })
          width = xy[2] + 40;
          height = xy[3]  + 60;
          return p1 + [xy[0] - 20, xy[1] - 40, width, height].join(' ') + p3;
        }

        // svg width and height regex
        var whRegex = /(.*<svg.+?width=")(.+?)(".+?height=")(.+?)(".+)/m;
        function whReplacer(match, p1, p2, p3, p4, p5) {
          return p1 + width + p3 + height + p5;
        }

        // Adjust viewBox, width/height and append logo
        try {
            svgClone = svgClone.replace(viewBoxRegex, viewBoxReplacer);
            if (xy) {
                svgClone = svgClone.replace(whRegex, whReplacer);
                svgClone = svgClone.replace(
                    '</svg>',
                    logo.replace('${xx yy}', (xy[0]) + " " + (xy[1] - 20)) + '</svg>'
                );
            }
        } catch (e) {
            // pass
        }

        // Send the svg to the server.
        new Ajax.Request(Apromore.CONFIG.PDF_EXPORT_URL, {
            method: 'POST',
            parameters: {
                resource: resource,
                data: svgClone,
                format: "pdf"
            },
            onSuccess: (function(request){
                myMask.hide();
                // Because the pdf may be opened in the same window as the
                // process, yet the process may not have been saved, we're
                // opening every other representation in a new window.
                // location.href = request.responseText
                window.open(request.responseText);
            }).bind(this),
            onFailure: (function(){
                myMask.hide();
                Ext.Msg.alert(window.Apromore.I18N.Apromore.title, window.Apromore.I18N.File.genPDFFailed);
            }).bind(this)
        });
    }

});

