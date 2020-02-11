package org.oryxeditor.server.diagram.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.StencilSetReference;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicNode;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.oryxeditor.server.diagram.label.LabelSettings;


public abstract class BasicShapeTest {

	public static final double delta = 0.00002;
	private static final int NUM_SHAPE_CLASSES = 2;	
	
	protected Random rand = new Random(System.currentTimeMillis());
	
	/**
	 * Returns the type of shape to be tested in an inheriting test class
	 * @param id
	 * @return
	 */
	protected abstract BasicShape getBasicShapeToTest(String id);
	/**
	 * Returns the type of shape to be tested in an inheriting test class
	 * @param id
	 * @param stencilRef
	 * @return
	 */
	protected abstract BasicShape getBasicShapeToTest(String id, String stencilId);
	/**
	 * Returns a shape NOT of the type to be tested in an inheriting class
	 * @param id
	 * @return
	 */
	protected abstract BasicShape getBasicShapeOfDifferentType(String id);
	/**
	 * Returns a shape of the type to be tested having a child, grandchild, bounds and dockers
	 * @return
	 */
	protected abstract BasicShape getBasicShapeWithChildren_Bounds_Dockers();
	
	/**
	 * Returns a shape of a random type
	 * @param id
	 * @return
	 */
	public BasicShape getBasicShapeOfRandomType(String id){
		switch (rand.nextInt(NUM_SHAPE_CLASSES)){
		case 0:	return new BasicNode(id);
		case 1:	return new BasicEdge(id);
		default: return new BasicNode(id);
		}
	}
		
	@Test
	public void testShapeConstructor1() {
		// test Shape(String resourceId, StencilReference stencilRef)
		BasicShape shape = getBasicShapeToTest("testResourceId", "testStencilId");
		assertTrue(shape.getResourceId().equals("testResourceId"));
		assertTrue(shape.getStencilId().equals("testStencilId"));
		assertTrue(shape.getQualifiedStencilId()
				.equals("testStencilId"));

		assertNotNull(shape.getPropertiesReadOnly());
		assertNotNull(shape.getChildShapesReadOnly());
		assertNotNull(shape.getOutgoingsReadOnly());
		assertNotNull(shape.getIncomingsReadOnly());
		assertNotNull(shape.getDockersReadOnly());
		assertNotNull(shape.getLabelSettings());

		shape = getBasicShapeToTest("1234567890\"!§$%&/()=", "1234567890\"!§$%&/xx()=");
		assertTrue(shape.getResourceId().equals("1234567890\"!§$%&/()="));
		assertTrue(shape.getStencilId()
				.equals("1234567890\"!§$%&/xx()="));
		assertTrue(shape.getQualifiedStencilId()
				.equals("1234567890\"!§$%&/xx()="));

		assertNotNull(shape.getPropertiesReadOnly());
		assertNotNull(shape.getChildShapesReadOnly());
		assertNotNull(shape.getOutgoingsReadOnly());
		assertNotNull(shape.getIncomingsReadOnly());
		assertNotNull(shape.getDockersReadOnly());
		assertNotNull(shape.getLabelSettings());

		shape = getBasicShapeToTest(null);
		// forbidding null values would be pointless, as it can be set
		// to null anytime
		assertNull(shape.getResourceId());

		assertNotNull(shape.getPropertiesReadOnly());
		assertNotNull(shape.getChildShapesReadOnly());
		assertNotNull(shape.getOutgoingsReadOnly());
		assertNotNull(shape.getIncomingsReadOnly());
		assertNotNull(shape.getDockersReadOnly());
		assertNotNull(shape.getLabelSettings());
	}


	@Test
	public void testShapeConstructor2() {
		BasicShape shape = getBasicShapeToTest("testResourceId");
		assertTrue(shape.getResourceId().equals("testResourceId"));
		assertNull(shape.getStencilId());

		shape = getBasicShapeToTest("1234567890\"!§$%&/()=");
		assertTrue(shape.getResourceId().equals("1234567890\"!§$%&/()="));
		assertNull(shape.getStencilId());

		shape = getBasicShapeToTest(null);
		assertNull(shape.getResourceId());

		assertNotNull(shape.getPropertiesReadOnly());
		assertNotNull(shape.getChildShapesReadOnly());
		assertNotNull(shape.getOutgoingsReadOnly());
		assertNotNull(shape.getIncomingsReadOnly());
		assertNotNull(shape.getDockersReadOnly());
		assertNotNull(shape.getLabelSettings());
	}

	
	@Test
	public void testGetStencilId() {
		BasicShape s = getBasicShapeToTest("");
		s.setStencilId("aStencilId");
		assertEquals("aStencilId", s.getStencilId());

		s = getBasicShapeToTest("");
		s.setStencilId("!\"§$%&/()=123456789äöüß*");
		assertEquals("!\"§$%&/()=123456789äöüß*", s.getStencilId());

		s = getBasicShapeToTest("");
		s.setStencilId(null);
		assertNull(s.getStencilId());

		s = getBasicShapeToTest("");
		s.setStencilId("StencilRef");
		assertEquals("StencilRef", s.getStencilId());
		
		s = getBasicShapeToTest("");
		s.setStencilId("someStencilId");
		BasicDiagram diagram = new BasicDiagram("someDiagram", "DiagramStencil", new StencilSetReference("someStencilset"));
		s.setDiagram(diagram);
		assertEquals("someStencilId", s.getStencilId());
	}


	@Test
	public void testGetQualifiedStencilId() {
		BasicShape s = getBasicShapeToTest("");
		s.setStencilId("aStencilId");
		assertEquals("aStencilId", s.getQualifiedStencilId());

		s = getBasicShapeToTest("");
		s.setStencilId("!\"§$%&/()=123456789äöüß*");
		assertEquals("!\"§$%&/()=123456789äöüß*", s.getQualifiedStencilId());

		s = getBasicShapeToTest("");
		s.setStencilId(null);
		assertNull(s.getQualifiedStencilId());

		s = getBasicShapeToTest("");
		s.setStencilId("StencilRef");
		assertEquals("StencilRef", s.getQualifiedStencilId());
		
		s = getBasicShapeToTest("");
		s.setStencilId("someStencilId");
		BasicDiagram diagram = new BasicDiagram("someDiagram", "DiagramStencil", new StencilSetReference("someStencilset"));
		s.setDiagram(diagram);
		assertEquals("someStencilset/someStencilId", s.getQualifiedStencilId());
		
		diagram.setStencilsetRef(new StencilSetReference(""));
		assertEquals("/someStencilId", s.getQualifiedStencilId());
		
		diagram.setStencilsetRef(new StencilSetReference(null));
		assertEquals("someStencilId", s.getQualifiedStencilId());
		
		diagram.setStencilsetRef(null);
		assertEquals("someStencilId", s.getQualifiedStencilId());
	}
	
	
	@Test
	public void testGetResourceId() {
		BasicShape s = getBasicShapeToTest("");
		s.setResourceId("aResourceId");
		assertEquals("aResourceId", s.getResourceId());

		s = getBasicShapeToTest(null);
		s.setResourceId("!\"§$%&/()=123456789äöüß*");
		assertEquals("!\"§$%&/()=123456789äöüß*", s.getResourceId());
	}


	@Test
	public void testSetResourceId() {
		// seriously, this has been tested before...
		testGetResourceId();
	}

	@Test
	public void testGetProperties() {
		BasicShape s = getBasicShapeToTest("");
		
		assertNotNull(s.getPropertiesReadOnly());
		assertEquals(0, s.getPropertiesReadOnly().size());

		Map<String, String> map = new HashMap<String, String>();
		map.put("Test", "TestValue");
		map.put(null, "12345678");
		map.put("", "1415926535");
		map.put("!\"§$%&/()=", "Xylometazolinhydrochlorid");
		s.setProperties(map);

		assertNotNull(s.getPropertiesReadOnly());
		assertEquals(4, s.getPropertiesReadOnly().size());

		assertEquals("TestValue", s.getPropertiesReadOnly().get("Test"));
		assertEquals("12345678", s.getPropertiesReadOnly().get(null));
		assertEquals("1415926535", s.getPropertiesReadOnly().get(""));
		assertEquals("Xylometazolinhydrochlorid",
				s.getPropertiesReadOnly().get("!\"§$%&/()="));
	}
	
	
	@Test
	public void testSetProperties() {
		// seriously, this has been tested before...
		testGetProperties();
	}

	@Test
	public void testGetProperty() {
		BasicShape s = getBasicShapeToTest("");

		s.setProperty("Test", "TestValue");
		s.setProperty(null, "12345678");
		s.setProperty("", "1415926535");
		s.setProperty("!\"§$%&/()=", "Xylometazolinhydrochlorid");

		assertEquals("TestValue", s.getProperty("Test"));
		assertEquals("12345678", s.getProperty(null));
		assertEquals("1415926535", s.getProperty(""));
		assertEquals("Xylometazolinhydrochlorid", s.getProperty("!\"§$%&/()="));
	}
	
	
	@Test
	public void testSetProperty() {
		testGetProperty();
	}


