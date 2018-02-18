import org.scalatest.FlatSpec

class PencilSpec extends FlatSpec {

  "Pencil" should "return spaces after reaching dull point" in {
    val pencil = new Pencil(initialSharpness=1000, dullPoint = 0, maxResharpenings = 0)
    assert(pencil.write("Here is some text") == "Here is          ")
  }

  "Pencil" should "not count whitespace towards wear" in {
    val pencil = new Pencil(initialSharpness=1, dullPoint = 0, maxResharpenings = 0)
    assert(pencil.write(" \t\r\n") == " \t\r\n")
    assert(pencil.initialSharpness == 1)
  }

  "Pencil" should "continue writing past dull point if resharpened" in {
    val pencil = new Pencil(initialSharpness=1000, dullPoint = 0, maxResharpenings = 2)
    assert(pencil.write("Here is") == "Here is")
    pencil.resharpen()
    assert(pencil.write("some t") == "some t")
    pencil.resharpen()
    assert(pencil.write("ext") == "ext")
  }

  "Pencil" should "continue writing spaces if resharpened too many times" in {
    val pencil = new Pencil(initialSharpness=1000, dullPoint = 0, maxResharpenings = 1)
    assert(pencil.write("Here is") == "Here is")
    pencil.resharpen()
    assert(pencil.write("some t") == "some t")
    pencil.resharpen()
    assert(pencil.write("   ") == "   ")
  }

  "Pencil" should "throw an exception if unable to determine wear for a character" in {
    assertThrows[IllegalArgumentException] {
      val pencil = new Pencil()
      pencil.write("象")
    }
  }

  "Pencil" should "throw an exception if initialSharpness is negative" in {
    assertThrows[IllegalArgumentException] {
      new Pencil(initialSharpness = -1)
    }
  }

  "Pencil" should "throw an exception if dullPoint is negative" in {
    assertThrows[IllegalArgumentException] {
      new Pencil(dullPoint = -1)
    }
  }

  "Pencil" should "throw an exception if maxResharpenings is negative" in {
    assertThrows[IllegalArgumentException] {
      new Pencil(maxResharpenings = -1)
    }
  }

}
