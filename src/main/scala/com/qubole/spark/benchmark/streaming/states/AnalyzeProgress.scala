package com.qubole.spark.benchmark.streaming.states

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{avg, max, round, sum}

object AnalyzeProgress {

  def main(args: Array[String]): Unit = {

    if (args.length < 1) {
      System.err.println("Usage: AnalyzeProgress <query-progress-file-path>")
      System.exit(1)
    }

    val spark = SparkSession
      .builder()
      .appName("AnalyzeProgress")
      .getOrCreate()

    val df = spark
      .read
      .json(args(0))

    df.selectExpr("runId",
        "batchId",
        "(numInputRows/1000000) as inputRows",
        "durationMs.triggerExecution/1000 as timeTakenInSec",
        "stateOperators[0].numRowsTotal/(1000000) as numStateInMillion",
        "stateOperators[0].memoryUsedBytes/(1024*1024*1024) as numStateGBytes" )
      .groupBy("runId")
      .agg(max("batchId").as("maxBatchId"),
        round(sum("inputRows"), 2).as("TotalProcessedRecordsInMillion"),
        round(sum("timeTakenInSec"), 2).as("TotalExecutionTimeInSec"),
        round(avg("timeTakenInSec"), 2).as("AvgExecutionTimeInSec"),
        round(max("numStateInMillion"),2).as("MaxStateRowsInMillion"),
        round(max("numStateGBytes"), 2).as("maxStateSizeInGB"))
      .show()
  }

}
