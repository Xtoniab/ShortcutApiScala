package models

import constants.ShortcutRegex
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}

case class ShortcutPath(category: String, action: String)

object ShortcutPath {
  def apply(path: String): Validated[String, ShortcutPath] = {
    if !ShortcutRegex.ActionRegex.matches(path) then Invalid("Invalid path format.")
    else
      val parts = path.split('.')
      Valid(new ShortcutPath(parts(0), parts(1)))
  }
}
