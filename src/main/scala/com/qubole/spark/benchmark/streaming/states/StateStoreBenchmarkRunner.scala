package com.qubole.spark.benchmark.streaming.states

object StateStoreBenchmarkRunner {
  def main(args: Array[String]): Unit = {
    val conf = new StateStoreBenchmarkConf(args)
    val benchmarkInstance = conf.initializeBenchmarkClass

    println(s"Benchmark class: ${benchmarkInstance.getClass.getCanonicalName}")
    println(s"Output Mode: ${conf.getSparkOutputMode}")

    benchmarkInstance.runBenchmark()
  }

}
