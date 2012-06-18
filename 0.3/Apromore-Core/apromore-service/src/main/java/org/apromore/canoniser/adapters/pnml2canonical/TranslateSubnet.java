package org.apromore.canoniser.adapters.pnml2canonical;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.exception.CanoniserException;
import org.apromore.pnml.PnmlType;

public class TranslateSubnet {
	private long ids = 6121980;
	DataHandler data = new DataHandler();
	TranslatePetriNet tpn = new TranslatePetriNet();
	RemoveDuplicateXORS xors = new RemoveDuplicateXORS();


	public CanonicalProcessType getCPF() {
		return data.getCanonicalProcess();
	}

	public AnnotationsType getANF() {
		return data.getAnnotations();
	}

	public TranslateSubnet(PnmlType pnml, org.apromore.pnml.NetType subnet, long id, String filename)
            throws CanoniserException {
		pnml = new PnmlType();
		pnml.getNet().add(subnet);
		xors.remove(pnml, data);
		main(pnml, subnet, id);

		CheckForSubnet check = new CheckForSubnet(pnml, filename, data.getRootid(), subnet);
	}

	void main(PnmlType pnml, org.apromore.pnml.NetType subnet, long id) throws CanoniserException {
		data.setCanonicalProcess(new CanonicalProcessType());
		TypeAttribute att = new TypeAttribute();
		att.setTypeRef("IntialFormat");
		att.setValue("PNML");
		data.getCanonicalProcess().getAttribute().add(att);

		if (pnml.getNet() != null && pnml.getNet().size() > 0) {
			for (int i = 0; i < pnml.getNet().size(); i++) {
				for (org.apromore.pnml.NetType pnet : pnml.getNet()) {
					NetType net = new NetType();
					data.setNet(net);
					tpn.setValues(data, ids);
					tpn.translatePetriNet(pnet);
					ids = tpn.getIds();
					data.put_id_map(String.valueOf(data.getNet().getId()), String.valueOf(ids));
					data.setRootId(ids);
					data.getNet().setId(String.valueOf(ids++));
					data.getCanonicalProcess().getNet().add(data.getNet());
					data.getCanonicalProcess().setRootId(String.valueOf(id));

				}
			}
		}

	}

}
