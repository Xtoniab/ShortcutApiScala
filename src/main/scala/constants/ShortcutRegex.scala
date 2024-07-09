package constants

import scala.util.matching.Regex

object ShortcutRegex {
  val BindingPattern: String = "^(Ctrl|Alt|Shift)(\\s*\\+\\s*(Ctrl|Alt|Shift)){0,2}\\s*\\+\\s*[A-Z]$"
  val ActionPattern: String = "^[a-z]+\\.[a-z]+$"

  val BindingRegex: Regex = BindingPattern.r
  val ActionRegex: Regex = ActionPattern.r
}
