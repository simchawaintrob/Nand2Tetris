package Ex1.src

class HackCodeWriter {

    var lebelRun = 0


    fun writeArithmetic(command : String)
    {
        when (command) {
            "add"-> binaryAritmetic("+")
            "sub" -> binaryAritmetic("-")
            "and" -> binaryAritmetic("&")
            "or" -> binaryAritmetic("|")
            "neg" -> unaryAritmetic("-")
            "not" -> unaryAritmetic("!")
            "eq" -> jumpAritmetic("JEQ",getLebel(),getLebel())
            "gt" -> jumpAritmetic("JGQ",getLebel(),getLebel())
            "lt" -> jumpAritmetic("JLT",getLebel(),getLebel())
        }
    }

    fun stackInit()
    {
        myfile.writeText("@256\nD=A\n@SP\nM=D\n")
    }
    fun binaryAritmetic(command : String)
    {

        myfile.writeText("@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\\nM=M"+ command +"D\n@SP\nM=M+1\n")
    }
    fun unaryAritmetic(command : String)
    {
        myfile.writeText("@SP\nM=M-1\nA=M\nM="+command+"M\n@SP\nM=M+1\n")
    }
    fun jumpAritmetic(command : String,lebel1:Int,lebel2:Int)
    {
        myfile.writeText("@SP\nM=M-1\nA=M\nD=M\nA=A-1\nA=M\nD=D-A\n@LABEL_"+lebel1+"\nD;"+command+"\n@SP\nA=M-1\nM=0\n@LABEL_"+lebel2+"\n1;JMP\n(LABEL_"+lebel1+")\n@SP\nA=M-1\nM=-1\n(LABEL_"+lebel2+")\n")
    }

    fun getLebel():Int
    {
        return lebelRun++
    }
}