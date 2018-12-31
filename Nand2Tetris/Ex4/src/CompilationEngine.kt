
import java.io.File

class CompilationEngine(outputFile: File, input:File) {
    val listOfKeyWords=listOf<String>("class", "method","function","constructor","int","boolean","char","void","var","static","field","let","do","if","else","while","return","true","false","null","this")
    val listOfKeyWordsConstant= listOf<String>("true","false","null","this")
    val listOfOp= listOf<String>("+","-","*","/","&amp;","|","&lt;","&gt;","=")
    var parsedFile:File
    var tokensFile:File
    var index:Int
    var tokens:List<String>
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

        parsedFile.appendText("<class>\n\t")

        parsedFile.appendText(getNextToken())//class

        parsedFile.appendText(getNextToken())//class name

        parsedFile.appendText(getNextToken())//{

        parseClassVarDec()
        parseSubDec()

        parsedFile.appendText(getNextToken())
        parsedFile.appendText("\b\b\b\b</class>\n")
    }

    private fun parseClassVarDec(){
        while(index<tokens.lastIndex&&checkNextToken().equals("static")||index<tokens.lastIndex&&checkNextToken().equals("field")){
            parsedFile.appendText("<classVarDec>\n\t")

            parsedFile.appendText(getNextToken())//static|field

            parsedFile.appendText(getNextToken())//type

            parsedFile.appendText(getNextToken())//name
            while(index<tokens.lastIndex&&checkNextToken().equals(",")){

                parsedFile.appendText(getNextToken())//,

                parsedFile.appendText(getNextToken())//name

            }

            parsedFile.appendText(getNextToken())//;
            parsedFile.appendText("\b\b\b\b</classVarDec>\n")


        }

    }

    private fun parseSubDec(){
        while(index<tokens.lastIndex&&checkNextToken().equals("constructor")||index<tokens.lastIndex&&checkNextToken().equals("function")||index<tokens.lastIndex&&checkNextToken().equals("method")){
            parsedFile.appendText("<subroutineDec>\n\t")

            parsedFile.appendText(getNextToken())//subroutine declaration
            parseType()

            parsedFile.appendText(getNextToken())//name

            parsedFile.appendText(getNextToken())//(
            parseParameterList()

            parsedFile.appendText(getNextToken())//)
            parseSubBody()
            parsedFile.appendText("\b\b\b\b</subroutineDec>\n")

        }

    }

    private fun parseType(){

        parsedFile.appendText(getNextToken())//type

    }

    private fun parseParameterList(){
        parsedFile.appendText("<parameterList>\n\t")
        if(!(index<tokens.lastIndex&&checkNextToken().equals(")"))){
            parseType()

            parsedFile.appendText(getNextToken())//varName
            while(index<tokens.lastIndex&&checkNextToken().equals(",")){

                parsedFile.appendText(getNextToken())//,
                parseType()

                parsedFile.appendText(getNextToken())//varName
            }

            //parsedFile.appendText(getNextToken())//)
        }
        parsedFile.appendText("\b\b\b\b</parameterList>\n")

    }

    private fun parseSubBody(){
        parsedFile.appendText("<subroutineBody>\n\t")
        parsedFile.appendText(getNextToken())//{
        while(index<tokens.lastIndex&&checkNextToken().equals("var"))
            parseVarDec()
        parseStatements()
        parsedFile.appendText(getNextToken())//}
        parsedFile.appendText(("\b\b\b\b</subroutineBody>\n"))
    }

    private fun parseVarDec(){
        parsedFile.appendText("<varDec>\n\t")
        parsedFile.appendText(getNextToken())//var
        parseType()
        parsedFile.appendText(getNextToken())//varName
        while(index<tokens.lastIndex&&checkNextToken().equals(",")){
            parsedFile.appendText(getNextToken())//,
            parsedFile.appendText(getNextToken())//varName

        }
        parsedFile.appendText(getNextToken())//;
        parsedFile.appendText("\b\b\b\b</varDec>\n")
    }

    private fun parseStatements(){
        parsedFile.appendText("<statements>\n\t")
        while(!(index<tokens.lastIndex&&checkNextToken().equals("}"))){
            parseStatement()
        }
        parsedFile.appendText("\b\b\b\b</statements>\n")

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
        parsedFile.appendText("<returnStatement>\n\t")
        parsedFile.appendText(getNextToken())//return
        if(!(index<tokens.lastIndex&&checkNextToken().equals(";")))
            parseExpression()
        parsedFile.appendText(getNextToken())//;
        parsedFile.appendText("\b\b\b\b</returnStatement>\n")
    }

    private fun parseExpression() {
        parsedFile.appendText("<expression>\n\t")
        parseTerm()
        while(index<tokens.lastIndex&&checkNextToken() in listOfOp)
        {
            parsedFile.appendText(getNextToken())//op
            parseTerm()
        }
        parsedFile.appendText("\b\b\b\b</expression>\n")
    }

    private fun parseTerm() {
        parsedFile.appendText("<term>\n\t")
        if(index<tokens.lastIndex&&checkTypeOfNextToken()=="integerConstant")
            parsedFile.appendText(getNextToken())//integerConstant
        else if(index<tokens.lastIndex&&checkNextToken() in listOfKeyWordsConstant){
            parsedFile.appendText(getNextToken())//keyword
        }
        else if(index<tokens.lastIndex&&checkNextToken().equals("(")){
            parsedFile.appendText(getNextToken())//(
            parseExpression()
            parsedFile.appendText(getNextToken())//)
        }
        else if(index<tokens.lastIndex&&checkNextToken().equals("-")||index<tokens.lastIndex&&checkNextToken().equals("~")){
            parsedFile.appendText(getNextToken())//unaryOp
            parseTerm()
        }
        else if(index<tokens.lastIndex&&checkTypeOfNextToken().equals("stringConstant")){
            parsedFile.appendText(getNextToken())//string
        }
        else if(index<tokens.lastIndex&& checkNextNextToken().equals("[")){
            parsedFile.appendText(getNextToken())//varName
            parsedFile.appendText(getNextToken())//[
            parseExpression()
            parsedFile.appendText(getNextToken())//]
        }
        else if(index<tokens.lastIndex&&checkNextNextToken().equals("(")||index<tokens.lastIndex&&checkNextNextToken().equals("."))
            parseSubCall()
        else
            parsedFile.appendText(getNextToken())//varName
        parsedFile.appendText("\b\b\b\b</term>\n")

    }

    private fun parseSubCall() {
        if(index<tokens.lastIndex&&checkNextNextToken().equals("(")){
            parsedFile.appendText(getNextToken())//subroutineNme
            parsedFile.appendText(getNextToken())//(
            parseExpressionList()
            parsedFile.appendText(getNextToken())//)
        }
        else{
            parsedFile.appendText(getNextToken())//className||varName
            parsedFile.appendText(getNextToken())//.
            parsedFile.appendText(getNextToken())//subroutineName
            parsedFile.appendText(getNextToken())//(
            parseExpressionList()
            parsedFile.appendText(getNextToken())//)
        }

    }

    private fun parseExpressionList() {
        parsedFile.appendText("<expressionList>\n\t")
        if(!(index<tokens.lastIndex&&checkNextToken().equals(")"))){
            parseExpression()
            while ((index<tokens.lastIndex&&checkNextToken().equals(","))){
                parsedFile.appendText(getNextToken())//,
                parseExpression()
            }
        }
        parsedFile.appendText("\b\b\b\b</expressionList>\n")
    }

    private fun parseDoStatement() {
        parsedFile.appendText("<doStatement>\n\t")
        parsedFile.appendText(getNextToken())//do
        parseSubCall()
        parsedFile.appendText(getNextToken())//;
        parsedFile.appendText("\b\b\b\b</doStatement>\n")
    }

    private fun parseWhileStatement() {
        parsedFile.appendText("<whileStatement>\n\t")
        parsedFile.appendText(getNextToken())//while
        parsedFile.appendText(getNextToken())//(
        parseExpression()
        parsedFile.appendText(getNextToken())//)
        parsedFile.appendText(getNextToken())//{
        parseStatements()
        parsedFile.appendText(getNextToken())//}
        parsedFile.appendText("\b\b\b\b</whileStatement>\n")
    }

    private fun parseIfStatement() {
        parsedFile.appendText("<ifStatement>\n\t")
        parsedFile.appendText(getNextToken())//if
        parsedFile.appendText(getNextToken())//(
        parseExpression()
        parsedFile.appendText(getNextToken())//)
        parsedFile.appendText(getNextToken())//{
        parseStatements()
        parsedFile.appendText(getNextToken())//}
        if(index<tokens.lastIndex&&checkNextToken().equals("else")){
            parsedFile.appendText(getNextToken())//else
            parsedFile.appendText(getNextToken())//{
            parseStatements()
            parsedFile.appendText(getNextToken())//}
        }
        parsedFile.appendText("\b\b\b\b</ifStatement>\n")
    }

    private fun parseLetStatement() {
        parsedFile.appendText("<letStatement>\n\t")
        parsedFile.appendText(getNextToken())//let
        parsedFile.appendText(getNextToken())//varName
        if(index<tokens.lastIndex&&checkNextToken().equals("["))
        {
            parsedFile.appendText(getNextToken())//[
            parseExpression()
            parsedFile.appendText(getNextToken())//]
        }
        parsedFile.appendText(getNextToken())//=
        parseExpression()
        parsedFile.appendText(getNextToken())//;
        parsedFile.appendText("\b\b\b\b</letStatement>\n")
    }


}
