
import jetbrains.datalore.plot.PlotSvgExport
import jetbrains.letsPlot.geom.geomDensity
import jetbrains.letsPlot.intern.Plot
import jetbrains.letsPlot.intern.toSpec
import jetbrains.letsPlot.letsPlot
import java.awt.Desktop
import java.io.File


fun createPlot(result: Map<Int, Double>): Plot {
    val data2 =mapOf<String, Any>(
        "year" to result.toSortedMap().values.toList()
    )

    return letsPlot(data2) + geomDensity(
        color = "dark-green",
        fill = "green",
        alpha = .3,
        size = 2.0
    ) { x = "year" }
}

fun openInBrowser(content: String) {
    val dir = File(System.getProperty("user.dir"), "lets-plot-images")
    dir.mkdir()
    val file = File(dir.canonicalPath, "my_plot.html")
    file.createNewFile()
    file.writeText(content)

    Desktop.getDesktop().browse(file.toURI())
}


fun main() {
    val countMatch = mutableMapOf<Int, Int>()
    val countTitle = mutableMapOf<Int, Int>()
    val countWords = mutableMapOf<String,Int>()
    //val regexMatch = Regex("^\\w+\t(\\d+)\t.* ?(\\w+?ing).*\$")
    val regexMatch = Regex("^\\w+\t(\\d+)\t(?:.* )?(\\w{2,}ing).*\$")
    val regexYear = Regex("^\\w+\t(\\d+)\t.*\$")
    File("data/titles.txt").forEachLine { line ->
        when {
            regexMatch.matches(line) -> {
                val match = regexMatch.find(line)!!.groupValues
                countMatch[match[1].toInt()] = (countMatch[match[1].toInt()] ?: 0) + 1
                countTitle[match[1].toInt()] = (countTitle[match[1].toInt()] ?: 0) + 1
                countWords[match[2]] = (countWords[match[2]] ?: 0) + 1

            }
            else -> {
                val match = regexYear.find(line)!!.groupValues
                countTitle[match[1].toInt()] = (countTitle[match[1].toInt()] ?: 0) + 1
            }
        }
    }

    val result = mutableMapOf<Int, Double>()
    countTitle.toSortedMap().forEach { (year, _) ->
        result[year] = (countMatch[year] ?: 0).toDouble() / ((countMatch[year] ?: 0) + countTitle[year]!!).toDouble()
    }

    val p = createPlot(result)

    // Export to SVG.
    // Note: if all you need is to save SVG to a file than you can just use the 'ggsave()' function.
    val content = PlotSvgExport.buildSvgImageFromRawSpecs(p.toSpec())
    openInBrowser(content)

    println("A2: ")
    val commonWords = countWords.toList().sortedByDescending { (key,value) -> value }
    for (i in 0..10) {
        println("Word: ${commonWords[i].first} occured ${commonWords[i].second} times")
    }
}

