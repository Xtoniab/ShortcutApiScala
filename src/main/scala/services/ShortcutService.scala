package services

import cats.effect.{IO, Ref}
import cats.effect.unsafe.implicits.global
import models.{Shortcut, ShortcutBinding}

trait ShortcutService {
  def addShortcut(shortcut: Shortcut): IO[Either[String, Unit]]
  def deleteShortcut(binding: ShortcutBinding): IO[Boolean]
  def getShortcutsByCategory(category: String): IO[List[Shortcut]]
  def clearShortcuts(): IO[Unit]
}

class ShortcutServiceImpl(shortcutsRef: Ref[IO, Map[ShortcutBinding, Shortcut]]) extends ShortcutService {

  override def addShortcut(shortcut: Shortcut): IO[Either[String, Unit]] = {
    shortcutsRef.modify { shortcuts =>
      if (shortcuts.contains(shortcut.binding)) {
        (shortcuts, Left("Shortcut already exists"))
      } else {
        (shortcuts + (shortcut.binding -> shortcut), Right(()))
      }
    }
  }

  override def deleteShortcut(binding: ShortcutBinding): IO[Boolean] = {
    shortcutsRef.modify { shortcuts =>
      if (shortcuts.contains(binding)) {
        (shortcuts - binding, true)
      } else {
        (shortcuts, false)
      }
    }
  }

  override def getShortcutsByCategory(category: String): IO[List[Shortcut]] = {
    shortcutsRef.get.map { shortcuts =>
      shortcuts.values.filter(_.path.category == category).toList
    }
  }

  override def clearShortcuts(): IO[Unit] = {
    shortcutsRef.set(Map.empty[ShortcutBinding, Shortcut])
  }
}

object ShortcutServiceImpl {
  def create: IO[ShortcutServiceImpl] = {
    Ref.of[IO, Map[ShortcutBinding, Shortcut]](Map.empty).map(new ShortcutServiceImpl(_))
  }
}