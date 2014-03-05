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

package org.apromore.canoniser.pnml.internal;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.pnml.internal.pnml2canonical.CheckForSubnet;
import org.apromore.canoniser.pnml.internal.pnml2canonical.DataHandler;
import org.apromore.canoniser.pnml.internal.pnml2canonical.RemoveDuplicateXORS;
import org.apromore.canoniser.pnml.internal.pnml2canonical.TranslatePetriNet;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.CpfObjectFactory;
import org.apromore.cpf.NetType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.pnml.PnmlType;

public class PNML2Canonical {

    private long ids = System.currentTimeMillis();

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

        CanonicalProcessType cpf = CpfObjectFactory.getInstance().createCanonicalProcessType();
        cpf.setUri("");
        cpf.setVersion("1.0");
        data.setCanonicalProcess(cpf);

        if (pnml.getNet() != null && pnml.getNet().size() > 0) {
            for (org.apromore.pnml.NetType pnet : pnml.getNet()) {
                NetType net = CpfObjectFactory.getInstance().createNetType();
                data.setNet(net);
                tpn.setValues(data, ids);
                tpn.translatePetriNet(pnet);
                ids = tpn.getIds();
                data.put_id_map(pnet.getId(), String.valueOf(ids));
                data.setRootId(ids);
                data.getNet().setId(String.valueOf(ids++));
                data.getCanonicalProcess().getNet().add(data.getNet());
            }
        }
    }

}
