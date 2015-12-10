/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.pnml.internal.pnml2canonical;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.anf.SimulationType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.WorkType;
import org.apromore.pnml.OrganizationUnitType;
import org.apromore.pnml.RoleType;
import org.apromore.pnml.TransitionToolspecificType;

public abstract class TranslateTransitionToolspecific {

    static public void translate(Object obj, DataHandler data) {
        org.apromore.pnml.TransitionType element = (org.apromore.pnml.TransitionType) obj;
        List<TransitionToolspecificType> pnmlTransitionToolspecific = element.getToolspecific();

        SimulationType simu = new SimulationType();
        String cpfId = data.get_id_map_value(element.getId());

        if (element.getToolspecific() != null) {
            int countsimu = 0;
            for (Object obj2 : pnmlTransitionToolspecific) {
                if (obj2 instanceof TransitionToolspecificType) {
                    TransitionToolspecificType transitionToolspecific = (TransitionToolspecificType) obj2;
//
//                    if (transitionToolspecific.getTrigger() != null) {
//                    }
//                    if (transitionToolspecific.getOperator() != null) {
//                    }
//                    if (transitionToolspecific.isSubprocess() != null) {
//                    }
                    if (transitionToolspecific.getTransitionResource() != null) {
                        if (transitionToolspecific.getTransitionResource().getOrganizationalUnitName() != null) {
                            if (!data.units.containsKey(((OrganizationUnitType) transitionToolspecific
                                    .getTransitionResource().getOrganizationalUnitName()).getName() +
                                    "-" + ((RoleType) transitionToolspecific.getTransitionResource().getRoleName()).getName())) {
                                data.units.put(((OrganizationUnitType) transitionToolspecific.getTransitionResource()
                                        .getOrganizationalUnitName()).getName() + "-" + ((RoleType) transitionToolspecific
                                        .getTransitionResource().getRoleName()).getName(), String.valueOf(data.getResourceID()));
                                ResourceTypeType roleunit = new ResourceTypeType();
                                long resid = data.getResourceID();
                                roleunit.setId(String.valueOf(resid++));
                                data.setResourceID(resid);
                                roleunit.setName(((OrganizationUnitType) transitionToolspecific.getTransitionResource().getOrganizationalUnitName()).getName() + "-"
                                        + ((RoleType) transitionToolspecific.getTransitionResource().getRoleName()).getName());

                                data.getCanonicalProcess().getResourceType().add(roleunit);
                                data.put_resourcemap(
                                        ((OrganizationUnitType) transitionToolspecific
                                                .getTransitionResource()
                                                .getOrganizationalUnitName())
                                                .getName(), roleunit);
                                data.put_resourcemap(
                                        ((RoleType) transitionToolspecific
                                                .getTransitionResource()
                                                .getRoleName()).getName(),
                                        roleunit);
                            }

                            Object test = data.get_objectmap_value(cpfId);
                            if (test != null) {
                                ResourceTypeRefType rtrunit = new ResourceTypeRefType();
                                rtrunit.setResourceTypeId(data.units.get(((OrganizationUnitType) transitionToolspecific
                                        .getTransitionResource()
                                        .getOrganizationalUnitName()).getName()
                                        + "-"
                                        + ((RoleType) transitionToolspecific
                                        .getTransitionResource()
                                        .getRoleName()).getName()));
                                ((WorkType) test).getResourceTypeRef().add(rtrunit);
                            }
                        } else {
                            Object test = data.get_objectmap_value(cpfId);
                            if (test != null) {
                                ((WorkType) test).getResourceTypeRef().add(new ResourceTypeRefType());
                            }
                        }
                    }
                    if (transitionToolspecific.getTime() != null) {
                        simu.setTime(BigInteger.valueOf(Long.valueOf(transitionToolspecific.getTime())));
                        countsimu++;
                    }
                    if (transitionToolspecific.getTimeUnit() != null) {
                        simu.setTimeUnit(BigInteger.valueOf(Long.valueOf(transitionToolspecific.getTimeUnit())));
                        countsimu++;
                    }
//                    if (transitionToolspecific.getOrientation() != null) {
//                    }
//                    if (transitionToolspecific.getAssign() != null) {
//                    }
//                    if (transitionToolspecific.getInvoke() != null) {
//                    }
//                    if (transitionToolspecific.getReply() != null) {
//                    }
//                    if (transitionToolspecific.getWait() != null) {
//                    }
//                    if (transitionToolspecific.getReceive() != null) {
//                    }
//                    if (transitionToolspecific.getEmpty() != null) {
//                    }
                }
            }
            if (countsimu != 0) {
                simu.setCpfId(String.valueOf(Long.valueOf(cpfId)));
                data.getAnnotations().getAnnotation().add(simu);

            }

        }
    }

}
