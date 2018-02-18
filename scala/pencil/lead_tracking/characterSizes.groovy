// another idea I had for approximating lead usage (just measure overall space taken by the glyph)
// https://docs.oracle.com/javase/tutorial/2d/text/measuringtext.html

import java.awt.geom.AffineTransform
import java.awt.font.FontRenderContext
import java.awt.Font

def alphabet = new ArrayList<String>()
def letters = 'abcdefghijklmnopqrstuvwxyz'
letters.each { alphabet += it }
letters.each { alphabet += it.toUpperCase() }
'1234567890!@#$%^&*()-_=+[{]}\\|;:\'",<.>/?'.each { alphabet += it }

def affineTransform = new AffineTransform()
def fontRenderContext = new FontRenderContext(affineTransform, true, true)
def font = new Font("Arial", Font.PLAIN, 20)

def sizeMap = [:]
alphabet.each { character ->
    int width = (int) font.getStringBounds(character, fontRenderContext).width
    int height = (int) font.getStringBounds(character, fontRenderContext).height
//    println "'${character}' -> ${width} x ${height} = ${width * height}"
    sizeMap.put("'${character}'".toString(), width*height)
}
println sizeMap.sort({-it.value})
