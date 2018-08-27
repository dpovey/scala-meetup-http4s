package brisbane.scala.meetup

import java.util.UUID

import brisbane.scala.meetup.dao.BooksDao
import brisbane.scala.meetup.models.{Book, Id}
import brisbane.scala.meetup.service.BooksDataServiceImpl
import brisbane.scala.meetup.models.Id._
import cats.Show
import cats.data._
import cats.implicits._
import cats.effect.IO
import io.circe.{Json, JsonObject}
import org.http4s._
import org.http4s.implicits._
import org.http4s.circe.CirceEntityCodec._
import io.circe.syntax._
import io.circe.jawn._
import org.scalamock.specs2.MockContext
import org.specs2.mutable.Specification

class LibrarySpec extends Specification {

  private def libraryService(dao: BooksDao[IO]): Kleisli[IO, Request[IO], Response[IO]] =
    new LibraryService[IO](new BooksDataServiceImpl[IO](dao)).service.orNotFound

  implicit class ResponseExtensions(response: Response[IO]) {
    def bodyAsJson: Json = response.bodyAsText.compile.last.unsafeRunSync().map(parse).get.right.get
  }

  class DaoContext extends MockContext {
    val dao: BooksDao[IO] = mock[BooksDao[IO]]
    val id: Id = Id(UUID.randomUUID())
    val idPath: String = Show[Id].show(id)
    val invalidIdPath: String = Show[Id].show(Id(UUID.randomUUID()))
    val book = Book(
      "Fear and Loathing in Las Vegas",
      "Hunter S. Thompson",
      "917.93/13, B",
      "PN4874.T444 A3 1971",
      206)
    val bookWithId = Book.withId(id, book)

    val bookUpdate = Book.Partial(author = Some("Hunter S. Thompson"))

    (dao.create   _).expects(book).returning(id.pure[IO])
    (dao.fetchAll _).expects().returning(Vector(Book.withId(id, book)).pure[IO])
    (dao.fetch    _).expects(id).returning(Some(Book.withId(id, book)).pure[IO])
    (dao.fetch    _).expects(*).returning(None.pure[IO])
    (dao.upsert   _).expects(id, book).returning(IO.unit)
    (dao.update   _).expects(id, bookUpdate).returning(Some(()).pure[IO])
    (dao.update   _).expects(*, bookUpdate).returning(None.pure[IO])
    (dao.delete   _).expects(id).returning(Some(()).pure[IO])
    (dao.delete   _).expects(*).returning(None.pure[IO])
  }

  "POST /books" should {
    "return 201 and Id when valid" in new DaoContext {
      val request = Request[IO](Method.POST, Uri.uri("/books")).withBody(book.asJson)
      val response = libraryService(dao).run(request.unsafeRunSync()).unsafeRunSync()
      response.status must beEqualTo(Status.Created)
      response.bodyAsJson must beEqualTo(JsonObject.fromMap(Map("id" -> id.asJson)).asJson)
    }
  }

  "GET /books" should {
    "return 200 and array of books" in new DaoContext {
      val request = Request[IO](Method.GET, Uri.uri("/books"))
      val response = libraryService(dao).run(request).unsafeRunSync()
      response.status must beEqualTo(Status.Ok)
      response.bodyAsJson must beEqualTo(Vector(bookWithId).asJson)
    }
  }

  "GET /books/id" should {
    "return 200 and a specified book" in new DaoContext {
      val request = Request[IO](Method.GET, Uri.uri("/books") / idPath)
      val response = libraryService(dao).run(request).unsafeRunSync()
      response.status must beEqualTo(Status.Ok)
      response.bodyAsJson must beEqualTo(bookWithId.asJson)
    }
    "return 404 if book does not exist" in new DaoContext {
      val request = Request[IO](Method.GET, Uri.uri("/books") / invalidIdPath)
      val response = libraryService(dao).run(request).unsafeRunSync()
      response.status must beEqualTo(Status.NotFound)
    }
  }

  "PUT /books/id" should {
    "return 200 and upsert the specified book" in new DaoContext {
      val request = Request[IO](Method.PUT, Uri.uri("/books") / idPath).withBody(book.asJson)
      val response = libraryService(dao).run(request.unsafeRunSync()).unsafeRunSync()
      response.status must beEqualTo(Status.Ok)
    }
  }

  "PATCH /books/id" should {
    "return 200 and update the specified book" in new DaoContext {
      val request = Request[IO](Method.PATCH, Uri.uri("/books") / idPath).withBody(bookUpdate.asJson)
      val response = libraryService(dao).run(request.unsafeRunSync()).unsafeRunSync()
      response.status must beEqualTo(Status.Ok)
    }
    "return 404 if book does not exist" in new DaoContext {
      val request = Request[IO](Method.PATCH, Uri.uri("/books") / invalidIdPath).withBody(bookUpdate.asJson)
      val response = libraryService(dao).run(request.unsafeRunSync()).unsafeRunSync()
      response.status must beEqualTo(Status.NotFound)
    }
  }

  "DELETE /books/id" should {
    "return 200 and delete specified book" in new DaoContext {
      val request = Request[IO](Method.DELETE, Uri.uri("/books") / idPath)
      val response = libraryService(dao).run(request).unsafeRunSync()
      response.status must beEqualTo(Status.Ok)
    }
    "return 404 if book does not exist" in new DaoContext {
      val request = Request[IO](Method.DELETE, Uri.uri("/books") / invalidIdPath)
      val response = libraryService(dao).run(request).unsafeRunSync()
      response.status must beEqualTo(Status.NotFound)
    }
  }
}
