package Ex1.src
import VmCommand
import java.io.File

class HackCodeWriter(outputFilePath:String) {

    var lebelRun = 0
    var inputFileName:String = ""
    var outputFilePath:String = ""
    init {
        this.outputFilePath = outputFilePath
        File(this.outputFilePath).writeText(GetStackInitCommand())

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
            "gt" -> WriteCommand(GetJumpAritmeticCommand("JGQ",getLebel(),getLebel()))
            "lt" -> WriteCommand(GetJumpAritmeticCommand("JLT",getLebel(),getLebel()))
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
            M=M+${command}D
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
            D;"${command}
            @SP
            A=M-1
            M=0
            @LABEL_${lebel2}
            1;JMP
            (LABEL_${lebel1})
            @SP
            A=M-1
            M=-1
            (LABEL_${lebel2}")
            """.trimIndent())
    }

    fun getLebel():Int
    {
        return lebelRun++
    }

    fun setFileName(fileName: String) {
        this.inputFileName = fileName;
    }

    fun writePushPop(command: VmCommand, arg1: String, arg2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun close() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    fun WriteCommand(command: String) {
        File(this.outputFilePath).writeText(command);
    }
}


