package org.apromore.clustering.containment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.dao.FragmentVersionDagDao;
import org.apromore.dao.FragmentVersionDao;
import org.apromore.dao.ProcessModelVersionDao;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("ContainmentRelation")
public class ContainmentRelationImpl implements ContainmentRelation {

    private Map<String, Integer> idIndexMap = new HashMap<String, Integer>();
    private  Map<Integer, String> indexIdMap = new HashMap<Integer, String>();
    private List<String> idList = new ArrayList<String>();
    private Map<String, Integer> fragSize = new HashMap<String, Integer>();

    private List<String> rootIds = new ArrayList<String>();

    @Autowired @Qualifier("FragmentVersionDao")
    private FragmentVersionDao fDao;
    @Autowired @Qualifier("FragmentVersionDagDao")
    private FragmentVersionDagDao fdagDao;
    @Autowired @Qualifier("ProcessModelVersionDao")
    private ProcessModelVersionDao pmvDao;

    /* Mapping from root fragment Id -> Ids of all ascendant fragments of that root fragment */
    private Map<String, List<String>> hierarchies = new HashMap<String, List<String>>();
    private boolean[][] contmatrix;
    private int minSize = 3;


    /**
     * Public Constructor.
     */
    public ContainmentRelationImpl() {
    }

    /**
     * Get something.
     * @throws Exception if something fails
     */
    public void queryFragments() throws Exception {
        idIndexMap.clear();
        fragSize.clear();

        List<FragmentVersion> fs = fDao.getSimilarFragmentsBySize(minSize, 5000);
        for (FragmentVersion f : fs) {
            Integer index = idIndexMap.size();
            String id = f.getFragmentVersionId();
            idIndexMap.put(id, index);
            indexIdMap.put(index, id);
            idList.add(id);
            fragSize.put(id, f.getFragmentSize());
        }
    }

    /**
     * @throws Exception
     */
    public void initHierarchies() throws Exception {
        List<String> rootIds = queryRoots();
        System.out.println("Total roots: " + rootIds.size());

        for (String rootId : rootIds) {
            List<String> hierarchy = new ArrayList<String>();
            hierarchies.put(rootId, hierarchy);
            hierarchy.add(rootId);

            int rootIndex = getFragmentIndex(rootId);
            Collection<Integer> fragmentIndecies = indexIdMap.keySet();
            for (Integer fIndex : fragmentIndecies) {
                if (!fIndex.equals(rootIndex) && areInContainmentRelation(rootIndex, fIndex)) {
                    hierarchy.add(getFragmentId(fIndex));
                }
            }
        }
    }


    public List<String> queryRoots() throws Exception {
        rootIds = pmvDao.getRootFragments(minSize);
        return rootIds;
    }


    /**
     * something.
     * @param fid fragment Id
     * @param rootIds root ids
     * @param visitedFIds visited fragments
     */
    private void fillRoots(String fid, List<String> rootIds, Set<String> visitedFIds) throws Exception {
        if (!visitedFIds.contains(fid)) {
            visitedFIds.add(fid);

            List<FragmentVersion> parents = fDao.getParentFragments(fid);
            if (parents.isEmpty()) {
                rootIds.add(fid);
            }
            else {
                for (FragmentVersion parent : parents) {
                    fillRoots(parent.getFragmentVersionId(), rootIds,
                            visitedFIds);
                }
            }
        }
    }


    /**
     *
     * @throws Exception
     */
    public void initContainmentMatrix() throws Exception {
        List<FragmentVersionDag> dags = fdagDao.getAllDAGEntries(minSize);
        contmatrix = new boolean[idIndexMap.size()][idIndexMap.size()];

        // Initialize the containment matrix using the parent-child relation
        for (FragmentVersionDag fdag : dags) {
            contmatrix[idIndexMap.get(fdag.getId().getFragmentVersionId())][idIndexMap.get(fdag.getId().getChildFragmentVersionId())] = true;
        }

        // Compute the transitive closure (i.e., ancestor-descendant relation)
        for (int i = 0; i < contmatrix.length; i++) {
            for (int j = 0; j < contmatrix.length; j++) {
                if (contmatrix[j][i]) {
                    for (int k = 0; k < contmatrix.length; k++) {
                        contmatrix[j][k] = contmatrix[j][k] | contmatrix[i][k];
                    }
                }
            }
        }

        // Compute the symmetric relation
        for (int i = 0; i < contmatrix.length; i++) {
            for (int j = 0; j < contmatrix.length; j++) {
                if (contmatrix[i][j]) {
                    contmatrix[j][i] = true;
                }
            }
        }

        initHierarchies();
    }


    @Override
    public List<String> getRoots() {
        return rootIds;
    }


    @Override
    public List<String> getHierarchy(String rootFragmentId) {
        return hierarchies.get(rootFragmentId);
    }

    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }


    public int getNumberOfFragments() {
        return idIndexMap.size();
    }

    public String getFragmentId(int frag) {
        return indexIdMap.get(frag);
    }

    public Integer getFragmentIndex(String frag) {
        return idIndexMap.get(frag);
    }

    public boolean areInContainmentRelation(int frag1, int frag2) {
        return contmatrix[frag1][frag2];
    }

    public void initialize() throws Exception {
        queryFragments();
        initContainmentMatrix();
    }
}
