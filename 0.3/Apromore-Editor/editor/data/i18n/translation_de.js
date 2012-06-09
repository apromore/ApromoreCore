/**
 * @author nicolas.peters
 * 
 * Contains all strings for German language.
 * Version 1 - 08/29/08
 */
if(!ORYX) var ORYX = {};

if(!ORYX.I18N) ORYX.I18N = {};

ORYX.I18N.Language = "de_DE"; //Pattern <ISO language code>_<ISO country code> in lower case!

if(!ORYX.I18N.Oryx) ORYX.I18N.Oryx = {};

ORYX.I18N.Oryx.pleaseWait = "Editor wird geladen. Bitte warten...";
ORYX.I18N.Oryx.notLoggedOn = "Nicht angemeldet";
ORYX.I18N.Oryx.noBackendDefined	= "Achtung! \n Es wurde kein Repository definiert.\n Ihr Model kann nicht geladen werden. Bitte nutzen sie eine Editor Konfiguration mit einem Speicher Plugin.";

if(!ORYX.I18N.AddDocker) ORYX.I18N.AddDocker = {};

ORYX.I18N.AddDocker.group = "Docker";
ORYX.I18N.AddDocker.add = "Docker Hinzufügen";
ORYX.I18N.AddDocker.addDesc = "Fügen Sie einer Kante einen Docker hinzu, indem Sie auf die Kante klicken";
ORYX.I18N.AddDocker.del = "Docker Löschen";
ORYX.I18N.AddDocker.delDesc = "Löscht einen Docker durch Klicken auf den zu löschenden Docker";

if(!ORYX.I18N.Arrangement) ORYX.I18N.Arrangement = {};

ORYX.I18N.Arrangement.groupZ = "Z-Order";
ORYX.I18N.Arrangement.btf = "In den Vordergrund";
ORYX.I18N.Arrangement.btfDesc = "In den Vordergrund";
ORYX.I18N.Arrangement.btb = "In den Hintergrund";
ORYX.I18N.Arrangement.btbDesc = "In den Hintergrund";
ORYX.I18N.Arrangement.bf = "Eine Ebene nach Vorne";
ORYX.I18N.Arrangement.bfDesc = "Eine Ebene nach Vorne";
ORYX.I18N.Arrangement.bb = "Eine Ebene nach Hinten";
ORYX.I18N.Arrangement.bbDesc = "Eine Ebene nach Hinten";
ORYX.I18N.Arrangement.groupA = "Alignment";
ORYX.I18N.Arrangement.ab = "Unten ausrichten";
ORYX.I18N.Arrangement.abDesc = "Unten ausrichten";
ORYX.I18N.Arrangement.am = "Horizontal ausrichten";
ORYX.I18N.Arrangement.amDesc = "Horizontal ausrichten";
ORYX.I18N.Arrangement.at = "Oben ausrichten";
ORYX.I18N.Arrangement.atDesc = "Oben ausrichten";
ORYX.I18N.Arrangement.al = "Links ausrichten";
ORYX.I18N.Arrangement.alDesc = "Links ausrichten";
ORYX.I18N.Arrangement.ac = "Vertikal ausrichten";
ORYX.I18N.Arrangement.acDesc = "Vertikal ausrichten";
ORYX.I18N.Arrangement.ar = "Rechts ausrichten";
ORYX.I18N.Arrangement.arDesc = "Rechts ausrichten";
ORYX.I18N.Arrangement.as = "Größenangleichung";
ORYX.I18N.Arrangement.asDesc = "Größenangleichung";

if(!ORYX.I18N.Edit) ORYX.I18N.Edit = {};

ORYX.I18N.Edit.group = "Edit";
ORYX.I18N.Edit.cut = "Ausschneiden";
ORYX.I18N.Edit.cutDesc = "Ausschneiden der selektierten Elemente";
ORYX.I18N.Edit.copy = "Kopieren";
ORYX.I18N.Edit.copyDesc = "Kopieren der selektierten Elemente";
ORYX.I18N.Edit.paste = "Einfügen";
ORYX.I18N.Edit.pasteDesc = "Einfügen von kopierten/ausgeschnittenen Elementen";
ORYX.I18N.Edit.del = "Löschen";
ORYX.I18N.Edit.delDesc = "Löschen der selektierten Elemente";

if(!ORYX.I18N.EPCSupport) ORYX.I18N.EPCSupport = {};

