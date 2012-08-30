/**
 * PNML2Canonical is a class for converting an PnmlType object
 * into a CanonicalProcessType object.
 * A PNML2Canonical object encapsulates the state of the two main
 * components resulted from the canonization process.  This
 * state information includes:
 * <ul>
 * <li>CanonicalProcessType object
 * <li>AnnotationsType object
 * </ul>
 * <p>
 *
 * @author Martin Snger, Niko Waldow
 * @version     %I%, %G%
 * @since 3.0
 */

package org.apromore.canoniser.adapters;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.adapters.pnml2canonical.CheckForSubnet;
import org.apromore.canoniser.adapters.pnml2canonical.DataHandler;
import org.apromore.canoniser.adapters.pnml2canonical.RemoveDuplicateXORS;
import org.apromore.canoniser.adapters.pnml2canonical.TranslatePetriNet;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.exception.CanoniserException;
import org.apromore.pnml.PnmlType;

public class PNML2Canonical {

    private long ids = System.currentTimeMillis(); // Change to 6121980 when
    // testing
    //private long ids = 6121980;
    DataHandler data = new DataHandler();
    TranslatePetriNet tpn = new TranslatePetriNet();
    RemoveDuplicateXORS removeDuplicatexors = new RemoveDuplicateXORS();


    public CanonicalProcessType getCPF() {
        return data.getCanonicalProcess();
    }

    public AnnotationsType getANF() {
        return data.getAnnotations();
    }

    /**
     * The constructor receives the header then does the canonization process in
     * order to allow the user to retrieve the produced process again into the
     * canonical format. The user also will be able to retrieve the annotation
     * element which stores the annotation data for the canonized model isolated
     * from the process flow.
     * <p/>
     *
     * @param pnml the header for an PNML (Petri Net Markup Language) which is
     *             file format for PNML diagrams.
     * @throws CanoniserException
     * @since 1.0
     */
    public PNML2Canonical(PnmlType pnml) throws CanoniserException {
        removeDuplicatexors.remove(pnml, data);
        main(pnml);
    }

    public PNML2Canonical(PnmlType pnml, String filename) throws CanoniserException {
        removeDuplicatexors.remove(pnml, data);
        main(pnml);
        new CheckForSubnet(pnml, filename, data.getRootid());
    }

    public PNML2Canonical(PnmlType pnml, long id) throws CanoniserException {
        this.ids = id;
        main(pnml);
    }

    void main(PnmlType pnml) throws CanoniserException {
        data.setCanonicalProcess(new CanonicalProcessType());
        TypeAttribute att = new TypeAttribute();
        att.setTypeRef("IntialFormat");
        att.setValue("PNML");
        data.getCanonicalProcess().getAttribute().add(att);

        if (pnml.getNet() != null && pnml.getNet().size() > 0) {
            for (int i = 0; i < pnml.getNet().size(); i++) {
                for (org.apromore.pnml.NetType pnet : pnml.getNet()) {
                    NetType net = new NetType();
                    data.setNet(net);
                    tpn.setValues(data, ids);
                    tpn.translatePetriNet(pnet);
                    ids = tpn.getIds();
                    data.put_id_map(pnet.getId(), String.valueOf(ids));
                    data.setRootId(ids);
                    data.getNet().setId(String.valueOf(ids++));

                    // data.getCanonicalProcess().setVersion(pnet.getType());
                    data.getCanonicalProcess().getNet().add(data.getNet());
                }
            }
        }
    }

}
