package cs.ut.providers

import cs.ut.configuration.ConfigFetcher
import org.apromore.plugin.portal.predictortraining.PortalPlugin

/**
 * Object that provides easy access to configured directories
 */
object DirectoryConfiguration {

    private const val NODE_NAME = "directories"
    private val configNode by ConfigFetcher(NODE_NAME)

    private val dirs: Map<String, String>

    init {
        dirs = mutableMapOf<String, String>().apply {
            configNode.itemList().forEach {
                this[it.identifier] = it.value
            }
        }
    }

    /**
     * Get directory path
     *
     * @param dir to get path for
     */
    fun dirPath(dir: Dir): String {
        return when(dir) {
            Dir.PYTHON   -> PortalPlugin.python!!
            Dir.TMP_DIR  -> PortalPlugin.tmpDir!!
            Dir.LOG_FILE -> PortalPlugin.logFile!!
            else         -> "${PortalPlugin.backend!!}${dirs[dir.value()]!!}"
        }
    }
}

enum class Dir(private val id: String) {
    PYTHON("python"),
    USER_LOGS("userLogDirectory"),
    SCRIPT_DIR("scriptDirectory"),
    TRAIN_DIR("trainDirectory"),
    DATA_DIR("datasetDirectory"),
    PKL_DIR("pklDirectory"),
    OHP_DIR("ohpdir"),
    DETAIL_DIR("detailedDir"),
    FEATURE_DIR("featureDir"),
    VALIDATION_DIR("validationDir"),
    TMP_DIR("tmpDir"),
    CORE_DIR("coreDir"),
    LOG_FILE("logFile");

    fun value(): String = id
}
