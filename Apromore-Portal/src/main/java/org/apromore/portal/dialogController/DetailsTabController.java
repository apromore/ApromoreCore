package org.apromore.portal.dialogController;

import org.apromore.model.Detail;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;
import org.zkoss.zul.Button;

import java.util.Iterator;
import java.util.List;

/**
 * Created by corno on 13/08/2014.
 */
public class DetailsTabController extends BaseController {

    private MainController mainController;
    private Window windowDetails;
    private Grid grid;
    private Textbox textBoxQuery;
//    private Button button;

    public DetailsTabController(final MainController mainC, final List<Detail> details, String query) throws SuspendNotAllowedException, InterruptedException {
        this.mainController = mainC;
        this.windowDetails = (Window) Executions.createComponents("macros/filter/infoQuery.zul", null, null);
        this.grid = (Grid) this.windowDetails.getFellow("grid");

        this.textBoxQuery = (Textbox) this.windowDetails.getFellow("textBoxQuery");
//        this.textBoxQuery.setValue(query);
        this.textBoxQuery.setText(query);

//        this.button = (Button) this.windowDetails.getFellow("closeButton");
        this.windowDetails.doModal();
        Rows rows=new Rows();
        Row row;
        Label labelOne;
        Label similarityOne;
        Label listLabels;
        for(Detail detail : details){
            row = new Row();
            labelOne = new Label(detail.getLabelOne());
            similarityOne = new Label(detail.getSimilarityLabelOne());
            row.getChildren().add(labelOne);
            row.getChildren().add(similarityOne);
            StringBuilder sb=new StringBuilder();
            Iterator<String> iterator = detail.getDetail().iterator();
            while(iterator.hasNext()){
                String str = iterator.next();
                if(iterator.hasNext()){
                    sb.append(str+", ");
                }else{
                    sb.append(str);
                }
            }
            listLabels = new Label(sb.toString());
            row.getChildren().add(listLabels);
            rows.appendChild(row);
        }

        grid.appendChild(rows);

//        this.button.addEventListener("onClick", new EventListener<Event>() {
//            @Override
//            public void onEvent(final Event event) throws Exception {
//                doCancel();
//            }
//        });
    }

    protected final void doCancel() {
        this.windowDetails.detach();
    }
}
