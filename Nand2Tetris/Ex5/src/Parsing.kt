import java.io.File

class Parsing(outputFile: File, input:File) {
    val listOfKeyWords=listOf<String>("class", "method","function","constructor","int","boolean","char","void","var","static","field","let","do","if","else","while","return","true","false","null","this")
    val listOfKeyWordsConstant= listOf<String>("true","false","null","this")
    val listOfOp= listOf<String>("+","-","*","/","&amp;","|","&lt;","&gt;","=")
    var parsedFile:File
    var tokensFile:File
    var index:Int
    var iflabelCounter:Int=0
    var whilelabelCounter:Int=0

    var tokens:List<String>
    var onlyTokens= arrayListOf<String>()
    var Class_scope_symbol_table= arrayListOf<Row>()
    var Method_scope= arrayListOf<Row>()
    var varCounter:Int=0
    lateinit var className:String

    init {
        parsedFile=outputFile
        tokensFile=input
        index=0

        tokens=tokensFile.readLines()
        tokens=tokens.drop(1)
        tokens=tokens.dropLast(1)
        var tempTokens=tokens
        tempTokens.forEach {
            var l=it
            l= l.dropWhile { c->c!='>' }
            l=l.drop(1)
            l=l.dropLastWhile { c->c!='<' }
            l=l.dropLast(1)
            l=l.dropWhile { c->c==' ' }
            l=l.dropLastWhile { c->c==' ' }
            onlyTokens.add(l)
        }



    }

    private fun getTrueToken():String{
        index=index.inc()
        return onlyTokens[index-1]

    }

    private fun getNextToken():String{
        index=index.inc()
        return tokens[index-1]+'\n'

    }
    private fun moveToNextToken(){
        index=index.inc()
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
        /*parsedFile.appendText("<class>\n")

        parsedFile.appendText(getNextToken())//class

        parsedFile.appendText(getNextToken())//class name

        parsedFile.appendText(getNextToken())//{

        parseClassVarDec()
        parseSubDec()

        parsedFile.appendText(getNextToken())
        parsedFile.appendText("</class>\n")*/

        moveToNextToken()
        className=getTrueToken()
        moveToNextToken()//'{'
        parseClassVarDec()//adds the class variables into the symbol table
        parseSubDec()
        moveToNextToken()//'}'


    }

    private fun parseClassVarDec(){
        /*while(index<tokens.lastIndex&&checkNextToken().equals("static")||index<tokens.lastIndex&&checkNextToken().equals("field")){
            parsedFile.appendText("<classVarDec>\n")

            parsedFile.appendText(getNextToken())//static|field

            parsedFile.appendText(getNextToken())//type

            parsedFile.appendText(getNextToken())//name
            while(index<tokens.lastIndex&&checkNextToken().equals(",")){

                parsedFile.appendText(getNextToken())//,

                parsedFile.appendText(getNextToken())//name

            }

            parsedFile.appendText(getNextToken())//;
            parsedFile.appendText("</classVarDec>\n")


        }*/
        var staticCounter:Int=0
        var fieldCounter:Int=0


        while(index<tokens.lastIndex&&checkNextToken().equals("static")||index<tokens.lastIndex&&checkNextToken().equals("field")){
            if(checkNextToken().equals("static")){
                moveToNextToken()//'static'
                var type:String=getTrueToken()
                var name:String=getTrueToken()
                var row=Row(name,type,"static", staticCounter)
                staticCounter=staticCounter.inc()
                Class_scope_symbol_table.add(row)
                while(index<tokens.lastIndex&&checkNextToken().equals(",")){
                    moveToNextToken()//','
                    name=getTrueToken()
                    row=Row(name, type, "static",staticCounter)
                    staticCounter=staticCounter.inc()
                    Class_scope_symbol_table.add(row)
                }

            }
            else{
                moveToNextToken()//'field'
                var type:String=getTrueToken()
                var name:String=getTrueToken()
                var row=Row(name,type,"field", fieldCounter)
                fieldCounter=fieldCounter.inc()
                Class_scope_symbol_table.add(row)
                while(index<tokens.lastIndex&&checkNextToken().equals(",")){
                    moveToNextToken()//','
                    name=getTrueToken()
                    row=Row(name, type, "field",fieldCounter)
                    fieldCounter=fieldCounter.inc()
                    Class_scope_symbol_table.add(row)
                }
            }
            moveToNextToken()//';'

        }


    }

