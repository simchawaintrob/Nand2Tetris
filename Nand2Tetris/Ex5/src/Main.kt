import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths

val listOfTokenTypes= listOf<String>("KEYWORD", "SYMBOL", "INT_CONST","STRING_CONST", "IDENTIFIER")
val listOfKeyWords= listOf<String>("CLASS", "METHOD", "FUNCTION", "CONSTRUCTOR", "INT", "BOOLEAN", "CHAR", "VOID", "VAR", "STATIC", "FIELD", "LET", "DO", "IF", "ELSE", "WHILE", "RETURN", "TRUE", "FALSE", "NULL", "THIS")
val listOfSymbols=listOf<Char>('{','}','(',')','[',']','.',',',';','+','-','*','/','&','|','<','>','=','~')

fun main(args: Array<String>) {
    fun createTokensFileName(directoryName: String): String {



        val dot = '.'
        val fileName = directoryName.dropLast(5)
        val xmlEnd = "T.xml"
        val xmlFileName = fileName + xmlEnd
        return xmlFileName
    }

    fun createParsingFileName(directoryName: String): String {



        val dot = '.'
        val fileName = directoryName.dropLast(5)
        val vmEnd = ".vm"
        val vmFileName = fileName + vmEnd
        return vmFileName
    }



    fun endsWithJack(fileName: String): Boolean {
        val jack = fileName.toString().takeLast(4)
        if (jack == "jack")
            return true
        return false
    }

    fun run(directory: String) {

        val path = Paths.get(directory)
        //var filesList=Files.walk(path)
        var filesList = File(directory).list().filter { endsWithJack(it.toString()) }



        filesList.forEach {

            val tokensFile = createTokensFileName(it.toString())
            val parsingFile=createParsingFileName(it.toString())
            File(directory, tokensFile).createNewFile()
            File(directory,parsingFile).createNewFile()
            val TxmlFile = File(directory, tokensFile)
            val vmFile=File(directory, parsingFile)
            // TxmlFile.appendText("<tokens>\n<tokens>")
            //val myFile:File=File(path)

            var lines = File(directory+'\\'+it).readLines()
            var xmlString: String = String()
//            lines.forEach {
//                if (it.startsWith("//"))
//                {it.}
//                   else if (it.startsWith("/**"))
//                    {}
//                    else if (it.startsWith("*"))
//                        {}
//
//                else
//                xmlString += it.toString()
//            }


            lines=lines.filter { !it.startsWith("//")}
            lines=lines.filter { !it.startsWith("/**") }
            lines=lines.filter { !it.startsWith("*") }
            lines=lines.filter { !it.isEmpty() }
            lines.forEach {
                if(it.startsWith(' '))
                {
                    var l=it
                    l= l.dropWhile { c->c==' ' }
                    if(
                    !(l.startsWith("//"))&&
                            !(l.startsWith("/**"))&&
                            !(l.startsWith('*'))&&
                            !(l.startsWith("*/")))
                    {
                        var stringToadd=l.takeWhile { c->c!='/' }
                        var droppedL=l
                        droppedL=droppedL.dropWhile { c->c!='/' }
                        droppedL=droppedL.drop(1)
                        if(droppedL.startsWith('/')||droppedL.startsWith('*'))
                            xmlString=xmlString+stringToadd
                        else
                            xmlString=xmlString+l

                    }

                }
                else
                // xmlString+=it
                {var stringToadd=it.takeWhile { c->c!='/' }
                    var droppedL=it
                    droppedL=droppedL.dropWhile { c->c!='/' }
                    droppedL=droppedL.drop(1)
                    if(droppedL.startsWith('/')||droppedL.startsWith('*'))
                        xmlString=xmlString+stringToadd
                    else
                        xmlString=xmlString+it}

            }

            //xmlString.replace("\\s".toRegex(), "")
            xmlString= xmlString.filter { !it.equals('\t') }

            val t: Tokenizing = Tokenizing(
                    TxmlFile,
                    input = xmlString
            )
            t.Q0()
            val p:Parsing=Parsing(vmFile, input = TxmlFile)
            p.parseClass()


        }


    }

    //run("C:\\Users\\Racheli\\Desktop\\Exercises\\Targil4\\project 10\\jackFiles\\ArrayTest")
    //run("C:\\Users\\Racheli\\Desktop\\Exercises\\Targil4\\project 10\\jackFiles\\ExpressionlessSquare")
    //run("C:\\Users\\Racheli\\Desktop\\Exercises\\Targil4\\project 10\\jackFiles\\Square")
    //run("C:\\Users\\Racheli\\Desktop\\Exercises\\Targil5\\project 11\\Average")
    //run("C:\\Users\\Racheli\\Desktop\\Exercises\\Targil5\\project 11\\ComplexArrays")
    //run("C:\\Users\\Racheli\\Desktop\\Exercises\\Targil5\\project 11\\ConvertToBin")
    //run("C:\\Users\\Racheli\\Desktop\\Exercises\\Targil5\\project 11\\Pong")
    //run("C:\\Users\\Racheli\\Desktop\\Exercises\\Targil5\\project 11\\Seven")
    //run("C:\\Users\\Racheli\\Desktop\\Exercises\\Targil5\\project 11\\Square")
    //run("C:\\Users\\Racheli\\Desktop\\targil 3\\aa")
    run("C:\\Users\\user\\Documents\\לימודים\\עקרונות\\Exercises\\Targil5\\project 11\\Square\\JackToVMdir")


}

