import java.io.FileReader
import java.io.FileWriter

class Scanner (
    private var row: Int = 1,
    private var col: Int = 0,
    private val maxState: Int = 100,
    private var automata: Array<IntArray> = Array(maxState + 1) { IntArray(256) {-1} },
    private var finalStates: IntArray = IntArray(maxState + 1) {0},
) {

    private val tError = -1
    private val tDouble = 0
    private val tId = 1
    private val tPark = 2
    private val tVar = 3
    private val tIf = 4
    private val tElse = 5
    private val tDrevo = 6
    private val tKlop = 7
    private val tKoš = 8
    private val tPot = 9
    private val tEllip = 10
    private val tPolygon = 11
    private val tLine = 12
    private val tBent_line = 13
    private val tBoundary = 14
    private val tValidate = 15
    private val tIn = 16
    private val tLbrace = 17
    private val tRbrace = 18
    private val tLparen = 19
    private val tRparen = 20
    private val tComma = 21
    private val tColon = 22
    private val tAssign = 23
    private val tPlus = 24
    private val tMinus = 25
    private val tTimes = 26
    private val tDivide = 27
    private val tGt = 28
    private val tLt = 29
    private val tGe = 30
    private val tLe = 31
    private val tEq = 32
    private val tNe = 33
    private val tWhiteSpace = 34
    private val tComment = 35

    init {

        for (i in 0..255) {
            automata[0][i] = tError
        }

        //DOUBLE
        for (i in '0'.code..'9'.code) {
            automata[0][i] = 1
            automata[1][i] = 1
        }
        automata[1]['.'.code] = 2
        for (i in '0'.code..'9'.code) {
            automata[2][i] = 3
            automata[3][i] = 3
        }

        //PARK
        automata[0]['p'.code] = 5
        automata[5]['a'.code] = 6
        automata[6]['r'.code] = 7
        automata[7]['k'.code] = 8

        //VAR
        automata[0]['v'.code] = 9
        automata[9]['a'.code] = 10
        automata[10]['r'.code] = 11

        //IF
        automata[0]['i'.code] = 12
        automata[12]['f'.code] = 13

        //ELSE
        automata[0]['e'.code] = 14
        automata[14]['l'.code] = 15
        automata[15]['s'.code] = 16
        automata[16]['e'.code] = 17

        //DREVO
        automata[0]['d'.code] = 18
        automata[18]['r'.code] = 19
        automata[19]['e'.code] = 20
        automata[20]['v'.code] = 21
        automata[21]['o'.code] = 22

        // KLOP
        automata[0]['k'.code] = 23
        automata[23]['l'.code] = 24
        automata[24]['o'.code] = 25
        automata[25]['p'.code] = 26

        //KOŠ
        automata[23]['o'.code] = 27
        automata[27]['s'.code] = 28

        // POT
        automata[5]['o'.code] = 29
        automata[29]['t'.code] = 30

        // ELLIP
        automata[15]['l'.code] = 31
        automata[31]['i'.code] = 32
        automata[32]['p'.code] = 33

        // POLYGON
        automata[29]['l'.code] = 34
        automata[34]['y'.code] = 35
        automata[35]['g'.code] = 36
        automata[36]['o'.code] = 37
        automata[37]['n'.code] = 38

        // LINE
        automata[0]['l'.code] = 39
        automata[39]['i'.code] = 40
        automata[40]['n'.code] = 41
        automata[41]['e'.code] = 42

        // BENT_LINE
        automata[0]['b'.code] = 43
        automata[43]['e'.code] = 44
        automata[44]['n'.code] = 45
        automata[45]['t'.code] = 46
        automata[46]['_'.code] = 47
        automata[47]['l'.code] = 48
        automata[48]['i'.code] = 49
        automata[49]['n'.code] = 50
        automata[50]['e'.code] = 51


        // BOUNDARY
        automata[43]['o'.code] = 52
        automata[52]['u'.code] = 53
        automata[53]['n'.code] = 54
        automata[54]['d'.code] = 55
        automata[55]['a'.code] = 56
        automata[56]['r'.code] = 57
        automata[57]['y'.code] = 58

        // VALIDATE
        automata[10]['l'.code] = 59
        automata[59]['i'.code] = 60
        automata[60]['d'.code] = 61
        automata[61]['a'.code] = 62
        automata[62]['t'.code] = 63
        automata[63]['e'.code] = 64

        // IN
        automata[12]['n'.code] = 65

        // SIMBOLI
        // {
        automata[0]['{'.code] = 66
        // }
        automata[0]['}'.code] = 67
        // (
        automata[0]['('.code] = 68
        // )
        automata[0][')'.code] = 69
        // ,
        automata[0][','.code] = 70
        // :
        automata[0][':'.code] = 71
        // :=
        automata[71]['='.code] = 72
        // +
        automata[0]['+'.code] = 73
        // -
        automata[0]['-'.code] = 74
        // *
        automata[0]['*'.code] = 75
        // /
        automata[0]['/'.code] = 76
        // >
        automata[0]['>'.code] = 77
        // >=
        automata[77]['='.code] = 78
        // <
        automata[0]['<'.code] = 79
        // <=
        automata[79]['='.code] = 80
        // ==
        automata[0]['='.code] = 81
        automata[81]['='.code] = 82
        // !=
        automata[0]['!'.code] = 83
        automata[83]['='.code] = 84

        // WHITESPACES
        automata[0][' '.code] = 85
        automata[0]['\t'.code] = 85
        automata[0]['\n'.code] = 85
        automata[0]['\r'.code] = 85
        automata[85][' '.code] = 85
        automata[85]['\t'.code] = 85
        automata[85]['\n'.code] = 85
        automata[85]['\r'.code] = 85

        // KOMENTAR
        automata[76]['/'.code] = 86
        for (i in 0..255) {
            if (i != '\n'.code) {
                automata[86][i] = 86
            }
        }

        //ID-JI
        for (i in 'a'.code..'z'.code) {
            automata[0][i] = 4
            automata[4][i] = 4
        }
        for (i in 'A'.code..'Z'.code) {
            automata[0][i] = 4
            automata[4][i] = 4
        }
        automata[0]['_'.code] = 4
        automata[4]['_'.code] = 4
        for (i in '0'.code..'9'.code) {
            automata[4][i] = 4
        }

        finalStates[0] = tError
        finalStates[1] = tDouble
        finalStates[3] = tDouble
        finalStates[4] = tId
        finalStates[8] = tPark
        finalStates[11] = tVar
        finalStates[13] = tIf
        finalStates[17] = tElse
        finalStates[22] = tDrevo
        finalStates[26] = tKlop
        finalStates[28] = tKoš
        finalStates[30] = tPot
        finalStates[33] = tEllip
        finalStates[38] = tPolygon
        finalStates[42] = tLine
        finalStates[51] = tBent_line
        finalStates[58] = tBoundary
        finalStates[64] = tValidate
        finalStates[65] = tIn
        finalStates[66] = tLbrace
        finalStates[67] = tRbrace
        finalStates[68] = tLparen
        finalStates[69] = tRparen
        finalStates[70] = tComma
        finalStates[71] = tColon
        finalStates[72] = tAssign
        finalStates[73] = tPlus
        finalStates[74] = tMinus
        finalStates[75] = tTimes
        finalStates[76] = tDivide
        finalStates[77] = tGt
        finalStates[78] = tGe
        finalStates[79] = tLt
        finalStates[80] = tLe
        finalStates[82] = tEq
        finalStates[84] = tNe
        finalStates[85] = tWhiteSpace
        finalStates[86] = tComment
    }

    private fun  identifyLexemType(lexem: String): String {
        return when (lexem) {
            "park" -> "park"
            "var" -> "var"
            "if" -> "if"
            "else" -> "else"
            "drevo" -> "drevo"
            "klop" -> "klop"
            "kos" -> "kos"
            "pot" -> "pot"
            "ellip" -> "ellip"
            "polygon" -> "polygon"
            "line" -> "line"
            "bent_line" -> "bent_line"
            "boundary" -> "boundary"
            "validate" -> "validate"
            "in" -> "in"
            "{" -> "lbrace"
            "}" -> "rbrace"
            "(" -> "lparen"
            ")" -> "rparen"
            "," -> "comma"
            ":" -> "colon"
            ":=" -> "assign"
            "+" -> "plus"
            "-" -> "minus"
            "*" -> "times"
            "/" -> "divide"
            ">" -> "gt"
            ">=" -> "ge"
            "<" -> "lt"
            "<=" -> "le"
            "==" -> "eq"
            "!=" -> "ne"
            else -> {
                when {
                    lexem.matches(Regex("[0-9]+(\\.[0-9]+)?")) -> "double"
                    lexem.matches(Regex("[a-zA-Z_][a-zA-Z0-9_]*")) -> "id"
                    else -> "unknown"
                }
            }
        }
    }
    private fun isFinalState(currentState: Int): Boolean {
        return (finalStates[currentState] != -1)
    }

    private fun returnFinalState(currentState: Int): Int {
        return finalStates[currentState]
    }

    private fun getNextState(currentState: Int, currentChar: Int): Int {
        return if (currentChar in automata[currentState].indices) {
            automata[currentState][currentChar]
        } else {
            -1
        }
    }

    fun readFile(filename: String): MutableList<Pair<String, String>> {
        val returnTokens: MutableList<Pair<String, String>> = mutableListOf()
        val reader = FileReader(filename)
        var currentState = 0
        var tempLexem = ""
        var token = readChar(reader)
        var prevToken = false
        var running = true
        var isComment = false
        var unreadToken: Token? = null
//        val fileWriter = FileWriter("out.txt")

        while (running) {
            prevToken = false

            if (unreadToken != null) {
                token = unreadToken
                unreadToken = null
            }

            val nextState = token?.lexem?.let { getNextState(currentState, it.code) }

            if (nextState == tComment) {
                isComment = true
            }

            if (nextState == tError || nextState == tWhiteSpace || token == null) {
                if (isComment) {
                    while (token?.lexem != '\n' && token != null) {
                        token = readChar(reader)
                    }
                    isComment = false
                } else {
                    if (isFinalState(currentState) && returnFinalState(currentState) != tWhiteSpace) {
                        val type = identifyLexemType(tempLexem)
                        if (type != "unknown") {
                            returnTokens.add(type to tempLexem)
//                            fileWriter.write("$type $tempLexem\n")
                            prevToken = true
                        }
                    }
                }

                if (nextState != tWhiteSpace && token != null && nextState != tComment) {
                    unreadToken = token
                }

                tempLexem = ""
                currentState = 0
            }

            if (nextState != tWhiteSpace && nextState != -1 && token != null) {
                tempLexem += token.lexem
                currentState = nextState ?: 0
            }

            if (!prevToken && unreadToken == null) {
                token = readChar(reader)
                if (token == null) {
                    running = false
                }
            }
        }

        if (tempLexem.isNotEmpty() && isFinalState(currentState) && returnFinalState(currentState) != tWhiteSpace) {
            val type = identifyLexemType(tempLexem)
            if (type != "unknown") {
                returnTokens.add(type to tempLexem)
//                fileWriter.write("$type $tempLexem\n")
            }
        }

//        fileWriter.close()
        return returnTokens
    }

    private fun readChar(reader: FileReader): Token? {
        val char = reader.read()
        if (char == -1) return null

        val lexem = char.toChar()

        if (lexem == '\n') {
            row++
            col = 1
        } else {
            col++
        }

        return Token(lexem, col, row)
    }
}