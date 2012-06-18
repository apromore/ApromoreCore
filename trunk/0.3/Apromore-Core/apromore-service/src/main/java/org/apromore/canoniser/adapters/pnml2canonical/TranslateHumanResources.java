package org.apromore.canoniser.adapters.pnml2canonical;

import org.apromore.cpf.HumanType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.pnml.NetToolspecificType;
import org.apromore.pnml.OrganizationUnitType;
import org.apromore.pnml.ResourceMappingType;
import org.apromore.pnml.ResourceType;
import org.apromore.pnml.ResourcesType;
import org.apromore.pnml.RoleType;

import java.util.HashMap;
import java.util.Map;

public class TranslateHumanResources {
	DataHandler data;
	long ids;
	long resid;
	String cpfId = null;
	Map<String, HumanType> hmap = new HashMap<String, HumanType>();

	public void setValues(DataHandler data, long ids) {
		this.data = data;
		this.ids = ids;
	}

	public void translate(org.apromore.pnml.NetType pnet) {
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
							resid = data.getResourceID();
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

	public long getIds() {
		return ids;
	}
}