    private fun parseSubDec(){
        /*while(index<tokens.lastIndex&&checkNextToken().equals("constructor")||index<tokens.lastIndex&&checkNextToken().equals("function")||index<tokens.lastIndex&&checkNextToken().equals("method")){
            parsedFile.appendText("<subroutineDec>\n")

            parsedFile.appendText(getNextToken())//subroutine declaration
            parseType()

            parsedFile.appendText(getNextToken())//name

            parsedFile.appendText(getNextToken())//(
            parseParameterList()

            parsedFile.appendText(getNextToken())//)
            parseSubBody()
            parsedFile.appendText("</subroutineDec>\n")

        }*/

        while(index<tokens.lastIndex&&checkNextToken().equals("constructor")||index<tokens.lastIndex&&checkNextToken().equals("function")||index<tokens.lastIndex&&checkNextToken().equals("method")){
            var argumentCounter:Int=0
            varCounter=0
            Method_scope.clear()//initializate the methoפשרd scope table


            if(checkNextToken().equals("method")){
                val typeOfSubroutine:String=getTrueToken()//'method'
                moveToNextToken()//type of method
                val name:String=getTrueToken()//name of method
                moveToNextToken()//'('
                Method_scope.add(Row("this", className, "argument",argumentCounter))
                argumentCounter=argumentCounter.inc()
                parseParameterList(argumentCounter)
                moveToNextToken()//')'
                moveToNextToken()//{
                while(index<tokens.lastIndex&&checkNextNextToken().equals("var"))
                {

                    //parseVarDec(varCounter)
                    parseVarDec()
                }

                var n:Int=0
                Method_scope.forEach { if(it.Kind.equals("var")) n=n.inc()}
                parsedFile.appendText("function "+className+"."+name+" "+n+'\n')

                parseSubBody(varCounter,typeOfSubroutine )


            }
            else if(checkNextToken().equals("constructor")){
                val typeOfSubroutine:String=getTrueToken()//'constructor'
                moveToNextToken()//type of method
                val name:String=getTrueToken()//name of constructor
                moveToNextToken()//'('

                parseParameterList(argumentCounter)
               //moveToNextToken()//')'
                moveToNextToken()//{
                while(index<tokens.lastIndex&&checkNextNextToken().equals("var"))
                {

                    //parseVarDec(varCounter)
                    parseVarDec()
                }

                var n:Int=0
                Method_scope.forEach { if(it.Kind.equals("var")) n=n.inc()}
                parsedFile.appendText("function "+className+"."+name+" "+n+'\n')

                parseSubBody(varCounter, typeOfSubroutine)
            }
            else{//function
                val typeOfSubroutine:String=getTrueToken()//'function'
                moveToNextToken()//type of method
                val name:String=getTrueToken()//name of method
                moveToNextToken()//'('

                parseParameterList(argumentCounter)
               moveToNextToken()//')'
                moveToNextToken()//{
                while(index<tokens.lastIndex&&checkNextToken().equals("var"))
                {

                    //parseVarDec(varCounter)
                    parseVarDec()
                }
                var n:Int=0
                Method_scope.forEach { if(it.Kind.equals("var")) n=n.inc()}
                parsedFile.appendText("function "+className+"."+name+" "+n+'\n')
                parseSubBody(varCounter, typeOfSubroutine)

            }


        }

    }

