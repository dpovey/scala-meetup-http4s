package brisbane.scala.meetup

import cats.effect.Effect
import cats.implicits._
import org.http4s._
import org.http4s.circe._
import brisbane.scala.meetup.models.Book
import brisbane.scala.meetup.models.Book._
import org.http4s.dsl.Http4sDsl

class LibraryService[F[_]: Effect] extends Http4sDsl[F] {

  val Books = Root / "books"

  implicit val bookDecoder: EntityDecoder[F, Book] = jsonOf[F, Book]
  implicit val bookPartialDecoder: EntityDecoder[F, Book.Partial] = jsonOf[F, Book.Partial]

  val service: HttpService[F] = {
    HttpService[F] {

      // Create a book
      case req @ POST -> Root / "books"  =>
        for {
          book <- req.as[Book]
          resp <- NotImplemented(s"for $book")
        } yield resp

      // List books
      case GET -> Books =>
        NotImplemented()

      // Get a particular book
      case GET -> Books / id =>
        NotImplemented(s"for $id")

      // Upsert a book
      case req @ PUT -> Books / id =>
        for {
          book <- req.as[Book]
          resp <- NotImplemented(s"for $id and $book")
        } yield resp

      // Modify a book
      case req @ PATCH -> Books / id =>
        for {
          bookUpdate <- req.as[Book.Partial]
          resp <- NotImplemented(s"for $id and $bookUpdate")
        } yield resp

      // Delete a book
      case DELETE -> Books / id =>
        NotImplemented(s"for $id")
    }
  }
}
