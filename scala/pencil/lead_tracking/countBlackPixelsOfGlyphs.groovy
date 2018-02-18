import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage

def alphabet = new ArrayList<String>()
def letters = 'abcdefghijklmnopqrstuvwxyz'
letters.each { alphabet += it }
letters.each { alphabet += it.toUpperCase() }
'1234567890!@#$%^&*()-_=+[{]}\\|;:\'",<.>/?'.each { alphabet += it }

//IMAGES_DIR = "groovy-images"
//new File(IMAGES_DIR).delete()
//new File(IMAGES_DIR).mkdir()

def sizeMap = [:]
alphabet.each { character ->
    // find minimum height and width to hold character
    def image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    def graphics2D = image.createGraphics()
    def font = new Font("Arial", Font.PLAIN, 48)
    graphics2D.setFont(font)
    def fontMetrics = graphics2D.getFontMetrics()
    int width = fontMetrics.stringWidth(character)
    int height = fontMetrics.getHeight()
    graphics2D.dispose()

    // paint character
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    graphics2D = image.createGraphics()
//    graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)
//    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
//    graphics2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
//    graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE)
//    graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
//    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
//    graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
//    graphics2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
    graphics2D.setFont(font)
    fontMetrics = graphics2D.getFontMetrics()
    graphics2D.setColor(Color.BLACK)
    graphics2D.drawString(character, 0, fontMetrics.getAscent())
    graphics2D.dispose()

//    javax.imageio.ImageIO.write(image, "png", new File("${IMAGES_DIR}/${character}.png"))

    // count black pixels
    int blackPixels = 0
    for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) {
            if (image.getRGB(x, y) == Color.BLACK.getRGB())
                blackPixels++
        }
    }
    sizeMap.put("'${character}'".toString(), blackPixels)
}

println sizeMap.sort({-it.value})