	@Test
	public void testGetChildShapes() {
		BasicShape s = getBasicShapeToTest("");
		List<BasicShape> shapes = new ArrayList<BasicShape>();

		BasicShape s1 = getBasicShapeOfRandomType("Shape1");
		BasicShape s2 = getBasicShapeOfRandomType("Shape2");
		BasicShape s3 = getBasicShapeOfRandomType("Shape3");
		shapes.add(s1);
		shapes.add(s2);
		shapes.add(s3);
		s.setChildShapes(shapes);
		assertEquals(shapes, s.getChildShapesReadOnly());

		s = getBasicShapeToTest("");
		s.addChildShape(s1);
		s.addChildShape(s2);
		s.addChildShape(s3);

		assertTrue(s.getChildShapesReadOnly().contains(s1));
		assertTrue(s.getChildShapesReadOnly().contains(s2));
		assertTrue(s.getChildShapesReadOnly().contains(s3));
		assertEquals(3, s.getChildShapesReadOnly().size());
	}


	@Test
	public void testSetChildShapes() {
		// has been tested...
		testGetChildShapes();
	}

	@Test
	public void testAddChildShapeIgnores() {
		BasicShape parent = getBasicShapeToTest("parent");
		BasicShape child = getBasicShapeOfRandomType("child1");
		
		//normal add
		assertTrue(parent.getChildShapesReadOnly().isEmpty());
		parent.addChildShape(child);
		assertEquals(1, parent.getChildShapesReadOnly().size());
		assertEquals(child, parent.getChildShapesReadOnly().get(0));
		assertNull(parent.getParent());
		
		//try to add yourself
		parent.addChildShape(parent);
		//check whether nothing happened
		assertNull(parent.getParent());
		assertEquals(1, parent.getChildShapesReadOnly().size());
		assertEquals(child, parent.getChildShapesReadOnly().get(0));
		
		//try to add a diagram
		BasicShape diagram = new BasicDiagram("canvas");
		assertNull(diagram.getParent());
		
		parent.addChildShape(diagram);
		//check whether nothing happened
		assertNull(diagram.getParent());
		assertEquals(1, parent.getChildShapesReadOnly().size());
		assertEquals(child, parent.getChildShapesReadOnly().get(0));
	}
	
	@Test
	public void testAddChildShape() {
		// has been tested partly
		testGetChildShapes();

		// test if parent adjusted
		BasicShape parent = getBasicShapeToTest("parent");
		BasicShape child = getBasicShapeOfRandomType("child1");
		BasicShape child2 = getBasicShapeOfRandomType("child2");
		BasicShape grandchild = getBasicShapeOfRandomType("grandchild");
		BasicDiagram diagram = new BasicDiagram("diagram");

		diagram.addChildShape(parent);

		parent.addChildShape(child);
		parent.addChildShape(child2);
		
		child.addChildShape(grandchild);

		assertSame(parent, child.getParent());
		assertTrue(parent.getChildShapesReadOnly().contains(child));
		assertSame(parent, child2.getParent());
		assertTrue(parent.getChildShapesReadOnly().contains(child2));
		assertEquals(2, parent.getChildShapesReadOnly().size());
		assertSame(child, grandchild.getParent());
		assertTrue(child.getChildShapesReadOnly().contains(grandchild));
		assertTrue(diagram.getAllShapesReadOnly().contains(child));
		assertTrue(diagram.getAllShapesReadOnly().contains(parent));
		assertTrue(diagram.getAllShapesReadOnly().contains(child2));
		assertTrue(diagram.getAllShapesReadOnly().contains(grandchild));

		//test diagram shape cache, when shapes are added bottom-up
		diagram = new BasicDiagram("diagram");
		parent = getBasicShapeToTest("parent");
		child = getBasicShapeOfRandomType("child1");
		child2 = getBasicShapeOfRandomType("child2");
		grandchild = getBasicShapeOfRandomType("grandchild");
		
		child.addChildShape(grandchild);
		
		parent.addChildShape(child);
		parent.addChildShape(child2);
		
		diagram.addChildShape(parent);
		
		assertSame(parent, child.getParent());
		assertTrue(parent.getChildShapesReadOnly().contains(child));
		assertSame(parent, child2.getParent());
		assertTrue(parent.getChildShapesReadOnly().contains(child2));
		assertEquals(2, parent.getChildShapesReadOnly().size());
		assertSame(child, grandchild.getParent());
		assertTrue(child.getChildShapesReadOnly().contains(grandchild));
		assertTrue(diagram.getAllShapesReadOnly().contains(child));
		assertTrue(diagram.getAllShapesReadOnly().contains(parent));
		assertTrue(diagram.getAllShapesReadOnly().contains(child2));
		assertTrue(diagram.getAllShapesReadOnly().contains(grandchild));
	}

	@Test
	public void testRemoveChildShape() {
		// test if parent adjusted
		BasicShape parent = getBasicShapeToTest("parent");
		BasicShape child = getBasicShapeOfRandomType("child1");
		BasicShape child2 = getBasicShapeOfRandomType("child2");
		BasicDiagram diagram = new BasicDiagram("diagram");

		diagram.addChildShape(parent);

		parent.addChildShape(child);
		parent.addChildShape(child2);

		assertSame(parent, child.getParent());
		assertSame(parent, child2.getParent());
		assertTrue(parent.getChildShapesReadOnly().contains(child));
		assertTrue(parent.getChildShapesReadOnly().contains(child2));
		assertEquals(2, parent.getChildShapesReadOnly().size());
		assertTrue(diagram.getAllShapesReadOnly().contains(child));
		assertTrue(diagram.getAllShapesReadOnly().contains(child2));
		assertTrue(diagram.getAllShapesReadOnly().contains(parent));

		parent.removeChildShape(child);

		assertNull(child.getParent());
		assertSame(parent, child2.getParent());
		assertFalse(parent.getChildShapesReadOnly().contains(child));
		assertTrue(parent.getChildShapesReadOnly().contains(child2));
		assertEquals(1, parent.getChildShapesReadOnly().size());
		assertFalse(diagram.getAllShapesReadOnly().contains(child));
		assertTrue(diagram.getAllShapesReadOnly().contains(child2));
		assertTrue(diagram.getAllShapesReadOnly().contains(parent));

		parent.removeChildShape(child2);

		assertNull(child.getParent());
		assertNull(child2.getParent());
		assertFalse(parent.getChildShapesReadOnly().contains(child));
		assertFalse(parent.getChildShapesReadOnly().contains(child2));
		assertEquals(0, parent.getChildShapesReadOnly().size());
		assertFalse(diagram.getAllShapesReadOnly().contains(child));
		assertFalse(diagram.getAllShapesReadOnly().contains(child2));
		assertTrue(diagram.getAllShapesReadOnly().contains(parent));
		
		parent.addChildShape(child);
		parent.addChildShape(child2);
		
		assertSame(parent, child.getParent());
		assertSame(parent, child2.getParent());
		assertSame(diagram, parent.getParent());
		assertTrue(parent.getChildShapesReadOnly().contains(child));
		assertTrue(parent.getChildShapesReadOnly().contains(child2));
		assertTrue(diagram.getChildShapesReadOnly().contains(parent));
		assertEquals(2, parent.getChildShapesReadOnly().size());
		assertTrue(diagram.getAllShapesReadOnly().contains(child));
		assertTrue(diagram.getAllShapesReadOnly().contains(child2));
		assertTrue(diagram.getAllShapesReadOnly().contains(parent));
		
		diagram.removeChildShape(parent);
		
		assertSame(parent, child.getParent());
		assertSame(parent, child2.getParent());
		assertNull(parent.getParent());
		assertTrue(parent.getChildShapesReadOnly().contains(child));
		assertTrue(parent.getChildShapesReadOnly().contains(child2));
		assertFalse(diagram.getChildShapesReadOnly().contains(parent));
		assertEquals(2, parent.getChildShapesReadOnly().size());
		assertFalse(diagram.getAllShapesReadOnly().contains(child));
		assertFalse(diagram.getAllShapesReadOnly().contains(child2));
		assertFalse(diagram.getAllShapesReadOnly().contains(parent));
	}

	
	@Test
	public void testRemoveAllChildShapes(){
		//preparation
		BasicShape parent = getBasicShapeToTest("parent");
		BasicShape child = getBasicShapeOfRandomType("child1");
		BasicShape child2 = getBasicShapeOfRandomType("child2");
		BasicShape grandChild = getBasicShapeOfRandomType("grandchild1");
		BasicDiagram d = new BasicDiagram("diagram");

		d.addChildShape(parent);

		parent.addChildShape(child);
		child.addChildShape(grandChild);
		parent.addChildShape(child2);

		//pre-conditions
		assertSame(parent, child.getParent());
		assertSame(parent, child2.getParent());
		assertTrue(parent.getChildShapesReadOnly().contains(child));
		assertTrue(parent.getChildShapesReadOnly().contains(child2));
		assertEquals(2, parent.getChildShapesReadOnly().size());
		
		assertSame(child, grandChild.getParent());
		assertTrue(child.getChildShapesReadOnly().contains(grandChild));
		assertEquals(1, child.getChildShapesReadOnly().size());
		
		assertTrue(d.getAllShapesReadOnly().contains(child));
		assertTrue(d.getAllShapesReadOnly().contains(child2));
		assertTrue(d.getAllShapesReadOnly().contains(grandChild));
		assertTrue(d.getAllShapesReadOnly().contains(parent));
		
		parent.removeAllChildShapes();
		
		//post-conditions
		assertNull(child.getParent());
		assertNull(child2.getParent());
		assertFalse(parent.getChildShapesReadOnly().contains(child));
		assertFalse(parent.getChildShapesReadOnly().contains(child2));
		assertEquals(0, parent.getChildShapesReadOnly().size());
		
		assertNull(grandChild.getParent());
		assertFalse(child.getChildShapesReadOnly().contains(grandChild));
		assertEquals(0, parent.getChildShapesReadOnly().size());
		
		assertFalse(d.getAllShapesReadOnly().contains(child));
		assertFalse(d.getAllShapesReadOnly().contains(child2));
		assertFalse(d.getAllShapesReadOnly().contains(grandChild));
		assertTrue(d.getAllShapesReadOnly().contains(parent));
	}
	

