import com.twitter.storehaus.cache.MutableLRUCache
import org.scalatest.FlatSpec

class PencilSpec extends FlatSpec {
  "Pencil" should "return spaces after reaching dull point" in {
    val pencil = new Pencil(maxSharpness=1500, dullPoint = 0, maxResharpenings = 0)
    assert(pencil.write("Here is some text") == "Here is          ")
  }

  "Pencil" should "not count whitespace towards wear" in {
    val pencil = new Pencil(maxSharpness=1, dullPoint = 0, maxResharpenings = 0)
    assert(pencil.write(" \t\r\n") == " \t\r\n")
    assert(pencil.sharpness == 1)
  }

  "Pencil" should "continue writing past dull point if resharpened" in {
    val pencil = new Pencil(maxSharpness=1500, dullPoint = 0, maxResharpenings = 2)
    assert(pencil.write("Here is") == "Here is")
    pencil.resharpen()
    assert(pencil.write("some t") == "some t")
    pencil.resharpen()
    assert(pencil.write("ext") == "ext")
  }

  "Pencil" should "continue writing spaces if resharpened too many times" in {
    val pencil = new Pencil(maxSharpness=1500, dullPoint = 0, maxResharpenings = 1)
    assert(pencil.write("Here is") == "Here is")
    pencil.resharpen()
    assert(pencil.write("some t") == "some t")
    pencil.resharpen()
    assert(pencil.write("   ") == "   ")
  }

  "Pencil" should "reduce sharpness for a non-ASCII character" in {
    val pencil = new Pencil(maxSharpness=1000, dullPoint = 0)
    assert(pencil.write("象") == "象")
    assert(pencil.sharpness < 1000)
  }

  "Pencil" should "throw an exception if maxSharpness is negative" in {
    assertThrows[IllegalArgumentException] {
      new Pencil(maxSharpness = -1)
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

  "Pencil" should "throw an exception if sharpness is negative" in {
    assertThrows[IllegalArgumentException] {
      val pencil: Pencil = new Pencil()
      pencil.sharpness = -1
    }
  }

  "Pencil" should "throw an exception if timesResharpened is negative" in {
    assertThrows[IllegalArgumentException] {
      val pencil: Pencil = new Pencil()
      pencil.timesResharpened = -1
    }
  }

  "Pencil" should "throw an exception if sharpness is greater than max sharpness" in {
    assertThrows[IllegalArgumentException] {
      val pencil: Pencil = new Pencil(maxSharpness = 0)
      pencil.sharpness = 1
    }
  }

  "Pencil" should "cache computed lead cost" in {
    val pencil = new Pencil(maxSharpness=1000, dullPoint = 0)
    val clazz = classOf[Pencil]
    val field = clazz.getDeclaredField("leadCostCache")
    field.setAccessible(true)
    val cache = field.get(pencil).asInstanceOf[MutableLRUCache[Char, Int]]
    cache += ('象' -> 0)
    assert(pencil.write("象") == "象")
    assert(pencil.sharpness == 1000)
  }

  // TODO: test lead cost computation?
  // I sorta TDDd that feature, by running the same code as a Groovy script and counting pixels in an image editor to verify
}
