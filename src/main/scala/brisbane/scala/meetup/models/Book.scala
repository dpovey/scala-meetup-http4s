package brisbane.scala.meetup.models

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

case class Book(
  title: String,
  author: String,
  deweyDecimalClass: String,
  libraryOfCongress: String,
  pages: Int
)

object Book  {

  case class Partial(
    title: Option[String] = None,
    author: Option[String] = None,
    deweyDecimalClass: Option[String] = None,
    libraryOfCongress: Option[String] = None,
    pages: Option[Int] = None
  )

  implicit val encoder: Encoder[Book] = deriveEncoder
  implicit val decoder: Decoder[Book] = deriveDecoder

  implicit val partialEncoder: Encoder[Partial] = deriveEncoder
  implicit val partialDecoder: Decoder[Partial] = deriveDecoder
}
