import java.io.File

fun main(args: Array<String>) {




    println("get source dir:")
    var sourceDir = ""
    sourceDir = readLine()!!;




    println("get oututDir:")
    var targetDir = readLine();



    File(sourceDir ).walkTopDown().forEach {
        if (File(it.name).extension == "jack") {

            var outputFile = targetDir + """\""" + File(it.name).nameWithoutExtension + "T.xml"
            if (File(outputFile).exists()) {
                File(outputFile).delete()
            }
            var tokenizer: JackTokenizer = JackTokenizer(it.path)
            tokenizer.Writer(outputFile)

        }

    }

}