package org.apromore.canoniser.adapters.canonical2pnml;

import org.apromore.cpf.NodeType;
import org.apromore.pnml.NodeNameType;
import org.apromore.pnml.OperatorType;
import org.apromore.pnml.TransitionToolspecificType;
import org.apromore.pnml.TransitionType;

import java.math.BigInteger;

public class TranslateANDSplitJoin {
	DataHandler data;
	long ids;

	public void setValues(DataHandler data, long ids) {
		this.data = data;
		this.ids = ids;
	}

	public void translate(NodeType node) {
		TransitionType tran = new TransitionType();
		TransitionToolspecificType trantool = new TransitionToolspecificType();
		OperatorType op = new OperatorType();
		NodeNameType test = new NodeNameType();
		data.put_id_map(node.getId(), String.valueOf(ids));
		test.setText(node.getName());
		tran.setId(String.valueOf(ids++));
		tran.setName(test);
		trantool.setTool("WoPeD");
		trantool.setVersion("1.0");
		String splitid[] = node.getOriginalID().split("_");
		op.setId(splitid[0]);
		op.setType(107);
		trantool.setOrientation(3);
		trantool.setOperator(op);
		tran.getToolspecific().add(trantool);
		data.getNet().getTransition().add(tran);
		data.put_pnmlRefMap(tran.getId(), tran);
		data.put_tempmap(tran.getName().getText(), tran);
		data.put_originalid_map(BigInteger.valueOf(Long.valueOf(tran.getId())),
				node.getOriginalID());
	}

	public long getIds() {
		return ids;
	}
}
