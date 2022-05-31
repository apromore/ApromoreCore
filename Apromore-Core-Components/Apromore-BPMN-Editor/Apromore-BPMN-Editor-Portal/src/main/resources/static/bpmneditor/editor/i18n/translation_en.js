/*
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
if(!Apromore) var Apromore = {};

if(!Apromore.I18N) Apromore.I18N = {};

Apromore.I18N.Language = "en"; //Pattern <ISO language code> in lower case!

if(!Apromore.I18N.Apromore) Apromore.I18N.Apromore = {};

Apromore.I18N.Apromore.title		= "Apromore";
Apromore.I18N.Apromore.noBackendDefined	= "Caution! \nNo Backend defined.\n The requested model cannot be loaded. Try to load a configuration with a save plugin.";
Apromore.I18N.Apromore.pleaseWait 	= "Please wait while loading...";
Apromore.I18N.Apromore.notLoggedOn = "Not logged on";
Apromore.I18N.Apromore.editorOpenTimeout = "The editor does not seem to be started yet. Please check, whether you have a popup blocker enabled and disable it or allow popups for this site. We will never display any commercials on this site.";

if(!Apromore.I18N.AddDocker) Apromore.I18N.AddDocker = {};

Apromore.I18N.AddDocker.group = "Docker";
Apromore.I18N.AddDocker.add = "Add Docker";
Apromore.I18N.AddDocker.addDesc = "Add a Docker to an edge, by clicking on it";
Apromore.I18N.AddDocker.del = "Delete Docker";
Apromore.I18N.AddDocker.delDesc = "Delete a Docker";

if(!Apromore.I18N.Arrangement) Apromore.I18N.Arrangement = {};

Apromore.I18N.Arrangement.groupZ = "Z-Order";
Apromore.I18N.Arrangement.btf = "Bring To Front";
Apromore.I18N.Arrangement.btfDesc = "Bring to Front";
Apromore.I18N.Arrangement.btb = "Bring To Back";
Apromore.I18N.Arrangement.btbDesc = "Bring To Back";
Apromore.I18N.Arrangement.bf = "Bring Forward";
Apromore.I18N.Arrangement.bfDesc = "Bring Forward";
Apromore.I18N.Arrangement.bb = "Bring Backward";
Apromore.I18N.Arrangement.bbDesc = "Bring Backward";
Apromore.I18N.Arrangement.groupA = "Alignment";
Apromore.I18N.Arrangement.ab = "Alignment Bottom";
Apromore.I18N.Arrangement.abDesc = "Bottom";
Apromore.I18N.Arrangement.am = "Alignment Middle";
Apromore.I18N.Arrangement.amDesc = "Middle";
Apromore.I18N.Arrangement.at = "Alignment Top";
Apromore.I18N.Arrangement.atDesc = "Top";
Apromore.I18N.Arrangement.al = "Alignment Left";
Apromore.I18N.Arrangement.alDesc = "Left";
Apromore.I18N.Arrangement.ac = "Alignment Center";
Apromore.I18N.Arrangement.acDesc = "Center";
Apromore.I18N.Arrangement.ar = "Alignment Right";
Apromore.I18N.Arrangement.arDesc = "Right";
Apromore.I18N.Arrangement.as = "Alignment Same Size";
Apromore.I18N.Arrangement.asDesc = "Same Size";

if(!Apromore.I18N.Edit) Apromore.I18N.Edit = {};

Apromore.I18N.Edit.group = "Edit";
Apromore.I18N.Edit.cut = "Cut";
Apromore.I18N.Edit.cutDesc = "Cuts the selection into an Apromore clipboard";
Apromore.I18N.Edit.copy = "Copy";
Apromore.I18N.Edit.copyDesc = "Copies the selection into an Apromore clipboard";
Apromore.I18N.Edit.paste = "Paste";
Apromore.I18N.Edit.pasteDesc = "Pastes the Apromore clipboard to the canvas";
Apromore.I18N.Edit.del = "Delete";
Apromore.I18N.Edit.delDesc = "Deletes all selected shapes";

if(!Apromore.I18N.Save) Apromore.I18N.Save = {};

Apromore.I18N.Save.group = "File";
Apromore.I18N.Save.save = "Save";
Apromore.I18N.Save.saveDesc = "Save";
Apromore.I18N.Save.saveAs = "Save As...";
Apromore.I18N.Save.saveAsDesc = "Save As...";
Apromore.I18N.Save.unsavedData = "There are unsaved data, please save before you leave, otherwise your changes get lost!";
Apromore.I18N.Save.newProcess = "New Process";
Apromore.I18N.Save.saveAsTitle = "Save as...";
Apromore.I18N.Save.saveBtn = "Save";
Apromore.I18N.Save.close = "Close";
Apromore.I18N.Save.savedAs = "Saved As";
Apromore.I18N.Save.saved = "Saved!";
Apromore.I18N.Save.failed = "Saving failed.";
Apromore.I18N.Save.noRights = "You have no rights to save changes.";
Apromore.I18N.Save.saving = "Saving";
Apromore.I18N.Save.saveAsHint = "The process diagram is stored under:";

if(!Apromore.I18N.Bimp) Apromore.I18N.Bimp = {};

Apromore.I18N.Bimp.group = "BIMP";
Apromore.I18N.Bimp.upload = "upload";
Apromore.I18N.Bimp.uploadDesc = "Upload file to BIMP Simulator";

if(!Apromore.I18N.File) Apromore.I18N.File = {};

Apromore.I18N.File.group = "File";
Apromore.I18N.File.print = "Print";
Apromore.I18N.File.printDesc = "Print current model";
Apromore.I18N.File.pdf = "Download as PDF";
Apromore.I18N.File.pdfDesc = "Download as PDF";
Apromore.I18N.File.info = "Info";
Apromore.I18N.File.infoDesc = "Info";
Apromore.I18N.File.genPDF = "Generating PDF";
Apromore.I18N.File.genPDFFailed = "Generating PDF failed.";
Apromore.I18N.File.printTitle = "Print";
Apromore.I18N.File.printMsg = "We are currently experiencing problems with the printing function. We recommend using the PDF Export to print the diagram. Do you really want to continue printing?";

Apromore.I18N.File.svg = "Download as SVG";
Apromore.I18N.File.svgDesc = "Download as SVG";
Apromore.I18N.File.bpmn = "Download as BPMN";
Apromore.I18N.File.bpmnDesc = "Download as BPMN";

if(!Apromore.I18N.Grouping) Apromore.I18N.Grouping = {};

Apromore.I18N.Grouping.grouping = "Grouping";
Apromore.I18N.Grouping.group = "Group";
Apromore.I18N.Grouping.groupDesc = "Groups all selected shapes";
Apromore.I18N.Grouping.ungroup = "Ungroup";
Apromore.I18N.Grouping.ungroupDesc = "Deletes the group of all selected Shapes";

if(!Apromore.I18N.Loading) Apromore.I18N.Loading = {};

Apromore.I18N.Loading.waiting ="Please wait...";

if(!Apromore.I18N.PropertyWindow) Apromore.I18N.PropertyWindow = {};

Apromore.I18N.PropertyWindow.name = "Name";
Apromore.I18N.PropertyWindow.value = "Value";
Apromore.I18N.PropertyWindow.selected = "selected";
Apromore.I18N.PropertyWindow.clickIcon = "Click Icon";
Apromore.I18N.PropertyWindow.add = "Add";
Apromore.I18N.PropertyWindow.rem = "Remove";
Apromore.I18N.PropertyWindow.complex = "Editor for a Complex Type";
Apromore.I18N.PropertyWindow.text = "Editor for a Text Type";
Apromore.I18N.PropertyWindow.ok = "Ok";
Apromore.I18N.PropertyWindow.cancel = "Cancel";
Apromore.I18N.PropertyWindow.dateFormat = "m/d/y";

if(!Apromore.I18N.ShapeMenuPlugin) Apromore.I18N.ShapeMenuPlugin = {};

Apromore.I18N.ShapeMenuPlugin.drag = "Drag";
Apromore.I18N.ShapeMenuPlugin.clickDrag = "Click or drag";
Apromore.I18N.ShapeMenuPlugin.morphMsg = "Morph shape";

if(!Apromore.I18N.SyntaxChecker) Apromore.I18N.SyntaxChecker = {};

Apromore.I18N.SyntaxChecker.group = "Verification";
Apromore.I18N.SyntaxChecker.name = "Syntax Checker";
Apromore.I18N.SyntaxChecker.desc = "Check Syntax";
Apromore.I18N.SyntaxChecker.noErrors = "There are no syntax errors.";
Apromore.I18N.SyntaxChecker.invalid = "Invalid answer from server.";
Apromore.I18N.SyntaxChecker.checkingMessage = "Checking ...";

if(!Apromore.I18N.ConfigurationExtension) Apromore.I18N.ConfigurationExtension = {};

Apromore.I18N.ConfigurationExtension.name = "Configuration";
Apromore.I18N.ConfigurationExtension.group = "Configuration";
Apromore.I18N.ConfigurationExtension.desc = "Show/Hide Variants";

if(!Apromore.I18N.SelectionExtension) Apromore.I18N.SelectionExtension = {};

Apromore.I18N.SelectionExtension.name = "Selection";
Apromore.I18N.SelectionExtension.group = "Configuration";
Apromore.I18N.SelectionExtension.desc = "Make selection...";

if(!Apromore.I18N.AnimationExtension) Apromore.I18N.AnimationExtension = {};

Apromore.I18N.AnimationExtension.name = "Animation";
Apromore.I18N.AnimationExtension.group = "Configuration";
Apromore.I18N.AnimationExtension.desc = "Animate logs...";

if(!Apromore.I18N.Undo) Apromore.I18N.Undo = {};

Apromore.I18N.Undo.group = "Undo";
Apromore.I18N.Undo.undo = "Undo";
Apromore.I18N.Undo.undoDesc = "Undo the last action";
Apromore.I18N.Undo.redo = "Redo";
Apromore.I18N.Undo.redoDesc = "Redo the last undone action";

if(!Apromore.I18N.View) Apromore.I18N.View = {};

Apromore.I18N.View.group = "Zoom";
Apromore.I18N.View.zoomIn = "Zoom in";
Apromore.I18N.View.zoomInDesc = "Zoom in";
Apromore.I18N.View.zoomOut = "Zoom out";
Apromore.I18N.View.zoomOutDesc = "Zoom out";
Apromore.I18N.View.zoomStandard = "Zoom Standard";
Apromore.I18N.View.zoomStandardDesc = "Zoom to the standard level";
Apromore.I18N.View.zoomFitToModel = "Fit to screen";
Apromore.I18N.View.zoomFitToModelDesc = "Fit to screen";

if(!Apromore.I18N.Share) Apromore.I18N.Share = {};

Apromore.I18N.Share.group = "Share";
Apromore.I18N.Share.share = "Share";
Apromore.I18N.Share.shareDesc = "Share the model";
Apromore.I18N.Share.publish = "Publish";
Apromore.I18N.Share.publishDesc = "Publish model";
Apromore.I18N.Share.unpublish = "Unpublish";
Apromore.I18N.Share.unpublishDesc = "Unpublish model";

if(!Apromore.I18N.SimulationPanel) Apromore.I18N.SimulationPanel = {};

Apromore.I18N.SimulationPanel.group = "Simulation";
Apromore.I18N.SimulationPanel.toggleSimulationDrawer = "Toggle simulation panel";
Apromore.I18N.SimulationPanel.toggleSimulationDrawerDesc = "Toggle Properties panel";
Apromore.I18N.SimulationPanel.simulateModel = "Simulate model";
Apromore.I18N.SimulationPanel.simulateModelDesc = "Simulate model";

/** New Language Properties: 08.12.2008 */

