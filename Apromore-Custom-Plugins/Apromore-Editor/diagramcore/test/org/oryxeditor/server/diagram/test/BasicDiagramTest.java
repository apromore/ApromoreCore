package org.oryxeditor.server.diagram.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.StencilSetReference;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicNode;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.oryxeditor.server.diagram.test.util.FileIO;


public class BasicDiagramTest extends BasicShapeTest{

	static final String diagramJSON = "data/test1.json";
	static final String diagramJSON2 = "data/processes/real_diagram_builder_test.json";
	BasicDiagram diagram;
	BasicDiagram diagram2;

	@Before
	public void setUp() throws Exception {
		diagram = BasicDiagramBuilder.parseJson(new JSONObject(FileIO.readWholeFile(diagramJSON)));
		String diagram2String = FileIO.readWholeFile(diagramJSON2);
		diagram2 = BasicDiagramBuilder.parseJson(new JSONObject(diagram2String));
	}

	
	@Test
	public void testGetStencilsetRef() {
		StencilSetReference ssr = new StencilSetReference("aNamespace");
		
		diagram2.setStencilsetRef(ssr);
		
		assertEquals(ssr, diagram2.getStencilsetRef());
	}

	@Test
	public void testSetStencilsetRef() {
		testGetStencilsetRef();
	}

	@Test
	public void testGetSsextensions() {
		testSetSsextensions();	
	}

	@Test
	public void testSetSsextensions() {
		List<String> l = new ArrayList<String>();
		l.add("someSSExtension");
		l.add("anotherSSExtension");
		diagram2.setSsextensions(l);
		
		assertEquals(l, diagram2.getSsextensions());
	}

	@Test
	public void testAddSsextension() {
		List<String> l = new ArrayList<String>();
		l.add("someSSExtension");
		l.add("anotherSSExtension");
		diagram2.setSsextensions(l);
		
		assertEquals(l, diagram2.getSsextensions());
		
		String s = "yetAnotherOne";
		diagram2.addSsextension(s);
		
		assertEquals(3, diagram2.getSsextensions().size());
		assertTrue(diagram2.getSsextensions().contains(s));
	}
	


//	@Test
//	public void testGetAllShapes() {
//		testSetAllShapes();
//	}
//
//	@Test
//	public void testSetAllShapes() {
//		BasicDiagram d = new BasicDiagram("aDiagram");
//		
//		assertNotNull(d.getAllShapes());
//		
//		d = new BasicDiagram("aDiagram");
//		
//		Shape shape2 = new Node("shape2");
//		Shape shape4 = new Node("shape4");
//		
//		List<Shape> ls = new ArrayList<Shape>();
//		ls.add(new Edge("shape1"));
//		ls.add(shape2);
//		ls.add(new Node("shape3"));
//		ls.add(shape4);
//		ls.add(new Edge("shape5"));
//		
//		d.setAllShapes(ls);
//		
//		assertEquals(d.getAllShapes(), ls);		
//	}

