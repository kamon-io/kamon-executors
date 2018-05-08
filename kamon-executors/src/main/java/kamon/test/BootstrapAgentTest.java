package kamon.test;


import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class BootstrapAgentTest {
    public static void main(String[] args) throws Exception {
//        kanela.agent.attacher.Kanela.attach();
//           AgentLoader.attachAgentToJVM(Kanela.class);
//           AgentLoader.attach();

//        new Hello().run();
//

//        premain(null, ByteBuddyAgent.install());
//        HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://www.google.com").openConnection();
//        System.out.println(urlConnection.getRequestMethod());

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("puta madre");
            }
        });

        Thread.sleep(5000);

    }

    public static void premain(String arg, Instrumentation inst) throws Exception {
        inst.addTransformer(new OutputClassTransformer());
        File temp = Files.createTempDirectory("tmp").toFile();
        ClassInjector.UsingInstrumentation.of(temp, ClassInjector.UsingInstrumentation.Target.BOOTSTRAP, inst).inject(Collections.singletonMap(
                new TypeDescription.ForLoadedType(MyInterceptor.class),
                ClassFileLocator.ForClassLoader.read(MyInterceptor.class).resolve()));
        new AgentBuilder.Default()
                .ignore(ElementMatchers.nameStartsWith("net.bytebuddy."))
                .enableBootstrapInjection(inst, temp)
                .type(not(isInterface()).and(hasSuperType(named(ExecutorService.class.getName()))))
                .transform(new AgentBuilder.Transformer() {
                    @Override
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule) {
                        return builder.visit(Advice.to(WrapRunnableAdvice.class).on(named("submit").and(not(isAbstract())).and(ElementMatchers.takesArgument(0, Runnable.class))));
//                        return builder.method(MethodAvinamed("submit").and(ElementMatchers.takesArgument(0, Runnable.class))(MethodDelegation.to(MyInterceptor.class));
                    }
                }).installOn(inst);
    }

    public static class MyInterceptor {

        public static String intercept(@SuperCall Callable<String> zuper) throws Exception {
            System.out.println("Intercepted!");
            return zuper.call();
        }
    }


    public static class WrapRunnableAdvice {
        @Advice.OnMethodEnter(suppress = Throwable.class)
        @Advice.OnMethodExit(suppress = Throwable.class)
        public static void wrapJob(@Advice.Argument(value = 0, readOnly = false) Runnable task) {
            System.out.println("Intercepted!");

        }
    }
}