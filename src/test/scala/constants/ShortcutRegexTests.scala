package constants

import constants.ShortcutRegex.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ShortcutRegexTests extends AnyFunSuite with Matchers {

  test("BindingPattern should match valid patterns") {
    val validCases = List(
      "Ctrl+Alt+S",
      "Alt+Shift+T",
      "Ctrl+Shift+X",
      "Ctrl+Alt+Shift+P")

    validCases.foreach { binding =>
      withClue(s"Testing binding pattern: $binding") {
        BindingRegex.matches(binding) shouldBe true
      }
    }
  }

  test("BindingPattern should not match invalid patterns") {
    val invalidCases = List(
      "Ctrl++A",
      "T",
      "Ctrl+Shift+1",
      "Ctrl+Shift+FS")

    invalidCases.foreach { binding =>
      withClue(s"Testing binding pattern: $binding") {
        BindingRegex.matches(binding) shouldBe false
      }
    }
  }

  test("ActionPattern should match valid patterns") {
    val validCases = List(
      "file.save",
      "edit.cut")

    validCases.foreach { action =>
      withClue(s"Testing action pattern: $action") {
        ActionRegex.matches(action) shouldBe true
      }
    }
  }

  test("ActionPattern should not match invalid patterns") {
    val invalidCases = List(
      "file.saveAs",
      "file-")

    invalidCases.foreach { action =>
      withClue(s"Testing action pattern: $action") {
        ActionRegex.matches(action) shouldBe false
      }
    }
  }
}