	@Test
	public void testGetShapeById() {

		List<String> l = new ArrayList<String>();
		l.add("shape1");
		l.add("shape4");
		l.add("shape6");
		
		BasicShape shape2 = getBasicShapeOfRandomType("shape2");
		BasicShape shape4 = getBasicShapeOfRandomType("shape4");
//		BasicShape shape6 = new BasicShape("shape6");
		
		BasicDiagram d = new BasicDiagram("aDiagram");
		d.addChildShape(getBasicShapeOfRandomType("shape1"));
		d.addChildShape(shape2);
		d.addChildShape(getBasicShapeOfRandomType("shape3"));
		d.addChildShape(shape4);
		d.addChildShape(getBasicShapeOfRandomType("shape5"));
		
		assertNotNull(d.getShapeById("shape1"));
		assertNotNull(d.getShapeById("shape2"));
		assertNotNull(d.getShapeById("shape3"));
		assertNotNull(d.getShapeById("shape4"));
		assertNotNull(d.getShapeById("shape5"));
		assertNull(d.getShapeById("shape6"));
		
		assertEquals(null, d.getShapeById(""));
		assertEquals(null, d.getShapeById(null));
		assertEquals(null, d.getShapeById("canvas"));
		assertEquals(d, d.getShapeById("aDiagram"));
		
		assertEquals(null, diagram.getShapeById(""));
		assertEquals(null, diagram.getShapeById(null));
		assertEquals(diagram, diagram.getShapeById("canvas"));
		
		assertNull(diagram.getShapeById("shape1"));
		assertNull(diagram.getShapeById("shape4"));
		assertNull(diagram.getShapeById("shape6"));
		
		assertNotNull(diagram.getShapeById("sid-CEE425D1-7ACE-4604-A5B7-D914D1619542")); // sequence flow

		assertNotNull(diagram.getShapeById("sid-A02B7A57-7B76-4D03-BAC9-D82C8E34464A")); // sequence flow

		assertNotNull(diagram.getShapeById("sid-AC1DE038-D963-46B9-9D5A-233D95D7F992")); // sequence flow

		assertNotNull(diagram.getShapeById("sid-1195F697-C11F-4DFD-BF5F-01E0FCCDD67D")); // sequence flow

		assertNotNull(diagram.getShapeById("sid-3DD4678A-1BA0-499A-A991-134096F4CF7F")); // sequence flow

		assertNotNull(diagram.getShapeById("sid-DD80D177-5B89-4BDB-AAB2-56E542D31738")); // sequence flow

		assertNotNull(diagram.getShapeById("sid-FF4A7D53-569E-4FAD-BC50-78F4FC94B2B6")); // sequence flow

		assertNotNull(diagram.getShapeById("sid-DA57751D-29C1-4BF5-9036-89C127C8E8DF")); // message flow
	}

