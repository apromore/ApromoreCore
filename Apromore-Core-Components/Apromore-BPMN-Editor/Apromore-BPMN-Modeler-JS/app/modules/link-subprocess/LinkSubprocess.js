import $ from 'jquery';
import { getBusinessObject } from 'bpmn-js/lib/util/ModelUtil';
import { is } from 'bpmn-js/lib/util/ModelUtil';
import { isExpanded } from 'bpmn-js/lib/util/DiUtil';

const TYPE = 'linkSubprocessBtn';

export default function LinkSubprocess(eventBus, overlays) {

    function createLinkSubprocessBtn(element) {
        let id = element.id;
        let subprocess = $(`[data-element-id=${id}]`)[0];
        let subprocessBBox = subprocess.getBBox();
        let $overlay = $(LinkSubprocess.OVERLAY_HTML);

        $overlay.click(() => {
            if (Apromore.BPMNEditor.clickSubprocessBtn) {
                Apromore.BPMNEditor.clickSubprocessBtn(id);
            } else {
                console.log('click subprocess btn function not found');
            }
        });

        overlays.remove({ element, type: TYPE });
        // attach an overlay to an element
        overlays.add(element, TYPE, {
            position: {
                bottom: 21,
                left: subprocessBBox.width / 2 - 15
            },
            html: $overlay
        });

    }

    eventBus.on('shape.added', function(event) {
        var element = event.element;

        if (is(element, 'bpmn:SubProcess') && !isExpanded(element)) {
            defer(function() {
              createLinkSubprocessBtn(element);
            });
            return;
        }

    });

    eventBus.on('shape.changed', function(event) {
        var element = event.element;

        if (is(element, 'bpmn:SubProcess') && !isExpanded(element)) {
            defer(function() {
              createLinkSubprocessBtn(element);
            });
        } else {
            overlays.remove({ element, type: TYPE });
        }

    });

    //Send an event when a subprocess is deleted to delete any links it has in db
    eventBus.on('shape.remove', function(event) {
        var element = event.element;

        if (is(element, 'bpmn:SubProcess') && Apromore.BPMNEditor.deleteSubprocess) {
            let id = element.id;
            Apromore.BPMNEditor.deleteSubprocess(id);
        }

    });

}

LinkSubprocess.$inject = [ 'eventBus', 'overlays' ];

LinkSubprocess.OVERLAY_HTML =
    '<div class="sub-process-marker-overlay" style="width:15px;height:15px">' +
    '</div>';

function defer(fn) {
    setTimeout(fn, 0);
}
