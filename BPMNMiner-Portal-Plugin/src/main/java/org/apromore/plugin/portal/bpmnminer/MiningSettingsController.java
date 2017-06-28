package org.apromore.plugin.portal.bpmnminer;

import com.raffaeleconforti.bpmnminer.subprocessminer.selection.SelectMinerResult;
import com.raffaeleconforti.wrapper.settings.MiningSettings;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adriano on 11/06/2017.
 */
public class MiningSettingsController {
    private Window miningSettins;
    private String windowPath;
    private Button ok, cancel;
    private List<Slider> thresholds = new ArrayList<>();
    private List<Radiogroup> flags = new ArrayList<>();
    private int miningAlgorithm;
    private MiningSettings params;

    private BPMNMinerController bpmnMinerController;

    public MiningSettingsController(BPMNMinerController bpmnMinerController, int miningAlgorithm) {
        List<Slider> thresholds = new ArrayList<>();
        List<Radiogroup> flags = new ArrayList<>();
        this.miningAlgorithm = miningAlgorithm;
        this.bpmnMinerController = bpmnMinerController;

        switch( miningAlgorithm ) {
            case SelectMinerResult.SMPOS:
                windowPath = "zul/splitminer.zul";
                break;
            case SelectMinerResult.IMPOS:
                windowPath = "zul/inductive.zul";
                break;
            case SelectMinerResult.HMPOS6:
                windowPath = "zul/heuristics.zul";
                break;
            default:
                bpmnMinerController.setMiningSettings(null);
                bpmnMinerController.createCanditatesEntity();
                return;
        }

        getSettings();
    }

    protected void getSettings() {
        try {
            miningSettins = (Window) bpmnMinerController.portalContext.getUI().createComponent(getClass().getClassLoader(), windowPath, null, null);

            cancel = (Button) miningSettins.getFellow("settingsCancelButton");
            ok = (Button) miningSettins.getFellow("settingsOKButton");

            switch( miningAlgorithm ) {
                case SelectMinerResult.SMPOS:
                    miningSettins.setTitle("Split Miner Setup");
                    thresholds.add((Slider)miningSettins.getFellow("epsilonSM"));
                    thresholds.add((Slider)miningSettins.getFellow("etaSM"));
                    flags.add((Radiogroup)miningSettins.getFellow("replaceORsSM"));
                    break;
                case SelectMinerResult.IMPOS:
                    miningSettins.setTitle("Inductive Miner Setup");
                    thresholds.add((Slider)miningSettins.getFellow("noiseThresholdIMf"));
                    break;
                case SelectMinerResult.HMPOS6:
                    miningSettins.setTitle("Heuristics Miner 6.0 Setup");
                    thresholds.add((Slider)miningSettins.getFellow("dependencyThresholdHM6"));
                    thresholds.add((Slider)miningSettins.getFellow("L1lThresholdHM6"));
                    thresholds.add((Slider)miningSettins.getFellow("L2lThresholdHM6"));
                    thresholds.add((Slider)miningSettins.getFellow("longDepThresholdHM6"));
                    thresholds.add((Slider)miningSettins.getFellow("relativeToBestThresholdHM6"));
                    flags.add((Radiogroup)miningSettins.getFellow("allConnectedHM6"));
                    flags.add((Radiogroup)miningSettins.getFellow("longDependencyHM6"));
                    break;
                default:
                    return;
            }

            cancel.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    miningSettins.detach();
                    bpmnMinerController.setMiningSettings(null);
                    bpmnMinerController.createCanditatesEntity();
                }
            });
            ok.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    readSettings();
                }
            });

            miningSettins.doModal();
        } catch( Exception e ) {
            e.printStackTrace();
            miningSettins.detach();
            bpmnMinerController.setMiningSettings(null);
            bpmnMinerController.createCanditatesEntity();
        }
    }

    protected void readSettings() {
        Double tValue;
        Boolean fValue;
        params = new MiningSettings();

        miningSettins.detach();

        for( Slider s : thresholds ) {
            tValue = new Double(s.getCurpos()/100.0);
            params.setParam(s.getId(), tValue);
            System.out.println("DEBUG - setting : " + s.getId() + " = " + tValue);
        }

        for( Radiogroup r : flags ) {
            fValue = r.getSelectedIndex() == 0 ? true : false;
            params.setParam(r.getId(), fValue);
            System.out.println("DEBUG - setting : " + r.getId() + " = " + fValue);
        }

        bpmnMinerController.setMiningSettings(params);
        bpmnMinerController.createCanditatesEntity();
    }
}