	@Test
	public void testGetShapesByIds() {
		
		BasicShape shape1 = new BasicNode("shape1");
		BasicShape shape1e = new BasicEdge("shape1");
		BasicShape shape2 = getBasicShapeOfRandomType("shape2");
		BasicShape shape3 = getBasicShapeOfRandomType("shape3");
		BasicShape shape4 = getBasicShapeOfRandomType("shape4");
		BasicShape shape5 = getBasicShapeOfRandomType("shape5");
		BasicShape shape6 = getBasicShapeOfRandomType("shape6");
		
		BasicDiagram d = new BasicDiagram("aDiagram");
		d.addChildShape(shape1e);
		d.addChildShape(shape2);
		d.addChildShape(shape3);
		d.addChildShape(shape4);
		d.addChildShape(shape5);
		
		List<String> l = new ArrayList<String>();
		l.add("shape1");
		l.add("shape4");
		l.add("shape6");
		List<BasicShape> x = d.getShapesByIds(l);
		assertEquals(x.size(), 2);
		assertTrue(x.contains(shape1e));
		assertTrue(x.contains(shape4));
		assertFalse(x.contains(shape1));
		assertFalse(x.contains(shape6));
		
		l.clear();
		l.add("");
		x = d.getShapesByIds(l);
		assertEquals(0, x.size());

		l.clear();
		l.add(null);
		x = d.getShapesByIds(l);
		assertEquals(0, x.size());
		
		l.clear();
		l.add("canvas");
		x = d.getShapesByIds(l);
		assertEquals(0, x.size());
		
		l.clear();
		l.add("shape1");
		l.add("shape4");
		l.add("shape6");
		
		x = diagram.getShapesByIds(l);
		assertTrue(x.isEmpty());
		
		x = diagram2.getShapesByIds(l);
		assertTrue(x.isEmpty());
		
		l.clear();
		l.add("sid-DA57751D-29C1-4BF5-9036-89C127C8E8DF");
		x = diagram.getShapesByIds(l);
		assertEquals(x.size(), 1);
		assertEquals(x.get(0).getResourceId(), "sid-DA57751D-29C1-4BF5-9036-89C127C8E8DF");
		
	}
	
	
	@Test
	public void testReorderListOfAllShapes(){
		BasicShape shape1 = getBasicShapeOfRandomType("shape1");
		BasicShape shape2 = getBasicShapeOfRandomType("shape2");
		BasicShape shape3 = getBasicShapeOfRandomType("shape3");
		BasicShape shape4 = getBasicShapeOfRandomType("shape4");
		BasicShape shape5 = getBasicShapeOfRandomType("shape5");
		
		BasicDiagram d = new BasicDiagram("aDiagram");
		d.addChildShape(shape1);
		d.addChildShape(shape2);
		d.addChildShape(shape3);
		d.addChildShape(shape4);
		d.addChildShape(shape5);
		
		assertSame(shape1, d.getAllShapesReadOnly().get(0));
		assertSame(shape2, d.getAllShapesReadOnly().get(1));
		assertSame(shape3, d.getAllShapesReadOnly().get(2));
		assertSame(shape4, d.getAllShapesReadOnly().get(3));
		assertSame(shape5, d.getAllShapesReadOnly().get(4));
		assertSame(shape1, d.getChildShapesReadOnly().get(0));
		assertSame(shape2, d.getChildShapesReadOnly().get(1));
		assertSame(shape3, d.getChildShapesReadOnly().get(2));
		assertSame(shape4, d.getChildShapesReadOnly().get(3));
		assertSame(shape5, d.getChildShapesReadOnly().get(4));
		
		//test happy case
		List<String> idsList = Arrays.asList(new String[]{"shape5", "shape3", "shape1", "shape2", "shape4"});
		d.reorderListOfAllShapes(idsList);
		assertSame(shape5, d.getAllShapesReadOnly().get(0));
		assertSame(shape3, d.getAllShapesReadOnly().get(1));
		assertSame(shape1, d.getAllShapesReadOnly().get(2));
		assertSame(shape2, d.getAllShapesReadOnly().get(3));
		assertSame(shape4, d.getAllShapesReadOnly().get(4));
		assertSame(shape1, d.getChildShapesReadOnly().get(0));
		assertSame(shape2, d.getChildShapesReadOnly().get(1));
		assertSame(shape3, d.getChildShapesReadOnly().get(2));
		assertSame(shape4, d.getChildShapesReadOnly().get(3));
		assertSame(shape5, d.getChildShapesReadOnly().get(4));
		
		//back to normal with additional strings
		idsList = Arrays.asList(new String[]{"shape1", "shape0", "shape2", null, "shape3", "", "shape4", "   ", "shape5"});
		d.reorderListOfAllShapes(idsList);
		assertSame(shape1, d.getAllShapesReadOnly().get(0));
		assertSame(shape2, d.getAllShapesReadOnly().get(1));
		assertSame(shape3, d.getAllShapesReadOnly().get(2));
		assertSame(shape4, d.getAllShapesReadOnly().get(3));
		assertSame(shape5, d.getAllShapesReadOnly().get(4));
		assertSame(shape1, d.getChildShapesReadOnly().get(0));
		assertSame(shape2, d.getChildShapesReadOnly().get(1));
		assertSame(shape3, d.getChildShapesReadOnly().get(2));
		assertSame(shape4, d.getChildShapesReadOnly().get(3));
		assertSame(shape5, d.getChildShapesReadOnly().get(4));
		
		//backwards
		idsList = Arrays.asList(new String[]{"blabla", "shape5", "shape4", "huiuiui", "shape3", "shape2", "shape6", "shape1"});
		d.reorderListOfAllShapes(idsList);
		assertSame(shape5, d.getAllShapesReadOnly().get(0));
		assertSame(shape4, d.getAllShapesReadOnly().get(1));
		assertSame(shape3, d.getAllShapesReadOnly().get(2));
		assertSame(shape2, d.getAllShapesReadOnly().get(3));
		assertSame(shape1, d.getAllShapesReadOnly().get(4));
		assertSame(shape1, d.getChildShapesReadOnly().get(0));
		assertSame(shape2, d.getChildShapesReadOnly().get(1));
		assertSame(shape3, d.getChildShapesReadOnly().get(2));
		assertSame(shape4, d.getChildShapesReadOnly().get(3));
		assertSame(shape5, d.getChildShapesReadOnly().get(4));
	}
	