Apromore.I18N.PropertyWindow.title = "Properties";

if(!Apromore.I18N.ShapeRepository) Apromore.I18N.ShapeRepository = {};
Apromore.I18N.ShapeRepository.title = "Shape Repository";

Apromore.I18N.Save.dialogDesciption = "Please enter a name, a description and a comment.";
Apromore.I18N.Save.dialogLabelTitle = "Title";
Apromore.I18N.Save.dialogLabelDesc = "Description";
Apromore.I18N.Save.dialogLabelType = "Type";
Apromore.I18N.Save.dialogLabelComment = "Revision comment";

Ext.MessageBox.buttonText.yes = "Yes";
Ext.MessageBox.buttonText.no = "No";
Ext.MessageBox.buttonText.cancel = "Cancel";
Ext.MessageBox.buttonText.ok = "OK";

if(!Apromore.I18N.Perspective) Apromore.I18N.Perspective = {};
Apromore.I18N.Perspective.no = "No Perspective"
Apromore.I18N.Perspective.noTip = "Unload the current perspective"

/** New Language Properties: 09.05.2009 */
if(!Apromore.I18N.JSONImport) Apromore.I18N.JSONImport = {};

Apromore.I18N.JSONImport.title = "JSON Import";
Apromore.I18N.JSONImport.wrongSS = "The stencil set of the imported file ({0}) does not match to the loaded stencil set ({1})."

