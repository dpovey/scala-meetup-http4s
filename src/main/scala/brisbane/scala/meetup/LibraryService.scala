package brisbane.scala.meetup

import cats.effect.Effect
import cats.implicits._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import brisbane.scala.meetup.models.{Book, Id}
import brisbane.scala.meetup.models.Book._
import brisbane.scala.meetup.service.BooksDataService
import org.http4s.dsl.Http4sDsl

class LibraryService[F[_]: Effect](
  val booksDataService: BooksDataService[F]
) extends Http4sDsl[F] {

  private val Books = Root / "books"

  private def handleError[F[_]](status: Status)(implicit F: Effect[F]) = Response[F](status).pure[F]

  val service: HttpService[F] = {
    HttpService[F] {

      // Create a book
      case req @ POST -> Root / "books" =>
        for {
          book   <- req.as[Book]
          result <- booksDataService.create(book).value.flatMap {
            case Left(err) => handleError(err)
            case Right(id)    => Created(Id(id))
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
      case GET -> Books / IntVar(id) => {
        booksDataService.fetch(id).value.flatMap {
          case Left(err) => handleError(err)
          case Right(book) => Ok(book)
        }
      }

      // Upsert a book
      case req @ PUT -> Books / IntVar(id) =>
        for {
          book <- req.as[Book]
          result <- booksDataService.upsert(id, book).value.flatMap {
            case Left(err) => handleError(err)
            case Right(()) => Ok()
          }
        } yield result

      // Modify a book
      case req @ PATCH -> Books / IntVar(id) =>
        for {
          bookUpdate <- req.as[Book.Partial]
          result <- booksDataService.update(id, bookUpdate).value.flatMap {
            case Left(err) => handleError(err)
            case Right(()) => Ok()
          }
        } yield result

      // Delete a book
      case DELETE -> Books / IntVar(id) =>
        booksDataService.delete(id).value.flatMap {
          case Left(err) => handleError(err)
          case Right(()) => Ok()
        }
    }
  }
}