	@Test
	public void testGetParent() {
		BasicShape parent = getBasicShapeToTest("parent");
		BasicShape child = getBasicShapeToTest("child");
		parent.addChildShape(child);

		assertSame(parent, child.getParent());
	}

	@Test
	public void testSetParent() {
		BasicShape parent = getBasicShapeToTest("parent");
		BasicShape child = getBasicShapeToTest("child");
		child.setParent(parent);

		assertSame(parent, child.getParent());
		assertEquals(0, parent.getChildShapesReadOnly().size());

		parent.addChildShape(child);

		assertSame(parent, child.getParent());
		assertTrue(parent.getChildShapesReadOnly().contains(child));
	}

	@Test
	public void testSetParentAndUpdateItsChildShapes() {
		BasicShape parent = getBasicShapeToTest("parent");
		BasicShape child = getBasicShapeOfRandomType("child");
		child.setParentAndUpdateItsChildShapes(parent);

		assertSame(parent, child.getParent());
		assertEquals(1, parent.getChildShapesReadOnly().size());
		assertTrue(parent.getChildShapesReadOnly().contains(child));

	}

	@Test
	public void testGetDiagram() {

		BasicDiagram d = new BasicDiagram("diagram");
		BasicShape grandfather = getBasicShapeToTest("Grandfather");
		BasicShape father = getBasicShapeToTest("Father");
		BasicShape son = getBasicShapeToTest("Son");

		d.addChildShape(grandfather);
		grandfather.addChildShape(father);
		father.addChildShape(son);

		assertSame(d, son.getDiagram());
		assertSame(d, grandfather.getDiagram());
		assertSame(d, father.getDiagram());

	}
	
	@Test
	public void testSetDiagram() {
		BasicDiagram d = new BasicDiagram("Bla");

		BasicShape s = getBasicShapeToTest("s");

		s.setDiagram(d);

		assertSame(d, s.getDiagram());
	}

	@Test
	public void testGetDockers() {

		Point p = new Point(456.321, 98907.3134);
		Point p2 = new Point(-93214792174.03, .3234);

		List<Point> l = new ArrayList<Point>();
		l.add(p2);
		l.add(p);

		BasicShape s = getBasicShapeToTest("s");
		assertNotNull(s.getDockersReadOnly());
		assertTrue(s.getDockersReadOnly().isEmpty());
		
		s.setDockers(null);
		assertNotNull(s.getDockersReadOnly());
		assertTrue(s.getDockersReadOnly().isEmpty());
		
		s.setDockers(l);
		assertEquals(l, s.getDockersReadOnly());

	}

	@Test
	public void testSetDockers() {
		// has been tested
		testGetDockers();
	}

	@Test
	public void testGetBounds() {
		testSetBounds();
	}

	@Test
	public void testGetAbsoluteBounds() {

		BasicDiagram d = new BasicDiagram("diagram");
		BasicShape grandfather = getBasicShapeToTest("Grandfather");
		BasicShape father = getBasicShapeToTest("Father");
		BasicShape son = getBasicShapeToTest("Son");

		d.addChildShape(grandfather);
		grandfather.addChildShape(father);
		father.addChildShape(son);

		Point p1 = new Point(rand.nextDouble(), rand.nextDouble());
		Point p2 = new Point(rand.nextDouble(), rand.nextDouble());
		Point p3 = new Point(rand.nextDouble(), rand.nextDouble());
		Point p4 = new Point(rand.nextDouble(), rand.nextDouble());
		Point p5 = new Point(rand.nextDouble(), rand.nextDouble());
		Point p6 = new Point(rand.nextDouble(), rand.nextDouble());

		Bounds b1 = new Bounds(p1, p2);
		Bounds b2 = new Bounds(p3, p4);
		Bounds b3 = new Bounds(p5, p6);

		grandfather.setBounds(b1);
		father.setBounds(b2);
		son.setBounds(b3);

		Bounds temp1 = father.getBounds().copy();
		temp1.moveBy(grandfather.getUpperLeft());

		Bounds temp2 = son.getBounds().copy();
		temp2.moveBy(father.getAbsoluteBounds().getUpperLeft());

		assertEquals(grandfather.getBounds().getUpperLeft().getX(), grandfather
				.getAbsoluteBounds().getUpperLeft().getX(), delta);
		assertEquals(grandfather.getBounds().getUpperLeft().getY(), grandfather
				.getAbsoluteBounds().getUpperLeft().getY(), delta);
		assertEquals(grandfather.getBounds().getLowerRight().getX(),
				grandfather.getAbsoluteBounds().getLowerRight().getX(), delta);
		assertEquals(grandfather.getBounds().getLowerRight().getY(),
				grandfather.getAbsoluteBounds().getLowerRight().getY(), delta);

		assertEquals(temp1.getUpperLeft().getX(), father.getAbsoluteBounds()
				.getUpperLeft().getX(), delta);
		assertEquals(temp1.getUpperLeft().getY(), father.getAbsoluteBounds()
				.getUpperLeft().getY(), delta);
		assertEquals(temp1.getLowerRight().getX(), father.getAbsoluteBounds()
				.getLowerRight().getX(), delta);
		assertEquals(temp1.getLowerRight().getY(), father.getAbsoluteBounds()
				.getLowerRight().getY(), delta);

		assertEquals(temp2.getUpperLeft().getX(), son.getAbsoluteBounds()
				.getUpperLeft().getX(), delta);
		assertEquals(temp2.getUpperLeft().getY(), son.getAbsoluteBounds()
				.getUpperLeft().getY(), delta);
		assertEquals(temp2.getLowerRight().getX(), son.getAbsoluteBounds()
				.getLowerRight().getX(), delta);
		assertEquals(temp2.getLowerRight().getY(), son.getAbsoluteBounds()
				.getLowerRight().getY(), delta);

	}

