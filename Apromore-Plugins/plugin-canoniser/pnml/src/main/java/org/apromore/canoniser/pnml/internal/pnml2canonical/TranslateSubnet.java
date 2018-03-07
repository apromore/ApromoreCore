/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.pnml.internal.pnml2canonical;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.pnml.PnmlType;

public class TranslateSubnet {

    public TranslateSubnet(PnmlType pnml, org.apromore.pnml.NetType subnet, String filename, DataHandler data)
            throws CanoniserException {
        pnml = new PnmlType();
        pnml.getNet().add(subnet);
        RemoveDuplicateXORS.remove(pnml, data);
        main(pnml, subnet, data);

        CheckForSubnet check = new CheckForSubnet(pnml, filename, subnet, data);
    }

    private void main(PnmlType pnml, org.apromore.pnml.NetType subnet, DataHandler data) throws CanoniserException {
        data.setCanonicalProcess(new CanonicalProcessType());
        //TODO FM extensions
//        TypeAttribute att = new TypeAttribute();
//        att.setTypeRef("IntialFormat");
//        att.setValue("PNML");
//        data.getCanonicalProcess().getAttribute().add(att);

        if (pnml.getNet() != null && pnml.getNet().size() > 0) {
            for (int i = 0; i < pnml.getNet().size(); i++) {
                for (org.apromore.pnml.NetType pnet : pnml.getNet()) {
                    NetType net = new NetType();
                    data.setNet(net);
                    TranslatePetriNet.translatePetriNet(pnet, data);
                    data.put_id_map(String.valueOf(data.getNet().getId()), String.valueOf(data.getIds()));
                    data.setRootId(data.getIds());
                    data.getNet().setId(String.valueOf(data.nextId()));
                    data.getCanonicalProcess().getNet().add(data.getNet());
                    data.getCanonicalProcess().getRootIds().add(String.valueOf(data.getRootid()));

                }
            }
        }

    }

}
