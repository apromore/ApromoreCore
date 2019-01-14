package cs.ut.util

import org.zkoss.util.resource.Labels
import org.zkoss.zk.ui.Desktop
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.util.Clients

class NirdizatiTranslator {
    companion object {
        fun localizeText(text: String, vararg args: Any): String = Labels.getLabel(text, args) ?: text

        fun showNotificationAsync(text: String, desktop: Desktop, type: String = "info") {
            Executions.schedule(
                desktop,
                { _ ->
                    Clients.showNotification(
                        text,
                        type,
                        desktop.components.first { it.id == MAINLAYOUT },
                        "bottom_left",
                        3000,
                        true
                    )
                },
                Event("notification", null, "push")
            )
        }
    }
}