
fun main() {
    val scanner = Scanner()
//    val tokens = scanner.readFile("magdalenskiPark.txt")
    val tokens = scanner.readFile("mariborskiPark.txt")
    val parser = Parser(tokens)
    parser.parse()
    parser.toGeoJson()

}