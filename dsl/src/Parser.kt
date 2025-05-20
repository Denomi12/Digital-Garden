//  PROGRAM ::= QUERY
//  QUERY ::= PARK
//  PARK ::= park ime { boundary: { POLYGON } ELEMENTS_OPT }
//
//  ELEMENTS ::= ELEMENT ELEMENTS'
//  ELEMENTS' ::= ELEMENT ELEMENTS' | ε
//  ELEMENT ::= DREVO | KLOP | KOŠ | SIMPLE_LAKE | POT | VARIABLE_DECLARATION | IF_STAVEK
//
//
//  VARIABLE_DECLARATION ::= var ID = VALUE
//  ID ::= [a-zA-Z_][a-zA-Z0-9_]*
//
//  VALUE ::= EXPR_VALUE | POLYGON_VALUE
//  EXPR_VALUE ::= EXPR | KOORDINATA
//  POLYGON_VALUE ::= polygon { POLYGON }
//
//  DREVO ::= drevo { KOORDINATA }
//  KLOP ::= klop { KOORDINATA }
//  KOŠ ::= koš { KOORDINATA }
//  POT ::= pot { TIP_POTI }
//  TIP_POTI ::= LINE | BENT_LINE
//  LINE ::= line ( KOORDINATA , KOORDINATA )
//  BENT_LINE ::= bent_line ( KOORDINATA , KOORDINATA , ANGLE )
//  SIMPLE_LAKE ::= ellip ( KOORDINATA , AXIS , AXIS )
//
//  POLYGON ::= KOORDINATA KOORDINATA KOORDINATA KOORDINATE_OPT
//  KOORDINATE_OPT ::= KOORDINATA KOORDINATE_OPT | ε
//  KOORDINATA ::= ( EXPR , EXPR ) | ID
//  ANGLE ::= EXPR
//  AXIS ::= EXPR
//
//  EXPR ::= ADDITIVE
//  ADDITIVE ::= MULTIPLICATIVE ADDITIVE'
//  ADDITIVE' ::= plus MULTIPLICATIVE ADDITIVE' | minus MULTIPLICATIVE ADDITIVE' | ε
//  MULTIPLICATIVE ::= UNARY MULTIPLICATIVE'
//  MULTIPLICATIVE' ::= times UNARY MULTIPLICATIVE' | divide UNARY MULTIPLICATIVE' | ε
//  UNARY ::= plus PRIMARY | minus PRIMARY | PRIMARY
//  PRIMARY ::= double | ID | lparen EXPR rparen
//
//  IF_STAVEK ::= if COND { ELEMENTS } ELSE
//  ELSE ::= else { ELEMENTS } | ε
//  COND ::= EXPR COMP EXPR
//  COMP ::= > | < | >= | <= | == | !=