/** New Language Properties: 15.05.2009*/
if(!Apromore.I18N.SyntaxChecker.BPMN) Apromore.I18N.SyntaxChecker.BPMN={};
Apromore.I18N.SyntaxChecker.BPMN_NO_SOURCE = "An edge must have a source.";
Apromore.I18N.SyntaxChecker.BPMN_NO_TARGET = "An edge must have a target.";
Apromore.I18N.SyntaxChecker.BPMN_DIFFERENT_PROCESS = "Source and target node must be contained in the same process.";
Apromore.I18N.SyntaxChecker.BPMN_SAME_PROCESS = "Source and target node must be contained in different pools.";
Apromore.I18N.SyntaxChecker.BPMN_FLOWOBJECT_NOT_CONTAINED_IN_PROCESS = "A flow object must be contained in a process.";
Apromore.I18N.SyntaxChecker.BPMN_ENDEVENT_WITHOUT_INCOMING_CONTROL_FLOW = "An end event must have an incoming sequence flow.";
Apromore.I18N.SyntaxChecker.BPMN_STARTEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "A start event must have an outgoing sequence flow.";
Apromore.I18N.SyntaxChecker.BPMN_STARTEVENT_WITH_INCOMING_CONTROL_FLOW = "Start events must not have incoming sequence flows.";
Apromore.I18N.SyntaxChecker.BPMN_ATTACHEDINTERMEDIATEEVENT_WITH_INCOMING_CONTROL_FLOW = "Attached intermediate events must not have incoming sequence flows.";
Apromore.I18N.SyntaxChecker.BPMN_ATTACHEDINTERMEDIATEEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "Attached intermediate events must have exactly one outgoing sequence flow.";
Apromore.I18N.SyntaxChecker.BPMN_ENDEVENT_WITH_OUTGOING_CONTROL_FLOW = "End events must not have outgoing sequence flows.";
Apromore.I18N.SyntaxChecker.BPMN_EVENTBASEDGATEWAY_BADCONTINUATION = "Event-based gateways must not be followed by gateways or subprocesses.";
Apromore.I18N.SyntaxChecker.BPMN_NODE_NOT_ALLOWED = "Node type is not allowed.";

