
package scraping

import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extract
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.*

// Data class za shranjevanje informacij o vsaki sekciji (tip in zelenjava)
data class PlantingSection(
    var type: String,
    var vegetables: List<String>
)

data class MonthlyPlanting(
    val month: String,
    val sections: List<PlantingSection>
)

fun extractVegetables(text: String): List<String> {
    val parts = text.split(Regex("\\b(Korenina|List|Cvet|Plod):|,"))

    return parts.map { it.trim() }
        .filter { it.isNotEmpty() && it !in listOf("Korenina", "List", "Cvet", "Plod") }
}

fun scrapeVegetables(): List<MonthlyPlanting>{
    val extractedData = skrape(HttpFetcher) {
        request {
            url = "https://www.sam.si/baza-znanja/vrt-in-okolica/koledar-setev-in-sajenj-po-mesecih"
        }

        extract {
            htmlDocument {
                val sections = mutableListOf<PlantingSection>()
                val monthlyPlanting = mutableListOf<MonthlyPlanting>()
                val months = mutableListOf<String>()
                var monthCounter = 0
                var sectionCounter = 0
                var addMonthAndSectionCounter = 0

                div {
                    withClass = "content"
//                    println("Div content found!")

                    h3 {
                        findAll {
                            forEach { title ->
                                val text = title.text.trim()
                                if (text.isNotEmpty()) {
                                    months.add(text)
                                }
                            }
                        }
                    }

                    table {
//                        println("Table found!")

                        // Poiščemo vse vrstice v tabeli (tr)
                        findAll {
//                            println("Rows found: $size")
                            forEach { tableElement ->
                                // Poiščemo vse vrstice (tr) znotraj tabele
                                tableElement.tr {

                                    findAll {
                                        // Preberemo vse celice (td) v vrstici
                                        val tempTypes = mutableListOf<String>()
                                        var counter = 0

                                        forEach { row ->
                                            val cells = row.td { findAll { map { it.text } } }
                                            // println("Found cells: $cells")
                                            counter++
                                            if (counter % 2 != 0) {
                                                for (i in 0..2) {
                                                    val cell = cells[i].trim()
                                                    if (cell.isNotEmpty() && cell !in tempTypes) {
                                                        tempTypes.add(cell)
                                                    }
                                                }
                                            } else if (cells.size >= 2) {

                                                for (i in 0..2) {
                                                    val type = tempTypes[i]
                                                    val vegetables = extractVegetables(cells[i])
                                                    sections.add(PlantingSection(type, vegetables))
                                                    addMonthAndSectionCounter++

//                                                    println("Added section: $type -> $vegetables")
                                                }

                                            }

                                            if (addMonthAndSectionCounter != 0 && addMonthAndSectionCounter % 6 == 0) {
                                                val temp = mutableListOf<PlantingSection>()
                                                for (i in 0..2) {
                                                    temp.add(sections[sectionCounter])
                                                    sectionCounter++
                                                }
                                                monthlyPlanting.add(MonthlyPlanting(months[monthCounter], temp))
                                                monthCounter++
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                val remainingSections = mutableListOf<PlantingSection>()
                while (sectionCounter < sections.size) {
                    remainingSections.add(sections[sectionCounter])
                    sectionCounter++
                }
                if (remainingSections.isNotEmpty()) {
                    monthlyPlanting.add(MonthlyPlanting(months[monthCounter], remainingSections))
                }
                monthlyPlanting
            }
        }
    }

//    println("========== izpis ==========")
//    extractedData.forEach { monthly ->
//        println("Mesec: ${monthly.month}")
//        monthly.sections.forEach { section ->
//            println("  Tip sekcije: ${section.type}")
//            section.vegetables.forEach { vegetable ->
//                println("    Zelenjava: $vegetable")
//            }
//        }
//    }
    return extractedData
}

data class GoodNeighbours(
    val vegetable: String,
    val goodNeighbours: String,
    val badNeighbours: String,
)

fun scrapeGoodNeighbours(): List<GoodNeighbours> {
    var remainingCells = mutableListOf<String>()
    val GoodNeighboursList = mutableListOf<GoodNeighbours>()


    val extractedData = skrape(HttpFetcher) {
        request {
            url = "https://www.okusnivrt.com/novice/dobri-slabi-sosedje/"
        }

        extract {
            htmlDocument {

                table{
                    withClass = "table"
                    findAll{
                        forEach { tableElement ->
                            // Poiščemo vse vrstice (tr) znotraj tabele
                            tableElement.tr {
                                findAll {
                                    val temp = th { findAll { map { it.text.trim() } } }
                                    remainingCells = temp.drop(3).toMutableList()
                                    remainingCells.removeAt(3)
                                    remainingCells.removeAt(7)

                                    val dataCells = td { findAll { map { it.text.trim() } } }.toMutableList()
                                    dataCells.removeAt(8)
                                    dataCells.removeAt(8)
                                    dataCells.removeAt(8)
                                    dataCells.removeAt(21)
                                    dataCells.removeAt(21)
                                    dataCells.removeAt(21)
                                    dataCells.removeAt(39)
                                    dataCells.removeAt(18)

                                    var whileCounter = 0
                                    var i = 0
                                    var veggieIndex = 0
                                    while (whileCounter < remainingCells.size) {
                                        val veg = remainingCells[veggieIndex]
                                        val good = dataCells[i]
                                        val bad = dataCells[i+1]

                                        GoodNeighboursList.add(GoodNeighbours(veg, good, bad))

                                        i += 3

                                        veggieIndex++
                                        whileCounter++
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

//    println("\n\n========== izpis ==========")
//
//    GoodNeighboursList.forEach { element ->
//        println(" Zelenjava: ${element.vegetable}")
//        println("    Dobri sosedje: ${element.goodNeighbours}")
//        println("    Slabi sosedje: ${element.badNeighbours}")
//    }

    return GoodNeighboursList
}

data class WaterRequirement(
    val plant: String,
    val minInchesPerWeek: Double,
    val maxInchesPerWeek: Double
)

fun scrapeWaterRequirements(): List<WaterRequirement> {
    return skrape(HttpFetcher) {
        request {
            url = "https://extension.usu.edu/yardandgarden/research/water-recommendations-for-vegetables"
        }

        extract {
            htmlDocument {
                val waterRequirements = mutableListOf<WaterRequirement>()

                table {
                    withClass = "table"


                    tbody {
                        tr {
                            findAll {
                                forEach { row ->
                                    val cells = row.td {
                                        findAll {
                                            map { it.text.trim().replace(",", ".") }
                                        }
                                    }

                                    if (cells.size == 3) {
                                        val plant = cells[0]
                                        val min = cells[1].toDoubleOrNull() ?: 0.0
                                        val max = cells[2].toDoubleOrNull() ?: 0.0

                                        waterRequirements.add(
                                            WaterRequirement(plant, min, max)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                println("========== Zalivanje ==========")
                waterRequirements.forEach {
                    println("${it.plant}: ${it.minInchesPerWeek} - ${it.maxInchesPerWeek} palcev/teden")
                }

                waterRequirements
            }
        }
    }
}



fun main() {

    scrapeVegetables()
    scrapeGoodNeighbours()
    scrapeWaterRequirements()

}
