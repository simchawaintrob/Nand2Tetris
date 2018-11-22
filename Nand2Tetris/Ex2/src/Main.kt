
fun main(args: Array<String>) {
    var translator : VMtTranslator = VMtTranslator()
    println("get source dir:")
    var dir = ""
    dir = readLine()!!;
    println("get oututDir:")
    var outputName = readLine();
    if (outputName != null) {
        translator.compile(dir,outputName)
    }

}