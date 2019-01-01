

import java.io.File


class JackTokenizer (inputJackFilePath :String) {


    var inputJackFilePath : String  = ""//os.File
    var allKeyWords : List<String> = listOf<String>()
    var allSymbols: List<String> = listOf<String>()
    var currentToken:  String = ""
    var fileContent: MutableList<Line> = mutableListOf()
    var lineIndex: Int = 0
    var numOfLines:  Int = 0
    var currentLine: Line = Line()
    var currentWord: String  = ""
    var constString : String  = ""
    var currentWordInLineIndex : Int = 0
    var currentLineIndex : Int = 0
    var currentLineWords = listOf<String>()




    init {

        this.inputJackFilePath = inputJackFilePath
        allKeyWords = listOf<String>(KeyWord.CLASS, KeyWord.METHOD, KeyWord.FUNCTION, KeyWord.CONSTRUCTOR, KeyWord.INT, KeyWord.BOOLEAN,
                KeyWord.CHAR, KeyWord.VOID, KeyWord.VAR, KeyWord.STATIC, KeyWord.FIELD, KeyWord.LET, KeyWord.DO, KeyWord.IF, KeyWord.ELSE,
                KeyWord.WHILE, KeyWord.RETURN, KeyWord.TRUE, KeyWord.FALSE, KeyWord.NULL, KeyWord.THIS)
        allSymbols = listOf<String>("{", "}", "(", ")", "[", "]", ".", ",", ";", "+", "-",
            "*", "/", "&", "|", "<", ">", "=", "~" )
        var byteContent : List<String> = File(this.inputJackFilePath).readLines()



        var lines = byteContent.count()

        //add line number for every line
        var commentedLines = false

        byteContent.forEachIndexed { index, it ->

            var trimLine = it.replace(Regex("\\s")," ")
            if(commentedLines) {
                var comment3 = trimLine.indexOf("*/")
                if (comment3 != -1) {

                    trimLine = trimLine.substring(comment3+2)  //trimLine = trimLine[comment3+2:]
                    commentedLines = false;
                } else {
                    trimLine = ""
                }
            }
           // trimLine = strings.Replace(trimLine, "  ", " ", -1) //remove unnecessary spaces
           // trimLine = strings.Replace(trimLine, "\t", " ", -1) //remove tabs
            trimLine = it.replace(Regex("\\s")," ")

            var comment = trimLine.indexOf( "//") // search for comments
            if (comment != -1){
                trimLine = trimLine.substring(0,comment)  //trimLine = trimLine[comment3+2:]
            }

            var comment2 = trimLine.indexOf( "/*") // search for comments
            if (comment2 != -1){
                var comment4 = trimLine.indexOf("*/")
                if (comment4 != -1){
                    trimLine = trimLine.substring(0,comment2) + " " + trimLine.substring(comment4+2)
                    if (trimLine == " ") {
                        trimLine = ""
                    }
                    commentedLines = false
                } else {
                    trimLine = trimLine.substring(0,comment2)
                    commentedLines = true
                }

            }


            if (trimLine != "") { // remove blank lines
                var trimContent = addSpacesBetweenSymbols(trimLine,allSymbols)
               // addSpacesBetweenSymbols(&line.lineContent, j.allSymbols)

                var line : Line = Line(trimContent, index);
                this.fileContent.add(line)
            }




        }
        currentLine = fileContent[0]
        numOfLines = fileContent.count()
        currentLineWords = currentLine.lineContent.split(" ")
        currentWordInLineIndex = 0
        currentLineIndex = 0
        currentWord = currentLineWords[0]

    }

    fun hasMoreTokens() : Boolean{
        if (currentLineIndex == fileContent.count()-1  &&  currentWordInLineIndex == currentLineWords.count()-1) {
            return false
        }
        else {
            return true
        }
    }