	@Test(expected=IllegalArgumentException.class)
	public void testReorderListOfAllShapesTooFew(){
		BasicShape shape1 = getBasicShapeOfRandomType("shape1");
		BasicShape shape2 = getBasicShapeOfRandomType("shape2");
		BasicShape shape3 = getBasicShapeOfRandomType("shape3");
		BasicShape shape4 = getBasicShapeOfRandomType("shape4");
		BasicShape shape5 = getBasicShapeOfRandomType("shape5");
		
		BasicDiagram d = new BasicDiagram("aDiagram");
		d.addChildShape(shape1);
		d.addChildShape(shape2);
		d.addChildShape(shape3);
		d.addChildShape(shape4);
		d.addChildShape(shape5);
		
		List<String> idsList = Arrays.asList(new String[]{"shape5", "shape3", "shape1", "shape2"});
		d.reorderListOfAllShapes(idsList);
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testReorderListOfAllShapesNotAll(){
		BasicShape shape1 = getBasicShapeOfRandomType("shape1");
		BasicShape shape2 = getBasicShapeOfRandomType("shape2");
		BasicShape shape3 = getBasicShapeOfRandomType("shape3");
		BasicShape shape4 = getBasicShapeOfRandomType("shape4");
		BasicShape shape5 = getBasicShapeOfRandomType("shape5");
		
		BasicDiagram d = new BasicDiagram("aDiagram");
		d.addChildShape(shape1);
		d.addChildShape(shape2);
		d.addChildShape(shape3);
		d.addChildShape(shape4);
		d.addChildShape(shape5);
		
		List<String> idsList = Arrays.asList(new String[]{"shape5", "shape3", "shape1", "shape2", null, "shape6"});
		d.reorderListOfAllShapes(idsList);
	}
	
	
	@Test
	public void testGetShapesAtPosition() {
		BasicDiagram d = new BasicDiagram("aDiagram");

		BasicNode n1 = new BasicNode("n1");
		BasicNode n2 = new BasicNode("n2");
		BasicNode n3 = new BasicNode("n3");
		BasicNode n4 = new BasicNode("n4");
		BasicNode n5 = new BasicNode("n5");

		BasicEdge e1 = new BasicEdge("e1");
		BasicEdge e2 = new BasicEdge("e2");
		BasicEdge e3 = new BasicEdge("e3");
		BasicEdge e4 = new BasicEdge("e4");
		BasicEdge e5 = new BasicEdge("e5");
		
		List<String> idsList = Arrays.asList(new String[]{
				"n1", "n2", "n3", "n4", "n5", "e1", "e2", "e3", "e4", "e5"});
		
		d.addChildShape(n1);
		d.addChildShape(n2);
		d.addChildShape(n3);
		d.addChildShape(n4);
		d.addChildShape(n5);
		d.addChildShape(e1);
		d.addChildShape(e2);
		d.addChildShape(e3);
		d.addChildShape(e4);
		d.addChildShape(e5);		

		// n2, n4, e3, e5 shall be at point (10, 100)

		n2.setBounds(new Bounds(new Point(randomHelper(-400, -40),
				randomHelper(10, 100)), new Point(randomHelper(10, 100),
				randomHelper(100, 500))));

		n4.setBounds(new Bounds(new Point(randomHelper(9, 9), randomHelper(10,
				10)), new Point(randomHelper(100, 200), randomHelper(300, 400))));

		e3.addDocker(new Point(randomHelper(5, 7), randomHelper(10, 90)));
		e3.addDocker(new Point(randomHelper(10, 20), randomHelper(100, 1000)));

		e5.addDocker(new Point(randomHelper(-100, -40), randomHelper(0, 100)));
		e5.addDocker(new Point(randomHelper(40, 400), randomHelper(100, 300)));

		// the others not
		n1.setBounds(new Bounds(new Point(randomHelper(-40, -400),
				randomHelper(10, 100)), new Point(randomHelper(-10, -100),
				randomHelper(50, 500))));

		n3.setBounds(new Bounds(new Point(randomHelper(40, 400), randomHelper(
				-10, -100)), new Point(randomHelper(40, 100), randomHelper(-50,
				-500))));

		n5.setBounds(new Bounds(new Point(randomHelper(-400, -40),
				randomHelper(10, 100)), new Point(randomHelper(-100, -10),
				randomHelper(50, 500))));

		e1.addDocker(new Point(randomHelper(5, 7), randomHelper(-90, -10)));
		e1.addDocker(new Point(randomHelper(10, 20), randomHelper(-1000, -100)));

		e2.addDocker(new Point(randomHelper(-7, -5), randomHelper(10, 90)));
		e2.addDocker(new Point(randomHelper(-20, -10), randomHelper(100, 1000)));

		e4.addDocker(new Point(randomHelper(5, 7), randomHelper(-90, -10)));
		e4.addDocker(new Point(randomHelper(-20, -10), randomHelper(100, 1000)));

		Collections.shuffle(idsList);
		d.reorderListOfAllShapes(idsList);
		
		List<BasicShape> x = d.getShapesAtPosition(new Point(10, 100));

		assertEquals(4, x.size());
		assertTrue(x.contains(n2));
		assertTrue(x.contains(n4));
		assertTrue(x.contains(e3));
		assertTrue(x.contains(e5));
	
		
	}

	@Test
	public void testGetParent() {

		BasicDiagram d = new BasicDiagram("aDiagram");
		d.setParent(diagram);
		assertNull(d.getParent());

		BasicShape parent = getBasicShapeOfRandomType("parent");
		d.setParent(parent);
		assertNull(d.getParent());
	}
	
	@Test
	public void testSetParent() {
		testGetParent();
	}	

//	@Test
//	public void testGetString() {
//		// does not work... and, there's no point to it...
////		assertEquals(diagram.getString(), FileIO.readWholeFile(diagramJSON));
//		throw new NotImplementedException();
//		
//	}
//
//	@Test
//	public void testGetJSON() throws JSONException {
//		// actually, this is already done during parsing... and, it fails :D
////		assertEquals(diagram.getJSON(),	new JSONObject(FileIO.readWholeFile(diagramJSON)));
//		throw new NotImplementedException();
//	}

	@Test
	public void testGetDiagram() {

		BasicDiagram d = new BasicDiagram("aDiagram");

		d.setDiagram(new BasicDiagram("anotherDiagram"));
		diagram.setDiagram(new BasicDiagram("yetAnotherDiagram"));
		diagram2.setDiagram(new BasicDiagram("yetAnotherDiagram2"));

		assertEquals(d, d.getDiagram());

		assertEquals(diagram, diagram.getDiagram());

		assertEquals(diagram2, diagram2.getDiagram());
	}

	@Test
	public void testSetDiagram() {
		// has been tested there
		testGetDiagram();
	}
	
	/**
	 * Same as super.testAddChildShape, but without two diagrams (diagram + getShapeToTest)
	 */
	@Override
	public void testAddChildShape() {
		// has been tested partly
		testGetChildShapes();

		// test if parent adjusted
		BasicShape child = getBasicShapeOfRandomType("child1");
		BasicShape child2 = getBasicShapeOfRandomType("child2");
		BasicDiagram parent = new BasicDiagram("diagram");

		parent.addChildShape(child);
		parent.addChildShape(child2);

		assertSame(parent, child.getParent());
		assertTrue(parent.getChildShapesReadOnly().contains(child));
		assertEquals(2, parent.getChildShapesReadOnly().size());
		assertTrue(parent.getAllShapesReadOnly().contains(child));
		assertFalse(parent.getAllShapesReadOnly().contains(parent));
	}

	@Override
	public void testRemoveChildShape() {
		// test if parent adjusted
		BasicShape child = getBasicShapeOfRandomType("child1");
		BasicShape child2 = getBasicShapeOfRandomType("child2");
		BasicDiagram parent = new BasicDiagram("diagram");

		parent.addChildShape(child);
		parent.addChildShape(child2);

		assertSame(parent, child.getParent());
		assertSame(parent, child2.getParent());
		assertTrue(parent.getChildShapesReadOnly().contains(child));
		assertTrue(parent.getChildShapesReadOnly().contains(child2));
		assertEquals(2, parent.getChildShapesReadOnly().size());
		assertTrue(parent.getAllShapesReadOnly().contains(child));
		assertTrue(parent.getAllShapesReadOnly().contains(child2));
		assertFalse(parent.getAllShapesReadOnly().contains(parent));

		parent.removeChildShape(child);

		assertNull(child.getParent());
		assertSame(parent, child2.getParent());
		assertFalse(parent.getChildShapesReadOnly().contains(child));
		assertTrue(parent.getChildShapesReadOnly().contains(child2));
		assertEquals(1, parent.getChildShapesReadOnly().size());
		assertFalse(parent.getAllShapesReadOnly().contains(child));
		assertTrue(parent.getAllShapesReadOnly().contains(child2));
		assertFalse(parent.getAllShapesReadOnly().contains(parent));

		parent.removeChildShape(child2);

		assertNull(child.getParent());
		assertNull(child2.getParent());
		assertFalse(parent.getChildShapesReadOnly().contains(child));
		assertFalse(parent.getChildShapesReadOnly().contains(child2));
		assertEquals(0, parent.getChildShapesReadOnly().size());
		assertFalse(parent.getAllShapesReadOnly().contains(child));
		assertFalse(parent.getAllShapesReadOnly().contains(child2));
		assertFalse(parent.getAllShapesReadOnly().contains(parent));
	}
	
	/**
	 * Absolute bounds always same to relative bounds, because diagrams can't have a parent!
	 */
	@Override
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

//		Bounds temp1 = father.getBounds().clone();
//		temp1.moveBy(grandfather.getUpperLeft());
//
//		Bounds temp2 = son.getBounds().clone();
//		temp2.moveBy(father.getAbsoluteBounds().getUpperLeft());

		assertEquals(grandfather.getBounds().getUpperLeft().getX(), 
				grandfather.getAbsoluteBounds().getUpperLeft().getX(), delta);
		assertEquals(grandfather.getBounds().getUpperLeft().getY(), 
				grandfather.getAbsoluteBounds().getUpperLeft().getY(), delta);
		assertEquals(grandfather.getBounds().getLowerRight().getX(),
				grandfather.getAbsoluteBounds().getLowerRight().getX(), delta);
		assertEquals(grandfather.getBounds().getLowerRight().getY(),
				grandfather.getAbsoluteBounds().getLowerRight().getY(), delta);

		assertEquals(father.getBounds().getUpperLeft().getX(),
				father.getAbsoluteBounds().getUpperLeft().getX(), delta);
		assertEquals(father.getBounds().getUpperLeft().getY(), 
				father.getAbsoluteBounds().getUpperLeft().getY(), delta);
		assertEquals(father.getBounds().getLowerRight().getX(),
				father.getAbsoluteBounds().getLowerRight().getX(), delta);
		assertEquals(father.getBounds().getLowerRight().getY(),
				father.getAbsoluteBounds().getLowerRight().getY(), delta);
		
		assertEquals(son.getBounds().getUpperLeft().getX(), 
				son.getAbsoluteBounds().getUpperLeft().getX(), delta);
		assertEquals(son.getBounds().getUpperLeft().getY(), 
				son.getAbsoluteBounds().getUpperLeft().getY(), delta);
		assertEquals(son.getBounds().getLowerRight().getX(),
				son.getAbsoluteBounds().getLowerRight().getX(), delta);
		assertEquals(son.getBounds().getLowerRight().getY(),
				son.getAbsoluteBounds().getLowerRight().getY(), delta);
	}