    private fun parseType(){

        parsedFile.appendText(getNextToken())//type

    }

    private fun parseParameterList(counter: Int) {
        /*parsedFile.appendText("<parameterList>\n")
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
        parsedFile.appendText("</parameterList>\n")*/
        var argumentCounter:Int=counter
        if(!(index<tokens.lastIndex&&checkNextToken().equals(")"))){
            var type:String=getTrueToken()
            var name:String=getTrueToken()
            var row:Row=Row(name,type,"argument",argumentCounter)
            Method_scope.add(row)
            argumentCounter=argumentCounter.inc()
            while(index<tokens.lastIndex&&checkNextToken().equals(",")){
                moveToNextToken()//','
                type=getTrueToken()
                name=getTrueToken()
                row=Row(name,type,"argument",argumentCounter)
                Method_scope.add(row)
                argumentCounter=argumentCounter.inc()
            }
            //moveToNextToken()//')'
        }

    }

    private fun parseSubBody(varCounter: Int, typeOfSubroutine: String) {
        /*parsedFile.appendText("<subroutineBody>\n")
        parsedFile.appendText(getNextToken())//{
        while(index<tokens.lastIndex&&checkNextToken().equals("var"))
            parseVarDec()
        parseStatements()
        parsedFile.appendText(getNextToken())//}
        parsedFile.appendText(("</subroutineBody>\n"))*/

        //moveToNextToken()//'{'
        while(index<tokens.lastIndex&&checkNextToken().equals("var"))
            //parseVarDec(varCounter)
            parseVarDec()
        if(typeOfSubroutine.equals("constructor")){
            var n:Int=0
            Class_scope_symbol_table.forEach{if (it.Kind.equals("field")) n=n.inc()}
            parsedFile.appendText("push constant "+n+'\n')
            parsedFile.appendText("call Memory.alloc 1 \n")
            parsedFile.appendText("pop pointer 0 \n")
        }
        else if(typeOfSubroutine.equals("method")){
            parsedFile.appendText("push argument 0 \n")
            parsedFile.appendText("pop pointer 0 \n")
        }
        parseStatements()
        moveToNextToken()//'}'

    }

    private fun parseVarDec() {
        /*parsedFile.appendText("<varDec>\n")
        parsedFile.appendText(getNextToken())//var
        parseType()
        parsedFile.appendText(getNextToken())//varName
        while(index<tokens.lastIndex&&checkNextToken().equals(",")){
            parsedFile.appendText(getNextToken())//,
            parsedFile.appendText(getNextToken())//varName

        }
        parsedFile.appendText(getNextToken())//;
        parsedFile.appendText("</varDec>\n")*/
        moveToNextToken()//'var'
       // var varCounter:Int=counter
        var type:String=getTrueToken()
        var name:String=getTrueToken()
        var row:Row=Row(name, type, "var",varCounter)
        Method_scope.add(row)
        varCounter=varCounter.inc()
        while(index<tokens.lastIndex&&checkNextToken().equals(",")){
            moveToNextToken()//','
            name=getTrueToken()
            var row:Row=Row(name, type, "var",varCounter)
            Method_scope.add(row)
            varCounter=varCounter.inc()
        }
        moveToNextToken()//';'
    }

    private fun parseStatements(){
        /*parsedFile.appendText("<statements>\n")
        while(!(index<tokens.lastIndex&&checkNextToken().equals("}"))){
            parseStatement()
        }
        parsedFile.appendText("</statements>\n")*/
        while(!(index<tokens.lastIndex&&checkNextToken().equals("}")))
            parseStatement()

    }

    private fun parseStatement(){

        if(index<tokens.lastIndex&&checkNextToken().equals("let"))
            parseLetStatement()
        else if(index<tokens.lastIndex&&checkNextToken().equals("if"))
            parseIfStatement()
        else if(index<tokens.lastIndex&&checkNextToken().equals("while"))
            parseWhileStatement()
        else if(index<tokens.lastIndex&&checkNextToken().equals("do"))
        {  parseDoStatement()
            parsedFile.appendText("pop temp 0\n")
        }
        else
            parseReturnStatement()

    }