ORYX.I18N.EPCSupport.group = "EPC";
ORYX.I18N.EPCSupport.exp = "EPML Export";
ORYX.I18N.EPCSupport.expDesc = "Exportieren nach EPML";
ORYX.I18N.EPCSupport.imp = "EPML Import";
ORYX.I18N.EPCSupport.impDesc = "Importieren einer EPML Datei";
ORYX.I18N.EPCSupport.progressExp = "Exportiere Modell";
ORYX.I18N.EPCSupport.selectFile = "Wählen Sie eine EPML Datei aus, die Sie importieren möchten.";
ORYX.I18N.EPCSupport.file = "Datei";
ORYX.I18N.EPCSupport.impPanel = "EPML Datei importieren";
ORYX.I18N.EPCSupport.impBtn = "Importieren";
ORYX.I18N.EPCSupport.close = "Schließen";
ORYX.I18N.EPCSupport.error = "Fehler";
ORYX.I18N.EPCSupport.progressImp = "Importiere...";

if(!ORYX.I18N.ERDFSupport) ORYX.I18N.ERDFSupport = {};

ORYX.I18N.ERDFSupport.exp = "ERDF Export";
ORYX.I18N.ERDFSupport.expDesc = "Exportieren nach ERDF";
ORYX.I18N.ERDFSupport.imp = "ERDF Import";
ORYX.I18N.ERDFSupport.impDesc = "ERDF Datei importieren";
ORYX.I18N.ERDFSupport.impFailed = "Anfrage für den Import der ERDF Datei ist fehlgeschlagen.";
ORYX.I18N.ERDFSupport.impFailed2 = "Während des Importierens ist ein Fehler aufgetreten. <br/>Fehlermeldung: <br/><br/>";
ORYX.I18N.ERDFSupport.error = "Fehler";
ORYX.I18N.ERDFSupport.noCanvas = "Das XML Dokument enthält keinen Oryx Canvas Knoten.";
ORYX.I18N.ERDFSupport.noSS = "Im XML Dokument ist kein Stencil Set referenziert.";
ORYX.I18N.ERDFSupport.wrongSS = "Das im XML Dokument referenzierte Stencil Set passt nicht zu dem im Editor geladenen Stencil Set.";
ORYX.I18N.ERDFSupport.selectFile = "Wählen sie eine ERDF Datei (.xml) aus oder geben Sie den ERDF Code im Textfeld ein.";
ORYX.I18N.ERDFSupport.file = "Datei";
ORYX.I18N.ERDFSupport.impERDF = "ERDF importieren";
ORYX.I18N.ERDFSupport.impBtn = "Importieren";
ORYX.I18N.ERDFSupport.impProgress = "Importiere...";
ORYX.I18N.ERDFSupport.close = "Schließen";
ORYX.I18N.ERDFSupport.deprTitle = "Wirklich nach eRDF exportieren?";
ORYX.I18N.ERDFSupport.deprText = "Der Export nach eRDF wird nicht empfohlen, da dieses Format in zukünftigen Versionen des Oryx Editors nicht mehr unterstützt wird. Verwenden Sie statt dessen den Export nach JSON, falls möglich. Wollen Sie dennoch das Model nach eRDF exportieren?";

if(!ORYX.I18N.jPDLSupport) ORYX.I18N.jPDLSupport = {};

ORYX.I18N.jPDLSupport.group = "ExecBPMN";
ORYX.I18N.jPDLSupport.exp = "jPDL Export";
ORYX.I18N.jPDLSupport.expDesc = "Exportieren nach jPDL";
ORYX.I18N.jPDLSupport.imp = "jPDL Import";
ORYX.I18N.jPDLSupport.impDesc = "jPDL Datei importieren";
ORYX.I18N.jPDLSupport.impFailedReq = "Anfrage für den Import der jPDL Datei ist fehlgeschlagen.";
ORYX.I18N.jPDLSupport.impFailedJson = "Transformation der jPDL Datei ist fehlgeschlagen.";
ORYX.I18N.jPDLSupport.impFailedJsonAbort = "Import abgebrochen.";
ORYX.I18N.jPDLSupport.loadSseQuestionTitle = "Stencil Set Erweiterung für jBPM muss geladen werden"; 
ORYX.I18N.jPDLSupport.loadSseQuestionBody = "Um jPDL importieren zu können, muss die Stencil Set Erweiterung für jBPM geladen werden. Möchten Sie fortfahren?";
ORYX.I18N.jPDLSupport.expFailedReq = "Anfrage für den Export des Models ist fehlgeschlagen.";
ORYX.I18N.jPDLSupport.expFailedXml = "Export nach jPDL ist fehlgeschlagen. Exporter meldet: ";
ORYX.I18N.jPDLSupport.error = "Fehler";
ORYX.I18N.jPDLSupport.selectFile = "Wählen sie eine jPDL Datei (.xml) aus oder geben Sie den jPDL Code im Textfeld ein.";
ORYX.I18N.jPDLSupport.file = "Datei";
ORYX.I18N.jPDLSupport.impJPDL = "jPDL importieren";
ORYX.I18N.jPDLSupport.impBtn = "Importieren";
ORYX.I18N.jPDLSupport.impProgress = "Importiere...";
ORYX.I18N.jPDLSupport.close = "Schließen";

