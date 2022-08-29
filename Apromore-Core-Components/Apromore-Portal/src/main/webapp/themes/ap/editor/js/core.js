let editorWrapper;
let disabledPlugins = [];
let forceSync = true; // force process save before invoking third party plugins
let idleTime = 0; // minutes counter
let idleInterval;
let MAX_IDLE_MINS = 30; // 30 minutes
let IDLE_CHECK_MS = 60000; // 1 min/60 seconds
let sourceXML = '${arg.bpmnXML}';

let initAutoSave = function() {
  setTimeout(function () {
    if (${arg.isNewProcess}) {
      Ap.common.notify('${arg.processName}' + ' (version 1.0) created', 'info');
    }
  }, 500);

  // Increment the idle time counter every minute.
  idleInterval = setInterval(timerIncrement, IDLE_CHECK_MS);

  // Reset the idle timer on mouse and key movement.
  jQuery(this).mousemove(function (e) {
    idleTime = 0;
  });
  jQuery(this).click(function (e) {
    idleTime = 0;
  });
  jQuery(this).keydown(function (e) {
    idleTime = 0;
  });
};

function timerIncrement() {
  idleTime++;

  // Send auto-save request
  if (!${arg.viewOnly}) {
    Apromore.BPMNEditor.saveDraft();
  } else {
    zk.Widget.$('$divKeepAlive').fire('onKeepAlive', null, { toServer: true });
  }

  if (idleTime >= MAX_IDLE_MINS) {
    // Timeout after MAX_IDLE_MINS minutes idle
    timeout();
  }
}

function timeout() {
  console.log('Logout user after ' + MAX_IDLE_MINS + ' minutes idle');
  clearInterval(idleInterval);
  window.location.href = '/logout';
}

if (!${arg.availableSimulateModelPlugin}) {
  disabledPlugins.push(Apromore.I18N.SimulationPanel.simulateModel);
}
if (!${arg.availablePublishModelPlugin}) {
  disabledPlugins.push(Apromore.I18N.Share.publish);
}

let isEditorSaving = false;
let linkedSubProcesses = {};

function setLinkedSubProcess(subProcessId, linkedProcessName) {
  linkedSubProcesses[subProcessId] = linkedProcessName;
  let subProcessLinkElement = document.getElementById('link-subprocess-view');
  if (subProcessLinkElement) {
    subProcessLinkElement.textContent = linkedProcessName;
    subProcessLinkElement.classList.add('link-subprocess-view-link');
  }
}

function removeLinkedSubProcess(subProcessId) {
  linkedSubProcesses[subProcessId] = undefined;
  let subProcessLinkElement = document.getElementById('link-subprocess-view');
  if (subProcessLinkElement) {
    subProcessLinkElement.textContent = 'None';
    subProcessLinkElement.classList.remove('link-subprocess-view-link');
  }
}

createEditor = async function (bpmnXML) {
  let config = {
    xml: bpmnXML,
    id: 'editorcanvas',
    fullscreen: true,
    useSimulationPanel: !${ arg.viewOnly },
    isPublished: ${ arg.isPublished },
    viewOnly: ${ arg.viewOnly },
    langTag: '${arg.langTag}',
    disabledButtons: disabledPlugins,
    username: '${arg.username}',
    processName: '${arg.processName}',
    zoneId: '${arg.zoneId}',
    defaultCurrency: '${arg.defaultCurrency}',
    currencyList: '${arg.currencyList}'
  };
  editorWrapper = new Apromore.BPMNEditor.EditorApp(config);
  Apromore.BPMNEditor.app = editorWrapper;
  return await editorWrapper.init(config);
}

Apromore.zoneId = '${arg.zoneId}';

Apromore.BPMNEditor.readXML = function (callback) {
  Apromore.BPMNEditor.app.getXML().then((xml) => {
    callback(xml);
  });
}

Apromore.BPMNEditor.afterSaveDraft = function (flowOnEvent) {
  console.log('After saving draft ...', flowOnEvent);
  // The dirty flag here is to indicate if the in-memory changes have been saved to draft.
  Apromore.BPMNEditor.app.getEditor().setDirty(false);
  setTimeout(function () {
    jq(".ap-bpmneditor-top-notice").hide();
  }, 1000);
  if (flowOnEvent && flowOnEvent.length) {
    if (flowOnEvent === 'onSave' || flowOnEvent === 'onSaveAs') {
      Apromore.BPMNEditor.save(flowOnEvent);
    } else {
      isEditorSaving = false;
      zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onCheckUnsaved', flowOnEvent));
    }
  } else {
    isEditorSaving = false;
  }
}

Apromore.BPMNEditor.saveDraft = function (flowOnEvent, force) {
  if (!force && isEditorSaving) {
    return;
  }
  isEditorSaving = true;
  flowOnEvent = flowOnEvent || '';
  console.log('Auto-saving draft ...', flowOnEvent);
  Apromore.BPMNEditor.readXML((xml) => {
    jq(".ap-bpmneditor-top-notice").show();
    zk.Widget.$('$divKeepAlive').fire(
      'onSaveDraft',
      { bpmnXML: xml, nativeType: '${arg.nativeType}', flowOnEvent },
      { toServer: true },
    );
  });
}