if(!Apromore.I18N.SyntaxChecker.IBPMN) Apromore.I18N.SyntaxChecker.IBPMN={};
Apromore.I18N.SyntaxChecker.IBPMN_NO_ROLE_SET = "Interactions must have a sender and a receiver role set";
Apromore.I18N.SyntaxChecker.IBPMN_NO_INCOMING_SEQFLOW = "This node must have incoming sequence flow.";
Apromore.I18N.SyntaxChecker.IBPMN_NO_OUTGOING_SEQFLOW = "This node must have outgoing sequence flow.";

if(!Apromore.I18N.SyntaxChecker.InteractionNet) Apromore.I18N.SyntaxChecker.InteractionNet={};
Apromore.I18N.SyntaxChecker.InteractionNet_SENDER_NOT_SET = "Sender not set";
Apromore.I18N.SyntaxChecker.InteractionNet_RECEIVER_NOT_SET = "Receiver not set";
Apromore.I18N.SyntaxChecker.InteractionNet_MESSAGETYPE_NOT_SET = "Message type not set";
Apromore.I18N.SyntaxChecker.InteractionNet_ROLE_NOT_SET = "Role not set";

if(!Apromore.I18N.SyntaxChecker.EPC) Apromore.I18N.SyntaxChecker.EPC={};
Apromore.I18N.SyntaxChecker.EPC_NO_SOURCE = "Each edge must have a source.";
Apromore.I18N.SyntaxChecker.EPC_NO_TARGET = "Each edge must have a target.";
Apromore.I18N.SyntaxChecker.EPC_NOT_CONNECTED = "Node must be connected with edges.";
Apromore.I18N.SyntaxChecker.EPC_NOT_CONNECTED_2 = "Node must be connected with more edges.";
Apromore.I18N.SyntaxChecker.EPC_TOO_MANY_EDGES = "Node has too many connected edges.";
Apromore.I18N.SyntaxChecker.EPC_NO_CORRECT_CONNECTOR = "Node is no correct connector.";
Apromore.I18N.SyntaxChecker.EPC_MANY_STARTS = "There must be only one start event.";
Apromore.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_OR = "There must be no functions after a splitting OR/XOR.";
Apromore.I18N.SyntaxChecker.EPC_PI_AFTER_OR = "There must be no process interface after a splitting OR/XOR.";
Apromore.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_FUNCTION =  "There must be no function after a function.";
Apromore.I18N.SyntaxChecker.EPC_EVENT_AFTER_EVENT =  "There must be no event after an event.";
Apromore.I18N.SyntaxChecker.EPC_PI_AFTER_FUNCTION =  "There must be no process interface after a function.";
Apromore.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_PI =  "There must be no function after a process interface.";
Apromore.I18N.SyntaxChecker.EPC_SOURCE_EQUALS_TARGET = "Edge must connect two distinct nodes."