if(!ORYX.I18N.Save) ORYX.I18N.Save = {};

ORYX.I18N.Save.group = "File";
ORYX.I18N.Save.save = "Speichern";
ORYX.I18N.Save.saveDesc = "Speichern";
ORYX.I18N.Save.saveAs = "Speichern als...";
ORYX.I18N.Save.saveAsDesc = "Speichern als...";
ORYX.I18N.Save.unsavedData = "Das Diagramm enthält nicht gespeicherte Daten. Sind Sie sicher, daß Sie den Editor schließen möchten?";
ORYX.I18N.Save.newProcess = "Neuer Prozess";
ORYX.I18N.Save.saveAsTitle = "Speichern als...";
ORYX.I18N.Save.saveBtn = "Speichern";
ORYX.I18N.Save.close = "Schließen";
ORYX.I18N.Save.savedAs = "Gespeichert als";
ORYX.I18N.Save.saved = "Gespeichert";
ORYX.I18N.Save.failed = "Das Speichern ist fehlgeschlagen.";
ORYX.I18N.Save.noRights = "Sie haben nicht die erforderlichen Rechte, um Änderungen zu speichern.";
ORYX.I18N.Save.saving = "Speichern";
ORYX.I18N.Save.saveAsHint = "Das Diagramm wurde unter folgendem Link gespeichert:";

if(!ORYX.I18N.File) ORYX.I18N.File = {};

ORYX.I18N.File.group = "File";
ORYX.I18N.File.print = "Drucken";
ORYX.I18N.File.printDesc = "Drucken";
ORYX.I18N.File.pdf = "PDF Export";
ORYX.I18N.File.pdfDesc = "Exportieren nach PDF";
ORYX.I18N.File.info = "Über";
ORYX.I18N.File.infoDesc = "Über";
ORYX.I18N.File.genPDF = "PDF wird generiert";
ORYX.I18N.File.genPDFFailed = "Die Generierung der PDF Datei ist fehlgeschlagen.";
ORYX.I18N.File.printTitle = "Drucken";
ORYX.I18N.File.printMsg = "Leider arbeitet die Druckfunktion zur Zeit nicht immer korrekt. Bitte nutzen Sie den PDF Export, und drucken Sie das PDF Dokument aus. Möchten Sie dennoch mit dem Drucken fortfahren?";

if(!ORYX.I18N.Grouping) ORYX.I18N.Grouping = {};

ORYX.I18N.Grouping.grouping = "Grouping";
ORYX.I18N.Grouping.group = "Gruppieren";
ORYX.I18N.Grouping.groupDesc = "Gruppierung der selektierten Elemente";
ORYX.I18N.Grouping.ungroup = "Gruppierung aufheben";
ORYX.I18N.Grouping.ungroupDesc = "Aufheben aller Gruppierungen der selektierten Elemente";

if(!ORYX.I18N.Loading) ORYX.I18N.Loading = {};

ORYX.I18N.Loading.waiting ="Bitte warten...";

if(!ORYX.I18N.PropertyWindow) ORYX.I18N.PropertyWindow = {};

ORYX.I18N.PropertyWindow.name = "Name";
ORYX.I18N.PropertyWindow.value = "Wert";
ORYX.I18N.PropertyWindow.selected = "ausgewählt";
ORYX.I18N.PropertyWindow.clickIcon = "Symbol anklicken";
ORYX.I18N.PropertyWindow.add = "Hinzufügen";
ORYX.I18N.PropertyWindow.rem = "Löschen";
ORYX.I18N.PropertyWindow.complex = "Editor für komplexe Eigenschaft";
ORYX.I18N.PropertyWindow.text = "Editor für einen Text";
ORYX.I18N.PropertyWindow.ok = "Ok";
ORYX.I18N.PropertyWindow.cancel = "Abbrechen";
ORYX.I18N.PropertyWindow.dateFormat = "d/m/y";

