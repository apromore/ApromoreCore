package cs.ut.ui

import cs.ut.util.Cookies
import org.zkoss.zk.ui.Executions

/**
 * Interface to ease logging
 * Provides easy access to session id inside controllers
 */
interface UIComponent {

    fun getSessionId(): String = Cookies.getCookieKey(Executions.getCurrent().nativeRequest)
}