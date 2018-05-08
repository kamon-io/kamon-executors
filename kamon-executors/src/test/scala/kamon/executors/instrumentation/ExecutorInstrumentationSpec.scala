package kamon.executors.instrumentation

import java.util.concurrent.{Callable, CountDownLatch, Executor, Executors}

import kamon.Kamon
import kamon.testkit.ContextTesting
import org.scalatest.concurrent.Eventually
import org.scalatest.{Matchers, OptionValues, WordSpec}

class ExecutorInstrumentationSpec extends WordSpec with Matchers with ContextTesting with Eventually with OptionValues {

  "the ExecutorInstrumentation" should {
    "capture the context when call execute" in {
      val singleThreadPoolExecutor = Executors.newFixedThreadPool(1)
      val context = contextWithLocal("in-runnable-body")

      val x = Kamon.withContext(context) {
        val runnable = new SimpleRunnable
        singleThreadPoolExecutor.submit(runnable)
//        singleThreadPoolExecutor.submit(new Callable[String]() {
//          override def call(): String = "jajajajaja"
//        })
        runnable.latch.await()
        runnable.ctx
      }

      x.value should be ("in-runnable-body")
    }
  }


}

object DirectExecutor extends Executor {
  override def execute(command: Runnable): Unit =
    command.run()
}

class SimpleRunnable extends Runnable with ContextTesting{
  val latch = new CountDownLatch(1)
  var ctx: Option[String] = _

  override def run(): Unit = {
    ctx = Kamon.currentContext().get(StringKey)
    latch.countDown()
  }
}