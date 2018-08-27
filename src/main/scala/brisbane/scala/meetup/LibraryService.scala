package brisbane.scala.meetup

import java.util.UUID

import cats.effect.Effect
import cats.implicits._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import brisbane.scala.meetup.models.{Book, Id}
import brisbane.scala.meetup.models.Book._
import brisbane.scala.meetup.service.BooksDataService
import org.http4s.dsl.Http4sDsl

import scala.util.Try

class LibraryService[F[_]: Effect](
  val booksDataService: BooksDataService[F]
) extends Http4sDsl[F] {

  private val Books = Root / "books"

  private def handleError(status: Status) = Response[F](status).pure[F]

  object IdVar {
    def unapply(arg: String): Option[Id] = Try { UUID.fromString(arg) }.toOption.map(Id(_))
  }

  val service: HttpService[F] = {
    HttpService[F] {

      // Create a book
      case req @ POST -> Root / "books" =>
        for {
          book   <- req.as[Book]
          result <- booksDataService.create(book).value.flatMap {
            case Left(err) => handleError(err)
            case Right(id)    => Created(Map("id" -> id))
          }
        } yield result

      // List books
      case GET -> Books => {
        booksDataService.fetch().value.flatMap {
          case Left(err) => handleError(err)
          case Right(books) => Ok(books)
        }
      }

      // Get a particular book
      case GET -> Books / IdVar(id) => {
        booksDataService.fetch(id).value.flatMap {
          case Left(err) => handleError(err)
          case Right(book) => Ok(book)
        }
      }

      // Upsert a book
      case req @ PUT -> Books / IdVar(id) =>
        for {
          book <- req.as[Book]
          result <- booksDataService.upsert(id, book).value.flatMap {
            case Left(err) => handleError(err)
            case Right(()) => Ok()
          }
        } yield result

      // Modify a book
      case req @ PATCH -> Books / IdVar(id) =>
        for {
          bookUpdate <- req.as[Book.Partial]
          result <- booksDataService.update(id, bookUpdate).value.flatMap {
            case Left(err) => handleError(err)
            case Right(()) => Ok()
          }
        } yield result

      // Delete a book
      case DELETE -> Books / IdVar(id) =>
        booksDataService.delete(id).value.flatMap {
          case Left(err) => handleError(err)
          case Right(()) => Ok()
        }
    }
  }
}
