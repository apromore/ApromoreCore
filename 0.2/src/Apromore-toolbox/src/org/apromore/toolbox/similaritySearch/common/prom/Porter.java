package org.apromore.toolbox.similaritySearch.common.prom;


/* author:   Fotis Lazarinis (actually I translated from C to Java)
   date:     June 1997
   address:  Psilovraxou 12, Agrinio, 30100

   comments: Compile it, import the Porter class into you program and create an instance.
   Then use the stripAffixes method of this method which takes a String as
    input and returns the stem of this String again as a String.

 */

public class Porter {

    @SuppressWarnings("static-access")
	private String Clean(String str) {
        int last = str.length();

        Character ch = new Character(str.charAt(0));
        String temp = "";

        for (int i = 0; i < last; i++) {
            if (ch.isLetterOrDigit(str.charAt(i))) {
                temp += str.charAt(i);
            }
        }

        return temp;
    } //clean

    private boolean hasSuffix(String word, String suffix, String stem) {

        String tmp = "";

        if (word.length() <= suffix.length()) {
            return false;
        }
        if (suffix.length() > 1) {
            if (word.charAt(word.length() - 2) !=
                suffix.charAt(suffix.length() - 2)) {
                return false;
            }
        }

        stem = "";

        for (int i = 0; i < word.length() - suffix.length(); i++) {
            stem += word.charAt(i);
        }
        tmp = stem;

        for (int i = 0; i < suffix.length(); i++) {
            tmp += suffix.charAt(i);
        }

        if (tmp.compareTo(word) == 0) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean vowel(char ch, char prev) {
        switch (ch) {
        case 'a':
        case 'e':
        case 'i':
        case 'o':
        case 'u':
            return true;
        case 'y': {

            switch (prev) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
                return false;

            default:
                return true;
            }
        }

        default:
            return false;
        }
    }

    private int measure(String stem) {

        int i = 0, count = 0;
        int length = stem.length();

        while (i < length) {
            for (; i < length; i++) {
                if (i > 0) {
                    if (vowel(stem.charAt(i), stem.charAt(i - 1))) {
                        break;
                    }
                } else {
                    if (vowel(stem.charAt(i), 'a')) {
                        break;
                    }
                }
            }

            for (i++; i < length; i++) {
                if (i > 0) {
                    if (!vowel(stem.charAt(i), stem.charAt(i - 1))) {
                        break;
                    }
                } else {
                    if (!vowel(stem.charAt(i), '?')) {
                        break;
                    }
                }
            }
            if (i < length) {
                count++;
                i++;
            }
        } //while

        return (count);
    }

