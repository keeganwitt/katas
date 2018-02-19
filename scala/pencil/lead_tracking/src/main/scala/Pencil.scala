import java.awt.image.BufferedImage
import java.awt.{Color, Font, FontMetrics}

import com.twitter.storehaus.cache.MutableLRUCache

class Pencil(val maxSharpness: Int = 10000, val dullPoint: Int = 1000, val maxResharpenings: Int = 10) {
  private[this] var _sharpness: Int = maxSharpness
  private[this] var _timesResharpened: Int = 0
  // using a LRU cache instead of a map because ideogram-based languages could grow to use a fair amount of memory (e.g. Chinese)
  private[this] val leadCostCache = MutableLRUCache[Char, Int](500)

  if (maxSharpness < 0)
    throw new IllegalArgumentException("Max sharpness must not be negative")
  if (dullPoint < 0)
    throw new IllegalArgumentException("Dull point must not be negative")
  if (maxResharpenings < 0)
    throw new IllegalArgumentException("Max resharpenings must not be negative")

  def write(string: String): String = {
    string.map(write).mkString
  }

  def write(character: Char): Char = {
    if (_sharpness <= dullPoint) {
      ' '
    } else {
      if (!character.isWhitespace)
        _sharpness -= findOrCalculateWear(character)
      character
    }
  }

  def resharpen() {
    if (_timesResharpened <= maxResharpenings) {
      _sharpness = maxSharpness
      _timesResharpened += 1
    }
  }

  def findOrCalculateWear(character: Char): Int = {
    leadCostCache.getOrElseUpdate(character, Pencil.calculateWear(character))
  }

  def sharpness: Int = _sharpness

  def sharpness_= (value:Int)() {
    if (value < 0)
      throw new IllegalArgumentException("Sharpness must not be negative")
    if (value > maxSharpness)
      throw new IllegalArgumentException("Sharpness must not exceed max sharpness")
    _sharpness = value
  }

  def timesResharpened: Int = _timesResharpened

  def timesResharpened_= (value:Int)() {
    if (value < 0)
      throw new IllegalArgumentException("Times resharpened must not be negative")
    _timesResharpened = value
  }
}

object Pencil {
  val FONT = new Font("Arial", Font.PLAIN, 48)

  def calculateWear(character: Char): Int = {
    val string = character.toString

    val widthAndHeight = calculateWidthAndHeight(string)
    val width = widthAndHeight._1
    val height = widthAndHeight._2

    val imageAndFontMetrics = drawImage(width, height, string)
    val image = imageAndFontMetrics._1
    val fontMetrics = imageAndFontMetrics._2

    // uncomment below to save the image used for pixel counting
    //    javax.imageio.ImageIO.write(image, "png", new java.io.File(s"$character.png"))

    countBlackPixels(image, fontMetrics)
  }

  def calculateWidthAndHeight(string: String): (Int, Int) = {
    val image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    val graphics2D = image.createGraphics()
    graphics2D.setFont(Pencil.FONT)
    val fontMetrics = graphics2D.getFontMetrics()
    val width = fontMetrics.stringWidth(string)
    val height = fontMetrics.getHeight
    graphics2D.dispose()
    (width, height)
  }

  def drawImage(width: Int, height: Int, string: String): (BufferedImage, FontMetrics) = {
    // TODO: antialias the image, and count grays as fractional usage?
    // antialiasing can be enabled with
    // graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val graphics2D = image.createGraphics()
    graphics2D.setFont(Pencil.FONT)
    val fontMetrics = graphics2D.getFontMetrics()
    graphics2D.setColor(Color.BLACK)
    graphics2D.drawString(string, 0, fontMetrics.getAscent)
    graphics2D.dispose()
    (image, fontMetrics)
  }

  def countBlackPixels(image: BufferedImage, fontMetrics: FontMetrics): Int = {
    var blackPixels = 0
    for (y <- fontMetrics.getAscent until image.getHeight()) {
      for (x <- 0 until image.getWidth()) {
        if (image.getRGB(x, y) == 0)
          blackPixels += 1
      }
    }
    blackPixels
  }
}
