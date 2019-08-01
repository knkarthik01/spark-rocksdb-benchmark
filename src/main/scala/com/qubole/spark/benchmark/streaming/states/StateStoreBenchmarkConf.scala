package com.qubole.spark.benchmark.streaming.states

import com.qubole.spark.benchmark.BenchmarkConf

class StateStoreBenchmarkConf(args: Array[String])
  extends BenchmarkConf(args) {
  import StateStoreBenchmarkConf._
  verify()

  def initializeBenchmarkClass: BaseQuery = {
    new SlidingWindowWithLargeValue(this)
    }
  }

object StateStoreBenchmarkConf {
}
