
fun main(args: Array<String>) {
    var translator : VMtTranslator = VMtTranslator()
    println("get dir:")
    var dir = ""
    dir = readLine()!!;
    println("get oututName:")
    var outputName = readLine();
    if (outputName != null) {
        translator.compile(dir,outputName)
    }

}