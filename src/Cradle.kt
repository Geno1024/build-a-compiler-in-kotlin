/**************************************************************/
object Cradle
{
    /**********************************************************/
    // Constant Declarations
    val TAB = "\t"
    val CR = '\r'

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
        Abort("$s Expected")
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
    // Recognize an Alphanumeric

    fun IsAlNum(c: Char): Boolean
    {
        return IsAlpha(c) or IsDigit(c)
    }

    /**********************************************************/
    // Recognize an Addop

    fun IsAddop(c: Char): Boolean
    {
        return c in arrayOf('+', '-')
    }

    /**********************************************************/
    // Recognize White Space

    fun IsWhite(c: Char): Boolean
    {
        return c in arrayOf(' ', TAB)
    }

    /**********************************************************/
    // Skip Over Leading White Space

    fun SkipWhite()
    {
        while (IsWhite(Look)) GetChar()
    }

    /**********************************************************/
    // Match a Specific Input Character

    fun Match(x: Char)
    {
        if (Look != x) Expected("'$x'")
        else
        {
            GetChar()
            SkipWhite()
        }
    }

    /**********************************************************/
    // Get an Identifier

    fun GetName(): String
    {
        var Token: String = ""
        if (!IsAlpha(Look)) Expected("Name")
        while (IsAlNum(Look))
        {
            Token = Token + Look.toUpperCase()
            GetChar()
        }
        val GetName = Token
        SkipWhite()
        return GetName
    }

    /**********************************************************/
    // Get a Number

    fun GetNum(): String
    {
        var Value: String = ""
        if (!IsDigit(Look)) Expected("Integer")
        while (IsDigit(Look))
        {
            Value = Value + Look
            GetChar()
        }
        val GetNum = Value
        SkipWhite()
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
    // Parse and Translate an Identifier
    fun Ident()
    {
        var Name: String = GetName()
        if (Look == '(')
        {
            Match('(')
            Match(')')
            EmitLn("BSR $Name")
        }
        else
            EmitLn("MOVE $Name(PC),D0")
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
            Ident()
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
        EmitLn("EXS.L D0")
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
            }
        }
    }

    /**********************************************************/
    // Parse and Translate an Assignment Statement

    fun Assignment()
    {
        var Name: String = GetName()
        Match('=')
        Expression()
        EmitLn("LEA $Name(PC),A0")
        EmitLn("MOVE D0,(A0)")
    }

    /**********************************************************/
    // Initialize

    fun Init()
    {
        GetChar()
        SkipWhite()
    }

    /**********************************************************/
    // Main Program

    @JvmStatic
    fun main(args: Array<String>)
    {
        Init()
        Assignment()
        if (Look != CR) Expected("Newline")
    }
}