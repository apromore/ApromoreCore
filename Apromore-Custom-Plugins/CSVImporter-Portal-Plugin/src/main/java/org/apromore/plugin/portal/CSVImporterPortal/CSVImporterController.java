//package org.apromore.plugin.portal.CSVImporterPortal;
//
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.Arrays;
//import java.util.List;
////import org.deckfour.xes.model.XLog;
//import org.zkoss.util.media.Media;
//import org.zkoss.zk.ui.event.Event;
//import org.zkoss.zul.*;
//import org.zkoss.zhtml.Messagebox;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.event.UploadEvent;
//import org.zkoss.zk.ui.select.SelectorComposer;
//import org.zkoss.zk.ui.select.annotation.Listen;
//import org.zkoss.zk.ui.select.annotation.Wire;
//import com.opencsv.CSVReader;
//
//// TODO: Auto-generated Javadoc
//
///**
// * The Class CSVImporterController.
// * <p>
// * import CSV file from user, display content into the page.
// */
//public class CSVImporterController extends SelectorComposer<Component> {
//
//    /**
//     * The my grid.
//     */
//    @Wire
//    private Grid myGrid;
//
//    @Wire
//    private Div attrBox;
//
//    @Wire
//    private Button toXESButton;
//
//    private Media media;
//
//
//    @Wire
//    private Div popUPBox;
//
//    private static String popupID = "pop_";
//    private static String textboxID = "txt_";
//    private static String labelID = "lbl_";
//
//    private static Integer AttribWidth = 300;
//
//    /**
//     * Upload file.
//     *
//     * @param event the event: upload event
//     *              allows importing CSV file, if imported correctly, it sets the grid model and row renderer.
//     */
//    @Listen("onUpload = #uploadFile")
//    public void uploadFile(UploadEvent event) {
//
//        this.media = event.getMedia();
//
//         if(attrBox != null) {
//             attrBox.getChildren().clear();
//         }
//        String[] allowedExtensions = {"csv", "xls", "xlsx"};
//        if (Arrays.asList(allowedExtensions).contains(media.getFormat())) {
//
//            // set grid model
//            myGrid.setModel(displayCSVContent(media));
//
//            //set grid row renderer
//            gridRendererController rowRenderer = new gridRendererController();
//            rowRenderer.setAttribWidth(AttribWidth);
//            myGrid.setRowRenderer(rowRenderer);
//
//            toXESButton.setDisabled(false);
//
//        } else {
//            Messagebox.show("Please select CSV file!", "Error", Messagebox.OK, Messagebox.ERROR);
//        }
//    }
//
//
//    private static CsvToXes CsvToXes = new CsvToXes();
//    /**
//     * Gets the Content.
//     *
//     * @param media the imported CSV file
//     * @return the model data
//     * <p>
//     * read CSV content and create list model to be set as grid model.
//     */
//    @SuppressWarnings("null")
//    private ListModel<String[]> displayCSVContent(Media media) {
//        CSVReader reader = null;
//
//        try {
//
//            // check file format to choose correct file reader.
//            if(media.isBinary()){
//                reader = new CSVReader(new InputStreamReader(media.getStreamData()));
//            } else {
//                reader = new CSVReader(media.getReaderData());
//            }
//            ListModelList<String[]> result = new ListModelList<String[]>();
//            String[] header;
//            String[] line;
//
//
//
//            /// display first numberOfrows to user and display drop down lists to set attributes
//            header = reader.readNext();   // read first line
//            result.add(header);
//
//            // add dropdown lists
//            if(attrBox != null) {
//                attrBox.getChildren().clear();
//            }
//            if(popUPBox != null) {
//                popUPBox.getChildren().clear();
//            }
//
//            line = reader.readNext();
//            if(line == null || header == null) {
//                Messagebox.show("Could not parse file!");
//                return null;
//            }
//
//            CsvToXes.setLine(line);
//            CsvToXes.setHeads(header);
//            CsvToXes.setOtherTimestamps();
//
//            myGrid.setWidth(line.length * AttribWidth + "px");
//            attrBox.setWidth(line.length * AttribWidth + "px");
//
//            CsvToXes.setLists(line.length, CsvToXes.getHeads(), (AttribWidth - 10) + "px");
//            List<Listbox> lists = CsvToXes.getLists();
//            for (Listbox list : lists) {
//                attrBox.appendChild(list);
//                attrBox.appendChild(new Space());
//            }
//            attrBox.clone();
//
//            creatPopUpTextBox(line.length);
//            CsvToXes.openPopUp();
//
//            // display first 1000 rows
//            int numberOfrows = 1000;
//            while (line != null && numberOfrows >= 0) {
//                result.add(line);
//                numberOfrows--;
//                line = reader.readNext();
//            }
//            reader.close();
//            return result;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Messagebox.show(e.getMessage());
//            return null;
//        }
//    }
//
//
//    private void creatPopUpTextBox(int colNum){
//        popUPBox.setWidth(colNum * (AttribWidth + 30) + "px");
//
//        for(int i =0; i<= colNum -1; i++){
//            Window item = new Window();
//            item.setId(popupID+ i);
//            item.setWidth((AttribWidth - 10) + "px");
//            item.setMinheight(300);
//            item.setClass("p-1");
//            item.setBorder("normal");
//            item.setStyle("margin-left:" + (i==0? 15: (i*AttribWidth) + 15)  + "px; position: absolute; z-index: 10; visibility: hidden;");
//
//            Span sp = new Span();
//            sp.setClass("fas fa-angle-double-up text-secondary float-right mb-1");
//            A hidelink = new A();
//            hidelink.appendChild(sp);
//            hidelink.addEventListener("onClick", (Event event) -> {
//                item.setStyle(item.getStyle().replace("visible", "hidden"));
//            });
//            item.appendChild(hidelink);
//
//            Textbox textbox = new Textbox();
//            textbox.setId(textboxID + i);
//            textbox.setWidth("100%");
//            textbox.setPlaceholder("Specify timestamp format");
//
//            textbox.addEventListener("onBlur", (Event event) -> {
//                if(!(textbox.getValue().isEmpty() || textbox.getValue().equals(""))){
//                    CsvToXes.tryParsing(textbox.getValue(), Integer.parseInt(textbox.getId().replace(textboxID,"")));
//                }
//            });
//            item.appendChild(textbox);
//
//            Label check_lbl = new Label();
//            check_lbl.setId(labelID + i);
//            item.appendChild(check_lbl);
//
//            popUPBox.appendChild(item);
//        }
//        popUPBox.clone();
//
//        CsvToXes.setPopUPBox(popUPBox);
//        CsvToXes.setPopupID(popupID);
//        CsvToXes.setTextboxID(textboxID);
//        CsvToXes.setLabelID(labelID);
//    }
//
//    @Listen("onClick = #toXESButton")
//    public void toXES() throws IOException{
////        if (media != null){
////            List<LogModel> xesModel = CsvToXes.prepareXesModel(media);
////            if (xesModel != null) {
////                // create XES file
////                XLog xlog = CsvToXes.createXLog(xesModel);
////                CsvToXes.toXESfile(xlog, media.getName());
////            }
////        }else{
//            Messagebox.show("Upload file first!");
////        }
//    }
//
//}