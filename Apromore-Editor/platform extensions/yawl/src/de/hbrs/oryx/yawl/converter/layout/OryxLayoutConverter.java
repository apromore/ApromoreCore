package de.hbrs.oryx.yawl.converter.layout;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;
import org.jdom.Element;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YNet;

public class OryxLayoutConverter {

    private OryxConversionContext context;
    private final BasicShape netShape;
    private final YNet net;

    public OryxLayoutConverter(OryxConversionContext context, YNet net, BasicShape netShape) {
        super();
        this.context = context;
        this.net = net;
        this.netShape = netShape;
    }

    public void convertLayout() {
        Element netLayoutElement = new Element("net");
        netLayoutElement.setAttribute("id", net.getID());

        context.addNetLayout(netLayoutElement);
    }

}
