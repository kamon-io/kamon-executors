package kamon.executors

import java.util.concurrent.{Executor, TimeUnit}

import kamon.Kamon
import kamon.executors.util.ContextAwareRunnable
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole


class ExecutorInstrumentationBenchmark {
  /**
    * This benchmark attempts to measure the performance without any context propagation.
    *
    * @param blackhole a { @link Blackhole} object supplied by JMH
    */
  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  @Fork
  def none(blackhole: Blackhole): Unit = {
    DirectExecutor.execute(new BlackholeRunnable(blackhole))
  }

  /**
    * This benchmark attempts to measure the performance with manual context propagation.
    *
    * @param blackhole a { @link Blackhole} object supplied by JMH
    */
  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  @Fork
  def manual(blackhole: Blackhole): Unit = {
    DirectExecutor.execute(new ContextAwareRunnable(new BlackholeRunnable(blackhole)))
  }

  /**
    * This benchmark attempts to measure the performance with automatic context propagation.
    *
    * @param blackhole a { @link Blackhole} object supplied by JMH
    */
  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  @Fork(jvmArgsAppend = Array("-javaagent:/home/diego/.m2/repository/io/kamon/kanela-agent/0.0.13/kanela-agent-0.0.13.jar"))
  def automatic(blackhole: Blackhole): Unit = {
    DirectExecutor.execute(new BlackholeRunnable(blackhole))
  }
}

private class BlackholeRunnable(blackhole: Blackhole) extends Runnable {
  override def run(): Unit = {
    blackhole.consume(Kamon.currentContext())
  }
}

object DirectExecutor extends Executor {
  override def execute(command: Runnable): Unit =
    command.run()
}