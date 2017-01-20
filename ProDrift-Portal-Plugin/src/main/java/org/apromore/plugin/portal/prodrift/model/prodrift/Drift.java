package org.apromore.plugin.portal.prodrift.model.prodrift;

import org.zkoss.zul.ListModel;


/**
 * Created by n9348531 on 17/01/2017.
 */
public class Drift implements Comparable<Drift>{

    private long driftPoint;
    private String driftStatement;
    private ListModel<String> characterizationStatements;

    public Drift(){

    }

    public long getDriftPoint() {
        return driftPoint;
    }

    public void setDriftPoint(long driftPoint) {
        this.driftPoint = driftPoint;
    }

    public String getDriftStatement() {
        return driftStatement;
    }

    public void setDriftStatement(String driftStatement) {
        this.driftStatement = driftStatement;
    }

    public ListModel<String> getCharacterizationStatements() {
        return characterizationStatements;
    }

    public void setCharacterizationStatements(ListModel<String> characterizationStatements) {
        this.characterizationStatements = characterizationStatements;
    }

    @Override
    public int compareTo(Drift o) {
        return Long.compare(this.driftPoint, o.getDriftPoint());
    }
}
