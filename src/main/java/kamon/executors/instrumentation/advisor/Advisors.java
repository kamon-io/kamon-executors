package kamon.executors.instrumentation.advisor;

import kanela.agent.bootstrap.context.ContextHandler;
import kanela.agent.libs.net.bytebuddy.asm.Advice;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class Advisors {
    public static class ExectuteParameterWrapper {
        @Advice.OnMethodEnter
        public static void wrapParam(@Advice.Argument(value = 0, readOnly = false) Runnable runnable) {
            runnable = ContextHandler.wrapInContextAware(runnable);
        }
    }


    public static class SubmitCallableParameterWrapper {
        @Advice.OnMethodEnter
        public static void wrapParam(@Advice.This Executor executor, @Advice.Argument(value = 0, readOnly = false) Callable<?> callable) {
            callable = ContextHandler.wrapInContextAware(callable);
        }
    }
}