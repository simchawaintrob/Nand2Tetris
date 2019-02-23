package exe5

import java.io.File

//rivky kremer  208817585
//isca cohen 316179175

fun main(args: Array<String>) {
    var pathDir = "C:\\Users\\Nurit\\Downloads\\Exercises\\Targil5\\project 11\\Pong"
    var newFile: File



    File(pathDir).walk()
            .forEach {
                if (it.extension == "jack") {
                    AllTokens.clear()

                    var fileName = File(it.name).nameWithoutExtension
                    newFile = File(pathDir + "\\" + fileName.toString() + "T.xml")
                    if (newFile.exists()) {
                        newFile.delete()
                    }
                    newFile.appendText("""
                        <tokens>

                    """.trimIndent())
                    it.forEachLine {
                        AllLine=it

                        if (!it.startsWith("//") && !it.startsWith("/**") && !it.startsWith(" *") && !it.endsWith("*/") && it.length > 0) {
                            if (it.contains("//")) {
                                Tokenizing(newFile).TokenAnalizer(it.substringBefore("//").split(Regex("\\s")))
                                //lineElements = file_line.split(Regex("\\s"))
                            } else if (it.contains("/**") and it.contains("*/")) {
                                Tokenizing(newFile).TokenAnalizer(it.substringBefore("/**").substringAfter("*/").split(Regex("\\s")))
                            } else if (it.contains("/**") and !it.contains("*/")) {
                                Tokenizing(newFile).TokenAnalizer(it.substringBefore("/**").split(Regex("\\s")))
                            } else if (!it.contains("/**") and it.contains("*/")) {
                                Tokenizing(newFile).TokenAnalizer(it.substringAfter("*/").split(Regex("\\s")))
                            }else if(it.contains("/*") and !it.contains("*/"))
                                Tokenizing(newFile).TokenAnalizer(it.substringBefore("/**").split(Regex("\\s")))
                            else if(it.contains("/*") and it.contains("*/"))
                                Tokenizing(newFile).TokenAnalizer(it.substringBefore("/*").substringAfter("*/").split(Regex("\\s")))
                            else
                                Tokenizing(newFile).TokenAnalizer(it.split(Regex("\\s")))


                        }
                    }
                    Tokenizing(newFile).WriteToFile()
                    newFile.appendText("""
                        </tokens>

                    """.trimIndent())
                    index=0
                    countOfTabs=0

                    var newFile2 = File(pathDir + "\\" + fileName.toString() + ".vm")
                    if (newFile2.exists()) {
                        newFile2.delete()
                    }

                    ProgramStructure(newFile2,newFile).buildClass()
                }
            }
}


