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

import java.util.HashMap;
import java.util.Map;

import org.apromore.cpf.HumanType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.pnml.NetToolspecificType;
import org.apromore.pnml.OrganizationUnitType;
import org.apromore.pnml.ResourceMappingType;
import org.apromore.pnml.ResourceType;
import org.apromore.pnml.ResourcesType;
import org.apromore.pnml.RoleType;

public abstract class TranslateHumanResources {

    static public void translate(org.apromore.pnml.NetType pnet, DataHandler data) {
        Map<String, HumanType> hmap = new HashMap<String, HumanType>();

        for (Object obj : pnet.getToolspecific()) {
            if (obj instanceof NetToolspecificType) {
                NetToolspecificType ts = (NetToolspecificType) obj;
                if (ts.getResources() != null) {
                    ResourcesType rs = ts.getResources();
                    if (rs.getResource().size() > 0) {
                        for (Object human : rs.getResource()) {
                            ResourceType humanres = (ResourceType) human;
                            HumanType ht = new HumanType();
                            ht.setName(humanres.getName());
                            long resid = data.getResourceID();
                            ht.setId(String.valueOf(resid++));
                            data.setResourceID(resid);
                            hmap.put(ht.getName(), ht);
                            data.getCanonicalProcess().getResourceType()
                                    .add(ht);
                        }
                        for (Object mapres : rs.getResourceMapping()) {
                            ResourceMappingType rmap = (ResourceMappingType) mapres;
                            if (rmap.getResourceClass() instanceof OrganizationUnitType) {
                                if (data.get_resourcemap().containsKey(
                                        ((OrganizationUnitType) rmap
                                                .getResourceClass()).getName())
                                        && hmap.containsKey(((ResourceType) rmap
                                        .getResourceID()).getName())) {
                                    ResourceTypeType spez = data
                                            .get_resourcemap_value(((OrganizationUnitType) rmap
                                                    .getResourceClass())
                                                    .getName());
                                    HumanType ht = hmap
                                            .get(((ResourceType) rmap
                                                    .getResourceID()).getName());
                                    ht.setName(ht.getName()
                                            + "-"
                                            + ((OrganizationUnitType) rmap
                                            .getResourceClass())
                                            .getName());
                                    if (!spez.getSpecializationIds().contains(
                                            ht.getId())) {
                                        spez.getSpecializationIds().add(
                                                ht.getId());
                                    }
                                }
                            } else if (rmap.getResourceClass() instanceof RoleType) {
                                if (data.get_resourcemap().containsKey(
                                        ((RoleType) rmap.getResourceClass())
                                                .getName())
                                        && hmap.containsKey(((ResourceType) rmap
                                        .getResourceID()).getName())) {
                                    ResourceTypeType spez = data
                                            .get_resourcemap_value(((RoleType) rmap
                                                    .getResourceClass())
                                                    .getName());
                                    HumanType ht = hmap
                                            .get(((ResourceType) rmap
                                                    .getResourceID()).getName());
                                    ht.setName(ht.getName()
                                            + "-"
                                            + ((RoleType) rmap
                                            .getResourceClass())
                                            .getName());
                                    if (!spez.getSpecializationIds().contains(
                                            ht.getId())) {
                                        spez.getSpecializationIds().add(
                                                ht.getId());
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }

    }

}
