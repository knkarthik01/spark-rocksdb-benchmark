package com.qubole.spark.benchmark.utils

import java.io.PrintWriter

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.apache.spark.internal.Logging
import org.apache.spark.sql.streaming.StreamingQueryListener

/*
 * This class is similar to Jungtaek Lim open sourced Benchmarking project
 * https://github.com/HeartSaVioR/iot-trucking-app-spark-structured-streaming/blob/master/src/main/scala/com/hortonworks/spark/utils/QueryListenerWriteProgressToFile.scala
 * Instead of writing into a local file, we write into a HDFS complaint filesystem so that we can do post run analysis
 */

class WriteProgressToFileListener(queryStatusFile: String) extends StreamingQueryListener with Logging{
  val conf = new Configuration()
  val fs= FileSystem.get(conf)
  val output = fs.create(new Path(queryStatusFile))
  val writer = new PrintWriter(output)

  override def onQueryStarted(event: StreamingQueryListener.QueryStartedEvent): Unit = {
    logInfo(s"Query is started for ID ${event.id}")
  }

  override def onQueryProgress(event: StreamingQueryListener.QueryProgressEvent): Unit = {

    try {
      writer.append(event.progress.json + "\n")
      writer.flush()
    } catch {
      case e: Exception =>
        logError("Error write event[" + event.progress.json + "] to file", e)
    }
  }

  override def onQueryTerminated(event: StreamingQueryListener.QueryTerminatedEvent): Unit = {
    logInfo(s"Query is terminated for ID ${event.id} and RUNID ${event.runId}")
    writer.close()
  }
}