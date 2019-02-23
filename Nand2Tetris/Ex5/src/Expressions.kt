package exe5
import java.io.File

val listOfOp= listOf<String>("+","-","*","/","&amp;","|","&lt;","&gt;","=")

class Expressions(parse_file: File, tokens_file: File) : Parsing(parse_file, tokens_file) {

    fun buildExpression() {
        //parse_file.appendText("//buildExpression\n")
        buildTerm()
        while(index <tokensOfFile.lastIndex && valueOfToken() in listOfOp){
            var op=valueOfToken()
            verifyAndNextToken(1)// op
            buildTerm()
            when(op){
                "+"->parse_file.appendText("add\n")
                "-"->parse_file.appendText("sub\n")
                "*"->parse_file.appendText("call Math.multiply 2\n")
                "/"->parse_file.appendText("call Math.divide 2\n")
                "&amp;"->parse_file.appendText("and\n")
                "|"->parse_file.appendText("or\n")
                "&lt;"->parse_file.appendText("lt\n")
                "&gt;"->parse_file.appendText("gt\n")
                "="->parse_file.appendText("eq\n")
            }
        }


    }

    private fun buildTerm() {
        //parse_file.appendText("//buildTerm\n")
        if (index <tokensOfFile.lastIndex){
            when(AllTokens[index].t){
                TokenTypes.integerConstant -> {
                    parse_file.appendText("push constant ${valueOfToken()}\n")
                    verifyAndNextToken(1)//integerConstant
                }
                TokenTypes.stringConstant -> {
                    var word=valueOfToken()
                    parse_file.appendText("""
                        push constant ${word.length}
                        call String.new 1

                    """.trimIndent())
                    for (i in word){
                        parse_file.appendText("""
                            push constant ${i.toInt()}
                            call String.appendChar 2

                        """.trimIndent())
                    }
                    verifyAndNextToken(1)//string
                }
                TokenTypes.keyword ->{
                    when ((AllTokens[index].v))
                    {
                        "true"->parse_file.appendText("""
                            push constant 0
                            not

                        """.trimIndent())
                        "null"->parse_file.appendText("push constant 0\n")
                        "false"->parse_file.appendText("push constant 0\n")
                        "this"->parse_file.appendText("push pointer 0\n")
                    }
                    verifyAndNextToken(1)
                }//keyword}
                TokenTypes.symbol ->{
                    when(AllTokens[index].v) {
                        "-"->{
                            verifyAndNextToken(1)
                            buildTerm()
                            parse_file.appendText("neg\n")
                        }
                        "~"->{
                            verifyAndNextToken(1)
                            buildTerm()
                            parse_file.appendText("not\n")
                        }
                        "("->{
                            verifyAndNextToken(1)//(
                            buildExpression()
                            verifyAndNextToken(1)//)
                        }
                    }
                }
                TokenTypes.identifier ->{
                    var d=0//flag
                    if(index <tokensOfFile.lastIndex-1){
                        when(valueOfTokenByIndex(index +1)){
                            "["->{
                                d=1
                                var n=valueOfToken()//n is tha varname
                                verifyAndNextToken(2)//varName [
                                buildExpression()
                                var row= subroutineSymbolTable.firstOrNull { it._name==n }//check if exist in function or in class
                                if (row==null)
                                    row= classSymbolTable.firstOrNull { it._name==n }
                                when (row!!._segment) {
                                    "var"->parse_file.appendText("push local ${row._index}\n")
                                    "argument"->parse_file.appendText("push argument ${row._index}\n")
                                    "field"->parse_file.appendText("push this ${row._index}\n")
                                    "static"->parse_file.appendText("push static ${row._index}\n")
                                }
                                parse_file.appendText("""
                                    add
                                    pop pointer 1
                                    push that 0

                                """.trimIndent())
                                verifyAndNextToken(1)//]
                            }
                            "("->{
                                d=1
                                buildSubroutineCall()
                            }
                            "."->{
                                d=1
                                buildSubroutineCall()
                            }
                            else-> {//only varname
                                d=1
                                var n=valueOfToken()
                                verifyAndNextToken(1)
                                var row= subroutineSymbolTable.firstOrNull { it._name==n }
                                if (row==null)
                                    row= classSymbolTable.firstOrNull { it._name==n }
                                when (row!!._segment) {
                                    "var"->parse_file.appendText("push local ${row._index}\n")
                                    "argument"->parse_file.appendText("push argument ${row._index}\n")
                                    "field"->parse_file.appendText("push this ${row._index}\n")
                                    "static"->parse_file.appendText("push static ${row._index}\n")
                                }
                            }//varName
                        }
                    }
                    if(d==0) {//if last note
                        var n=valueOfToken()
                        verifyAndNextToken(1)
                        var row = subroutineSymbolTable.firstOrNull { it._name == n }
                        if (row == null)
                            row = classSymbolTable.firstOrNull { it._name == n }
                        when (row!!._segment) {
                            "var" -> parse_file.appendText("push local ${row._index}\n")
                            "argument" -> parse_file.appendText("push argument ${row._index}\n")
                            "field" -> parse_file.appendText("push this ${row._index}\n")
                            "static" -> parse_file.appendText("push static ${row._index}\n")
                        }
                        verifyAndNextToken(1)//varName
                    }
                }
            }

        }


    }

