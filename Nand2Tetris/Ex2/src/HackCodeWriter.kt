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
            "static" -> code = "16"
            "temp" -> code = "5"
        }
        if(command == VmCommand.C_PUSH) {
            when (segment) {

                "constant" -> WriteCommand(pushConstantComannd(index.toString()))
                "local", "argument", "this", "that" -> WriteCommand(pushLattComannd(code, index.toString()))
                "pointer"-> WriteCommand(pushPointerComannd((index+3).toString()))
                "temp" -> WriteCommand(pushTempComannd(code,index.toString()))
                "static" -> WriteCommand(pushStaticComannd(inputFileName, index.toString()))
            }
        }
        else {
            when (segment) {

                "local", "argument", "this", "that" -> WriteCommand(popLattComannd(code, index.toString()))
                "pointer"-> WriteCommand(popPointerComannd((index+3).toString()))
                "temp" -> WriteCommand(popTempComannd(code, index.toString()))
                "static" -> WriteCommand(popStaticComannd(inputFileName, index.toString()))
            }
        }
    }

    fun pushConstantComannd(index : String):String
    {
        return ( """
            @$index
            D=A
            @SP
            M=M+1
            A=M-1
            M=D
            """.trimIndent())
    }

    fun pushLattComannd(code : String, index : String):String
    {
        return ( """
            @$index
            D=A
            @$code
            A=M
            A=D+A
            D=M
            @SP
            M=M+1
            A=M-1
            M=D
            """.trimIndent())
    }

    fun pushTempComannd(code : String, index : String):String
    {
        return ( """
            @$index
            D=A
            @$code
            A=D+A
            D=M
            @SP
            M=M+1
            A=M-1
            M=D
            """.trimIndent())
    }

    fun pushStaticComannd(fileName : String, index : String):String
    {
        return ( """
            @$fileName.$index
            D=M
            @SP
            A=M
            M=D
            @SP
            M=M+1
            """.trimIndent())
    }



    fun pushPointerComannd(index : String):String
    {
        return ( """
            @$index
            D=M
            @SP
            M=M+1
            A=M-1
            M=D
            """.trimIndent())
    }



    fun popLattComannd(code : String, index : String):String
    {
        return ( """

            @$code
            D=M
            @$index
            D=D+A
            @13
            M=D

            @SP
            M=M-1

            A=M
            D=M

            @13
            A=M
            M=D
            """.trimIndent())
    }

    fun popTempComannd(code : String, index : String):String
    {

        return ( """

            @$code
            D=A
            @$index
            D=D+A
            @13
            M=D

            @SP
            M=M-1

            A=M
            D=M


            @13
            A=M
            M=D


            """.trimIndent())
    }
    fun popPointerComannd( index : String):String
    {
        return ( """
            @SP
            M=M-1
            A=M
            D=M
            @$index
            M=D
            """.trimIndent())
    }

    fun popStaticComannd(fileName : String, index : String):String
    {
        return ( """
            @SP
            M=M-1
            A=M
            D=M
            @$fileName.$index
            M=D
            """.trimIndent())
    }

    fun close() {
        return;
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    fun WriteCommand(command: String) {
        File(this.outputFilePath).appendText(command);
    }

    fun writeLabel(label: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun writeGoto(label: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun writeIf(label: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun writeFunction(functionName: String, numLocals: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun writeCall(functionName: String, numArgs: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    fun writeReturn() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


