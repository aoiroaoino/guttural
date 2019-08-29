package monoton.http

import monoton.http.FormMapping.MappingError
import monoton.util.Read

trait FormMapping[A] {
  def getValue(m: Map[String, String]): FormMapping.Result[A]
}

object FormMapping {
  type Result[A] = Either[List[MappingError], A]

  def apply[A](implicit F: FormMapping[A]): FormMapping[A] = F

  trait FieldValueDecoder[A] {}

  sealed abstract case class MappingError(msg: String)

  object MappingError {
    def NotFound(key: String): MappingError           = new MappingError(s"not found: $key")            {}
    def NotConvertibleType(key: String): MappingError = new MappingError(s"not convertible type: $key") {}
  }

//  implicit val intFormMapping: FormMapping[Int] = new FormMapping[Int] {
//    override def getValue(m: Map[String, String]): Either[List[MappingError], Int] = ???
//  }
//
//  def mapping2[A0: FormMapping, A1: FormMapping, B](key1: String, key2: String)(f: (A0, A1) => B): FormMapping[B] =
//    new FormMapping[B] {
//      override def getValue(m: Map[String, String]): Either[List[MappingError], B] = {
//        (FormMapping[A0].getValue(m), FormMapping[A1].getValue(m)) match {
//          case (Right(v1), Right(v2)) =>
//            Right(f((v1, v2)))
//          case ms =>
//            Left(List(ms._1, ms._2).collect { case Left(es) => es }.foldLeft(List.empty)(_ ++ _))
//        }
//      }
//    }
}
