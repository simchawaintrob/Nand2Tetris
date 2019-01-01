

import java.io.File

class CompilationEngine(outputFile: File, input:File) {
    val listOfKeyWords=listOf<String>("class", "method","function","constructor","int","boolean","char","void","var","static","field","let","do","if","else","while","return","true","false","null","this")
    val listOfKeyWordsConstant= listOf<String>("true","false","null","this")
    val listOfOp= listOf<String>("+","-","*","/","&amp;","|","&lt;","&gt;","=")
    var parsedFile:File
    var tokensFile:File
    var index:Int
    var tokens:List<String>
    var tab:String="  "
    var countTab:Int=0
    init {
        parsedFile=outputFile
        tokensFile=input
        index=0
        tokens=tokensFile.readLines()
        tokens=tokens.drop(1)
        tokens=tokens.dropLast(1)

    }

    private fun getNextToken():String{
        index=index.inc()
        return tokens[index-1]+'\n'

    }

    private fun checkTypeOfNextToken():String{
        var token=tokens[index]
        token=token.dropWhile { c->c!='<' }
        token=token.drop(1)
        token=token.takeWhile { c->c!='>' }
        return token
    }

    private fun checkNextNextToken():String{
        var token=tokens[index+1]
        token=token.dropWhile { c->c!='>' }
        token=token.drop(1)
        token=token.takeWhile { c->c!='<' }
        token=token.filter { !it.equals(' ') }
        return token
    }


    private fun checkNextToken():String{
        var token=tokens[index]
        token=token.dropWhile { c->c!='>' }
        token=token.drop(1)
        token=token.takeWhile { c->c!='<' }
        token=token.filter { !it.equals(' ') }
        return token
    }

    public fun parseClass(){

        parsedFile.appendText("<class>\n")

        parsedFile.appendText("$tab${getNextToken()}")//class

        parsedFile.appendText("$tab${getNextToken()}")//class name

        parsedFile.appendText("$tab${getNextToken()}")//{

        parseClassVarDec()
        parseSubDec()

        parsedFile.appendText("$tab${getNextToken()}")
        parsedFile.appendText("</class>\n")
    }

    private fun parseClassVarDec(){
        while(index<tokens.lastIndex&&checkNextToken().equals("static")||index<tokens.lastIndex&&checkNextToken().equals("field")){
            parsedFile.appendText("$tab<classVarDec>\n")

            tab+="  "

            parsedFile.appendText("$tab${getNextToken()}")//static|field

            parsedFile.appendText("$tab${getNextToken()}")//type

            parsedFile.appendText("$tab${getNextToken()}")//name
            while(index<tokens.lastIndex&&checkNextToken().equals(",")){

                parsedFile.appendText("$tab${getNextToken()}")//,

                parsedFile.appendText("$tab${getNextToken()}")//name

            }

            parsedFile.appendText("$tab${getNextToken()}")//;

            tab = tab.substring(0, tab.length - 2);
            parsedFile.appendText("$tab</classVarDec>\n")


        }

    }

    private fun parseSubDec(){
        while(index<tokens.lastIndex&&checkNextToken().equals("constructor")||index<tokens.lastIndex&&checkNextToken().equals("function")||index<tokens.lastIndex&&checkNextToken().equals("method")){
            parsedFile.appendText("$tab<subroutineDec>\n")
            tab+="  "

            parsedFile.appendText("$tab${getNextToken()}")//subroutine declaration
            parseType()

            parsedFile.appendText("$tab${getNextToken()}")//name

            parsedFile.appendText("$tab${getNextToken()}")//(
            parseParameterList()

            parsedFile.appendText("$tab${getNextToken()}")//)
            parseSubBody()
            tab = tab.substring(0, tab.length - 2);
            parsedFile.appendText("$tab</subroutineDec>\n")

        }

    }

    private fun parseType(){

        parsedFile.appendText("$tab${getNextToken()}")//type

    }

    private fun parseParameterList(){
        parsedFile.appendText("$tab<parameterList>\n")
        tab+="  "
        if(!(index<tokens.lastIndex&&checkNextToken().equals(")"))){
            parseType()

            parsedFile.appendText("$tab${getNextToken()}")//varName
            while(index<tokens.lastIndex&&checkNextToken().equals(",")){

                parsedFile.appendText("$tab${getNextToken()}")//,
                parseType()

                parsedFile.appendText("$tab${getNextToken()}")//varName
            }

            //parsedFile.appendText(getNextToken())//)
        }
        tab = tab.substring(0, tab.length - 2);
        parsedFile.appendText("$tab</parameterList>\n")

    }

