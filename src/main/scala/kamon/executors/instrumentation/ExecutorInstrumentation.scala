package kamon.executors.instrumentation

import java.util.concurrent.Callable

import kamon.agent.scala.KanelaInstrumentation
import kamon.executors.instrumentation.advisor.Advisors
import kamon.executors.instrumentation.advisor.Advisors.ExectuteParameterWrapper
import kamon.executors.util.{ContextAwareCallable, ContextAwareRunnable}
import kanela.agent.bootstrap.context.{ContextHandler, ContextProvider}

class ExecutorInstrumentation extends KanelaInstrumentation {

  ContextHandler.setContexManipulationProvider(new KamonExecutorContextAware)

  forSubtypeOf("java.util.concurrent.Executor") { builder =>
    builder
      .withAdvisorFor(method("execute").and(takesArguments(classOf[Runnable])), classOf[ExectuteParameterWrapper])
      .build()
  }

  private final class KamonExecutorContextAware extends ContextProvider {
    override def wrapInContextAware(runnable: Runnable): Runnable = {
      new ContextAwareRunnable(runnable)
    }

    override def wrapInContextAware[A](callable: Callable[A]): Callable[_] = {
      println("CaALLLALLALA")
      new ContextAwareCallable[A](callable)
    }
  }
}