    private fun parseReturnStatement() {
        /*parsedFile.appendText("<returnStatement>\n")
        parsedFile.appendText(getNextToken())//return
        if(!(index<tokens.lastIndex&&checkNextToken().equals(";")))
            parseExpression()
        parsedFile.appendText(getNextToken())//;
        parsedFile.appendText("</returnStatement>\n")*/

        moveToNextToken()//'return'
        if(!(index<tokens.lastIndex&&checkNextToken().equals(";")))
        { parseExpression()
            parsedFile.appendText("return\n")
        }
        else{
            parsedFile.appendText("push constant 0\n")
            parsedFile.appendText("return\n")
        }
        moveToNextToken()//';
    }

    private fun parseExpression() {
        /*parsedFile.appendText("<expression>\n")
        parseTerm()
        while(index<tokens.lastIndex&&checkNextToken() in listOfOp)
        {
            parsedFile.appendText(getNextToken())//op
            parseTerm()
        }
        parsedFile.appendText("</expression>\n")*/

        parseTerm()
        while(index<tokens.lastIndex&&checkNextToken() in listOfOp){
            if(checkNextToken().equals("+")){
                moveToNextToken()//op
                parseTerm()
                parsedFile.appendText("add\n")
                //moveToNextToken()//op
               // parseTerm()
            }
            else if(checkNextToken().equals("-")){
                moveToNextToken()//op
                parseTerm()
                parsedFile.appendText("sub\n")
                //moveToNextToken()//op
                //parseTerm()
            }
            else if(checkNextToken().equals("*")){
                moveToNextToken()//op
                parseTerm()
                parsedFile.appendText("call Math.multiply 2\n")
               // moveToNextToken()//op
                //parseTerm()
            }
            else if(checkNextToken().equals("/")){
                moveToNextToken()//op
                parseTerm()
                parsedFile.appendText("call Math.divide 2\n")
                //moveToNextToken()//op
               // parseTerm()
            }
            else if(checkNextToken().equals("&amp;")){
                moveToNextToken()//op
                parseTerm()
                parsedFile.appendText("and\n")
                //moveToNextToken()//op
                //parseTerm()
            }
            else if(checkNextToken().equals("|")){
                moveToNextToken()//op
                parseTerm()
                parsedFile.appendText("or\n")
                //moveToNextToken()//op
                //parseTerm()
            }
            else if(checkNextToken().equals("&lt;")){
                moveToNextToken()//op
                parseTerm()
                parsedFile.appendText("lt\n")
                //moveToNextToken()//op
                //parseTerm()
            }
            else if(checkNextToken().equals("&gt;")){
                moveToNextToken()//op
                parseTerm()
                parsedFile.appendText("gt\n")
                //moveToNextToken()//op
                //parseTerm()
            }
            else{
                moveToNextToken()//op
                parseTerm()
                parsedFile.appendText("eq\n")
                //moveToNextToken()//op
               // parseTerm()
            }
        }

    }