// Pre-check before invoking plugin
Apromore.BPMNEditor.beforeCheckUnsaved = function (flowOnEvent) {
  console.log('beforeCheckUnsaved ...', flowOnEvent);
  if (forceSync) {
    Apromore.BPMNEditor.readXML((xml) => {
      zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onForceSave', { xml, flowOnEvent }));
    });
  } else {
    if (Apromore.BPMNEditor.app.getEditor().isDirty()) {
      // Make sure the most recent changes saved to draft
      console.log('Dirty editor is flushed to draft');
      Apromore.BPMNEditor.saveDraft(flowOnEvent, true);
    } else {
      console.log('Editor and draft are in sync');
      zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onCheckUnsaved', flowOnEvent));
    }
  }
}

Apromore.BPMNEditor.afterCheckUnsaved = function (flowOnEvent) {
  console.log('afterCheckUnsaved - Proceed with flow on action ...', flowOnEvent);
  if (flowOnEvent === 'onShare' || flowOnEvent === 'onPublishModel') {
    zAu.send(new zk.Event(zk.Widget.$(jq("$win")), flowOnEvent));
  } else if (flowOnEvent === 'onSimulateModel' || flowOnEvent === 'onDownloadXML') {
    Apromore.BPMNEditor.readXML((xml) => {
      zAu.send(new zk.Event(zk.Widget.$(jq("$win")), flowOnEvent, xml));
    });
  }
}

Apromore.BPMNEditor.beforeSave = function (saveEvent) {
  // console.log('Before save ...', saveEvent);
  // Force save draft before actual save
  Apromore.BPMNEditor.saveDraft(saveEvent, true);
}

Apromore.BPMNEditor.save = function (saveEvent) {
  console.log('Auto-saving process model ...');
  Apromore.BPMNEditor.readXML((xml) => {
    zAu.send(new zk.Event(zk.Widget.$(jq("$win")), saveEvent, xml));
  });
}

Apromore.BPMNEditor.afterSave = function () {
  // console.log('After saving process model ...');
  isEditorSaving = false;
}

Apromore.BPMNEditor.initz = function (sourceXml) {
  console.log('sourceXml', sourceXml);
  createEditor(sourceXml).then((editor) => {
    Apromore.BPMNEditor.Plugins.Share.shareExt = function () {
      Apromore.BPMNEditor.beforeCheckUnsaved('onShare');
    };

    Apromore.BPMNEditor.Plugins.ApromoreSave.apromoreSaveAs = function (xml, svg) {
      Apromore.BPMNEditor.save('onSaveAs');
    };

    Apromore.BPMNEditor.Plugins.ApromoreSave.apromoreSave = function (xml, svg) {
      Apromore.BPMNEditor.save('onSave');
    };

    Apromore.BPMNEditor.Plugins.SimulateModel.apromoreSimulateModel = function (xml) {
      Apromore.BPMNEditor.beforeCheckUnsaved('onSimulateModel');
    };

    Apromore.BPMNEditor.Plugins.PublishModel.apromorePublishModel = function () {
      Apromore.BPMNEditor.beforeCheckUnsaved('onPublishModel');
    };

    Apromore.BPMNEditor.Plugins.Attachment.toggleAttachment = function () {
      jq(".djs-overlay-container").toggleClass('aux-disabled');
    };

    Apromore.BPMNEditor.Plugins.Attachment.toggleComment = function () {
      jq(".djs-overlay-container").toggleClass('comment-disabled');
    };

    Apromore.BPMNEditor.clickSubprocessBtn = function (elementId) {
      zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onClickSubprocessBtn', elementId));
    };

    Apromore.BPMNEditor.viewSubprocess = function (elementId) {
      zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onViewSubprocess', elementId));
    };

    Apromore.BPMNEditor.linkSubprocess = function (elementId) {
      //requires manual saving since this can also be called in view mode
      zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onLinkSubprocess', elementId));
    };

    Apromore.BPMNEditor.unlinkSubprocess = function (elementId) {
      zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onUnlinkSubprocess', elementId));
      removeLinkedSubProcess(elementId);
    };

    Apromore.BPMNEditor.deleteSubprocess = function (elementId) {
      zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onDeleteSubprocess', elementId));
    };

    Apromore.BPMNEditor.Plugins.Export.exportXML = function (xml) {
      Apromore.BPMNEditor.beforeCheckUnsaved('onDownloadXML');
    };

    Apromore.BPMNEditor.updateFontSize = function (newFontSize) {
      zk.Widget.$('$slider').setCurpos(newFontSize);
    };

    Apromore.BPMNEditor.Plugins.FontChange.change = function () {
      zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onChangeFont'));
    };

    Apromore.BPMNEditor.updateProcessName = function (processName) {
      Apromore.BPMNEditor.app.processName = processName;
    };

    Apromore.BPMNEditor.Plugins.ShapeChange.change = function () {
      Apromore.BPMNEditor.app.editor.initShapeSize();
    };
    // window.setTimeout(function() {
    //   console.log($$("div.Apromore_Editor div.bjs-container div.djs-container svg g.viewport")[0]);
    //   console.log(editorWrapper.getCanvas()._editor.getDefinitions());
    // }, 1000);

    document.title = '${labels.brand_shortName} - ${labels.bpmnEditor_title_text}';
    initAutoSave();
  });
};

function changeFontSize(newFontSize) {
  if (newFontSize < 8) {
    newFontSize = 8;
  }
  if (newFontSize > 72) {
    newFontSize = 72;
  }
  zk.Widget.$('$slider').setCurpos(newFontSize);
  Apromore.BPMNEditor.app.editor.changeFontSize(newFontSize);
}

Apromore.BPMNEditor.qbpProcessMaxLimit = (${ arg.qbpProcessMaxLimit }) ? ${ arg.qbpProcessMaxLimit } : 25000;
