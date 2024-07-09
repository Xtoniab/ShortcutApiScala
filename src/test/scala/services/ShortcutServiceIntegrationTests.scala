package services

import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Resource}
import controllers.ShortcutsController
import dtos.ShortcutDto
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.Method.*
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.client.*
import org.http4s.client.dsl.io.*
import org.http4s.implicits.*
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext

case class AddResponse(success: Boolean, error: Option[String])
case class GetResponseEntry(actionName: String, binding: String)

class ShortcutServiceIntegrationTests extends AnyFunSuite with Matchers with BeforeAndAfterAll {

  implicit val ec: ExecutionContext = ExecutionContext.global

  val clientResource: Resource[IO, Client[IO]] = BlazeClientBuilder[IO].withExecutionContext(ec).resource
  val shortcutService = new ShortcutServiceImpl()
  val baseUrl = "http://localhost:5000"

  private def createServer(shortcutService: ShortcutService): Resource[IO, Unit] =
    for {
      shortcutsController <- Resource.pure(new ShortcutsController(shortcutService))
      server <- BlazeServerBuilder[IO]
        .bindHttp(5000, "localhost")
        .withHttpApp(shortcutsController.routes.orNotFound)
        .resource
    } yield ()

  override def beforeAll(): Unit = {
    createServer(shortcutService).use(_ => IO.never).start.unsafeRunSync()
  }

  def withClient(testCode: Client[IO] => Any): Unit = {
    clientResource.use { client =>
      IO(testCode(client))
    }.unsafeRunSync()
  }

  test("AddShortcut should return Ok result when shortcut is valid") {
    withClient { client =>
      val testCases = List(
        ("Ctrl + Shift + T", "Open new tab", "browser.newtab"),
        ("Alt + Shift + N", "Open new window", "browser.newwindow"),
        ("Ctrl + Alt + Z", "Task Manager", "system.taskmanager")
      )

      testCases.foreach { case (binding, description, action) =>
        val shortcutDto = ShortcutDto(binding, description, action)
        val req = POST(shortcutDto, Uri.unsafeFromString(s"$baseUrl/add"))
        val res = client.expect[AddResponse](req).unsafeRunSync()

        res.success shouldBe true
      }
    }
  }

  test("AddShortcut should return BadRequest when shortcut is invalid") {
    withClient { client =>
      val testCases = List(
        ("Ctrl + Shift", "Incomplete shortcut", "browser.incomplete"),
        ("Ctrl + Shift + 1", "Invalid key", "browser.invalidkey"),
        ("Ctrl + Shift + Z + W", "Too many keys", "browser.toomanykeys")
      )

      testCases.foreach { case (binding, description, action) =>
        val shortcutDto = ShortcutDto(binding, description, action)
        val req = POST(shortcutDto, Uri.unsafeFromString(s"$baseUrl/add"))
        val res = client.status(req).unsafeRunSync()

        res shouldBe Status.BadRequest
      }
    }
  }

  test("GetShortcutsByCategory should return shortcuts when category exists") {
    shortcutService.clearShortcuts().unsafeRunSync()

    withClient { client =>
      val testCases = List(
        ("browser", "Ctrl + Shift + T", "Open new tab", "newtab"),
        ("browser", "Ctrl + Shift + N", "Open new window", "newwindow")
      )

      testCases.foreach { case (category, binding, description, action) =>
        val shortcutDto = ShortcutDto(
          binding = binding,
          description = description,
          action = s"$category.$action"
        )

        client.expect[AddResponse](
          POST(shortcutDto, Uri.unsafeFromString(s"$baseUrl/add"))
        ).unsafeRunSync()

        val response = client.expect[List[GetResponseEntry]](
          GET(Uri.unsafeFromString(s"$baseUrl/category/$category"))
        ).unsafeRunSync()

        response should contain(GetResponseEntry(action, binding))
      }
    }
  }


  test("GetShortcutsByCategory should return empty when category does not exist") {
    shortcutService.clearShortcuts().unsafeRunSync()
    withClient { client =>
      val res = client.expect[List[GetResponseEntry]](
        GET(
          Uri.unsafeFromString(s"$baseUrl/category/nonexistent")
        )).unsafeRunSync()
      res shouldBe empty
    }
  }

  test("AddShortcut should return false when shortcut already exists") {
    shortcutService.clearShortcuts().unsafeRunSync()

    withClient { client =>
      val shortcutDto = ShortcutDto(
        binding = "Ctrl + Shift + T",
        description = "Open new tab",
        action = "browser.newtab"
      )

      client.expect[AddResponse](
        POST(shortcutDto, Uri.unsafeFromString(s"$baseUrl/add"))
      ).unsafeRunSync()

      val addRequest = POST(shortcutDto, Uri.unsafeFromString(s"$baseUrl/add"))

      val result = client.fetch(addRequest) {
        case Status.BadRequest(response) => response.as[AddResponse]
        case _ => IO.raiseError(new Exception("Unexpected status"))
      }.unsafeRunSync()

      result.success shouldBe false
    }
  }

  test("AddShortcut and GetShortcutsByCategory") {
    shortcutService.clearShortcuts().unsafeRunSync()

    withClient { client =>
      val testCases = List(
        ("Ctrl + Shift + K", "Push current branch to remote repository", "git.push"),
        ("Ctrl + T", "Fetch latest changes", "git.fetch")
      )

      testCases.foreach { case (binding, description, action) =>
        val shortcutDto = ShortcutDto(binding, description, action)
        client.expect[AddResponse](POST(shortcutDto, Uri.unsafeFromString(s"$baseUrl/add"))).unsafeRunSync()

        val category = action.split('.')(0)
        val actionName = action.split('.')(1)

        val res = client.expect[List[GetResponseEntry]](
          GET(Uri.unsafeFromString(s"$baseUrl/category/$category"))
        ).unsafeRunSync()

        res should contain(GetResponseEntry(actionName, binding))
      }
    }
  }
}