    private fun parseSubBody(){
        parsedFile.appendText("$tab<subroutineBody>\n")
        countTab++
        tab+="  "
        parsedFile.appendText("$tab${getNextToken()}")//{
        while(index<tokens.lastIndex&&checkNextToken().equals("var"))
            parseVarDec()
        parseStatements()
        parsedFile.appendText("$tab${getNextToken()}")//}
        countTab--
        tab = tab.substring(0, tab.length - 2);
        parsedFile.appendText(("$tab</subroutineBody>\n"))
    }

    private fun parseVarDec(){
        parsedFile.appendText("$tab<varDec>\n")
        tab+="  "
        parsedFile.appendText("$tab${getNextToken()}")//var
        parseType()
        parsedFile.appendText("$tab${getNextToken()}")//varName
        while(index<tokens.lastIndex&&checkNextToken().equals(",")){
            parsedFile.appendText("$tab${getNextToken()}")//,
            parsedFile.appendText("$tab${getNextToken()}")//varName

        }
        parsedFile.appendText("$tab${getNextToken()}")//;

        tab = tab.substring(0, tab.length - 2);
        parsedFile.appendText("$tab</varDec>\n")
    }

    private fun parseStatements(){
        parsedFile.appendText("$tab<statements>\n")
        tab+="  "
        while(!(index<tokens.lastIndex&&checkNextToken().equals("}"))){
            parseStatement()
        }
        tab = tab.substring(0, tab.length - 2);
        parsedFile.appendText("$tab</statements>\n")

    }

    private fun parseStatement(){

        if(index<tokens.lastIndex&&checkNextToken().equals("let"))
            parseLetStatement()
        else if(index<tokens.lastIndex&&checkNextToken().equals("if"))
            parseIfStatement()
        else if(index<tokens.lastIndex&&checkNextToken().equals("while"))
            parseWhileStatement()
        else if(index<tokens.lastIndex&&checkNextToken().equals("do"))
            parseDoStatement()
        else
            parseReturnStatement()

    }

    private fun parseReturnStatement() {
        parsedFile.appendText("$tab<returnStatement>\n")
        tab+="  "
        parsedFile.appendText("$tab${getNextToken()}")//return
        if(!(index<tokens.lastIndex&&checkNextToken().equals(";")))
            parseExpression()
        parsedFile.appendText("$tab${getNextToken()}")//;
        tab = tab.substring(0, tab.length - 2);
        parsedFile.appendText("$tab</returnStatement>\n")
    }

    private fun parseExpression() {
        parsedFile.appendText("$tab<expression>\n")
        tab+="  "
        parseTerm()
        while(index<tokens.lastIndex&&checkNextToken() in listOfOp)
        {
            parsedFile.appendText("$tab${getNextToken()}")//op
            parseTerm()
        }
        tab = tab.substring(0, tab.length - 2);
        parsedFile.appendText("$tab</expression>\n")
    }

    private fun parseTerm() {
        parsedFile.appendText("$tab<term>\n")
        tab+="  "
        if(index<tokens.lastIndex&&checkTypeOfNextToken()=="integerConstant")
            parsedFile.appendText("$tab${getNextToken()}")//integerConstant
        else if(index<tokens.lastIndex&&checkNextToken() in listOfKeyWordsConstant){
            parsedFile.appendText("$tab${getNextToken()}")//keyword
        }
        else if(index<tokens.lastIndex&&checkNextToken().equals("(")){
            parsedFile.appendText("$tab${getNextToken()}")//(
            parseExpression()
            parsedFile.appendText("$tab${getNextToken()}")//)
        }
        else if(index<tokens.lastIndex&&checkNextToken().equals("-")||index<tokens.lastIndex&&checkNextToken().equals("~")){
            parsedFile.appendText("$tab${getNextToken()}")//unaryOp
            parseTerm()
        }
        else if(index<tokens.lastIndex&&checkTypeOfNextToken().equals("stringConstant")){
            parsedFile.appendText("$tab${getNextToken()}")//string
        }
        else if(index<tokens.lastIndex&& checkNextNextToken().equals("[")){
            parsedFile.appendText("$tab${getNextToken()}")//varName
            parsedFile.appendText("$tab${getNextToken()}")//[
            parseExpression()
            parsedFile.appendText("$tab${getNextToken()}")//]
        }
        else if(index<tokens.lastIndex&&checkNextNextToken().equals("(")||index<tokens.lastIndex&&checkNextNextToken().equals("."))
            parseSubCall()
        else
            parsedFile.appendText("$tab${getNextToken()}")//varName
        tab = tab.substring(0, tab.length - 2);
        parsedFile.appendText("$tab</term>\n")

    }

