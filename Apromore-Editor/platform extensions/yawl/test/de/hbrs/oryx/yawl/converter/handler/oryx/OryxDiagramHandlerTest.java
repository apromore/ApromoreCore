package de.hbrs.oryx.yawl.converter.handler.oryx;

import org.junit.Test;
import org.oryxeditor.server.diagram.StencilSetReference;
import org.oryxeditor.server.diagram.basic.BasicDiagram;

public class OryxDiagramHandlerTest extends OryxHandlerTest {

    @Test
    public void testBasicConversion() {

        String stencilSetNs = "http://b3mn.org/stencilset/yawl2.2#";
        StencilSetReference stencilSetRef = new StencilSetReference(stencilSetNs);
        BasicDiagram diagram = new BasicDiagram("test", "Diagram", stencilSetRef);
        diagram.setProperty("yawlid", "test");

        // Test with empty property
        diagram.setProperty("speccontributor", "");

        context.setRootNetID(diagram.getProperty("yawlid"));

        OryxDiagramHandler handler = new OryxDiagramHandler(context, diagram);
        handler.convert();

    }
}
