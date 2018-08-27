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

  def withId(id: Id, book: Book) = Book.WithId(
    id = id,
    title = book.title,
    author = book.author,
    deweyDecimalClass = book.deweyDecimalClass,
    libraryOfCongress = book.libraryOfCongress,
    pages = book.pages
  )

  case class WithId(
    id: Id,
    title: String,
    author: String,
    deweyDecimalClass: String,
    libraryOfCongress: String,
    pages: Int
  ) {
    lazy val book = Book(
      title = title,
      author = author,
      deweyDecimalClass = deweyDecimalClass,
      libraryOfCongress = libraryOfCongress,
      pages = pages,
    )
  }

  case class Partial(
    title: Option[String] = None,
    author: Option[String] = None,
    deweyDecimalClass: Option[String] = None,
    libraryOfCongress: Option[String] = None,
    pages: Option[Int] = None
  )

  lazy implicit val encoder: Encoder[Book] = deriveEncoder
  lazy implicit val decoder: Decoder[Book] = deriveDecoder

  lazy implicit val withIdEncoder: Encoder[WithId] = deriveEncoder
  lazy implicit val withIdDecoder: Decoder[WithId] = deriveDecoder

  lazy implicit val partialEncoder: Encoder[Partial] = deriveEncoder
  lazy implicit val partialDecoder: Decoder[Partial] = deriveDecoder
}