if(!Apromore.I18N.SyntaxChecker.PetriNet) Apromore.I18N.SyntaxChecker.PetriNet={};
Apromore.I18N.SyntaxChecker.PetriNet_NOT_BIPARTITE = "The graph is not bipartite";
Apromore.I18N.SyntaxChecker.PetriNet_NO_LABEL = "Label not set for a labeled transition";
Apromore.I18N.SyntaxChecker.PetriNet_NO_ID = "There is a node without id";
Apromore.I18N.SyntaxChecker.PetriNet_SAME_SOURCE_AND_TARGET = "Two flow relationships have the same source and target";
Apromore.I18N.SyntaxChecker.PetriNet_NODE_NOT_SET = "A node is not set for a flowrelationship";

/** New Language Properties: 02.06.2009*/
Apromore.I18N.Edge = "Edge";
Apromore.I18N.Node = "Node";

/** New Language Properties: 03.06.2009*/
Apromore.I18N.SyntaxChecker.notice = "Move the mouse over a red cross icon to see the error message.";

/** New Language Properties: 05.06.2009*/
if(!Apromore.I18N.RESIZE) Apromore.I18N.RESIZE = {};
Apromore.I18N.RESIZE.tipGrow = "Increase canvas size:";
Apromore.I18N.RESIZE.tipShrink = "Decrease canvas size:";
Apromore.I18N.RESIZE.N = "Top";
Apromore.I18N.RESIZE.W = "Left";
Apromore.I18N.RESIZE.S ="Down";
Apromore.I18N.RESIZE.E ="Right";

