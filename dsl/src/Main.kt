import java.io.File

fun main() {
    val scanner = Scanner()
    val tokens = scanner.readFile("mariborskiPark.txt")
    tokens.forEach { (type, lexeme) -> println("$type: $lexeme") }
    val parser = Parser(tokens)
    parser.parse()
    println()
    val geojson = parser.toGeoJson()
    println()
    File("geojsonOutput.txt").writeText(geojson)
    println("Geojson:\n$geojson")

}