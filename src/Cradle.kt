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
    // Recognize an Addop

    fun IsAddop(c: Char): Boolean
    {
        return c in arrayOf('+', '-')
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
    // Parse and Translate a Math Factor

    fun Factor()
    {
        if (Look == '(')
        {
            Match('(')
            Expression()
            Match(')')
        }
        else if (IsAlpha(Look))
            EmitLn("MOVE ${GetName()}(PC),D0")
        else
            EmitLn("MOVE #${GetNum()},D0")
    }

    /**********************************************************/
    // Recognize and Translate a Multiply

    fun Multiply()
    {
        Match('*')
        Factor()
        EmitLn("MULS (SP)+,D0")
    }

    /**********************************************************/
    // Recognize and Translate a Divide

    fun Divide()
    {
        Match('/')
        Factor()
        EmitLn("MOVE (SP)+,D1")
        EmitLn("DIVS D1,D0")
    }

    /**********************************************************/
    // Parse and Translate a Math Expression

    fun Term()
    {
        Factor()
        while (Look in arrayOf('*', '/'))
        {
            EmitLn("MOVE D0,-(SP)")
            when (Look)
            {
                '*' -> Multiply()
                '/' -> Divide()
                else -> Expected("Mulop")
            }
        }
    }

    /**********************************************************/
    // Recognize and Translate an Add

    fun Add()
    {
        Match('+')
        Term()
        EmitLn("ADD (SP)+,D0")
    }

    /**********************************************************/
    // Recognize and Translate a Subtract

    fun Subtract()
    {
        Match('-')
        Term()
        EmitLn("SUB (SP)+,D0")
        EmitLn("NEG D0")
    }

    /**********************************************************/
    // Parse and Translate an Expression

    fun Expression()
    {
        if (IsAddop(Look)) EmitLn("CLR D0")
        else Term()
        while (IsAddop(Look))
        {
            EmitLn("MOVE D0,-(SP)")
            when (Look)
            {
                '+' -> Add()
                '-' -> Subtract()
                else -> Expected("Addop")
            }
        }
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