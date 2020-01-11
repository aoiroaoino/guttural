package guttural.http

import guttural.http.FormMapping.{MappingError, Result}
import guttural.util.Read

// cats とか使いたくなる事案。使ったとしても boilerplate はコード生成したい。
object Form {

  def mapping[R, A0](key0: String)(f: A0 => R)(implicit M0: Read[A0]): FormMapping[R] = { m: Map[String, String] =>
    m.get(key0)
      .toRight(MappingError.NotFound(key0))
      .flatMap(M0.readEither(_).left.map(_ => MappingError.NotConvertibleType(key0)))
      .map(f)
      .left
      .map(_ :: Nil)
  }

  def mapping[R, A0, A1](key0: String, key1: String)(
      f: (A0, A1) => R
  )(implicit M0: Read[A0], M1: Read[A1]): FormMapping[R] = { m: Map[String, String] =>
    (
      m.get(key0)
        .toRight(MappingError.NotFound(key0))
        .flatMap(M0.readEither(_).left.map(_ => MappingError.NotConvertibleType(key0))),
      m.get(key1)
        .toRight(MappingError.NotFound(key1))
        .flatMap(M1.readEither(_).left.map(_ => MappingError.NotConvertibleType(key1))),
    ) match {
      case (Right(v0), Right(v1)) =>
        Right(f(v0, v1))
      case other =>
        Left(other.productIterator.collect { case Left(e: MappingError) => e }.toList) // unsafe...
    }
  }

  def mapping[R, A0, A1, A2](key0: String, key1: String, key2: String)(
      f: (A0, A1, A2) => R
  )(implicit M0: Read[A0], M1: Read[A1], M2: Read[A2]): FormMapping[R] = { m: Map[String, String] =>
    (
      m.get(key0)
        .toRight(MappingError.NotFound(key0))
        .flatMap(M0.readEither(_).left.map(_ => MappingError.NotConvertibleType(key0))),
      m.get(key1)
        .toRight(MappingError.NotFound(key1))
        .flatMap(M1.readEither(_).left.map(_ => MappingError.NotConvertibleType(key1))),
      m.get(key2)
        .toRight(MappingError.NotFound(key2))
        .flatMap(M2.readEither(_).left.map(_ => MappingError.NotConvertibleType(key2))),
    ) match {
      case (Right(v0), Right(v1), Right(v2)) =>
        Right(f(v0, v1, v2))
      case other =>
        Left(other.productIterator.collect { case Left(e: MappingError) => e }.toList)
    }
  }

  def mapping[R, A0, A1, A2, A3](key0: String, key1: String, key2: String, key3: String)(
      f: (A0, A1, A2, A3) => R
  )(implicit M0: Read[A0], M1: Read[A1], M2: Read[A2], M3: Read[A3]): FormMapping[R] = { m: Map[String, String] =>
    (
      m.get(key0)
        .toRight(MappingError.NotFound(key0))
        .flatMap(M0.readEither(_).left.map(_ => MappingError.NotConvertibleType(key0))),
      m.get(key1)
        .toRight(MappingError.NotFound(key1))
        .flatMap(M1.readEither(_).left.map(_ => MappingError.NotConvertibleType(key1))),
      m.get(key2)
        .toRight(MappingError.NotFound(key2))
        .flatMap(M2.readEither(_).left.map(_ => MappingError.NotConvertibleType(key2))),
      m.get(key3)
        .toRight(MappingError.NotFound(key3))
        .flatMap(M3.readEither(_).left.map(_ => MappingError.NotConvertibleType(key3))),
    ) match {
      case (Right(v0), Right(v1), Right(v2), Right(v3)) =>
        Right(f(v0, v1, v2, v3))
      case other =>
        Left(other.productIterator.collect { case Left(e: MappingError) => e }.toList)
    }
  }

  def mapping[R, A0, A1, A2, A3, A4](key0: String, key1: String, key2: String, key3: String, key4: String)(
      f: (A0, A1, A2, A3, A4) => R
  )(implicit M0: Read[A0], M1: Read[A1], M2: Read[A2], M3: Read[A3], M4: Read[A4]): FormMapping[R] = {
    m: Map[String, String] =>
      (
        m.get(key0)
          .toRight(MappingError.NotFound(key0))
          .flatMap(M0.readEither(_).left.map(_ => MappingError.NotConvertibleType(key0))),
        m.get(key1)
          .toRight(MappingError.NotFound(key1))
          .flatMap(M1.readEither(_).left.map(_ => MappingError.NotConvertibleType(key1))),
        m.get(key2)
          .toRight(MappingError.NotFound(key2))
          .flatMap(M2.readEither(_).left.map(_ => MappingError.NotConvertibleType(key2))),
        m.get(key3)
          .toRight(MappingError.NotFound(key3))
          .flatMap(M3.readEither(_).left.map(_ => MappingError.NotConvertibleType(key3))),
        m.get(key4)
          .toRight(MappingError.NotFound(key4))
          .flatMap(M4.readEither(_).left.map(_ => MappingError.NotConvertibleType(key4))),
      ) match {
        case (Right(v0), Right(v1), Right(v2), Right(v3), Right(v4)) =>
          Right(f(v0, v1, v2, v3, v4))
        case other =>
          Left(other.productIterator.collect { case Left(e: MappingError) => e }.toList)
      }
  }
}
