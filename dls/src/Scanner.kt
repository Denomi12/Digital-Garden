class Scanner(private val input: String) {
    private var pos = 0
    private val length = input.length

    private enum class State {
        START, IDENTIFIER, NUMBER, LPAREN, RPAREN, LCURLY, RCURLY, COLON, COMMA, ASSIGN, SEMI, ERROR
    }

    private var currentState = State.START

    private val keywords = setOf("park", "koordinate", "koordinata", "drevo", "klop", "koš", "ribnik", "pot", "for", "to", "begin", "end")

    fun getNextToken() : Token? {
        while (pos < length) {
            val currentChar = input[pos]

            // iz začetnega stanja se na podlagi naslednjega znaka premaknemo v novo stanje
            when (currentState) {
                State.START -> {
                    when {
                        currentChar.isWhitespace() -> {
                            pos++
                            continue
                        }
                        currentChar.isLetter() -> {
                            currentState = State.IDENTIFIER
                        }
                        currentChar.isDigit() -> {
                            currentState = State.NUMBER
                        }
                        currentChar == '(' -> {
                            currentState = State.LPAREN
                        }
                        currentChar == ')' -> {
                            currentState = State.RPAREN
                        }
                        currentChar == '{' -> {
                            currentState = State.LCURLY
                        }
                        currentChar == '}' -> {
                            currentState = State.RCURLY
                        }
                        currentChar == ',' -> {
                            currentState = State.COMMA
                        }
                        currentChar == ':' -> {
                            currentState = State.COLON
                        }
                        currentChar == '=' -> {
                            currentState = State.ASSIGN
                        }
                        currentChar == ';' -> {
                            currentState = State.SEMI
                        }
                        else -> {
                            throw IllegalArgumentException("Unknown character: $currentChar")
                        }
                    }
                }
                // double števila oblike 12.34, 34, -5.43
                State.NUMBER -> {
                    val start = pos
                    // omogočanje negativnih števil
                    if (input[pos] == '-') pos++
                    var dotSeen = false

                    // loopamo dokler ne naletimo več na število
                    while (pos < length && (input[pos].isDigit() || input[pos] == '.')) {
                        // preverimo, če že imamo piko v številu, drugače breakamo
                        if (input[pos] == '.') {
                            if (dotSeen == true) break
                            dotSeen = true
                        }
                        pos++
                    }

                    currentState = State.START
                    return Token("double", input.substring(start,pos))
                }
                State.IDENTIFIER -> {
                    val start = pos

                    // identifier se more začeti s črko
                    if (!input[start].isLetter()) {
                        throw IllegalArgumentException("Identifier must start with letter at position $start")
                    }

                    while (pos < length && input[pos].isLetterOrDigit()) {
                        pos++
                    }
                    currentState = State.START
                    val word = input.substring(start, pos)
                    // imamo ključne besede v setOf, ki jih potrebujemo za jezik
                    return if (word in keywords) {
                        Token(word, word)
                    }
                    else {
                        Token("variable", word)
                    }
                }
                State.LPAREN -> {
                    pos++
                    currentState = State.START
                    return Token("lparen", "(")
                }
                State.RPAREN -> {
                    pos++
                    currentState = State.START
                    return Token("rparen", ")")
                }
                State.LCURLY -> {
                    pos++
                    currentState = State.START
                    return Token("lcurly", "{")
                }
                State.RCURLY -> {
                    pos++
                    currentState = State.START
                    return Token("rcurly", "}")
                }
                State.COMMA -> {
                    pos++
                    currentState = State.START
                    return Token("comma", ",")
                }
                State.COLON -> {
                    pos++
                    currentState = State.START
                    return Token("colon", ":")
                }
                State.ASSIGN -> {
                    pos++
                    currentState = State.START
                    return Token("assign", "=")
                }
                State.SEMI -> {
                    pos++
                    currentState = State.START
                    return Token("semi", ";")
                }
                else -> {
                    throw IllegalStateException("Unexpected state: $currentState")
                }
            }
        }
        return null
    }
}

