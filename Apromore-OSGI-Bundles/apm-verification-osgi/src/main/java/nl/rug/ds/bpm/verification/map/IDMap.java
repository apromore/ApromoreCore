package nl.rug.ds.bpm.verification.map;

import nl.rug.ds.bpm.verification.comparator.StringComparator;

import java.util.*;

/**
 * Created by p256867 on 4-4-2017.
 */
public class IDMap {
    private int n;
    private String ap;
    private TreeMap<String, String> idToAp, apToId;

    public IDMap() {
        ap = "n";
        n = 0;
        idToAp = new TreeMap<>(new StringComparator());
        apToId = new TreeMap<>(new StringComparator());
    }

    public IDMap(String apIdentifier) {
        this();
        ap = apIdentifier;
    }

    public IDMap(String apIdentifier, Map<String, String> idToAp, Map<String, String> apToId) {
        this(apIdentifier);
        this.idToAp.putAll(idToAp);
        this.apToId.putAll(apToId);
    }

    public synchronized void addID(String id) {
        if(!idToAp.containsKey(id)) {
            String nid = ap + n++;
            idToAp.put(id, nid);
            apToId.put(nid, id);
        }
    }

    public synchronized void addID(String id, String ap) {
        if(idToAp.containsKey(id))
            apToId.remove(idToAp.get(id));
        idToAp.put(id, ap);
        apToId.put(ap, id);
    }

    public String getAP(String id) {
        return idToAp.get(id);
    }

    public String getID(String ap) {
        return apToId.get(ap);
    }

    public Set<String> getIDKeys() { return idToAp.keySet(); }

    public Set<String> getAPKeys() { return  apToId.keySet(); }

    public Map<String, String> getIdToAp() { return idToAp; }

    public Map<String, String> getApToId() { return apToId; }
}
