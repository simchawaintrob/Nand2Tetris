
fun main(args: Array<String>) {
    var myParser : VmParser = VmParser("""C:\Users\leora\Desktop\a\Exercises\Targil1\project 07\SimpleAdd\SimpleAdd.vm""")
    println( myParser.currentLine.lineContent )
    var commnadStr: String = myParser.getSplitedCommand()[0]
    var arg1  =  myParser.arg1()
    var arg2 = myParser.arg2()
    println("""
    |$commnadStr
    |$arg1
    |$arg2
    |""".trimMargin())
}