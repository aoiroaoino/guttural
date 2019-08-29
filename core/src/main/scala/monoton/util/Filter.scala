package monoton.util

trait Filter[S, T] extends Flow[S, T, S, T]

object Filter {

  // to だけを filter したい時
  trait Upstream[S, T] extends Filter[S, T] {
    override final def from(b: T, s: S): T = b
  }

  // from だけを filter したい時
  trait Downstream[S, T] extends Filter[S, T] {
    override final def to(s: S): Either[T, S] = Right(s)
  }
}