if(!ORYX.I18N.ShapeMenuPlugin) ORYX.I18N.ShapeMenuPlugin = {};

ORYX.I18N.ShapeMenuPlugin.drag = "Ziehen";
ORYX.I18N.ShapeMenuPlugin.clickDrag = "Klicken oder ziehen";
ORYX.I18N.ShapeMenuPlugin.morphMsg = "Shape morphen";

if(!ORYX.I18N.SyntaxChecker) ORYX.I18N.SyntaxChecker = {};

ORYX.I18N.SyntaxChecker.group = "Verification";
ORYX.I18N.SyntaxChecker.name = "Syntax-Checker";
ORYX.I18N.SyntaxChecker.desc = "Überprüfung der Syntax";
ORYX.I18N.SyntaxChecker.noErrors = "Es wurden keine Syntaxfehler gefunden.";
ORYX.I18N.SyntaxChecker.invalid = "Ungültige Antwort vom Server.";
ORYX.I18N.SyntaxChecker.checkingMessage = "Überprüfung wird durchgeführt ...";

if(!ORYX.I18N.Undo) ORYX.I18N.Undo = {};

ORYX.I18N.Undo.group = "Undo";
ORYX.I18N.Undo.undo = "Rückgängig";
ORYX.I18N.Undo.undoDesc = "Rückgängig";
ORYX.I18N.Undo.redo = "Wiederherstellen";
ORYX.I18N.Undo.redoDesc = "Wiederherstellen";

if(!ORYX.I18N.View) ORYX.I18N.View = {};

ORYX.I18N.View.group = "Zoom";
ORYX.I18N.View.zoomIn = "Vergrößern";
ORYX.I18N.View.zoomInDesc = "Vergrößern";
ORYX.I18N.View.zoomOut = "Verkleinern";
ORYX.I18N.View.zoomOutDesc = "Verkleinern";
ORYX.I18N.View.zoomStandard = "Originalgröße";
ORYX.I18N.View.zoomStandardDesc = "Originalgröße";
ORYX.I18N.View.zoomFitToModel = "Modelgröße";
ORYX.I18N.View.zoomFitToModelDesc = "Modelgröße";

/** New Language Properties: 08.12.2008 **/

ORYX.I18N.PropertyWindow.title = "Eigenschaften";

if(!ORYX.I18N.ShapeRepository) ORYX.I18N.ShapeRepository = {};
ORYX.I18N.ShapeRepository.title = "Shape Verzeichnis";

ORYX.I18N.Save.dialogDesciption = "Bitte geben Sie einen Namen, eine Beschreibung und einen Kommentar ein.";
ORYX.I18N.Save.dialogLabelTitle = "Titel";
ORYX.I18N.Save.dialogLabelDesc = "Beschreibung";
ORYX.I18N.Save.dialogLabelType = "Typ";
ORYX.I18N.Save.dialogLabelComment = "Revisionskommentar";

Ext.MessageBox.buttonText.yes = "Ja";
Ext.MessageBox.buttonText.no = "Nein";
Ext.MessageBox.buttonText.cancel = "Abbrechen";
Ext.MessageBox.buttonText.ok = "OK";

if(!ORYX.I18N.Perspective) ORYX.I18N.Perspective = {};
ORYX.I18N.Perspective.no = "Keine Perspektive"
ORYX.I18N.Perspective.noTip = "Zurücksetzen der aktuellen Perspektive"

/** New Language Properties: 21.04.2009 */
ORYX.I18N.JSONSupport = {
    imp: {
        name: "JSON importieren",
        desc: "Importiert ein neues Modell aus JSON",
        group: "Export",
        selectFile: "Wählen Sie eine JSON-Datei (*.json) aus, die Sie importieren möchten, oder fügen Sie JSON in das Textfeld ein.",
        file: "Datei",
        btnImp: "Importieren",
        btnClose: "Schließen",
        progress: "Importieren ...",
        syntaxError: "Syntaxfehler"
    },
    exp: {
        name: "Nach JSON exportieren",
        desc: "Exportiert das aktuelle Modell nach JSON",
        group: "Export"
    }
};

