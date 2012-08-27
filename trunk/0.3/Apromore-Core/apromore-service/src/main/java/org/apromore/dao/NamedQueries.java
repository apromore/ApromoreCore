package org.apromore.dao;

/**
 * Interface to define all the named queries names in the Persistence setup.
 * <p/>
 * The format of the names are define as [ENTITY].[QUERY NAME] The Entity could be empty if the query uses joins and doesn't relate to a single
 * entity.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface NamedQueries {

    /** The clustering find all clusters. */
    String GET_ALL_CLUSTERS = "cluster.getAllClusters";
    String GET_CLUSTER_BY_ID = "cluster.getClusterById";
    String GET_FILTERED_CLUSTERS = "cluster.getFilteredClusters";
    String GET_CLUSTERING_SUMMARY = "clusteringSummary.getClusteringSummary";
    String GET_FRAGMENTIDS_OF_CLUSTER = "clusterAssignment.getFragmentIdsOfCluster";
    String GET_FRAGMENTS_OF_CLUSTER = "clusterAssignment.getFragmentsOfCluster";
    String GET_FRAGMENT_DISTANCE = "fragmentDistance.getDistance";
    String GET_DISTANCES_BELOW_THRESHOLD = "fragmentDistance.getDistancesBelowThreshold";
    String GET_UNPROCESSED_FRAGMENTS = "fragmentVersion.getUnprocessedFragments";
    String GET_UNPROCESSED_FRAGMENTS_OF_PROCESSES = "fragmentVersion.getFragmentsOfProcesses";

    String DELETE_ALL_CLUSTERS = "cluster.deleteAllClusters";
    String DELETE_ALL_CLUSTER_ASSIGNMENTS = "clusterAssignment.deleteAllClusterAssignments";

    String GET_ANNOTATION = "annotation.getAnnotation";
    String GET_ANNOTATION_BY_URI = "annotation.getAnnotationByUrl";

    String GET_CONTENT_BY_FRAGMENT_VERSION = "content.getContentByFragmentVersion";
    String GET_CONTENT_BY_HASH = "content.getContentByHash";

    String GET_EDGES_BY_CONTENT = "edge.getEdgesByContentId";
    String GET_EDGES_BY_FRAGMENT = "edge.getEdgesByFragmentId";
    String GET_STORED_EDGES = "edge.getStoredEdges";

    String GET_FRAGMENT_VERSION = "fragmentVersion.getFragmentVersion";
    String GET_ALL_FRAGMENT_VERSION = "fragmentVersion.getAllFragmentVersion";
    String GET_FRAGMENT_BY_CONTENT_MAPPING = "fragmentVersion.getFragmentVersionByContentIdMappingCode";
    String GET_USED_PROCESS_MODEL_FOR_FRAGMENT = "fragmentVersion.getUsedProcessModelForFragment";
    String GET_LOCKED_PARENT_FRAGMENTS = "fragmentVersion.getLockedParentFragmentIds";
    String GET_CHILD_FRAGMENTS_WITH_SIZE = "fragmentVersion.getChildFragmentsWithSize";
    String GET_CHILD_FRAGMENTS_WITH_TYPE = "fragmentVersion.getChildFragmentsWithType";
    String GET_FRAGMENT_DATA = "fragmentVersion.getFragmentData";
    String GET_FRAGMENT_DATA_OF_PROCESS_MODEL = "fragmentVersion.getFragmentDataOfProcessModel";
    String GET_ALL_FRAGMENTS_WITH_SIZE = "fragmentVersion.getAllFragmentsWithSize";
    String GET_USED_FRAGMENT_IDS = "fragmentVersion.getUsedFragmentIds";
    String GET_SIMILAR_FRAGMENTS_BY_SIZE = "fragmentVersion.getSimilarFragmentsBySize";
    String GET_SIMILAR_FRAGMENTS_BY_SIZE_AND_TYPE = "fragmentVersion.getSimilarFragmentsBySizeType";
    String GET_ROOT_FRAGMENT_IDS_ABOVE_SIZE = "fragmentVersion.getRootFragmentIdAboveSize";

    String GET_PARENT_FRAGMENT_VERSIONS = "fragmentVersionDag.getParentFragmentVersions";
    String GET_CHILD_MAPPINGS = "fragmentVersionDag.getChildMappings";
    String GET_CHILD_FRAGMENTS_BY_FRAGMENT_VERSION = "fragmentVersionDag.getChildFragmentsByFragmentVersion";
    String GET_ALL_PARENT_CHILD_MAPPINGS = "fragmentVersionDag.getAllParentChildMappings";
    String GET_ALL_DAGS_WITH_SIZE = "fragmentVersionDag.getAllDagsWithSize";

    String GET_NATIVE = "native.getNative";
    String GET_NATIVE_TYPES = "native.getNativeTypes";

    String GET_NATIVE_TYPE_FORMAT = "nativeType.getNativeTypeFormat";
    String GET_NATIVE_TYPE_FORMATS = "nativeType.getNativeTypeFormats";

    String GET_All_PROCESSES = "process.getAllProcesses";
    String GET_All_DOMAINS = "process.getAllDomains";
    String GET_PROCESS_BY_ID = "process.getProcessById";
    String GET_PROCESS_BY_NAME = "process.getProcessByName";

    String GET_BRANCH_BY_PROCESS_BRANCH_NAME = "processBranch.getProcessBranchByProcessBranchName";

    String GET_PROCESS_MODEL_VERSION_BY_BRANCH = "processModelVersion.getProcessModelVersionByBranch";
    String GET_USED_PROCESS_MODEL_VERSIONS = "processModelVersion.getUsedProcessModelVersions";
    String GET_ROOT_FRAGMENT_PROCESS_MODEL = "processModelVersion.getRootFragmentProcessModel";
    String GET_MAX_VERSION_PROCESS_MODEL = "processModelVersion.getMaxVersionProcessModel";
    String GET_CURRENT_PROCESS_MODELS = "processModelVersion.getCurrentProcessModels";
    String GET_CURRENT_PROCESS_MODEL_VERSION = "processModelVersion.getCurrentProcessModelVersion";
    String GET_CURRENT_PROCESS_MODEL_VERSION_A = "processModelVersion.getCurrentProcessModelVersionA";
    String GET_CURRENT_PROCESS_MODEL_VERSION_B = "processModelVersion.getCurrentProcessModelVersionB";
    String GET_CURRENT_PROCESS_MODEL_VERSION_C = "processModelVersion.getCurrentProcessModelVersionC";
    String GET_MAX_MODEL_VERSIONS = "processModelVersion.getMaxModelVersions";
    String GET_CURRENT_MODEL_VERSIONS = "processModelVersion.getCurrentModelVersions";
    String GET_CURRENT_PROCESS_MODEL = "processModelVersion.getCurrentModel";

    String GET_CONTAINED_PROCESS_MODEL = "processModelMap.getContainedProcessModel";

    String GET_ALL_USERS = "user.getAllUsers";

    String GET_CONTENT_IDS = "node.getContentIds";
    String GET_VERTICES_BY_CONTENT = "node.getVerticesByContentId";
    String GET_VERTICES_BY_FRAGMENT = "node.getVerticesByFragmentId";
    String GET_STORED_VERTICES = "node.getStoredVertices";

}
