package org.oryxeditor.server.diagram.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicShape;


public class BasicEdgeTest extends BasicShapeTest{

	@Test
	public void testGetSource() {
		BasicEdge e = new BasicEdge("anEdge");

		BasicShape source = getBasicShapeOfRandomType("source");

		e.setSourceAndUpdateIncomings(source);

		assertEquals(source, e.getSource());

	}

	@Test
	public void testSetSourceAndUpdateIncomings() {
		BasicEdge e = new BasicEdge("anEdge");

		BasicShape source = getBasicShapeOfRandomType("source");

		e.setSourceAndUpdateIncomings(source);

		assertEquals(source, e.getSource());
		assertNull(e.getTarget());
		assertEquals(0, e.getOutgoingsReadOnly().size());
		assertEquals(1, e.getIncomingsReadOnly().size());
		assertEquals(0, source.getIncomingsReadOnly().size());
		assertEquals(0, source.getOutgoingsReadOnly().size());
	}


	@Test
	public void testGetTarget() {
		BasicEdge e = new BasicEdge("anEdge");
		BasicShape target = getBasicShapeOfRandomType("target");
		e.setTargetAndUpdateOutgoings(target);
		assertEquals(target, e.getTarget());
	}

	@Test
	public void testSetTargetAndUpdateOutgoings() {
		BasicEdge e = new BasicEdge("anEdge");
		BasicShape target = getBasicShapeOfRandomType("target");

		e.setTargetAndUpdateOutgoings(target);

		assertEquals(target, e.getTarget());
		assertNull(e.getSource());
		assertEquals(1, e.getOutgoingsReadOnly().size());
		assertEquals(0, e.getIncomingsReadOnly().size());
		assertEquals(0, target.getIncomingsReadOnly().size());
		assertEquals(0, target.getOutgoingsReadOnly().size());

	}
	
	@Ignore //TODO rewrite, to actually test 'addDocker'
	@Test
	public void testAddDockerEdge1() {
		
		BasicShape source = getBasicShapeOfRandomType("aSource");
		
		BasicEdge e = new BasicEdge("anEdge");
		
//		e.connectToASource(source);
		
		List<Point> l = new ArrayList<Point>();
		Random r = new Random(System.currentTimeMillis());
		Point p1 = new Point(r.nextDouble() * (r.nextInt(2000) - 1000),
				r.nextDouble() * (r.nextInt(2000) - 1000));
		Point p2 = new Point(r.nextDouble() * (r.nextInt(2000) - 1000),
				r.nextDouble() * (r.nextInt(2000) - 1000));
		Point p3 = new Point(r.nextDouble() * (r.nextInt(2000) - 1000),
				r.nextDouble() * (r.nextInt(2000) - 1000));
		Point p4 = new Point(r.nextDouble() * (r.nextInt(2000) - 1000),
				r.nextDouble() * (r.nextInt(2000) - 1000));
		
		l.add(p1);
		l.add(p2);
		l.add(p3);
		l.add(p4);
		e.setDockers(l);
	
		ArrayList<Point> l2 = new ArrayList<Point>();
		for(Point p : l)
			l2.add(p.copy());
		Point exceptionPoint = l2.get(0);
		exceptionPoint.moveBy(-source.getUpperLeft().getX(), -source.getUpperLeft().getY());
		
		assertEqualBoundsHelper(calculateBoundsFromPointsTestHelper(l2), e.getBounds());
		
	}
	
	@Ignore //TODO rewrite, to actually test 'addDocker'
	@Test
	public void testAddDockerEdge2() {
		BasicShape target = getBasicShapeOfRandomType("aTarget");
		

		BasicEdge e = new BasicEdge("anEdge");
		
//		e.connectToATarget(target);
		
		List<Point> l = new ArrayList<Point>();
		Random r = new Random(System.currentTimeMillis());
		Point p1 = new Point(r.nextDouble() * (r.nextInt(2000) - 1000),
				r.nextDouble() * (r.nextInt(2000) - 1000));
		Point p2 = new Point(r.nextDouble() * (r.nextInt(2000) - 1000),
				r.nextDouble() * (r.nextInt(2000) - 1000));
		Point p3 = new Point(r.nextDouble() * (r.nextInt(2000) - 1000),
				r.nextDouble() * (r.nextInt(2000) - 1000));
		Point p4 = new Point(r.nextDouble() * (r.nextInt(2000) - 1000),
				r.nextDouble() * (r.nextInt(2000) - 1000));
		
		l.add(p1);
		l.add(p2);
		l.add(p3);
		l.add(p4);
		e.setDockers(l);
	
		ArrayList<Point> l2 = new ArrayList<Point>();
		for(Point p : l)
			l2.add(p.copy());
		Point exceptionPoint = l2.get(l2.size() - 1);
		exceptionPoint.moveBy(-target.getUpperLeft().getX(), -target.getUpperLeft().getY());
		
		assertEqualBoundsHelper(calculateBoundsFromPointsTestHelper(l2), e.getBounds());
	}
	
	@Test
	public void testSetDockers(){
		BasicEdge e = new BasicEdge("anEdge");
		
		List<Point> l = new ArrayList<Point>();
		Random r = new Random(System.currentTimeMillis());
		Point p1 = new Point(r.nextDouble() * (r.nextInt(2000) - 1000),
				r.nextDouble() * (r.nextInt(2000) - 1000));
		Point p2 = new Point(r.nextDouble() * (r.nextInt(2000) - 1000),
				r.nextDouble() * (r.nextInt(2000) - 1000));
		l.add(p1);
		l.add(p2);
		
		e.setDockers(l);
		
		Bounds b = new Bounds(p1, p2);
		
		assertEqualBoundsHelper(b, e.getBounds());
		
		Point p3 = new Point(r.nextDouble() * (r.nextInt(2000) - 1000),
				r.nextDouble() * (r.nextInt(2000) - 1000));
		Point p4 = new Point(r.nextDouble() * (r.nextInt(2000) - 1000),
				r.nextDouble() * (r.nextInt(2000) - 1000));
		l.add(p3);
		l.add(p4);
		
		e.setDockers(l);

		assertEqualBoundsHelper(calculateBoundsFromPointsTestHelper(l), e.getBounds());
	}


	@Override
	protected BasicShape getBasicShapeToTest(String id) {
		return new BasicEdge(id);
	}

	@Override
	protected BasicShape getBasicShapeToTest(String id, String stencilId) {
		return new BasicEdge(id, stencilId);
	}

	@Override
	protected BasicShape getBasicShapeOfDifferentType(String id) {
		BasicShape shape = getBasicShapeOfRandomType(id);
		while (shape instanceof BasicEdge){
			shape = getBasicShapeOfRandomType(id);
		}
		return shape;
	}

	@Override
	protected BasicShape getBasicShapeWithChildren_Bounds_Dockers() {
		BasicShape testShape = getBasicShapeToTest("test1");
		testShape.addDocker(new Point(100.26535, 200.14159));
		testShape.addDocker(new Point(300.89793, 400.23846));
		
		BasicShape testChild = getBasicShapeToTest(
				"subshape", 
				"SubShape");
		// relative to parent shape!
		testChild.addDocker(new Point(10.1, 10.2));
		testChild.addDocker(new Point(120.3, 120.4));

		BasicShape testChildChild = getBasicShapeToTest(
				"subsubshape2",
				"SubShape");
		testChildChild.addDocker(new Point(20.56, 30.57));
		testChildChild.addDocker(new Point(100.00, 99.999));

		testChild.addChildShape(testChildChild);
		testShape.addChildShape(testChild);

		return testShape;
	}

}