/** New Language Properties: 15.07.2009*/
if(!Apromore.I18N.Layouting) Apromore.I18N.Layouting ={};
Apromore.I18N.Layouting.doing = "Layouting...";

/** New Language Properties: 18.08.2009*/
Apromore.I18N.SyntaxChecker.MULT_ERRORS = "Multiple Errors";

/** New Language Properties: 08.09.2009*/
if(!Apromore.I18N.PropertyWindow) Apromore.I18N.PropertyWindow = {};
Apromore.I18N.PropertyWindow.oftenUsed = "Often used";
Apromore.I18N.PropertyWindow.moreProps = "More Properties";

/** New Language Properties 01.10.2009 */
if(!Apromore.I18N.SyntaxChecker.BPMN2) Apromore.I18N.SyntaxChecker.BPMN2 = {};

Apromore.I18N.SyntaxChecker.BPMN2_DATA_INPUT_WITH_INCOMING_DATA_ASSOCIATION = "A Data Input must not have any incoming Data Associations.";
Apromore.I18N.SyntaxChecker.BPMN2_DATA_OUTPUT_WITH_OUTGOING_DATA_ASSOCIATION = "A Data Output must not have any outgoing Data Associations.";
Apromore.I18N.SyntaxChecker.BPMN2_EVENT_BASED_TARGET_WITH_TOO_MANY_INCOMING_SEQUENCE_FLOWS = "Targets of Event-based Gateways may only have one incoming Sequence Flow.";

/** New Language Properties 02.10.2009 */
Apromore.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WITH_TOO_LESS_OUTGOING_SEQUENCE_FLOWS = "An Event-based Gateway must have two or more outgoing Sequence Flows.";
Apromore.I18N.SyntaxChecker.BPMN2_EVENT_BASED_EVENT_TARGET_CONTRADICTION = "If Message Intermediate Events are used in the configuration, then Receive Tasks must not be used and vice versa.";
Apromore.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WRONG_TRIGGER = "Only the following Intermediate Event triggers are valid: Message, Signal, Timer, Conditional and Multiple.";
Apromore.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WRONG_CONDITION_EXPRESSION = "The outgoing Sequence Flows of the Event Gateway must not have a condition expression.";
Apromore.I18N.SyntaxChecker.BPMN2_EVENT_BASED_NOT_INSTANTIATING = "The Gateway does not meet the conditions to instantiate the process. Please use a start event or an instantiating attribute for the gateway.";

/** New Language Properties 05.10.2009 */
Apromore.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_MIXED_FAILURE = "The Gateway must have both multiple incoming and outgoing Sequence Flows.";
Apromore.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_CONVERGING_FAILURE = "The Gateway must have multiple incoming but most NOT have multiple outgoing Sequence Flows.";
Apromore.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_DIVERGING_FAILURE = "The Gateway must NOT have multiple incoming but must have multiple outgoing Sequence Flows.";
Apromore.I18N.SyntaxChecker.BPMN2_GATEWAY_WITH_NO_OUTGOING_SEQUENCE_FLOW = "A Gateway must have a minimum of one outgoing Sequence Flow.";
Apromore.I18N.SyntaxChecker.BPMN2_RECEIVE_TASK_WITH_ATTACHED_EVENT = "Receive Tasks used in Event Gateway configurations must not have any attached Intermediate Events.";
Apromore.I18N.SyntaxChecker.BPMN2_EVENT_SUBPROCESS_BAD_CONNECTION = "An Event Subprocess must not have any incoming or outgoing Sequence Flow.";

