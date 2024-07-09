import cats.effect.{ExitCode, IO, IOApp, Resource}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._
import controllers.ShortcutsController
import services.{ShortcutService, ShortcutServiceImpl}

object Main extends IOApp {

  private def createServer(shortcutService: ShortcutService): Resource[IO, Unit] =
    for {
      shortcutsController <- Resource.pure(new ShortcutsController(shortcutService))
      server <- BlazeServerBuilder[IO]
        .bindHttp(5001, "0.0.0.0")
        .withHttpApp(Routes(shortcutsController).orNotFound)
        .resource
    } yield ()

  def run(args: List[String]): IO[ExitCode] = {
    for {
      shortcutService <- ShortcutServiceImpl.create
      _ <- createServer(shortcutService).use(_ => IO.never)
    } yield ExitCode.Success
  }
}
