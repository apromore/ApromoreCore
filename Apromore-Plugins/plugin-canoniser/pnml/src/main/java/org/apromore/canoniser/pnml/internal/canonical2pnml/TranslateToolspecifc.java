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

package org.apromore.canoniser.pnml.internal.canonical2pnml;

import java.math.BigDecimal;

import org.apromore.anf.AnnotationsType;
import org.apromore.pnml.DimensionType;
import org.apromore.pnml.GraphicsSimpleType;
import org.apromore.pnml.NetToolspecificType;
import org.apromore.pnml.OrganizationUnitType;
import org.apromore.pnml.PositionType;
import org.apromore.pnml.ResourcesType;
import org.apromore.pnml.RoleType;
import org.apromore.pnml.SimulationsType;
import org.apromore.pnml.TPartnerLinks;
import org.apromore.pnml.TVariables;

public class TranslateToolspecifc {
    DataHandler data;
    long ids;

    public void setValues(DataHandler data, long ids) {
        this.data = data;
        this.ids = ids;
    }

    public void translate(AnnotationsType annotations) {

        NetToolspecificType def = new NetToolspecificType();
        PositionType pt = new PositionType();
        DimensionType dt = new DimensionType();
        GraphicsSimpleType gt = new GraphicsSimpleType();
        pt.setX(BigDecimal.valueOf(Long.valueOf(2)));
        pt.setY(BigDecimal.valueOf(Long.valueOf(25)));
        dt.setX(BigDecimal.valueOf(Long.valueOf(779)));
        dt.setY(BigDecimal.valueOf(Long.valueOf(527)));
        gt.setPosition(pt);
        gt.setDimension(dt);

        def.setTool("WoPeD");
        def.setVersion("1.0");
        def.setBounds(gt);
        def.setTreeWidth(1);
        def.setVerticalLayout(false);
        if (data.getunit().size() > 0 && data.getroles().size() > 0) {
            ResourcesType rt = new ResourcesType();
            for (String role : data.getroles()) {
                RoleType ro = new RoleType();
                ro.setName(role);
                rt.getRoleOrOrganizationUnit().add(ro);
            }
            for (String unit : data.getunit()) {
                OrganizationUnitType ui = new OrganizationUnitType();
                ui.setName(unit);
                rt.getRoleOrOrganizationUnit().add(ui);
            }
            def.setResources(rt);

        } else {
            def.setResources(new ResourcesType());
        }

        def.setSimulations(new SimulationsType());
        def.setPartnerLinks(new TPartnerLinks());
        def.setVariables(new TVariables());

        data.getNet().getToolspecific().add(def);

    }

    public long getIds() {
        return ids;
    }
}
