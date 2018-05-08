package kamon.executors.instrumentation

import kamon.Kamon
import kamon.context.Storage
import kanela.agent.libs.net.bytebuddy.asm.Advice
import kanela.agent.scala.KanelaInstrumentation

//class RunnableCallableInstrumentation extends KanelaInstrumentation {

  //  forSubtypeOf("java.lang.Runnable" or "java.util.concurrent.Callable") { builder =>
  //    builder
  //      .withMixin(classOf[ContextAwareMixin])
  //      .withAdvisorFor(anyMethod("run", "call"), classOf[RunnableOrCallableMethodAdvisor])
  //      .build()
  //  }
  //}
//}

class RunnableOrCallableMethodAdvisor
object RunnableOrCallableMethodAdvisor {
  @Advice.OnMethodEnter
  def enter(@Advice.This contextAware: ContextAware): Storage.Scope =
    Kamon.storeContext(contextAware.getContext)

  @Advice.OnMethodExit(onThrowable = classOf[Throwable])
  def exit(@Advice.Enter scope: Storage.Scope): Unit =
    scope.close()
}