    private fun parseTerm() {
       /* parsedFile.appendText("<term>\n")
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
        parsedFile.appendText("</term>\n")*/
        if(index<tokens.lastIndex&&checkTypeOfNextToken()=="integerConstant"){
            parsedFile.appendText("push constant "+getTrueToken()+'\n')
           // moveToNextToken()//integer constant
            //parseTerm()
        }
        else if(index<tokens.lastIndex&&checkNextToken() in listOfKeyWordsConstant){
            if(checkNextToken().equals("true"))
            {parsedFile.appendText("push constant 0 \nnot \n")
                moveToNextToken()//true
                //moveToNextToken()//;
                //parseTerm()
            }
            else if(checkNextToken().equals("null")||checkNextToken().equals("false"))
            {parsedFile.appendText("push constant 0\n")
                moveToNextToken()//nuul||false
                //moveToNextToken()//;
               // parseTerm()
            }
            else{//this
                parsedFile.appendText("push pointer 0\n")
                moveToNextToken()//this
               // moveToNextToken()//;
               // parseTerm()
            }
        }
        else if(index<tokens.lastIndex&&checkNextToken().equals("(")){
            moveToNextToken()//(
            parseExpression()
            moveToNextToken()//)
          //  parseTerm()
        }
        else if(index<tokens.lastIndex&&checkNextToken().equals("-")||index<tokens.lastIndex&&checkNextToken().equals("~")){
            if(checkNextToken().equals("-")){
                moveToNextToken()//-
                parseTerm()
                parsedFile.appendText("neg\n")
                //moveToNextToken()
            }
            else {
                moveToNextToken()//~
                parseTerm()
                parsedFile.appendText("not\n")

                // moveToNextToken()}
            }
            //parseTerm()

        }
        else if(index<tokens.lastIndex&&checkTypeOfNextToken().equals("stringConstant")){
            var Word=getTrueToken()
            var Strlen:Int=Word.length
            parsedFile.appendText("push constant "+Strlen+'\n')
            parsedFile.appendText("call String.new 1 \n")
            var i:Int=0
            while(i<Strlen){
                var ascii:Int=Word[i].toInt()
                parsedFile.appendText("push constant "+ascii+'\n')
                parsedFile.appendText("call String.appendChar 2\n")
                i=i.inc()
            }
        }
        else if(index<tokens.lastIndex&&checkNextNextToken().equals("(")||index<tokens.lastIndex&&checkNextNextToken().equals("."))
            parseSubCall()
        else if(index<tokens.lastIndex&&checkNextNextToken().equals("[")){
            var varName=getTrueToken()//varName
            moveToNextToken()//[
            parseExpression()
            var row=Method_scope.firstOrNull { it.Name==varName}
            if(row==null) row=Class_scope_symbol_table.firstOrNull{it.Name==varName}
            //var varNumber:Int=Method_scope.count { it.Kind=="var" }

            if(row!=null){
                var varNumber:Int=row.SeqNum
                if(row.Kind=="var")
                    parsedFile.appendText("push local "+varNumber+'\n')
                else if(row.Kind=="argument")
                    parsedFile.appendText("push argument "+varNumber+'\n')
                else if(row.Kind=="field")
                    parsedFile.appendText("push this "+varNumber+'\n')
                else
                    parsedFile.appendText("push static "+varNumber+'\n')


            }
            parsedFile.appendText("add\n")
            parsedFile.appendText("pop pointer 1\n")
            parsedFile.appendText("push that 0\n")
            moveToNextToken()//]
        }
        else{//varName
            var varName=getTrueToken()//varName
            var row=Method_scope.firstOrNull { it.Name==varName}
            if(row==null) row=Class_scope_symbol_table.firstOrNull{it.Name==varName}
            //var varNumber:Int=Method_scope.count { it.Kind=="var" }

            if(row!=null){
                var varNumber:Int=row.SeqNum
                if(row.Kind=="var")
                    parsedFile.appendText("push local "+varNumber+'\n')
                else if(row.Kind=="argument")
                    parsedFile.appendText("push argument "+varNumber+'\n')
                else if(row.Kind=="field")
                    parsedFile.appendText("push this "+varNumber+'\n')
                else
                    parsedFile.appendText("push static "+varNumber+'\n')
            }
        }

    }

