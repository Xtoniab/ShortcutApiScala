package controllers

import cats.effect.IO
import org.http4s.dsl.io._
import org.http4s.{HttpRoutes, Request}
import org.http4s.circe.CirceEntityCodec._
import io.circe.syntax._
import dtos.ShortcutDto
import services.ShortcutService
import encoders.Encoders._

class ShortcutsController(shortcutService: ShortcutService) {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req@POST -> Root / "add" =>
      req.as[ShortcutDto].flatMap { shortcutDto =>
        shortcutDto.toModel match {
          case Right(shortcut) =>
            shortcutService.addShortcut(shortcut).flatMap {
              case Right(_) => Ok(Map(
                "success" -> true).asJson
              )
              case Left(error) => BadRequest(Map(
                "success" -> false,
                "error" -> error).asJson
              )
            }
          case Left(error) =>
            BadRequest(Map("success" -> false, "error" -> error).asJson)
        }
      }

    case GET -> Root / "category" / categoryName =>
      shortcutService.getShortcutsByCategory(categoryName).flatMap { shortcuts =>
        val result = shortcuts.map { s =>
          Map(
            "actionName" -> s.path.action,
            "binding" -> s.binding.toString
          )
        }
        Ok(result.asJson)
      }
  }
}