/** New Language Properties: 09.05.2009 */
if(!ORYX.I18N.JSONImport) ORYX.I18N.JSONImport = {};

ORYX.I18N.JSONImport.title = "JSON Import";
ORYX.I18N.JSONImport.wrongSS = "Das Stencil Set der importierten Datei ({0}) entspricht nicht dem geladenen Stencil Set ({1})."

/** New Language Properties: 14.05.2009 */
if(!ORYX.I18N.RDFExport) ORYX.I18N.RDFExport = {};
ORYX.I18N.RDFExport.group = "Export";
ORYX.I18N.RDFExport.rdfExport = "Nach RDF exportieren";
ORYX.I18N.RDFExport.rdfExportDescription = "Exportiert das aktuelle Model in die XML-Serialisierung des Resource Description Frameworks (RDF)";

/** New Language Properties: 15.05.2009*/
if(!ORYX.I18N.SyntaxChecker.BPMN) ORYX.I18N.SyntaxChecker.BPMN={};
ORYX.I18N.SyntaxChecker.BPMN_NO_SOURCE = "Eine Kante muss einen Ursprung haben.";
ORYX.I18N.SyntaxChecker.BPMN_NO_TARGET = "Eine Kante muss ein Ziel haben.";
ORYX.I18N.SyntaxChecker.BPMN_DIFFERENT_PROCESS = "Ursprungs- und Zielknoten müssen im gleichen Prozess sein.";
ORYX.I18N.SyntaxChecker.BPMN_SAME_PROCESS = "Ursprungs- und Zielknoten müssen in verschiedenen Pools enthalten sein.";
ORYX.I18N.SyntaxChecker.BPMN_FLOWOBJECT_NOT_CONTAINED_IN_PROCESS = "Ein Kontrollflussobjekt muss sich in einem Prozess befinden.";
ORYX.I18N.SyntaxChecker.BPMN_ENDEVENT_WITHOUT_INCOMING_CONTROL_FLOW = "Ein End-Ereignis muss einen eingehenden Sequenzfluss haben.";
ORYX.I18N.SyntaxChecker.BPMN_STARTEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "Ein Start-Ereignis muss einen ausgehenden Sequenzfluss haben.";
ORYX.I18N.SyntaxChecker.BPMN_STARTEVENT_WITH_INCOMING_CONTROL_FLOW = "Start-Ereignisse dürfen keinen eingehenden Sequenzfluss haben.";
ORYX.I18N.SyntaxChecker.BPMN_ATTACHEDINTERMEDIATEEVENT_WITH_INCOMING_CONTROL_FLOW = "Angeheftete Zwischen-Ereignisse dürfen keinen eingehenden Sequenzfluss haben.";
ORYX.I18N.SyntaxChecker.BPMN_ATTACHEDINTERMEDIATEEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "Angeheftete Zwischen-Ereignisse müssen genau einen ausgehenden Sequenzfluss haben.";
ORYX.I18N.SyntaxChecker.BPMN_ENDEVENT_WITH_OUTGOING_CONTROL_FLOW = "End-Ereignisse dürfen keinen ausgehenden Sequenzfluss haben.";
ORYX.I18N.SyntaxChecker.BPMN_EVENTBASEDGATEWAY_BADCONTINUATION = "Auf Ereignis-basierte Gateways dürfen weder Gateways noch Subprozesse folgen.";
ORYX.I18N.SyntaxChecker.BPMN_NODE_NOT_ALLOWED = "Knotentyp ist nicht erlaubt.";

if(!ORYX.I18N.SyntaxChecker.IBPMN) ORYX.I18N.SyntaxChecker.IBPMN={};
ORYX.I18N.SyntaxChecker.IBPMN_NO_ROLE_SET = "Für Interaktionen muss ein Sender und ein Empfänger definiert sein.";
ORYX.I18N.SyntaxChecker.IBPMN_NO_INCOMING_SEQFLOW = "Dieser Knoten muss eingehenden Sequenzfluss besitzen.";
ORYX.I18N.SyntaxChecker.IBPMN_NO_OUTGOING_SEQFLOW = "Dieser Knoten muss ausgehenden Sequenzfluss besitzen.";

