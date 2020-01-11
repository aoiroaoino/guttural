package guttural.util

import java.util.UUID

import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal

// string parser
trait Read[A] {

  def read(s: String): A

  def readOption(s: String): Option[A] =
    try Some(read(s))
    catch { case NonFatal(_) => None }

  def readEither(s: String): Either[Throwable, A] =
    try Right(read(s))
    catch { case NonFatal(e) => Left(e) }

  def map[B](f: A => B): Read[B] = s => f(read(s))
}

object Read {
  def apply[A](implicit R: Read[A]): Read[A] = R

  implicit val booleanRead: Read[Boolean] = _.toBoolean

  implicit val stringRead: Read[String] = identity _

  implicit val charArrayRead: Read[Array[Char]] = _.toCharArray

  implicit val byteRead: Read[Byte] = new Read[Byte] {
    override def read(s: String): Byte               = s.toByte
    override def readOption(s: String): Option[Byte] = s.toByteOption
  }

  implicit val shortRead: Read[Short] = new Read[Short] {
    override def read(s: String): Short               = s.toShort
    override def readOption(s: String): Option[Short] = s.toShortOption
  }

  implicit val intRead: Read[Int] = new Read[Int] {
    override def read(s: String): Int               = s.toInt
    override def readOption(s: String): Option[Int] = s.toIntOption
  }

  implicit val longRead: Read[Long] = new Read[Long] {
    override def read(s: String): Long               = s.toLong
    override def readOption(s: String): Option[Long] = s.toLongOption
  }

  implicit val floatRead: Read[Float] = new Read[Float] {
    override def read(s: String): Float               = s.toFloat
    override def readOption(s: String): Option[Float] = s.toFloatOption
  }

  implicit val doubleRead: Read[Double] = new Read[Double] {
    override def read(s: String): Double               = s.toDouble
    override def readOption(s: String): Option[Double] = s.toDoubleOption
  }

  implicit val uuidRead: Read[UUID] = UUID.fromString

  //

  implicit def optionRead[A](implicit M: Read[A]): Read[Option[A]] = M.readOption

  implicit def eitherRead[A](implicit M: Read[A]): Read[Either[Throwable, A]] = M.readEither

  implicit def tryRead[A](implicit M: Read[A]): Read[Try[A]] = M.readEither(_).fold(Failure(_), Success(_))
}
