/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.yawl.internal.utils;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;
import javax.xml.xpath.XPathFunctionResolver;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.ExpressionType;
import org.apromore.cpf.InputExpressionType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.OutputExpressionType;
import org.apromore.cpf.SoftType;
import org.apromore.cpf.TaskType;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.xml.SimpleNamespaceContext;
import org.xml.sax.InputSource;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;

public class ExpressionUtilsUnitTest {

    @Test
    public void testCreateExpressionReferencingNetObject() throws CanoniserException, XPathExpressionException {
        try {
            ExpressionUtils.createExpressionReferencingNetObject("test", new NetType());
            fail();
        } catch (CanoniserException e) {
        }

        NetType net = new NetType();
        ObjectType obj = new ObjectType();
        obj.setName("test");
        net.getObject().add(obj);
        String expression = ExpressionUtils.createExpressionReferencingNetObject("test", net);
        assertEquals("cpf:getObjectValue('test')", expression);

        XPathFactory factory = XPathFactory.newInstance();
        factory.setXPathFunctionResolver(new XPathFunctionResolver() {

            private final QName name = new QName("http://www.apromore.org/cpf", "getObjectValue");

            @Override
            public XPathFunction resolveFunction(final QName functionName, final int arity) {
                if (name.equals(functionName)) {
                    return new XPathFunction() {

                        @Override
                        public Object evaluate(@SuppressWarnings("rawtypes") final List args) throws XPathFunctionException {
                            if (args.size() == 1 && args.get(0) instanceof String) {
                                String objectName = (String) args.get(0);
                                if (objectName.equals("test")) {
                                    return "testObjectValue";
                                }
                            }
                            throw new XPathFunctionException("Did not find Object invalid argument!");
                        }
                    };
                }
                return null;
            }
        });
        XPath newXPath = factory.newXPath();
        SimpleNamespaceContext namespaceContext = new SimpleNamespaceContext();
        namespaceContext.bindNamespaceUri("cpf", "http://www.apromore.org/cpf");
        newXPath.setNamespaceContext(namespaceContext);
        String result = newXPath.compile(expression).evaluate(new InputSource(new StringReader("<test></test>")));
        assertEquals("testObjectValue", result);
    }

    @Test
    public void testCreateQueryReferencingTaskVariables() throws CanoniserException {
        ExternalTaskFactsType task = new ExternalTaskFactsType();
        task.setId("Freight_in_Transit");
        TaskType cpfTask = new TaskType();
        cpfTask.setId("C-Freight_in_Transit");
        String rewrittenQuery1 = ExpressionUtils.createQueryReferencingTaskVariables("<test>{/Freight_in_Transit/AcceptanceCertificate/*}</test>",
                task);
        assertEquals("{cpf:getTaskObjectValue('AcceptanceCertificate')/*}", rewrittenQuery1);
        assertEquals("{/Ordering/PO_timedout/text()}",
                ExpressionUtils.createQueryReferencingTaskVariables("<test>{/Ordering/PO_timedout/text()}</test>", task));
        assertEquals("{cpf:getTaskObjectValue('PO_timedout')/text()}",
                ExpressionUtils.createQueryReferencingTaskVariables("<test>{/Freight_in_Transit/PO_timedout/text()}</test>", task));
        assertEquals("{current-date()}", ExpressionUtils.createQueryReferencingTaskVariables("<test>{current-date()}</test>", task));
        assertEquals("current-date()", ExpressionUtils.createQueryReferencingTaskVariables("current-date()", task));

    }

    @Test
    public void testCreateQueryReferencingNetObjects() throws CanoniserException {

        NetType cpfNet = new NetType();
        cpfNet.setId("testId");
        cpfNet.setOriginalID("Film_Production_Process");

        Set<String> objectList = new HashSet<String>();
        SoftType softType = new SoftType();
        softType.setName("cameraSheetNo");
        objectList.add(softType.getName());
        SoftType softType2 = new SoftType();
        softType2.setName("callSheetToday");
        objectList.add(softType2.getName());
        SoftType softType3 = new SoftType();
        softType3.setName("camRollsToday");
        objectList.add(softType3.getName());

        // Add duplicate
        objectList.add(softType.getName());

        String complexQuery = "<cameraInfo><sheetNumber>{/Film_Production_Process/cameraSheetNo/text()}</sheetNumber>"
                + "<camRoll/>"
                + "<studios_location>{for $x in /Film_Production_Process/callSheetToday/location/singleLocation     return concat(' ',$x/locationName,' @ ',$x/address,'.')}</studios_location>"
                + "</cameraInfo>";

        String simpleQuery = "<camRolls>{/Film_Production_Process/camRollsToday/text()}</camRolls>";

        String constantQuery = "true";

        String rewrittenComplex = ExpressionUtils.createQueryReferencingNetObjects(complexQuery, cpfNet, objectList);
        assertEquals(
                "<sheetNumber>{cpf:getObjectValue('cameraSheetNo')/text()}</sheetNumber><camRoll/><studios_location>{for $x in cpf:getObjectValue('callSheetToday')/location/singleLocation     return concat(' ',$x/locationName,' @ ',$x/address,'.')}</studios_location>",
                rewrittenComplex);
        String rewrittenSimple = ExpressionUtils.createQueryReferencingNetObjects(simpleQuery, cpfNet, objectList);
        assertEquals("{cpf:getObjectValue('camRollsToday')/text()}", rewrittenSimple);
        String rewrittenConstant = ExpressionUtils.createQueryReferencingNetObjects(constantQuery, cpfNet, objectList);
        assertEquals(constantQuery, rewrittenConstant);
    }