	@Override
	public void testEquals() {
		BasicShape d1 = new BasicDiagram("aShape");
		BasicShape s1 = new BasicDiagram("aShape");
		BasicShape s2 = getBasicShapeOfDifferentType("aShape");
		BasicShape s3 = new BasicDiagram("anotherShape");
		BasicShape s4 = new BasicDiagram(null);		

		assertTrue(s1.equals(s1));
		assertTrue(d1.equals(s1));
		assertTrue(s1.equals(d1));
		assertTrue(d1.equals(d1));

		assertFalse(s1.equals(null));
		assertFalse(s2.equals(null));
		assertFalse(s3.equals(null));
		assertFalse(s4.equals(null));

		assertFalse(s2.equals(d1));
		assertFalse(s3.equals(d1));
		assertFalse(s4.equals(d1));

		assertFalse(s2.equals(s1));
		assertFalse(s3.equals(s1));
		assertFalse(s4.equals(s1));
	}

	@Override
	public void testGetDescendantShapes() {
		BasicDiagram d = new BasicDiagram("diagram");
		BasicShape grandfather = getBasicShapeOfRandomType("Grandfather");
		BasicShape father = getBasicShapeOfRandomType("Father");
		BasicShape father2 = getBasicShapeOfRandomType("Father2");
		BasicShape son = getBasicShapeOfRandomType("Son");
		BasicShape son1 = getBasicShapeOfRandomType("Son1");
		BasicShape son2 = getBasicShapeOfRandomType("Son2");
		BasicShape son3 = getBasicShapeOfRandomType("Son3");
		
		d.addChildShape(grandfather);
		grandfather.addChildShape(father);
		grandfather.addChildShape(father2);
		father.addChildShape(son);
		father.addChildShape(son1);
		father2.addChildShape(son3);
		father2.addChildShape(son2);
		
		assertTrue(d.getDescendantShapesReadOnly().contains(grandfather));
		assertTrue(d.getDescendantShapesReadOnly().contains(father));
		assertTrue(d.getDescendantShapesReadOnly().contains(father2));
		assertTrue(d.getDescendantShapesReadOnly().contains(son));
		assertTrue(d.getDescendantShapesReadOnly().contains(son2));
		assertTrue(d.getDescendantShapesReadOnly().contains(son3));
		assertTrue(d.getDescendantShapesReadOnly().contains(son1));
	}

