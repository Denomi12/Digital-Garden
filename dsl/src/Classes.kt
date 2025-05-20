data class Kos(val x: Double, val y: Double)

data class Drevo(val x: Double, val y: Double)

data class Klop(val x: Double, val y: Double)

data class Ellip(val center: Koordinate, val a: Double, val b: Double)

data class Koordinate(val x: Double, val y: Double)

data class Polygon(val points: List<Koordinate>)


sealed class Pot

data class PotLine(val line: Line) : Pot()
data class PotBentLine(val bentLine: BentLine) : Pot()

data class Line(val start: Koordinate, val end: Koordinate)

data class BentLine(val start: Koordinate, val end: Koordinate, val angle: Double)