if(!ORYX.I18N.SyntaxChecker.InteractionNet) ORYX.I18N.SyntaxChecker.InteractionNet={};
ORYX.I18N.SyntaxChecker.InteractionNet_SENDER_NOT_SET = "Sender ist nicht definiert";
ORYX.I18N.SyntaxChecker.InteractionNet_RECEIVER_NOT_SET = "Empfänger ist nicht definiert";
ORYX.I18N.SyntaxChecker.InteractionNet_MESSAGETYPE_NOT_SET = "Nachrichtentyp ist nicht definiert.";
ORYX.I18N.SyntaxChecker.InteractionNet_ROLE_NOT_SET = "Rolle ist nicht definiert.";

if(!ORYX.I18N.SyntaxChecker.EPC) ORYX.I18N.SyntaxChecker.EPC={};
ORYX.I18N.SyntaxChecker.EPC_NO_SOURCE = "Eine Kante muss einen Ursprung haben.";
ORYX.I18N.SyntaxChecker.EPC_NO_TARGET = "Eine Kante muss ein Ziel haben.";
ORYX.I18N.SyntaxChecker.EPC_NOT_CONNECTED = "Dieser Knoten muss eingehende oder ausgehende Kanten besitzen.";
ORYX.I18N.SyntaxChecker.EPC_NOT_CONNECTED_2 = "Dieser Knoten muss sowohl eingehende als auch ausgehende Kanten besitzen.";
ORYX.I18N.SyntaxChecker.EPC_TOO_MANY_EDGES = "Knoten ist mit zu vielen Kanten verbunden.";
ORYX.I18N.SyntaxChecker.EPC_NO_CORRECT_CONNECTOR = "Knoten ist kein korrekter Konnektor.";
ORYX.I18N.SyntaxChecker.EPC_MANY_STARTS = "Es darf nur ein Start-Ereignis geben.";
ORYX.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_OR = "Funktionen hinter einem OR-/XOR-Split sind nicht erlaubt.";
ORYX.I18N.SyntaxChecker.EPC_PI_AFTER_OR = "Prozessschnittstellen hinter einem OR-/XOR-Split ist nicht erlaubt.";
ORYX.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_FUNCTION =  "Auf eine Funktion darf keine Funktion folgen.";
ORYX.I18N.SyntaxChecker.EPC_EVENT_AFTER_EVENT =  "Auf ein Ereignis darf kein Ereignis folgen.";
ORYX.I18N.SyntaxChecker.EPC_PI_AFTER_FUNCTION =  "Auf eine Funktion darf keine Prozessschnittstelle folgen.";
ORYX.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_PI =  "Auf eine Prozessschnittstelle darf keine Funktion folgen.";
ORYX.I18N.SyntaxChecker.EPC_SOURCE_EQUALS_TARGET = "Eine Kante muss zwei verschiedene Knoten verbinden."

if(!ORYX.I18N.SyntaxChecker.PetriNet) ORYX.I18N.SyntaxChecker.PetriNet={};
ORYX.I18N.SyntaxChecker.PetriNet_NOT_BIPARTITE = "Der Graph ist nicht bepartit.";
ORYX.I18N.SyntaxChecker.PetriNet_NO_LABEL = "Bezeichnung für einen bezeichnete Transition ist nicht gesetzt.";
ORYX.I18N.SyntaxChecker.PetriNet_NO_ID = "Ein Knoten besitzt keine ID.";
ORYX.I18N.SyntaxChecker.PetriNet_SAME_SOURCE_AND_TARGET = "Zwei Flussbeziehungen besitzen den gleichen Ursprung und das gleiche Ziel.";
ORYX.I18N.SyntaxChecker.PetriNet_NODE_NOT_SET = "Ein Knoten ist nicht definiert für einen Flussbeziehung.";

/** New Language Properties: 02.06.2009*/
ORYX.I18N.Edge = "Kante";
ORYX.I18N.Node = "Knoten";

/** New Language Properties: 02.06.2009*/
ORYX.I18N.SyntaxChecker.notice = "Bitte bewegen Sie den Mauszeiger über ein rotes Kreuz, um die Details zu erfahren.";

/** New Language Properties: 15.07.2009*/
if(!ORYX.I18N.Layouting) ORYX.I18N.Layouting ={};
ORYX.I18N.Layouting.doing = "Layouten...";

/** New Language Properties: 18.08.2009*/
ORYX.I18N.SyntaxChecker.MULT_ERRORS = "Mehrere Fehler";

/** New Language Properties: 08.09.2009*/
if(!ORYX.I18N.PropertyWindow) ORYX.I18N.PropertyWindow = {};
ORYX.I18N.PropertyWindow.oftenUsed = "Hauptattribute";
ORYX.I18N.PropertyWindow.moreProps = "Mehr Attribute";