    @Test
    public void testDeterminedUsedVariables() throws CanoniserException {

        NetType cpfNet = new NetType();
        cpfNet.setId("testId");
        cpfNet.setOriginalID("Film_Production_Process");

        String veryComplexQuery = "<DPRinfo>{/Film_Production_Process/shootingSchedule/startDate} {/Film_Production_Process/shootingSchedule/scheduledFinish} {/Film_Production_Process/shootingSchedule/revisedFinish}  <shootingDaysSchedule>   <scheduledDays>     {/Film_Production_Process/shootingSchedule/scheduledShootingDays/text()}   </scheduledDays>   <daysToDate>     {/Film_Production_Process/shootDayNoToday/text()}   </daysToDate>   <estdToComplete>     {number(/Film_Production_Process/shootingSchedule/scheduledShootingDays/text())-     number(/Film_Production_Process/shootDayNoToday/text())}   </estdToComplete>   <estdTotal>     {/Film_Production_Process/shootingSchedule/scheduledShootingDays/text()}   </estdTotal> </shootingDaysSchedule>  {/Film_Production_Process/continuityDailyReport/locationSets}  <slateNOs>   {/Film_Production_Process/continuityDailyReport/slateNos/singleUnit/slate} </slateNOs>  {/Film_Production_Process/continuityDailyReport/scheduledScenesShot} {/Film_Production_Process/continuityDailyReport/scheduledScenesNotShot} <ScenesNotYetCompleted>{/Film_Production_Process/continuityDailyReport/scenesNotYetCompleted/*}</ScenesNotYetCompleted> <ScenesDeleted>{/Film_Production_Process/continuityDailyReport/scenesDeleted/*}</ScenesDeleted> <ScenesAdded>{/Film_Production_Process/continuityDailyReport/scenesAdded/*}</ScenesAdded> {/Film_Production_Process/continuityDailyReport/unscheduledScenesShot}  {/Film_Production_Process/continuityDailyReport/scriptTiming}  <ratioTimingSpec>   {/Film_Production_Process/scheduledRatio}   <dailyRatio>     {let $x:= /Film_Production_Process/continuityDailyReport/scriptTiming/shotToday/actualTiming,     $a:= number(substring($x,1,2))*60+number(substring($x,4,2))+(round(number(substring($x,7,2))*100 div 60) div 100),     $b:= /Film_Production_Process/stockInfoToday/gross     return (round($b*100 div ($a*36)) div 100)}   </dailyRatio>   <averageRatio>     {let $x:= /Film_Production_Process/continuityDailyReport/scriptTiming/shotToDate/actualTiming,     $a:= number(substring($x,1,2))*60+number(substring($x,4,2))+(round(number(substring($x,7,2))*100 div 60) div 100),     $b:= /Film_Production_Process/stockInfoPrevious/gross,     $c:= /Film_Production_Process/stockInfoToday/gross     return (round(($b+$c)*100 div ($a*36)) div 100)}   </averageRatio>   <averageTiming>     {let $x:= /Film_Production_Process/continuityDailyReport/scriptTiming/shotToDate/actualTiming,     $y:= /Film_Production_Process/shootDayNoToday,     $z:= round((number(substring($x,1,2))*3600 + number(substring($x,4,2))*60 + number(substring($x,7,2))) div $y),     $s:=($z mod 3600) mod 60,     $m:=(($z - $s) div 60) mod 60,     $h:= round((($z - $s) div 60 - $m) div 60),     $S:= if ($s > 10) then string($s) else (if ($s > 0) then concat('0',string($s)) else '00'),     $M:= if ($m > 10) then string($m) else (if ($m > 0) then concat('0',string($m)) else '00'),     $H:= if ($h > 10) then string($h) else (if ($h > 0) then concat('0',string($h)) else '00')     return concat(string($H),':',string($M),':',string($S))}   </averageTiming>   {let $x:= /Film_Production_Process/continuityDailyReport/scriptTiming/shotToDate/actualTiming,   $y:= /Film_Production_Process/continuityDailyReport/scriptTiming/shotToDate/estTiming,   $u:= number(substring($x,1,2))*3600 + number(substring($x,4,2))*60 + number(substring($x,7,2)),   $v:= number(substring($y,1,2))*3600 + number(substring($y,4,2))*60 + number(substring($y,7,2)),   $z:= abs($u - $v),   $s:= ($z mod 3600) mod 60,   $m:= (($z - $s) div 60) mod 60,   $h:= round((($z - $s) div 60 - $m) div 60),   $S:= if ($s > 10) then string($s) else (if ($s > 0) then concat('0',string($s)) else '00'),   $M:= if ($m > 10) then string($m) else (if ($m > 0) then concat('0',string($m)) else '00'),   $H:= if ($h > 10) then string($h) else (if ($h > 0) then concat('0',string($h)) else '00')   return   <cumulative>     <sign>       {if ($u >= $v) then true() else false()}     </sign>     <varTime>       {concat(string($H),':',string($M),':',string($S))}     </varTime>   </cumulative>}   {/Film_Production_Process/originalTiming} </ratioTimingSpec>  {for $x in /Film_Production_Process/stockInfoPrevious,  $y in /Film_Production_Process/stockInfoToday return <stockInfo>     <previously>       {$x/*}       <soundRolls>{/Film_Production_Process/soundRollsPrevious/text()}</soundRolls>       <camRolls>{/Film_Production_Process/camRollsPrevious/text()}</camRolls>     </previously>     <today>       {$y/*}       <soundRolls>{/Film_Production_Process/soundRollsToday/text()}</soundRolls>       <camRolls>{/Film_Production_Process/camRollsToday/text()}</camRolls>     </today>     <totalToDate>       <loaded>{number($x/loaded/text())+number($y/loaded/text())}</loaded>       <gross>{number($x/gross/text())+number($y/gross/text())}</gross>       <exposed>{number($x/exposed/text())+number($y/exposed/text())}</exposed>       <print>{number($x/print/text())+number($y/print/text())}</print>       <N_G>{number($x/N_G/text())+number($y/N_G/text())}</N_G>       <waste>{number($x/waste/text())+number($y/waste/text())}</waste>       <shortEnds>{number($x/shortEnds/text())+number($y/shortEnds/text())}</shortEnds>       <soundRolls>         {if (/Film_Production_Process/soundRollsPrevious/text()='nil')         then /Film_Production_Process/soundRollsToday/text() else         concat(/Film_Production_Process/soundRollsPrevious/text(),' ',         /Film_Production_Process/soundRollsToday/text())}       </soundRolls>       <camRolls>         {if (/Film_Production_Process/camRollsPrevious/text()='nil')         then /Film_Production_Process/camRollsToday/text() else         concat(/Film_Production_Process/camRollsPrevious/text(),' ',         /Film_Production_Process/camRollsToday/text())}       </camRolls>     </totalToDate> </stockInfo>}  <artistTimeSheet>   {for $x in /Film_Production_Process/timeSheetInfo/artistTimeSheet/singleArtist   return   <singleArtist>     {$x/artist}     {if (for $y in /Film_Production_Process/castInfo/singleCastInfo          where $y/artist=$x/artist return true())      then (for $y in /Film_Production_Process/castInfo/singleCastInfo          where $y/artist=$x/artist return $y/character)      else <character>character?</character>}     {$x/P_U}     {$x/MU_WD_Call_scheduled}     {$x/MU_WD_Call_actualArrival}     {$x/mealBreak}     {$x/timeWrap}     {$x/travel}     <totalHRs>00:00:00</totalHRs>   </singleArtist>} </artistTimeSheet>  <extrasTimeSheet>   {for $x in /Film_Production_Process/timeSheetInfo/extrasTimeSheet/singleArtist   return   <singleArtist>     {$x/artist}     <character>background character</character>     {$x/P_U}     {$x/MU_WD_Call_scheduled}     {$x/MU_WD_Call_actualArrival}     {$x/mealBreak}     {$x/timeWrap}     {$x/travel}     <totalHRs>00:00:00</totalHRs>   </singleArtist>} </extrasTimeSheet>  <crewTimeSheet>   {for $x in /Film_Production_Process/timeSheetInfo/crewTimeSheet/singleCrew   return   <singleCrew>     <crewName>       {for $y in /Film_Production_Process/crewInfo/singleCrewInfo       where $y/role=$x/crew return concat($y/firstName,' ',$y/lastName)}     </crewName>     <crewRole>{$x/crew/text()}</crewRole>     {$x/crewCall}     {$x/travelIn}     {$x/locationCall}     {if ($x/MU_WD_Call_mealBreak='00:00:00')     then <mealBreak>00:00:00</mealBreak>     else <mealBreak>00:45:00</mealBreak>}     {$x/wrap}     {$x/wrapLoc}     {$x/departLoc}     {$x/travelOut}     <totalHRs>00:00:00</totalHRs>   </singleCrew>} </crewTimeSheet>  <majorProps_actionVehicles_additionalEquipment>   {/Film_Production_Process/timeSheetInfo/majorProps_actionVehicles_extraEquipment/text()} </majorProps_actionVehicles_additionalEquipment>   <additionalCrew>   {/Film_Production_Process/timeSheetInfo/additionalPersonnel/text()} </additionalCrew>   <livestocks_other>   {/Film_Production_Process/timeSheetInfo/livestock/text()} </livestocks_other>   <accidents_delays>   {/Film_Production_Process/timeSheetInfo/accidents_delays/text()} </accidents_delays>  <catering>   {for $x in /Film_Production_Process/timeSheetInfo/mealInfo/singleMeal return   <singleMeal>     {$x/meal}     <time>{$x/duration/text()}</time>     {$x/numbers}     {$x/location}   </singleMeal>} </catering>  <generalRemarks>   {concat(/Film_Production_Process/timeSheetInfo/generalComments/text(),   /Film_Production_Process/continuityDailyReport/remarks/text())} </generalRemarks></DPRinfo>";

        Set<String> usedVariables = ExpressionUtils.determinedUsedVariables(veryComplexQuery, cpfNet);
        assertTrue(usedVariables.contains("shootingSchedule"));
        assertTrue(usedVariables.contains("shootDayNoToday"));
        assertTrue(usedVariables.contains("continuityDailyReport"));
        assertTrue(usedVariables.contains("scheduledRatio"));
        assertTrue(usedVariables.contains("stockInfoToday"));
        assertTrue(usedVariables.contains("shootDayNoToday"));
        assertTrue(usedVariables.contains("stockInfoPrevious"));
        assertTrue(usedVariables.contains("camRollsToday"));
        assertTrue(usedVariables.contains("camRollsPrevious"));
        assertTrue(usedVariables.contains("soundRollsPrevious"));
        assertTrue(usedVariables.contains("soundRollsToday"));
        assertTrue(usedVariables.contains("originalTiming"));
        assertTrue(usedVariables.contains("stockInfoToday"));
    }