    fun advance(){
        if (currentWordInLineIndex < currentLineWords.count() - 1) {
            currentWordInLineIndex++
            currentWord = currentLineWords[currentWordInLineIndex]
            if(currentWord == "" || currentWord == " ") {
                advance()
            }
        } else
        {
            var flag: Boolean = true;

            while (flag)
            {
                flag = false
                if (fileContent.count()-1 == currentLineIndex) {
                    return;
                }
                currentLineIndex++
                currentLine = fileContent[currentLineIndex]
                currentLineWords = currentLine.lineContent.split(Regex("\\s")) // the seperetor is any white spate
                currentWordInLineIndex = 0;
                if (currentLineWords.count() > 0) {
                    currentWord = currentLineWords[currentWordInLineIndex]

                }else{
                    flag = true
                }
            }

            if (currentWord== "" || currentWord == " ") {
                advance()
            }
        }
    }
    fun getTokenType() : TokenType {
        var notFirstTime : Boolean
        if (isKeyWord()) {  return TokenType.KEYWORD
        }
        else if(isSymbol()) { return TokenType.SYMBOL
        }
        else if (isNumber()) {return TokenType.INT_CONST
        }
        else if (isIdentifier()) {return TokenType.IDENTIFIER
        }

        if(currentWord[0]=='"') {
            constString = ""
            notFirstTime = false

            currentWordInLineIndex++
            while (currentWordInLineIndex < currentLineWords.count()){

                currentWord = currentLineWords[currentWordInLineIndex]
                if (currentWord == ""){
                    constString += " "
                    currentWordInLineIndex++
                    continue
                }

                if (currentWord[0] != '"') {
                    if (notFirstTime && (constString[constString.count()-1]!=' ') && (currentWord != ","))  {
                        constString += " "
                    }
                    constString += currentLineWords[currentWordInLineIndex]
                    notFirstTime = true
                } else {
                    currentWordInLineIndex++
                    return TokenType.STRING_CONST
                }


                currentWordInLineIndex++
            }

        }

        return TokenType.UNKNOWN_TOKEN
    }





    fun getKeyWordIdentifier(): String{
        return currentWord

    }

    fun getSymbol(): String{

        when(currentWord){
            "<" -> return "&lt;"
            ">" -> return "&gt;"
            "\"&\"" -> return "&amp;"
            "\"" -> return "&quot;"
        }
        return currentWord
    }
    //getSymbol() string
    //getIdentifier() string
    fun getIntVal(): Int {

        var value: Int = currentWord.toInt()
        return  value
    }
    fun getStringVal(): String{
        return constString
    }
    private fun isSymbol(): Boolean {

        for (symbol: String in allSymbols) {
            if (currentWord == symbol )
                return true
        }
        return  false

    }

    private fun isKeyWord(): Boolean {

        for (keyWord: String in allKeyWords) {
            if (currentWord == keyWord.toString() )
                return true
        }
        return  false

    }
    private fun isNumber(): Boolean {
        var numeric = true

        numeric = currentWord.matches("-?\\d+(\\.\\d+)?".toRegex())

        return numeric

    }
    private fun isIdentifier(): Boolean {
        var flag : Boolean = true
        if ((currentWord[0] < 'A' || currentWord[0] > 'z') && (currentWord[0] != '_')) {
            flag = false
        }
        for (char: Char in currentWord) {
            if (char < 'A' || char > 'z') {
                if (char < '0' || char > '9') {
                    if (char != '_') {
                        flag = false;
                    }
                }
            }
        }

        return flag

    }

    fun addSpacesBetweenSymbols(text: String, symbols : List<String>) : String{
        var newText: String = text
        var spacesString: String = ""
        for (symbol in symbols){
            spacesString = " " + symbol + " "
         //   if (text.indexOf(symbol) != -1) {
                newText = newText.replace(symbol, spacesString)
          //  }

        }
        newText = newText.replace("\"", " \" ")
        return  newText
    }


    fun Writer(outFilePath: String){


        File(outFilePath).appendText("<tokens>\n")
        while (hasMoreTokens()){

            when(getTokenType()){
                TokenType.KEYWORD -> File(outFilePath).appendText("<keyword> " + getKeyWordIdentifier() + " </keyword>\n")
                TokenType.SYMBOL -> File(outFilePath).appendText("<symbol> " + getSymbol() + " </symbol>\n");
                TokenType.IDENTIFIER ->File(outFilePath).appendText("<identifier> " + getKeyWordIdentifier() + " </identifier>\n");
                TokenType.INT_CONST ->File(outFilePath).appendText("<integerConstant> " + getIntVal().toInt() + " </integerConstant>\n");
                TokenType.STRING_CONST ->File(outFilePath).appendText("<stringConstant> " + getStringVal() + " </stringConstant>\n");
                TokenType.UNKNOWN_TOKEN ->println ("Unknown token!")
            }

            advance()
        }
        File(outFilePath).appendText("</tokens>")


    }
}