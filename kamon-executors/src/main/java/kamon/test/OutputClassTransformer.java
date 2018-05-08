package kamon.test;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class OutputClassTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className.endsWith("ThreadPoolExecutor")) {
            System.out.println("classLoader " + loader);
            System.out.println("outing " + className);
        }
        return null;
    }
}

