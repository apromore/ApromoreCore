package org.apromore.dao;

/**
 * Interface to define all the named queries names in the Persistence setup.
 * <p/>
 * The format of the names are define as [ENTITY].[QUERY NAME]
 * The Entity could be empty if the query uses joins and doesn't relate to a single entity.
 *
 * @author Cameron James
 */
public interface NamedQueries {

    public static final String GET_ALL_CLUSTERS = "cluster.getAllClusters";
    public static final String GET_CLUSTER_BY_ID = "cluster.getClusterById";
    public static final String GET_FILTERED_CLUSTERS = "cluster.getFilteredClusters";
    public static final String GET_CLUSTERING_SUMMARY = "clusteringSummary.getClusteringSummary";
    public static final String GET_FRAGMENTIDS_OF_CLUSTER = "clusterAssignment.getFragmentIdsOfCluster";
    public static final String GET_FRAGMENTS_OF_CLUSTER = "clusterAssignment.getFragmentsOfCluster";
    public static final String GET_FRAGMENT_DISTANCE = "fragmentDistance.getDistance";
    public static final String GET_DISTANCES_BELOW_THRESHOLD = "fragmentDistance.getDistancesBelowThreshold";
    public static final String GET_UNPROCESSED_FRAGMENTS = "fragmentVersion.getUnprocessedFragments";
    public static final String GET_UNPROCESSED_FRAGMENTS_OF_PROCESSES = "fragmentVersion.getFragmentsOfProcesses";

    public static final String GET_ANNOTATION = "annotation.getAnnotation";
    public static final String GET_ANNOTATION_BY_URI = "annotation.getAnnotationByUrl";

    public static final String GET_CONTENT_BY_FRAGMENT_VERSION = "content.getContentByFragmentVersion";
    public static final String GET_CONTENT_BY_HASH = "content.getContentByHash";

    public static final String GET_EDGES_BY_CONTENT = "edge.getEdgesByContentId";
    public static final String GET_STORED_EDGES = "edge.getStoredEdges";

    public static final String GET_FRAGMENT_VERSION = "fragmentVersion.getFragmentVersion";
    public static final String GET_ALL_FRAGMENT_VERSION = "fragmentVersion.getAllFragmentVersion";
    public static final String GET_FRAGMENT_BY_CONTENT_MAPPING = "fragmentVersion.getFragmentVersionByContentIdMappingCode";
    public static final String GET_USED_PROCESS_MODEL_FOR_FRAGMENT = "fragmentVersion.getUsedProcessModelForFragment";
    public static final String GET_LOCKED_PARENT_FRAGMENTS = "fragmentVersion.getLockedParentFragmentIds";
    public static final String GET_CHILD_FRAGMENTS_WITH_SIZE = "fragmentVersion.getChildFragmentsWithSize";
    public static final String GET_CHILD_FRAGMENTS_WITH_TYPE = "fragmentVersion.getChildFragmentsWithType";
    public static final String GET_FRAGMENT_DATA = "fragmentVersion.getFragmentData";
    public static final String GET_FRAGMENT_DATA_OF_PROCESS_MODEL = "fragmentVersion.getFragmentDataOfProcessModel";
    public static final String GET_ALL_FRAGMENTS_WITH_SIZE = "fragmentVersion.getAllFragmentsWithSize";
    public static final String GET_USED_FRAGMENT_IDS = "fragmentVersion.getUsedFragmentIds";
    public static final String GET_SIMILAR_FRAGMENTS_BY_SIZE = "fragmentVersion.getSimilarFragmentsBySize";
    public static final String GET_SIMILAR_FRAGMENTS_BY_SIZE_AND_TYPE = "fragmentVersion.getSimilarFragmentsBySizeType";

    public static final String GET_PARENT_FRAGMENT_VERSIONS = "fragmentVersionDag.getParentFragmentVersions";
    public static final String GET_CHILD_MAPPINGS = "fragmentVersionDag.getChildMappings";
    public static final String GET_CHILD_FRAGMENTS_BY_FRAGMENT_VERSION = "fragmentVersionDag.getChildFragmentsByFragmentVersion";
    public static final String GET_ALL_PARENT_CHILD_MAPPINGS = "fragmentVersionDag.getAllParentChildMappings";

    public static final String GET_NATIVE = "native.getNative";
    public static final String GET_NATIVE_TYPES = "native.getNativeTypes";

    public final static String GET_NATIVE_TYPE_FORMAT = "nativeType.getNativeTypeFormat";
    public final static String GET_NATIVE_TYPE_FORMATS = "nativeType.getNativeTypeFormats";

    public static final String GET_All_PROCESSES = "process.getAllProcesses";
    public static final String GET_All_DOMAINS = "process.getAllDomains";
    public static final String GET_PROCESS_BY_ID = "process.getProcessById";
    public static final String GET_PROCESS_BY_NAME = "process.getProcessByName";

    public static final String GET_BRANCH_BY_PROCESS_BRANCH_NAME = "processBranch.getProcessBranchByProcessBranchName";

    public static final String GET_PROCESS_MODEL_VERSION_BY_BRANCH = "processModelVersion.getProcessModelVersionByBranch";
    public static final String GET_USED_PROCESS_MODEL_VERSIONS = "processModelVersion.getUsedProcessModelVersions";
    public static final String GET_ROOT_FRAGMENT_PROCESS_MODEL = "processModelVersion.getRootFragmentProcessModel";
    public static final String GET_MAX_VERSION_PROCESS_MODEL = "processModelVersion.getMaxVersionProcessModel";
    public static final String GET_CURRENT_PROCESS_MODELS = "processModelVersion.getCurrentProcessModels";
    public static final String GET_CURRENT_PROCESS_MODEL_VERSION_A = "processModelVersion.getCurrentProcessModelVersionA";
    public static final String GET_CURRENT_PROCESS_MODEL_VERSION_B = "processModelVersion.getCurrentProcessModelVersionB";
    public static final String GET_CURRENT_PROCESS_MODEL_VERSION_C = "processModelVersion.getCurrentProcessModelVersionC";
    public static final String GET_MAX_MODEL_VERSIONS = "processModelVersion.getMaxModelVersions";
    public static final String GET_CURRENT_MODEL_VERSIONS = "processModelVersion.getCurrentModelVersions";

    public static final String GET_CONTAINED_PROCESS_MODEL = "processModelMap.getContainedProcessModel";

    public static final String GET_ALL_USERS = "user.getAllUsers";

    public static final String GET_CONTENT_IDS = "node.getContentIds";
    public static final String GET_VERTICES_BY_CONTENT = "node.getVerticesByContentId";
    public static final String GET_STORED_VERTICES = "node.getStoredVertices";

}
