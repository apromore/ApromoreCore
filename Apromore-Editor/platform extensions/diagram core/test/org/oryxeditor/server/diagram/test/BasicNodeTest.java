package org.oryxeditor.server.diagram.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.basic.BasicNode;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.oryxeditor.server.diagram.exception.TooManyDockersException;


public class BasicNodeTest extends BasicShapeTest{

	@Override
	protected BasicShape getBasicShapeToTest(String id, String stencilId) {
		return new BasicNode(id, stencilId);
	}


	@Override
	protected BasicShape getBasicShapeToTest(String id) {
		return new BasicNode(id);
	}

	@Override
	protected BasicShape getBasicShapeOfDifferentType(String id) {
		BasicShape shape = getBasicShapeOfRandomType(id);
		while (shape instanceof BasicNode){
			shape = getBasicShapeOfRandomType(id);
		}
		return shape;
	}


	@Override
	protected BasicShape getBasicShapeWithChildren_Bounds_Dockers() {
		BasicShape testShape = new BasicNode("test1");
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
	public void testGetDockers() {
		Point p = new Point(456.321, 98907.3134);
		Point p2 = new Point(-93214792174.03, .3234);

		List<Point> l = new ArrayList<Point>();
		l.add(p);

		BasicNode node = new BasicNode("test");
		assertNotNull(node.getDockersReadOnly());
		assertTrue(node.getDockersReadOnly().isEmpty());
		
		node.setDockers(null);
		assertNotNull(node.getDockersReadOnly());
		assertTrue(node.getDockersReadOnly().isEmpty());
		
		node.setDockers(l);
		assertEquals(l, node.getDockersReadOnly());
		
		l.clear();
		l.add(p2);
		node.setDockers(l);
		assertEquals(l, node.getDockersReadOnly());
		
		l.clear();
		l.add(p);
		assertFalse(l.equals(node.getDockersReadOnly()));
	}

	@Override
	public void testSetDockers() {
		testGetDockers();
	}
	
	@Test(expected=TooManyDockersException.class)
	public void testSetDockersException(){
		Point p = new Point(456.321, 98907.3134);
		Point p2 = new Point(-93214792174.03, .3234);

		List<Point> l = new ArrayList<Point>();
		l.add(p);
		l.add(p2);
		
		new BasicNode("test").setDockers(l);
	}

	@Override
	public void testAddDocker1() {
		BasicNode node = new BasicNode("test");
		Point p1 = new Point(1, 1);
		Point p2 = new Point(2, 2);
		
		assertTrue(node.getDockersReadOnly().isEmpty());
		node.addDocker(p1);
		assertFalse(node.getDockersReadOnly().isEmpty());
		assertEquals(p1, node.getDockersReadOnly().get(0));
		
		node.setDockers(null);
		assertTrue(node.getDockersReadOnly().isEmpty());
		node.addDocker(p2);
		assertEquals(p2, node.getDockersReadOnly().get(0));
	}
	
	@Override
	public void testAddDocker4(){
		BasicNode node = new BasicNode("test");
		Point p1 = new Point(1, 1);
		Point p2 = new Point(2, 2);
		node.addDocker(p1,0);
		assertEquals(p1, node.getDockersReadOnly().get(0));
		
		node.setDockers(null);
		node.addDocker(p2,0);
		assertEquals(p2, node.getDockersReadOnly().get(0));
	}

	
	@Test(expected=TooManyDockersException.class)
	public void testAddDockerException1(){
		BasicNode node = new BasicNode("test");
		node.addDocker(new Point(1, 1));
		
		node.addDocker(new Point(2, 2));
	}
	
	@Test(expected=TooManyDockersException.class)
	public void testAddDockerException2(){
		BasicNode node = new BasicNode("test");
		node.addDocker(new Point(1, 1),0);
		
		node.addDocker(new Point(2, 2),1);
	}
}
