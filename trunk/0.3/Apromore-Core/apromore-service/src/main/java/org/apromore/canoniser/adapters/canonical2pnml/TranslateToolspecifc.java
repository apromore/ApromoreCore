package org.apromore.canoniser.adapters.canonical2pnml;

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
