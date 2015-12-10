/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.bpmncmap.parser;

import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.List;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.BDDPairing;
import net.sf.javabdd.JFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test suite for {@link Parser}.
 */
public class ParserTest {

  @Test
  public void test1() throws Exception {
    BDDFactory factory = JFactory.init(100, 100);
    System.out.println("Set BDD vars to 10; old BDD vars was " + factory.setVarNum(10));
    System.out.println("0:" + factory.zero());
    System.out.println("1:" + factory.one());
    System.out.println("x:" + factory.ithVar(0));
    System.out.println("~x:" + factory.nithVar(0));
    System.out.println("y:" + factory.ithVar(1));
    System.out.println("x.y:" + factory.ithVar(0).and(factory.ithVar(1)));
    System.out.println("x+y:" + factory.ithVar(0).or(factory.ithVar(1)));
    System.out.println("x|y:" + factory.ithVar(0).xor(factory.ithVar(1)));
    BDD bdd = factory.ithVar(0).xor(factory.ithVar(1)).not();
    System.out.println("~(x|y):" + bdd);
  }

  @Test
  public void testTrue() throws Exception {
     Parser parser = new Parser(new StringBufferInputStream("1"));
     parser.init();
     BDD bdd = parser.AdditiveExpression();
     assertTrue(bdd.isOne());
  }

  @Test
  public void testFalse() throws Exception {
     Parser parser = new Parser(new StringBufferInputStream("0"));
     parser.init();
     BDD bdd = parser.AdditiveExpression();
     assertTrue(bdd.isZero());
  }

  @Test
  public void testX() throws Exception {
     Parser parser = new Parser(new StringBufferInputStream("f1"));
     parser.init();
     BDD bdd = parser.AdditiveExpression();
     assertEquals(parser.findVariable("f1"), bdd);

     BDDFactory factory = parser.getFactory();
     /*
     BDDPairing g = factory.makePair();
     g.set(0, factory.one());
     //g.set(1, factory.one());
     //g.set(2, factory.one());
     BDD bdd2 = bdd.replace(g);
     */
     BDD g = parser.findVariable("f1").not().and(parser.findVariable("f2"));;
     BDD bdd2 = bdd.restrict(g);
     assertEquals(factory.zero(), bdd2);
  }

  @Test
  public void testNotX() throws Exception {
     Parser parser = new Parser(new StringBufferInputStream("-f1"));
     parser.init();
     BDD bdd = parser.AdditiveExpression();
     assertEquals(parser.findVariable("f1").not(), bdd);
  }

  @Test
  public void testXAndY() throws Exception {
     Parser parser = new Parser(new StringBufferInputStream("f1.f2"));
     parser.init();
     BDD bdd = parser.AdditiveExpression();
     assertEquals(parser.findVariable("f1").and(parser.findVariable("f2")), bdd);
  }

  @Test
  public void testXOrY() throws Exception {
     Parser parser = new Parser(new StringBufferInputStream("f1+f2"));
     parser.init();
     BDD bdd = parser.AdditiveExpression();
     assertEquals(parser.findVariable("f1").or(parser.findVariable("f2")), bdd);
  }

  @Test
  public void testXorFunction() throws Exception {
    Parser parser = new Parser(new StringBufferInputStream("xor(f1, f2)"));
    parser.init();
    Parser.XorFunction xorFunction = parser.new XorFunction();
    List<BDD> parameterList = new ArrayList<>(2);
    parameterList.add(parser.findVariable("f1"));
    parameterList.add(parser.findVariable("f2"));

    BDD bdd = xorFunction.eval(parameterList);
    assertEquals(parser.findVariable("f1").xor(parser.findVariable("f2")), bdd);
  }

  @Test
  public void testXor() throws Exception {
    Parser parser = new Parser(new StringBufferInputStream("xor"));
    parser.init();
    Parser.NaryFunction function = parser.Function();
    assertTrue(function.getClass().equals(Parser.ExactlyOneFunction.class));
  }

  @Test
  public void testParameterList() throws Exception {
    Parser parser = new Parser(new StringBufferInputStream("(f1, f2)"));
    parser.init();
    List<BDD> parameterList = parser.ParameterList();
    assertEquals(2, parameterList.size());
    assertEquals(parser.findVariable("f1"), parameterList.get(0));
    assertEquals(parser.findVariable("f2"), parameterList.get(1));
  }

  @Test
  public void testXXorY() throws Exception {
     Parser parser = new Parser(new StringBufferInputStream("xor(f1, f2)"));
     parser.init();
     BDD bdd = parser.AdditiveExpression();
     assertEquals(parser.findVariable("f1").xor(parser.findVariable("f2")), bdd);
  }

  @Test
  public void testXIffY() throws Exception {
     Parser parser = new Parser(new StringBufferInputStream("f1=f2"));
     parser.init();
     BDD bdd = parser.AdditiveExpression();
     assertEquals(parser.findVariable("f1").biimp(parser.findVariable("f2")), bdd);
  }

  @Test
  public void testImplication1() throws Exception {
     Parser parser = new Parser(new StringBufferInputStream("(f1.f2) => f1"));
     parser.init();
     BDD bdd = parser.AdditiveExpression();
     assertEquals(parser.findVariable("f1").and(parser.findVariable("f2")).imp(parser.findVariable("f1")), bdd);

     BDDFactory factory = parser.getFactory();
     BDDPairing g = factory.makePair();
     g.set(0, factory.one());
     //g.set(1, factory.one());
     g.set(2, factory.one());
     BDD bdd2 = bdd.replace(g);
     assertEquals(factory.one(), bdd2);
  }

  /*
  @Test
  public void testImplication2() throws Exception {
     Parser parser = new Parser(new StringBufferInputStream("~f1.f2 => f1"));
     parser.init();
     BDD bdd = parser.AdditiveExpression();
     assertEquals(parser.findVariable("f1").not().and(parser.findVariable("f2")).imp(parser.findVariable("f1")), bdd);
     assertFalse(bdd.isOne());
  }

  @Test
  public void testImplication3() throws Exception {
     Parser parser = new Parser(new StringBufferInputStream("f1.f2 => f3"));
     parser.init();
     BDD bdd = parser.AdditiveExpression();
     assertFalse(bdd.isZero());
     assertFalse(bdd.isOne());
     assertEquals(parser.findVariable("f1").and(parser.findVariable("f2")).imp(parser.findVariable("f3")), bdd);
     assertFalse(bdd.isOne());
  }
*/

  @Test
  public void testExactlyOne1() throws Exception {
    Parser parser = new Parser(new StringBufferInputStream("f1"));
    parser.init();

    List<BDD> bddList = new ArrayList<BDD>();
    assertFalse(parser.exactlyOne(bddList).isOne());  // <>

    bddList.add(parser.getFactory().zero());
    assertFalse(parser.exactlyOne(bddList).isOne());  // <0>

    bddList.add(parser.getFactory().one());
    assertTrue(parser.exactlyOne(bddList).isOne());   // <0,1>

    bddList.add(parser.getFactory().one());
    assertFalse(parser.exactlyOne(bddList).isOne());  // <0,1,1>
  }
}
