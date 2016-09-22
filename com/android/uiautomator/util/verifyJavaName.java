package com.android.uiautomator.util;

/**
 * Created by huangshuli on 2016/9/21.
 */
public class verifyJavaName {
    /**
     * @param variable name of java
     * @return true if name is acceptable else return false
     */

    public static String checkJavaName(String name)
    {
        //the variable could not be null or empty and ""
        if(name == null || name.length() == 0 || name.trim() == "")
            return "Variable of java could not empty , null or \"\"!";

        //check the first character
        char first = name.charAt(0);
        if(!isFirstChar(first))
        {
            return "The first character of java variable is invalid!";
        }

        //check the content of the name after the first character
        for(int i = 1; i < name.length(); i++){
            char c = name.charAt(i);
            if((!Character.isLetterOrDigit(c)) && (c != '_'))
                return "The remaining content contains invalid characters";     }

        return "SUCCESS";
    }

    /**
     * @param A character
     * @return true if the char contains in the list else return false
     */
    private static boolean isFirstChar(char c)
    {
        switch(c){
            case 'A': return true;
            case 'B': return true;
            case 'C': return true;
            case 'D': return true;
            case 'E': return true;
            case 'F': return true;
            case 'G': return true;
            case 'H': return true;
            case 'I': return true;
            case 'J': return true;
            case 'K': return true;
            case 'L': return true;
            case 'M': return true;
            case 'N': return true;
            case 'O': return true;
            case 'P': return true;
            case 'Q': return true;
            case 'R': return true;
            case 'S': return true;
            case 'T': return true;
            case 'U': return true;
            case 'V': return true;
            case 'W': return true;
            case 'X': return true;
            case 'Y': return true;
            case 'Z': return true;
            case 'a': return true;
            case 'b': return true;
            case 'c': return true;
            case 'd': return true;
            case 'e': return true;
            case 'f': return true;
            case 'g': return true;
            case 'h': return true;
            case 'i': return true;
            case 'j': return true;
            case 'k': return true;
            case 'l': return true;
            case 'm': return true;
            case 'n': return true;
            case 'o': return true;
            case 'p': return true;
            case 'q': return true;
            case 'r': return true;
            case 's': return true;
            case 't': return true;
            case 'u': return true;
            case 'v': return true;
            case 'w': return true;
            case 'x': return true;
            case 'y': return true;
            case 'z': return true;
            case '_': return true;
        }
        return false;
    }
}
