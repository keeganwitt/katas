class Pencil(val initialSharpness: Int = 10000, val dullPoint: Int = 1000, val maxResharpenings: Int = 10) {
  var sharpness: Int = initialSharpness
  var timesResharpened: Int = 0

  if (initialSharpness < 0 || dullPoint < 0 || maxResharpenings < 0)
    throw new IllegalArgumentException("Initial sharpness, dull point, and max resharpenings must not be negative")

  object Pencil {
    val WEAR = Map('@' -> 512, 'M' -> 427, 'B' -> 406, 'W' -> 389, 'R' -> 387, 'E' -> 380, 'D' -> 356, 'N' -> 353,
      'G' -> 328, 'H' -> 321, 'Q' -> 319, 'P' -> 310, 'Z' -> 310, 'K' -> 302, 'O' -> 302, '&' -> 293, '#' -> 289,
      'm' -> 288, '$' -> 285, 'F' -> 280, 'S' -> 278, 'A' -> 274, 'U' -> 268, 'g' -> 267, 'w' -> 264, 'X' -> 259,
      '8' -> 256, '%' -> 253, 'C' -> 247, '6' -> 245, '5' -> 242, '9' -> 242, '2' -> 239, 'q' -> 236, 'd' -> 235,
      '4' -> 234, 'p' -> 229, 'T' -> 228, '0' -> 228, 'b' -> 226, 'e' -> 219, 'a' -> 212, 'V' -> 212, ']' -> 210,
      'k' -> 209, 'h' -> 207, 'L' -> 204, '3' -> 201, 'z' -> 193, 'Y' -> 189, 'o' -> 186, '7' -> 185, 'u' -> 182,
      'y' -> 182, 'J' -> 177, '=' -> 176, 'n' -> 175, 's' -> 174, 'x' -> 171, '[' -> 165, 't' -> 153, 'c' -> 152,
      '{' -> 149, '}' -> 147, '?' -> 147, 'v' -> 145, 'f' -> 144, '+' -> 142, 'I' -> 140, '|' -> 135, '1' -> 126,
      'j' -> 125, '(' -> 123, ')' -> 122, '>' -> 116, '<' -> 115, 'l' -> 105, '!' -> 105, '^' -> 97, 'i' -> 93,
      'r' -> 93, '_' -> 81, '/' -> 77, '\\' -> 77, '"' -> 76, '*' -> 58, '-' -> 48, ';' -> 48, ':' -> 40, '\'' -> 34,
      ',' -> 28, '.' -> 20)
  }

  def write(text: String): String = {
    verify(text)
    text.map(write(_)).mkString
  }

  def write(char: Character): Character = {
    if (sharpness <= dullPoint) {
      ' '
    } else {
      if (!Character.isWhitespace(char))
        sharpness -= Pencil.WEAR(char)
      char
    }
  }

  def resharpen() {
    if (timesResharpened <= maxResharpenings) {
      sharpness = initialSharpness
      timesResharpened += 1
    }
  }

  def verify(text: String) {
    text.foreach { c =>
      if (!Character.isWhitespace(c) && !Pencil.WEAR.isDefinedAt(c))
        throw new IllegalArgumentException(s"Unknown cost for '$c'")
    }
  }
}
