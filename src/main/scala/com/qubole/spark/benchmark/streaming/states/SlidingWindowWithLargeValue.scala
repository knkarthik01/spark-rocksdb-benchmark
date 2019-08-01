package com.qubole.spark.benchmark.streaming.states

import com.qubole.spark.benchmark.BenchmarkQueryHelper
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}


class SlidingWindowWithLargeValue(conf: StateStoreBenchmarkConf)
  extends BaseQuery(conf, "SlidingWindowWithWatermark", "slidingWindow") {

  override def applyOperations(spark: SparkSession, df: DataFrame): DataFrame = {
    import spark.implicits._
    df.withWatermark("timestamp", "1 minutes")
      .selectExpr(
        "timestamp",
        "mod(value, 10000) as mod",
        "round(value/500000) as div",
        "value",
        BenchmarkQueryHelper.createCaseExprStr(
          "mod(CAST(RANDN(0) * 1000 as INTEGER), 50)", 50, 1000) + " as word")
      .groupBy(
        window($"timestamp", "60 minutes",  "5 minutes"),
        $"mod",
        $"div")
      .agg(max("value").as("max_value"),
        min("value").as("min_value"),
        avg("value").as("avg_value"),
        last("word").as("word_last"))
  }
}
