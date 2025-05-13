import java.io.File

fun main() {
    val fileName = "input.txt"
    val file = File(fileName)

    if (!file.exists()) {
        println("Napaka: Datoteka $fileName ne obstaja.")
        return
    }

    val input = file.readText()
    val scanner = Scanner(input)

    val tokens = mutableListOf<Token>()
    while (true) {
        val token = scanner.getNextToken()
        if (token == null) {
            break
        }
        tokens.add(token)
    }

    println(tokens.joinToString("\n") { it.toString() })

    val parser = Parser(tokens)
    if (parser.parse()) {
        println("ACCEPT")
    } else {
        println("REJECT")
    }

}
