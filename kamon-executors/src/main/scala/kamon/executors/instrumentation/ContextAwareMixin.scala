package kamon.executors.instrumentation

import kamon.context.Context

import scala.beans.BeanProperty

trait ContextAware {
  def getContext:Context
  def setContext(ctx:Context):Unit
}

class ContextAwareMixin extends ContextAware {
  @BeanProperty @volatile var context: Context = _
}

