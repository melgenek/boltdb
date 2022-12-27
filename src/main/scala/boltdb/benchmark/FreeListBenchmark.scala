package boltdb.benchmark

import boltdb.benchmark.FreeListBenchmark.PageIdState
import boltdb.freelist.FreeList
import boltdb.types.Types.{PageIdList, TxId}

import scala.collection.mutable
import org.openjdk.jmh.annotations.{Benchmark, BenchmarkMode, Fork, Level, Measurement, Mode, OutputTimeUnit, Param, Scope, Setup, State, TearDown, Warmup}

import java.util.concurrent.TimeUnit
import scala.util.Random

object FreeListBenchmark {
  @State(Scope.Benchmark)
  class Params {
    @Param(Array("10000"))
//    @Param(Array("10000", "100000", "1000000", "10000000"))
    var size: Int = _
  }

  @State(Scope.Thread)
  class PageIdState {
    var pageIds: PageIdList = _
    var pending: PageIdList = _

    @Setup(Level.Iteration)
    def up(params: Params): Unit = {
      pageIds = createRandomPageList(params.size)
      pending = createRandomPageList(params.size / 400)
    }

    private def createRandomPageList(size: Int): PageIdList = {
      val result = PageIdList.empty(size)
      val random = new Random(42)
      for (_ <- 0 until size) {
        result.addOne(random.nextLong())
      }
      result.sort()
      result
    }
  }
}


@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@BenchmarkMode(Array(Mode.AverageTime, Mode.Throughput))
class FreeListBenchmark {

  @Benchmark
  def release(state: PageIdState): Unit = {
    FreeList(state.pageIds, mutable.HashMap(TxId(1L) -> state.pending)).release(TxId(1L))
  }

}


