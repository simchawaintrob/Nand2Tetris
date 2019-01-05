import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths



class Tokenizing(outputFile:File, input:String) {
    val listOfTokenTypes= listOf<String>("KEYWORD", "SYMBOL", "INT_CONST","STRING_CONST", "IDENTIFIER")
    //val listOfKeyWords= listOf<String>("CLASS", "METHOD", "FUNCTION", "CONSTRUCTOR", "INT", "BOOLEAN", "CHAR", "VOID", "VAR", "STATIC", "FIELD", "LET", "DO", "IF", "ELSE", "WHILE", "RETURN", "TRUE", "FALSE", "NULL", "THIS")
    val listOfKeyWords=listOf<String>("class", "method","function","constructor","int","boolean","char","void","var","static","field","let","do","if","else","while","return","true","false","null","this")
    val listOfSymbols=listOf<Char>('{','}','(',')','[',']','.',',',';','+','-','*','/','&','|','<','>','=','~')
    val listOfChars=listOf<Char>('A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z')
    var token:String
    var firstchr:Char
    var xmlFile:File
    var xmlString:String
    init {
        this.token=""
        firstchr='a'
        xmlFile=outputFile
        xmlFile.appendText("<tokens>\n")
        xmlString=input
    }

    public fun Q0(){
        if(xmlString.isEmpty()) {
            finish()
        }
        else{ firstchr=xmlString[0]


            if(firstchr=='"') {
                xmlString= xmlString.drop(1)

                Q5()
            }
            else if(firstchr in listOfSymbols){
                xmlString=xmlString.drop(1)
                token=firstchr.toString()
                Q4()
            }
            else if(firstchr.isDigit()){
                token=firstchr.toString()
                xmlString=xmlString.drop(1)
                Q3()
            }
            else if(firstchr=='_')
            {
                token=firstchr.toString()
                xmlString=xmlString.drop(1)
                Q2()
            }
            else if(firstchr==' ')
            {
                xmlString=xmlString.drop(1)
                Q0()
            }
            else{
                token=firstchr.toString()
                xmlString=xmlString.drop(1)
                Q1()
            }
        }

    }

    private fun Q1(){
        if(xmlString.isEmpty()) {
            finish()
        }
        else{
            firstchr=xmlString[0]

            if(firstchr in listOfChars)
            {  token+=firstchr
                xmlString=xmlString.drop(1)
                if(token in listOfKeyWords)
                {
                    xmlFile.appendText("<keyword> ${token} </keyword>\n")
                    token=""
                    Q0()
                }
                else{
                    Q1()
                }
            }
            else
            {
                Q2()
            }

        }

    }

    private fun Q2(){
        if(xmlString.isEmpty()) {
            finish()
        }
        else{
            firstchr=xmlString[0]

            if(firstchr in listOfChars||firstchr.isDigit()||firstchr=='_')
            {
                token+=firstchr
                xmlString=xmlString.drop(1)
                Q2()
            }
            else
            {
                xmlFile.appendText("<identifier> ${token} </identifier>\n")
                token=""
                Q0()
            }

        }

    }

    private fun Q3(){
        if(xmlString.isEmpty()) {
            finish()
        }
        else{
            firstchr=xmlString[0]

            if(firstchr.isDigit())
            {token+=firstchr
                xmlString=xmlString.drop(1)
                Q3()
            }
            else
            {
                xmlFile.appendText("<integerConstant> ${token} </integerConstant>\n")
                token=""
                Q0()
            }

        }

    }

    private fun Q4(){
//        if(xmlString.isEmpty()) {
//            finish()
//        }
//        else{
        //firstchr=xmlString[0]

        if(token=='>'.toString())
        {
            xmlFile.appendText("<symbol> &gt; </symbol>\n")
            token=""
            Q0()
        }
        else if(token=='<'.toString())
        {
            xmlFile.appendText("<symbol> &lt; </symbol>\n")
            token=""
            Q0()
        }
        else if(token=='&'.toString())
        {
            xmlFile.appendText("<symbol> &amp; </symbol>\n")
            token=""
            Q0()
        }
        else
        {
            xmlFile.appendText("<symbol> ${token} </symbol>\n")
            token=""
            Q0()
        }


        // }

    }

    private fun Q5(){
        if(xmlString.isEmpty()) {
            finish()
        }
        else{ firstchr=xmlString[0]

            if(firstchr=='"')
            {
                xmlFile.appendText("<stringConstant> ${token} </stringConstant>\n")
                token=""
                xmlString=xmlString.drop(1)
                Q0()

            }
            else
            {
                token+=firstchr
                xmlString=xmlString.drop(1)
                Q5()
            }
        }

    }
    fun finish(){
        xmlFile.appendText("</tokens>")
    }

}