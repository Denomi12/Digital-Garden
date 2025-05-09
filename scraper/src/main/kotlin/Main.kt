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

//data class PlantingCalendar(
//    val months: List<MonthlyPlanting>
//)

fun scrapeVegetables(): List<MonthlyPlanting>{

    println("========== scraping - extracting example ==========")

    val extractedData = skrape(HttpFetcher) {
        request {
            url = "https://www.sam.si/baza-znanja/vrt-in-okolica/koledar-setev-in-sajenj-po-mesecih"
        }

        extract {
            htmlDocument {
                // Seznam za shranjevanje sekcij
                val sections = mutableListOf<PlantingSection>()
                val monthlyPlanting = mutableListOf<MonthlyPlanting>()
                val months = mutableListOf<String>()
                var monthCounter = 0
                var sectionCounter = 0
                var addMonthAndSectionCounter = 0

                div {
                    withClass = "content"
                    println("Div content found!")

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
                        println("Table found!")

                        // Poiscemo vse vrstice v tabeli (tr)
                        findAll {
                            println("Rows found: $size")
                            forEach { tableElement ->
                                // Poiscemo vse vrstice (tr) znotraj tabele
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
                                                    val vegetables =
                                                        cells[i].split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                                    sections.add(PlantingSection(type, vegetables))
                                                    addMonthAndSectionCounter++

                                                    println("Added section: $type -> $vegetables")
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

    println("========== izpisanih sekcij ==========")
    extractedData.forEach { monthly ->
        println("Mesec: ${monthly.month}")
        monthly.sections.forEach { section ->
            println("  Tip sekcije: ${section.type}")
            section.vegetables.forEach { vegetable ->
                println("    Zelenjava: $vegetable")
            }
        }
    }
    return extractedData
}

fun main() {

    val temp = scrapeVegetables()
}