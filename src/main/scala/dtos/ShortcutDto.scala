package dtos

import io.circe.generic.semiauto.*
import io.circe.{Codec, Decoder, Encoder}
import models.{Shortcut, ShortcutBinding, ShortcutPath}

case class ShortcutDto(binding: String, description: String, action: String) derives Codec.AsObject {
  def toModel: Either[String, Shortcut] = {
    for {
      path <- ShortcutPath(action).toEither
      binding <- ShortcutBinding(binding).toEither
    } yield Shortcut(path = path, description = description, binding = binding)
  }
}