
import java.io.File


class VmParser (filePath:String) {


    var filePath: String =""
    var fileContent: MutableList<Line> = mutableListOf()
    var lineIndex : Int = 0
    var numOfLines : Int = 0
    var currentLine : Line = Line("",0)


    init {
        this.filePath = filePath
        var tempContent : List<String> = File(filePath).readLines()
        tempContent.forEachIndexed { index, it ->
            if (it.startsWith("//") || it.trim().isEmpty()){
                //do simtihng
            }
            else{
                var trimLine = it.replace(Regex("\\s")," ")
                var line : Line = Line(trimLine,index);
                this.fileContent.add(line)

            }
        }
        this.currentLine = fileContent[0]
        this.numOfLines = fileContent.count()

    }


    fun getSplitedCommand() : List<String> {
        return  this.currentLine.lineContent.split(Regex("\\s")) // the seperetor is any white spate
    }


    fun hasMoreCommands(): Boolean{
        return (this.lineIndex < this.numOfLines)
    }

    fun advance()
    {
        this.lineIndex ++;
        if(this.hasMoreCommands()){
            this.currentLine = fileContent[lineIndex]
        }


    }
    fun commandType() : VmCommand {

       // var temp: List<String> = this.currentLine.lineContent.split(" ")
        var firstWord : String = getSplitedCommand()[0]
        if ( firstWord == "add" ||
                firstWord == "sub" ||
                firstWord == "neg" ||
                firstWord == "eq" ||
                firstWord == "gt" ||
                firstWord == "lt" ||
                firstWord == "and" ||
                firstWord == "or" ||
                firstWord == "not") {
            return VmCommand.C_ARITHMETIC
        }
        if (firstWord == "push") { return VmCommand.C_PUSH;}
        if (firstWord == "pop") { return VmCommand.C_POP;}

        // If an unknown command is received
        println("Error: Unknown token '" + firstWord + "' in line " + (this.currentLine.sourceLineNumber).toInt() + " in file \""+this.filePath +"\"")
        return VmCommand.C_UNKNOWN

    }

    ///Returns the first argument of the current
    //command. In the case of C_ARITHMETIC,
    //the command itself (add, sub, etc.) is
    //returned. Should not be called if the current
    //command is C_RETURN.
    fun arg1(): String{

        var temp: List<String> = this.currentLine.lineContent.split(" ")

        when(commandType()){
            VmCommand.C_RETURN -> println("Warning: arg1() not supposed to be called for C_RETURN type")
            VmCommand.C_UNKNOWN -> return ""
        }


        if (commandType() == VmCommand.C_ARITHMETIC) {
           return  temp[0];
        }
        else{
            return temp[1]
        }


    }

    //Returns the second argument of the current
    //command. Should be called only if the
    //current command is C_PUSH, C_POP,
    //C_FUNCTION, or C_CALL
    fun arg2():Int {

        var currentCommnadType = commandType()


        when (currentCommnadType) {
            VmCommand.C_RETURN -> {
                println("Warning: arg2() not supposed to be called for C_RETURN type")
                return 0
            }
            VmCommand.C_ARITHMETIC -> {
                println("Warning: arg2() not supposed to be called for C_ARITHMETIC type")
                return 0
            }
            VmCommand.C_LABEL -> {
                println("Warning: arg2() not supposed to be called for C_LABEL type")
                return 0
            }
            VmCommand.C_GOTO -> {
                println("Warning: arg2() not supposed to be called for C_GOTO type")
                return 0
            }
            VmCommand.C_IF -> {
                println("Warning: arg2() not supposed to be called for C_IF type")
                return 0
            }
            VmCommand.C_UNKNOWN -> return 0
        }

        if (currentCommnadType == VmCommand.C_PUSH ||
                currentCommnadType == VmCommand.C_POP ||
                currentCommnadType == VmCommand.C_FUNCTION ||
                currentCommnadType == VmCommand.C_CALL) {
            return getSplitedCommand()[2].toInt()
        }
        return 0

    }
}