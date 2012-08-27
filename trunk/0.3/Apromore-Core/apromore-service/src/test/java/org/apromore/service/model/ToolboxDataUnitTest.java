package org.apromore.service.model;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test the Toolbox Data POJO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ToolboxDataUnitTest {

    @Test
    public void testLikeJavaBean() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ToolboxData.class);
    }

    @Test
    public void testConstructor() {
        ToolboxData obj = new ToolboxData();
        obj.addModel(new ProcessModelVersion(), new CanonicalProcessType());
        obj.addModel(new ProcessModelVersion(), new CanonicalProcessType());

        assertThat(obj.getModel().size(), equalTo(2));
    }

}