/** New Language Properties 01.10.2009 */
if(!ORYX.I18N.SyntaxChecker.BPMN2) ORYX.I18N.SyntaxChecker.BPMN2 = {};

ORYX.I18N.SyntaxChecker.BPMN2_DATA_INPUT_WITH_INCOMING_DATA_ASSOCIATION = "Ein Dateninput darf keine ausgehenden Datenassoziationen haben.";
ORYX.I18N.SyntaxChecker.BPMN2_DATA_OUTPUT_WITH_OUTGOING_DATA_ASSOCIATION = "Ein Datenoutput darf keine eingehenden Datenassoziationen haben.";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_TARGET_WITH_TOO_MANY_INCOMING_SEQUENCE_FLOWS = "Ziele von Ereignis-basierten Gateways dürfen nicht mehr als einen eingehenden Sequenzfluss haben.";

/** New Language Properties 02.10.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WITH_TOO_LESS_OUTGOING_SEQUENCE_FLOWS = "Ein Ereignis-basiertes Gateway muss 2 oder mehr ausgehende Sequenzflüsse besitzen.";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_EVENT_TARGET_CONTRADICTION = "Wenn Nachrichten-Zwischenereignisse im Diagramm verwendet werden, dann dürfen Receive Tasks nicht verwendet werden und umgekehrt.";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WRONG_TRIGGER = "Nur die folgenden Zwischen-Ereignis-Auslöser sind hier zulässig: Nachricht, Signal, Timer, Bedingungs und Mehrfach.";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WRONG_CONDITION_EXPRESSION = "Die ausgehenden Sequenzflüsse eines Ereignis-Gateways dürfen keinen Bedingungsausdruck besitzen.";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_NOT_INSTANTIATING = "Das Gateway erfüllt nicht die Voraussetzungen um den Prozess zu instantiieren. Bitte verwenden Sie ein Start-Ereignis oder setzen Sie die Instanziierungs-Attribute korrekt.";

/** New Language Properties 05.10.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_MIXED_FAILURE = "Das Gateway muss mehrere eingehende und ausgehende Sequenzflüsse besitzen.";
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_CONVERGING_FAILURE = "Das Gateway muss mehrere eingehende aber darf keine mehrfache ausgehende Sequenzflüsse besitzen.";
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_DIVERGING_FAILURE = "Das Gateway darf keine mehrfachen eingehenden aber muss mehrfache ausgehende Sequenzflüsse besitzen.";
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAY_WITH_NO_OUTGOING_SEQUENCE_FLOW = "Ein Gateway muss mindestens einen ausgehenden Sequenzfluss besitzen.";
ORYX.I18N.SyntaxChecker.BPMN2_RECEIVE_TASK_WITH_ATTACHED_EVENT = "Empfangende Tasks, die in Ereignis-Gateway-Konfigurationen benutzt werden, dürfen keine angehefteten Zwischen-Ereignisse besitzen.";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_SUBPROCESS_BAD_CONNECTION = "Ein Ereignis-Unterprozess darf keinen eingehenden oder ausgehenden Sequenzfluss besitzen.";

/** New Language Properties 13.10.2009 */
ORYX.I18N.SyntaxChecker.BPMN_MESSAGE_FLOW_NOT_CONNECTED = "Mindestens ein Ende des Nachrichtenflusses muss mit einem anderen Objekt verbunden sein.";

/** New Language Properties 05.11.2009 */
if(!ORYX.I18N.RESIZE) ORYX.I18N.RESIZE = {};
ORYX.I18N.RESIZE.tipGrow = "Zeichenfläche vergrößern:";
ORYX.I18N.RESIZE.tipShrink = "Zeichenfläche verkleinern:";
ORYX.I18N.RESIZE.N = "Nach oben";
ORYX.I18N.RESIZE.W = "Nach links";
ORYX.I18N.RESIZE.S ="Nach unten";
ORYX.I18N.RESIZE.E ="Nach rechts";