	@Test
	public void testSetBounds() {

		BasicShape s = getBasicShapeToTest("s");

		Point p = new Point(456.321, 98907.3134);
		Point p2 = new Point(-93214792174.03, .3234);

		Bounds b = new Bounds(p, p2);
		s.setBounds(b);

		assertNotNull(s.getBounds());
		assertTrue(b.hasSamePositionsAs(s.getBounds()));

	}
	
	
	@Test
	public void testGetIncomings() {
		BasicShape s = getBasicShapeToTest("...");

		BasicDiagram d = new BasicDiagram("d");
		BasicShape child = getBasicShapeOfRandomType("child");
		BasicShape parent = getBasicShapeOfRandomType("parent");
		BasicShape adjacent1 = getBasicShapeOfRandomType("inOutgoing");
		BasicShape adjacent2 = getBasicShapeOfRandomType("inIncoming");
		BasicShape adjacent3 = getBasicShapeOfRandomType("inOutgoing2");
		BasicShape adjacent4 = getBasicShapeOfRandomType("inIncoming2");

		d.addChildShape(parent);
		s.addChildShape(child);
		parent.addChildShape(s);
		s.addOutgoingAndUpdateItsIncomings(adjacent1);
		s.addIncomingAndUpdateItsOutgoings(adjacent2);
		s.addOutgoingAndUpdateItsIncomings(adjacent3);
		s.addIncomingAndUpdateItsOutgoings(adjacent4);

		assertTrue(s.getIncomingsReadOnly().contains(adjacent2));
		assertTrue(s.getIncomingsReadOnly().contains(adjacent4));
		assertTrue(s.getOutgoingsReadOnly().contains(adjacent1));
		assertTrue(s.getOutgoingsReadOnly().contains(adjacent3));

		assertTrue(adjacent1.getIncomingsReadOnly().contains(s));
		assertTrue(adjacent3.getIncomingsReadOnly().contains(s));
		assertTrue(adjacent2.getOutgoingsReadOnly().contains(s));
		assertTrue(adjacent4.getOutgoingsReadOnly().contains(s));

		assertEquals(2, s.getIncomingsReadOnly().size());
		assertEquals(2, s.getOutgoingsReadOnly().size());
		assertEquals(1, adjacent1.getIncomingsReadOnly().size());
		assertEquals(1, adjacent3.getIncomingsReadOnly().size());
		assertEquals(1, adjacent2.getOutgoingsReadOnly().size());
		assertEquals(1, adjacent4.getOutgoingsReadOnly().size());

		assertEquals(0, adjacent2.getIncomingsReadOnly().size());
		assertEquals(0, adjacent4.getIncomingsReadOnly().size());
		assertEquals(0, adjacent1.getOutgoingsReadOnly().size());
		assertEquals(0, adjacent3.getOutgoingsReadOnly().size());

	}
	
	
//	@Test
//	public void testSetIncomings() {
//		BasicShape s = getBasicShapeToTest("...");
//
////		BasicDiagramd = new BasicDiagram("d");
////		BasicShape child = new Shape("child");
////		BasicShape parent = new Shape("parent");
//		BasicShape adjacent1 = getBasicShapeOfRandomType("inOutgoing");
//		BasicShape adjacent2 = getBasicShapeOfRandomType("inIncoming");
//		BasicShape adjacent3 = getBasicShapeOfRandomType("inOutgoing2");
//		BasicShape adjacent4 = getBasicShapeOfRandomType("inIncoming2");
//
//		List<BasicShape> li = new ArrayList<BasicShape>();
//		List<BasicShape> lo = new ArrayList<BasicShape>();
//		List<BasicShape> ls = new ArrayList<BasicShape>();
//
//		li.add(adjacent2);
//		li.add(adjacent4);
//		lo.add(adjacent1);
//		lo.add(adjacent3);
//		ls.add(s);
//
//		s.setIncomings(li);
//		s.setOutgoings(lo);
//		adjacent1.setIncomings(ls);
//		adjacent3.setIncomings(ls);
//		adjacent2.setOutgoings(ls);
//		adjacent4.setOutgoings(ls);
//
//		assertTrue(s.getIncomingsReadOnly().contains(adjacent2));
//		assertTrue(s.getIncomingsReadOnly().contains(adjacent4));
//		assertTrue(s.getOutgoingsReadOnly().contains(adjacent1));
//		assertTrue(s.getOutgoingsReadOnly().contains(adjacent3));
//
//		assertTrue(adjacent1.getIncomingsReadOnly().contains(s));
//		assertTrue(adjacent3.getIncomingsReadOnly().contains(s));
//		assertTrue(adjacent2.getOutgoingsReadOnly().contains(s));
//		assertTrue(adjacent4.getOutgoingsReadOnly().contains(s));
//
//		assertEquals(2, s.getIncomingsReadOnly().size());
//		assertEquals(2, s.getOutgoingsReadOnly().size());
//		assertEquals(1, adjacent1.getIncomingsReadOnly().size());
//		assertEquals(1, adjacent3.getIncomingsReadOnly().size());
//		assertEquals(1, adjacent2.getOutgoingsReadOnly().size());
//		assertEquals(1, adjacent4.getOutgoingsReadOnly().size());
//
//		assertEquals(0, adjacent2.getIncomingsReadOnly().size());
//		assertEquals(0, adjacent4.getIncomingsReadOnly().size());
//		assertEquals(0, adjacent1.getOutgoingsReadOnly().size());
//		assertEquals(0, adjacent3.getOutgoingsReadOnly().size());
//
//	}

	@Test
	public void testSetIncomingsAndUpdateTheirOutgoings() {
		BasicShape s = getBasicShapeToTest("...");

//		BasicDiagramd = new BasicDiagram("d");
//		BasicShape child = new Shape("child");
//		BasicShape parent = new Shape("parent");
		BasicShape adjacent1 = getBasicShapeOfRandomType("inOutgoing");
		BasicShape adjacent2 = getBasicShapeOfRandomType("inIncoming");
		BasicShape adjacent3 = getBasicShapeOfRandomType("inOutgoing2");
		BasicShape adjacent4 = getBasicShapeOfRandomType("inIncoming2");

		List<BasicShape> li = new ArrayList<BasicShape>();
		List<BasicShape> lo = new ArrayList<BasicShape>();
		List<BasicShape> ls = new ArrayList<BasicShape>();

		li.add(adjacent2);
		li.add(adjacent4);
		lo.add(adjacent1);
		lo.add(adjacent3);
		ls.add(s);

		s.setIncomingsAndUpdateTheirOutgoings(li);
		s.setOutgoingsAndUpdateTheirIncomings(lo);

		assertTrue(s.getIncomingsReadOnly().contains(adjacent2));
		assertTrue(s.getIncomingsReadOnly().contains(adjacent4));
		assertTrue(s.getOutgoingsReadOnly().contains(adjacent1));
		assertTrue(s.getOutgoingsReadOnly().contains(adjacent3));

		assertTrue(adjacent1.getIncomingsReadOnly().contains(s));
		assertTrue(adjacent3.getIncomingsReadOnly().contains(s));
		assertTrue(adjacent2.getOutgoingsReadOnly().contains(s));
		assertTrue(adjacent4.getOutgoingsReadOnly().contains(s));

		assertEquals(2, s.getIncomingsReadOnly().size());
		assertEquals(2, s.getOutgoingsReadOnly().size());
		assertEquals(1, adjacent1.getIncomingsReadOnly().size());
		assertEquals(1, adjacent3.getIncomingsReadOnly().size());
		assertEquals(1, adjacent2.getOutgoingsReadOnly().size());
		assertEquals(1, adjacent4.getOutgoingsReadOnly().size());

		assertEquals(0, adjacent2.getIncomingsReadOnly().size());
		assertEquals(0, adjacent4.getIncomingsReadOnly().size());
		assertEquals(0, adjacent1.getOutgoingsReadOnly().size());
		assertEquals(0, adjacent3.getOutgoingsReadOnly().size());

		s.setIncomingsAndUpdateTheirOutgoings(lo);
		s.setOutgoingsAndUpdateTheirIncomings(li);

		assertTrue(s.getOutgoingsReadOnly().contains(adjacent2));
		assertTrue(s.getOutgoingsReadOnly().contains(adjacent4));
		assertTrue(s.getIncomingsReadOnly().contains(adjacent1));
		assertTrue(s.getIncomingsReadOnly().contains(adjacent3));

		assertTrue(adjacent1.getOutgoingsReadOnly().contains(s));
		assertTrue(adjacent3.getOutgoingsReadOnly().contains(s));
		assertTrue(adjacent2.getIncomingsReadOnly().contains(s));
		assertTrue(adjacent4.getIncomingsReadOnly().contains(s));

		assertEquals(2, s.getOutgoingsReadOnly().size());
		assertEquals(2, s.getIncomingsReadOnly().size());
		assertEquals(1, adjacent1.getOutgoingsReadOnly().size());
		assertEquals(1, adjacent3.getOutgoingsReadOnly().size());
		assertEquals(1, adjacent2.getIncomingsReadOnly().size());
		assertEquals(1, adjacent4.getIncomingsReadOnly().size());

		assertEquals(0, adjacent2.getOutgoingsReadOnly().size());
		assertEquals(0, adjacent4.getOutgoingsReadOnly().size());
		assertEquals(0, adjacent1.getIncomingsReadOnly().size());
		assertEquals(0, adjacent3.getIncomingsReadOnly().size());
	}

//	@Test
//	public void testAddIncoming() {
//		BasicShape s = getBasicShapeToTest("...");
//
////		BasicDiagramd = new BasicDiagram("d");
////		BasicShape child = new Shape("child");
////		BasicShape parent = new Shape("parent");
//		BasicShape adjacent1 = getBasicShapeOfRandomType("inOutgoing");
//		BasicShape adjacent2 = getBasicShapeOfRandomType("inIncoming");
//		BasicShape adjacent3 = getBasicShapeOfRandomType("inOutgoing2");
//		BasicShape adjacent4 = getBasicShapeOfRandomType("inIncoming2");
//
//		s.addIncoming(adjacent2);
//		s.addIncoming(adjacent4);
//		s.addOutgoing(adjacent3);
//		s.addOutgoing(adjacent1);
//
//		adjacent1.addIncoming(s);
//		adjacent2.addOutgoing(s);
//		adjacent3.addIncoming(s);
//		adjacent4.addOutgoing(s);
//
//		assertTrue(s.getIncomingsReadOnly().contains(adjacent2));
//		assertTrue(s.getIncomingsReadOnly().contains(adjacent4));
//		assertTrue(s.getOutgoingsReadOnly().contains(adjacent1));
//		assertTrue(s.getOutgoingsReadOnly().contains(adjacent3));
//
//		assertTrue(adjacent1.getIncomingsReadOnly().contains(s));
//		assertTrue(adjacent3.getIncomingsReadOnly().contains(s));
//		assertTrue(adjacent2.getOutgoingsReadOnly().contains(s));
//		assertTrue(adjacent4.getOutgoingsReadOnly().contains(s));
//
//		assertEquals(2, s.getIncomingsReadOnly().size());
//		assertEquals(2, s.getOutgoingsReadOnly().size());
//		assertEquals(1, adjacent1.getIncomingsReadOnly().size());
//		assertEquals(1, adjacent3.getIncomingsReadOnly().size());
//		assertEquals(1, adjacent2.getOutgoingsReadOnly().size());
//		assertEquals(1, adjacent4.getOutgoingsReadOnly().size());
//
//		assertEquals(0, adjacent2.getIncomingsReadOnly().size());
//		assertEquals(0, adjacent4.getIncomingsReadOnly().size());
//		assertEquals(0, adjacent1.getOutgoingsReadOnly().size());
//		assertEquals(0, adjacent3.getOutgoingsReadOnly().size());
//	}