    private fun parseSubCall() {
       /* if(index<tokens.lastIndex&&checkNextNextToken().equals("(")){
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
        }*/
        if(index<tokens.lastIndex&&checkNextNextToken().equals("(")){
            //val typeOfSubRoutine:String=Method_scope[0].Name//method or not
            //val n:Int=Method_scope.size
            val subName:String=getTrueToken()
            moveToNextToken()//'('
            val n:Int= parseExpressionList()+1
            //if(typeOfSubRoutine.equals("method")){
                parsedFile.appendText("push pointer 0\n")
          //  }
           /* if(typeOfSubRoutine=="this"){

            parsedFile.appendText("call "+className+"."+subName+" "+parseExpressionList()+1+'\n')}
            else
                parsedFile.appendText("call "+className+"."+subName+" "+parseExpressionList()+'\n')*/
            parsedFile.appendText("call "+className+"."+subName+" "+n+'\n')

            moveToNextToken()//')'
        }
        else{
            //val typeOfSubRoutine:String=Method_scope[0].Name//method or not
            //val n:Int=Method_scope.size
            val OtherClassName:String=getTrueToken()//className||varName


            moveToNextToken()//'.'
            val subName:String=getTrueToken()//subroutine name
            moveToNextToken()//'('
            var n:Int=parseExpressionList()
            var varName:Boolean=false
            Class_scope_symbol_table.forEach{if(it.Name==OtherClassName)//method
                varName=true

            }
            if(OtherClassName==className){//current class
                parsedFile.appendText("call "+className+"."+subName+" "+n+'\n')

            }
            else if(varName){

                var row=Class_scope_symbol_table.firstOrNull { it.Name==OtherClassName }
                //var varNumber:Int=Method_scope.count { it.Kind=="var" }

                if(row!=null){
                    var varNumber:Int=row.SeqNum
                    if(row.Kind=="static")
                        parsedFile.appendText("push static "+varNumber+'\n')
                    else
                        parsedFile.appendText("push this "+varNumber+'\n')
                }
            }
            else
            {
                parsedFile.appendText("call "+OtherClassName+"."+subName+" "+n+'\n')
            }



            moveToNextToken()//')'

        }


    }



    private fun parseExpressionList(): Int {
       /* parsedFile.appendText("<expressionList>\n")
        if(!(index<tokens.lastIndex&&checkNextToken().equals(")"))){
            parseExpression()
            while ((index<tokens.lastIndex&&checkNextToken().equals(","))){
                parsedFile.appendText(getNextToken())//,
                parseExpression()
            }
        }
        parsedFile.appendText("</expressionList>\n")*/
        var paramCounter=0

        if(!(index<tokens.lastIndex&&checkNextToken().equals(")"))){//if there are arguments
            paramCounter=paramCounter.inc()

            parseExpression()
            while ((index<tokens.lastIndex&&checkNextToken().equals(","))){//if there are more expressions
                moveToNextToken()//','
                paramCounter=paramCounter.inc()

                parseExpression()
            }
        }
        return paramCounter

    }

    private fun parseDoStatement() {
       /* parsedFile.appendText("<doStatement>\n")
        parsedFile.appendText(getNextToken())//do
        parseSubCall()
        parsedFile.appendText(getNextToken())//;
        parsedFile.appendText("</doStatement>\n")*/
        moveToNextToken()//'do'
        parseSubCall()
        moveToNextToken()//';'
    }

    private fun parseWhileStatement() {
       /* parsedFile.appendText("<whileStatement>\n")
        parsedFile.appendText(getNextToken())//while
        parsedFile.appendText(getNextToken())//(
        parseExpression()
        parsedFile.appendText(getNextToken())//)
        parsedFile.appendText(getNextToken())//{
        parseStatements()
        parsedFile.appendText(getNextToken())//}
        parsedFile.appendText("</whileStatement>\n")*/
        var counter=whilelabelCounter
        whilelabelCounter=whilelabelCounter.inc()

        parsedFile.appendText("label WHILE_EXP"+counter+'\n')
        moveToNextToken()//'while'
        moveToNextToken()//'('
        parseExpression()
        moveToNextToken()//')'
        parsedFile.appendText("not\n")
        parsedFile.appendText("if-goto WHILE_END"+counter+'\n')
        moveToNextToken()//'{'
        parseStatements()
        moveToNextToken()//'}'
        parsedFile.appendText("goto WHILE_EXP"+counter+'\n')
        parsedFile.appendText("label WHILE_END"+counter+'\n')
    }

