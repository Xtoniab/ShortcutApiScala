import org.http4s.HttpRoutes
import cats.effect.IO
import controllers.ShortcutsController

object Routes {
  def apply(shortcutsController: ShortcutsController): HttpRoutes[IO] = {
    shortcutsController.routes
  }
}
