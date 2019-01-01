

import java.io.File

fun main(args: Array<String>) {




    println("get source dir:")
    var sourceDir = ""
    sourceDir = readLine()!!;




    println("get oututDir:")
    var targetDir = readLine();



    File(sourceDir ).walkTopDown().forEach {
        if (File(it.name).extension == "jack") {

            var outputFile1 = targetDir + """\""" + File(it.name).nameWithoutExtension + "T.xml"
            var outputFile2 = targetDir + """\""" + File(it.name).nameWithoutExtension + ".xml"
            if (File(outputFile1).exists()) {
                File(outputFile1).delete()
            }
            var tokenizer: JackTokenizer = JackTokenizer(it.path)
            tokenizer.Writer(outputFile1)

           // var NewCompilationEngine : CompilationEngine = CompilationEngine(File(outputFile2),File(outputFile1))
          // NewCompilationEngine.parseClass()


        }

    }

}