    private fun parseIfStatement() {
       /* parsedFile.appendText("<ifStatement>\n")
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
        parsedFile.appendText("</ifStatement>\n")*/
        var counter=iflabelCounter
        iflabelCounter=iflabelCounter.inc()

        moveToNextToken()//'if'
        moveToNextToken()//'('
        parseExpression()
        moveToNextToken()//')'
        parsedFile.appendText("if-goto IF_TRUE"+counter+'\n')
        parsedFile.appendText("goto IF_FALSE"+counter+'\n')
        parsedFile.appendText("label IF_TRUE"+counter+'\n')
        moveToNextToken()//'{'
        parseStatements()
        moveToNextToken()//'}'

        if(index<tokens.lastIndex&&checkNextToken().equals("else")){
            parsedFile.appendText("goto IF_END"+counter+'\n')
            parsedFile.appendText("label IF_FALSE"+counter+'\n')
            moveToNextToken()//'else'
            moveToNextToken()//{'
            parseStatements()
            moveToNextToken()//'}'
            parsedFile.appendText("label IF_END"+counter+'\n')
        }
        else
            parsedFile.appendText("label IF_FALSE"+counter+'\n')

    }

    private fun parseLetStatement() {
       /* parsedFile.appendText("<letStatement>\n")
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
        parsedFile.appendText("</letStatement>\n")

    }*/
        moveToNextToken()//let
        var varName:String=getTrueToken()
        if(index<tokens.lastIndex&&checkNextToken().equals("["))
        {
            moveToNextToken()//'['
            parseExpression()
            moveToNextToken()//']'
            var row=Method_scope.firstOrNull { it.Name==varName }
            if(row==null)row=Class_scope_symbol_table.firstOrNull{it.Name==varName}
                //var varNumber:Int=Method_scope.count { it.Kind=="var" }
                if(row!=null){
                    var varNumber:Int=row.SeqNum
                    if(row.Kind=="var"){
                        parsedFile.appendText("push local "+varNumber+'\n')
                    }
                    else if(row.Kind=="argument"){
                        parsedFile.appendText("push argument "+varNumber+'\n')
                    }
                    else if(row.Kind=="field"){
                        parsedFile.appendText("push this "+varNumber+'\n')
                    }
                    else{
                        parsedFile.appendText("push static "+varNumber+'\n')
                    }
            }

            parsedFile.appendText("add\n")
            moveToNextToken()//=
            parseExpression()
            parsedFile.appendText("pop temp 0\n")
            parsedFile.appendText("pop pointer 0\n")
            parsedFile.appendText("push temp 0\n")
            parsedFile.appendText("pop that 0\n")
            moveToNextToken()//;
        }
        else{
            moveToNextToken()//=
            parseExpression()
            moveToNextToken()//;
            var row=Method_scope.firstOrNull { it.Name==varName }
            if(row==null)row=Class_scope_symbol_table.firstOrNull{it.Name==varName}
           // var varNumber:Int=Method_scope.count { it.Kind=="var" }

            if(row!=null){
                var varNumber:Int=row.SeqNum
                if(row.Kind=="var"){
                    parsedFile.appendText("pop local "+varNumber+'\n')
                }
                else if(row.Kind=="argument"){
                    parsedFile.appendText("pop argument "+varNumber+'\n')
                }
                else if(row.Kind=="field"){
                    parsedFile.appendText("pop this "+varNumber+'\n')
                }
                else{
                    parsedFile.appendText("pop static "+varNumber+'\n')
                }
            }

        }
       // moveToNextToken()//;
    }

}