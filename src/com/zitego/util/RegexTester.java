package com.zitego.util;

import java.util.regex.*;

public class RegexTester
{
    /**
     * Tests supplied text against a supplied pattern.
     * <br>
     * Usage: java com.zitego.util.RegexTester &lt;regexp&gt; &lt;text&rt;
     *
     * @param String[] The regular expression and test text.
     */
    public static void main(String[] args) throws Exception
    {
        if (args.length == 0)
        {
            System.out.println("Usage: java com.zitego.util.RegexTester <regexp> <text>");
            System.exit(1);
        }
        String re = args[0];
        String test = args[1];
        Pattern p = Pattern.compile(re);
        System.out.println( "Pattern: " + p.pattern() );
        System.out.println( "Test: " + test );
        System.out.println( "Results: " + (p.matcher(test).find() ? "Matches" : "Does Not Match" ) );
    }
}