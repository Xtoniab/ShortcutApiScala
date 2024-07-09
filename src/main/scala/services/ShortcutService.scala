package services

import cats.effect.IO
import models.{Shortcut, ShortcutBinding}
import scala.collection.concurrent.TrieMap

trait ShortcutService {
  def addShortcut(shortcut: Shortcut): IO[Either[String, Unit]]
  def deleteShortcut(binding: ShortcutBinding): IO[Boolean]
  def getShortcutsByCategory(category: String): IO[List[Shortcut]]
  def clearShortcuts(): IO[Unit]
}

class ShortcutServiceImpl extends ShortcutService {
  private val shortcuts = TrieMap.empty[ShortcutBinding, Shortcut]

  override def addShortcut(shortcut: Shortcut): IO[Either[String, Unit]] = IO {
    if (shortcuts.putIfAbsent(shortcut.binding, shortcut).isEmpty) Right(())
    else Left("Shortcut already exists")
  }

  override def deleteShortcut(binding: ShortcutBinding): IO[Boolean] = IO {
    shortcuts.remove(binding).isDefined
  }

  override def getShortcutsByCategory(category: String): IO[List[Shortcut]] = IO {
    shortcuts.values.filter(_.path.category == category).toList
  }

  override def clearShortcuts(): IO[Unit] = IO {
    shortcuts.clear()
  }
}