	@Override
	public void testGetAncestorShapes() {
		//will always be empty!
		BasicDiagram d = new BasicDiagram("diagram");
		BasicShape grandfather = getBasicShapeOfRandomType("Grandfather");
		BasicShape father = getBasicShapeOfRandomType("Father");
		
		grandfather.addChildShape(father);
		father.addChildShape(d);
		
		assertFalse(d.getAncestorShapesReadOnly().contains(grandfather));
		assertFalse(d.getAncestorShapesReadOnly().contains(father));
		assertTrue(d.getAncestorShapesReadOnly().isEmpty());
	}
	
	
	@Override
	public void testRemoveAllChildShapes() {
		//preparation
		BasicDiagram parent = new BasicDiagram("parent");
		BasicShape child = getBasicShapeOfRandomType("child1");
		BasicShape child2 = getBasicShapeOfRandomType("child2");
		BasicShape grandChild = getBasicShapeOfRandomType("grandchild1");

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
		
		assertTrue(parent.getAllShapesReadOnly().contains(child));
		assertTrue(parent.getAllShapesReadOnly().contains(child2));
		assertTrue(parent.getAllShapesReadOnly().contains(grandChild));
		assertFalse(parent.getAllShapesReadOnly().contains(parent));
		
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
		
		assertFalse(parent.getAllShapesReadOnly().contains(child));
		assertFalse(parent.getAllShapesReadOnly().contains(child2));
		assertFalse(parent.getAllShapesReadOnly().contains(grandChild));
		assertFalse(parent.getAllShapesReadOnly().contains(parent));
	}