    fun buildSubroutineCall() {
        //parse_file.appendText("//buildSubroutineCall\n")
        if (index <tokensOfFile.lastIndex-1 ){
            var subName:String
            var classOrVar_Name:String
            var n:Int
            var numOfArg=0
            var subroutineFullName:String=""
            when(valueOfTokenByIndex(index +1)){
                "("->{
                    subName=valueOfToken()
                    verifyAndNextToken(2)//subroutineName (
                    parse_file.appendText("push pointer 0\n")//method of mySelf,push pointer to myself
                    n=buildExpressionList()+1//+1 because the pointer
                    verifyAndNextToken(1)//)
                    parse_file.appendText("""
                        call $class_Name.$subName $n

                    """.trimIndent())
                }
                "."->{
                    classOrVar_Name=valueOfToken()
                    verifyAndNextToken(2)// className| varName .
                    subName=valueOfToken()
                    verifyAndNextToken(1)//subroutineName
                    //n=buildExpressionList()
                   // verifyAndNextToken(1)//)
                    var subroutineType:String
                    var subroutineIndex:Int
                    if(subroutineSymbolTable.firstOrNull { it._name==classOrVar_Name }!=null){//method of other class
                        subroutineSymbolTable.forEach{if(it._name==classOrVar_Name){
                            subroutineType=it._type
                            subroutineIndex=it._index
                            when (it._segment) {//argument
                                "var" -> parse_file.appendText("push local $subroutineIndex\n")
                                "argument" -> parse_file.appendText("push argument $subroutineIndex\n")
                                "field" -> parse_file.appendText("push this $subroutineIndex\n")
                                "static" -> parse_file.appendText("push static $subroutineIndex\n")
                            }
                            subroutineFullName=subroutineType+"."+subName
                        }
                        }
                        numOfArg++

                    }
                    else if(classSymbolTable.firstOrNull{it._name==classOrVar_Name}!=null){
                        classSymbolTable.forEach {
                            if(it._name==classOrVar_Name)
                            {
                                subroutineFullName=it._type+"."+subName
                                when (it._segment) {
                                    "var" -> parse_file.appendText("push local ${it._index}\n")
                                    "argument" -> parse_file.appendText("push argument ${it._index}\n")
                                    "field" -> parse_file.appendText("push this ${it._index}\n")
                                    "static" -> parse_file.appendText("push static ${it._index}\n")
                                }
                            }
                        }
                        numOfArg++
                    }
                    else{//if this class, (class_name==classOrVarName) Or other class, not need to push parameter
                        subroutineFullName= classOrVar_Name+"."+subName
                    }


                    verifyAndNextToken(1)//(
                    numOfArg=numOfArg+buildExpressionList()//numOfArgs+1
                    verifyAndNextToken(1)//)
                    parse_file.appendText("call $subroutineFullName $numOfArg\n")


                    //if(classOrVar_Name== class_Name){
                     //   parse_file.appendText("call $class_Name.$subName $n\n")
                    //}
                    //else if(classSymbolTable.firstOrNull { it._name==classOrVar_Name }!= null){
                    //    var row=classSymbolTable.firstOrNull { it._name==classOrVar_Name }
                     //   parse_file.appendText("push ${row!!._segment} ${row!!._index}\n")
                   // }
                   // else{
                    //    parse_file.appendText("call $classOrVar_Name.$subName $n\n")
                    //}
                }
            }
        }
    }

    private fun buildExpressionList() :Int{
        //parse_file.appendText("//buildExpressionList\n")
        var paramCounter=0
        if(index <tokensOfFile.lastIndex && valueOfToken()!=")"){
            buildExpression()
            paramCounter++
            while(index <tokensOfFile.lastIndex && valueOfToken()==","){
                verifyAndNextToken(1)//,
                buildExpression()
                paramCounter++
            }
        }
        return paramCounter

    }
}

