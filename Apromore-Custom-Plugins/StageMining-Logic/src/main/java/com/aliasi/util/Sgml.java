/*
 * LingPipe v. 4.1.0
 * Copyright (C) 2003-2011 Alias-i
 *
 * This program is licensed under the Alias-i Royalty Free License
 * Version 1 WITHOUT ANY WARRANTY, without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Alias-i
 * Royalty Free License Version 1 for more details.
 *
 * You should have received a copy of the Alias-i Royalty Free License
 * Version 1 along with this program; if not, visit
 * http://alias-i.com/lingpipe/licenses/lingpipe-license-1.txt or contact
 * Alias-i, Inc. at 181 North 11th Street, Suite 401, Brooklyn, NY 11211,
 * +1 (718) 290-9170.
 */

package com.aliasi.util;

import java.util.HashMap;
import java.util.Map;

/**
 * The <code>Sgml</code> class contains static methods for processing SGML
 * into unicode characters.  There is a method {@link #entityToCharacter(String)}
 * which returns the unicode character corresponding to an SGML entity.  There
 * is also a method {@link #replaceEntities(String,String)} which performs
 * a substitution for entities in an input string.
 *
 * <p>See the following document for a complete list of over 1000
 * entities known by this class:
 *
 * <ul>
 * <li>John Cowan's <a href="http://unicode.org/Public/MAPPINGS/VENDORS/MISC/SGML.TXT">SGML to Unicode Mapping</a>
 * </li>
 * </ul>
 *
 * @author Bob Carpenter (from data provided by John Cowan)
 * @version 3.9.1
 * @since   LingPipe3.2
 */
public class Sgml {

    private Sgml() {
        /* no instances */
    }

    /**
     * Returns the character represented by the specified SGML entity,
     * or <code>null</code> if the entity is undefined.  Note that the
     * SGML entity should be passed in without its preceding ampersand
     * or following semicolon.
     *
     * @param entity Name of SGML entity (without initial ampersand
     * and final semicolon).
     * @return The character for the entity, or <code>null</code> if
     * it is undefined.
     */
    public static Character entityToCharacter(String entity) {
        return SGML_MAP.get(entity);
    }

    /**
     * Returns the result of replacing all the entities appearing
     * in the specified string with their corresponding unicode
     * characters, using the specified replacement string for
     * unknown entities.
     *
     * @param in Input string.
     * @param unknownReplacement String with which to replace unknown
     * entities.
     * @return The input string with entities replaced with their
     * corresponding characters.
     */
    public static String replaceEntities(String in, String unknownReplacement) {
        int ampIndex = in.indexOf('&');
        if (ampIndex < 0) return in;
        int semicolonIndex = in.indexOf(';',ampIndex+1);
        if (semicolonIndex < 0) return in;
        StringBuilder sb = new StringBuilder();
        int start = 0;
        while (true) {
            sb.append(in.substring(start,ampIndex));
            String entity = in.substring(ampIndex+1,semicolonIndex);
            Character replacement = SGML_MAP.get(entity);
            sb.append(replacement != null ? replacement : unknownReplacement);
            start = semicolonIndex+1;
            ampIndex = in.indexOf('&',start);
            if (ampIndex < 0) return sb + in.substring(start);
            semicolonIndex = in.indexOf(';',ampIndex+1);
            if (semicolonIndex < 0) return sb + in.substring(start);
        }
    }


    /**
     * Convenience method to call {@link #replaceEntities(String,String)}
     * with the question marked used for unknown entities.
     *
     * @param in Input string.
     * @return The input string with entities replaced with their
     * corresponding characters.
     */
    public static String replaceEntities(String in) {
        return replaceEntities(in,"?");
    }