class Parser(
    private val tokens: MutableList<Pair<String, String>> = mutableListOf()
) {
    private var tokenCounter = 0
    private var currentToken: Pair<String, String>? = null

    private fun getNextToken(): Pair<String, String>? {
        return if (tokenCounter < tokens.size) {
            tokens[tokenCounter++]
        } else {
            null
        }
    }

    fun parse() {
        currentToken = getNextToken()
        if (procedureProgram() && currentToken == null) {
            println("ACCEPT")
        } else {
            println("REJECT")
        }
    }

    private fun procedureProgram(): Boolean {
        return procedureQuery()
    }

    private fun procedureQuery(): Boolean {
        return procedurePark()
    }

    private fun procedurePark(): Boolean {
        if (currentToken?.first == "park") {
            currentToken = getNextToken()
            if (currentToken?.first == "id") {
                currentToken = getNextToken()
                if (currentToken?.first == "lbrace") {
                    currentToken = getNextToken()
                    if (currentToken?.first == "boundary") {
                        currentToken = getNextToken()
                        if (currentToken?.first == "colon") {
                            currentToken = getNextToken()
                            if (currentToken?.first == "lbrace") {
                                currentToken = getNextToken()
                                if (procedurePolygon()) {
                                    if (currentToken?.first == "rbrace") {
                                        currentToken = getNextToken()
                                        if (procedureElements()) {
                                            if (currentToken?.first == "rbrace") {
                                                currentToken = getNextToken()
                                                return true
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false
    }


    private fun procedureElements(): Boolean {
        if (!procedureElement()) return false
        return procedureElements_()
    }

    private fun procedureElements_(): Boolean {
        if (procedureElement()) {
            return procedureElements_()
        }
        return true
    }

    private fun procedureElement(): Boolean {
        return when (currentToken?.first) {
            "drevo" -> procedureDrevo()
            "klop" -> procedureKlop()
            "kos" -> procedureKos()
            "ellip" -> procedureSimpleLake()
            "pot" -> procedurePot()
            "var" -> procedureVariableDeclaration()
            "if" -> procedureIfStavek()
            else -> false
        }
    }

    private fun  procedureVariableDeclaration(): Boolean {
        if (currentToken?.first == "var") {
            currentToken = getNextToken()
            if (currentToken?.first == "id") {
                currentToken = getNextToken()
                if (currentToken?.first == "assign") {
                    currentToken = getNextToken()
                    return procedureValue()
                }
            }
        }
        return false
    }

    private fun procedureValue(): Boolean {
        return procedureExprValue() || procedurePolygonValue()
    }

    private fun procedureExprValue(): Boolean {
        return procedureExpr() || procedureKoordinata()
    }

    private fun procedurePolygonValue(): Boolean {
        if (currentToken?.first == "polygon") {
            currentToken = getNextToken()
            if (currentToken?.first == "lbrace") {
                currentToken = getNextToken()
                if (procedurePolygon()) {
                    if (currentToken?.first == "rbrace") {
                        currentToken = getNextToken()
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun procedureDrevo(): Boolean {
        if (currentToken?.first == "drevo") {
            currentToken = getNextToken()
            if (currentToken?.first == "lbrace") {
                currentToken = getNextToken()
                if (procedureKoordinata()) {
                    if (currentToken?.first == "rbrace") {
                        currentToken = getNextToken()
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun procedureKlop(): Boolean {
        if (currentToken?.first == "klop") {
            currentToken = getNextToken()
            if (currentToken?.first == "lbrace") {
                currentToken = getNextToken()
                if (procedureKoordinata()) {
                    if (currentToken?.first == "rbrace") {
                        currentToken = getNextToken()
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun procedureKos(): Boolean {
        if (currentToken?.first == "kos") {
            currentToken = getNextToken()
            if (currentToken?.first == "lbrace") {
                currentToken = getNextToken()
                if (procedureKoordinata()) {
                    if (currentToken?.first == "rbrace") {
                        currentToken = getNextToken()
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun procedurePot(): Boolean {
        if (currentToken?.first == "pot") {
            currentToken = getNextToken()
            if (currentToken?.first == "lbrace") {
                currentToken = getNextToken()
                if (procedureTipPoti()) {
                    if (currentToken?.first == "rbrace") {
                        currentToken = getNextToken()
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun procedureTipPoti(): Boolean {
        return procedureLine() || procedureBentLine()
    }

    private fun procedureLine(): Boolean {
        if (currentToken?.first == "line") {
            currentToken = getNextToken()
            if (currentToken?.first == "lparen") {
                currentToken = getNextToken()
                if (procedureKoordinata()) {
                    if (currentToken?.first == "comma") {
                        currentToken = getNextToken()
                        if (procedureKoordinata()) {
                            if (currentToken?.first == "rparen") {
                                currentToken = getNextToken()
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    private fun procedureBentLine(): Boolean {
        if (currentToken?.first == "bent_line") {
            currentToken = getNextToken()
            if (currentToken?.first == "lparen") {
                currentToken = getNextToken()
                if (procedureKoordinata()) {
                    if (currentToken?.first == "comma") {
                        currentToken = getNextToken()
                        if (procedureKoordinata()) {
                            if (currentToken?.first == "comma") {
                                currentToken = getNextToken()
                                if (procedureAngle()) {
                                    if (currentToken?.first == "rparen") {
                                        currentToken = getNextToken()
                                        return true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    private fun procedureSimpleLake(): Boolean {
        if (currentToken?.first == "ellip") {
            currentToken = getNextToken()
            if (currentToken?.first == "lparen") {
                currentToken = getNextToken()
                if (procedureKoordinata()) {
                    if (currentToken?.first == "comma") {
                        currentToken = getNextToken()
                        if (procedureAxis()) {
                            if (currentToken?.first == "comma") {
                                currentToken = getNextToken()
                                if (procedureAxis()) {
                                    if (currentToken?.first == "rparen") {
                                        currentToken = getNextToken()
                                        return true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    private fun procedurePolygon(): Boolean {
        if (procedureKoordinata()) {
            if (procedureKoordinata()) {
                if (procedureKoordinata()) {
                    return procedureKoordinataOpt()
                }
            }
        }
        return false
    }


    private fun procedureKoordinataOpt(): Boolean {
        if (procedureKoordinata()) {
            return procedureKoordinataOpt()
        }
        return true
    }

//    private fun procedureKoordinataOpt(): Boolean {
//        // Only try to parse another coordinate if one exists
//        if (currentToken?.first == "lparen" || currentToken?.first == "id") {
//            return procedureKoordinata() && procedureKoordinataOpt()
//        }
//        return true
//    }

    private fun procedureKoordinata(): Boolean {
        if (currentToken?.first == "lparen") {
            currentToken = getNextToken()
            if (procedureExpr()) {
                if (currentToken?.first == "comma") {
                    currentToken = getNextToken()
                    if (procedureExpr()) {
                        if (currentToken?.first == "rparen") {
                            currentToken = getNextToken()
                            return true
                        }
                    }
                }
            }
        } else if (currentToken?.first == "id") {
            currentToken = getNextToken()
            return true
        }
        return false
    }

    private fun procedureAngle(): Boolean {
        return procedureExpr()
    }

    private fun procedureAxis(): Boolean {
        return procedureExpr()
    }

    private fun procedureIfStavek(): Boolean {
        if (currentToken?.first == "if") {
            currentToken = getNextToken()
            if (procedureCond()) {
                if (currentToken?.first == "lbrace") {
                    currentToken = getNextToken()
                    if (procedureElements()) {
                        if (currentToken?.first == "rbrace") {
                            currentToken = getNextToken()
                            return procedureElse() // else stavek je opcijski
                        }
                    }
                }
            }
        }
        return false
    }


    private fun procedureElse(): Boolean {
        if (currentToken?.first == "else") {
            currentToken = getNextToken()
            if (currentToken?.first == "lbrace") {
                currentToken = getNextToken()
                if (procedureElements()) {
                    if (currentToken?.first == "rbrace") {
                        currentToken = getNextToken()
                        return true
                    }
                }
            }
            return false
        }
        return true
    }

    private fun procedureCond(): Boolean {
        println("test")
        if (procedureExpr()) {
            if (procedureComp()) {
                return procedureExpr()
            }
        }
        return false
    }

    private fun procedureComp(): Boolean {
        return when (currentToken?.first) {
            "gt" -> {
                currentToken = getNextToken()
                true
            }
            "lt" -> {
                currentToken = getNextToken()
                true
            }
            "ge" -> {
                currentToken = getNextToken()
                true
            }
            "le" -> {
                currentToken = getNextToken()
                true
            }
            "eq" -> {
                currentToken = getNextToken()
                true
            }
            "ne" -> {
                currentToken = getNextToken()
                true
            }
            else -> false
        }
    }

    private fun procedureExpr(): Boolean {
        return procedureAdditive()
    }

    private fun procedureAdditive(): Boolean {
        return procedureMultiplicative() && procedureAdditive_()
    }

    private fun procedureAdditive_(): Boolean {
        if (currentToken?.first == "plus" || currentToken?.first == "minus") {
            currentToken = getNextToken()
            return procedureMultiplicative() && procedureAdditive_()
        }
        return true
    }

    private fun procedureMultiplicative(): Boolean {
        return procedureUnary() && procedureMultiplicative_()
    }

    private fun procedureMultiplicative_(): Boolean {
        if (currentToken?.first == "times" || currentToken?.first == "divide") {
            currentToken = getNextToken()
            return procedureUnary() && procedureMultiplicative_()
        }
        return true
    }

    private fun procedureUnary(): Boolean {
        if (currentToken?.first == "plus" || currentToken?.first == "minus") {
            currentToken = getNextToken()
        }
        return procedurePrimary()
    }

    private fun procedurePrimary(): Boolean {
        return when (currentToken?.first) {
            "double" -> {
                currentToken = getNextToken()
                true
            }
            "id" -> {
                currentToken = getNextToken()
                true
            }
            "lparen" -> {
                currentToken = getNextToken()
                if (procedureExpr()) {
                    if (currentToken?.first == "rparen") {
                        currentToken = getNextToken()
                        true
                    } else false
                } else false
            }
            else -> false
        }
    }
}

