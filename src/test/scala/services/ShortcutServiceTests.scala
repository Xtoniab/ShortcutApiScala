package services

import cats.effect.unsafe.implicits.global
import dtos.ShortcutDto
import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import services.ShortcutServiceImpl

import scala.compiletime.uninitialized

class ShortcutServiceTests extends AnyFunSuite with Matchers with TableDrivenPropertyChecks with BeforeAndAfter {

  private var service: ShortcutServiceImpl = uninitialized

  before {
    service = new ShortcutServiceImpl
  }

  test("AddShortcut should return expected result") {
    val testCases = Table(
      ("binding", "description", "path", "expected"),
      ("Ctrl+Alt+S", "Save File", "file.save", true),
      ("Ctrl+Shift+D", "Save As File", "file.save", true),
      ("Ctrl+Alt+S", "Open File", "file.open", true)
    )

    forAll(testCases) { (binding: String, description: String, path: String, expected: Boolean) =>
      service.clearShortcuts().unsafeRunSync()

      val shortcutDto = ShortcutDto(binding, description, path)
      val shortcut = shortcutDto.toModel.toOption.get

      val result = service.addShortcut(shortcut).unsafeRunSync()

      if (expected) {
        result shouldBe Right(())
      } else {
        result shouldBe a[Left[?, ?]]
      }
    }
  }

  test("GetShortcutsByCategory should return shortcuts") {
    service.clearShortcuts().unsafeRunSync()

    val shortcutDto1 = ShortcutDto("Ctrl+Alt+S", "Save File", "file.save")
    val shortcut1 = shortcutDto1.toModel.toOption.get
    service.addShortcut(shortcut1).unsafeRunSync()

    val shortcutDto2 = ShortcutDto("Ctrl+Alt+O", "Open File", "file.open")
    val shortcut2 = shortcutDto2.toModel.toOption.get
    service.addShortcut(shortcut2).unsafeRunSync()

    val result = service.getShortcutsByCategory("file").unsafeRunSync()

    result should have size 2
  }

  test("GetShortcutsByCategory should return empty list when category does not exist") {
    service.clearShortcuts().unsafeRunSync()

    val result = service.getShortcutsByCategory("nonexistent").unsafeRunSync()

    result shouldBe empty
  }

  test("ShortcutBinding should parse correctly") {
    val testCases = Table(
      ("binding", "expected"),
      ("Alt+     Shift+T", "Alt + Shift + T"),
      ("Ctrl+T", "Ctrl + T")
    )

    forAll(testCases) { (binding: String, expected: String) =>
      service.clearShortcuts().unsafeRunSync()

      val shortcutDto = ShortcutDto(binding, "Test Binding", "category.action")
      val shortcut = shortcutDto.toModel.toOption.get

      val bindingString = shortcut.binding.toString

      bindingString shouldBe expected
    }
  }

  test("ShortcutBinding should throw exception with invalid format") {
    val testCases = Table(
      "binding",
      "T",
      "Ctrl++A"
    )
    
    testCases.forall(binding => {
      service.clearShortcuts().unsafeRunSync()

      val shortcutDto = ShortcutDto(binding, "Test Binding", "category.action")
      val shortcut = shortcutDto.toModel

      shortcut.isLeft
    }) shouldBe true
    
  }
}
