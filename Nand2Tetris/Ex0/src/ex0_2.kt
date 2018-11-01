import java.io.File
import java.io.IOException
import java.nio.file.Files
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView




fun main(args: Array<String>){

    var intNumber:Int = 0;

    //println("enter a library path");

    val fr :JFileChooser  = JFileChooser()
    val fw:FileSystemView  = fr.fileSystemView

    var path: String?  = fw.defaultDirectory.path;
    path = path + "\\"
    
    try {

        //Scans all files in a folder
        File(path ).walkTopDown().forEach {
            if (it.name == "hello.vm") {

                var text: List<String> = Files.readAllLines((it.toPath()));
                println(it);

                text.forEach { inIt ->
                    var filePath = path + intNumber++.toString() + ".asm";
                    if (inIt.contains("you")) {
                        println(inIt)
                    }
                    File(filePath).writeText(inIt)
                }

            }


        }
    }
    catch (e: IOException) {
    }

}
