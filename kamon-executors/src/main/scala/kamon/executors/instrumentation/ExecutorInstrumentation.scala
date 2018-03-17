package kamon.executors.instrumentation

import java.util
import java.util.concurrent.Callable

import kamon.agent.scala.KanelaInstrumentation
import kamon.executors.instrumentation.advisor.Advisors.{CallableCollectionWrapperAdvisor, CallableWrapperAdvisor, RunnableWrapperAdvisor}
import kamon.executors.util.{ContextAwareCallable, ContextAwareRunnable}
import kanela.agent.bootstrap.context.{ContextHandler, ContextProvider}

/**
  * Instrument:
  *
  * java.util.concurrent.Executor::execute
  *
  */
class ExecutorInstrumentation extends KanelaInstrumentation {

  ContextHandler.setContexManipulationProvider(new KamonExecutorContextAware)

  /**
    * Instrument all implementations of:
    *
    * java.util.concurrent.Executor::execute
    *
    */
  forSubtypeOf("java.util.concurrent.Executor") { builder =>
    builder
      .withAdvisorFor(method("execute").and(takesArguments(classOf[Runnable])), classOf[RunnableWrapperAdvisor])
      .build()
  }

  /**
    * Instrument all implementations of:
    *
    * java.util.concurrent.ExecutorService::submit(Runnable)
    * java.util.concurrent.ExecutorService::submit(Callable)
    * java.util.concurrent.ExecutorService::(invokeAny)|invokeAll)
    *
    */
  forSubtypeOf("java.util.concurrent.ExecutorService") { builder =>
    builder
      .withAdvisorFor(method("submit").and(takesArguments(classOf[Runnable])), classOf[RunnableWrapperAdvisor])
      .withAdvisorFor(method("submit").and(takesArguments(classOf[Callable[_]])), classOf[CallableWrapperAdvisor])
      .withAdvisorFor(anyMethod("invokeAny", "invokeAll").and(takesArguments(classOf[util.Collection[Callable[_]]])), classOf[CallableCollectionWrapperAdvisor])
      .build()
  }


  /**
    * implementation of kanela.agent.bootstrap.context.ContextProvider
    */
  private final class KamonExecutorContextAware extends ContextProvider {
    def wrapInContextAware(runnable: Runnable): Runnable =
      new ContextAwareRunnable(runnable)

    def wrapInContextAware[A](callable: Callable[A]): Callable[_] =
      new ContextAwareCallable[A](callable)
  }
}