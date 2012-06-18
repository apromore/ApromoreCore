package org.apromore.canoniser.adapters.pnml2canonical;

import org.apromore.anf.SimulationType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.WorkType;
import org.apromore.pnml.OrganizationUnitType;
import org.apromore.pnml.RoleType;
import org.apromore.pnml.TransitionToolspecificType;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslateTransitionToolspecifc {
    DataHandler data;
    long ids;
    long resid;
    String cpfId = null;
    Map<Object, String> units = new HashMap<Object, String>();
    Map<Object, String> roles = new HashMap<Object, String>();

    public void setValues(DataHandler data, long ids) {
        this.data = data;
        this.ids = ids;
    }

    public void translate(Object obj) {
        org.apromore.pnml.TransitionType element = (org.apromore.pnml.TransitionType) obj;
        List<TransitionToolspecificType> pnmlTransitionToolspecific = element.getToolspecific();

        SimulationType simu = new SimulationType();
        cpfId = data.get_id_map_value(element.getId());

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
                            if (!units .containsKey(((OrganizationUnitType) transitionToolspecific
                                .getTransitionResource().getOrganizationalUnitName()).getName() +
                                "-" + ((RoleType) transitionToolspecific.getTransitionResource().getRoleName()).getName())) {
                                    units.put(((OrganizationUnitType) transitionToolspecific.getTransitionResource()
                                        .getOrganizationalUnitName()).getName() + "-" + ((RoleType) transitionToolspecific
                                        .getTransitionResource().getRoleName()).getName(), String.valueOf(data.getResourceID()));
                                ResourceTypeType roleunit = new ResourceTypeType();
                                resid = data.getResourceID();
                                roleunit.setId(String.valueOf(resid++));
                                data.setResourceID(resid);
                                roleunit.setName(((OrganizationUnitType) transitionToolspecific.getTransitionResource().getOrganizationalUnitName()).getName()+ "-"
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
                                rtrunit.setResourceTypeId(units.get(((OrganizationUnitType) transitionToolspecific
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

    public long getIds() {
        return ids;
    }
}
