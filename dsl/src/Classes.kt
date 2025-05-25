import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

interface ToGeoJson {
    fun toGeoJson(): String
}

data class Park(val boundary: List<Koordinate>, val parkName: String = "Park"): ToGeoJson {
    override fun toGeoJson(): String {
        val coordinates = (boundary + boundary.first()).joinToString(",\n") {
            koord -> String.format(Locale.US, "\t\t\t\t[%.6f, %.6f]", koord.y, koord.x)
        }
        return """
{
    "type": "Feature",
    "properties": {
        "ime":"$parkName",
        "stroke": "#056d03",
        "stroke-width": 2,
        "stroke-opacity": 1,
        "fill": "#34fe0b",
        "fill-opacity": 0.2

    },
    "geometry": {
        "type": "Polygon",
        "coordinates": [
            [
$coordinates
            ]
        ]
    }
}
"""
    }

}

data class Kos(val x: Double, val y: Double): ToGeoJson {
    override fun toGeoJson(): String {
        return """
    {
        "type": "Feature",
        "properties": {
            "ime": "Koš",
            "marker-color": "#454545",
            "marker-size": "medium",
            "marker-symbol": "circle-stroked"
        },
        "geometry": {
            "coordinates": [
                $y,
                $x
            ],
            "type": "Point"
        }
    }
"""

    }
}

data class Drevo(val x: Double, val y: Double): ToGeoJson {
    override fun toGeoJson(): String {
        return """
    {
        "type": "Feature",
        "properties": {
            "ime": "Drevo",
            "marker-color": "#047116",
            "marker-size": "medium",
            "marker-symbol": "circle"
        },
        "geometry": {
            "coordinates": [
                $y,
                $x
            ],
            "type": "Point"
        }
    }
"""

    }
}

data class Klop(val x: Double, val y: Double): ToGeoJson {
    override fun toGeoJson(): String {
        return """
    {
        "type": "Feature",
        "properties": {
            "ime": "Klop",
            "marker-color": "#cc9705",
            "marker-size": "medium",
            "marker-symbol": "circle"
        },
        "geometry": {
            "coordinates": [
                $y,
                $x
            ],
            "type": "Point"
        }
    }
"""
    }

}

data class Ellip(val center: Koordinate, val a: Double, val b: Double) : ToGeoJson {
    override fun toGeoJson(): String {
        val numPoints = 64
        val points = (0 until numPoints).map { i ->
            val angle = 2 * Math.PI * i / numPoints
            val x = center.x + a * cos(angle)
            val y = center.y + b * sin(angle)
            String.format(Locale.US, "\t\t\t\t[%.6f, %.6f]", y, x) // note indentation
        }

        val coordinates = (points + points.first()).joinToString(",\n")

        return """
{
    "type": "Feature",
    "properties": {
        "ime":"Ribnik",
        "stroke": "#021783",
        "stroke-width": 2,
        "stroke-opacity": 1,
        "fill": "#24b3c6",
        "fill-opacity": 0.5
    },
    "geometry": {
        "type": "Polygon",
        "coordinates": [
            [
$coordinates
            ]
        ]
    }
}
"""
    }
}

data class Koordinate(val x: Double, val y: Double): ToGeoJson {
    override fun toGeoJson(): String {
        return "[${y}, ${x}]"
    }

}

data class Polygon(val points: List<Koordinate>)

sealed class Pot: ToGeoJson

data class PotLine(val line: Line) : Pot() {
    override fun toGeoJson(): String {
        return """
    {
        "type": "Feature",
        "properties": {
            "ime": "Pot",
            "stroke": "#000000",
            "stroke-width": 4,
            "stroke-opacity": 1
        },
        "geometry": {
            "coordinates": [${line.toGeoJson()}
            ],
            "type": "LineString"
        }
    }
"""
    }
}

data class PotBentLine(val bentLine: BentLine) : Pot() {
    override fun toGeoJson(): String {
        return bentLine.toGeoJson()
    }
}

data class Line(val start: Koordinate, val end: Koordinate): ToGeoJson {
    override fun toGeoJson(): String {
        return """
               ${start.toGeoJson()},
               ${end.toGeoJson()}"""
    }

}

data class BentLine(val start: Koordinate, val end: Koordinate, val angle: Double) : ToGeoJson {
    override fun toGeoJson(): String {
        val points = computeArcPoints(start, end, angle, 32)

        val coordinates = points.joinToString(",\n") {
            String.format(Locale.US, "\t\t\t\t[%.6f, %.6f]", it.y, it.x)
        }

        return """
{
    "type": "Feature",
    "properties": {
        "ime": "Kriva pot",
        "stroke": "#000000",
        "stroke-width": 4,
        "stroke-opacity": 1,
        "kot": $angle
    },
    "geometry": {
        "type": "LineString",
        "coordinates": [
$coordinates
        ]
    }
}
""".trimIndent()
    }

    private fun computeArcPoints(start: Koordinate, end: Koordinate, angleDeg: Double, steps: Int): List<Koordinate> {
        val angleRad = Math.toRadians(angleDeg)

        val dx = end.x - start.x
        val dy = end.y - start.y
        val dist = sqrt(dx * dx + dy * dy)

        val mid = Koordinate((start.x + end.x) / 2, (start.y + end.y) / 2)

        val radius = dist / (2 * sin(abs(angleRad) / 2))
        val h = radius * cos(angleRad / 2)

        val perpX = -dy / dist
        val perpY = dx / dist

        val sign = if (angleDeg >= 0) 1 else -1
        val centerX = mid.x + sign * perpX * h
        val centerY = mid.y + sign * perpY * h
        val center = Koordinate(centerX, centerY)

        val startAngle = atan2(start.y - center.y, start.x - center.x)
        var endAngle = atan2(end.y - center.y, end.x - center.x)

        if (angleDeg > 0 && endAngle < startAngle) {
            endAngle += 2 * PI
        } else if (angleDeg < 0 && endAngle > startAngle) {
            endAngle -= 2 * PI
        }

        val points = (0..steps).map { i ->
            val t = i.toDouble() / steps
            val theta = startAngle + t * (endAngle - startAngle)
            val x = center.x + radius * cos(theta)
            val y = center.y + radius * sin(theta)
            Koordinate(x, y)
        }

        return points
    }
}