    private boolean containsVowel(String word) {

        for (int i = 0; i < word.length(); i++) {
            if (i > 0) {
                if (vowel(word.charAt(i), word.charAt(i - 1))) {
                    return true;
                }
            } else {
                if (vowel(word.charAt(0), 'a')) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean cvc(String str) {
        int length = str.length();

        if (length < 3) {
            return false;
        }

        if ((!vowel(str.charAt(length - 1), str.charAt(length - 2)))
            && (str.charAt(length - 1) != 'w') &&
            (str.charAt(length - 1) != 'x') && (str.charAt(length - 1) != 'y')
            && (vowel(str.charAt(length - 2), str.charAt(length - 3)))) {

            if (length == 3) {
                if (!vowel(str.charAt(0), '?')) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (!vowel(str.charAt(length - 3), str.charAt(length - 4))) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    private String step1(String str) {

        String stem = "";

        if (str.charAt(str.length() - 1) == 's') {
            if ((hasSuffix(str, "sses", stem)) || (hasSuffix(str, "ies", stem))) {
                String tmp = "";
                for (int i = 0; i < str.length() - 2; i++) {
                    tmp += str.charAt(i);
                }
                str = tmp;
            } else {
                if ((str.length() == 1) && (str.charAt(str.length() - 1) == 's')) {
                    str = "";
                    return str;
                }
                if (str.charAt(str.length() - 2) != 's') {
                    String tmp = "";
                    for (int i = 0; i < str.length() - 1; i++) {
                        tmp += str.charAt(i);
                    }
                    str = tmp;
                }
            }
        }

        if (hasSuffix(str, "eed", stem)) {
            if (measure(stem) > 0) {
                String tmp = "";
                for (int i = 0; i < str.length() - 1; i++) {
                    tmp += str.charAt(i);
                }
                str = tmp;
            }
        } else {
            if ((hasSuffix(str, "ed", stem)) || (hasSuffix(str, "ing", stem))) {
                if (containsVowel(stem)) {

                    String tmp = "";
                    for (int i = 0; i < stem.length(); i++) {
                        tmp += str.charAt(i);
                    }
                    str = tmp;
                    if (str.length() == 1) {
                        return str;
                    }

                    if ((hasSuffix(str, "at", stem)) ||
                        (hasSuffix(str, "bl", stem)) ||
                        (hasSuffix(str, "iz", stem))) {
                        str += "e";

                    } else {
                        int length = str.length();
                        if ((str.charAt(length - 1) == str.charAt(length - 2))
                            && (str.charAt(length - 1) != 'l') &&
                            (str.charAt(length - 1) != 's') &&
                            (str.charAt(length - 1) != 'z')) {

                            tmp = "";
                            for (int i = 0; i < str.length() - 1; i++) {
                                tmp += str.charAt(i);
                            }
                            str = tmp;
                        } else
                        if (measure(str) == 1) {
                            if (cvc(str)) {
                                str += "e";
                            }
                        }
                    }
                }
            }
        }

        if (hasSuffix(str, "y", stem)) {
            if (containsVowel(stem)) {
                String tmp = "";
                for (int i = 0; i < str.length() - 1; i++) {
                    tmp += str.charAt(i);
                }
                str = tmp + "i";
            }
        }
        return str;
    }

    private static final String[][] step2_suffixes = { {"ational", "ate"}, {"tional", "tion"}, {"enci",
                          "ence"}, {"anci", "ance"}, {"izer", "ize"},
                          {"iser", "ize"}, {"abli", "able"}, {"alli", "al"},
                          {"entli", "ent"}, {"eli", "e"}, {"ousli", "ous"},
                          {"ization", "ize"}, {"isation", "ize"}, {"ation",
                          "ate"}, {"ator", "ate"}, {"alism", "al"},
                          {"iveness", "ive"}, {"fulness", "ful"},
                          {"ousness", "ous"}, {"aliti", "al"}, {"iviti",
                          "ive"}, {"biliti", "ble"}
};

    private String step2(String str) {

        String stem = "";

        for (int index = 0; index < step2_suffixes.length; index++) {
            if (hasSuffix(str, step2_suffixes[index][0], stem)) {
                if (measure(stem) > 0) {
                    str = stem + step2_suffixes[index][1];
                    return str;
                }
            }
        }

        return str;
    }

    private static final String[][] step3_suffixes = { {"icate", "ic"}, {"ative", ""}, {"alize", "al"},
                          {"alise", "al"}, {"iciti", "ic"}, {"ical", "ic"},
                          {"ful", ""}, {"ness", ""}
    };

    private String step3(String str) {

        String stem = "";

        for (int index = 0; index < step3_suffixes.length; index++) {
            if (hasSuffix(str, step3_suffixes[index][0], stem)) {
                if (measure(stem) > 0) {
                    str = stem + step3_suffixes[index][1];
                    return str;
                }
            }
        }
        return str;
    }

    private static final String[] step4_suffixes = {"al", "ance", "ence", "er", "ic", "able", "ible",
                        "ant", "ement", "ment", "ent", "sion", "tion",
                        "ou", "ism", "ate", "iti", "ous", "ive", "ize",
                        "ise"};

    private String step4(String str) {


        String stem = "";

        for (int index = 0; index < step4_suffixes.length; index++) {
            if (hasSuffix(str, step4_suffixes[index], stem)) {

                if (measure(stem) > 1) {
                    str = stem;
                    return str;
                }
            }
        }
        return str;
    }

    private String step5(String str) {

        if (str.charAt(str.length() - 1) == 'e') {
            if (measure(str) > 1) {
                    /* measure(str)==measure(stem) if ends in vowel */
                String tmp = "";
                for (int i = 0; i < str.length() - 1; i++) {
                    tmp += str.charAt(i);
                }
                str = tmp;
            } else
            if (measure(str) == 1) {
                String stem = "";
                for (int i = 0; i < str.length() - 1; i++) {
                    stem += str.charAt(i);
                }

                if (!cvc(stem)) {
                    str = stem;
                }
            }
        }

        if (str.length() == 1) {
            return str;
        }
        if ((str.charAt(str.length() - 1) == 'l') &&
            (str.charAt(str.length() - 2) == 'l') && (measure(str) > 1)) {
            if (measure(str) > 1) {
                    /* measure(str)==measure(stem) if ends in vowel */
                String tmp = "";
                for (int i = 0; i < str.length() - 1; i++) {
                    tmp += str.charAt(i);
                }
                str = tmp;
            }
        }
        return str;
    }

    private static final String[] prefixes = {"kilo", "micro", "milli", "intra", "ultra", "mega",
                        "nano", "pico", "pseudo"};


    private String stripPrefixes(String str) {

        int last = prefixes.length;
        for (int i = 0; i < last; i++) {
            if (str.startsWith(prefixes[i])) {
                String temp = "";
                for (int j = 0; j < str.length() - prefixes[i].length(); j++) {
                    temp += str.charAt(j + prefixes[i].length());
                }
                return temp;
            }
        }

        return str;
    }


    private String stripSuffixes(String str) {

        str = step1(str);
        if (str.length() >= 1) {
            str = step2(str);
        }
        if (str.length() >= 1) {
            str = step3(str);
        }
        if (str.length() >= 1) {
            str = step4(str);
        }
        if (str.length() >= 1) {
            str = step5(str);
        }

        return str;
    }


    public String stripAffixes(String str) {

        str = str.toLowerCase();
        str = Clean(str);

        if ((str != "") && (str.length() > 2)) {
            str = stripPrefixes(str);

            if (str != "") {
                str = stripSuffixes(str);
            }

        }

        return str;
    } //stripAffixes

} //class
