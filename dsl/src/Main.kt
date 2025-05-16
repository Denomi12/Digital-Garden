fun main() {
    val scanner = Scanner()
    val tokens = scanner.readFile("input.txt")
    tokens.forEach { (type, lexeme) -> println("$type: $lexeme") }
}