if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

/**
   Presents a dialog which allows selection of a list of process logs to animate..
   @class ORYX.Plugins.AnimationExtension
   @constructor Creates a new instance
   @extends ORYX.Plugins.AbstractPlugin
*/
ORYX.Plugins.AnimationExtension = ORYX.Plugins.AbstractPlugin.extend({

    construct: function(){
        arguments.callee.$.construct.apply(this, arguments);
                
        this.facade.offer({
            name: ORYX.I18N.AnimationExtension.name,
            functionality: this.showDialog.bind(this),
            group: ORYX.I18N.AnimationExtension.group,
            icon: ORYX.PATH + 'images/animation_extension.png',
            description: ORYX.I18N.AnimationExtension.desc,
            index: 2,
            minShape: 0,
            maxShape: 0
        });
    },
    
    showDialog: function(){

	var json = this.facade.getJSON();
        json = Ext.encode(json);

        var element = document.createElement("input");
        element.setAttribute("type", "hidden");
        element.setAttribute("value", json);
        element.setAttribute("id", "jsonForAnimation");
        document.body.appendChild(element);

	// Create the form to present
	var fp = new Ext.FormPanel({
       	    //renderTo: 'fi-form',
	    fileUpload: true,
	    width: 500,
	    frame: true,
	    autoHeight: true,
	    bodyStyle: 'padding: 10px 10px 0 10px;',
	    labelWidth: 50,
	    defaults: {
		anchor: '95%',
		allowBlank: false,
		msgTarget: 'side'
	    },
	    items: [new Ext.form.Hidden({
		id: 'json',
		name: 'json',
		value: element.getAttribute("value")
	    }), new Ext.form.FileUploadField({
		id: 'form-file',
		emptyText: 'Select a process log',
		fieldLabel: 'Log file',
		name: 'log',
		buttonCfg: {
		    text: '',
		    iconCls: 'upload-icon'
		}
	    }), new Ext.ux.ColorField({
		fieldLabel: 'Color',
		name: "color",
		value: "#7777FF"
	    })]
	});

	// Present the form to the user
        var dialog = new Ext.Window({
            autoCreate: true,
            layout: 'fit',
            plain: true,
            bodyStyle: 'padding:5px;',
            title: 'Select log files for animation',
            height: 350,
            width: 500,
            modal: true,
            fixedcenter: true,
            shadow: true,
            proxyDrag: true,
            resizable: true,
            items: [fp],
            buttons: [{
		text: 'Animate',
                handler: function(){
                    if(fp.getForm().isValid()){  // TODO: find a better war to prevent isValid from being undefined
                        fp.getForm().submit({
                            url: '/editor/editor/bpmnanimation',
                            params: {
                                newStatus: 'delivered'
                            },
                            waitMsg: 'Uploading process log(s)...',
                            success: function(fp, action) {

                                //var json =  this.facade.getJSON();
                                //json = Ext.encode(json);

                                var element = document.getElementById("jsonForAnimation");
                                if (!element) {
                                	element = document.createElement("input");
                                	element.setAttribute("type", "hidden");
                                	element.setAttribute("id", "jsonForAnimation");
                                	document.body.appendChild(element);
				}
                                element.setAttribute("value", json);

                                action.result.success = undefined;  // only had this property to humor Ext2JS's file uploader

                                var data = Ext.encode(action.result);
                                console.log("data");
                                console.dir(action.result);
                                console.log(data);

				var element2 = document.getElementById("jsonForAnimation2");
				if (!element2) {
                                	element2 = document.createElement("input");
                                	element2.setAttribute("type", "hidden");
                                	element2.setAttribute("id", "jsonForAnimation2");
                                	document.body.appendChild(element2);
				}
                               	element2.setAttribute("value", data);

                                window.open("/editor/editor/animation/animation.html", "_blank");
                            },
                            failure:  function(fp, action) {
                                switch (action.failureType) {
                                case Ext.form.Action.CLIENT_INVALID:
                                    Ext.Msg.alert("Failure", "Form fields may not be submitted with invalid values");
                                    break;
                                case Ext.form.Action.CONNECT_FAILURE:
                                    Ext.Msg.alert("Failure", "Ajax communication failed");
                                    break;
                                case Ext.form.Action.SERVER_INVALID:
                                    Ext.Msg.alert("Failure", "Server Error. Please read the server log."); //action.result.errors.errormsg);
                                    break;
                                default:
                                    Ext.Msg.alert("Failure", "Unknown server failure code: " + action.failureType);
                                }
                            }
                        });
                    }
                }
	    }, {
		text: 'Add log',
		handler: function(){
		    fp.add(new Ext.form.FileUploadField({
				emptyText: 'Select a process log',
				fieldLabel: 'Log file',
				name: 'log',
				buttonCfg: {
				    text: '',
				    iconCls: 'upload-icon'
				}
			})
		    );
		    fp.add(new Ext.ux.ColorField({
				fieldLabel: 'Color',
				name: "color",
				value: (dialog.defaultLogColors.length > 0) ? dialog.defaultLogColors.pop() : "black"
			})
		    );
		    fp.doLayout(false);
		}
            }, {
                text: 'Reset',
                handler: function(){
                    fp.getForm().reset();
                }
            }, {
                text: ORYX.I18N.JSONSupport.imp.btnClose,
                handler: function(){
                    dialog.close();
                }.bind(this)
            }]
        });

	dialog.defaultLogColors = ["#FF00FF" /* fuschia */, "#00CCFF" /* azure */, "FFCC00" /* amber */, "#00CC00" /* green */];
        dialog.show();
    },
});
