package org.apromore.canoniser.yawl.yawl2cpf.patterns.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternTest;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.SoftType;
import org.apromore.cpf.TaskType;
import org.junit.Ignore;
import org.junit.Test;

public class TaskDataTest extends BasePatternTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/Data/WPD1TaskData.yawl");
    }

    @Test
    public void testNetVariables() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);

        assertEquals(5, rootNet.getObject().size());

        final SoftType n1 = (SoftType) getObjectByName(rootNet, "n1");
        assertNotNull(n1);
        final SoftType n2 = (SoftType) getObjectByName(rootNet, "n2");
        assertNotNull(n2);
        final SoftType n3 = (SoftType) getObjectByName(rootNet, "n3");
        assertNotNull(n3);
        final SoftType n4 = (SoftType) getObjectByName(rootNet, "n4");
        assertNotNull(n4);
        final SoftType n5 = (SoftType) getObjectByName(rootNet, "n5");
        assertNotNull(n5);

        assertEquals("boolean", n1.getType());
        assertEquals("string", n2.getType());
        assertEquals("byte", n3.getType());
        assertEquals("string", n4.getType());
        assertEquals("YDocumentType", n5.getType());
    }

    @Ignore
    @Test
    public void testTaskVariables() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);
        final TaskType taskA = (TaskType) getNodeByName(rootNet, "A");
        assertNotNull(taskA);
        final TaskType taskB = (TaskType) getNodeByName(rootNet, "B");
        assertNotNull(taskB);

        // final SoftType at1 = (SoftType) getObjectByName(taskA, "t1");
        // assertNotNull(at1);
        // final SoftType at2 = (SoftType) getObjectByName(taskA, "t2");
        // assertNotNull(at2);
        // final SoftType at3 = (SoftType) getObjectByName(taskA, "t3");
        // assertNotNull(at3);
        // final SoftType at4 = (SoftType) getObjectByName(taskA, "t4");
        // assertNotNull(at4);

        // assertEquals("string", at1.getType());
        // assertEquals("boolean", at2.getType());
        // assertEquals("string", at3.getType());
        // assertEquals("string", at4.getType());
        //
        // final SoftType bt1 = (SoftType) getObjectByName(taskB, "t1");
        // assertNotNull(bt1);
        // final SoftType bt2 = (SoftType) getObjectByName(taskB, "t2");
        // assertNotNull(bt2);
        // final SoftType bt3 = (SoftType) getObjectByName(taskB, "t3");
        // assertNotNull(bt3);

        // assertEquals("boolean", bt1.getType());
        // assertEquals("string", bt2.getType());
        // assertEquals("string", bt3.getType());
    }

    @Ignore
    @Test
    public void testTaskMappings() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);

        final TaskType taskA = (TaskType) getNodeByName(rootNet, "A");
        assertNotNull(taskA);
        final TaskType taskB = (TaskType) getNodeByName(rootNet, "B");
        assertNotNull(taskB);

        /********** TASK A *************/

        assertEquals(6, taskA.getObjectRef().size());

        // Test Input

        final List<ObjectRefType> inRefA1 = getObjectInputRef(taskA, getObjectByName(rootNet, "n1"));
        assertEquals(2, inRefA1.size());
        // assertNotNull(getObjectRefByTarget(inRefA1, getObjectByName(taskA, "t1")));
        // assertNotNull(getObjectRefByTarget(inRefA1, getObjectByName(taskA, "t2")));

        final List<ObjectRefType> inRefA2 = getObjectInputRef(taskA, getObjectByName(rootNet, "n2"));
        assertEquals(0, inRefA2.size());

        final List<ObjectRefType> inRefA3 = getObjectInputRef(taskA, getObjectByName(rootNet, "n3"));
        assertEquals(1, inRefA3.size());
        // assertEquals(getObjectByName(taskA, "t3").getId(), inRefA3.get(0).getMapsToObjectId());

        final List<ObjectRefType> inRefA4 = getObjectInputRef(taskA, getObjectByName(rootNet, "n4"));
        assertEquals(0, inRefA4.size());

        // Test Output

        final List<ObjectRefType> outRefA1 = getObjectOutputRef(taskA, getObjectByName(rootNet, "n1"));
        assertEquals(0, outRefA1.size());

        final List<ObjectRefType> outRefA2 = getObjectOutputRef(taskA, getObjectByName(rootNet, "n2"));
        assertEquals(1, outRefA2.size());
        // assertEquals(getObjectByName(taskA, "t3").getId(), outRefA2.get(0).getMapsToObjectId());

        final List<ObjectRefType> outRefA3 = getObjectOutputRef(taskA, getObjectByName(rootNet, "n3"));
        assertEquals(1, outRefA3.size());
        // assertEquals(getObjectByName(taskA, "t3").getId(), outRefA3.get(0).getMapsToObjectId());

        final List<ObjectRefType> outRefA4 = getObjectOutputRef(taskA, getObjectByName(rootNet, "n4"));
        assertEquals(1, outRefA4.size());
        // assertEquals(getObjectByName(taskA, "t4").getId(), outRefA4.get(0).getMapsToObjectId());

        /********** TASK B *************/

        assertEquals(4, taskB.getObjectRef().size());

        // Test Input

        final List<ObjectRefType> inRefB1 = getObjectInputRef(taskB, getObjectByName(rootNet, "n1"));
        assertEquals(1, inRefB1.size());
        // assertEquals(getObjectByName(taskB, "t1").getId(), inRefB1.get(0).getMapsToObjectId());

        final List<ObjectRefType> inRefB2 = getObjectInputRef(taskB, getObjectByName(rootNet, "n2"));
        assertEquals(1, inRefB2.size());
        // assertEquals(getObjectByName(taskB, "t2").getId(), inRefB2.get(0).getMapsToObjectId());

        final List<ObjectRefType> inRefB3 = getObjectInputRef(taskB, getObjectByName(rootNet, "n3"));
        assertEquals(0, inRefB3.size());

        final List<ObjectRefType> inRefB4 = getObjectInputRef(taskB, getObjectByName(rootNet, "n4"));
        assertEquals(0, inRefB4.size());

        // Test Output

        final List<ObjectRefType> outRefB1 = getObjectOutputRef(taskB, getObjectByName(rootNet, "n1"));
        assertEquals(0, outRefB1.size());

        final List<ObjectRefType> outRefB2 = getObjectOutputRef(taskB, getObjectByName(rootNet, "n2"));
        assertEquals(1, outRefB2.size());
        // assertEquals(getObjectByName(taskB, "t1").getId(), outRefB2.get(0).getMapsToObjectId());

        final List<ObjectRefType> outRefB3 = getObjectOutputRef(taskB, getObjectByName(rootNet, "n3"));
        assertEquals(1, outRefB3.size());
        // assertEquals(getObjectByName(taskB, "t1").getId(), outRefB3.get(0).getMapsToObjectId());

        final List<ObjectRefType> outRefB4 = getObjectOutputRef(taskB, getObjectByName(rootNet, "n4"));
        assertEquals(0, outRefB4.size());
    }
}