/** New Language Properties 24.11.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_TOO_MANY_INITIATING_MESSAGES = "Eine Choreographie-Aktivität darf nur eine initiierende Nachricht besitzen.";
ORYX.I18N.SyntaxChecker.BPMN_MESSAGE_FLOW_NOT_ALLOWED = "Ein Nachrichtenfluss ist an dieser Stelle nicht erlaubt.";

/** New Language Properties 27.11.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WITH_TOO_LESS_INCOMING_SEQUENCE_FLOWS = "Ein Ereignis-basiertes Gateway, dass nicht instanziierend ist, muss mindestens einen eingehenden Kontrollfluss besitzen.";
ORYX.I18N.SyntaxChecker.BPMN2_TOO_FEW_INITIATING_PARTICIPANTS = "Eine Choreographie-Aktivität musst genau einen initiierenden Teilnehmer (weiß) besitzen.";
ORYX.I18N.SyntaxChecker.BPMN2_TOO_MANY_INITIATING_PARTICIPANTS = "Eine Choreographie-Aktivität darf nicht mehr als einen initiierenden Teilnehmer (weiß) besitzen."

ORYX.I18N.SyntaxChecker.COMMUNICATION_AT_LEAST_TWO_PARTICIPANTS = "Die Kommunikation oder Sub-Konversation muss mit mindestens zwei Teilnehmern verbunden sein.";
ORYX.I18N.SyntaxChecker.MESSAGEFLOW_START_MUST_BE_PARTICIPANT = "Die Nachrichtenflussquelle muss ein Teilnehmer sein.";
ORYX.I18N.SyntaxChecker.MESSAGEFLOW_END_MUST_BE_PARTICIPANT = "Das Nachrichtenflussziel muss ein Teilnehmer sein.";
ORYX.I18N.SyntaxChecker.CONV_LINK_CANNOT_CONNECT_CONV_NODES = "Der Konversationslink muss eine Kommunikation oder Sub-Konversation mit einem Teilnehmer verbinden.";

/** New Language Properties 24.04.2012 - YAWL Extension */
/** YAWL Plugin **/
if(!ORYX.I18N.YAWL) ORYX.I18N.YAWL = {};
ORYX.I18N.YAWL.importName = "YAWL importieren";
ORYX.I18N.YAWL.importDesc = "";
ORYX.I18N.YAWL.importGroup = "Export";
ORYX.I18N.YAWL.exportName = "YAWL exportieren";
ORYX.I18N.YAWL.exportDesc = "";
ORYX.I18N.YAWL.exportGroup = "Export";
ORYX.I18N.YAWL.selectFileText = "Eine YAWL Spezifikation �ber eine Datei ausw�hlen (.yawl) oder in das Textfeld eingeben!"
ORYX.I18N.YAWL.fileText = "Datei: "
ORYX.I18N.YAWL.importTitle = "Importiere YAWL Spezifikation"
ORYX.I18N.YAWL.importButtonText = "Importieren"
ORYX.I18N.YAWL.importProgress = "Importiere YAWL Spezifikation..."
ORYX.I18N.YAWL.importFailed = "Der Import der YAWL Spezifikation ist fehlgeschlagen!"
ORYX.I18N.YAWL.importFinished = 'Import abgeschlossen!';
ORYX.I18N.YAWL.importFinishedText = 'You will be redirected to the just saved root net.';
ORYX.I18N.YAWL.closeButtonText = "Schlie�en"
ORYX.I18N.YAWL.yawlGroup = "YAWL";
ORYX.I18N.YAWL.cancelationSetName = "Cancelation Set hervorheben";
ORYX.I18N.YAWL.cancelationSetDesc = "";
ORYX.I18N.YAWL.messageGetIDofNet = "Getting ID of Net ";
ORYX.I18N.YAWL.messageInsertSubnetUrls = "Inserting URLs of Subnets to Decomposite Tasks";
ORYX.I18N.YAWL.messageSavingNet = "Saving net ";
ORYX.I18N.YAWL.messageConversionFailed = "Konvertierung fehlgeschlagen! \n";
ORYX.I18N.YAWL.messageConversionWarnings = "Konvertierung mit Warnungen abgeschlossen: \n";	
ORYX.I18N.YAWL.unfoldMenuNewSubnet = "neues Subnetz";
ORYX.I18N.YAWL.unfoldMenu = ".. bestehendes Subnetz";
ORYX.I18N.YAWL.unfoldMenuChooseSubnet = "W�hle ein Subnetz:";
ORYX.I18N.YAWL.unfoldMenuSuccess = "Unfold to net";
ORYX.I18N.YAWL.unfoldMenuSuccessText = "Unfolded to: ";	
ORYX.I18N.YAWL.unfoldExistingMenuFailure = "Could not retrieve possible subnets from backend!";
ORYX.I18N.YAWL.unfoldNewMenuFailure = "Could not save new subnet";