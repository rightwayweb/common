package com.zitego.util;

/**
 * Generates a random passwords using lowercase, uppercase, and numbers.
 *
 * @author John Glorioso
 * @version $Id: RandomPasswordGenerator.java,v 1.1.1.1 2008/02/20 15:09:44 jglorioso Exp $
 */
public class RandomPasswordGenerator
{
    private static final int LOWER = 0;
    private static final int UPPER = 1;
    private static final int[][] RANGES = new int[][]
    {
        { (int)'0', (int)'9' },
        { (int)'A', (int)'Z' },
        { (int)'a', (int)'z' }
    };
    
    public static void main(String[] args)
    {
        int len = 10;
        if (args.length > 0) len = Integer.parseInt(args[0]);
        System.out.println("password length="+len);
        System.out.println( "generated password="+generatePassword(len) );
    }
    
    /**
     * Generates a random password of the given length. This method uses characters between
     * the ranges of 48-57 (numeric), 65-90 (uppercase), and 97-22 (lowercase).
     *
     * @param len The password length.
     * @return String
     */
    public static String generatePassword(int len)
    {
        char[] pass = new char[len];
        java.util.Random rand = new java.util.Random();
        int range = 0;
        for (int i=0; i<pass.length; i++)
        {
            //Pick a range to use
            range = rand.nextInt(3);
            pass[i] = (char)( ((int)(rand.nextFloat()*(RANGES[range][UPPER]-RANGES[range][LOWER])))+RANGES[range][LOWER] );
        }
        return new String(pass);
    }
}