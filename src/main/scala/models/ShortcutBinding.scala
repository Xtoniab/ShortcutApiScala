package models

import constants.ShortcutRegex
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import models.SystemKey.SystemKey

case class ShortcutBinding(modifierKeys: List[SystemKey], key: Char) {

  private val sortedModifiers: List[SystemKey] = modifierKeys.sorted

  override def equals(obj: Any): Boolean = obj match {
    case that: ShortcutBinding =>
      this.sortedModifiers == that.sortedModifiers && this.key == that.key
    case _ => false
  }

  override def hashCode(): Int = {
    sortedModifiers.hashCode() ^ key.hashCode()
  }

  override def toString: String = {
    val modifiersStr: String = sortedModifiers.map(_.toString).mkString(" + ")
    s"$modifiersStr + $key"
  }
}

object ShortcutBinding {
  def apply(binding: String): Validated[String, ShortcutBinding] = {
    if (!ShortcutRegex.BindingRegex.matches(binding)) {
      Invalid("Invalid binding format.")
    } else {
      val keys: List[String] = binding.split('+').map(_.trim).toList
      val modifiers: List[SystemKey] = keys.dropRight(1).map(SystemKey.withName)
      val key: Char = keys.last.head
      Valid(ShortcutBinding(modifiers, key))
    }
  }
}
