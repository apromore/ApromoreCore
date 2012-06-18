package org.apromore.canoniser.adapters.canonical2pnml;

import org.apromore.cpf.MessageType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.pnml.DimensionType;
import org.apromore.pnml.GraphicsSimpleType;
import org.apromore.pnml.NodeNameType;
import org.apromore.pnml.OrganizationUnitType;
import org.apromore.pnml.PlaceType;
import org.apromore.pnml.RoleType;
import org.apromore.pnml.TransitionResourceType;
import org.apromore.pnml.TransitionToolspecificType;
import org.apromore.pnml.TransitionType;
import org.apromore.pnml.TriggerType;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TranslateNode {

	DataHandler data;
	long ids;

	public void setValues(DataHandler data, long ids) {
		this.data = data;
		this.ids = ids;

	}

	public void translateTask(TaskType task) {
		TransitionToolspecificType trantool = new TransitionToolspecificType();
		TransitionToolspecificType ttt = new TransitionToolspecificType();
		TransitionType tran = new TransitionType();
		boolean isSub = false;
		NodeNameType test = new NodeNameType();
		data.put_id_map(task.getId(), String.valueOf(ids));
		tran.setId(String.valueOf(ids++));
		if (task.getName() != null) {
			test.setText(task.getName());
			tran.setName(test);
		}
		if (task.getSubnetId() != null) {
			isSub = true;
			data.setSubnet(tran);
		}
		if (data.get_triggermap().containsKey(tran.getName().getText())) {
			trantool.setTool("WoPeD");
			trantool.setVersion("1.0");
			trantool.setTrigger((TransitionToolspecificType.Trigger) data.get_triggermap_value(tran.getName().getText()));
			if (isSub) {
				trantool.setSubprocess(true);
			}
			tran.getToolspecific().add(trantool);
		}

        if (task.getResourceTypeRef().size() > 0) {
			ttt.setTool("WoPeD");
			ttt.setVersion("1.0");
			TriggerType tt = new TriggerType();
			GraphicsSimpleType gt = new GraphicsSimpleType();
			DimensionType dt = new DimensionType();
			dt.setX(BigDecimal.valueOf(Long.valueOf(24)));
			dt.setY(BigDecimal.valueOf(Long.valueOf(22)));
			gt.setDimension(dt);
			tt.setId("");
			tt.setType(200);
			tt.setGraphics(gt);
			data.put_triggermap(task.getName(), tt);
			ttt.setTrigger((TransitionToolspecificType.Trigger) tt);
			for (Object resource : task.getResourceTypeRef()) {
				if (resource instanceof ResourceTypeRefType) {
					if (((ResourceTypeRefType) resource).getResourceTypeId() != null) {
						if (data.get_resourcemap().containsKey(
								String.valueOf(((ResourceTypeRefType) resource).getResourceTypeId()))) {
							ResourceTypeType res = data
									.get_resourcemap_value(String
											.valueOf(((ResourceTypeRefType) resource).getResourceTypeId()));
							String[] split = res.getName().split("-");
							split[0] = split[0].substring(0);
							split[1] = split[1].substring(0);

							OrganizationUnitType ui = new OrganizationUnitType();
							ui.setName(split[0]);
							RoleType ro = new RoleType();
							ro.setName(split[1]);
							TransitionResourceType tres = new TransitionResourceType();
							tres.setOrganizationalUnitName(ui);
							tres.setRoleName(ro);
							GraphicsSimpleType gtres = new GraphicsSimpleType();
							DimensionType dtres = new DimensionType();
							dtres.setX(BigDecimal.valueOf(Long.valueOf(60)));
							dtres.setY(BigDecimal.valueOf(Long.valueOf(22)));
							gtres.setDimension(dt);
							tres.setGraphics(gtres);
							ttt.setTransitionResource((TransitionToolspecificType.TransitionResource) tres);
							data.put_resourcepositionmap(task.getName(), tres);
							if (!data.getunit().contains(split[0])) {
								data.addunit(split[0]);
							}
							if (!data.getroles().contains(split[1])) {
								data.addroles(split[1]);
							}
						}
					}
				}
			}
			if (isSub) {
				trantool.setSubprocess(true);
			}
			tran.getToolspecific().add(ttt);

		}
		if (trantool.getTool() == null && ttt.getTool() == null && isSub) {
			TransitionToolspecificType sub = new TransitionToolspecificType();
			sub.setTool("WoPeD");
			sub.setVersion("1.0");
			sub.setSubprocess(true);
			tran.getToolspecific().add(sub);
		}

		data.getNet().getTransition().add(tran);
		data.put_pnmlRefMap(tran.getId(), tran);
		if (task.getName() != null) {
			data.put_tempmap(tran.getName().getText(), tran);
		}
		data.put_originalid_map(BigInteger.valueOf(Long.valueOf(tran.getId())),
				task.getOriginalID());

	}

	public void translateEvent(NodeType node) {
		if (node instanceof MessageType) {
			TransitionType tran = new TransitionType();
			NodeNameType test = new NodeNameType();
			data.put_id_map(node.getId(), String.valueOf(ids));
			tran.setId(String.valueOf(ids++));
			if (node.getName() != null) {
				test.setText(node.getName());
				tran.setName(test);
			}
			TransitionToolspecificType ttt = new TransitionToolspecificType();
			ttt.setTool("WoPeD");
			ttt.setVersion("1.0");
			TriggerType tt = new TriggerType();
			GraphicsSimpleType gt = new GraphicsSimpleType();
			DimensionType dt = new DimensionType();
			dt.setX(BigDecimal.valueOf(Long.valueOf(24)));
			dt.setY(BigDecimal.valueOf(Long.valueOf(22)));
			gt.setDimension(dt);
			tt.setId("");
			tt.setType(201);
			tt.setGraphics(gt);
			data.put_triggermap(node.getName(), tt);
			ttt.setTrigger((TransitionToolspecificType.Trigger) tt);
			tran.getToolspecific().add(ttt);
			data.getNet().getTransition().add(tran);
			data.put_pnmlRefMap(tran.getId(), tran);
			if (node.getName() != null) {
				data.put_tempmap(tran.getName().getText(), tran);
			}
			data.put_originalid_map(
					BigInteger.valueOf(Long.valueOf(tran.getId())),
					node.getOriginalID());

		} else if (node instanceof TimerType) {
			TransitionType tran = new TransitionType();
			NodeNameType test = new NodeNameType();
			data.put_id_map(node.getId(), String.valueOf(ids));
			tran.setId(String.valueOf(ids++));
			if (node.getName() != null) {
				test.setText(node.getName());
				tran.setName(test);
			}
			TransitionToolspecificType ttt = new TransitionToolspecificType();
			ttt.setTool("WoPeD");
			ttt.setVersion("1.0");
			TriggerType tt = new TriggerType();
			GraphicsSimpleType gt = new GraphicsSimpleType();
			DimensionType dt = new DimensionType();
			dt.setX(BigDecimal.valueOf(Long.valueOf(24)));
			dt.setY(BigDecimal.valueOf(Long.valueOf(22)));
			gt.setDimension(dt);
			tt.setId("");
			tt.setType(202);
			tt.setGraphics(gt);
			data.put_triggermap(node.getName(), tt);
			ttt.setTrigger((TransitionToolspecificType.Trigger) tt);
			tran.getToolspecific().add(ttt);
			data.getNet().getTransition().add(tran);
			data.put_pnmlRefMap(tran.getId(), tran);
			if (node.getName() != null) {
				data.put_tempmap(tran.getName().getText(), tran);
			}
			data.put_originalid_map(BigInteger.valueOf(Long.valueOf(tran.getId())), node.getOriginalID());

		} else {
			PlaceType place = new PlaceType();
			NodeNameType test = new NodeNameType();
			data.put_id_map(node.getId(), String.valueOf(ids));
			place.setId(String.valueOf(ids++));
			if (node.getName() != null) {
				test.setText(node.getName());
				place.setName(test);
			}
			data.getNet().getPlace().add(place);
			data.put_pnmlRefMap(place.getId(), place);
			if (node.getName() != null) {
				data.put_tempmap(place.getName().getText(), place);
			}
			data.put_originalid_map(
					BigInteger.valueOf(Long.valueOf(place.getId())),
					node.getOriginalID());
		}
	}

	public void translateState(NodeType node) {

		PlaceType place = new PlaceType();
		NodeNameType test = new NodeNameType();
		data.put_id_map(node.getId(), String.valueOf(ids));
		place.setId(String.valueOf(ids++));
		if (node.getName() != null) {
			test.setText(node.getName());
			place.setName(test);
		}
		data.getNet().getPlace().add(place);
		data.put_pnmlRefMap(place.getId(), place);
		if (node.getName() != null) {
			data.put_tempmap(place.getName().getText(), place);
		}
		data.put_originalid_map(
				BigInteger.valueOf(Long.valueOf(place.getId())),
				node.getOriginalID());

	}

	public long getIds() {
		return ids;
	}

}
