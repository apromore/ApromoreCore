/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package cs.ut.ui.controllers

import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.io.StringReader
import java.util.Arrays
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.deckfour.xes.`in`.XesXmlParser
import org.deckfour.xes.model.XLog
import org.json.JSONObject
import org.junit.Ignore
import org.junit.Test

class TrainingControllerUnitTest {

  @Test
  fun testConvertXLogToDatasetParams_bpi12(): Unit {
      val inputStream = TrainingControllerUnitTest::class.java.getClassLoader().getResourceAsStream("test_bpi12.xes")
      val log = XesXmlParser().parse(inputStream).`get`(0)
      val result = TrainingController.convertXLogToDatasetParams(log)

      val inputStream2 = TrainingControllerUnitTest::class.java.getClassLoader().getResourceAsStream("test_bpi12.json")
      val expected = JSONObject(String(inputStream2.readBytes()))

      assertEquals(expected.toString(), result.toString())

      //assertEquals(expected.get("case_id_col"), result.get("case_id_col"))
      //assertEquals(expected.get("dynamic_cat_cols"), result.get("dynamic_cat_cols"))
  }

  @Test
    fun testConvertXLogToDatasetParams_sepsis(): Unit {
        val inputStream = TrainingControllerUnitTest::class.java.getClassLoader().getResourceAsStream("sepsis.xes")
        val log = XesXmlParser().parse(inputStream).`get`(0)
        val result = TrainingController.convertXLogToDatasetParams(log)

        val inputStream2 = TrainingControllerUnitTest::class.java.getClassLoader().getResourceAsStream("sepsis.json")
        val expected = JSONObject(String(inputStream2.readBytes()))

        assertEquals(expected.toString(), result.toString())

  }

  @Test
  fun testConvertXLogToDatasetParams_missingHeader(): Unit {
      val inputStream = TrainingControllerUnitTest::class.java.getClassLoader().getResourceAsStream("missing_header.xes")
      val log = XesXmlParser().parse(inputStream).`get`(0)
      val result = TrainingController.convertXLogToDatasetParams(log)

      val inputStream2 = TrainingControllerUnitTest::class.java.getClassLoader().getResourceAsStream("missing_header.json")
      val expected = JSONObject(String(inputStream2.readBytes()))

      assertEquals(expected.toString(), result.toString())
  }

  @Test
  fun testConvertXLogToCSV_sepsis() : Unit {
      val inputStream = TrainingControllerUnitTest::class.java.getClassLoader().getResourceAsStream("sepsis.xes")
      val log = XesXmlParser().parse(inputStream).`get`(0)

      val outputStream = ByteArrayOutputStream()
      TrainingController.convertXLogToCSV(log, outputStream)
      val reader = BufferedReader(StringReader(outputStream.toString()))
      assertEquals("case_id,org:group,InfectionSuspected,Age,DiagnosticUrinarySediment,SIRSCritTemperature,DiagnosticLiquor,DiagnosticIC,DiagnosticLacticAcid,DiagnosticBlood,SIRSCritHeartRate,DiagnosticArtAstrup,lifecycle:transition,DiagnosticOther,DiagnosticXthorax,Oligurie,Hypotensie,SIRSCriteria2OrMore,DiagnosticUrinaryCulture,Infusion,DiagnosticSputum,DiagnosticECG,concept:name,DisfuncOrg,Diagnose,Hypoxie,time:timestamp,SIRSCritLeucos,SIRSCritTachypnea,Leucocytes,CRP,LacticAcid", reader.readLine())
      assertEquals("A,A,true,85,true,true,false,true,true,true,true,true,complete,false,true,false,true,true,true,true,false,true,ER Registration,true,A,false,2014-10-22T09:15:41,false,true,,", reader.readLine())
      assertEquals("A,B,,,,,,,,,,,complete,,,,,,,,,,Leucocytes,,,,2014-10-22T09:27:00,,,9.6,", reader.readLine())
  }

  @Test
  fun testConvertXLogToCSV_missingHeader() : Unit {
      val inputStream = TrainingControllerUnitTest::class.java.getClassLoader().getResourceAsStream("missing_header.xes")
      val log = XesXmlParser().parse(inputStream).`get`(0)

      val outputStream = ByteArrayOutputStream()
      TrainingController.convertXLogToCSV(log, outputStream)
      val reader = BufferedReader(StringReader(outputStream.toString()))
      assertEquals("case_id,ApplicationType,Resource,MonthlyCost,LoanGoal,RequestedAmount,hour,concept:name,FirstWithdrawalAmount,elapsed,duration,remtime,Action,weekday,activity_duration,time:timestamp,lifecycle:transition,month,CreditScore,NumberOfTerms,EventOrigin,OfferedAmount", reader.readLine())
      assertEquals("c_617529015,New credit,User_1,0.0,Home improvement,20000.0,16,A_Create Application,0.0,0.0,0.0,2761324.019,Created,4,0.0,2016-09-09T06:58:02,complete,9,missing,0.0,Application", reader.readLine())
      assertEquals("c_617529015,New credit,User_1,0.0,Home improvement,20000.0,16,A_Submitted,0.0,0.064,0.00106666666667,2761323.955,statechange,4,0.0,2016-09-09T06:58:02,complete,9,missing,0.0,Application", reader.readLine())
      assertNull(reader.readLine())
  }

  @Test
  fun testWriteCSV() : Unit {
       val values = Arrays.asList("one", "two,three", "\"four\"", "five\nsix")
       val outputStream = ByteArrayOutputStream()
       val printWriter = PrintWriter(outputStream)
       TrainingController.writeCSV(values, printWriter)
       printWriter.close()

       assertEquals("one,\"two,three\",\"\"\"four\"\"\",\"five\nsix\"\n", outputStream.toString())
  }
}
