import Ex1.src.HackCodeWriter
import java.io.File
import java.io.IOException
import java.nio.file.Files

class VMtTranslator(){


    //  var inputDirPath:String = ""
    //var outputFilePath:String = ""
    //var MyVmParser: VmParser
    // var CodeWriter : HackCodeWriter = HackCodeWriter("");
    var ssucceed: Boolean = true;


    fun compile (inputDirPath:String, outputFilePath :String): Boolean {


        try {

            //Scans all files in a folder
            File(inputDirPath ).walkTopDown().forEach {
                if (File(it.name).extension == "vm") {
                    var outputFile = outputFilePath + """\""" + File(it.name).nameWithoutExtension + ".asm"
                    if (File(outputFile).exists()){
                        File(outputFile).delete()
                    }
                    var codeWriter = HackCodeWriter(outputFile);
                    codeWriter.setFileName(it.name);
                    println(inputDirPath + """\""" + it.name) //test
                    var MyVmParser = VmParser(inputDirPath + """\""" + it.name);
                    var command:VmCommand
                    var arg1:String
                    var arg2: Int = 0
                    var startMessage = """
 |
                    |
                    |//    ------ START OF FILE : ${it.name} --------
                    |
                    |
                    """.trimMargin("|")


                    var endMessage = """
                    |
                    |
                    |//    ------ END OF FILE : ${it.name} --------
                    |
                    |
                    """.trimMargin("|")


                    File(codeWriter.outputFilePath).appendText(startMessage)
                    while ( MyVmParser.hasMoreCommands()){
                        command = MyVmParser.commandType()
                        arg1 = MyVmParser.arg1()
                        if (command != VmCommand.C_ARITHMETIC) {
                            arg2 = MyVmParser.arg2()
                        }

                        if(command != VmCommand.C_UNKNOWN){

                            // print line content  as comment
                            File(codeWriter.outputFilePath).appendText("""

                                // line ${MyVmParser.currentLine.sourceLineNumber}: ${MyVmParser.currentLine.lineContent}

                            """.trimIndent())

                            when(command){

                                VmCommand.C_ARITHMETIC -> codeWriter.writeArithmetic(arg1)
                                VmCommand.C_PUSH -> codeWriter.writePushPop(VmCommand.C_PUSH, arg1, arg2)
                                VmCommand.C_POP -> codeWriter.writePushPop(VmCommand.C_POP, arg1, arg2)
                                VmCommand.C_UNKNOWN -> {
                                    println("Error: Unknown command in file - ${it.name} -")
                                    ssucceed = false}
                            }
                        }
                        MyVmParser.advance();

                    }
                    File(codeWriter.outputFilePath).appendText(endMessage)
                    codeWriter.close()

                }



                /*  var text: List<String> = Files.readAllLines((it.toPath()));
                  println(it);

                  text.forEach { inIt ->
                      var filePath = path + intNumber++.toString() + ".asm";
                      if (inIt.contains("you")) {
                          println(inIt)
                      }
                      File(filePath).appendText(inIt)
                  }*/

            }



        }
        catch (e: IOException) {
        }


        return ssucceed;
    }

}