package org.apromore.toolbox.similaritySearch.common;

import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;

import org.apromore.toolbox.similaritySearch.common.stemmer.SnowballStemmer;

import static java.lang.Class.forName;

public class Settings {
    public static String STRING_DELIMETER = " ,.:;&/?!#()";

    public static boolean logResult = true;
    public static boolean considerEvents = true;
    public static boolean considerGateways = true;

    private static SnowballStemmer englishStemmer;

    public static SnowballStemmer getEnglishStemmer() {
        if (englishStemmer == null) {
            englishStemmer = getStemmer("english");
        }

        return englishStemmer;
    }

    @SuppressWarnings("rawtypes")
    public static SnowballStemmer getStemmer(String language){
        Class stemClass;
        SnowballStemmer stemmer;

        try {
            stemClass = forName("org.apromore.toolbox.similaritySearch.common.stemmer.ext." + language + "Stemmer");
            stemmer = (SnowballStemmer) stemClass.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return stemmer;
    }

    public static String removeSpaces(String s) {
        StringTokenizer st = new StringTokenizer(s);
        String result = "";

        while (st.hasMoreTokens()) {
            result += st.nextToken()+ (st.hasMoreTokens() ? " " : "");
        }

        return result;
    }
}
