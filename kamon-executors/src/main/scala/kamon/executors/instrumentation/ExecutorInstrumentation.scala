package kamon.executors.instrumentation

import java.io.Closeable
import java.util.concurrent.{Callable, Executors}

import kamon.Kamon
import kamon.context.Context
import ExecutorsInstrumentationAdvisors.RunnableWrapperAdvisor
import kamon.executors.util.{ContextAwareCallable, ContextAwareRunnable}
import kanela.agent.bootstrap.context.{ContextHandler, ContextProvider}
import kanela.agent.scala.KanelaInstrumentation
import stormpot.{BlazePool, Poolable, Slot}

///**
//  * Instrument:
//  *
//  * java.util.concurrent.Executor::execute
//  *
//  */
//class ExecutorInstrumentation extends KanelaInstrumentation {
//
//  /**
//    *TODO
//    */
//  ContextHandler.setContexProvider(new KamonContextProvider)
//
//  /**
//    * Instrument all implementations of:
//    *
//    * java.util.concurrent.Executor::execute
//    *
//    */
//  forSubtypeOf("java.util.concurrent.Executor") { builder =>
//    builder
//      .withAdvisorFor(method("execute").and(withArgument(classOf[Runnable])), classOf[RunnableWrapperAdvisor])
//      .build()
//  }
//
//  /**
//    * Instrument all implementations of:
//    *
//    * java.util.concurrent.ExecutorService::submit(Runnable)
//    * java.util.concurrent.ExecutorService::submit(Callable)
//    * java.util.concurrent.ExecutorService::[invokeAny|invokeAll](Collection[Callable])
//    *
//    */
//  forSubtypeOf("java.util.concurrent.ExecutorService") { builder =>
//    builder
//      .withAdvisorFor(method("submit").and(withArgument(classOf[Runnable])), classOf[RunnableWrapperAdvisor])
////      .withAdvisorFor(method("submit").and(withArgument(classOf[Callable[_]])), classOf[CallableWrapperAdvisor])
//////      .withAdvisorFor(anyMethod("invokeAny", "invokeAll").and(withArgument(classOf[util.Collection[_]])), classOf[CallableCollectionWrapperAdvisor])
//      .build()
//  }
//
//}
//
//  /**
//    * implementation of kanela.agent.bootstrap.context.ContextProvider
//    */
//  private final class KamonContextProvider extends ContextProvider {
//
//    val p = new PooledContextAwareRunnable
//
//    def wrapInContextAware(runnable: Runnable): Runnable = {
//      p.setRunnable(runnable)
//      p
////      new ContextAwareRunnable(runnable)
//    }
//
//  def wrapInContextAware[A](callable: Callable[A]): Callable[_] =
//    new ContextAwareCallable[A](callable)
//
//  override def setCurrentContext(obj: scala.Any): Unit = {
//    obj.asInstanceOf[ContextAware].setContext(Kamon.currentContext())
//  }
//}
//
//
//class PooledContextAwareRunnable extends Runnable {
//  @volatile var underlying:Runnable = _
//  @volatile var context: Context = _
//
//  override def run(): Unit = {
//    Kamon.withContext(context) {
//      try underlying.run() finally {
//        underlying = null
//      }
//    }
//  }
//
//  def setRunnable(underlying:Runnable): Unit = {
//    this.underlying = underlying
//    this.context = Kamon.currentContext()
//  }
//}
