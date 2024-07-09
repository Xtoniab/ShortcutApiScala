package dtos

import io.circe.generic.semiauto.*
import io.circe.{Decoder, Encoder}
import models.{Shortcut, ShortcutBinding, ShortcutPath}

case class ShortcutDto(binding: String, description: String, action: String) {
  def toModel: Either[String, Shortcut] = {
    for {
      path <- ShortcutPath(action).toEither
      binding <- ShortcutBinding(binding).toEither
    } yield Shortcut(path = path, description = description, binding = binding)
  }
}

object ShortcutDto {
  implicit val encoder: Encoder[ShortcutDto] = io.circe.generic.semiauto.deriveEncoder
  implicit val decoder: Decoder[ShortcutDto] = io.circe.generic.semiauto.deriveDecoder
}
