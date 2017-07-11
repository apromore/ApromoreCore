package nl.rug.ds.bpm.verification.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 07-Apr-17.
 */
public class GroupMap {
	private HashMap<String, Set<String>> groups;
	
	public GroupMap() {
		groups = new HashMap<>();
	}
	
	public void addGroup(String group) {
		Set<String> members = new HashSet<>();
		groups.put(group, members);
	}
	
	public void addToGroup(String group, String member) {
		groups.get(group).add(member);
	}
	
	public Set<String> getMembers(String group) {
		return groups.get(group);
	}

	public Set<String> keySet() { return groups.keySet(); }
	
	public String toString(String group) {
		String sb = "";
		Set<String> members = groups.get(group);
		if(members.size() == 1) {
			sb = members.iterator().next();
		}
		else if(members.size() > 1) {
			Iterator<String> iterator = members.iterator();
			sb = iterator.next();
			while (iterator.hasNext()) {
				sb = "(" + sb + " | " + iterator.next() + ")";
			}
		}
		//else empty
		return sb.toString();
	}
}
