/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.NetType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.pnml.PNMLSchema;
import org.apromore.pnml.PnmlType;

public class PNML2Canonical {

    private long ids = 0; //System.currentTimeMillis();

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
     * @param filename may be <code>null</code>
     * @param isCpfTaskPnmlTransition plugin parameter
     * @param isCpfEdgePnmlPlace plugin parameter
     * @throws CanoniserException
     * @since 1.0
     */
    /*
    public PNML2Canonical(PnmlType pnml) throws CanoniserException {
        removeDuplicatexors.remove(pnml, data);
        main(pnml);
    }
    */
    public PNML2Canonical(final PnmlType pnml,
                          final String   filename,
                          final boolean  isCpfTaskPnmlTransition,
                          final boolean  isCpfEdgePnmlPlace) throws CanoniserException {

        removeDuplicatexors.remove(pnml, data);
        main(pnml, isCpfTaskPnmlTransition, isCpfEdgePnmlPlace);
        if (filename != null) {
            new CheckForSubnet(pnml, filename, data.getRootid());
        }
    }

    public PNML2Canonical(final PnmlType pnml,
                          final long     id,
                          final boolean  isCpfTaskPnmlTransition,
                          final boolean  isCpfEdgePnmlPlace) throws CanoniserException {
        this.ids = id;
        main(pnml, isCpfTaskPnmlTransition, isCpfEdgePnmlPlace);
    }

    void main(final PnmlType pnml,
              final boolean  isCpfTaskPnmlTransition,
              final boolean  isCpfEdgePnmlPlace) throws CanoniserException {

        CanonicalProcessType cpf = CpfObjectFactory.getInstance().createCanonicalProcessType();
        cpf.setName("dummy");
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

    public static void main(String[] arg) throws Exception {
        PnmlType pnml = PNMLSchema.unmarshalPNMLFormat(System.in, true).getValue();
        CanonicalProcessType cpf = new PNML2Canonical(pnml, null, false, false).getCPF();
        CPFSchema.marshalCanonicalFormat(System.out, cpf, true);
    }
}
