package exe5
import java.io.File

var whileLabelCounter=0
var ifLabelCounter=0
class Statements(parse_file: File, tokens_file: File) : Parsing(parse_file, tokens_file) {
    fun buildStatements() {
        //parse_file.appendText("//buildStatements\n")
        while (index <tokensOfFile.lastIndex && valueOfToken()!="}"){
            buildStatement()
        }


    }

    private fun buildStatement() {
        //parse_file.appendText("//buildStatement\n")
        if (index <tokensOfFile.lastIndex){
            when(valueOfToken()){
                "let"->buildLetStatement()
                "if"->{
                    buildIfStatement()
                }

                "while"->buildWhileStatement()
                "do"->buildDoStatement()
                "return"->buildReturnStatement()
            }
        }
    }

    private fun buildReturnStatement() {
        //parse_file.appendText("//buildReturnStatement\n")
        verifyAndNextToken(1)// return
        if (index <tokensOfFile.lastIndex && valueOfToken()!=";"){
            Expressions(parse_file, tokens_file).buildExpression()
            parse_file.appendText("return\n")
        }
        else
            parse_file.appendText("""
                push constant 0
                return

            """.trimIndent())//push constant 0
        verifyAndNextToken(1)// ;


    }

    private fun buildDoStatement() {
        //parse_file.appendText("//buildDoStatement\n")
        verifyAndNextToken(1)//do
        Expressions(parse_file, tokens_file).buildSubroutineCall()
        verifyAndNextToken(1)// ;
        parse_file.appendText("pop temp 0\n")


    }

    private fun buildWhileStatement() {
        //parse_file.appendText("//buildWhileStatement\n")

        verifyAndNextToken(2)// while (
        var CurrentCounter= whileLabelCounter

        whileLabelCounter++
        parse_file.appendText("label WHILE_EXP"+ CurrentCounter+"\n")
        Expressions(parse_file, tokens_file).buildExpression()
        verifyAndNextToken(2)// ) {
        parse_file.appendText("""
            not
            if-goto WHILE_END$CurrentCounter

        """.trimIndent())
        buildStatements()
        verifyAndNextToken(1)//}
        parse_file.appendText("""
            goto WHILE_EXP$CurrentCounter
            label WHILE_END$CurrentCounter

        """.trimIndent())

    }

    private fun buildIfStatement() {
        //parse_file.appendText("//buildIfStatement\n")
        var CurrentCounter= ifLabelCounter
        ifLabelCounter++
        verifyAndNextToken(2)// if (
        Expressions(parse_file, tokens_file).buildExpression()
        verifyAndNextToken(2)// ) {
        parse_file.appendText("""
            if-goto IF_TRUE$CurrentCounter
            goto IF_FALSE$CurrentCounter
            label IF_TRUE$CurrentCounter

        """.trimIndent())
        buildStatements()
        verifyAndNextToken(1)// }
        if(index <tokensOfFile.lastIndex && valueOfToken()=="else"){
            parse_file.appendText("""
                goto IF_END$CurrentCounter
                label IF_FALSE$CurrentCounter

            """.trimIndent())
            verifyAndNextToken(2)//else {
            buildStatements()
            verifyAndNextToken(1)//}
            parse_file.appendText("label IF_END"+ CurrentCounter+"\n")
        }
        else
            parse_file.appendText("label IF_FALSE$CurrentCounter\n")


    }

    private fun buildLetStatement() {
        //parse_file.appendText("//buildLetStatement\n")
        verifyAndNextToken(1)// let
        var name=valueOfToken()
        verifyAndNextToken(1)//varName
        var row= subroutineSymbolTable.firstOrNull { it._name==name }
        if(row==null)
            row= classSymbolTable.firstOrNull { it._name==name }
        if (index <tokensOfFile.lastIndex && valueOfToken()=="["){
            verifyAndNextToken(1)//[
            Expressions(parse_file, tokens_file).buildExpression()
            verifyAndNextToken(1)// ]
            when (row!!._segment) {
                "var"->parse_file.appendText("push local ${row._index}\n")
                "argument"->parse_file.appendText("push argument ${row._index}\n")
                "field"->parse_file.appendText("push this ${row._index}\n")
                "static"->parse_file.appendText("push static ${row._index}\n")
            }
            parse_file.appendText("add"+"\n")
            verifyAndNextToken(1)//=
            Expressions(parse_file, tokens_file).buildExpression()
            parse_file.appendText("""
                pop temp 0
                pop pointer 1
                push temp 0
                pop that 0

            """.trimIndent())
            verifyAndNextToken(1)//;

        }
        else {
            verifyAndNextToken(1)// =
            Expressions(parse_file, tokens_file).buildExpression()
            verifyAndNextToken(1)// ;
            when (row!!._segment) {
                "var"->parse_file.appendText("pop local ${row._index}\n")
                "argument"->parse_file.appendText("pop argument ${row._index}\n")
                "field"->parse_file.appendText("pop this ${row._index}\n")
                "static"->parse_file.appendText("pop static ${row._index}\n")
            }

        }

    }
}