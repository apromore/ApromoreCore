package ee.ut.mining.log;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.collect.TreeMultimap;

public class AlphaRelations implements ConcurrencyRelations {
	private Table<String, String, Integer> directSuccession;
	private Multimap<String, String> parallelRelations;
	private Multimap<String, String> shortLoopRelation;

	public AlphaRelations(XLog log) {
		this.directSuccession = HashBasedTable.create();
		this.parallelRelations = TreeMultimap.create();
		this.shortLoopRelation = HashMultimap.create();
		
		for (XTrace t: log) {
			String prevLabel = null;
			String prevPrevLabel = null;
			for (XEvent e: t) {
				if (isCompleteEvent(e) && e.getAttributes().get(XConceptExtension.KEY_NAME) != null) {
					String label = getEventName(e);
					if (prevLabel != null) {
						Integer currentCount = directSuccession.get(prevLabel, label);
						directSuccession.put(prevLabel, label, currentCount == null ? 1 : currentCount + 1);
						
						if (prevPrevLabel != null && prevPrevLabel.equals(label)) {
							shortLoopRelation.put(prevLabel, label);
							shortLoopRelation.put(label, prevLabel);
						}
					}
					prevPrevLabel = prevLabel;
					prevLabel = label;
				}
			}
		}

		for (Cell<String, String, Integer> cell: directSuccession.cellSet())
			if (directSuccession.contains(cell.getColumnKey(), cell.getRowKey()) && !shortLoopRelation.containsEntry(cell.getRowKey(), cell.getColumnKey()))
				parallelRelations.put(cell.getRowKey(), cell.getColumnKey());
	}
	
	private String getEventName(XEvent e) {
		return e.getAttributes().get(XConceptExtension.KEY_NAME).toString();
	}

	private boolean isCompleteEvent(XEvent e) {
		XAttributeMap amap = e.getAttributes();
		if (amap.get(XLifecycleExtension.KEY_TRANSITION) != null)
			return (amap.get(XLifecycleExtension.KEY_TRANSITION).toString().toLowerCase().equals("complete"));
		else
			return false;
	}

	public Multimap<String, String> getConcurrency() {
		return parallelRelations;
	}

	public boolean areConcurrent(String label1, String label2) {
		return parallelRelations.containsEntry(label1, label2);
	}

}