	@Test
	public final void testGetAllShapes() {
		fail("Not yet implemented"); // TODO
	}


	@Test
	public final void testGetString() {
		
		fail("Not yet implemented"); // TODO
	}


	@Test
	public final void testGetJSON() throws JSONException {
		BasicDiagram parent = new BasicDiagram("parent");
		BasicShape child = getBasicShapeOfRandomType("child1");
		BasicShape child2 = getBasicShapeOfRandomType("child2");
		BasicShape grandChild = getBasicShapeOfRandomType("grandchild1");

		parent.addChildShape(child);
		child.addChildShape(grandChild);
		parent.addChildShape(child2);
		
		parent.getJSON();
		fail("Not yet implemented"); // TODO
	}


	@Override
	protected BasicShape getBasicShapeToTest(String id) {
		return new BasicDiagram(id);
	}


	@Override
	protected BasicShape getBasicShapeToTest(String id, String stencilId) {
		return new BasicDiagram(id, stencilId);
	}


	@Override
	protected BasicShape getBasicShapeOfDifferentType(String id) {
		BasicShape shape = getBasicShapeOfRandomType(id);
		while (shape instanceof BasicDiagram){
			shape = getBasicShapeOfRandomType(id);
		}
		return shape;
	}


	@Override
	protected BasicShape getBasicShapeWithChildren_Bounds_Dockers() {
		BasicShape testShape = new BasicDiagram("test1");
		Bounds b = new Bounds(new Point(100.26535, 200.14159), new Point(
				300.89793, 400.23846));
		testShape.setBounds(b);
		List<Point> dockers1 = new ArrayList<Point>();
		dockers1.add(new Point(b.getCenter()));
		testShape.setDockers(dockers1);
		
		BasicShape testChild = new BasicNode(
				"subshape", 
				"SubShape");
		// relative to parent shape!
		Bounds b2 = new Bounds(new Point(10.1, 10.2), new Point(120.3, 120.4));
		testChild.setBounds(b2);
		List<Point> dockers2 = new ArrayList<Point>();
		dockers2.add(new Point(b2.getCenter()));
		testChild.setDockers(dockers2);

		BasicShape testChildChild = new BasicNode(
				"subsubshape2",
				"SubShape");
		Bounds b3 = new Bounds(new Point(20.56, 30.57), new Point(100.00,
				99.999));
		testChildChild.setBounds(b3);
		List<Point> dockers3 = new ArrayList<Point>();
		dockers3.add(new Point(b3.getCenter()));
		testChildChild.setDockers(dockers3);

		testChild.addChildShape(testChildChild);
		testShape.addChildShape(testChild);

		return testShape;
	}