	@Test
	public void testAddIncomingAndUpdateItsOutgoings() {
		BasicShape s = getBasicShapeToTest("...");

//		BasicDiagramd = new BasicDiagram("d");
//		BasicShape child = new Shape("child");
//		BasicShape parent = new Shape("parent");
		BasicShape adjacent1 = getBasicShapeOfRandomType("inOutgoing");
		BasicShape adjacent2 = getBasicShapeOfRandomType("inIncoming");
		BasicShape adjacent3 = getBasicShapeOfRandomType("inOutgoing2");
		BasicShape adjacent4 = getBasicShapeOfRandomType("inIncoming2");

		s.addIncomingAndUpdateItsOutgoings(adjacent2);
		s.addIncomingAndUpdateItsOutgoings(adjacent4);
		s.addOutgoingAndUpdateItsIncomings(adjacent3);
		s.addOutgoingAndUpdateItsIncomings(adjacent1);

		assertTrue(s.getIncomingsReadOnly().contains(adjacent2));
		assertTrue(s.getIncomingsReadOnly().contains(adjacent4));
		assertTrue(s.getOutgoingsReadOnly().contains(adjacent1));
		assertTrue(s.getOutgoingsReadOnly().contains(adjacent3));

		assertTrue(adjacent1.getIncomingsReadOnly().contains(s));
		assertTrue(adjacent3.getIncomingsReadOnly().contains(s));
		assertTrue(adjacent2.getOutgoingsReadOnly().contains(s));
		assertTrue(adjacent4.getOutgoingsReadOnly().contains(s));

		assertEquals(2, s.getIncomingsReadOnly().size());
		assertEquals(2, s.getOutgoingsReadOnly().size());
		assertEquals(1, adjacent1.getIncomingsReadOnly().size());
		assertEquals(1, adjacent3.getIncomingsReadOnly().size());
		assertEquals(1, adjacent2.getOutgoingsReadOnly().size());
		assertEquals(1, adjacent4.getOutgoingsReadOnly().size());

		assertEquals(0, adjacent2.getIncomingsReadOnly().size());
		assertEquals(0, adjacent4.getIncomingsReadOnly().size());
		assertEquals(0, adjacent1.getOutgoingsReadOnly().size());
		assertEquals(0, adjacent3.getOutgoingsReadOnly().size());
	}

//	@Test
//	public void testRemoveIncoming() {
//		BasicShape s = getBasicShapeToTest("...");
//
//		BasicShape adjacent1 = getBasicShapeOfRandomType("inOutgoing");
//		BasicShape adjacent2 = getBasicShapeOfRandomType("inIncoming");
//		BasicShape adjacent3 = getBasicShapeOfRandomType("inOutgoing2");
//		BasicShape adjacent4 = getBasicShapeOfRandomType("inIncoming2");
//
//		s.addIncomingAndUpdateItsOutgoings(adjacent2);
//		s.addIncomingAndUpdateItsOutgoings(adjacent4);
//		s.addOutgoingAndUpdateItsIncomings(adjacent3);
//		s.addOutgoingAndUpdateItsIncomings(adjacent1);
//
//		s.removeIncoming(adjacent2);
//
//		assertFalse(s.getIncomingsReadOnly().contains(adjacent2));
//		assertTrue(s.getIncomingsReadOnly().contains(adjacent4));
//		assertTrue(s.getOutgoingsReadOnly().contains(adjacent1));
//		assertTrue(s.getOutgoingsReadOnly().contains(adjacent3));
//
//		assertTrue(adjacent1.getIncomingsReadOnly().contains(s));
//		assertTrue(adjacent3.getIncomingsReadOnly().contains(s));
//		assertTrue(adjacent2.getOutgoingsReadOnly().contains(s));
//		assertTrue(adjacent4.getOutgoingsReadOnly().contains(s));
//
//		assertEquals(1, s.getIncomingsReadOnly().size());
//		assertEquals(2, s.getOutgoingsReadOnly().size());
//		assertEquals(1, adjacent1.getIncomingsReadOnly().size());
//		assertEquals(1, adjacent3.getIncomingsReadOnly().size());
//		assertEquals(1, adjacent2.getOutgoingsReadOnly().size());
//		assertEquals(1, adjacent4.getOutgoingsReadOnly().size());
//
//		assertEquals(0, adjacent2.getIncomingsReadOnly().size());
//		assertEquals(0, adjacent4.getIncomingsReadOnly().size());
//		assertEquals(0, adjacent1.getOutgoingsReadOnly().size());
//		assertEquals(0, adjacent3.getOutgoingsReadOnly().size());
//
//		s.removeIncoming(adjacent4);
//
//		assertFalse(s.getIncomingsReadOnly().contains(adjacent2));
//		assertFalse(s.getIncomingsReadOnly().contains(adjacent4));
//		assertTrue(s.getOutgoingsReadOnly().contains(adjacent1));
//		assertTrue(s.getOutgoingsReadOnly().contains(adjacent3));
//
//		assertTrue(adjacent1.getIncomingsReadOnly().contains(s));
//		assertTrue(adjacent3.getIncomingsReadOnly().contains(s));
//		assertTrue(adjacent2.getOutgoingsReadOnly().contains(s));
//		assertTrue(adjacent4.getOutgoingsReadOnly().contains(s));
//
//		assertEquals(0, s.getIncomingsReadOnly().size());
//		assertEquals(2, s.getOutgoingsReadOnly().size());
//		assertEquals(1, adjacent1.getIncomingsReadOnly().size());
//		assertEquals(1, adjacent3.getIncomingsReadOnly().size());
//		assertEquals(1, adjacent2.getOutgoingsReadOnly().size());
//		assertEquals(1, adjacent4.getOutgoingsReadOnly().size());
//
//		assertEquals(0, adjacent2.getIncomingsReadOnly().size());
//		assertEquals(0, adjacent4.getIncomingsReadOnly().size());
//		assertEquals(0, adjacent1.getOutgoingsReadOnly().size());
//		assertEquals(0, adjacent3.getOutgoingsReadOnly().size());
//
//		s.removeOutgoing(adjacent3);
//
//		assertFalse(s.getIncomingsReadOnly().contains(adjacent2));
//		assertFalse(s.getIncomingsReadOnly().contains(adjacent4));
//		assertTrue(s.getOutgoingsReadOnly().contains(adjacent1));
//		assertFalse(s.getOutgoingsReadOnly().contains(adjacent3));
//
//		assertTrue(adjacent1.getIncomingsReadOnly().contains(s));
//		assertTrue(adjacent3.getIncomingsReadOnly().contains(s));
//		assertTrue(adjacent2.getOutgoingsReadOnly().contains(s));
//		assertTrue(adjacent4.getOutgoingsReadOnly().contains(s));
//
//		assertEquals(0, s.getIncomingsReadOnly().size());
//		assertEquals(1, s.getOutgoingsReadOnly().size());
//		assertEquals(1, adjacent1.getIncomingsReadOnly().size());
//		assertEquals(1, adjacent3.getIncomingsReadOnly().size());
//		assertEquals(1, adjacent2.getOutgoingsReadOnly().size());
//		assertEquals(1, adjacent4.getOutgoingsReadOnly().size());
//
//		assertEquals(0, adjacent2.getIncomingsReadOnly().size());
//		assertEquals(0, adjacent4.getIncomingsReadOnly().size());
//		assertEquals(0, adjacent1.getOutgoingsReadOnly().size());
//		assertEquals(0, adjacent3.getOutgoingsReadOnly().size());
//
//		s.removeOutgoing(adjacent1);
//
//		assertFalse(s.getIncomingsReadOnly().contains(adjacent2));
//		assertFalse(s.getIncomingsReadOnly().contains(adjacent4));
//		assertFalse(s.getOutgoingsReadOnly().contains(adjacent1));
//		assertFalse(s.getOutgoingsReadOnly().contains(adjacent3));
//
//		assertTrue(adjacent1.getIncomingsReadOnly().contains(s));
//		assertTrue(adjacent3.getIncomingsReadOnly().contains(s));
//		assertTrue(adjacent2.getOutgoingsReadOnly().contains(s));
//		assertTrue(adjacent4.getOutgoingsReadOnly().contains(s));
//
//		assertEquals(0, s.getIncomingsReadOnly().size());
//		assertEquals(0, s.getOutgoingsReadOnly().size());
//		assertEquals(1, adjacent1.getIncomingsReadOnly().size());
//		assertEquals(1, adjacent3.getIncomingsReadOnly().size());
//		assertEquals(1, adjacent2.getOutgoingsReadOnly().size());
//		assertEquals(1, adjacent4.getOutgoingsReadOnly().size());
//
//		assertEquals(0, adjacent2.getIncomingsReadOnly().size());
//		assertEquals(0, adjacent4.getIncomingsReadOnly().size());
//		assertEquals(0, adjacent1.getOutgoingsReadOnly().size());
//		assertEquals(0, adjacent3.getOutgoingsReadOnly().size());
//
//	}

