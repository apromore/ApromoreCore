package cs.ut.ui.controllers

import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.util.Arrays
import kotlin.test.assertEquals
import org.deckfour.xes.`in`.XesXmlParser
import org.deckfour.xes.model.XLog
import org.json.JSONObject
import org.junit.Test

class TrainingControllerUnitTest {

  @Test
  fun testConvertXLogToDatasetParams(): Unit {
      val inputStream = TrainingControllerUnitTest::class.java.getClassLoader().getResourceAsStream("test_bpi12.xes")
      val log = XesXmlParser().parse(inputStream).`get`(0)

      val result = TrainingController.convertXLogToDatasetParams(log)

      assertEquals("case_id", result.get("case_id_col"))
      //assertEquals(setOf("lifecycle:model","creator","library"), result.get("global"))
      //assertEquals(setOf("concept:name","lifecycle:transition","org:resource","time:timestamp","Resource","AMOUNT_REQ","proctime","elapsed","activity_name","label","event_nr","last","time"), result.get("global_event"))
      //assertEquals(setOf("concept:name","variant","variant-index"), result.get("global_trace"))
      System.out.println(result.toString())
  }

  @Test
  fun testConvertXLogToCSV() : Unit {
      val inputStream = TrainingControllerUnitTest::class.java.getClassLoader().getResourceAsStream("test_bpi12.xes")
      val log = XesXmlParser().parse(inputStream).`get`(0)

      val outputStream = ByteArrayOutputStream()
      TrainingController.convertXLogToCSV(log, outputStream)

      // TODO: actually confirm the contents of the conversion, rather than just that it ran without exception
  }

  @Test
  fun testWriteCSV() : Unit {
       val values = Arrays.asList("one", "two", "three")
       val outputStream = ByteArrayOutputStream()
       val printWriter = PrintWriter(outputStream)
       TrainingController.writeCSV(values, printWriter)
       printWriter.close()

       assertEquals("one,two,three\n", outputStream.toString())
  }
}
