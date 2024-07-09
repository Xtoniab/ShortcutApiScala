package controllers

import cats.effect.IO
import org.http4s.dsl.io.*
import org.http4s.{HttpRoutes, Request}
import org.http4s.circe.CirceEntityCodec.*
import io.circe.syntax.*
import org.http4s.circe.*
import dtos.ShortcutDto
import io.circe.Codec
import services.ShortcutService

case class PostAddResponse(success: Boolean, error: Option[String] = None) derives Codec.AsObject
case class GetCategoryResponseEntry(actionName: String, binding: String) derives Codec.AsObject

class ShortcutsController(shortcutService: ShortcutService) {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "add" =>
      req.as[ShortcutDto].flatMap { shortcutDto =>
        shortcutDto.toModel match {
          case Right(shortcut) =>
            shortcutService.addShortcut(shortcut).flatMap {
              case Right(_) => Ok(PostAddResponse(success = true).asJson)
              case Left(error) => BadRequest(PostAddResponse(success = false, error = Some(error)).asJson)
            }
          case Left(error) =>
            BadRequest(PostAddResponse(success = false, error = Some(error)).asJson)
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
