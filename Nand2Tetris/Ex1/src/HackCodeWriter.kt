package Ex1.src
import VmCommand
import java.io.File

class HackCodeWriter(outputFilePath:String) {

    var lebelRun = 0
    var inputFileName:String = ""
    var outputFilePath:String = ""
    init {
        this.outputFilePath = outputFilePath
        File(this.outputFilePath).appendText(GetStackInitCommand())

    }


    fun writeArithmetic(command : String)
    {
        when (command) {
            "add"-> WriteCommand(GatBinaryAritmeticCommand("+"))
            "sub" -> WriteCommand(GatBinaryAritmeticCommand("-"))
            "and" -> WriteCommand(GatBinaryAritmeticCommand("&"))
            "or" -> WriteCommand(GatBinaryAritmeticCommand("|"))
            "neg" ->  WriteCommand(GetUnaryAritmeticCommand("-"))
            "not" -> WriteCommand(GetUnaryAritmeticCommand("!"))
            "eq" -> WriteCommand(GetJumpAritmeticCommand("JEQ",getLebel(),getLebel()))
            "gt" -> WriteCommand(GetJumpAritmeticCommand("JLT",getLebel(),getLebel()))
            "lt" -> WriteCommand(GetJumpAritmeticCommand("JGT",getLebel(),getLebel()))
        }
    }

    fun GetStackInitCommand(): String {
        return ( """
            |@256
            |D=A
            |@SP
            |M=D
            |""".trimMargin("|"))
    }
    fun GatBinaryAritmeticCommand(command : String):String{
        return( """
            @SP
            M=M-1
            A=M
            D=M
            @SP
            M=M-1
            A=M
            M=M${command}D
            @SP
            M=M+1
        """.trimIndent())
    }
    fun GetUnaryAritmeticCommand(command : String): String {
        return ("""
            @SP
            M=M-1
            A=M
            M=${command}M
            @SP
            M=M+1
            """.trimIndent())
    }
    fun GetJumpAritmeticCommand(command : String,lebel1:Int,lebel2:Int):String{
        return ("""
            @SP
            M=M-1
            A=M
            D=M
            A=A-1
            A=M
            D=D-A
            @LABEL_${lebel1}
            D; ${command}
            @SP
            A=M-1
            M=0
            @LABEL_${lebel2}
            1;JMP
            (LABEL_${lebel1})
            @SP
            A=M-1
            M=-1
            (LABEL_${lebel2})
            """.trimIndent())
    }

    fun getLebel():Int
    {
        return lebelRun++
    }

    fun setFileName(fileName: String) {
        this.inputFileName = fileName;
    }

    fun writePushPop(command: VmCommand, segment: String, index: Int) {
        var code : String = ""
        when(segment)
        {
            "local" -> code = "LCL"
            "argument" -> code = "ARG"
            "this" -> code = "THIS"
            "that" -> code = "THAT"
            "pointer" -> code = "3"
            "static" -> code = "5"
            "temp" -> code = "5"
        }

        when (segment)
        {
            "constant" ->WriteCommand(constantComannd(index.toString()))
            "local","argument","this","that"->WriteCommand(lattComannd(code,index.toString()))
            "pointer","temp"->WriteCommand(ptComannd(code,index.toString()))
            "static"->WriteCommand(staticComannd(inputFileName,index.toString()))
        }

        if(command == VmCommand.C_PUSH)
            WriteCommand(push())
        else
            WriteCommand(pop())
    }

    fun constantComannd(index : String):String
    {
        return ( """
            |@$index
            |D=A
            |""".trimMargin("|"))
    }

    fun lattComannd(code : String, index : String):String
    {
        return ( """
            |@$code
            |D=A
            |@$index
            |A=D+A

            |""".trimMargin("|"))
    }

    fun ptComannd(code : String, index : String):String
    {
        return ( """
            |@$code
            |D=A
            |@$index
            |D=D+A
            |""".trimMargin("|"))
    }

    fun staticComannd(fileName : String, index : String):String
    {
        return ( """
            |@$fileName.$index
            |D=M
            |""".trimMargin("|"))
    }

    fun push():String
    {
        return ( """
            |D=M
            |@SP
            |A=M
            |M=D
            |D=A+1
            |@SP
            |M=D
            |""".trimMargin("|"))

    }

    fun pop():String
    {
        return ( """
            |D=A
            |@R13
            |M=D
            |@SP
            |M=M-1
            |A=M
            |D=M
            |@R13
            |M=D
            |""".trimMargin("|"))
    }

    fun close() {
        return;
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    fun WriteCommand(command: String) {
        File(this.outputFilePath).appendText(command);
    }
}


