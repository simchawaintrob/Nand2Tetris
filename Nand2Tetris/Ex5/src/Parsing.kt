package exe5
import java.io.File


var index:Int = 0
var classSymbolTable= arrayListOf<SymbolTable>()
var counterClassSymbolTable= arrayListOf<HelpCounters>()
var subroutineSymbolTable= arrayListOf<SymbolTable>()
var countersubroutineSymbolTable= arrayListOf<HelpCounters>()
open class Parsing(var parse_file: File,var tokens_file:File) {
    var tokensOfFile:List<String>

    init {
        var temp_file=tokens_file

        tokensOfFile=temp_file.readLines()
        tokensOfFile-="<tokens>"
        tokensOfFile-="</tokens>"
        initCounters(counterClassSymbolTable)
        initCounters(countersubroutineSymbolTable)

    }
    fun initCounters(_list: ArrayList<HelpCounters>){
        _list.clear()
        _list.add(HelpCounters("static",0))
        _list.add(HelpCounters("field",0))
        _list.add(HelpCounters("var",0))
        _list.add(HelpCounters("argument",0))
    }

    fun getNextToken(): String {
        return tokensOfFile[index++]
    }

    fun valueOfToken(): String {
        //var x= tokensOfFile[index].substringBeforeLast(' ')
        //return tokensOfFile[index].substringAfter(' ').substringBefore(' ')
       var s=tokensOfFile[index].substringAfter(' ').substringBeforeLast(' ')
        return s
        //return the value between > and <
    }
    fun valueOfTokenByIndex(num:Int): String {
        return tokensOfFile[num].substringAfter(' ').substringBefore(' ')
        //return the value between > and <
    }
    fun verifyAndNextToken(count:Int){

        for (i in 0..(count-1)) {
            getNextToken()
        }

    }
    fun printTabs(){
        for (i in 0..(countOfTabs -1))
            parse_file.appendText("  ")
    }


}