    private fun parseSubCall() {
        if(index<tokens.lastIndex&&checkNextNextToken().equals("(")){
            parsedFile.appendText("$tab${getNextToken()}")//subroutineNme
            parsedFile.appendText("$tab${getNextToken()}")//(
            parseExpressionList()
            parsedFile.appendText("$tab${getNextToken()}")//)
        }
        else{
            parsedFile.appendText("$tab${getNextToken()}")//className||varName
            parsedFile.appendText("$tab${getNextToken()}")//.
            parsedFile.appendText("$tab${getNextToken()}")//subroutineName
            parsedFile.appendText("$tab${getNextToken()}")//(
            parseExpressionList()
            parsedFile.appendText("$tab${getNextToken()}")//)
        }

    }

    private fun parseExpressionList() {
        parsedFile.appendText("$tab<expressionList>\n")
        tab+="  "
        if(!(index<tokens.lastIndex&&checkNextToken().equals(")"))){
            parseExpression()
            while ((index<tokens.lastIndex&&checkNextToken().equals(","))){
                parsedFile.appendText("$tab${getNextToken()}")//,
                parseExpression()
            }
        }
        tab = tab.substring(0, tab.length - 2);
        parsedFile.appendText("$tab</expressionList>\n")
    }

    private fun parseDoStatement() {
        parsedFile.appendText("$tab<doStatement>\n")
        tab+="  "
        parsedFile.appendText("$tab${getNextToken()}")//do
        parseSubCall()
        parsedFile.appendText("$tab${getNextToken()}")//;
        tab = tab.substring(0, tab.length - 2);
        parsedFile.appendText("$tab</doStatement>\n")
    }

    private fun parseWhileStatement() {
        parsedFile.appendText("$tab<whileStatement>\n")
        tab+="  "
        parsedFile.appendText("$tab${getNextToken()}")//while
        parsedFile.appendText("$tab${getNextToken()}")//(
        parseExpression()
        parsedFile.appendText("$tab${getNextToken()}")//)
        parsedFile.appendText("$tab${getNextToken()}")//{
        parseStatements()
        parsedFile.appendText("$tab${getNextToken()}")//}
        tab = tab.substring(0, tab.length - 2);
        parsedFile.appendText("$tab</whileStatement>\n")
    }

    private fun parseIfStatement() {
        parsedFile.appendText("$tab<ifStatement>\n")
        tab+="  "
        parsedFile.appendText("$tab${getNextToken()}")//if
        parsedFile.appendText("$tab${getNextToken()}")//(
        parseExpression()
        parsedFile.appendText("$tab${getNextToken()}")//)
        parsedFile.appendText("$tab${getNextToken()}")//{
        parseStatements()
        parsedFile.appendText("$tab${getNextToken()}")//}
        if(index<tokens.lastIndex&&checkNextToken().equals("else")){
            parsedFile.appendText("$tab${getNextToken()}")//else
            parsedFile.appendText("$tab${getNextToken()}")//{
            parseStatements()
            parsedFile.appendText("$tab${getNextToken()}")//}
        }
        tab = tab.substring(0, tab.length - 2);
        parsedFile.appendText("$tab</ifStatement>\n")
    }

    private fun parseLetStatement() {
        parsedFile.appendText("$tab<letStatement>\n")
        tab+="  "
        parsedFile.appendText("$tab${getNextToken()}")//let
        parsedFile.appendText("$tab${getNextToken()}")//varName
        if(index<tokens.lastIndex&&checkNextToken().equals("["))
        {
            parsedFile.appendText("$tab${getNextToken()}")//[
            parseExpression()
            parsedFile.appendText("$tab${getNextToken()}")//]
        }
        parsedFile.appendText("$tab${getNextToken()}")//=
        parseExpression()
        parsedFile.appendText("$tab${getNextToken()}")//;
        tab = tab.substring(0, tab.length - 2);
        parsedFile.appendText("$tab</letStatement>\n")
    }


}