/** New Language Properties 13.10.2009 */
Apromore.I18N.SyntaxChecker.BPMN_MESSAGE_FLOW_NOT_CONNECTED = "At least one side of the Message Flow has to be connected.";

/** New Language Properties 24.11.2009 */
Apromore.I18N.SyntaxChecker.BPMN2_TOO_MANY_INITIATING_MESSAGES = "A Choreography Activity may only have one initiating message.";
Apromore.I18N.SyntaxChecker.BPMN_MESSAGE_FLOW_NOT_ALLOWED = "A Message Flow is not allowed here.";

/** New Language Properties 27.11.2009 */
Apromore.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WITH_TOO_LESS_INCOMING_SEQUENCE_FLOWS = "An Event-based Gateway that is not instantiating must have a minimum of one incoming Sequence Flow.";
Apromore.I18N.SyntaxChecker.BPMN2_TOO_FEW_INITIATING_PARTICIPANTS = "A Choreography Activity must have one initiating Participant (white).";
Apromore.I18N.SyntaxChecker.BPMN2_TOO_MANY_INITIATING_PARTICIPANTS = "A Choreography Acitivity must not have more than one initiating Participant (white)."

Apromore.I18N.SyntaxChecker.COMMUNICATION_AT_LEAST_TWO_PARTICIPANTS = "The communication must be connected to at least two participants.";
Apromore.I18N.SyntaxChecker.MESSAGEFLOW_START_MUST_BE_PARTICIPANT = "The message flow's source must be a participant.";
Apromore.I18N.SyntaxChecker.MESSAGEFLOW_END_MUST_BE_PARTICIPANT = "The message flow's target must be a participant.";
Apromore.I18N.SyntaxChecker.CONV_LINK_CANNOT_CONNECT_CONV_NODES = "The conversation link must connect a communication or sub conversation node with a participant.";

// Migrated Apromore specific configuration

Apromore.I18N.PropertyWindow.dateFormat = "d/m/y";

Apromore.I18N.View.East = "Properties";
Apromore.I18N.View.West = "Modeling Elements";

Apromore.I18N.Apromore.title	= "Apromore.";
Apromore.I18N.Apromore.pleaseWait = "Please wait while the Apromore. Process Editor is loading...";
Apromore.I18N.Edit.cutDesc = "Cuts the selection into the clipboard";
Apromore.I18N.Edit.copyDesc = "Copies the selection into the clipboard";
Apromore.I18N.Edit.pasteDesc = "Pastes the clipboard to the canvas";
Apromore.I18N.Save.pleaseWait = "Please wait<br/>while saving...";

Apromore.I18N.Save.saveAs = "Save a copy...";
Apromore.I18N.Save.saveAsDesc = "Save a copy...";
Apromore.I18N.Save.saveAsTitle = "Save a copy...";
Apromore.I18N.Save.savedAs = "Copy saved";
Apromore.I18N.Save.savedDescription = "The process diagram is stored under";
Apromore.I18N.Save.notAuthorized = "You are currently not logged in. Please <a href='/p/login' target='_blank'>log in</a> in a new window so that you can save the current diagram."
Apromore.I18N.Save.transAborted = "The saving request took too long. You may use a faster internet connection. If you use wireless LAN, please check the strength of your connection.";
Apromore.I18N.Save.noRights = "You do not have the required rights to store that model. Please check in the <a href='/p/explorer' target='_blank'>Apromore. Explorer</a>, if you still have the rights to write in the target directory.";
Apromore.I18N.Save.comFailed = "The communication with the Apromore. server failed. Please check your internet connection. If the problem resides, please contact the Apromore. Support via the envelope symbol in the toolbar.";
Apromore.I18N.Save.failed = "Something went wrong when trying to save your diagram. Please try again. If the problem resides, please contact the Apromore. Support via the envelope symbol in the toolbar.";
Apromore.I18N.Save.exception = "Some exceptions are raised while trying to save your diagram. Please try again. If the problem resides, please contact the Apromore. Support via the envelope symbol in the toolbar.";
Apromore.I18N.Save.retrieveData = "Please wait, data is retrieving.";

