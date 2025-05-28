
fun main() {
    val scanner = Scanner()
//    val tokens = scanner.readFile("magdalenskiPark.txt")
//    val tokens = scanner.readFile("mariborskiPark.txt")
//    val tokens = scanner.readFile("smesni.txt")
//    val tokens = scanner.readFile("poljane.txt")
    val tokens = scanner.readFile("celiPark.txt")

    val parser = Parser(tokens)
    parser.parse()
    parser.toGeoJson()

}