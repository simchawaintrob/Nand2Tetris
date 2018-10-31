import java.io.File
import java.io.IOException
import java.nio.file.Files



     fun main(args: Array<String>){
//C:\Users\simcha\Downloads
        var intNumber:Int = 0;

        //println("enter a library path");

        var path: String?  ="C:\\Users\\user\\Downloads\\"

        //Scans all files in a folder
        File(path).walkTopDown().forEach{
            if (it.name == "hello.vm")
                try {

                    var text : List<String> =  Files.readAllLines((it.toPath()));
                    println(it);

                    text.forEach { inIt ->
                        var filePath = path + intNumber++.toString() + ".asm"
                        println(inIt)
                        File(filePath).writeText(inIt)
                    }

                    // Files.write(Paths.get(it.path), number.toByteArray(), StandardOpenOption.APPEND)
                    //   intNumber ++;// = (intNumber + 1);
                    //   number = intNumber.toString();
                } catch (e: IOException) {
                }

        }

    }