/** New Language Properties: 10.6.09*/
if(!Apromore.I18N.ShapeMenuPlugin) Apromore.I18N.ShapeMenuPlugin = {};
Apromore.I18N.ShapeMenuPlugin.morphMsg = "Transform shape";
Apromore.I18N.ShapeMenuPlugin.morphWarningTitleMsg = "Transform shape";
Apromore.I18N.ShapeMenuPlugin.morphWarningMsg = "There are child shape which can not be contained in the transformed element.<br/>Do you want to transform anyway?";

/** New Language Properties: 08.09.2009*/
if(!Apromore.I18N.PropertyWindow) Apromore.I18N.PropertyWindow = {};
Apromore.I18N.PropertyWindow.oftenUsed = "Main properties";
Apromore.I18N.PropertyWindow.moreProps = "More properties";

Apromore.I18N.PropertyWindow.btnOpen = "Open";
Apromore.I18N.PropertyWindow.btnRemove = "Remove";
Apromore.I18N.PropertyWindow.btnEdit = "Edit";
Apromore.I18N.PropertyWindow.btnUp = "Move up";
Apromore.I18N.PropertyWindow.btnDown = "Move down";
Apromore.I18N.PropertyWindow.createNew = "Create new";

if(!Apromore.I18N.PropertyWindow) Apromore.I18N.PropertyWindow = {};
Apromore.I18N.PropertyWindow.oftenUsed = "Main attributes";
Apromore.I18N.PropertyWindow.moreProps = "More attributes";
Apromore.I18N.PropertyWindow.characteristicNr = "Cost &amp; Resource Analysis";
Apromore.I18N.PropertyWindow.meta = "Custom attributes";

if(!Apromore.I18N.PropertyWindow.Category){Apromore.I18N.PropertyWindow.Category = {}}
Apromore.I18N.PropertyWindow.Category.popular = "Main Attributes";
Apromore.I18N.PropertyWindow.Category.characteristicnr = "Cost &amp; Resource Analysis";
Apromore.I18N.PropertyWindow.Category.others = "More Attributes";
Apromore.I18N.PropertyWindow.Category.meta = "Custom Attributes";

if(!Apromore.I18N.PropertyWindow.ListView) Apromore.I18N.PropertyWindow.ListView = {};
Apromore.I18N.PropertyWindow.ListView.title = "Edit: ";
Apromore.I18N.PropertyWindow.ListView.dataViewLabel = "Already existing entries.";
Apromore.I18N.PropertyWindow.ListView.dataViewEmptyText = "No list entries.";
Apromore.I18N.PropertyWindow.ListView.addEntryLabel = "Add a new entry";
Apromore.I18N.PropertyWindow.ListView.buttonAdd = "Add";
Apromore.I18N.PropertyWindow.ListView.save = "Save";
Apromore.I18N.PropertyWindow.ListView.cancel = "Cancel";

if(!Apromore.I18N.Attachment) Apromore.I18N.Attachment = {};
Apromore.I18N.Attachment.attachment = "Attachment";
Apromore.I18N.Attachment.showDesc = "Show attachments";
Apromore.I18N.Attachment.hideDesc = "Hide attachments";
Apromore.I18N.Attachment.comment = "Comments";
Apromore.I18N.Attachment.showComments = "Show comments";
Apromore.I18N.Attachment.hideComments = "Hide comments";

if(!Apromore.I18N.FontSize) Apromore.I18N.FontSize = {};
Apromore.I18N.FontSize.fontSizeDesc= "Change font size";
Apromore.I18N.FontSize.fontsize ="Change Font Size";