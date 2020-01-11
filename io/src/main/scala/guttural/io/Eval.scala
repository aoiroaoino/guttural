package guttural.io

import scala.concurrent.{CanAwait, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.Try

class Eval[+A] extends Future[A] {

  override def onComplete[U](f: Try[A] => U)(implicit executor: ExecutionContext): Unit = ???

  override def isCompleted: Boolean = ???

  override def value: Option[Try[A]] = ???

  override def transform[S](f: Try[A] => Try[S])(implicit executor: ExecutionContext): Future[S] = ???

  override def transformWith[S](f: Try[A] => Future[S])(implicit executor: ExecutionContext): Future[S] = ???

  override def ready(atMost: Duration)(implicit permit: CanAwait): Eval.this.type = ???

  override def result(atMost: Duration)(implicit permit: CanAwait): A = ???
}
