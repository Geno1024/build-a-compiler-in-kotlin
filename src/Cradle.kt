/**************************************************************/
object Cradle
{
    /**********************************************************/
    // Constant Declarations
    val TAB = "\t"

    /**********************************************************/
    // Variable Declarations
    var Look: Char = ' ' // Lookahead Character

    /**********************************************************/
    // Read New Character From Input Stream

    fun GetChar()
    {
        Look = System.`in`.read().toChar()
    }

    /**********************************************************/
    // Report an Error

    fun Error(s: String)
    {
        println()
        System.err.println("Error: $s.")
    }

    /**********************************************************/
    // Report Error and Halt

    fun Abort(s: String)
    {
        Error(s)
        kotlin.system.exitProcess(-1)
    }

    /**********************************************************/
    // Report What Was Expected

    fun Expected(s: String)
    {
        Abort("$s Expected.")
    }

    /**********************************************************/
    // Match a Specific Input Character

    fun Match(x: Char)
    {
        if (Look == x) GetChar()
        else Expected("'$x'")
    }

    /**********************************************************/
    // Recognize an Alpha Character

    fun IsAlpha(c: Char): Boolean
    {
        return c.isLetter()
    }

    /**********************************************************/
    // Recognize a Decimal Digit

    fun IsDigit(c: Char): Boolean
    {
        return c.isDigit()
    }

    /**********************************************************/
    // Get an Identifier

    fun GetName(): Char
    {
        if (!IsAlpha(Look)) Expected("Name")
        val GetName = Look.toUpperCase()
        GetChar()
        return GetName
    }

    /**********************************************************/
    // Get a Number

    fun GetNum(): Char
    {
        if (!IsDigit(Look)) Expected("Integer")
        val GetNum = Look
        GetChar()
        return GetNum
    }

    /**********************************************************/
    // Output a String with Tab

    fun Emit(s: String)
    {
        print("$TAB$s")
    }

    /**********************************************************/
    // Output a String with Tab and CRLF

    fun EmitLn(s: String)
    {
        Emit(s)
        println()
    }

    /**********************************************************/
    // Initialize

    fun Init()
    {
        GetChar()
    }

    /**********************************************************/
    // Parse and Translate a Math Expression

    fun Expression()
    {
        EmitLn("MOVE #${GetNum()},D0")
    }

    /**********************************************************/
    // Main Program

    @JvmStatic
    fun main(args: Array<String>)
    {
        Init()
        Expression()
    }
}