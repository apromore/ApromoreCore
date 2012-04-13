package org.apromore.canoniser.adapters.pnml2canonical;

import org.apromore.pnml.TransitionToolspecificType;
import org.apromore.pnml.TransitionType;

public class TranslateOperations {
	DataHandler data;
	long ids;

	public void setValues(DataHandler data, long ids) {
		this.data = data;
		this.ids = ids;
	}

	public void translateOperation(TransitionToolspecificType tran,
			TransitionType tra) {

		TranslateJoins tj = new TranslateJoins();
		TranslateSplits ts = new TranslateSplits();
		TranslateSplitJoins tsj = new TranslateSplitJoins();
		data.put_id_map(tra.getId(), String.valueOf(ids));

		if (tran == null) {

			if (data.gettargetvalues().contains(tra.getId())
					&& !data.getsourcevalues().contains(tra.getId())) {

				ts.setValues(data, ids);
				ts.translateAndSplits(tra);
				ids = ts.getIds();
			} else if (data.getsourcevalues().contains(tra.getId())
					&& !data.gettargetvalues().contains(tra.getId())) {
				tj.setValues(data, ids);
				tj.translateAndJoins(tra);
				ids = tj.getIds();
			} else if (data.getsourcevalues().contains(tra.getId())
					&& data.gettargetvalues().contains(tra.getId())) {
				tsj.setValues(data, ids);
				tsj.translateAndSplitJoins(tra);
				ids = tsj.getIds();

			}
		} else {

			if (tran.getOperator().getType() == 101) {
				ts.setValues(data, ids);
				ts.translateAndSplits(tra);
				ids = ts.getIds();
			} else if (tran.getOperator().getType() == 102) {
				tj.setValues(data, ids);
				tj.translateAndJoins(tra);
				ids = tj.getIds();
			} else if (tran.getOperator().getType() == 104) {
				ts.setValues(data, ids);
				ts.translateXorSplits(tra);
				ids = ts.getIds();
			} else if (tran.getOperator().getType() == 105) {
				tj.setValues(data, ids);
				tj.translateXorJoins(tra);
				ids = tj.getIds();
			} else if (tran.getOperator().getType() == 106) {
				tsj.setValues(data, ids);
				tsj.translateXorSplitJoins(tra);
				ids = tsj.getIds();
			} else if (tran.getOperator().getType() == 107) {
				tsj.setValues(data, ids);
				tsj.translateAndSplitJoins(tra);
				ids = tsj.getIds();
			} else if (tran.getOperator().getType() == 108) {
				tsj.setValues(data, ids);
				tsj.translateAndJoinXorSplit(tra);
				ids = tsj.getIds();
			} else if (tran.getOperator().getType() == 109) {
				tsj.setValues(data, ids);
				tsj.translateXorJoinAndSplit(tra);
				ids = tsj.getIds();
			}

		}
	}

	public long getIds() {
		return ids;
	}

}