	@Override
	public void testGetQualifiedStencilId() {
		BasicDiagram diagram = new BasicDiagram("");
		diagram.setStencilId("aStencilId");
		assertEquals("aStencilId", diagram.getQualifiedStencilId());

		diagram = new BasicDiagram("");
		diagram.setStencilId("!\"§$%&/()=123456789äöüß*");
		assertEquals("!\"§$%&/()=123456789äöüß*", diagram.getQualifiedStencilId());

		diagram = new BasicDiagram("");
		diagram.setStencilId(null);
		assertNull(diagram.getQualifiedStencilId());

		diagram = new BasicDiagram("");
		diagram.setStencilId("StencilRef");
		assertEquals("StencilRef", diagram.getQualifiedStencilId());
		
		diagram = new BasicDiagram("");
		diagram.setStencilId("someStencilId");
		diagram.setStencilsetRef(new StencilSetReference("someStencilset"));
		assertEquals("someStencilset/someStencilId", diagram.getQualifiedStencilId());
		
		diagram.setStencilsetRef(new StencilSetReference(""));
		assertEquals("/someStencilId", diagram.getQualifiedStencilId());
		
		diagram.setStencilsetRef(new StencilSetReference(null));
		assertEquals("someStencilId", diagram.getQualifiedStencilId());
		
		diagram.setStencilsetRef(null);
		assertEquals("someStencilId", diagram.getQualifiedStencilId());
	}

}
