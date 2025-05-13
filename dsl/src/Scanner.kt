class Scanner(private val input: String) {
    private var pos = 0
    private val length = input.length

    private enum class State {
        START, IDENTIFIER, NUMBER, LPAREN, RPAREN, LCURLY, RCURLY, COLON, COMMA, ASSIGN, SEMI, DIVIDE, ERROR
    }

    private var currentState = State.START

    private val keywords = setOf("park", "koordinate", "koordinata", "drevo", "klop", "koš", "ribnik", "pot", "drevored", "for", "in", "range", "start", "end", "loop", "count", "interpolate")

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
                        currentChar.isDigit() || currentChar == '-' -> {
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
                        currentChar == '/' -> {
                            currentState = State.DIVIDE
                        }
                        else -> {
                            throw IllegalArgumentException("Unknown character: $currentChar")
                        }
                    }
                }
                // double števila oblike 12.34, 34, -5.43
                State.NUMBER -> {
                    val start = pos
                    var isDouble = false

                    // handlanje negativnih števil
                    if (input[pos] == '-') {
                        pos++
                    }

                    // števke pred piko
                    while (pos < length && input[pos].isDigit()) {
                        pos++
                    }

                    // preverjanje decimalne pike
                    if (pos < length && input[pos] == '.') {
                        isDouble = true
                        pos++
                        // števke po piki
                        while (pos < length && input[pos].isDigit()) {
                            pos++
                        }
                    }

                    currentState = State.START
                    val number = input.substring(start, pos)

                    return if (isDouble) {
                        Token("double", number)
                    } else {
                        Token("int", number)
                    }
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
                State.DIVIDE -> {
                    pos++
                    currentState = State.START
                    return Token("divide", "/")
                }
                else -> {
                    throw IllegalStateException("Unexpected state: $currentState")
                }
            }
        }
        return null
    }
}