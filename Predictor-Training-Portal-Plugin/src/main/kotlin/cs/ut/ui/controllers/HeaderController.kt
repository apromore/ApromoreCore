package cs.ut.ui.controllers

import cs.ut.configuration.ConfigNode
import cs.ut.configuration.ConfigurationReader
import cs.ut.util.DEST
import cs.ut.util.NirdizatiTranslator
import cs.ut.util.Page
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.select.SelectorComposer
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;

class HeaderController : SelectorComposer<Component>(), Redirectable {

    private val configNode = ConfigurationReader.findNode("header")

    @Wire
    private lateinit var navbar: Hbox //Navbar

    override fun doAfterCompose(comp: Component?) {
        super.doAfterCompose(comp)
        composeHeader()
    }

    /**
     * Compose header based on configuration node
     */
    private fun composeHeader() {
        val items: List<ConfigNode> = configNode.childNodes

        items.forEach {
            val navItem = Button() //Navitem()
            navItem.label = NirdizatiTranslator.localizeText(it.valueWithIdentifier("label").value)
            navItem.setAttribute(DEST, it.valueWithIdentifier("redirect").value)
            navItem.iconSclass = it.valueWithIdentifier("icon").value
            navItem.sclass = "n-nav-item"
            navItem.addEventListener(Events.ON_CLICK, { _ ->
                setContent(it.valueWithIdentifier("redirect").value, page)
                //navbar.selectItem(navItem)
            })
            navItem.isVisible = it.isEnabled()

            navbar.appendChild(navItem)
        }
    }

    /**
     * Listener - update selected item in navigation bar when clicked
     */
    @Listen("onClick = #headerLogo")
    fun handleLogoClick() {
        Executions.getCurrent().sendRedirect("..");
        //setContent(Page.LANDING.value, page)
        //navbar.selectItem(null)
    }

    @Listen("onClick = #info")
    fun handleInfoClick() {
        Executions.getCurrent().sendRedirect("http://apromore.org/documentation/features/visualize-performance-predictions-via-dashboard");
    }
}
