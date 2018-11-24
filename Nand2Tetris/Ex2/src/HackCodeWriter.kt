import java.io.File

class HackCodeWriter(outputFilePath:String) {

    var lebelRun = 0
    var returnAddressNum = 0
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

    fun getReturnAddressNumber():Int
    {
        return returnAddressNum ++
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
        WriteCommand("""($inputFileName.$label)""")
    }

    fun writeGoto(label: String) {
        WriteCommand("""
            @$inputFileName.$label
            0;JMP
            """.trimIndent());
    }

    fun writeIf(label: String) {
        WriteCommand("""
            @SP
            M=M-1
            A=M
            D=M
            @$inputFileName.$label
            D;JNE
            """.trimIndent());

    }

    fun writeFunction(functionName: String, numLocals: Int)
    {
        WriteCommand("""
            ($functionName)
            @$numLocals
            D=A
            @$functionName.End
            D; JEQ
            ($functionName.Loop)
            @SP
            A=M
            M=0
            @SP
            M=M+1
            @$functionName.Loop
            D=D-1; JNE
            ($functionName.End)
            """.trimIndent());
    }

    fun writeCall(functionName: String, numArgs: Int) {
        var returnNumber = getReturnAddressNumber()
        WriteCommand("""
        @$functionName.ReturnAddress$returnNumber
        D=A
        @SP
        A=M
        M=D
        @SP
        M=M+1
        @LCL
        D=M
        @SP
        A=M
        M=D
        @SP
        M=M+1
        @ARG
        D=M
        @SP
        A=M
        M=D
        @SP
        M=M+1
        @THIS
        D=M
        @SP
        A=M
        M=D
        @SP
        M=M+1
        @THAT
        D=M
        @SP
        A=M
        M=D
        @SP
        M=M+1
        @SP
        D=M
        @${numArgs + 5}
        D=D-A
        @ARG
        M=D
        @SP
        D=M
        @LCL
        M=D
        @$functionName
        0; JMP
        ($functionName.ReturnAddress$returnNumber)
        """.trimIndent())

    }

    fun writeReturn() {

        WriteCommand("""
            @LCL
            A=M-1
            A=A-1
            A=A-1
            A=A-1
            A=A-1
            D=M
            @R14
            M=D

            @SP
            M=M-1
            @SP
            A=M
            D=M
            @ARG
            A=M
            M=D

            @ARG
            D=M+1
            @SP
            M=D

            @LCL
            A=M-1
            D=M
            @THAT
            M=D

            @LCL
            A=M-1
            A=A-1
            D=M
            @THIS
            M=D

            @LCL
            A=M-1
            A=A-1
            A=A-1
            D=M
            @ARG
            M=D

            @LCL
            A=M-1
            A=A-1
            A=A-1
            A=A-1
            D=M
            @LCL
            M=D

            @R14
            A=M
            0; JMP
            """.trimIndent())
    }
}