    @Test
    public void testDetermineResultType() {
        ExpressionType expr = new ExpressionType();
        expr.setLanguage(CPFSchema.EXPRESSION_LANGUAGE_XPATH);
        expr.setExpression("{cpf:getObjectValue('camRollsToday')/text()}");
        assertEquals("string", ExpressionUtils.determineResultType(expr));
        expr.setExpression("boolean({cpf:getObjectValue('camRollsToday')/text()})");
        assertEquals("boolean", ExpressionUtils.determineResultType(expr));
        expr.setExpression("cpf:getObjectValue('camRollsToday')");
        assertEquals("anyType", ExpressionUtils.determineResultType(expr));
    }

    @Test
    public void testConvertXQueryToYAWLNetQuery() throws CanoniserException {
        InputExpressionType expr = new InputExpressionType();
        expr.setLanguage(CPFSchema.EXPRESSION_LANGUAGE_XQUERY);
        expr.setExpression("test = {cpf:getObjectValue('camRollsToday')/text()}");
        NetFactsType net = new NetFactsType();
        net.setId("netId");
        String yawlXQuery = ExpressionUtils.convertXQueryToYAWLNetQuery(expr, net);
        assertEquals("<test>{/netId/camRollsToday/text()}</test>", yawlXQuery);
    }

