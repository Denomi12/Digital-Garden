import java.io.File

//  PROGRAM ::= QUERY
//  QUERY ::= PARK
//  PARK ::= park ime { boundary: { POLYGON } ELEMENTS_OPT }
//
//  ELEMENTS ::= ELEMENT ELEMENTS'
//  ELEMENTS' ::= ELEMENT ELEMENTS' | ε
//  ELEMENT ::= DREVO | KLOP | KOŠ | SIMPLE_LAKE | POT | VARIABLE_DECLARATION | IF_STAVEK | VALIDATION
//
//  VARIABLE_DECLARATION ::= var ID = VALUE
//  ID ::= [a-zA-Z_][a-zA-Z0-9_]*
//
//  VALUE ::= EXPR_VALUE | POLYGON_VALUE | OBJECT_VALUE
//  EXPR_VALUE ::= EXPR | KOORDINATA
//  POLYGON_VALUE ::= polygon { POLYGON }
//  OBJECT_VALUE ::= DREVO | KLOP | KOŠ | SIMPLE_LAKE
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
//
//  VALIDATION ::= validate (ID in ID)                      // prvi ID je drevo, klop, kos; drugi ID je ellip
//


class Parser(
    private val tokens: MutableList<Pair<String, String>> = mutableListOf()
) {
    private var tokenCounter = 0
    private var currentToken: Pair<String, String>? = null
    private val mapOfValues = mutableMapOf<String, Any>()
    private var parkBoundary: List<Koordinate>? = null
    private val mapOfElements = mutableMapOf<String, Any>()
    private var parkName: String = "Park"

    private fun getNextToken(): Pair<String, String>? {
        return if (tokenCounter < tokens.size) {
            tokens[tokenCounter++]
        } else {
            null
        }
    }

    fun toGeoJson(): String {

        var stringifiedElements: MutableList<String> = mutableListOf()

        val parkBoundary = Park(boundary = parkBoundary!!, parkName).toGeoJson()
        stringifiedElements.add(parkBoundary)
        mapOfElements.forEach { (key, value) ->
            if (value is ToGeoJson) {
                stringifiedElements.add(value.toGeoJson())
            }
            else if (value is List<*>) {
               for (element in value){
                   if(element is ToGeoJson){
                       stringifiedElements.add(element.toGeoJson())
                   }
               }
            }
            else {
                println(value)
            }
        }

        val features = stringifiedElements.joinToString(",\n") { it.trimIndent() }
        val geojson = """
{
  "type": "FeatureCollection",
  "features": [${features}]
}
"""
        File("geojsonOutput.txt").writeText(geojson)

        return geojson

    }

    fun parse() {
        currentToken = getNextToken()
//        if (procedureProgram() && currentToken == null) {
//            println("ACCEPT")
//        } else {
//            println("REJECT")
//        }
        val result = procedureProgram()
        if(currentToken != null){
            println("Napaka!")
        }else {
            println("------ Elementi v mapOfElements ------")
            for ((key, value) in mapOfElements) {
                println("$key: $value")
            }
            println("--------------------------------------")
        }
        println("\n------ Spremenljivke v mapOfValues ------")
        mapOfValues.forEach { (key, value) ->
            println("$key: $value")
        }
        println("--------------------------------------")

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
                parkName = currentToken!!.second
                currentToken = getNextToken()
                if (currentToken?.first == "lbrace") {
                    currentToken = getNextToken()
                    if (currentToken?.first == "boundary") {
                        currentToken = getNextToken()
                        if (currentToken?.first == "colon") {
                            currentToken = getNextToken()
                            if (currentToken?.first == "lbrace") {
                                currentToken = getNextToken()

                                val polygonCoords = procedurePolygon()

                                parkBoundary = polygonCoords

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
        return false
    }


    private fun procedureElements(): Boolean {
        return procedureElements_()
    }

    private fun procedureElements_(): Boolean {
        val element = procedureElement() ?: return true

        if(currentToken == null){
            error("napaka")
        }
        if (!(element == Unit || element == true)) {
            val key = "element_${mapOfElements.size}"
            mapOfElements[key] = element
        }

        return procedureElements_()
    }


    private fun procedureElement(): Any? {
        return when (currentToken?.first) {
            "drevo" -> procedureDrevo()
            "klop" -> procedureKlop()
            "kos" -> procedureKos()
            "ellip" -> procedureSimpleLake()
            "pot" -> procedurePot()
            "var" -> {
                procedureVariableDeclaration()
                Unit
            }
            "if" -> procedureIfStavek()
            "validate" -> {
                procedureValidation()
                Unit
            }
            else -> null
        }
    }


    private fun procedureVariableDeclaration(): Any? {
        if (currentToken?.first == "var") {
            currentToken = getNextToken()
            if (currentToken?.first == "id") {
                var name = currentToken!!.second
                currentToken = getNextToken()
                if (currentToken?.first == "assign") {
                    currentToken = getNextToken()
                    val value = procedureValue() ?: return false

                    mapOfValues[name] = value
                    mapOfValues[name] = when (value) {
                        is Double -> value
                        is Koordinate -> value
                        is List<*> -> value
                        is Drevo -> value
                        is Klop -> value
                        is Kos -> value
                        is Ellip -> value
                        else -> error("Unsupported value type")
                    }

                    when (value) {
                        is Drevo, is Klop, is Kos, is Ellip -> {
                            val key = "var_$name"
                            mapOfElements[key] = value
                        }
                    }
                    return true
                }
            }
        }
        return false
    }

    private fun procedureValue(): Any? {
        if (currentToken?.first == "polygon") {
            return procedurePolygonValue()
        }
        else if (currentToken?.first in listOf("drevo", "klop", "kos", "ellip")) {
            return procedureObjectValue()
        }
        else {
            return procedureExprValue()
        }
    }

    private fun procedureExprValue(): Any {
        return try {
            procedureExpr()
        } catch (e: Exception) {
            procedureKoordinata()
        }
    }

    private fun procedureObjectValue(): Any? {
        return when (currentToken?.first) {
            "drevo" -> procedureDrevo()
            "klop" -> procedureKlop()
            "kos" -> procedureKos()
            "ellip" -> procedureSimpleLake()
            else -> null
        }
    }


    private fun procedurePolygonValue(): List<Koordinate>? {
        if (currentToken?.first == "polygon") {
            currentToken = getNextToken()
            if (currentToken?.first == "lbrace") {
                currentToken = getNextToken()

                val koordinate = procedurePolygon()

                if (currentToken?.first == "rbrace") {
                    currentToken = getNextToken()

                    return koordinate

                }
            }
        }
        return null
    }

    private fun procedureDrevo(): Drevo {
        if (currentToken?.first == "drevo") {
            currentToken = getNextToken()
            if (currentToken?.first == "lbrace") {
                currentToken = getNextToken()
                val drevoKoordinate = procedureKoordinata()
                val drevo = Drevo(drevoKoordinate.x, drevoKoordinate.y)
                if (currentToken?.first == "rbrace") {
                    currentToken = getNextToken()
                    return drevo
                }
            }
        }
        error("Expected 'drevo { (x, y) }'")
    }

    private fun procedureKlop(): Klop {
        if (currentToken?.first == "klop") {
            currentToken = getNextToken()
            if (currentToken?.first == "lbrace") {
                currentToken = getNextToken()
                val klopKoordinate = procedureKoordinata()
                val klop = Klop(klopKoordinate.x, klopKoordinate.y)
                if (currentToken?.first == "rbrace") {
                    currentToken = getNextToken()
                    return klop
                }
            }
        }
        error("Expected 'klop { (x, y) }'")
    }

    private fun procedureKos(): Kos {
        if (currentToken?.first == "kos") {
            currentToken = getNextToken()
            if (currentToken?.first == "lbrace") {
                currentToken = getNextToken()
                val kosKoordinate = procedureKoordinata()
                val kos = Kos(kosKoordinate.x, kosKoordinate.y)
                if (currentToken?.first == "rbrace") {
                    currentToken = getNextToken()
                    return kos
                }

            }
        }
        error("Expected 'kos { (x, y) }'")
    }


    private fun procedurePot(): List<Pot> {
        val poti = mutableListOf<Pot>()

        if (currentToken?.first != "pot") error("Expected keyword 'pot'")
        currentToken = getNextToken()

        if (currentToken?.first != "lbrace") error("Expected '{' after keyword 'pot'")
        currentToken = getNextToken()

        while (true) {
            val potElement = procedureTipPoti()
            if (potElement != null) {
                poti.add(potElement)
            } else {
                break
            }
        }

        if (currentToken?.first != "rbrace") error("Missing closing brace '}' in 'pot' block")
        currentToken = getNextToken()

        return poti
    }

    private fun procedureTipPoti(): Pot? {
        return when (currentToken?.first) {
            "line" -> procedureLine()
            "bent_line" -> procedureBentLine()
            else -> null
        }
    }


    private fun procedureLine(): PotLine {
        if (currentToken?.first == "line") {
            currentToken = getNextToken()
            if (currentToken?.first == "lparen") {
                currentToken = getNextToken()

                val firstCoordinate = procedureKoordinata()


                if (currentToken?.first == "comma") {
                    currentToken = getNextToken()

                    val secondCoordinate = procedureKoordinata()

                    if (currentToken?.first == "rparen") {
                        currentToken = getNextToken()
                        return PotLine(Line(firstCoordinate, secondCoordinate))
                    }
                }
            }
        }
        error("Expected 'line(start, end)' structure")
    }

    private fun procedureBentLine(): PotBentLine {
        if (currentToken?.first == "bent_line") {
            currentToken = getNextToken()
            if (currentToken?.first == "lparen") {
                currentToken = getNextToken()

                val firstCoordinate = procedureKoordinata()

                if (currentToken?.first == "comma") {
                    currentToken = getNextToken()

                    val secondCoordinate = procedureKoordinata()

                    if (currentToken?.first == "comma") {
                        currentToken = getNextToken()

                        val angle = procedureAngle()

                        if (currentToken?.first == "rparen") {
                            currentToken = getNextToken()
                            return PotBentLine(BentLine(firstCoordinate, secondCoordinate, angle))
                        }

                    }

                }
            }
        }
        error("Expected 'bent_line(start, end, angle)' structure")
    }

    private fun procedureSimpleLake(): Ellip {
        if (currentToken?.first == "ellip") {
            currentToken = getNextToken()
            if (currentToken?.first == "lparen") {
                currentToken = getNextToken()

                val simpleLakeCoordinates = procedureKoordinata()

                if (currentToken?.first == "comma") {
                    currentToken = getNextToken()

                    val firstAxis = procedureAxis()

                    if (currentToken?.first == "comma") {
                        currentToken = getNextToken()

                        val secondAxis = procedureAxis()

                        if (currentToken?.first == "rparen") {
                            currentToken = getNextToken()
                            return Ellip(simpleLakeCoordinates, firstAxis, secondAxis)
                        }
                    }
                }
            }
        }
        error("Expected 'ellip { ((x, y), a, b )}'")
    }

    private fun procedurePolygon(): List<Koordinate>? {
        return try {
            val first = procedureKoordinata()
            val second = procedureKoordinata()
            val third = procedureKoordinata()
            val others = procedureKoordinataOpt()
            listOf(first, second, third) + others
        } catch (e: Exception) {
            error("Polygon must have three or more coordinates")
        }
    }


    private fun procedureKoordinataOpt(): List<Koordinate> {
        val koordinate = mutableListOf<Koordinate>()

        while (currentToken?.first == "lparen" || currentToken?.first == "id") {
            koordinate.add(procedureKoordinata())
        }

        return koordinate
    }


//    private fun procedureKoordinataOpt(): Boolean {
//        // Only try to parse another coordinate if one exists
//        if (currentToken?.first == "lparen" || currentToken?.first == "id") {
//            return procedureKoordinata() && procedureKoordinataOpt()
//        }
//        return true
//    }

    private fun procedureKoordinata(): Koordinate {
        if (currentToken?.first == "lparen") {
            currentToken = getNextToken()
            val firstCoordinate = procedureExpr()
            if (currentToken?.first == "comma") {
                currentToken = getNextToken()
                val secondCoordinate = procedureExpr()
                if (currentToken?.first == "rparen") {
                    currentToken = getNextToken()
                    return Koordinate(firstCoordinate, secondCoordinate)
                }
            }
        } else if (currentToken?.first == "id") {
            val variable = currentToken?.second ?: error("Missing variable name")
            currentToken = getNextToken()

            val value = mapOfValues[variable] ?: error("Variable $variable not found")

            if (value is Koordinate) {
                return value
            } else {
                error("Variable $variable is not of type Koordinate")
            }
        }

        error("Koordinate is not the right structure")
    }

    private fun procedureAngle(): Double {
        return procedureExpr()
    }

    private fun procedureAxis(): Double {
        return procedureExpr()
    }

    private fun procedureIfStavek(): Boolean {
        if (currentToken?.first == "if") {
            currentToken = getNextToken()

            val ifCheck = procedureCond()

                if (!ifCheck) { //else block
                    skipBlock()
                    return procedureElse()
                } else {
                    if (currentToken?.first == "lbrace") {
                        currentToken = getNextToken()
                    if (procedureElements()) {
                        if (currentToken?.first == "rbrace") {

                            currentToken = getNextToken()
                            while(currentToken?.first == "else"){
                                currentToken = getNextToken()
                                skipBlock()
                            }
                            return true

                        }
                    }
                }

            }

        }
        return false
    }

    private fun skipBlock() {
        var braceCount = 0

        if (currentToken?.first == "lbrace") {
            braceCount++
            currentToken = getNextToken()
        }

        while (currentToken != null && braceCount > 0) {
            if (currentToken?.first == "lbrace") {
                braceCount++
            } else if (currentToken?.first == "rbrace") {
                braceCount--
            }

            currentToken = getNextToken()
        }
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
        val vhod = procedureExpr()
        return procedureComp(vhod)
    }

    private fun procedureComp(vhod: Double): Boolean {
//        currentToken = getNextToken()
        return when (currentToken?.first) {
            "gt" -> {
                currentToken = getNextToken()
                return vhod > procedureExpr()
            }

            "lt" -> {
                currentToken = getNextToken()
                return vhod < procedureExpr()
            }

            "ge" -> {
                currentToken = getNextToken()
                return vhod >= procedureExpr()
            }

            "le" -> {
                currentToken = getNextToken()
                return vhod <= procedureExpr()
            }

            "eq" -> {
                currentToken = getNextToken()
                return vhod == procedureExpr()
            }

            "ne" -> {
                currentToken = getNextToken()
                return vhod != procedureExpr()
            }

            else -> false
        }
    }

    private fun procedureValidation(): Boolean {
        if (currentToken?.first == "validate") {
            currentToken = getNextToken()
            if (currentToken?.first == "lparen") {
                currentToken = getNextToken()
                if (currentToken?.first == "id") {
                    val elementName = currentToken!!.second
                    println("\nValidating element: $elementName")
                    currentToken = getNextToken()
                    if (currentToken?.first == "in") {
                        currentToken = getNextToken()
                        if (currentToken?.first == "id") {
                            val lakeName = currentToken!!.second
                            currentToken = getNextToken()
                            if (currentToken?.first == "rparen") {
                                currentToken = getNextToken()

                                // Semantic check
                                val element = mapOfValues[elementName]
                                val lake = mapOfValues[lakeName]

                                if (element == null) {
                                    val errorMsg = "ERROR: Element $elementName not found"
                                    println(errorMsg)
                                    error(errorMsg)
                                }
                                if (lake == null) {
                                    val errorMsg = "ERROR: Lake $lakeName not found"
                                    println(errorMsg)
                                    error(errorMsg)
                                }
                                if (lake !is Ellip) {
                                    val errorMsg = "ERROR: $lakeName is not a lake (ellip)"
                                    println(errorMsg)
                                    error(errorMsg)
                                }

                                // Check if element is inside lake
                                val isInside = when (element) {
                                    is Drevo -> {
                                        isPointInsideEllipse(element.x, element.y, lake.center, lake.a, lake.b)
                                    }
                                    is Klop -> {
                                        isPointInsideEllipse(element.x, element.y, lake.center, lake.a, lake.b)
                                    }
                                    is Kos -> {
                                        isPointInsideEllipse(element.x, element.y, lake.center, lake.a, lake.b)
                                    }
                                    else -> {
                                        val errorMsg = "ERROR: $elementName is not a valid element (must be drevo, klop, or kos)"
                                        println(errorMsg)
                                        error(errorMsg)
                                    }
                                }

                                if (isInside) {
                                    println("$elementName is inside $lakeName\n")
                                } else {
                                    println("$elementName is not inside $lakeName\n")
                                }

                                return isInside
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    private fun isPointInsideEllipse(x: Double, y: Double, center: Koordinate, a: Double, b: Double): Boolean {
        val normalizedX = x - center.x
        val normalizedY = y - center.y
        return (normalizedX * normalizedX) / (a * a) + (normalizedY * normalizedY) / (b * b) <= 1.0
    }

    private fun procedureExpr(): Double {
        return procedureAdditive()
    }

    private fun procedureAdditive(): Double {
        val vhodna = procedureMultiplicative()
        return procedureAdditive_(vhodna)
    }

    private fun procedureAdditive_(vhodna: Double): Double {
        var temp = vhodna
        if (currentToken?.first == "plus") {
            currentToken = getNextToken()
            temp = vhodna + procedureMultiplicative()
            return procedureAdditive_(temp)
        } else if (currentToken?.first == "minus") {
            currentToken = getNextToken()
            temp = vhodna - procedureMultiplicative()
            return procedureAdditive_(temp)
        }
        return temp //epsilon
    }

    private fun procedureMultiplicative(): Double {
        val vhodna = procedureUnary()
        return procedureMultiplicative_(vhodna)
    }

    private fun procedureMultiplicative_(vhodna: Double): Double {
        var temp = vhodna
        if (currentToken?.first == "times") {
            currentToken = getNextToken()
            temp = vhodna * procedureUnary()
            return procedureMultiplicative_(temp)
        } else if (currentToken?.first == "divide") {
            currentToken = getNextToken()
            temp = vhodna / procedureUnary()
            return procedureMultiplicative_(temp)
        }
        return temp //epsilon
    }

    private fun procedureUnary(): Double {
        if (currentToken?.first == "minus") {
            currentToken = getNextToken()
            return -procedurePrimary()
        }
        return procedurePrimary()
    }

    private fun procedurePrimary(): Double {
        return when (currentToken?.first) {
            "double" -> {
                val value = currentToken?.second?.toDoubleOrNull() ?: error("Invalid double value")
                currentToken = getNextToken()
                value
            }

            "id" -> {
                val variable = currentToken?.second ?: error("Variable name missing")
                currentToken = getNextToken()
                val value = mapOfValues[variable] ?: error("Variable $variable not found")

                if (value is Double) {
                    value
                } else {
                    error("Variable $variable is not a numeric value")
                }
            }

            "lparen" -> {
                currentToken = getNextToken()
                val value = procedureExpr()
                if (currentToken?.first == "rparen") {
                    currentToken = getNextToken()
                    value
                } else {
                    error("Missing rparen")
                }
            }

            else -> error("Unknown token")
        }
    }

}

