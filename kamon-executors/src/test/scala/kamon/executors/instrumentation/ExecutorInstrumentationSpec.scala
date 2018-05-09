package kamon.executors.instrumentation

import java.util.concurrent.{Callable, CountDownLatch, Executors}

import com.google.common.util.concurrent.MoreExecutors
import kamon.Kamon
import kamon.testkit.ContextTesting
import org.scalatest.concurrent.Eventually
import org.scalatest.{Matchers, OptionValues, WordSpec}

import scala.collection.mutable.ListBuffer

class ExecutorInstrumentationSpec extends WordSpec with Matchers with ContextTesting with Eventually with OptionValues {

  "the ExecutorInstrumentation" should {
    "capture the context when call execute(Runnable) in DirectExecutor" in {
      val ctx = Kamon.withContext(contextWithLocal("in-runnable-body")) {
        val runnable = new SimpleRunnable
        MoreExecutors.directExecutor().execute(runnable)
        runnable.latch.await()
        runnable.ctx
      }

      ctx.value should be ("in-runnable-body")
    }

    "capture the context when call execute(Runnable) in ThreadPool" in {
      val executor = Executors.newSingleThreadExecutor()
      val ctx = Kamon.withContext(contextWithLocal("in-runnable-body")) {
        val runnable = new SimpleRunnable
        executor.execute(runnable)
        runnable.latch.await()
        runnable.ctx
      }

      ctx.value should be ("in-runnable-body")
    }

    "capture the context when call submit(Runnable) in ThreadPool" in {
      val executor = Executors.newSingleThreadExecutor()
      val ctx = Kamon.withContext(contextWithLocal("in-runnable-body")) {
        val runnable = new SimpleRunnable
        executor.submit(runnable)
        runnable.latch.await()
        runnable.ctx
      }

      ctx.value should be ("in-runnable-body")
    }

    "capture the context when call execute(Runnable) in ForkJoinPool" in {
      val executor = Executors.newWorkStealingPool()

      val ctx = Kamon.withContext(contextWithLocal("in-runnable-body")) {
        val runnable = new SimpleRunnable
        executor.execute(runnable)
        runnable.latch.await()
        runnable.ctx
      }

      ctx.value should be ("in-runnable-body")
    }

    "capture the context when call submit(Runnable) in ForkJoinPool" in {
      val executor = Executors.newWorkStealingPool()

      val ctx = Kamon.withContext(contextWithLocal("in-runnable-body")) {
        val runnable = new SimpleRunnable
        executor.submit(runnable)
        runnable.latch.await()
        runnable.ctx
      }

      ctx.value should be ("in-runnable-body")
    }

    "capture the context when call execute(Runnable) in ScheduledThreadPool" in {
      val executor = Executors.newScheduledThreadPool(1)

      val ctx = Kamon.withContext(contextWithLocal("in-runnable-body")) {
        val runnable = new SimpleRunnable
        executor.execute(runnable)
        runnable.latch.await()
        runnable.ctx
      }

      ctx.value should be ("in-runnable-body")
    }

    "capture the context when call submit(Runnable) in ScheduledThreadPool" in {
      val executor = Executors.newScheduledThreadPool(1)

      val ctx = Kamon.withContext(contextWithLocal("in-runnable-body")) {
        val runnable = new SimpleRunnable
        executor.submit(runnable)
        runnable.latch.await()
        runnable.ctx
      }

      ctx.value should be ("in-runnable-body")
    }

    "capture the context when call submit(Callable) in ThreadPool" in {
      val executor = Executors.newSingleThreadExecutor()
      val ctx = Kamon.withContext(contextWithLocal("in-callable-body")) {
        val callable = new SimpleCallable
        executor.submit(callable)
        callable.latch.await()
        callable.ctx
      }

      ctx.value should be ("in-callable-body")
    }

    "capture the context when call submit(Callable) in ScheduledThreadPool" in {
      val executor = Executors.newSingleThreadExecutor()
      val ctx = Kamon.withContext(contextWithLocal("in-callable-body")) {
        val callable = new SimpleCallable
        executor.submit(callable)
        callable.latch.await()
        callable.ctx
      }

      ctx.value should be ("in-callable-body")
    }

    "capture the context when call submit(Callable) in ForkJoinPool" in {
      val executor = Executors.newWorkStealingPool()

      val ctx = Kamon.withContext(contextWithLocal("in-callable-body")) {
        val callable = new SimpleCallable
        executor.submit(callable)
        callable.latch.await()
        callable.ctx
      }

      ctx.value should be ("in-callable-body")
    }

    "capture the context when call invokeAll(Colection<Callables>) in ExecutorService" in {
      import scala.collection.JavaConverters._

      val values = Kamon.withContext(contextWithLocal("all-callables-should-see-this-key")) {
        val callables = new CallableWithContext("A") :: new CallableWithContext( "B") :: new CallableWithContext("C") :: Nil
        Executors.newCachedThreadPool().invokeAll(callables.asJava).asScala.foldLeft(ListBuffer.empty[String]) { (acc, f) => acc += f.get() }
      }
      values should contain allOf("all-callables-should-see-this-key-A", "all-callables-should-see-this-key-B", "all-callables-should-see-this-key-C")
    }
  }
}

class SimpleRunnable extends Runnable with ContextTesting{
  val latch = new CountDownLatch(1)
  var ctx: Option[String] = _

  override def run(): Unit = {
    ctx = Kamon.currentContext().get(StringKey)
    latch.countDown()
  }
}

class SimpleCallable extends Callable[Option[String]] with ContextTesting {
  val latch = new CountDownLatch(1)
  var ctx: Option[String] = _

  override def call(): Option[String] = {
    ctx = Kamon.currentContext().get(StringKey)
    latch.countDown()
    ctx
  }
}

class CallableWithContext[A](value:String) extends Callable[String] with ContextTesting {
  override def call(): String =
    Kamon.currentContext().get(StringKey).map(_ + s"-$value").getOrElse("undefined")
}