    @Test
    public void testConvertComplexXQueryToYAWLNetQuery() throws CanoniserException {
        InputExpressionType expr = new InputExpressionType();
        expr.setLanguage(CPFSchema.EXPRESSION_LANGUAGE_XQUERY);
        expr.setExpression("shootingSchedule =  <lastUpdatedDate>{current-date()}</lastUpdatedDate>  {cpf:getObjectValue('crewMember')/director}  {cpf:getObjectValue('crewMember')/producer}  <startDate>{current-date()}</startDate>  <scheduledFinish>{current-date()}</scheduledFinish>  <scheduledShootingDays>0</scheduledShootingDays>");
        NetFactsType net = new NetFactsType();
        net.setId("netId");
        String yawlXQuery = ExpressionUtils.convertXQueryToYAWLNetQuery(expr, net);
        assertEquals(
                "<shootingSchedule> <lastUpdatedDate>{current-date()}</lastUpdatedDate>  {/netId/crewMember/director}  {/netId/crewMember/producer}  <startDate>{current-date()}</startDate>  <scheduledFinish>{current-date()}</scheduledFinish>  <scheduledShootingDays>0</scheduledShootingDays></shootingSchedule>",
                yawlXQuery);
    }

    @Ignore
    @Test
    public void testConvertVeryComplexXQueryToYAWLNetQuery() throws CanoniserException {
        InputExpressionType expr = new InputExpressionType();
        expr.setLanguage(CPFSchema.EXPRESSION_LANGUAGE_XQUERY);
        expr.setExpression("DPRinfo =  <DPRinfo>{/Film_Production_Process/shootingSchedule/startDate} {/Film_Production_Process/shootingSchedule/scheduledFinish} {/Film_Production_Process/shootingSchedule/revisedFinish}  <shootingDaysSchedule>   <scheduledDays>     {/Film_Production_Process/shootingSchedule/scheduledShootingDays/text()}   </scheduledDays>   <daysToDate>     {/N-Film-Production-Process/shootDayNoToday/text()}   </daysToDate>   <estdToComplete>     {number(/Film_Production_Process/shootingSchedule/scheduledShootingDays/text())-     number(/N-Film-Production-Process/shootDayNoToday/text())}   </estdToComplete>   <estdTotal>     {/Film_Production_Process/shootingSchedule/scheduledShootingDays/text()}   </estdTotal> </shootingDaysSchedule>  {/Film_Production_Process/continuityDailyReport/locationSets}  <slateNOs>   {/Film_Production_Process/continuityDailyReport/slateNos/singleUnit/slate} </slateNOs>  {/Film_Production_Process/continuityDailyReport/scheduledScenesShot} {/Film_Production_Process/continuityDailyReport/scheduledScenesNotShot} <ScenesNotYetCompleted>{/Film_Production_Process/continuityDailyReport/scenesNotYetCompleted/*}</ScenesNotYetCompleted> <ScenesDeleted>{/Film_Production_Process/continuityDailyReport/scenesDeleted/*}</ScenesDeleted> <ScenesAdded>{/Film_Production_Process/continuityDailyReport/scenesAdded/*}</ScenesAdded> {/Film_Production_Process/continuityDailyReport/unscheduledScenesShot}  {/Film_Production_Process/continuityDailyReport/scriptTiming}  <ratioTimingSpec>   {/Film_Production_Process/scheduledRatio}   <dailyRatio>     {let $x:= /Film_Production_Process/continuityDailyReport/scriptTiming/shotToday/actualTiming,     $a:= number(substring($x,1,2))*60+number(substring($x,4,2))+(round(number(substring($x,7,2))*100 div 60) div 100),     $b:= /Film_Production_Process/stockInfoToday/gross     return (round($b*100 div ($a*36)) div 100)}   </dailyRatio>   <averageRatio>     {let $x:= /Film_Production_Process/continuityDailyReport/scriptTiming/shotToDate/actualTiming,     $a:= number(substring($x,1,2))*60+number(substring($x,4,2))+(round(number(substring($x,7,2))*100 div 60) div 100),     $b:= /Film_Production_Process/stockInfoPrevious/gross,     $c:= /Film_Production_Process/stockInfoToday/gross     return (round(($b+$c)*100 div ($a*36)) div 100)}   </averageRatio>   <averageTiming>     {let $x:= /Film_Production_Process/continuityDailyReport/scriptTiming/shotToDate/actualTiming,     $y:= /N-Film-Production-Process/shootDayNoToday,     $z:= round((number(substring($x,1,2))*3600 + number(substring($x,4,2))*60 + number(substring($x,7,2))) div $y),     $s:=($z mod 3600) mod 60,     $m:=(($z - $s) div 60) mod 60,     $h:= round((($z - $s) div 60 - $m) div 60),     $S:= if ($s &gt; 10) then string($s) else (if ($s &gt; 0) then concat('0',string($s)) else '00'),     $M:= if ($m &gt; 10) then string($m) else (if ($m &gt; 0) then concat('0',string($m)) else '00'),     $H:= if ($h &gt; 10) then string($h) else (if ($h &gt; 0) then concat('0',string($h)) else '00')     return concat(string($H),':',string($M),':',string($S))}   </averageTiming>   {let $x:= /Film_Production_Process/continuityDailyReport/scriptTiming/shotToDate/actualTiming,   $y:= /Film_Production_Process/continuityDailyReport/scriptTiming/shotToDate/estTiming,   $u:= number(substring($x,1,2))*3600 + number(substring($x,4,2))*60 + number(substring($x,7,2)),   $v:= number(substring($y,1,2))*3600 + number(substring($y,4,2))*60 + number(substring($y,7,2)),   $z:= abs($u - $v),   $s:= ($z mod 3600) mod 60,   $m:= (($z - $s) div 60) mod 60,   $h:= round((($z - $s) div 60 - $m) div 60),   $S:= if ($s &gt; 10) then string($s) else (if ($s &gt; 0) then concat('0',string($s)) else '00'),   $M:= if ($m &gt; 10) then string($m) else (if ($m &gt; 0) then concat('0',string($m)) else '00'),   $H:= if ($h &gt; 10) then string($h) else (if ($h &gt; 0) then concat('0',string($h)) else '00')   return   <cumulative>     <sign>       {if ($u &gt;= $v) then true() else false()}     </sign>     <varTime>       {concat(string($H),':',string($M),':',string($S))}     </varTime>   </cumulative>}   {/Film_Production_Process/originalTiming} </ratioTimingSpec>  {for $x in /Film_Production_Process/stockInfoPrevious,  $y in /Film_Production_Process/stockInfoToday return <stockInfo>     <previously>       {$x/*}       <soundRolls>{/N-Film-Production-Process/soundRollsPrevious/text()}</soundRolls>       <camRolls>{/N-Film-Production-Process/camRollsPrevious/text()}</camRolls>     </previously>     <today>       {$y/*}       <soundRolls>{/N-Film-Production-Process/soundRollsToday/text()}</soundRolls>       <camRolls>{/N-Film-Production-Process/camRollsToday/text()}</camRolls>     </today>     <totalToDate>       <loaded>{number($x/loaded/text())+number($y/loaded/text())}</loaded>       <gross>{number($x/gross/text())+number($y/gross/text())}</gross>       <exposed>{number($x/exposed/text())+number($y/exposed/text())}</exposed>       <print>{number($x/print/text())+number($y/print/text())}</print>       <N_G>{number($x/N_G/text())+number($y/N_G/text())}</N_G>       <waste>{number($x/waste/text())+number($y/waste/text())}</waste>       <shortEnds>{number($x/shortEnds/text())+number($y/shortEnds/text())}</shortEnds>       <soundRolls>         {if (/N-Film-Production-Process/soundRollsPrevious/text()='nil')         then /N-Film-Production-Process/soundRollsToday/text() else         concat(/N-Film-Production-Process/soundRollsPrevious/text(),' ',         /N-Film-Production-Process/soundRollsToday/text())}       </soundRolls>       <camRolls>         {if (/N-Film-Production-Process/camRollsPrevious/text()='nil')         then /N-Film-Production-Process/camRollsToday/text() else         concat(/N-Film-Production-Process/camRollsPrevious/text(),' ',         /N-Film-Production-Process/camRollsToday/text())}       </camRolls>     </totalToDate> </stockInfo>}  <artistTimeSheet>   {for $x in /Film_Production_Process/timeSheetInfo/artistTimeSheet/singleArtist   return   <singleArtist>     {$x/artist}     {if (for $y in /Film_Production_Process/castInfo/singleCastInfo          where $y/artist=$x/artist return true())      then (for $y in /Film_Production_Process/castInfo/singleCastInfo          where $y/artist=$x/artist return $y/character)      else <character>character?</character>}     {$x/P_U}     {$x/MU_WD_Call_scheduled}     {$x/MU_WD_Call_actualArrival}     {$x/mealBreak}     {$x/timeWrap}     {$x/travel}     <totalHRs>00:00:00</totalHRs>   </singleArtist>} </artistTimeSheet>  <extrasTimeSheet>   {for $x in /Film_Production_Process/timeSheetInfo/extrasTimeSheet/singleArtist   return   <singleArtist>     {$x/artist}     <character>background character</character>     {$x/P_U}     {$x/MU_WD_Call_scheduled}     {$x/MU_WD_Call_actualArrival}     {$x/mealBreak}     {$x/timeWrap}     {$x/travel}     <totalHRs>00:00:00</totalHRs>   </singleArtist>} </extrasTimeSheet>  <crewTimeSheet>   {for $x in /Film_Production_Process/timeSheetInfo/crewTimeSheet/singleCrew   return   <singleCrew>     <crewName>       {for $y in /Film_Production_Process/crewInfo/singleCrewInfo       where $y/role=$x/crew return concat($y/firstName,' ',$y/lastName)}     </crewName>     <crewRole>{$x/crew/text()}</crewRole>     {$x/crewCall}     {$x/travelIn}     {$x/locationCall}     {if ($x/MU_WD_Call_mealBreak='00:00:00')     then <mealBreak>00:00:00</mealBreak>     else <mealBreak>00:45:00</mealBreak>}     {$x/wrap}     {$x/wrapLoc}     {$x/departLoc}     {$x/travelOut}     <totalHRs>00:00:00</totalHRs>   </singleCrew>} </crewTimeSheet>  <majorProps_actionVehicles_additionalEquipment>   {/Film_Production_Process/timeSheetInfo/majorProps_actionVehicles_extraEquipment/text()} </majorProps_actionVehicles_additionalEquipment>   <additionalCrew>   {/Film_Production_Process/timeSheetInfo/additionalPersonnel/text()} </additionalCrew>   <livestocks_other>   {/Film_Production_Process/timeSheetInfo/livestock/text()} </livestocks_other>   <accidents_delays>   {/Film_Production_Process/timeSheetInfo/accidents_delays/text()} </accidents_delays>  <catering>   {for $x in /Film_Production_Process/timeSheetInfo/mealInfo/singleMeal return   <singleMeal>     {$x/meal}     <time>{$x/duration/text()}</time>     {$x/numbers}     {$x/location}   </singleMeal>} </catering>  <generalRemarks>   {concat(/Film_Production_Process/timeSheetInfo/generalComments/text(),   /Film_Production_Process/continuityDailyReport/remarks/text())} </generalRemarks></DPRinfo>");
        NetFactsType net = new NetFactsType();
        net.setId("netId");
        String yawlXQuery = ExpressionUtils.convertXQueryToYAWLNetQuery(expr, net);
        assertEquals(
                "<DPRinfo>{/netId/shootingSchedule/startDate} {/netId/shootingSchedule/scheduledFinish} {/netId/shootingSchedule/revisedFinish}  <shootingDaysSchedule>   <scheduledDays>     {/netId/shootingSchedule/scheduledShootingDays/text()}   </scheduledDays>   <daysToDate>     {/netId/shootDayNoToday/text()}   </daysToDate>   <estdToComplete>     {number(/netId/shootingSchedule/scheduledShootingDays/text())-     number(/netId/shootDayNoToday/text())}   </estdToComplete>   <estdTotal>     {/netId/shootingSchedule/scheduledShootingDays/text()}   </estdTotal> </shootingDaysSchedule>  {/netId/continuityDailyReport/locationSets}  <slateNOs>   {/netId/continuityDailyReport/slateNos/singleUnit/slate} </slateNOs>  {/netId/continuityDailyReport/scheduledScenesShot} {/netId/continuityDailyReport/scheduledScenesNotShot} <ScenesNotYetCompleted>{/netId/continuityDailyReport/scenesNotYetCompleted/*}</ScenesNotYetCompleted> <ScenesDeleted>{/netId/continuityDailyReport/scenesDeleted/*}</ScenesDeleted> <ScenesAdded>{/netId/continuityDailyReport/scenesAdded/*}</ScenesAdded> {/netId/continuityDailyReport/unscheduledScenesShot}  {/netId/continuityDailyReport/scriptTiming}  <ratioTimingSpec>   {/netId/scheduledRatio}   <dailyRatio>     {let $x:= /netId/continuityDailyReport/scriptTiming/shotToday/actualTiming,     $a:= number(substring($x,1,2))*60+number(substring($x,4,2))+(round(number(substring($x,7,2))*100 div 60) div 100),     $b:= /netId/stockInfoToday/gross     return (round($b*100 div ($a*36)) div 100)}   </dailyRatio>   <averageRatio>     {let $x:= /netId/continuityDailyReport/scriptTiming/shotToDate/actualTiming,     $a:= number(substring($x,1,2))*60+number(substring($x,4,2))+(round(number(substring($x,7,2))*100 div 60) div 100),     $b:= /netId/stockInfoPrevious/gross,     $c:= /netId/stockInfoToday/gross     return (round(($b+$c)*100 div ($a*36)) div 100)}   </averageRatio>   <averageTiming>     {let $x:= /netId/continuityDailyReport/scriptTiming/shotToDate/actualTiming,     $y:= /netId/shootDayNoToday,     $z:= round((number(substring($x,1,2))*3600 + number(substring($x,4,2))*60 + number(substring($x,7,2))) div $y),     $s:=($z mod 3600) mod 60,     $m:=(($z - $s) div 60) mod 60,     $h:= round((($z - $s) div 60 - $m) div 60),     $S:= if ($s > 10) then string($s) else (if ($s > 0) then concat('0',string($s)) else '00'),     $M:= if ($m > 10) then string($m) else (if ($m > 0) then concat('0',string($m)) else '00'),     $H:= if ($h > 10) then string($h) else (if ($h > 0) then concat('0',string($h)) else '00')     return concat(string($H),':',string($M),':',string($S))}   </averageTiming>   {let $x:= /netId/continuityDailyReport/scriptTiming/shotToDate/actualTiming,   $y:= /netId/continuityDailyReport/scriptTiming/shotToDate/estTiming,   $u:= number(substring($x,1,2))*3600 + number(substring($x,4,2))*60 + number(substring($x,7,2)),   $v:= number(substring($y,1,2))*3600 + number(substring($y,4,2))*60 + number(substring($y,7,2)),   $z:= abs($u - $v),   $s:= ($z mod 3600) mod 60,   $m:= (($z - $s) div 60) mod 60,   $h:= round((($z - $s) div 60 - $m) div 60),   $S:= if ($s > 10) then string($s) else (if ($s > 0) then concat('0',string($s)) else '00'),   $M:= if ($m > 10) then string($m) else (if ($m > 0) then concat('0',string($m)) else '00'),   $H:= if ($h > 10) then string($h) else (if ($h > 0) then concat('0',string($h)) else '00')   return   <cumulative>     <sign>       {if ($u >= $v) then true() else false()}     </sign>     <varTime>       {concat(string($H),':',string($M),':',string($S))}     </varTime>   </cumulative>}   {/netId/originalTiming} </ratioTimingSpec>  {for $x in /netId/stockInfoPrevious,  $y in /netId/stockInfoToday return <stockInfo>     <previously>       {$x/*}       <soundRolls>{/netId/soundRollsPrevious/text()}</soundRolls>       <camRolls>{/netId/camRollsPrevious/text()}</camRolls>     </previously>     <today>       {$y/*}       <soundRolls>{/netId/soundRollsToday/text()}</soundRolls>       <camRolls>{/netId/camRollsToday/text()}</camRolls>     </today>     <totalToDate>       <loaded>{number($x/loaded/text())+number($y/loaded/text())}</loaded>       <gross>{number($x/gross/text())+number($y/gross/text())}</gross>       <exposed>{number($x/exposed/text())+number($y/exposed/text())}</exposed>       <print>{number($x/print/text())+number($y/print/text())}</print>       <N_G>{number($x/N_G/text())+number($y/N_G/text())}</N_G>       <waste>{number($x/waste/text())+number($y/waste/text())}</waste>       <shortEnds>{number($x/shortEnds/text())+number($y/shortEnds/text())}</shortEnds>       <soundRolls>         {if (/netId/soundRollsPrevious/text()='nil')         then /netId/soundRollsToday/text() else         concat(/netId/soundRollsPrevious/text(),' ',         /netId/soundRollsToday/text())}       </soundRolls>       <camRolls>         {if (/netId/camRollsPrevious/text()='nil')         then /netId/camRollsToday/text() else         concat(/netId/camRollsPrevious/text(),' ',         /netId/camRollsToday/text())}       </camRolls>     </totalToDate> </stockInfo>}  <artistTimeSheet>   {for $x in /netId/timeSheetInfo/artistTimeSheet/singleArtist   return   <singleArtist>     {$x/artist}     {if (for $y in /netId/castInfo/singleCastInfo          where $y/artist=$x/artist return true())      then (for $y in /netId/castInfo/singleCastInfo          where $y/artist=$x/artist return $y/character)      else <character>character?</character>}     {$x/P_U}     {$x/MU_WD_Call_scheduled}     {$x/MU_WD_Call_actualArrival}     {$x/mealBreak}     {$x/timeWrap}     {$x/travel}     <totalHRs>00:00:00</totalHRs>   </singleArtist>} </artistTimeSheet>  <extrasTimeSheet>   {for $x in /netId/timeSheetInfo/extrasTimeSheet/singleArtist   return   <singleArtist>     {$x/artist}     <character>background character</character>     {$x/P_U}     {$x/MU_WD_Call_scheduled}     {$x/MU_WD_Call_actualArrival}     {$x/mealBreak}     {$x/timeWrap}     {$x/travel}     <totalHRs>00:00:00</totalHRs>   </singleArtist>} </extrasTimeSheet>  <crewTimeSheet>   {for $x in /netId/timeSheetInfo/crewTimeSheet/singleCrew   return   <singleCrew>     <crewName>       {for $y in /netId/crewInfo/singleCrewInfo       where $y/role=$x/crew return concat($y/firstName,' ',$y/lastName)}     </crewName>     <crewRole>{$x/crew/text()}</crewRole>     {$x/crewCall}     {$x/travelIn}     {$x/locationCall}     {if ($x/MU_WD_Call_mealBreak='00:00:00')     then <mealBreak>00:00:00</mealBreak>     else <mealBreak>00:45:00</mealBreak>}     {$x/wrap}     {$x/wrapLoc}     {$x/departLoc}     {$x/travelOut}     <totalHRs>00:00:00</totalHRs>   </singleCrew>} </crewTimeSheet>  <majorProps_actionVehicles_additionalEquipment>   {/netId/timeSheetInfo/majorProps_actionVehicles_extraEquipment/text()} </majorProps_actionVehicles_additionalEquipment>   <additionalCrew>   {/netId/timeSheetInfo/additionalPersonnel/text()} </additionalCrew>   <livestocks_other>   {/netId/timeSheetInfo/livestock/text()} </livestocks_other>   <accidents_delays>   {/netId/timeSheetInfo/accidents_delays/text()} </accidents_delays>  <catering>   {for $x in /netId/timeSheetInfo/mealInfo/singleMeal return   <singleMeal>     {$x/meal}     <time>{$x/duration/text()}</time>     {$x/numbers}     {$x/location}   </singleMeal>} </catering>  <generalRemarks>   {concat(/netId/timeSheetInfo/generalComments/text(),   /netId/continuityDailyReport/remarks/text())} </generalRemarks></DPRinfo>",
                yawlXQuery);
    }

