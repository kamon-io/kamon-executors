package kamon.attach
import java.io.File
import java.net.{URL, URLClassLoader}
import java.security.{AccessController, PrivilegedAction}

import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

object AttachmentProviders {

  sealed trait AttachmentProvider {
    def resolve(): Option[Class[_]]
  }

  trait DefaultAttachmentProvider extends AttachmentProvider {
    val VirtualMachineTyeName = "com.sun.tools.attach.VirtualMachine"

    def toolsJarPath: String

    /**
      * Gets the current HotSpotVirtualMachine implementation otherwise a None.
      *
      * @return
      * Returns the HotSpotVirtualMachine implementation of the running JVM.
      */
    def resolve(): Option[Class[_]] = {
      val toolsJar = new File(System.getProperty("java.home").replace('\\', '/') + "/" + toolsJarPath)
      if (toolsJar.isFile && toolsJar.canRead) {
        Try(AccessController.doPrivileged(new ClassLoaderCreationAction(toolsJar)).loadClass(VirtualMachineTyeName)) match {
          case Success(vm)          ⇒ Some(vm)
          case Failure(NonFatal(_)) ⇒ None
        }
      } else None
    }
  }

  case object JVM extends DefaultAttachmentProvider { val toolsJarPath = "../lib/tools.jar" }
  case object JDK extends DefaultAttachmentProvider { val toolsJarPath = "lib/tools.jar" }
  case object MAC extends DefaultAttachmentProvider { val toolsJarPath = "../Classes/classes.jar" }
  case object IBM extends AttachmentProvider {

    val IBMVirtualMachineTyeName = "com.ibm.tools.attach.VirtualMachine"

    override def resolve(): Option[Class[_]] = {
      Try(ClassLoader.getSystemClassLoader.loadClass(IBMVirtualMachineTyeName)) match {
        case Success(vm)          ⇒ Some(vm)
        case Failure(NonFatal(_)) ⇒ None
      }
    }
  }

  private val providers = Seq(JVM, JDK, MAC, IBM)

  private final class ClassLoaderCreationAction(toolsJar: File) extends PrivilegedAction[ClassLoader] {
    override def run(): ClassLoader = new URLClassLoader(Array[URL](toolsJar.toURI.toURL), null)
  }

  def resolve(): Option[Class[_]] =
    providers.iterator.map(_.resolve()).collectFirst { case Some(clazz) ⇒ clazz }
}