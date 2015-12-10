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

package org.apromore.canoniser.pnml.internal.canonical2pnml;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.HumanType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.pnml.NetToolspecificType;
import org.apromore.pnml.OrganizationUnitType;
import org.apromore.pnml.ResourceMappingType;
import org.apromore.pnml.ResourceType;
import org.apromore.pnml.RoleType;

public class TranslateHumanResources {
    DataHandler data;
    long ids;
    List<ResourceType> resources = new LinkedList<ResourceType>();
    List<ResourceMappingType> resourcesmappings = new LinkedList<ResourceMappingType>();
    Map<String, OrganizationUnitType> units = new HashMap<String, OrganizationUnitType>();
    Map<String, RoleType> roles = new HashMap<String, RoleType>();

    public void setValues(DataHandler data, long ids) {
        this.data = data;
        this.ids = ids;
    }

    public void translate(CanonicalProcessType cproc) {
        for (NetToolspecificType ntt : data.getNet().getToolspecific()) {
            NetToolspecificType toolspec = ntt;
            for (Object ru : toolspec.getResources()
                    .getRoleOrOrganizationUnit()) {
                if (ru instanceof OrganizationUnitType) {
                    units.put(((OrganizationUnitType) ru).getName(),
                            (OrganizationUnitType) ru);
                }
                if (ru instanceof RoleType) {
                    roles.put(((RoleType) ru).getName(), (RoleType) ru);
                }

            }

        }

        for (ResourceTypeType res : cproc.getResourceType()) {
            if (res instanceof HumanType) {

                String[] split = res.getName().split("-");
                String name = split[0].substring(0);
                ResourceType rt = new ResourceType();
                rt.setName(name);
                resources.add(rt);
                for (int i = 0; i < split.length; i++) {
                    if (i != 0) {
                        String work = split[i];
                        ResourceMappingType rmt = new ResourceMappingType();
                        if (units.containsKey(work)) {
                            rmt.setResourceClass(units.get(work));
                        } else if (roles.containsKey(work)) {
                            rmt.setResourceClass(roles.get(work));
                        }

                        rmt.setResourceID(rt);
                        resourcesmappings.add(rmt);
                    }
                }
            }
        }
        for (NetToolspecificType ntt : data.getNet().getToolspecific()) {
            NetToolspecificType toolspec = ntt;
            if (resources.size() > 0) {
                for (ResourceType rt : resources) {
                    toolspec.getResources().getResource().add(rt);
                }
                for (ResourceMappingType rmt : resourcesmappings) {
                    toolspec.getResources().getResourceMapping().add(rmt);
                }
            }
        }

    }

    public long getIds() {
        return ids;
    }
}