    // Author: John Cowan <cowan@ccil.org>
    // Date: 25 July 1997
    // from: http://unicode.org/Public/MAPPINGS/VENDORS/MISC/SGML.TXT
    static final Map<String,Character> SGML_MAP = new HashMap<String,Character>(1500);
    static {
        SGML_MAP.put("Aacgr",'\u0386'); // GREEK CAPITAL LETTER ALPHA WITH TONOS
        SGML_MAP.put("aacgr",'\u03AC'); // GREEK SMALL LETTER ALPHA WITH TONOS
        SGML_MAP.put("Aacute",'\u00C1'); // LATIN CAPITAL LETTER A WITH ACUTE
        SGML_MAP.put("aacute",'\u00E1'); // LATIN SMALL LETTER A WITH ACUTE
        SGML_MAP.put("Abreve",'\u0102'); // LATIN CAPITAL LETTER A WITH BREVE
        SGML_MAP.put("abreve",'\u0103'); // LATIN SMALL LETTER A WITH BREVE
        SGML_MAP.put("Acirc",'\u00C2'); // LATIN CAPITAL LETTER A WITH CIRCUMFLEX
        SGML_MAP.put("acirc",'\u00E2'); // LATIN SMALL LETTER A WITH CIRCUMFLEX
        SGML_MAP.put("acute",'\u00B4'); // ACUTE ACCENT
        SGML_MAP.put("Acy",'\u0410'); // CYRILLIC CAPITAL LETTER A
        SGML_MAP.put("acy",'\u0430'); // CYRILLIC SMALL LETTER A
        SGML_MAP.put("AElig",'\u00C6'); // LATIN CAPITAL LETTER AE
        SGML_MAP.put("aelig",'\u00E6'); // LATIN SMALL LETTER AE
        SGML_MAP.put("Agr",'\u0391'); // GREEK CAPITAL LETTER ALPHA
        SGML_MAP.put("agr",'\u03B1'); // GREEK SMALL LETTER ALPHA
        SGML_MAP.put("Agrave",'\u00C0'); // LATIN CAPITAL LETTER A WITH GRAVE
        SGML_MAP.put("agrave",'\u00E0'); // LATIN SMALL LETTER A WITH GRAVE
        SGML_MAP.put("alefsym",'\u2135'); // ALEF SYMBOL
        SGML_MAP.put("aleph",'\u2135'); // ALEF SYMBOL
        SGML_MAP.put("Alpha",'\u0391'); // GREEK CAPITAL LETTER ALPHA
        SGML_MAP.put("alpha",'\u03B1'); // GREEK SMALL LETTER ALPHA
        SGML_MAP.put("Amacr",'\u0100'); // LATIN CAPITAL LETTER A WITH MACRON
        SGML_MAP.put("amacr",'\u0101'); // LATIN SMALL LETTER A WITH MACRON
        SGML_MAP.put("amalg",'\u2210'); // N-ARY COPRODUCT
        SGML_MAP.put("amp",'\u0026'); // AMPERSAND
        SGML_MAP.put("and",'\u2227'); // LOGICAL AND
        SGML_MAP.put("ang",'\u2220'); // ANGLE
        SGML_MAP.put("ang90",'\u221F'); // RIGHT ANGLE
        SGML_MAP.put("angmsd",'\u2221'); // MEASURED ANGLE
        SGML_MAP.put("angsph",'\u2222'); // SPHERICAL ANGLE
        SGML_MAP.put("angst",'\u212B'); // ANGSTROM SIGN
        SGML_MAP.put("Aogon",'\u0104'); // LATIN CAPITAL LETTER A WITH OGONEK
        SGML_MAP.put("aogon",'\u0105'); // LATIN SMALL LETTER A WITH OGONEK
        SGML_MAP.put("ap",'\u2248'); // ALMOST EQUAL TO
        SGML_MAP.put("ape",'\u224A'); // ALMOST EQUAL OR EQUAL TO
        SGML_MAP.put("apos",'\u02BC'); // MODIFIER LETTER APOSTROPHE
        SGML_MAP.put("Aring",'\u00C5'); // LATIN CAPITAL LETTER A WITH RING ABOVE
        SGML_MAP.put("aring",'\u00E5'); // LATIN SMALL LETTER A WITH RING ABOVE
        SGML_MAP.put("ast",'\u002A'); // ASTERISK
        SGML_MAP.put("asymp",'\u2248'); // ALMOST EQUAL TO
        SGML_MAP.put("Atilde",'\u00C3'); // LATIN CAPITAL LETTER A WITH TILDE
        SGML_MAP.put("atilde",'\u00E3'); // LATIN SMALL LETTER A WITH TILDE
        SGML_MAP.put("Auml",'\u00C4'); // LATIN CAPITAL LETTER A WITH DIAERESIS
        SGML_MAP.put("auml",'\u00E4'); // LATIN SMALL LETTER A WITH DIAERESIS
        SGML_MAP.put("b.alpha",'\u03B1'); // GREEK SMALL LETTER ALPHA
        SGML_MAP.put("barwed",'\u22BC'); // NAND
        SGML_MAP.put("Barwed",'\u2306'); // PERSPECTIVE
        SGML_MAP.put("b.beta",'\u03B2'); // GREEK SMALL LETTER BETA
        SGML_MAP.put("b.chi",'\u03C7'); // GREEK SMALL LETTER CHI
        SGML_MAP.put("bcong",'\u224C'); // ALL EQUAL TO
        SGML_MAP.put("Bcy",'\u0411'); // CYRILLIC CAPITAL LETTER BE
        SGML_MAP.put("bcy",'\u0431'); // CYRILLIC SMALL LETTER BE
        SGML_MAP.put("b.Delta",'\u0394'); // GREEK CAPITAL LETTER DELTA
        SGML_MAP.put("b.delta",'\u03B4'); // GREEK SMALL LETTER DELTA
        SGML_MAP.put("bdquo",'\u201E'); // DOUBLE LOW-9 QUOTATION MARK
        SGML_MAP.put("becaus",'\u2235'); // BECAUSE
        SGML_MAP.put("bepsi",'\u220D'); // SMALL CONTAINS AS MEMBER
        SGML_MAP.put("b.epsi",'\u03B5'); // GREEK SMALL LETTER EPSILON
        SGML_MAP.put("b.epsis",'\u03B5'); // GREEK SMALL LETTER EPSILON
        SGML_MAP.put("b.epsiv",'\u03B5'); // GREEK SMALL LETTER EPSILON
        SGML_MAP.put("bernou",'\u212C'); // SCRIPT CAPITAL B
        SGML_MAP.put("Beta",'\u0392'); // GREEK CAPITAL LETTER BETA
        SGML_MAP.put("beta",'\u03B2'); // GREEK SMALL LETTER BETA
        SGML_MAP.put("b.eta",'\u03B7'); // GREEK SMALL LETTER ETA
        SGML_MAP.put("beth",'\u2136'); // BET SYMBOL
        SGML_MAP.put("b.Gamma",'\u0393'); // GREEK CAPITAL LETTER GAMMA
        SGML_MAP.put("b.gamma",'\u03B3'); // GREEK SMALL LETTER GAMMA
        SGML_MAP.put("b.gammad",'\u03DC'); // GREEK LETTER DIGAMMA
        SGML_MAP.put("Bgr",'\u0392'); // GREEK CAPITAL LETTER BETA
        SGML_MAP.put("bgr",'\u03B2'); // GREEK SMALL LETTER BETA
        SGML_MAP.put("b.iota",'\u03B9'); // GREEK SMALL LETTER IOTA
        SGML_MAP.put("b.kappa",'\u03BA'); // GREEK SMALL LETTER KAPPA
        SGML_MAP.put("b.kappav",'\u03F0'); // GREEK KAPPA SYMBOL
        SGML_MAP.put("b.Lambda",'\u039B'); // GREEK CAPITAL LETTER LAMDA
        SGML_MAP.put("b.lambda",'\u03BB'); // GREEK SMALL LETTER LAMDA
        SGML_MAP.put("blank",'\u2423'); // OPEN BOX
        SGML_MAP.put("blk12",'\u2592'); // MEDIUM SHADE
        SGML_MAP.put("blk14",'\u2591'); // LIGHT SHADE
        SGML_MAP.put("blk34",'\u2593'); // DARK SHADE
        SGML_MAP.put("block",'\u2588'); // FULL BLOCK
        SGML_MAP.put("b.mu",'\u03BC'); // GREEK SMALL LETTER MU
        SGML_MAP.put("b.nu",'\u03BD'); // GREEK SMALL LETTER NU
        SGML_MAP.put("b.Omega",'\u03A9'); // GREEK CAPITAL LETTER OMEGA
        SGML_MAP.put("b.omega",'\u03CE'); // GREEK SMALL LETTER OMEGA WITH TONOS
        SGML_MAP.put("bottom",'\u22A5'); // UP TACK
        SGML_MAP.put("bowtie",'\u22C8'); // BOWTIE
        SGML_MAP.put("boxdl",'\u2510'); // BOX DRAWINGS LIGHT DOWN AND LEFT
        SGML_MAP.put("boxdL",'\u2555'); // BOX DRAWINGS DOWN SINGLE AND LEFT DOUBLE
        SGML_MAP.put("boxDl",'\u2556'); // BOX DRAWINGS DOWN DOUBLE AND LEFT SINGLE
        SGML_MAP.put("boxDL",'\u2557'); // BOX DRAWINGS DOUBLE DOWN AND LEFT
        SGML_MAP.put("boxdr",'\u250C'); // BOX DRAWINGS LIGHT DOWN AND RIGHT
        SGML_MAP.put("boxdR",'\u2552'); // BOX DRAWINGS DOWN SINGLE AND RIGHT DOUBLE
        SGML_MAP.put("boxDr",'\u2553'); // BOX DRAWINGS DOWN DOUBLE AND RIGHT SINGLE
        SGML_MAP.put("boxDR",'\u2554'); // BOX DRAWINGS DOUBLE DOWN AND RIGHT
        SGML_MAP.put("boxh",'\u2500'); // BOX DRAWINGS LIGHT HORIZONTAL
        SGML_MAP.put("boxH",'\u2550'); // BOX DRAWINGS DOUBLE HORIZONTAL
        SGML_MAP.put("boxhd",'\u252C'); // BOX DRAWINGS LIGHT DOWN AND HORIZONTAL
        SGML_MAP.put("boxHd",'\u2564'); // BOX DRAWINGS DOWN SINGLE AND HORIZONTAL DOUBLE
        SGML_MAP.put("boxhD",'\u2565'); // BOX DRAWINGS DOWN DOUBLE AND HORIZONTAL SINGLE
        SGML_MAP.put("boxHD",'\u2566'); // BOX DRAWINGS DOUBLE DOWN AND HORIZONTAL
        SGML_MAP.put("boxhu",'\u2534'); // BOX DRAWINGS LIGHT UP AND HORIZONTAL
        SGML_MAP.put("boxHu",'\u2567'); // BOX DRAWINGS UP SINGLE AND HORIZONTAL DOUBLE
        SGML_MAP.put("boxhU",'\u2568'); // BOX DRAWINGS UP DOUBLE AND HORIZONTAL SINGLE
        SGML_MAP.put("boxHU",'\u2569'); // BOX DRAWINGS DOUBLE UP AND HORIZONTAL
        SGML_MAP.put("boxul",'\u2518'); // BOX DRAWINGS LIGHT UP AND LEFT
        SGML_MAP.put("boxuL",'\u255B'); // BOX DRAWINGS UP SINGLE AND LEFT DOUBLE
        SGML_MAP.put("boxUl",'\u255C'); // BOX DRAWINGS UP DOUBLE AND LEFT SINGLE
        SGML_MAP.put("boxUL",'\u255D'); // BOX DRAWINGS DOUBLE UP AND LEFT
        SGML_MAP.put("boxur",'\u2514'); // BOX DRAWINGS LIGHT UP AND RIGHT
        SGML_MAP.put("boxuR",'\u2558'); // BOX DRAWINGS UP SINGLE AND RIGHT DOUBLE
        SGML_MAP.put("boxUr",'\u2559'); // BOX DRAWINGS UP DOUBLE AND RIGHT SINGLE
        SGML_MAP.put("boxUR",'\u255A'); // BOX DRAWINGS DOUBLE UP AND RIGHT
        SGML_MAP.put("boxv",'\u2502'); // BOX DRAWINGS LIGHT VERTICAL
        SGML_MAP.put("boxV",'\u2551'); // BOX DRAWINGS DOUBLE VERTICAL
        SGML_MAP.put("boxvh",'\u253C'); // BOX DRAWINGS LIGHT VERTICAL AND HORIZONTAL
        SGML_MAP.put("boxvH",'\u256A'); // BOX DRAWINGS VERTICAL SINGLE AND HORIZONTAL DOUBLE
        SGML_MAP.put("boxVh",'\u256B'); // BOX DRAWINGS VERTICAL DOUBLE AND HORIZONTAL SINGLE
        SGML_MAP.put("boxVH",'\u256C'); // BOX DRAWINGS DOUBLE VERTICAL AND HORIZONTAL
        SGML_MAP.put("boxvl",'\u2524'); // BOX DRAWINGS LIGHT VERTICAL AND LEFT
        SGML_MAP.put("boxvL",'\u2561'); // BOX DRAWINGS VERTICAL SINGLE AND LEFT DOUBLE
        SGML_MAP.put("boxVl",'\u2562'); // BOX DRAWINGS VERTICAL DOUBLE AND LEFT SINGLE
        SGML_MAP.put("boxVL",'\u2563'); // BOX DRAWINGS DOUBLE VERTICAL AND LEFT
        SGML_MAP.put("boxvr",'\u251C'); // BOX DRAWINGS LIGHT VERTICAL AND RIGHT
        SGML_MAP.put("boxvR",'\u255E'); // BOX DRAWINGS VERTICAL SINGLE AND RIGHT DOUBLE
        SGML_MAP.put("boxVr",'\u255F'); // BOX DRAWINGS VERTICAL DOUBLE AND RIGHT SINGLE
        SGML_MAP.put("boxVR",'\u2560'); // BOX DRAWINGS DOUBLE VERTICAL AND RIGHT
        SGML_MAP.put("b.Phi",'\u03A6'); // GREEK CAPITAL LETTER PHI
        SGML_MAP.put("b.phis",'\u03C6'); // GREEK SMALL LETTER PHI
        SGML_MAP.put("b.phiv",'\u03D5'); // GREEK PHI SYMBOL
        SGML_MAP.put("b.Pi",'\u03A0'); // GREEK CAPITAL LETTER PI
        SGML_MAP.put("b.pi",'\u03C0'); // GREEK SMALL LETTER PI
        SGML_MAP.put("b.piv",'\u03D6'); // GREEK PI SYMBOL
        SGML_MAP.put("bprime",'\u2035'); // REVERSED PRIME
        SGML_MAP.put("b.Psi",'\u03A8'); // GREEK CAPITAL LETTER PSI
        SGML_MAP.put("b.psi",'\u03C8'); // GREEK SMALL LETTER PSI
        SGML_MAP.put("breve",'\u02D8'); // BREVE
        SGML_MAP.put("b.rho",'\u03C1'); // GREEK SMALL LETTER RHO
        SGML_MAP.put("b.rhov",'\u03F1'); // GREEK RHO SYMBOL
        SGML_MAP.put("brvbar",'\u00A6'); // BROKEN BAR
        SGML_MAP.put("b.Sigma",'\u03A3'); // GREEK CAPITAL LETTER SIGMA
        SGML_MAP.put("b.sigma",'\u03C3'); // GREEK SMALL LETTER SIGMA
        SGML_MAP.put("b.sigmav",'\u03C2'); // GREEK SMALL LETTER FINAL SIGMA
        SGML_MAP.put("bsim",'\u223D'); // REVERSED TILDE
        SGML_MAP.put("bsime",'\u22CD'); // REVERSED TILDE EQUALS
        // SGML_MAP.put("bsol","005C"); // REVERSE SOLIDUS
        SGML_MAP.put("b.tau",'\u03C4'); // GREEK SMALL LETTER TAU
        SGML_MAP.put("b.Theta",'\u0398'); // GREEK CAPITAL LETTER THETA
        SGML_MAP.put("b.thetas",'\u03B8'); // GREEK SMALL LETTER THETA
        SGML_MAP.put("b.thetav",'\u03D1'); // GREEK THETA SYMBOL
        SGML_MAP.put("bull",'\u2022'); // BULLET
        SGML_MAP.put("bump",'\u224E'); // GEOMETRICALLY EQUIVALENT TO
        SGML_MAP.put("bumpe",'\u224F'); // DIFFERENCE BETWEEN
        SGML_MAP.put("b.Upsi",'\u03A5'); // GREEK CAPITAL LETTER UPSILON
        SGML_MAP.put("b.upsi",'\u03C5'); // GREEK SMALL LETTER UPSILON
        SGML_MAP.put("b.Xi",'\u039E'); // GREEK CAPITAL LETTER XI
        SGML_MAP.put("b.xi",'\u03BE'); // GREEK SMALL LETTER XI
        SGML_MAP.put("b.zeta",'\u03B6'); // GREEK SMALL LETTER ZETA
        SGML_MAP.put("Cacute",'\u0106'); // LATIN CAPITAL LETTER C WITH ACUTE
        SGML_MAP.put("cacute",'\u0107'); // LATIN SMALL LETTER C WITH ACUTE
        SGML_MAP.put("Cap",'\u22D2'); // DOUBLE INTERSECTION
        SGML_MAP.put("cap",'\u2229'); // INTERSECTION
        SGML_MAP.put("caret",'\u2041'); // CARET INSERTION POINT
        SGML_MAP.put("caron",'\u02C7'); // CARON
        SGML_MAP.put("Ccaron",'\u010C'); // LATIN CAPITAL LETTER C WITH CARON
        SGML_MAP.put("ccaron",'\u010D'); // LATIN SMALL LETTER C WITH CARON
        SGML_MAP.put("Ccedil",'\u00C7'); // LATIN CAPITAL LETTER C WITH CEDILLA
        SGML_MAP.put("ccedil",'\u00E7'); // LATIN SMALL LETTER C WITH CEDILLA
        SGML_MAP.put("Ccirc",'\u0108'); // LATIN CAPITAL LETTER C WITH CIRCUMFLEX
        SGML_MAP.put("ccirc",'\u0109'); // LATIN SMALL LETTER C WITH CIRCUMFLEX
        SGML_MAP.put("Cdot",'\u010A'); // LATIN CAPITAL LETTER C WITH DOT ABOVE
        SGML_MAP.put("cdot",'\u010B'); // LATIN SMALL LETTER C WITH DOT ABOVE
        SGML_MAP.put("cedil",'\u00B8'); // CEDILLA
        SGML_MAP.put("cent",'\u00A2'); // CENT SIGN
        SGML_MAP.put("CHcy",'\u0427'); // CYRILLIC CAPITAL LETTER CHE
        SGML_MAP.put("chcy",'\u0447'); // CYRILLIC SMALL LETTER CHE
        SGML_MAP.put("check",'\u2713'); // CHECK MARK
        SGML_MAP.put("Chi",'\u03A7'); // GREEK CAPITAL LETTER CHI
        SGML_MAP.put("chi",'\u03C7'); // GREEK SMALL LETTER CHI
        SGML_MAP.put("cir",'\u25CB'); // WHITE CIRCLE
        SGML_MAP.put("circ",'\u02C6'); // MODIFIER LETTER CIRCUMFLEX ACCENT
        SGML_MAP.put("cire",'\u2257'); // RING EQUAL TO
        SGML_MAP.put("clubs",'\u2663'); // BLACK CLUB SUIT
        SGML_MAP.put("colon",'\u003A'); // COLON
        SGML_MAP.put("colone",'\u2254'); // COLON EQUALS
        SGML_MAP.put("comma",'\u002C'); // COMMA
        SGML_MAP.put("commat",'\u0040'); // COMMERCIAL AT
        SGML_MAP.put("comp",'\u2201'); // COMPLEMENT
        SGML_MAP.put("compfn",'\u2218'); // RING OPERATOR
        SGML_MAP.put("cong",'\u2245'); // APPROXIMATELY EQUAL TO
        SGML_MAP.put("conint",'\u222E'); // CONTOUR INTEGRAL
        SGML_MAP.put("coprod",'\u2210'); // N-ARY COPRODUCT
        SGML_MAP.put("copy",'\u00A9'); // COPYRIGHT SIGN
        SGML_MAP.put("copysr",'\u2117'); // SOUND RECORDING COPYRIGHT
        SGML_MAP.put("crarr",'\u21B5'); // DOWNWARDS ARROW WITH CORNER LEFTWARDS
        SGML_MAP.put("cross",'\u2717'); // BALLOT X
        SGML_MAP.put("cuepr",'\u22DE'); // EQUAL TO OR PRECEDES
        SGML_MAP.put("cuesc",'\u22DF'); // EQUAL TO OR SUCCEEDS
        SGML_MAP.put("cularr",'\u21B6'); // ANTICLOCKWISE TOP SEMICIRCLE ARROW
        SGML_MAP.put("Cup",'\u22D3'); // DOUBLE UNION
        SGML_MAP.put("cup",'\u222A'); // UNION
        SGML_MAP.put("cupre",'\u227C'); // PRECEDES OR EQUAL TO
        SGML_MAP.put("curarr",'\u21B7'); // CLOCKWISE TOP SEMICIRCLE ARROW
        SGML_MAP.put("curren",'\u00A4'); // CURRENCY SIGN
        SGML_MAP.put("cuvee",'\u22CE'); // CURLY LOGICAL OR
        SGML_MAP.put("cuwed",'\u22CF'); // CURLY LOGICAL AND
        SGML_MAP.put("dagger",'\u2020'); // DAGGER
        SGML_MAP.put("Dagger",'\u2021'); // DOUBLE DAGGER
        SGML_MAP.put("daleth",'\u2138'); // DALET SYMBOL
        SGML_MAP.put("dArr",'\u21D3'); // DOWNWARDS DOUBLE ARROW
        SGML_MAP.put("darr",'\u2193'); // DOWNWARDS ARROW
        SGML_MAP.put("darr2",'\u21CA'); // DOWNWARDS PAIRED ARROWS
        SGML_MAP.put("dash",'\u2010'); // HYPHEN
        SGML_MAP.put("dashv",'\u22A3'); // LEFT TACK
        SGML_MAP.put("dblac",'\u02DD'); // DOUBLE ACUTE ACCENT
        SGML_MAP.put("Dcaron",'\u010E'); // LATIN CAPITAL LETTER D WITH CARON
        SGML_MAP.put("dcaron",'\u010F'); // LATIN SMALL LETTER D WITH CARON
        SGML_MAP.put("Dcy",'\u0414'); // CYRILLIC CAPITAL LETTER DE
        SGML_MAP.put("dcy",'\u0434'); // CYRILLIC SMALL LETTER DE
        SGML_MAP.put("deg",'\u00B0'); // DEGREE SIGN
        SGML_MAP.put("Delta",'\u0394'); // GREEK CAPITAL LETTER DELTA
        SGML_MAP.put("delta",'\u03B4'); // GREEK SMALL LETTER DELTA
        SGML_MAP.put("Dgr",'\u0394'); // GREEK CAPITAL LETTER DELTA
        SGML_MAP.put("dgr",'\u03B4'); // GREEK SMALL LETTER DELTA
        SGML_MAP.put("dharl",'\u21C3'); // DOWNWARDS HARPOON WITH BARB LEFTWARDS
        SGML_MAP.put("dharr",'\u21C2'); // DOWNWARDS HARPOON WITH BARB RIGHTWARDS
        SGML_MAP.put("diam",'\u22C4'); // DIAMOND OPERATOR
        SGML_MAP.put("diams",'\u2666'); // BLACK DIAMOND SUIT
        SGML_MAP.put("die",'\u00A8'); // DIAERESIS
        SGML_MAP.put("divide",'\u00F7'); // DIVISION SIGN
        SGML_MAP.put("divonx",'\u22C7'); // DIVISION TIMES
        SGML_MAP.put("DJcy",'\u0402'); // CYRILLIC CAPITAL LETTER DJE
        SGML_MAP.put("djcy",'\u0452'); // CYRILLIC SMALL LETTER DJE
        SGML_MAP.put("dlarr",'\u2199'); // SOUTH WEST ARROW
        SGML_MAP.put("dlcorn",'\u231E'); // BOTTOM LEFT CORNER
        SGML_MAP.put("dlcrop",'\u230D'); // BOTTOM LEFT CROP
        SGML_MAP.put("dollar",'\u0024'); // DOLLAR SIGN
        SGML_MAP.put("dot",'\u02D9'); // DOT ABOVE
        SGML_MAP.put("Dot",'\u00A8'); // DIAERESIS
        SGML_MAP.put("DotDot",'\u20DC'); // COMBINING FOUR DOTS ABOVE
        SGML_MAP.put("drarr",'\u2198'); // SOUTH EAST ARROW
        SGML_MAP.put("drcorn",'\u231F'); // BOTTOM RIGHT CORNER
        SGML_MAP.put("drcrop",'\u230C'); // BOTTOM RIGHT CROP
        SGML_MAP.put("DScy",'\u0405'); // CYRILLIC CAPITAL LETTER DZE
        SGML_MAP.put("dscy",'\u0455'); // CYRILLIC SMALL LETTER DZE
        SGML_MAP.put("Dstrok",'\u0110'); // LATIN CAPITAL LETTER D WITH STROKE
        SGML_MAP.put("dstrok",'\u0111'); // LATIN SMALL LETTER D WITH STROKE
        SGML_MAP.put("dtri",'\u25BF'); // WHITE DOWN-POINTING SMALL TRIANGLE
        SGML_MAP.put("dtrif",'\u25BE'); // BLACK DOWN-POINTING SMALL TRIANGLE
        SGML_MAP.put("DZcy",'\u040F'); // CYRILLIC CAPITAL LETTER DZHE
        SGML_MAP.put("dzcy",'\u045F'); // CYRILLIC SMALL LETTER DZHE
        SGML_MAP.put("Eacgr",'\u0388'); // GREEK CAPITAL LETTER EPSILON WITH TONOS
        SGML_MAP.put("eacgr",'\u03AD'); // GREEK SMALL LETTER EPSILON WITH TONOS
        SGML_MAP.put("Eacute",'\u00C9'); // LATIN CAPITAL LETTER E WITH ACUTE
        SGML_MAP.put("eacute",'\u00E9'); // LATIN SMALL LETTER E WITH ACUTE
        SGML_MAP.put("Ecaron",'\u011A'); // LATIN CAPITAL LETTER E WITH CARON
        SGML_MAP.put("ecaron",'\u011B'); // LATIN SMALL LETTER E WITH CARON
        SGML_MAP.put("ecir",'\u2256'); // RING IN EQUAL TO
        SGML_MAP.put("Ecirc",'\u00CA'); // LATIN CAPITAL LETTER E WITH CIRCUMFLEX
        SGML_MAP.put("ecirc",'\u00EA'); // LATIN SMALL LETTER E WITH CIRCUMFLEX
        SGML_MAP.put("ecolon",'\u2255'); // EQUALS COLON
        SGML_MAP.put("Ecy",'\u042D'); // CYRILLIC CAPITAL LETTER E
        SGML_MAP.put("ecy",'\u044D'); // CYRILLIC SMALL LETTER E
        SGML_MAP.put("eDot",'\u2251'); // GEOMETRICALLY EQUAL TO
        SGML_MAP.put("Edot",'\u0116'); // LATIN CAPITAL LETTER E WITH DOT ABOVE
        SGML_MAP.put("edot",'\u0117'); // LATIN SMALL LETTER E WITH DOT ABOVE
        SGML_MAP.put("EEacgr",'\u0389'); // GREEK CAPITAL LETTER ETA WITH TONOS
        SGML_MAP.put("eeacgr",'\u03AE'); // GREEK SMALL LETTER ETA WITH TONOS
        SGML_MAP.put("EEgr",'\u0397'); // GREEK CAPITAL LETTER ETA
        SGML_MAP.put("eegr",'\u03B7'); // GREEK SMALL LETTER ETA
        SGML_MAP.put("efDot",'\u2252'); // APPROXIMATELY EQUAL TO OR THE IMAGE OF
        SGML_MAP.put("Egr",'\u0395'); // GREEK CAPITAL LETTER EPSILON
        SGML_MAP.put("egr",'\u03B5'); // GREEK SMALL LETTER EPSILON
        SGML_MAP.put("Egrave",'\u00C8'); // LATIN CAPITAL LETTER E WITH GRAVE
        SGML_MAP.put("egrave",'\u00E8'); // LATIN SMALL LETTER E WITH GRAVE
        SGML_MAP.put("egs",'\u22DD'); // EQUAL TO OR GREATER-THAN
        SGML_MAP.put("ell",'\u2113'); // SCRIPT SMALL L
        SGML_MAP.put("els",'\u22DC'); // EQUAL TO OR LESS-THAN
        SGML_MAP.put("Emacr",'\u0112'); // LATIN CAPITAL LETTER E WITH MACRON
        SGML_MAP.put("emacr",'\u0113'); // LATIN SMALL LETTER E WITH MACRON
        SGML_MAP.put("empty",'\u2205'); // EMPTY SET
        SGML_MAP.put("emsp",'\u2003'); // EM SPACE
        SGML_MAP.put("emsp13",'\u2004'); // THREE-PER-EM SPACE
        SGML_MAP.put("emsp14",'\u2005'); // FOUR-PER-EM SPACE
        SGML_MAP.put("ENG",'\u014A'); // LATIN CAPITAL LETTER ENG
        SGML_MAP.put("eng",'\u014B'); // LATIN SMALL LETTER ENG
        SGML_MAP.put("ensp",'\u2002'); // EN SPACE
        SGML_MAP.put("Eogon",'\u0118'); // LATIN CAPITAL LETTER E WITH OGONEK
        SGML_MAP.put("eogon",'\u0119'); // LATIN SMALL LETTER E WITH OGONEK
        SGML_MAP.put("epsi",'\u03B5'); // GREEK SMALL LETTER EPSILON
        SGML_MAP.put("Epsilon",'\u0395'); // GREEK CAPITAL LETTER EPSILON
        SGML_MAP.put("epsilon",'\u03B5'); // GREEK SMALL LETTER EPSILON
        SGML_MAP.put("epsis",'\u220A'); // SMALL ELEMENT OF
        // SGML_MAP.put("epsiv",'?'); // variant epsilon
        SGML_MAP.put("equals",'\u003D'); // EQUALS SIGN
        SGML_MAP.put("equiv",'\u2261'); // IDENTICAL TO
        SGML_MAP.put("erDot",'\u2253'); // IMAGE OF OR APPROXIMATELY EQUAL TO
        SGML_MAP.put("esdot",'\u2250'); // APPROACHES THE LIMIT
        SGML_MAP.put("Eta",'\u0397'); // GREEK CAPITAL LETTER ETA
        SGML_MAP.put("eta",'\u03B7'); // GREEK SMALL LETTER ETA
        SGML_MAP.put("ETH",'\u00D0'); // LATIN CAPITAL LETTER ETH
        SGML_MAP.put("eth",'\u00F0'); // LATIN SMALL LETTER ETH
        SGML_MAP.put("Euml",'\u00CB'); // LATIN CAPITAL LETTER E WITH DIAERESIS
        SGML_MAP.put("euml",'\u00EB'); // LATIN SMALL LETTER E WITH DIAERESIS
        SGML_MAP.put("excl",'\u0021'); // EXCLAMATION MARK
        SGML_MAP.put("exist",'\u2203'); // THERE EXISTS
        SGML_MAP.put("Fcy",'\u0424'); // CYRILLIC CAPITAL LETTER EF
        SGML_MAP.put("fcy",'\u0444'); // CYRILLIC SMALL LETTER EF
        SGML_MAP.put("female",'\u2640'); // FEMALE SIGN
        SGML_MAP.put("ffilig",'\uFB03'); // LATIN SMALL LIGATURE FFI
        SGML_MAP.put("fflig",'\uFB00'); // LATIN SMALL LIGATURE FF
        SGML_MAP.put("ffllig",'\uFB04'); // LATIN SMALL LIGATURE FFL
        SGML_MAP.put("filig",'\uFB01'); // LATIN SMALL LIGATURE FI
        // SGML_MAP.put("fjlig",'?'); // fj ligature
        SGML_MAP.put("flat",'\u266D'); // MUSIC FLAT SIGN
        SGML_MAP.put("fllig",'\uFB02'); // LATIN SMALL LIGATURE FL
        SGML_MAP.put("fnof",'\u0192'); // LATIN SMALL LETTER F WITH HOOK
        SGML_MAP.put("forall",'\u2200'); // FOR ALL
        SGML_MAP.put("fork",'\u22D4'); // PITCHFORK
        SGML_MAP.put("frac12",'\u00BD'); // VULGAR FRACTION ONE HALF
        SGML_MAP.put("frac13",'\u2153'); // VULGAR FRACTION ONE THIRD
        SGML_MAP.put("frac14",'\u00BC'); // VULGAR FRACTION ONE QUARTER
        SGML_MAP.put("frac15",'\u2155'); // VULGAR FRACTION ONE FIFTH
        SGML_MAP.put("frac16",'\u2159'); // VULGAR FRACTION ONE SIXTH
        SGML_MAP.put("frac18",'\u215B'); // VULGAR FRACTION ONE EIGHTH
        SGML_MAP.put("frac23",'\u2154'); // VULGAR FRACTION TWO THIRDS
        SGML_MAP.put("frac25",'\u2156'); // VULGAR FRACTION TWO FIFTHS
        SGML_MAP.put("frac34",'\u00BE'); // VULGAR FRACTION THREE QUARTERS
        SGML_MAP.put("frac35",'\u2157'); // VULGAR FRACTION THREE FIFTHS
        SGML_MAP.put("frac38",'\u215C'); // VULGAR FRACTION THREE EIGHTHS
        SGML_MAP.put("frac45",'\u2158'); // VULGAR FRACTION FOUR FIFTHS
        SGML_MAP.put("frac56",'\u215A'); // VULGAR FRACTION FIVE SIXTHS
        SGML_MAP.put("frac58",'\u215D'); // VULGAR FRACTION FIVE EIGHTHS
        SGML_MAP.put("frac78",'\u215E'); // VULGAR FRACTION SEVEN EIGHTHS
        SGML_MAP.put("frasl",'\u2044'); // FRACTION SLASH
        SGML_MAP.put("frown",'\u2322'); // FROWN
        SGML_MAP.put("gacute",'\u01F5'); // LATIN SMALL LETTER G WITH ACUTE
        SGML_MAP.put("Gamma",'\u0393'); // GREEK CAPITAL LETTER GAMMA
        SGML_MAP.put("gamma",'\u03B3'); // GREEK SMALL LETTER GAMMA
        SGML_MAP.put("gammad",'\u03DC'); // GREEK LETTER DIGAMMA
        // SGML_MAP.put("gap",'?'); // greater-than, approximately equal to
        SGML_MAP.put("Gbreve",'\u011E'); // LATIN CAPITAL LETTER G WITH BREVE
        SGML_MAP.put("gbreve",'\u011F'); // LATIN SMALL LETTER G WITH BREVE
        SGML_MAP.put("Gcedil",'\u0122'); // LATIN CAPITAL LETTER G WITH CEDILLA
        SGML_MAP.put("gcedil",'\u0123'); // LATIN SMALL LETTER G WITH CEDILLA
        SGML_MAP.put("Gcirc",'\u011C'); // LATIN CAPITAL LETTER G WITH CIRCUMFLEX
        SGML_MAP.put("gcirc",'\u011D'); // LATIN SMALL LETTER G WITH CIRCUMFLEX
        SGML_MAP.put("Gcy",'\u0413'); // CYRILLIC CAPITAL LETTER GHE
        SGML_MAP.put("gcy",'\u0433'); // CYRILLIC SMALL LETTER GHE
        SGML_MAP.put("Gdot",'\u0120'); // LATIN CAPITAL LETTER G WITH DOT ABOVE
        SGML_MAP.put("gdot",'\u0121'); // LATIN SMALL LETTER G WITH DOT ABOVE
        SGML_MAP.put("gE",'\u2267'); // GREATER-THAN OVER EQUAL TO
        SGML_MAP.put("ge",'\u2265'); // GREATER-THAN OR EQUAL TO
        // SGML_MAP.put("gEl",'?'); // greater-than, double equals, less-than
        SGML_MAP.put("gel",'\u22DB'); // GREATER-THAN EQUAL TO OR LESS-THAN
        SGML_MAP.put("ges",'\u2265'); // GREATER-THAN OR EQUAL TO
        SGML_MAP.put("Gg",'\u22D9'); // VERY MUCH GREATER-THAN
        SGML_MAP.put("Ggr",'\u0393'); // GREEK CAPITAL LETTER GAMMA
        SGML_MAP.put("ggr",'\u03B3'); // GREEK SMALL LETTER GAMMA
        SGML_MAP.put("gimel",'\u2137'); // GIMEL SYMBOL
        SGML_MAP.put("GJcy",'\u0403'); // CYRILLIC CAPITAL LETTER GJE
        SGML_MAP.put("gjcy",'\u0453'); // CYRILLIC SMALL LETTER GJE
        SGML_MAP.put("gl",'\u2277'); // GREATER-THAN OR LESS-THAN
        // SGML_MAP.put("gnap",'?'); // greater-than, not approximately equal to
        SGML_MAP.put("gne",'\u2269'); // GREATER-THAN BUT NOT EQUAL TO
        SGML_MAP.put("gnE",'\u2269'); // GREATER-THAN BUT NOT EQUAL TO
        SGML_MAP.put("gnsim",'\u22E7'); // GREATER-THAN BUT NOT EQUIVALENT TO
        SGML_MAP.put("grave",'\u0060'); // GRAVE ACCENT
        SGML_MAP.put("gsdot",'\u22D7'); // GREATER-THAN WITH DOT
        SGML_MAP.put("gsim",'\u2273'); // GREATER-THAN OR EQUIVALENT TO
        SGML_MAP.put("Gt",'\u226B'); // MUCH GREATER-THAN
        SGML_MAP.put("gt",'\u003E'); // GREATER-THAN SIGN
        SGML_MAP.put("gvnE",'\u2269'); // GREATER-THAN BUT NOT EQUAL TO
        SGML_MAP.put("hairsp",'\u200A'); // HAIR SPACE
        SGML_MAP.put("half",'\u00BD'); // VULGAR FRACTION ONE HALF
        SGML_MAP.put("hamilt",'\u210B'); // SCRIPT CAPITAL H
        SGML_MAP.put("HARDcy",'\u042A'); // CYRILLIC CAPITAL LETTER HARD SIGN
        SGML_MAP.put("hardcy",'\u044A'); // CYRILLIC SMALL LETTER HARD SIGN
        SGML_MAP.put("harr",'\u2194'); // LEFT RIGHT ARROW
        SGML_MAP.put("hArr",'\u21D4'); // LEFT RIGHT DOUBLE ARROW
        SGML_MAP.put("harrw",'\u21AD'); // LEFT RIGHT WAVE ARROW
        SGML_MAP.put("Hcirc",'\u0124'); // LATIN CAPITAL LETTER H WITH CIRCUMFLEX
        SGML_MAP.put("hcirc",'\u0125'); // LATIN SMALL LETTER H WITH CIRCUMFLEX
        SGML_MAP.put("hearts",'\u2665'); // BLACK HEART SUIT
        SGML_MAP.put("hellip",'\u2026'); // HORIZONTAL ELLIPSIS
        SGML_MAP.put("horbar",'\u2015'); // HORIZONTAL BAR
        SGML_MAP.put("Hstrok",'\u0126'); // LATIN CAPITAL LETTER H WITH STROKE
        SGML_MAP.put("hstrok",'\u0127'); // LATIN SMALL LETTER H WITH STROKE
        SGML_MAP.put("hybull",'\u2043'); // HYPHEN BULLET
        SGML_MAP.put("hyphen",'\u002D'); // HYPHEN-MINUS
        SGML_MAP.put("Iacgr",'\u038A'); // GREEK CAPITAL LETTER IOTA WITH TONOS
        SGML_MAP.put("iacgr",'\u03AF'); // GREEK SMALL LETTER IOTA WITH TONOS
        SGML_MAP.put("Iacute",'\u00CD'); // LATIN CAPITAL LETTER I WITH ACUTE
        SGML_MAP.put("iacute",'\u00ED'); // LATIN SMALL LETTER I WITH ACUTE
        SGML_MAP.put("Icirc",'\u00CE'); // LATIN CAPITAL LETTER I WITH CIRCUMFLEX
        SGML_MAP.put("icirc",'\u00EE'); // LATIN SMALL LETTER I WITH CIRCUMFLEX
        SGML_MAP.put("Icy",'\u0418'); // CYRILLIC CAPITAL LETTER I
        SGML_MAP.put("icy",'\u0438'); // CYRILLIC SMALL LETTER I
        SGML_MAP.put("idiagr",'\u0390'); // GREEK SMALL LETTER IOTA WITH DIALYTIKA AND TONOS
        SGML_MAP.put("Idigr",'\u03AA'); // GREEK CAPITAL LETTER IOTA WITH DIALYTIKA
        SGML_MAP.put("idigr",'\u03CA'); // GREEK SMALL LETTER IOTA WITH DIALYTIKA
        SGML_MAP.put("Idot",'\u0130'); // LATIN CAPITAL LETTER I WITH DOT ABOVE
        SGML_MAP.put("IEcy",'\u0415'); // CYRILLIC CAPITAL LETTER IE
        SGML_MAP.put("iecy",'\u0435'); // CYRILLIC SMALL LETTER IE
        SGML_MAP.put("iexcl",'\u00A1'); // INVERTED EXCLAMATION MARK
        SGML_MAP.put("iff",'\u21D4'); // LEFT RIGHT DOUBLE ARROW
        SGML_MAP.put("Igr",'\u0399'); // GREEK CAPITAL LETTER IOTA
        SGML_MAP.put("igr",'\u03B9'); // GREEK SMALL LETTER IOTA
        SGML_MAP.put("Igrave",'\u00CC'); // LATIN CAPITAL LETTER I WITH GRAVE
        SGML_MAP.put("igrave",'\u00EC'); // LATIN SMALL LETTER I WITH GRAVE
        SGML_MAP.put("IJlig",'\u0132'); // LATIN CAPITAL LIGATURE IJ
        SGML_MAP.put("ijlig",'\u0133'); // LATIN SMALL LIGATURE IJ
        SGML_MAP.put("Imacr",'\u012A'); // LATIN CAPITAL LETTER I WITH MACRON
        SGML_MAP.put("imacr",'\u012B'); // LATIN SMALL LETTER I WITH MACRON
        SGML_MAP.put("image",'\u2111'); // BLACK-LETTER CAPITAL I
        SGML_MAP.put("incare",'\u2105'); // CARE OF
        SGML_MAP.put("infin",'\u221E'); // INFINITY
        SGML_MAP.put("inodot",'\u0131'); // LATIN SMALL LETTER DOTLESS I
        SGML_MAP.put("inodot",'\u0131'); // LATIN SMALL LETTER DOTLESS I
        SGML_MAP.put("int",'\u222B'); // INTEGRAL
        SGML_MAP.put("intcal",'\u22BA'); // INTERCALATE
        SGML_MAP.put("IOcy",'\u0401'); // CYRILLIC CAPITAL LETTER IO
        SGML_MAP.put("iocy",'\u0451'); // CYRILLIC SMALL LETTER IO
        SGML_MAP.put("Iogon",'\u012E'); // LATIN CAPITAL LETTER I WITH OGONEK
        SGML_MAP.put("iogon",'\u012F'); // LATIN SMALL LETTER I WITH OGONEK
        SGML_MAP.put("Iota",'\u0399'); // GREEK CAPITAL LETTER IOTA
        SGML_MAP.put("iota",'\u03B9'); // GREEK SMALL LETTER IOTA
        SGML_MAP.put("iquest",'\u00BF'); // INVERTED QUESTION MARK
        SGML_MAP.put("isin",'\u2208'); // ELEMENT OF
        SGML_MAP.put("Itilde",'\u0128'); // LATIN CAPITAL LETTER I WITH TILDE
        SGML_MAP.put("itilde",'\u0129'); // LATIN SMALL LETTER I WITH TILDE
        SGML_MAP.put("Iukcy",'\u0406'); // CYRILLIC CAPITAL LETTER BYELORUSSIAN-UKRAINIAN I
        SGML_MAP.put("iukcy",'\u0456'); // CYRILLIC SMALL LETTER BYELORUSSIAN-UKRAINIAN I
        SGML_MAP.put("Iuml",'\u00CF'); // LATIN CAPITAL LETTER I WITH DIAERESIS
        SGML_MAP.put("iuml",'\u00EF'); // LATIN SMALL LETTER I WITH DIAERESIS
        SGML_MAP.put("Jcirc",'\u0134'); // LATIN CAPITAL LETTER J WITH CIRCUMFLEX
        SGML_MAP.put("jcirc",'\u0135'); // LATIN SMALL LETTER J WITH CIRCUMFLEX
        SGML_MAP.put("Jcy",'\u0419'); // CYRILLIC CAPITAL LETTER SHORT I
        SGML_MAP.put("jcy",'\u0439'); // CYRILLIC SMALL LETTER SHORT I
        // SGML_MAP.put("jnodot",'?'); // latin small letter dotless j
        SGML_MAP.put("Jsercy",'\u0408'); // CYRILLIC CAPITAL LETTER JE
        SGML_MAP.put("jsercy",'\u0458'); // CYRILLIC SMALL LETTER JE
        SGML_MAP.put("Jukcy",'\u0404'); // CYRILLIC CAPITAL LETTER UKRAINIAN IE
        SGML_MAP.put("jukcy",'\u0454'); // CYRILLIC SMALL LETTER UKRAINIAN IE
        SGML_MAP.put("Kappa",'\u039A'); // GREEK CAPITAL LETTER KAPPA
        SGML_MAP.put("kappa",'\u03BA'); // GREEK SMALL LETTER KAPPA
        SGML_MAP.put("kappav",'\u03F0'); // GREEK KAPPA SYMBOL
        SGML_MAP.put("Kcedil",'\u0136'); // LATIN CAPITAL LETTER K WITH CEDILLA
        SGML_MAP.put("kcedil",'\u0137'); // LATIN SMALL LETTER K WITH CEDILLA
        SGML_MAP.put("Kcy",'\u041A'); // CYRILLIC CAPITAL LETTER KA
        SGML_MAP.put("kcy",'\u043A'); // CYRILLIC SMALL LETTER KA
        SGML_MAP.put("Kgr",'\u039A'); // GREEK CAPITAL LETTER KAPPA
        SGML_MAP.put("kgr",'\u03BA'); // GREEK SMALL LETTER KAPPA
        SGML_MAP.put("kgreen",'\u0138'); // LATIN SMALL LETTER KRA
        SGML_MAP.put("KHcy",'\u0425'); // CYRILLIC CAPITAL LETTER HA
        SGML_MAP.put("khcy",'\u0445'); // CYRILLIC SMALL LETTER HA
        SGML_MAP.put("KHgr",'\u03A7'); // GREEK CAPITAL LETTER CHI
        SGML_MAP.put("khgr",'\u03C7'); // GREEK SMALL LETTER CHI
        SGML_MAP.put("KJcy",'\u040C'); // CYRILLIC CAPITAL LETTER KJE
        SGML_MAP.put("kjcy",'\u045C'); // CYRILLIC SMALL LETTER KJE
        SGML_MAP.put("lAarr",'\u21DA'); // LEFTWARDS TRIPLE ARROW
        SGML_MAP.put("Lacute",'\u0139'); // LATIN CAPITAL LETTER L WITH ACUTE
        SGML_MAP.put("lacute",'\u013A'); // LATIN SMALL LETTER L WITH ACUTE
        SGML_MAP.put("lagran",'\u2112'); // SCRIPT CAPITAL L
        SGML_MAP.put("Lambda",'\u039B'); // GREEK CAPITAL LETTER LAMDA
        SGML_MAP.put("lambda",'\u03BB'); // GREEK SMALL LETTER LAMDA
        SGML_MAP.put("lang",'\u2329'); // LEFT-POINTING ANGLE BRACKET
        // SGML_MAP.put("lap",'?'); // less-than, approximately equal to
        SGML_MAP.put("laquo",'\u00AB'); // LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
        SGML_MAP.put("Larr",'\u219E'); // LEFTWARDS TWO HEADED ARROW
        SGML_MAP.put("larr",'\u2190'); // LEFTWARDS ARROW
        SGML_MAP.put("lArr",'\u21D0'); // LEFTWARDS DOUBLE ARROW
        SGML_MAP.put("larr2",'\u21C7'); // LEFTWARDS PAIRED ARROWS
        SGML_MAP.put("larrhk",'\u21A9'); // LEFTWARDS ARROW WITH HOOK
        SGML_MAP.put("larrlp",'\u21AB'); // LEFTWARDS ARROW WITH LOOP
        SGML_MAP.put("larrtl",'\u21A2'); // LEFTWARDS ARROW WITH TAIL
        SGML_MAP.put("Lcaron",'\u013D'); // LATIN CAPITAL LETTER L WITH CARON
        SGML_MAP.put("lcaron",'\u013E'); // LATIN SMALL LETTER L WITH CARON
        SGML_MAP.put("Lcedil",'\u013B'); // LATIN CAPITAL LETTER L WITH CEDILLA
        SGML_MAP.put("lcedil",'\u013C'); // LATIN SMALL LETTER L WITH CEDILLA
        SGML_MAP.put("lceil",'\u2308'); // LEFT CEILING
        SGML_MAP.put("lcub",'\u007B'); // LEFT CURLY BRACKET
        SGML_MAP.put("Lcy",'\u041B'); // CYRILLIC CAPITAL LETTER EL
        SGML_MAP.put("lcy",'\u043B'); // CYRILLIC SMALL LETTER EL
        SGML_MAP.put("ldot",'\u22D6'); // LESS-THAN WITH DOT
        SGML_MAP.put("ldquo",'\u201C'); // LEFT DOUBLE QUOTATION MARK
        SGML_MAP.put("ldquor",'\u201E'); // DOUBLE LOW-9 QUOTATION MARK
        SGML_MAP.put("lE",'\u2266'); // LESS-THAN OVER EQUAL TO
        SGML_MAP.put("le",'\u2264'); // LESS-THAN OR EQUAL TO
        // SGML_MAP.put("lEg",'?'); // less-than, double equals, greater-than
        SGML_MAP.put("leg",'\u22DA'); // LESS-THAN EQUAL TO OR GREATER-THAN
        SGML_MAP.put("les",'\u2264'); // LESS-THAN OR EQUAL TO
        SGML_MAP.put("lfloor",'\u230A'); // LEFT FLOOR
        SGML_MAP.put("lg",'\u2276'); // LESS-THAN OR GREATER-THAN
        SGML_MAP.put("Lgr",'\u039B'); // GREEK CAPITAL LETTER LAMDA
        SGML_MAP.put("lgr",'\u03BB'); // GREEK SMALL LETTER LAMDA
        SGML_MAP.put("lhard",'\u21BD'); // LEFTWARDS HARPOON WITH BARB DOWNWARDS
        SGML_MAP.put("lharu",'\u21BC'); // LEFTWARDS HARPOON WITH BARB UPWARDS
        SGML_MAP.put("lhblk",'\u2584'); // LOWER HALF BLOCK
        SGML_MAP.put("LJcy",'\u0409'); // CYRILLIC CAPITAL LETTER LJE
        SGML_MAP.put("ljcy",'\u0459'); // CYRILLIC SMALL LETTER LJE
        SGML_MAP.put("Ll",'\u22D8'); // VERY MUCH LESS-THAN
        SGML_MAP.put("Lmidot",'\u013F'); // LATIN CAPITAL LETTER L WITH MIDDLE DOT
        SGML_MAP.put("lmidot",'\u0140'); // LATIN SMALL LETTER L WITH MIDDLE DOT
        // SGML_MAP.put("lnap",'?'); // less-than, not approximately equal to
        SGML_MAP.put("lnE",'\u2268'); // LESS-THAN BUT NOT EQUAL TO
        SGML_MAP.put("lne",'\u2268'); // LESS-THAN BUT NOT EQUAL TO
        SGML_MAP.put("lnsim",'\u22E6'); // LESS-THAN BUT NOT EQUIVALENT TO
        SGML_MAP.put("lowast",'\u2217'); // ASTERISK OPERATOR
        SGML_MAP.put("lowbar",'\u005F'); // LOW LINE
        SGML_MAP.put("loz",'\u25CA'); // LOZENGE
        SGML_MAP.put("loz",'\u2727'); // WHITE FOUR POINTED STAR
        SGML_MAP.put("lozf",'\u2726'); // BLACK FOUR POINTED STAR
        SGML_MAP.put("lpar",'\u0028'); // LEFT PARENTHESIS
        // SGML_MAP.put("lpargt",'?'); // left parenthesis, greater-than
        SGML_MAP.put("lrarr2",'\u21C6'); // LEFTWARDS ARROW OVER RIGHTWARDS ARROW
        SGML_MAP.put("lrhar2",'\u21CB'); // LEFTWARDS HARPOON OVER RIGHTWARDS HARPOON
        SGML_MAP.put("lrm",'\u200E'); // LEFT-TO-RIGHT MARK
        SGML_MAP.put("lsaquo",'\u2039'); // SINGLE LEFT-POINTING ANGLE QUOTATION MARK
        SGML_MAP.put("lsh",'\u21B0'); // UPWARDS ARROW WITH TIP LEFTWARDS
        SGML_MAP.put("lsim",'\u2272'); // LESS-THAN OR EQUIVALENT TO
        SGML_MAP.put("lsqb",'\u005B'); // LEFT SQUARE BRACKET
        SGML_MAP.put("lsquo",'\u2018'); // LEFT SINGLE QUOTATION MARK
        SGML_MAP.put("lsquor",'\u201A'); // SINGLE LOW-9 QUOTATION MARK
        SGML_MAP.put("Lstrok",'\u0141'); // LATIN CAPITAL LETTER L WITH STROKE
        SGML_MAP.put("lstrok",'\u0142'); // LATIN SMALL LETTER L WITH STROKE
        SGML_MAP.put("Lt",'\u226A'); // MUCH LESS-THAN
        SGML_MAP.put("lt",'\u003C'); // LESS-THAN SIGN
        SGML_MAP.put("lthree",'\u22CB'); // LEFT SEMIDIRECT PRODUCT
        SGML_MAP.put("ltimes",'\u22C9'); // LEFT NORMAL FACTOR SEMIDIRECT PRODUCT
        SGML_MAP.put("ltri",'\u25C3'); // WHITE LEFT-POINTING SMALL TRIANGLE
        SGML_MAP.put("ltrie",'\u22B4'); // NORMAL SUBGROUP OF OR EQUAL TO
        SGML_MAP.put("ltrif",'\u25C2'); // BLACK LEFT-POINTING SMALL TRIANGLE
        SGML_MAP.put("lvnE",'\u2268'); // LESS-THAN BUT NOT EQUAL TO
        SGML_MAP.put("macr",'\u00AF'); // MACRON
        SGML_MAP.put("male",'\u2642'); // MALE SIGN
        SGML_MAP.put("malt",'\u2720'); // MALTESE CROSS
        SGML_MAP.put("map",'\u21A6'); // RIGHTWARDS ARROW FROM BAR
        SGML_MAP.put("marker",'\u25AE'); // BLACK VERTICAL RECTANGLE
        SGML_MAP.put("Mcy",'\u041C'); // CYRILLIC CAPITAL LETTER EM
        SGML_MAP.put("mcy",'\u043C'); // CYRILLIC SMALL LETTER EM
        SGML_MAP.put("mdash",'\u2014'); // EM DASH
        SGML_MAP.put("Mgr",'\u039C'); // GREEK CAPITAL LETTER MU
        SGML_MAP.put("mgr",'\u03BC'); // GREEK SMALL LETTER MU
        SGML_MAP.put("micro",'\u00B5'); // MICRO SIGN
        SGML_MAP.put("mid",'\u2223'); // DIVIDES
        SGML_MAP.put("middot",'\u00B7'); // MIDDLE DOT
        SGML_MAP.put("minus",'\u2212'); // MINUS SIGN
        SGML_MAP.put("minusb",'\u229F'); // SQUARED MINUS
        SGML_MAP.put("mldr",'\u2026'); // HORIZONTAL ELLIPSIS
        SGML_MAP.put("mnplus",'\u2213'); // MINUS-OR-PLUS SIGN
        SGML_MAP.put("models",'\u22A7'); // MODELS
        SGML_MAP.put("Mu",'\u039C'); // GREEK CAPITAL LETTER MU
        SGML_MAP.put("mu",'\u03BC'); // GREEK SMALL LETTER MU
        SGML_MAP.put("mumap",'\u22B8'); // MULTIMAP
        SGML_MAP.put("nabla",'\u2207'); // NABLA
        SGML_MAP.put("Nacute",'\u0143'); // LATIN CAPITAL LETTER N WITH ACUTE
        SGML_MAP.put("nacute",'\u0144'); // LATIN SMALL LETTER N WITH ACUTE
        SGML_MAP.put("nap",'\u2249'); // NOT ALMOST EQUAL TO
        SGML_MAP.put("napos",'\u0149'); // LATIN SMALL LETTER N PRECEDED BY APOSTROPHE
        SGML_MAP.put("natur",'\u266E'); // MUSIC NATURAL SIGN
        SGML_MAP.put("nbsp",'\u00A0'); // NO-BREAK SPACE
        SGML_MAP.put("Ncaron",'\u0147'); // LATIN CAPITAL LETTER N WITH CARON
        SGML_MAP.put("ncaron",'\u0148'); // LATIN SMALL LETTER N WITH CARON
        SGML_MAP.put("Ncedil",'\u0145'); // LATIN CAPITAL LETTER N WITH CEDILLA
        SGML_MAP.put("ncedil",'\u0146'); // LATIN SMALL LETTER N WITH CEDILLA
        SGML_MAP.put("ncong",'\u2247'); // NEITHER APPROXIMATELY NOR ACTUALLY EQUAL TO
        SGML_MAP.put("Ncy",'\u041D'); // CYRILLIC CAPITAL LETTER EN
        SGML_MAP.put("ncy",'\u043D'); // CYRILLIC SMALL LETTER EN
        SGML_MAP.put("ndash",'\u2013'); // EN DASH
        SGML_MAP.put("ne",'\u2260'); // NOT EQUAL TO
        SGML_MAP.put("nearr",'\u2197'); // NORTH EAST ARROW
        SGML_MAP.put("nequiv",'\u2262'); // NOT IDENTICAL TO
        SGML_MAP.put("nexist",'\u2204'); // THERE DOES NOT EXIST
        // SGML_MAP.put("ngE",'?'); // not greater-than, double equals
        SGML_MAP.put("nge",'\u2271'); // NEITHER GREATER-THAN NOR EQUAL TO
        SGML_MAP.put("nges",'\u2271'); // NEITHER GREATER-THAN NOR EQUAL TO
        SGML_MAP.put("Ngr",'\u039D'); // GREEK CAPITAL LETTER NU
        SGML_MAP.put("ngr",'\u03BD'); // GREEK SMALL LETTER NU
        SGML_MAP.put("ngt",'\u226F'); // NOT GREATER-THAN
        SGML_MAP.put("nharr",'\u21AE'); // LEFT RIGHT ARROW WITH STROKE
        SGML_MAP.put("nhArr",'\u21CE'); // LEFT RIGHT DOUBLE ARROW WITH STROKE
        SGML_MAP.put("ni",'\u220B'); // CONTAINS AS MEMBER
        SGML_MAP.put("NJcy",'\u040A'); // CYRILLIC CAPITAL LETTER NJE
        SGML_MAP.put("njcy",'\u045A'); // CYRILLIC SMALL LETTER NJE
        SGML_MAP.put("nlarr",'\u219A'); // LEFTWARDS ARROW WITH STROKE
        SGML_MAP.put("nlArr",'\u21CD'); // LEFTWARDS DOUBLE ARROW WITH STROKE
        SGML_MAP.put("nldr",'\u2025'); // TWO DOT LEADER
        // SGML_MAP.put("nlE",'?'); // not less-than, double equals
        SGML_MAP.put("nle",'\u2270'); // NEITHER LESS-THAN NOR EQUAL TO
        SGML_MAP.put("nles",'\u2270'); // NEITHER LESS-THAN NOR EQUAL TO
        SGML_MAP.put("nlt",'\u226E'); // NOT LESS-THAN
        SGML_MAP.put("nltri",'\u22EA'); // NOT NORMAL SUBGROUP OF
        SGML_MAP.put("nltrie",'\u22EC'); // NOT NORMAL SUBGROUP OF OR EQUAL TO
        SGML_MAP.put("nmid",'\u2224'); // DOES NOT DIVIDE
        SGML_MAP.put("not",'\u00AC'); // NOT SIGN
        SGML_MAP.put("notin",'\u2209'); // NOT AN ELEMENT OF
        SGML_MAP.put("npar",'\u2226'); // NOT PARALLEL TO
        SGML_MAP.put("npr",'\u2280'); // DOES NOT PRECEDE
        SGML_MAP.put("npre",'\u22E0'); // DOES NOT PRECEDE OR EQUAL
        SGML_MAP.put("nrarr",'\u219B'); // RIGHTWARDS ARROW WITH STROKE
        SGML_MAP.put("nrArr",'\u21CF'); // RIGHTWARDS DOUBLE ARROW WITH STROKE
        SGML_MAP.put("nrtri",'\u22EB'); // DOES NOT CONTAIN AS NORMAL SUBGROUP
        SGML_MAP.put("nrtrie",'\u22ED'); // DOES NOT CONTAIN AS NORMAL SUBGROUP OR EQUAL
        SGML_MAP.put("nsc",'\u2281'); // DOES NOT SUCCEED
        SGML_MAP.put("nsce",'\u22E1'); // DOES NOT SUCCEED OR EQUAL
        SGML_MAP.put("nsim",'\u2241'); // NOT TILDE
        SGML_MAP.put("nsime",'\u2244'); // NOT ASYMPTOTICALLY EQUAL TO
        // SGML_MAP.put("nsmid",'?'); // nshortmid
        SGML_MAP.put("nspar",'\u2226'); // NOT PARALLEL TO
        SGML_MAP.put("nsub",'\u2284'); // NOT A SUBSET OF
        SGML_MAP.put("nsubE",'\u2288'); // NEITHER A SUBSET OF NOR EQUAL TO
        SGML_MAP.put("nsube",'\u2288'); // NEITHER A SUBSET OF NOR EQUAL TO
        SGML_MAP.put("nsup",'\u2285'); // NOT A SUPERSET OF
        SGML_MAP.put("nsupE",'\u2289'); // NEITHER A SUPERSET OF NOR EQUAL TO
        SGML_MAP.put("nsupe",'\u2289'); // NEITHER A SUPERSET OF NOR EQUAL TO
        SGML_MAP.put("Ntilde",'\u00D1'); // LATIN CAPITAL LETTER N WITH TILDE
        SGML_MAP.put("ntilde",'\u00F1'); // LATIN SMALL LETTER N WITH TILDE
        SGML_MAP.put("Nu",'\u039D'); // GREEK CAPITAL LETTER NU
        SGML_MAP.put("nu",'\u03BD'); // GREEK SMALL LETTER NU
        SGML_MAP.put("num",'\u0023'); // NUMBER SIGN
        SGML_MAP.put("numero",'\u2116'); // NUMERO SIGN
        SGML_MAP.put("numsp",'\u2007'); // FIGURE SPACE
        SGML_MAP.put("nvdash",'\u22AC'); // DOES NOT PROVE
        SGML_MAP.put("nvDash",'\u22AD'); // NOT TRUE
        SGML_MAP.put("nVdash",'\u22AE'); // DOES NOT FORCE
        SGML_MAP.put("nVDash",'\u22AF'); // NEGATED DOUBLE VERTICAL BAR DOUBLE RIGHT
        SGML_MAP.put("nwarr",'\u2196'); // NORTH WEST ARROW
        SGML_MAP.put("Oacgr",'\u038C'); // GREEK CAPITAL LETTER OMICRON WITH TONOS
        SGML_MAP.put("oacgr",'\u03CC'); // GREEK SMALL LETTER OMICRON WITH TONOS
        SGML_MAP.put("Oacute",'\u00D3'); // LATIN CAPITAL LETTER O WITH ACUTE
        SGML_MAP.put("oacute",'\u00F3'); // LATIN SMALL LETTER O WITH ACUTE
        SGML_MAP.put("oast",'\u229B'); // CIRCLED ASTERISK OPERATOR
        SGML_MAP.put("ocir",'\u229A'); // CIRCLED RING OPERATOR
        SGML_MAP.put("Ocirc",'\u00D4'); // LATIN CAPITAL LETTER O WITH CIRCUMFLEX
        SGML_MAP.put("ocirc",'\u00F4'); // LATIN SMALL LETTER O WITH CIRCUMFLEX
        SGML_MAP.put("Ocy",'\u041E'); // CYRILLIC CAPITAL LETTER O
        SGML_MAP.put("ocy",'\u043E'); // CYRILLIC SMALL LETTER O
        SGML_MAP.put("odash",'\u229D'); // CIRCLED DASH
        SGML_MAP.put("Odblac",'\u0150'); // LATIN CAPITAL LETTER O WITH DOUBLE ACUTE
        SGML_MAP.put("odblac",'\u0151'); // LATIN SMALL LETTER O WITH DOUBLE ACUTE
        SGML_MAP.put("odot",'\u2299'); // CIRCLED DOT OPERATOR
        SGML_MAP.put("OElig",'\u0152'); // LATIN CAPITAL LIGATURE OE
        SGML_MAP.put("oelig",'\u0153'); // LATIN SMALL LIGATURE OE
        SGML_MAP.put("ogon",'\u02DB'); // OGONEK
        SGML_MAP.put("Ogr",'\u039F'); // GREEK CAPITAL LETTER OMICRON
        SGML_MAP.put("ogr",'\u03BF'); // GREEK SMALL LETTER OMICRON
        SGML_MAP.put("Ograve",'\u00D2'); // LATIN CAPITAL LETTER O WITH GRAVE
        SGML_MAP.put("ograve",'\u00F2'); // LATIN SMALL LETTER O WITH GRAVE
        SGML_MAP.put("OHacgr",'\u038F'); // GREEK CAPITAL LETTER OMEGA WITH TONOS
        SGML_MAP.put("ohacgr",'\u03CE'); // GREEK SMALL LETTER OMEGA WITH TONOS
        SGML_MAP.put("OHgr",'\u03A9'); // GREEK CAPITAL LETTER OMEGA
        SGML_MAP.put("ohgr",'\u03C9'); // GREEK SMALL LETTER OMEGA
        SGML_MAP.put("ohm",'\u2126'); // OHM SIGN
        SGML_MAP.put("olarr",'\u21BA'); // ANTICLOCKWISE OPEN CIRCLE ARROW
        SGML_MAP.put("oline",'\u203E'); // OVERLINE
        SGML_MAP.put("Omacr",'\u014C'); // LATIN CAPITAL LETTER O WITH MACRON
        SGML_MAP.put("omacr",'\u014D'); // LATIN SMALL LETTER O WITH MACRON
        SGML_MAP.put("Omega",'\u03A9'); // GREEK CAPITAL LETTER OMEGA
        SGML_MAP.put("omega",'\u03C9'); // GREEK SMALL LETTER OMEGA
        SGML_MAP.put("Omicron",'\u039F'); // GREEK CAPITAL LETTER OMICRON
        SGML_MAP.put("omicron",'\u03BF'); // GREEK SMALL LETTER OMICRON
        SGML_MAP.put("ominus",'\u2296'); // CIRCLED MINUS
        SGML_MAP.put("oplus",'\u2295'); // CIRCLED PLUS
        SGML_MAP.put("or",'\u2228'); // LOGICAL OR
        SGML_MAP.put("orarr",'\u21BB'); // CLOCKWISE OPEN CIRCLE ARROW
        SGML_MAP.put("order",'\u2134'); // SCRIPT SMALL O
        SGML_MAP.put("ordf",'\u00AA'); // FEMININE ORDINAL INDICATOR
        SGML_MAP.put("ordm",'\u00BA'); // MASCULINE ORDINAL INDICATOR
        SGML_MAP.put("oS",'\u24C8'); // CIRCLED LATIN CAPITAL LETTER S
        SGML_MAP.put("Oslash",'\u00D8'); // LATIN CAPITAL LETTER O WITH STROKE
        SGML_MAP.put("oslash",'\u00F8'); // LATIN SMALL LETTER O WITH STROKE
        SGML_MAP.put("osol",'\u2298'); // CIRCLED DIVISION SLASH
        SGML_MAP.put("Otilde",'\u00D5'); // LATIN CAPITAL LETTER O WITH TILDE
        SGML_MAP.put("otilde",'\u00F5'); // LATIN SMALL LETTER O WITH TILDE
        SGML_MAP.put("otimes",'\u2297'); // CIRCLED TIMES
        SGML_MAP.put("Ouml",'\u00D6'); // LATIN CAPITAL LETTER O WITH DIAERESIS
        SGML_MAP.put("ouml",'\u00F6'); // LATIN SMALL LETTER O WITH DIAERESIS
        SGML_MAP.put("par",'\u2225'); // PARALLEL TO
        SGML_MAP.put("para",'\u00B6'); // PILCROW SIGN
        SGML_MAP.put("part",'\u2202'); // PARTIAL DIFFERENTIAL
        SGML_MAP.put("Pcy",'\u041F'); // CYRILLIC CAPITAL LETTER PE
        SGML_MAP.put("pcy",'\u043F'); // CYRILLIC SMALL LETTER PE
        SGML_MAP.put("percnt",'\u0025'); // PERCENT SIGN
        SGML_MAP.put("period",'\u002E'); // FULL STOP
        SGML_MAP.put("permil",'\u2030'); // PER MILLE SIGN
        SGML_MAP.put("perp",'\u22A5'); // UP TACK
        SGML_MAP.put("Pgr",'\u03A0'); // GREEK CAPITAL LETTER PI
        SGML_MAP.put("pgr",'\u03C0'); // GREEK SMALL LETTER PI
        SGML_MAP.put("PHgr",'\u03A6'); // GREEK CAPITAL LETTER PHI
        SGML_MAP.put("phgr",'\u03C6'); // GREEK SMALL LETTER PHI
        SGML_MAP.put("phi",'\u03C6'); // GREEK SMALL LETTER PHI
        SGML_MAP.put("Phi",'\u03A6'); // GREEK CAPITAL LETTER PHI
        SGML_MAP.put("phis",'\u03C6'); // GREEK SMALL LETTER PHI
        SGML_MAP.put("phiv",'\u03D5'); // GREEK PHI SYMBOL
        SGML_MAP.put("phmmat",'\u2133'); // SCRIPT CAPITAL M
        SGML_MAP.put("phone",'\u260E'); // BLACK TELEPHONE
        SGML_MAP.put("Pi",'\u03A0'); // GREEK CAPITAL LETTER PI
        SGML_MAP.put("pi",'\u03C0'); // GREEK SMALL LETTER PI
        SGML_MAP.put("piv",'\u03D6'); // GREEK PI SYMBOL
        SGML_MAP.put("planck",'\u210F'); // PLANCK CONSTANT OVER TWO PI
        SGML_MAP.put("plus",'\u002B'); // PLUS SIGN
        SGML_MAP.put("plusb",'\u229E'); // SQUARED PLUS
        SGML_MAP.put("plusdo",'\u2214'); // DOT PLUS
        SGML_MAP.put("plusmn",'\u00B1'); // PLUS-MINUS SIGN
        SGML_MAP.put("pound",'\u00A3'); // POUND SIGN
        SGML_MAP.put("pr",'\u227A'); // PRECEDES
        // SGML_MAP.put("prap",'?'); // precedes, approximately equal to
        SGML_MAP.put("pre",'\u227C'); // PRECEDES OR EQUAL TO
        SGML_MAP.put("prime",'\u2032'); // PRIME
        SGML_MAP.put("Prime",'\u2033'); // DOUBLE PRIME
        // SGML_MAP.put("prnap",'?'); // precedes, not approximately equal to
        // SGML_MAP.put("prnE",'?'); // precedes, not double equal
        SGML_MAP.put("prnsim",'\u22E8'); // PRECEDES BUT NOT EQUIVALENT TO
        SGML_MAP.put("prod",'\u220F'); // N-ARY PRODUCT
        SGML_MAP.put("prop",'\u221D'); // PROPORTIONAL TO
        SGML_MAP.put("prsim",'\u227E'); // PRECEDES OR EQUIVALENT TO
        SGML_MAP.put("PSgr",'\u03A8'); // GREEK CAPITAL LETTER PSI
        SGML_MAP.put("psgr",'\u03C8'); // GREEK SMALL LETTER PSI
        SGML_MAP.put("Psi",'\u03A8'); // GREEK CAPITAL LETTER PSI
        SGML_MAP.put("psi",'\u03C8'); // GREEK SMALL LETTER PSI
        SGML_MAP.put("puncsp",'\u2008'); // PUNCTUATION SPACE
        SGML_MAP.put("quest",'\u003F'); // QUESTION MARK
        SGML_MAP.put("quot",'\u0022'); // QUOTATION MARK
        SGML_MAP.put("rAarr",'\u21DB'); // RIGHTWARDS TRIPLE ARROW
        SGML_MAP.put("Racute",'\u0154'); // LATIN CAPITAL LETTER R WITH ACUTE
        SGML_MAP.put("racute",'\u0155'); // LATIN SMALL LETTER R WITH ACUTE
        SGML_MAP.put("radic",'\u221A'); // SQUARE ROOT
        SGML_MAP.put("rang",'\u232A'); // RIGHT-POINTING ANGLE BRACKET
        SGML_MAP.put("raquo",'\u00BB'); // RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
        SGML_MAP.put("Rarr",'\u21A0'); // RIGHTWARDS TWO HEADED ARROW
        SGML_MAP.put("rarr",'\u2192'); // RIGHTWARDS ARROW
        SGML_MAP.put("rArr",'\u21D2'); // RIGHTWARDS DOUBLE ARROW
        SGML_MAP.put("rarr2",'\u21C9'); // RIGHTWARDS PAIRED ARROWS
        SGML_MAP.put("rarrhk",'\u21AA'); // RIGHTWARDS ARROW WITH HOOK
        SGML_MAP.put("rarrlp",'\u21AC'); // RIGHTWARDS ARROW WITH LOOP
        SGML_MAP.put("rarrtl",'\u21A3'); // RIGHTWARDS ARROW WITH TAIL
        SGML_MAP.put("rarrw",'\u219D'); // RIGHTWARDS WAVE ARROW
        SGML_MAP.put("Rcaron",'\u0158'); // LATIN CAPITAL LETTER R WITH CARON
        SGML_MAP.put("rcaron",'\u0159'); // LATIN SMALL LETTER R WITH CARON
        SGML_MAP.put("Rcedil",'\u0156'); // LATIN CAPITAL LETTER R WITH CEDILLA
        SGML_MAP.put("rcedil",'\u0157'); // LATIN SMALL LETTER R WITH CEDILLA
        SGML_MAP.put("rceil",'\u2309'); // RIGHT CEILING
        SGML_MAP.put("rcub",'\u007D'); // RIGHT CURLY BRACKET
        SGML_MAP.put("Rcy",'\u0420'); // CYRILLIC CAPITAL LETTER ER
        SGML_MAP.put("rcy",'\u0440'); // CYRILLIC SMALL LETTER ER
        SGML_MAP.put("rdquo",'\u201D'); // RIGHT DOUBLE QUOTATION MARK
        SGML_MAP.put("rdquor",'\u201C'); // LEFT DOUBLE QUOTATION MARK
        SGML_MAP.put("real",'\u211C'); // BLACK-LETTER CAPITAL R
        SGML_MAP.put("rect",'\u25AD'); // WHITE RECTANGLE
        SGML_MAP.put("reg",'\u00AE'); // REGISTERED SIGN
        SGML_MAP.put("rfloor",'\u230B'); // RIGHT FLOOR
        SGML_MAP.put("Rgr",'\u03A1'); // GREEK CAPITAL LETTER RHO
        SGML_MAP.put("rgr",'\u03C1'); // GREEK SMALL LETTER RHO
        SGML_MAP.put("rhard",'\u21C1'); // RIGHTWARDS HARPOON WITH BARB DOWNWARDS
        SGML_MAP.put("rharu",'\u21C0'); // RIGHTWARDS HARPOON WITH BARB UPWARDS
        SGML_MAP.put("Rho",'\u03A1'); // GREEK CAPITAL LETTER RHO
        SGML_MAP.put("rho",'\u03C1'); // GREEK SMALL LETTER RHO
        SGML_MAP.put("rhov",'\u03F1'); // GREEK RHO SYMBOL
        SGML_MAP.put("ring",'\u02DA'); // RING ABOVE
        SGML_MAP.put("rlarr2",'\u21C4'); // RIGHTWARDS ARROW OVER LEFTWARDS ARROW
        SGML_MAP.put("rlhar2",'\u21CC'); // RIGHTWARDS HARPOON OVER LEFTWARDS HARPOON
        SGML_MAP.put("rlm",'\u200F'); // RIGHT-TO-LEFT MARK
        SGML_MAP.put("rpar",'\u0029'); // RIGHT PARENTHESIS
        // SGML_MAP.put("rpargt",'?'); // right parenthesis, greater-than
        SGML_MAP.put("rsaquo",'\u203A'); // SINGLE RIGHT-POINTING ANGLE QUOTATION MARK
        SGML_MAP.put("rsh",'\u21B1'); // UPWARDS ARROW WITH TIP RIGHTWARDS
        SGML_MAP.put("rsqb",'\u005D'); // RIGHT SQUARE BRACKET
        SGML_MAP.put("rsquo",'\u2019'); // RIGHT SINGLE QUOTATION MARK
        SGML_MAP.put("rsquor",'\u2018'); // LEFT SINGLE QUOTATION MARK
        SGML_MAP.put("rthree",'\u22CC'); // RIGHT SEMIDIRECT PRODUCT
        SGML_MAP.put("rtimes",'\u22CA'); // RIGHT NORMAL FACTOR SEMIDIRECT PRODUCT
        SGML_MAP.put("rtri",'\u25B9'); // WHITE RIGHT-POINTING SMALL TRIANGLE
        SGML_MAP.put("rtrie",'\u22B5'); // CONTAINS AS NORMAL SUBGROUP OR EQUAL TO
        SGML_MAP.put("rtrif",'\u25B8'); // BLACK RIGHT-POINTING SMALL TRIANGLE
        SGML_MAP.put("rx",'\u211E'); // PRESCRIPTION TAKE
        SGML_MAP.put("Sacute",'\u015A'); // LATIN CAPITAL LETTER S WITH ACUTE
        SGML_MAP.put("sacute",'\u015B'); // LATIN SMALL LETTER S WITH ACUTE
        SGML_MAP.put("samalg",'\u2210'); // N-ARY COPRODUCT
        SGML_MAP.put("sbquo",'\u201A'); // SINGLE LOW-9 QUOTATION MARK
        // SGML_MAP.put("sbsol","005C"); // REVERSE SOLIDUS
        SGML_MAP.put("sc",'\u227B'); // SUCCEEDS
        // SGML_MAP.put("scap",'?'); // succeeds, approximately equal to
        SGML_MAP.put("Scaron",'\u0160'); // LATIN CAPITAL LETTER S WITH CARON
        SGML_MAP.put("scaron",'\u0161'); // LATIN SMALL LETTER S WITH CARON
        SGML_MAP.put("sccue",'\u227D'); // SUCCEEDS OR EQUAL TO
        SGML_MAP.put("sce",'\u227D'); // SUCCEEDS OR EQUAL TO
        SGML_MAP.put("Scedil",'\u015E'); // LATIN CAPITAL LETTER S WITH CEDILLA
        SGML_MAP.put("scedil",'\u015F'); // LATIN SMALL LETTER S WITH CEDILLA
        SGML_MAP.put("Scirc",'\u015C'); // LATIN CAPITAL LETTER S WITH CIRCUMFLEX
        SGML_MAP.put("scirc",'\u015D'); // LATIN SMALL LETTER S WITH CIRCUMFLEX
        // SGML_MAP.put("scnap",'?'); // succeeds, not approximately equal to
        // SGML_MAP.put("scnE",'?'); // succeeds, not double equals
        SGML_MAP.put("scnsim",'\u22E9'); // SUCCEEDS BUT NOT EQUIVALENT TO
        SGML_MAP.put("scsim",'\u227F'); // SUCCEEDS OR EQUIVALENT TO
        SGML_MAP.put("Scy",'\u0421'); // CYRILLIC CAPITAL LETTER ES
        SGML_MAP.put("scy",'\u0441'); // CYRILLIC SMALL LETTER ES
        SGML_MAP.put("sdot",'\u22C5'); // DOT OPERATOR
        SGML_MAP.put("sdotb",'\u22A1'); // SQUARED DOT OPERATOR
        SGML_MAP.put("sect",'\u00A7'); // SECTION SIGN
        SGML_MAP.put("semi",'\u003B'); // SEMICOLON
        SGML_MAP.put("setmn",'\u2216'); // SET MINUS
        SGML_MAP.put("sext",'\u2736'); // SIX POINTED BLACK STAR
        SGML_MAP.put("sfgr",'\u03C2'); // GREEK SMALL LETTER FINAL SIGMA
        SGML_MAP.put("sfrown",'\u2322'); // FROWN
        SGML_MAP.put("Sgr",'\u03A3'); // GREEK CAPITAL LETTER SIGMA
        SGML_MAP.put("sgr",'\u03C3'); // GREEK SMALL LETTER SIGMA
        SGML_MAP.put("sharp",'\u266F'); // MUSIC SHARP SIGN
        SGML_MAP.put("SHCHcy",'\u0429'); // CYRILLIC CAPITAL LETTER SHCHA
        SGML_MAP.put("shchcy",'\u0449'); // CYRILLIC SMALL LETTER SHCHA
        SGML_MAP.put("SHcy",'\u0428'); // CYRILLIC CAPITAL LETTER SHA
        SGML_MAP.put("shcy",'\u0448'); // CYRILLIC SMALL LETTER SHA
        SGML_MAP.put("shy",'\u00AD'); // SOFT HYPHEN
        SGML_MAP.put("Sigma",'\u03A3'); // GREEK CAPITAL LETTER SIGMA
        SGML_MAP.put("sigma",'\u03C3'); // GREEK SMALL LETTER SIGMA
        SGML_MAP.put("sigmaf",'\u03C2'); // GREEK SMALL LETTER FINAL SIGMA
        SGML_MAP.put("sigmav",'\u03C2'); // GREEK SMALL LETTER FINAL SIGMA
        SGML_MAP.put("sim",'\u223C'); // TILDE OPERATOR
        SGML_MAP.put("sime",'\u2243'); // ASYMPTOTICALLY EQUAL TO
        // SGML_MAP.put("smid",'?'); // shortmid
        SGML_MAP.put("smile",'\u2323'); // SMILE
        SGML_MAP.put("SOFTcy",'\u042C'); // CYRILLIC CAPITAL LETTER SOFT SIGN
        SGML_MAP.put("softcy",'\u044C'); // CYRILLIC SMALL LETTER SOFT SIGN
        SGML_MAP.put("sol",'\u002F'); // SOLIDUS
        SGML_MAP.put("spades",'\u2660'); // BLACK SPADE SUIT
        SGML_MAP.put("spar",'\u2225'); // PARALLEL TO
        SGML_MAP.put("sqcap",'\u2293'); // SQUARE CAP
        SGML_MAP.put("sqcup",'\u2294'); // SQUARE CUP
        SGML_MAP.put("sqsub",'\u228F'); // SQUARE IMAGE OF
        SGML_MAP.put("sqsube",'\u2291'); // SQUARE IMAGE OF OR EQUAL TO
        SGML_MAP.put("sqsup",'\u2290'); // SQUARE ORIGINAL OF
        SGML_MAP.put("sqsupe",'\u2292'); // SQUARE ORIGINAL OF OR EQUAL TO
        SGML_MAP.put("squ",'\u25A1'); // WHITE SQUARE
        SGML_MAP.put("square",'\u25A1'); // WHITE SQUARE
        SGML_MAP.put("squf",'\u25AA'); // BLACK SMALL SQUARE
        SGML_MAP.put("ssetmn",'\u2216'); // SET MINUS
        SGML_MAP.put("ssmile",'\u2323'); // SMILE
        SGML_MAP.put("sstarf",'\u22C6'); // STAR OPERATOR
        SGML_MAP.put("star",'\u2606'); // WHITE STAR
        SGML_MAP.put("starf",'\u2605'); // BLACK STAR
        SGML_MAP.put("Sub",'\u22D0'); // DOUBLE SUBSET
        SGML_MAP.put("sub",'\u2282'); // SUBSET OF
        SGML_MAP.put("subE",'\u2286'); // SUBSET OF OR EQUAL TO
        SGML_MAP.put("sube",'\u2286'); // SUBSET OF OR EQUAL TO
        SGML_MAP.put("subnE",'\u228A'); // SUBSET OF WITH NOT EQUAL TO
        SGML_MAP.put("subne",'\u228A'); // SUBSET OF WITH NOT EQUAL TO
        SGML_MAP.put("sum",'\u2211'); // N-ARY SUMMATION
        SGML_MAP.put("sung",'\u266A'); // EIGHTH NOTE
        SGML_MAP.put("Sup",'\u22D1'); // DOUBLE SUPERSET
        SGML_MAP.put("sup",'\u2283'); // SUPERSET OF
        SGML_MAP.put("sup1",'\u00B9'); // SUPERSCRIPT ONE
        SGML_MAP.put("sup2",'\u00B2'); // SUPERSCRIPT TWO
        SGML_MAP.put("sup3",'\u00B3'); // SUPERSCRIPT THREE
        SGML_MAP.put("supE",'\u2287'); // SUPERSET OF OR EQUAL TO
        SGML_MAP.put("supe",'\u2287'); // SUPERSET OF OR EQUAL TO
        SGML_MAP.put("supnE",'\u228B'); // SUPERSET OF WITH NOT EQUAL TO
        SGML_MAP.put("supne",'\u228B'); // SUPERSET OF WITH NOT EQUAL TO
        SGML_MAP.put("szlig",'\u00DF'); // LATIN SMALL LETTER SHARP S
        SGML_MAP.put("target",'\u2316'); // POSITION INDICATOR
        SGML_MAP.put("Tau",'\u03A4'); // GREEK CAPITAL LETTER TAU
        SGML_MAP.put("tau",'\u03C4'); // GREEK SMALL LETTER TAU
        SGML_MAP.put("Tcaron",'\u0164'); // LATIN CAPITAL LETTER T WITH CARON
        SGML_MAP.put("tcaron",'\u0165'); // LATIN SMALL LETTER T WITH CARON
        SGML_MAP.put("Tcedil",'\u0162'); // LATIN CAPITAL LETTER T WITH CEDILLA
        SGML_MAP.put("tcedil",'\u0163'); // LATIN SMALL LETTER T WITH CEDILLA
        SGML_MAP.put("Tcy",'\u0422'); // CYRILLIC CAPITAL LETTER TE
        SGML_MAP.put("tcy",'\u0442'); // CYRILLIC SMALL LETTER TE
        SGML_MAP.put("tdot",'\u20DB'); // COMBINING THREE DOTS ABOVE
        SGML_MAP.put("telrec",'\u2315'); // TELEPHONE RECORDER
        SGML_MAP.put("Tgr",'\u03A4'); // GREEK CAPITAL LETTER TAU
        SGML_MAP.put("tgr",'\u03C4'); // GREEK SMALL LETTER TAU
        SGML_MAP.put("there4",'\u2234'); // THEREFORE
        SGML_MAP.put("theta",'\u03B8'); // GREEK SMALL LETTER THETA
        SGML_MAP.put("Theta",'\u0398'); // GREEK CAPITAL LETTER THETA
        SGML_MAP.put("thetas",'\u03B8'); // GREEK SMALL LETTER THETA
        SGML_MAP.put("thetasym",'\u03D1'); // GREEK THETA SYMBOL
        SGML_MAP.put("thetav",'\u03D1'); // GREEK THETA SYMBOL
        SGML_MAP.put("THgr",'\u0398'); // GREEK CAPITAL LETTER THETA
        SGML_MAP.put("thgr",'\u03B8'); // GREEK SMALL LETTER THETA
        SGML_MAP.put("thinsp",'\u2009'); // THIN SPACE
        SGML_MAP.put("thkap",'\u2248'); // ALMOST EQUAL TO
        SGML_MAP.put("thksim",'\u223C'); // TILDE OPERATOR
        SGML_MAP.put("THORN",'\u00DE'); // LATIN CAPITAL LETTER THORN
        SGML_MAP.put("thorn",'\u00FE'); // LATIN SMALL LETTER THORN
        SGML_MAP.put("tilde",'\u02DC'); // SMALL TILDE
        SGML_MAP.put("times",'\u00D7'); // MULTIPLICATION SIGN
        SGML_MAP.put("timesb",'\u22A0'); // SQUARED TIMES
        SGML_MAP.put("top",'\u22A4'); // DOWN TACK
        SGML_MAP.put("tprime",'\u2034'); // TRIPLE PRIME
        SGML_MAP.put("trade",'\u2122'); // TRADE MARK SIGN
        SGML_MAP.put("trie",'\u225C'); // DELTA EQUAL TO
        SGML_MAP.put("TScy",'\u0426'); // CYRILLIC CAPITAL LETTER TSE
        SGML_MAP.put("tscy",'\u0446'); // CYRILLIC SMALL LETTER TSE
        SGML_MAP.put("TSHcy",'\u040B'); // CYRILLIC CAPITAL LETTER TSHE
        SGML_MAP.put("tshcy",'\u045B'); // CYRILLIC SMALL LETTER TSHE
        SGML_MAP.put("Tstrok",'\u0166'); // LATIN CAPITAL LETTER T WITH STROKE
        SGML_MAP.put("tstrok",'\u0167'); // LATIN SMALL LETTER T WITH STROKE
        SGML_MAP.put("twixt",'\u226C'); // BETWEEN
        SGML_MAP.put("Uacgr",'\u038E'); // GREEK CAPITAL LETTER UPSILON WITH TONOS
        SGML_MAP.put("uacgr",'\u03CD'); // GREEK SMALL LETTER UPSILON WITH TONOS
        SGML_MAP.put("Uacute",'\u00DA'); // LATIN CAPITAL LETTER U WITH ACUTE
        SGML_MAP.put("uacute",'\u00FA'); // LATIN SMALL LETTER U WITH ACUTE
        SGML_MAP.put("uArr",'\u21D1'); // UPWARDS DOUBLE ARROW
        SGML_MAP.put("uarr",'\u2191'); // UPWARDS ARROW
        SGML_MAP.put("uarr2",'\u21C8'); // UPWARDS PAIRED ARROWS
        SGML_MAP.put("Ubrcy",'\u040E'); // CYRILLIC CAPITAL LETTER SHORT U
        SGML_MAP.put("ubrcy",'\u045E'); // CYRILLIC SMALL LETTER SHORT U
        SGML_MAP.put("Ubreve",'\u016C'); // LATIN CAPITAL LETTER U WITH BREVE
        SGML_MAP.put("ubreve",'\u016D'); // LATIN SMALL LETTER U WITH BREVE
        SGML_MAP.put("Ucirc",'\u00DB'); // LATIN CAPITAL LETTER U WITH CIRCUMFLEX
        SGML_MAP.put("ucirc",'\u00FB'); // LATIN SMALL LETTER U WITH CIRCUMFLEX
        SGML_MAP.put("Ucy",'\u0423'); // CYRILLIC CAPITAL LETTER U
        SGML_MAP.put("ucy",'\u0443'); // CYRILLIC SMALL LETTER U
        SGML_MAP.put("Udblac",'\u0170'); // LATIN CAPITAL LETTER U WITH DOUBLE ACUTE
        SGML_MAP.put("udblac",'\u0171'); // LATIN SMALL LETTER U WITH DOUBLE ACUTE
        SGML_MAP.put("udiagr",'\u03B0'); // GREEK SMALL LETTER UPSILON WITH DIALYTIKA AND
        SGML_MAP.put("Udigr",'\u03AB'); // GREEK CAPITAL LETTER UPSILON WITH DIALYTIKA
        SGML_MAP.put("udigr",'\u03CB'); // GREEK SMALL LETTER UPSILON WITH DIALYTIKA
        SGML_MAP.put("Ugr",'\u03A5'); // GREEK CAPITAL LETTER UPSILON
        SGML_MAP.put("ugr",'\u03C5'); // GREEK SMALL LETTER UPSILON
        SGML_MAP.put("Ugrave",'\u00D9'); // LATIN CAPITAL LETTER U WITH GRAVE
        SGML_MAP.put("ugrave",'\u00F9'); // LATIN SMALL LETTER U WITH GRAVE
        SGML_MAP.put("uharl",'\u21BF'); // UPWARDS HARPOON WITH BARB LEFTWARDS
        SGML_MAP.put("uharr",'\u21BE'); // UPWARDS HARPOON WITH BARB RIGHTWARDS
        SGML_MAP.put("uhblk",'\u2580'); // UPPER HALF BLOCK
        SGML_MAP.put("ulcorn",'\u231C'); // TOP LEFT CORNER
        SGML_MAP.put("ulcrop",'\u230F'); // TOP LEFT CROP
        SGML_MAP.put("Umacr",'\u016A'); // LATIN CAPITAL LETTER U WITH MACRON
        SGML_MAP.put("umacr",'\u016B'); // LATIN SMALL LETTER U WITH MACRON
        SGML_MAP.put("uml",'\u00A8'); // DIAERESIS
        SGML_MAP.put("Uogon",'\u0172'); // LATIN CAPITAL LETTER U WITH OGONEK
        SGML_MAP.put("uogon",'\u0173'); // LATIN SMALL LETTER U WITH OGONEK
        SGML_MAP.put("uplus",'\u228E'); // MULTISET UNION
        SGML_MAP.put("Upsi",'\u03A5'); // GREEK CAPITAL LETTER UPSILON
        SGML_MAP.put("upsi",'\u03C5'); // GREEK SMALL LETTER UPSILON
        SGML_MAP.put("upsih",'\u03D2'); // GREEK UPSILON WITH HOOK SYMBOL
        SGML_MAP.put("Upsilon",'\u03A5'); // GREEK CAPITAL LETTER UPSILON
        SGML_MAP.put("upsilon",'\u03C5'); // GREEK SMALL LETTER UPSILON
        SGML_MAP.put("urcorn",'\u231D'); // TOP RIGHT CORNER
        SGML_MAP.put("urcrop",'\u230E'); // TOP RIGHT CROP
        SGML_MAP.put("Uring",'\u016E'); // LATIN CAPITAL LETTER U WITH RING ABOVE
        SGML_MAP.put("uring",'\u016F'); // LATIN SMALL LETTER U WITH RING ABOVE
        SGML_MAP.put("Utilde",'\u0168'); // LATIN CAPITAL LETTER U WITH TILDE
        SGML_MAP.put("utilde",'\u0169'); // LATIN SMALL LETTER U WITH TILDE
        SGML_MAP.put("utri",'\u25B5'); // WHITE UP-POINTING SMALL TRIANGLE
        SGML_MAP.put("utrif",'\u25B4'); // BLACK UP-POINTING SMALL TRIANGLE
        SGML_MAP.put("Uuml",'\u00DC'); // LATIN CAPITAL LETTER U WITH DIAERESIS
        SGML_MAP.put("uuml",'\u00FC'); // LATIN SMALL LETTER U WITH DIAERESIS
        SGML_MAP.put("varr",'\u2195'); // UP DOWN ARROW
        SGML_MAP.put("vArr",'\u21D5'); // UP DOWN DOUBLE ARROW
        SGML_MAP.put("Vcy",'\u0412'); // CYRILLIC CAPITAL LETTER VE
        SGML_MAP.put("vcy",'\u0432'); // CYRILLIC SMALL LETTER VE
        SGML_MAP.put("vdash",'\u22A2'); // RIGHT TACK
        SGML_MAP.put("vDash",'\u22A8'); // TRUE
        SGML_MAP.put("Vdash",'\u22A9'); // FORCES
        SGML_MAP.put("veebar",'\u22BB'); // XOR
        SGML_MAP.put("vellip",'\u22EE'); // VERTICAL ELLIPSIS
        SGML_MAP.put("verbar",'\u007C'); // VERTICAL LINE
        SGML_MAP.put("Verbar",'\u2016'); // DOUBLE VERTICAL LINE
        SGML_MAP.put("vltri",'\u22B2'); // NORMAL SUBGROUP OF
        SGML_MAP.put("vprime",'\u2032'); // PRIME
        SGML_MAP.put("vprop",'\u221D'); // PROPORTIONAL TO
        SGML_MAP.put("vrtri",'\u22B3'); // CONTAINS AS NORMAL SUBGROUP
        SGML_MAP.put("vsubnE",'\u228A'); // SUBSET OF WITH NOT EQUAL TO
        SGML_MAP.put("vsubne",'\u228A'); // SUBSET OF WITH NOT EQUAL TO
        SGML_MAP.put("vsupne",'\u228B'); // SUPERSET OF WITH NOT EQUAL TO
        SGML_MAP.put("vsupnE",'\u228B'); // SUPERSET OF WITH NOT EQUAL TO
        SGML_MAP.put("Vvdash",'\u22AA'); // TRIPLE VERTICAL BAR RIGHT TURNSTILE
        SGML_MAP.put("Wcirc",'\u0174'); // LATIN CAPITAL LETTER W WITH CIRCUMFLEX
        SGML_MAP.put("wcirc",'\u0175'); // LATIN SMALL LETTER W WITH CIRCUMFLEX
        SGML_MAP.put("wedgeq",'\u2259'); // ESTIMATES
        SGML_MAP.put("weierp",'\u2118'); // SCRIPT CAPITAL P
        SGML_MAP.put("wreath",'\u2240'); // WREATH PRODUCT
        SGML_MAP.put("xcirc",'\u25CB'); // WHITE CIRCLE
        SGML_MAP.put("xdtri",'\u25BD'); // WHITE DOWN-POINTING TRIANGLE
        SGML_MAP.put("Xgr",'\u039E'); // GREEK CAPITAL LETTER XI
        SGML_MAP.put("xgr",'\u03BE'); // GREEK SMALL LETTER XI
        SGML_MAP.put("xhArr",'\u2194'); // LEFT RIGHT ARROW
        SGML_MAP.put("xharr",'\u2194'); // LEFT RIGHT ARROW
        SGML_MAP.put("Xi",'\u039E'); // GREEK CAPITAL LETTER XI
        SGML_MAP.put("xi",'\u03BE'); // GREEK SMALL LETTER XI
        SGML_MAP.put("xlArr",'\u21D0'); // LEFTWARDS DOUBLE ARROW
        SGML_MAP.put("xrArr",'\u21D2'); // RIGHTWARDS DOUBLE ARROW
        SGML_MAP.put("xutri",'\u25B3'); // WHITE UP-POINTING TRIANGLE
        SGML_MAP.put("Yacute",'\u00DD'); // LATIN CAPITAL LETTER Y WITH ACUTE
        SGML_MAP.put("yacute",'\u00FD'); // LATIN SMALL LETTER Y WITH ACUTE
        SGML_MAP.put("YAcy",'\u042F'); // CYRILLIC CAPITAL LETTER YA
        SGML_MAP.put("yacy",'\u044F'); // CYRILLIC SMALL LETTER YA
        SGML_MAP.put("Ycirc",'\u0176'); // LATIN CAPITAL LETTER Y WITH CIRCUMFLEX
        SGML_MAP.put("ycirc",'\u0177'); // LATIN SMALL LETTER Y WITH CIRCUMFLEX
        SGML_MAP.put("Ycy",'\u042B'); // CYRILLIC CAPITAL LETTER YERU
        SGML_MAP.put("ycy",'\u044B'); // CYRILLIC SMALL LETTER YERU
        SGML_MAP.put("yen",'\u00A5'); // YEN SIGN
        SGML_MAP.put("YIcy",'\u0407'); // CYRILLIC CAPITAL LETTER YI
        SGML_MAP.put("yicy",'\u0457'); // CYRILLIC SMALL LETTER YI
        SGML_MAP.put("YUcy",'\u042E'); // CYRILLIC CAPITAL LETTER YU
        SGML_MAP.put("yucy",'\u044E'); // CYRILLIC SMALL LETTER YU
        SGML_MAP.put("yuml",'\u00FF'); // LATIN SMALL LETTER Y WITH DIAERESIS
        SGML_MAP.put("Yuml",'\u0178'); // LATIN CAPITAL LETTER Y WITH DIAERESIS
        SGML_MAP.put("Zacute",'\u0179'); // LATIN CAPITAL LETTER Z WITH ACUTE
        SGML_MAP.put("zacute",'\u017A'); // LATIN SMALL LETTER Z WITH ACUTE
        SGML_MAP.put("Zcaron",'\u017D'); // LATIN CAPITAL LETTER Z WITH CARON
        SGML_MAP.put("zcaron",'\u017E'); // LATIN SMALL LETTER Z WITH CARON
        SGML_MAP.put("Zcy",'\u0417'); // CYRILLIC CAPITAL LETTER ZE
        SGML_MAP.put("zcy",'\u0437'); // CYRILLIC SMALL LETTER ZE
        SGML_MAP.put("Zdot",'\u017B'); // LATIN CAPITAL LETTER Z WITH DOT ABOVE
        SGML_MAP.put("zdot",'\u017C'); // LATIN SMALL LETTER Z WITH DOT ABOVE
        SGML_MAP.put("Zeta",'\u0396'); // GREEK CAPITAL LETTER ZETA
        SGML_MAP.put("zeta",'\u03B6'); // GREEK SMALL LETTER ZETA
        SGML_MAP.put("Zgr",'\u0396'); // GREEK CAPITAL LETTER ZETA
        SGML_MAP.put("zgr",'\u03B6'); // GREEK SMALL LETTER ZETA
        SGML_MAP.put("ZHcy",'\u0416'); // CYRILLIC CAPITAL LETTER ZHE
        SGML_MAP.put("zhcy",'\u0436'); // CYRILLIC SMALL LETTER ZHE
        SGML_MAP.put("zwj",'\u200D'); // ZERO WIDTH JOINER
        SGML_MAP.put("zwnj",'\u200C'); // ZERO WIDTH NON-JOINER
    }
}