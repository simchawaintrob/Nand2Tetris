package exe5
import java.io.File


enum class TokenTypes {
    keyword, symbol, integerConstant, stringConstant,identifier
}

val KeyWords=listOf<String>("class", "method","function","constructor","int","boolean","char","void","var","static","field","let","do","if","else","while","return","true","false","null","this")
val fSymbols=listOf<Char>('{','}','(',')','[',']','.',',',';','+','-','*','/','&','|','<','>','=','~')
val Chars=listOf<Char>('A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z')
var AllTokens:MutableList<Token> = mutableListOf()

var tempvalue:String=""
var CharsOnLine=""
var AllLine=""

class Tokenizing(var out_file: File) {

    fun TokenAnalizer(LineElements: List<String>){
        if(!AllLine.contains('"')) {
            for (i in LineElements) {
                if (i.length > 0) {
                    if (i in KeyWords)
                        AllTokens.add(Token(TokenTypes.keyword, i))
                    else if (i[0] in fSymbols) {
                        AllTokens.add(Token(TokenTypes.symbol, GetSymbol(i[0].toString())))
                        CharsOnLine = i
                        CharsOnLine = CharsOnLine.removeRange(0, 1)
                        Q0()
                    } else if (i.matches(Regex("[0-9]+")))
                        AllTokens.add(Token(TokenTypes.integerConstant, i))
                     else {
                        tempvalue = ""

                        //.add(Token(TokenTypes.IDENTIFIER, i))
                        CharsOnLine = i
                        Q0()
                        AllLine = ""
                    }
                }
            }
        }
        else{
            var temp= AllLine
            if (AllLine.startsWith('"')) {
                var m: String = AllLine.substring(AllLine.indexOf('"'), AllLine.indexOf('"'))
                AllLine =m
                AllTokens.add(Token(TokenTypes.stringConstant, m))
                TokenAnalizer(AllLine.substring(AllLine.indexOf('"')+1, AllLine.length).split(Regex("\\s")))
            }
            else{
                AllLine =temp.substringBefore('"')
                TokenAnalizer(AllLine.substringBefore('"').split(Regex("\\s")))
                //var m=temp.sub
                var first=temp.indexOf('"')
                var newtemp=temp.reversed()
                var last=newtemp.indexOf('"')
                last=temp.length-last-1
                var m: String = temp.substring(first+1, last)
                AllTokens.add(Token(TokenTypes.stringConstant, m))
                AllLine =temp.substring(last+1)
                TokenAnalizer(AllLine.split(Regex("\\s")))
                AllLine =temp
            }
        }


    }

    fun GetSymbol(value:String): String {
        when(value){
            "<"-> return "&lt;"
            ">"->return  "&gt;"
            "&"-> return "&amp;"
            else->return  value
        }
    }
    fun WriteToFile(){
        for(i in AllTokens){
            out_file.appendText("<${i.t}> ${i.v} </${i.t}>\n")
            //out_file.appendText("\t<${i.t}> ${i.v} </${i.t}>\n")
        }
    }

    fun Q0(){
        if(!CharsOnLine.isEmpty()) {
            tempvalue += CharsOnLine[0]

            if (CharsOnLine[0] in Chars) {
                CharsOnLine = CharsOnLine.removeRange(0,1)
                Q1()
            } else if (CharsOnLine[0] == '_') {
                CharsOnLine = CharsOnLine.removeRange(0,1)
                Q2()
            } else if (CharsOnLine[0].isDigit()) {
                CharsOnLine = CharsOnLine.removeRange(0,1)
                Q3()
            } else if (CharsOnLine[0] in fSymbols) {
                CharsOnLine = CharsOnLine.removeRange(0,1)
                Q4()
            } else if (CharsOnLine[0] == '"') {
                CharsOnLine = CharsOnLine.removeRange(0,1)
                Q5()
            }
        }

    }

     fun Q1() {
         if(!CharsOnLine.isEmpty()){
             tempvalue += CharsOnLine[0]
             if(tempvalue in KeyWords && (CharsOnLine.length==1||!(CharsOnLine[1] in Chars))){
                 AllTokens.add(Token(TokenTypes.keyword, tempvalue))
                 CharsOnLine = CharsOnLine.removeRange(0,1)
                 tempvalue =""
                 Q0()
             }
             else if(CharsOnLine[0] in Chars){
                 CharsOnLine = CharsOnLine.removeRange(0,1)
                 Q1()
             }
             else if(CharsOnLine[0].isDigit()){
                 CharsOnLine = CharsOnLine.removeRange(0,1)
                 Q2()
             }
             else if(CharsOnLine[0]=='_'){
                 CharsOnLine = CharsOnLine.removeRange(0,1)
                 Q2()
             }
             else {
                 if(tempvalue.length>1)
                    tempvalue = tempvalue.removeRange(tempvalue.length-1, tempvalue.length)
                 else
                     tempvalue =""
                 AllTokens.add(Token(TokenTypes.identifier, tempvalue))
                 tempvalue =""
                 Q0()
             }
         }
         else if(tempvalue.length>0){
             AllTokens.add(Token(TokenTypes.identifier, tempvalue))
             tempvalue =""
         }

    }
    fun Q2() {
        if(!CharsOnLine.isEmpty()) {
            tempvalue += CharsOnLine[0]
            if(CharsOnLine[0].isDigit()|| CharsOnLine[0]=='_'|| CharsOnLine[0] in Chars)
            {
                CharsOnLine = CharsOnLine.removeRange(0,1)
                Q2()
            }
            else{
                if(tempvalue.length>1)
                    tempvalue = tempvalue.removeRange(tempvalue.length-1, tempvalue.length)
                else
                    tempvalue =""
                AllTokens.add(Token(TokenTypes.identifier, tempvalue))
                tempvalue =""
                Q0()
            }
        }
        else if(tempvalue.length>0){
            AllTokens.add(Token(TokenTypes.identifier, tempvalue))
            tempvalue =""
        }

    }
    fun Q3() {
        if(!CharsOnLine.isEmpty()) {
            if(!CharsOnLine[0].isDigit()){
                AllTokens.add(Token(TokenTypes.integerConstant, tempvalue))
                tempvalue =""
                Q0()
            }
            else{
                tempvalue += CharsOnLine[0]
                CharsOnLine = CharsOnLine.removeRange(0,1)
                Q3()
            }
        }
        else if(tempvalue.length>0){
            AllTokens.add(Token(TokenTypes.integerConstant, tempvalue))
            tempvalue =""
        }


    }
    fun Q4() {

            AllTokens.add(Token(TokenTypes.symbol, tempvalue))
            tempvalue =""
            //CharsOnLine=CharsOnLine.removeRange(0,1)
            Q0()


    }
    fun Q5() {
        if(!CharsOnLine.isEmpty()){
            //var a=CharsOnLine.find { x->x=='"' }
            tempvalue += CharsOnLine.substring(0, CharsOnLine.indexOf('"'))
            AllTokens.add(Token(TokenTypes.stringConstant, tempvalue))
            tempvalue =""
            CharsOnLine = CharsOnLine.removeRange(tempvalue.length-1, CharsOnLine.length)
            Q0()
        }

    }


}

fun TempString(){

}