	@Test
	public void testRemoveIncomingAndUpdateItsOutgoings() {
		BasicShape s = getBasicShapeToTest("...");

		BasicShape adjacent1 = getBasicShapeOfRandomType("inOutgoing");
		BasicShape adjacent2 = getBasicShapeOfRandomType("inIncoming");
		BasicShape adjacent3 = getBasicShapeOfRandomType("inOutgoing2");
		BasicShape adjacent4 = getBasicShapeOfRandomType("inIncoming2");

		s.addIncomingAndUpdateItsOutgoings(adjacent2);
		s.addIncomingAndUpdateItsOutgoings(adjacent4);
		s.addOutgoingAndUpdateItsIncomings(adjacent3);
		s.addOutgoingAndUpdateItsIncomings(adjacent1);

		s.removeIncomingAndUpdateItsOutgoings(adjacent2);

		assertFalse(s.getIncomingsReadOnly().contains(adjacent2));
		assertTrue(s.getIncomingsReadOnly().contains(adjacent4));
		assertTrue(s.getOutgoingsReadOnly().contains(adjacent1));
		assertTrue(s.getOutgoingsReadOnly().contains(adjacent3));

		assertTrue(adjacent1.getIncomingsReadOnly().contains(s));
		assertTrue(adjacent3.getIncomingsReadOnly().contains(s));
		assertFalse(adjacent2.getOutgoingsReadOnly().contains(s));
		assertTrue(adjacent4.getOutgoingsReadOnly().contains(s));

		assertEquals(1, s.getIncomingsReadOnly().size());
		assertEquals(2, s.getOutgoingsReadOnly().size());
		assertEquals(1, adjacent1.getIncomingsReadOnly().size());
		assertEquals(1, adjacent3.getIncomingsReadOnly().size());
		assertEquals(0, adjacent2.getOutgoingsReadOnly().size());
		assertEquals(1, adjacent4.getOutgoingsReadOnly().size());

		assertEquals(0, adjacent2.getIncomingsReadOnly().size());
		assertEquals(0, adjacent4.getIncomingsReadOnly().size());
		assertEquals(0, adjacent1.getOutgoingsReadOnly().size());
		assertEquals(0, adjacent3.getOutgoingsReadOnly().size());

		s.removeIncomingAndUpdateItsOutgoings(adjacent4);

		assertFalse(s.getIncomingsReadOnly().contains(adjacent2));
		assertFalse(s.getIncomingsReadOnly().contains(adjacent4));
		assertTrue(s.getOutgoingsReadOnly().contains(adjacent1));
		assertTrue(s.getOutgoingsReadOnly().contains(adjacent3));

		assertTrue(adjacent1.getIncomingsReadOnly().contains(s));
		assertTrue(adjacent3.getIncomingsReadOnly().contains(s));
		assertFalse(adjacent2.getOutgoingsReadOnly().contains(s));
		assertFalse(adjacent4.getOutgoingsReadOnly().contains(s));

		assertEquals(0, s.getIncomingsReadOnly().size());
		assertEquals(2, s.getOutgoingsReadOnly().size());
		assertEquals(1, adjacent1.getIncomingsReadOnly().size());
		assertEquals(1, adjacent3.getIncomingsReadOnly().size());
		assertEquals(0, adjacent2.getOutgoingsReadOnly().size());
		assertEquals(0, adjacent4.getOutgoingsReadOnly().size());

		assertEquals(0, adjacent2.getIncomingsReadOnly().size());
		assertEquals(0, adjacent4.getIncomingsReadOnly().size());
		assertEquals(0, adjacent1.getOutgoingsReadOnly().size());
		assertEquals(0, adjacent3.getOutgoingsReadOnly().size());

		s.removeOutgoingAndUpdateItsIncomings(adjacent3);

		assertFalse(s.getIncomingsReadOnly().contains(adjacent2));
		assertFalse(s.getIncomingsReadOnly().contains(adjacent4));
		assertTrue(s.getOutgoingsReadOnly().contains(adjacent1));
		assertFalse(s.getOutgoingsReadOnly().contains(adjacent3));

		assertTrue(adjacent1.getIncomingsReadOnly().contains(s));
		assertFalse(adjacent3.getIncomingsReadOnly().contains(s));
		assertFalse(adjacent2.getOutgoingsReadOnly().contains(s));
		assertFalse(adjacent4.getOutgoingsReadOnly().contains(s));

		assertEquals(0, s.getIncomingsReadOnly().size());
		assertEquals(1, s.getOutgoingsReadOnly().size());
		assertEquals(1, adjacent1.getIncomingsReadOnly().size());
		assertEquals(0, adjacent3.getIncomingsReadOnly().size());
		assertEquals(0, adjacent2.getOutgoingsReadOnly().size());
		assertEquals(0, adjacent4.getOutgoingsReadOnly().size());

		assertEquals(0, adjacent2.getIncomingsReadOnly().size());
		assertEquals(0, adjacent4.getIncomingsReadOnly().size());
		assertEquals(0, adjacent1.getOutgoingsReadOnly().size());
		assertEquals(0, adjacent3.getOutgoingsReadOnly().size());

		s.removeOutgoingAndUpdateItsIncomings(adjacent1);

		assertFalse(s.getIncomingsReadOnly().contains(adjacent2));
		assertFalse(s.getIncomingsReadOnly().contains(adjacent4));
		assertFalse(s.getOutgoingsReadOnly().contains(adjacent1));
		assertFalse(s.getOutgoingsReadOnly().contains(adjacent3));

		assertFalse(adjacent1.getIncomingsReadOnly().contains(s));
		assertFalse(adjacent3.getIncomingsReadOnly().contains(s));
		assertFalse(adjacent2.getOutgoingsReadOnly().contains(s));
		assertFalse(adjacent4.getOutgoingsReadOnly().contains(s));

		assertEquals(0, s.getIncomingsReadOnly().size());
		assertEquals(0, s.getOutgoingsReadOnly().size());
		assertEquals(0, adjacent1.getIncomingsReadOnly().size());
		assertEquals(0, adjacent3.getIncomingsReadOnly().size());
		assertEquals(0, adjacent2.getOutgoingsReadOnly().size());
		assertEquals(0, adjacent4.getOutgoingsReadOnly().size());

		assertEquals(0, adjacent2.getIncomingsReadOnly().size());
		assertEquals(0, adjacent4.getIncomingsReadOnly().size());
		assertEquals(0, adjacent1.getOutgoingsReadOnly().size());
		assertEquals(0, adjacent3.getOutgoingsReadOnly().size());

	}

	@Test
	public void testGetOutgoings() {
		testGetIncomings();
	}
	
	
//	@Test
//	public void testSetOutgoings() {
//		testSetIncomings();
//	}

	@Test
	public void testSetOutgoingsAndUpdateTheirIncomings() {
		testSetIncomingsAndUpdateTheirOutgoings();
	}

//	@Test
//	public void testAddOutgoing() {
//		testAddIncoming();
//	}

