// PROGRAM ::= QUERY
//
// QUERY ::= PARK | DREVO | KLOP | KOŠ | RIBNIK | POT | DREVORED
//
// PARK ::= park IME { koordinate : { KOORDINATE } }
//
// KOORDINATE ::= KOORDINATA KOORDINATA`
//
// KOORDINATA ::= KOORDINATA KOORDINATA | ε
//
// KOORDINATA ::= ( DOUBLE , DOUBLE )
//
// DREVO ::= drevo { koordinata : { KOORDINATA } }
//
// KLOP ::= klop { koordinata : { KOORDINATA } }
//
// KOŠ ::= koš { koordinata : { KOORDINATA } }
//
// RIBNIK ::= ribnik { koordinate : { KOORDINATE } }
//
// POT ::= "pot" { TIP_POTI }
//
// TIP_POTI ::= LINE | BENT_LINE
//
// LINE ::= ( KOORDINATA, KOORDINATA )
//
// BENT_LINE ::= ( KOORDINATA, KOORDINATA, DOUBLE )
//
// DREVORED ::= drevored {
//                  start : { KOORDINATA },
//                  end : { KOORDINATA },
//                  count : int,
//                  loop : { LOOP }
//             }
//
// LOOP ::= for i in range (int) { DREVO_INTERPOLATED | LOOP}
//
// DREVO_INTERPOLATED ::= drevo {
//                          koordinata : {
//                              interpolate ( start, end, i/int )
//                          }
//                      }


class Parser(private val tokens: List<Token>) {
    private var pos = 0

    private fun currentToken(): Token? = if (pos < tokens.size) tokens[pos] else null
    private fun nextToken() {
        if (pos < tokens.size) pos++
    }

    private fun match(type: String): Boolean {
        return if (currentToken()?.type == type) {
            nextToken()
            true
        } else {
            false
        }
    }

    fun parse(): Boolean {
        return PROGRAM() && pos == tokens.size
    }

    private fun PROGRAM(): Boolean {
        return QUERY()
    }

    private fun QUERY(): Boolean {
        val startPos = pos
        return when (currentToken()?.value) {
            "park" -> PARK()
            "drevo" -> DREVO()
            "klop" -> KLOP()
            "koš" -> KOS()
            "ribnik" -> RIBNIK()
            "pot" -> POT()
            "drevored" -> DREVORED()
            else -> false
        }.also {
            if (!it) pos = startPos
        }
    }

    private fun PARK(): Boolean {
        return match("park") &&
                IME() &&
                match("lcurly") &&
                match("koordinate") &&
                match("colon") &&
                match("lcurly") &&
                KOORDINATE() &&
                match("rcurly") &&
                match("rcurly")
    }

    private fun IME(): Boolean {
        return match("variable")
    }

    private fun KOORDINATE(): Boolean {
            val startPos = pos
            if (!KOORDINATA()) return false

            // KOORDINATE' je lahko prazna (epsilon)
            while (KOORDINATA()) {}

            return true
    }

    private fun KOORDINATA(): Boolean {
        return match("lparen") &&
                match("double") &&
                match("comma") &&
                match("double") &&
                match("rparen")
    }

    private fun DREVO(): Boolean {
        return match("drevo") &&
                match("lcurly") &&
                match("koordinata") &&
                match("colon") &&
                match("lcurly") &&
                KOORDINATA() &&
                match("rcurly") &&
                match("rcurly")
    }

    private fun KLOP(): Boolean {
        return match("klop") &&
                match("lcurly") &&
                match("koordinata") &&
                match("colon") &&
                match("lcurly") &&
                KOORDINATA() &&
                match("rcurly") &&
                match("rcurly")
    }

    private fun KOS(): Boolean {
        return match("koš") &&
                match("lcurly") &&
                match("koordinata") &&
                match("colon") &&
                match("lcurly") &&
                KOORDINATA() &&
                match("rcurly") &&
                match("rcurly")
    }

    private fun RIBNIK(): Boolean {
        return match("ribnik") &&
                match("lcurly") &&
                match("koordinate") &&
                match("colon") &&
                match("lcurly") &&
                KOORDINATE() &&
                match("rcurly") &&
                match("rcurly")
    }

    private fun POT(): Boolean {
        return match("pot") &&
                match("lcurly") &&
                TIP_POTI() &&
                match("rcurly")
    }

    private fun TIP_POTI(): Boolean {
        val startPos = pos
        return (LINE() || BENT_LINE()).also {
            if (!it) pos = startPos
        }
    }

    private fun LINE(): Boolean {
        return match("lparen") &&
                KOORDINATA() &&
                match("comma") &&
                KOORDINATA() &&
                match("rparen")
    }

    private fun BENT_LINE(): Boolean {
        return match("lparen") &&
                KOORDINATA() &&
                match("comma") &&
                KOORDINATA() &&
                match("comma") &&
                match("double") &&
                match("rparen")
    }

    private fun DREVORED(): Boolean {
        return match("drevored") &&
                match("lcurly") &&
                match("start") && match("colon") && match("lcurly") && KOORDINATA() && match("rcurly") &&
                match("comma") &&
                match("end") && match("colon") && match("lcurly") && KOORDINATA() && match("rcurly") &&
                match("comma") &&
                match("count") && match("colon") && match("integer") &&
                match("comma") &&
                match("loop") && match("colon") && match("lcurly") &&
                LOOP() &&
                match("rcurly") &&
                match("rcurly")
    }

    private fun LOOP(): Boolean {
        val startPos = pos
        return (match("for") &&
                match("variable") &&
                match("in") &&
                match("range") &&
                match("lparen") &&
                (match("integer") || match("count")) &&  // Število ali referenca na count
                match("rparen") &&
                match("lcurly") &&
                // Gnezdene zanke ali drevesa
                LOOP_BODY() &&
                match("rcurly")).also { if (!it) pos = startPos }
    }

    private fun LOOP_BODY(): Boolean {
        while (true) {
            val savedPos = pos
            when {
                // Rekurzivno kliče LOOP za gnezdene zanke
                currentToken()?.value == "for" -> {
                    if (!LOOP()) {
                        pos = savedPos
                        break
                    }
                }
                // Interpolirano drevo
                currentToken()?.value == "drevo" -> {
                    if (!DREVO_INTERPOLATED()) {
                        pos = savedPos
                        break
                    }
                }
                else -> break
            }
        }
        return true
    }

    private fun DREVO_INTERPOLATED(): Boolean {
        return match("drevo") &&
                match("lcurly") &&
                match("koordinata") && match("colon") && match("lcurly") &&
                match("interpolate") &&
                match("lparen") &&
                match("start") && match("comma") &&
                match("end") && match("comma") &&
                match("variable") &&
                match("divide") &&
                match("count") &&
                match("rparen") &&
                match("rcurly") &&
                match("rcurly")
    }
}