    @Test
    public void testConvertXQueryToYAWLNetQueryShouldFail() {
        InputExpressionType expr = new InputExpressionType();
        expr.setLanguage(CPFSchema.EXPRESSION_LANGUAGE_MVEL);
        expr.setExpression("test = FOOBAR");
        NetFactsType net = new NetFactsType();
        net.setId("netId");
        try {
            ExpressionUtils.convertXQueryToYAWLNetQuery(expr, net);
            fail();
        } catch (CanoniserException e) {

        }
    }

    @Test
    public void testConvertXQueryToYAWLTaskQuery() throws CanoniserException {
        OutputExpressionType expr = new OutputExpressionType();
        expr.setLanguage(CPFSchema.EXPRESSION_LANGUAGE_XQUERY);
        expr.setExpression("test = {cpf:getTaskObjectValue('camRollsToday')/text()}");
        ExternalTaskFactsType task = new ExternalTaskFactsType();
        task.setId("taskId");
        String yawlXQuery = ExpressionUtils.convertXQueryToYAWLTaskQuery(expr, task);
        assertEquals("<test>{/taskId/camRollsToday/text()}</test>", yawlXQuery);
    }

    @Test
    public void testConvertXQueryToYAWLTaskQueryShouldFail() throws CanoniserException {
        OutputExpressionType expr = new OutputExpressionType();
        expr.setLanguage(CPFSchema.EXPRESSION_LANGUAGE_MVEL);
        expr.setExpression("test = FOOBAR");
        ExternalTaskFactsType task = new ExternalTaskFactsType();
        task.setId("taskId");
        try {
            ExpressionUtils.convertXQueryToYAWLTaskQuery(expr, task);
            fail();
        } catch (CanoniserException e) {
        }
    }

}