	@Test
	public void testAddOutgoingAndUpdateItsIncomings() {
		testAddIncomingAndUpdateItsOutgoings();
	}

//	@Test
//	public void testRemoveOutgoing() {
//		testRemoveIncoming();
//	}

	@Test
	public void testRemoveOutgoingAndUpdateItsIncomings() {
		testRemoveIncomingAndUpdateItsOutgoings();
	}

	@Test
	public void testGetConnectedShapes() {
		BasicShape s = getBasicShapeToTest("...");

		BasicShape adjacent1 = getBasicShapeOfRandomType("inOutgoing");
		BasicShape adjacent2 = getBasicShapeOfRandomType("inIncoming");
		BasicShape adjacent3 = getBasicShapeOfRandomType("inOutgoing2");
		BasicShape adjacent4 = getBasicShapeOfRandomType("inIncoming2");

		s.addIncomingAndUpdateItsOutgoings(adjacent2);
		s.addIncomingAndUpdateItsOutgoings(adjacent4);
		s.addOutgoingAndUpdateItsIncomings(adjacent3);
		s.addOutgoingAndUpdateItsIncomings(adjacent1);

		List<BasicShape> l = new ArrayList<BasicShape>();

		l.add(adjacent1);
		l.add(adjacent2);
		l.add(adjacent3);
		l.add(adjacent4);

		for (BasicShape t : l)
			assertTrue(s.getConnectedShapesReadOnly().contains(t));
		for (BasicShape t : s.getConnectedShapesReadOnly())
			assertTrue(l.contains(t));
	}

	@Test
	public void testGetUpperLeft() {

		Point p1 = new Point(rand.nextDouble(), rand.nextDouble());
		Point p2 = new Point(100 + rand.nextDouble(), 100 + rand.nextDouble());
		Point p3 = new Point(100 + 100 + rand.nextDouble(),
				100 + 100 + rand.nextDouble());
		Point p4 = new Point(100 + 100 + 100 + rand.nextDouble(),
				100 + 100 + 100 + rand.nextDouble());

		Bounds b1 = new Bounds(p1, p2);
		Bounds b2 = new Bounds(p2, p3);
		Bounds b3 = new Bounds(p3, p4);

		BasicShape s1 = getBasicShapeToTest("s1");
		s1.setBounds(b1);
		BasicShape s2 = getBasicShapeToTest("s2");
		s2.setBounds(b2);
		BasicShape s3 = getBasicShapeToTest("s3");
		s3.setBounds(b3);
		BasicShape s4 = getBasicShapeToTest("s4");
		s4.setBounds(null);

		assertEquals(s1.getUpperLeft().getX(), p1.getX(), delta);
		assertEquals(s1.getUpperLeft().getY(), p1.getY(), delta);
		assertEquals(s1.getLowerRight().getX(), p2.getX(), delta);
		assertEquals(s1.getLowerRight().getY(), p2.getY(), delta);

		assertEquals(s2.getUpperLeft().getX(), p2.getX(), delta);
		assertEquals(s2.getUpperLeft().getY(), p2.getY(), delta);
		assertEquals(s2.getLowerRight().getX(), p3.getX(), delta);
		assertEquals(s2.getLowerRight().getY(), p3.getY(), delta);

		assertEquals(s3.getUpperLeft().getX(), p3.getX(), delta);
		assertEquals(s3.getUpperLeft().getY(), p3.getY(), delta);
		assertEquals(s3.getLowerRight().getX(), p4.getX(), delta);
		assertEquals(s3.getLowerRight().getY(), p4.getY(), delta);

		assertNull(s4.getLowerRight());
		assertNull(s4.getUpperLeft());

	}

	@Test
	public void testGetLowerRight() {
		testGetUpperLeft();
	}

	@Test
	public void testGetHeight() {

		Point p1 = new Point(rand.nextDouble(), rand.nextDouble());
		Point p2 = new Point(100 + rand.nextDouble(), 100 + rand.nextDouble());
		Point p3 = new Point(100 + 100 + rand.nextDouble(),
				100 + 100 + rand.nextDouble());
		Point p4 = new Point(100 + 100 + 100 + rand.nextDouble(),
				100 + 100 + 100 + rand.nextDouble());

		Bounds b1 = new Bounds(p1, p2);
		Bounds b2 = new Bounds(p2, p3);
		Bounds b3 = new Bounds(p3, p4);

		BasicShape s1 = getBasicShapeToTest("s1");
		s1.setBounds(b1);
		BasicShape s2 = getBasicShapeToTest("s2");
		s2.setBounds(b2);
		BasicShape s3 = getBasicShapeToTest("s3");
		s3.setBounds(b3);

		assertEquals(s1.getHeight(), p2.getY() - p1.getY(), delta);
		assertEquals(s1.getWidth(), p2.getX() - p1.getX(), delta);

		assertEquals(s2.getHeight(), p3.getY() - p2.getY(), delta);
		assertEquals(s2.getWidth(), p3.getX() - p2.getX(), delta);

		assertEquals(s3.getHeight(), p4.getY() - p3.getY(), delta);
		assertEquals(s3.getWidth(), p4.getX() - p3.getX(), delta);

	}

	@Test
	public void testGetWidth() {
		testGetHeight();
	}
	
	
	@Test
	public void testHashCode() {
		BasicShape s =getBasicShapeToTest("testResourceId", "testStencilId");

		assertEquals(s.hashCode(), s.getResourceId().hashCode());

		s = getBasicShapeToTest("1234567890\"!§$%&/()=", "1234567890\"!§$%&/xx()=");
		assertEquals(s.hashCode(), s.getResourceId().hashCode());
	}

	@Test
	public void testEquals() {

		BasicShape d1 = new BasicDiagram("aShape");
		BasicShape s1 = getBasicShapeToTest("aShape");
		BasicShape s2 = getBasicShapeToTest("aShape");
		
		BasicShape s3 = getBasicShapeToTest("anotherShape");
		BasicShape s4 = getBasicShapeToTest(null);
		BasicShape s5 = getBasicShapeToTest(null);
		BasicShape s6 = getBasicShapeOfDifferentType("aShape");

		assertTrue(s1.equals(s1));
		assertTrue(s2.equals(s1));
		assertTrue(s1.equals(s2));
		assertTrue(s2.equals(s2));

		assertFalse(s1.equals(null));
		assertFalse(s2.equals(null));
		assertFalse(s3.equals(null));
		assertFalse(s4.equals(null));
		assertFalse(s5.equals(null));
		assertFalse(s6.equals(null));

		assertFalse(s1.equals(d1));
		assertFalse(s2.equals(d1));
		assertFalse(s3.equals(d1));
		assertFalse(s4.equals(d1));
		assertFalse(s5.equals(d1));

		assertFalse(s1.equals(s3));
		assertFalse(s1.equals(s4));
		assertFalse(s1.equals(s5));
		assertFalse(s1.equals(s6));

		assertTrue(s4.equals(s5));

	}

	@Test
	public void testGetLabelSettingsForReference() {
		LabelSettings lp1 = new LabelSettings();
		LabelSettings lp2 = new LabelSettings();
		LabelSettings lp3 = new LabelSettings();
		LabelSettings lp4 = new LabelSettings();

		lp1.setReference("lp1");
		lp1.setFrom(1);
		lp2.setReference("lp2");
		lp2.setFrom(2);
		lp3.setReference("lp3");
		lp3.setFrom(3);
		lp4.setReference("lp3");
		lp4.setFrom(4);
		
		BasicShape s = getBasicShapeToTest("aShape");
		
		s.setLabelSettings(Arrays.asList(new LabelSettings[]{lp1, lp2, lp3, lp4}));
		
		assertEquals(1, s.getLabelSettingsForReference("lp1").getFrom().longValue());
		assertEquals(2, s.getLabelSettingsForReference("lp2").getFrom().longValue());
		assertEquals(4, s.getLabelSettingsForReference("lp3").getFrom().longValue());
	}

	@Test
	public void testGetLabelSettings() {
		LabelSettings lp1 = new LabelSettings();
		LabelSettings lp2 = new LabelSettings();
		LabelSettings lp3 = new LabelSettings();

		lp1.setReference("lp1");
		lp2.setReference("lp2");
		lp3.setReference("lp3");		
		
		List<LabelSettings> l = new ArrayList<LabelSettings>();
		
		l.add(lp1);
		l.add(lp2);
		l.add(lp3);
		
		BasicShape s = getBasicShapeToTest("aShape");
		
		s.setLabelSettings(l);
		
		for(LabelSettings pos : s.getLabelSettings())
			assertTrue(l.contains(pos));
		
		for(LabelSettings pos : l)
			assertTrue(s.getLabelSettings().contains(pos));
	}

