package brisbane.scala.meetup

import brisbane.scala.meetup.models.Book
import cats.effect.IO
import org.http4s._
import org.http4s.implicits._
import org.http4s.circe.CirceEntityCodec._
import io.circe.syntax._

class LibrarySpec extends org.specs2.mutable.Specification {

  val libraryService = new LibraryService[IO].service.orNotFound
  
  "POST /books" >> {
    val book = Book(
      "Fear and Loathing in Las Vegas",
      "Hunter S. Thompson",
      "917.93/13, B",
      "PN4874.T444 A3 1971",
      206)
    val request = Request[IO](Method.POST, Uri.uri("/books")).withBody(book.asJson)
    val response = libraryService.run(request.unsafeRunSync()).unsafeRunSync()
    response.status must beEqualTo(Status.NotImplemented)
  }

  "GET /books" >> {
    val request = Request[IO](Method.GET, Uri.uri("/books"))
    val response = libraryService.run(request).unsafeRunSync()
    response.status must beEqualTo(Status.NotImplemented)
  }

  "GET /books/id" >> {
    val request = Request[IO](Method.GET, Uri.uri("/books/id"))
    val response = libraryService.run(request).unsafeRunSync()
    response.status must beEqualTo(Status.NotImplemented)
  }

  "PUT /books/id" >> {
    val book = Book(
      "Fear and Loathing in Las Vegas",
      "Hunter S. Thompson",
      "917.93/13, B",
      "PN4874.T444 A3 1971",
      206)
    val request = Request[IO](Method.PUT, Uri.uri("/books/id")).withBody(book.asJson)
    val response = libraryService.run(request.unsafeRunSync()).unsafeRunSync()
    response.status must beEqualTo(Status.NotImplemented)
  }

  "PATCH /books/id" >> {
    val bookUpdate = Book.Partial(author = Some("Hunter S. Thompson"))
    val request = Request[IO](Method.PATCH, Uri.uri("/books/id")).withBody(bookUpdate.asJson)
    val response = libraryService.run(request.unsafeRunSync()).unsafeRunSync()
    response.status must beEqualTo(Status.NotImplemented)
  }

}