	@Test
	public void testSetLabelSettings() {
		testGetLabelSettings();
	}
	

	@Test
	public void testIsPointIncluded() {
		BasicShape testShape = getBasicShapeWithChildren_Bounds_Dockers();
		BasicShape testChild = testShape.getChildShapesReadOnly().get(0);
		assertFalse(testChild.isPointIncluded(new Point(10, 20)));
		assertTrue(testChild.isPointIncluded(new Point(100, 100)));
	}

	@Test
	public void testIsPointIncludedAbsolute() {
		BasicShape testShape = getBasicShapeWithChildren_Bounds_Dockers();
		
		assertFalse(testShape.isPointIncludedAbsolute(new Point(10.8, 20)));
		assertTrue(testShape.isPointIncludedAbsolute(new Point(243.68, 257.333333)));

		BasicShape parent = new BasicNode("parent");
		BasicShape s = new BasicNode("s");

		Bounds b1 = new Bounds(new Point(1000, 1000), new Point(1100, 1100));
		Bounds b2 = new Bounds(new Point(100, 100), new Point(200, 200));
		parent.setBounds(b1);
		s.setBounds(b2);
		parent.addChildShape(s);

		assertFalse(s.isPointIncludedAbsolute(new Point(150, 150)));
		assertTrue(s.isPointIncludedAbsolute(new Point(1150, 1150)));

	}

	@Test
	public void testHasChild() {
		BasicShape testShape = getBasicShapeWithChildren_Bounds_Dockers();
		BasicShape testChild = testShape.getChildShapesReadOnly().get(0);
		BasicShape testChildChild = testChild.getChildShapesReadOnly().get(0);
		BasicShape other = getBasicShapeOfRandomType("other");
		
		assertTrue(testShape.hasChild(testChild));
		assertTrue(testChild.hasChild(testChildChild));
		assertFalse(testShape.hasChild(testChildChild));
		assertFalse(testShape.hasChild(other));
	}

	@Test
	public void testContains() {
		BasicShape testShape = getBasicShapeWithChildren_Bounds_Dockers();
		BasicShape testChild = testShape.getChildShapesReadOnly().get(0);
		BasicShape testChildChild = testChild.getChildShapesReadOnly().get(0);
		BasicShape other = getBasicShapeOfRandomType("other");
		
		assertTrue(testShape.contains(testChild));
		assertTrue(testChild.contains(testChildChild));
		assertTrue(testShape.contains(testChildChild));
		assertFalse(testShape.contains(other));
	}
	
	
	@Test
	public void testAddDocker1(){
		BasicShape s2 = getBasicShapeToTest("testMe!!");
		
		Point p1 = getRandomPoint();
		Point p2 = getRandomPoint();
		Point p3 = getRandomPoint();
		Point p4 = getRandomPoint();
		
		List<Point> dockers = Arrays.asList(new Point[]{p1,p2,p3,p4});
		
		s2.addDocker(p1);
		s2.addDocker(p2);
		s2.addDocker(p3);
		s2.addDocker(p4);
		
		assertEquals(dockers, s2.getDockersReadOnly());
	}

	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testAddDocker2(){
		BasicShape s = getBasicShapeToTest("testMe3!");
		
		Point p1 = getRandomPoint();
		s.addDocker(p1, -1);
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testAddDocker3(){
		BasicShape s = getBasicShapeToTest("testMe3!");
		
		Point p1 = getRandomPoint();
		s.addDocker(p1, 1);
	}
	
	@Test
	public void testAddDocker4(){
		BasicShape s = getBasicShapeToTest("testMe!");
		
		Point p1 = getRandomPoint();
		Point p2 = getRandomPoint();
		Point p3 = getRandomPoint();
		Point p4 = getRandomPoint();
		
		s.addDocker(p1,0);
		s.addDocker(p2,1);
		s.addDocker(p3,2);
		s.addDocker(p4,1);
		
		assertEquals(4, s.getDockersReadOnly().size());
		assertEquals(p1, s.getDockersReadOnly().get(0));
		assertEquals(p4, s.getDockersReadOnly().get(1));
		assertEquals(p2, s.getDockersReadOnly().get(2));
		assertEquals(p3, s.getDockersReadOnly().get(3));
	}
	
	@Test
	public void testGetDescendantShapes(){
		BasicDiagram d = new BasicDiagram("diagram");
		BasicShape grandfather = getBasicShapeToTest("Grandfather");
		BasicShape father = getBasicShapeOfRandomType("Father");
		BasicShape father2 = getBasicShapeOfRandomType("Father2");
		BasicShape son = getBasicShapeToTest("Son");
		BasicShape son1 = getBasicShapeToTest("Son1");
		BasicShape son2 = getBasicShapeToTest("Son2");
		BasicShape son3 = getBasicShapeToTest("Son3");
		
		d.addChildShape(grandfather);
		grandfather.addChildShape(father);
		grandfather.addChildShape(father2);
		father.addChildShape(son);
		father.addChildShape(son1);
		father2.addChildShape(son3);
		father2.addChildShape(son2);
		
		assertTrue(grandfather.getDescendantShapesReadOnly().contains(father));
		assertTrue(grandfather.getDescendantShapesReadOnly().contains(father2));
		assertTrue(grandfather.getDescendantShapesReadOnly().contains(son));
		assertTrue(grandfather.getDescendantShapesReadOnly().contains(son2));
		assertTrue(grandfather.getDescendantShapesReadOnly().contains(son3));
		assertTrue(grandfather.getDescendantShapesReadOnly().contains(son1));
		
		assertTrue(son2.getAncestorShapesReadOnly().contains(father2));
		assertFalse(son2.getAncestorShapesReadOnly().contains(father));
		assertTrue(son2.getAncestorShapesReadOnly().contains(grandfather));
		assertTrue(son2.getAncestorShapesReadOnly().contains(d));
		assertTrue(son1.getAncestorShapesReadOnly().contains(grandfather));
		assertTrue(son1.getAncestorShapesReadOnly().contains(d));
		assertFalse(son1.getAncestorShapesReadOnly().contains(father2));
		assertTrue(son1.getAncestorShapesReadOnly().contains(father));
		assertTrue(son.getAncestorShapesReadOnly().contains(grandfather));
		assertTrue(son.getAncestorShapesReadOnly().contains(d));
		assertFalse(son.getAncestorShapesReadOnly().contains(father2));
		assertTrue(son.getAncestorShapesReadOnly().contains(father));
		assertTrue(son3.getAncestorShapesReadOnly().contains(grandfather));
		assertTrue(son3.getAncestorShapesReadOnly().contains(d));
		assertTrue(son3.getAncestorShapesReadOnly().contains(father2));
		assertFalse(son3.getAncestorShapesReadOnly().contains(father));
	}
	
	@Test
	public void testGetAncestorShapes(){
		testGetDescendantShapes();
	}

	
	protected Point getRandomPoint(){
		return new Point(rand.nextDouble() * (rand.nextInt(2000) - 1000), rand.nextDouble() * (rand.nextInt(2000) - 1000));
	}
	
	protected static void assertEqualBoundsHelper(Bounds b1, Bounds b2){
		assertEquals(b1.getUpperLeft().getX(), b2.getUpperLeft().getX(), delta);
		assertEquals(b1.getUpperLeft().getY(), b2.getUpperLeft().getY(), delta);
		assertEquals(b1.getLowerRight().getX(), b2.getLowerRight().getX(), delta);
		assertEquals(b1.getLowerRight().getY(), b2.getLowerRight().getY(), delta);
	}
	
	protected static Bounds calculateBoundsFromPointsTestHelper(List<Point> pointlist) {
		List<Point> points = new ArrayList<Point>(pointlist);
		if (points.size() == 0)
			return null;

		double calculatedUpperLeftX;
		double calculatedUpperLeftY;
		double calculatedLowerRightX;
		double calculatedLowerRightY;

		calculatedUpperLeftX = points.get(0).getX();
		calculatedLowerRightX = points.get(0).getX();
		calculatedUpperLeftY = points.get(0).getY();
		calculatedLowerRightY = points.get(0).getY();
		
		points.remove(0);
		
		for(Point p : points){
			if(calculatedLowerRightX < p.getX())
				calculatedLowerRightX = p.getX();
			if(calculatedLowerRightY < p.getY())
				calculatedLowerRightY = p.getY();
			if(calculatedUpperLeftX > p.getX())
				calculatedUpperLeftX = p.getX();
			if(calculatedUpperLeftY > p.getY())
				calculatedUpperLeftY = p.getY();
		}
		

		return new Bounds(
				new Point(calculatedUpperLeftX, calculatedUpperLeftY),
				new Point(calculatedLowerRightX, calculatedLowerRightY));

	}
	
	protected double randomHelper(double from, double to){
		return rand.nextDouble()*(to-from) + from; 
	}
}
