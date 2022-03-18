package org.apromore.portal.util;

import org.junit.Assert;
import org.junit.Test;

public class AlphaNumericComparatorUnitTest {


    @Test
    public void samePrefixStringCompare(){
        String name1="procure";
        String name2="procurePay";
        Assert.assertTrue((name1.compareTo(name2))<0);
        Assert.assertTrue((AlphaNumericComparator.compareTo(name1,name2))<0);
    }

    @Test
    public void samePrefixWithDifferentSuffixStringCompare(){
        String name1="procureMent";
        String name2="procurePay";
        Assert.assertTrue((name1.compareTo(name2))<0);
        Assert.assertTrue((AlphaNumericComparator.compareTo(name1,name2))<0);
    }

    @Test
    public void prefixNumberWithStringCompare(){
        String name1="1procure";
        String name2="2procure";
        Assert.assertTrue((name1.compareTo(name2))<0);
        Assert.assertTrue((AlphaNumericComparator.compareTo(name1,name2))<0);
    }
    @Test
    public void prefixDecimalNumberAndStringCompare(){

        String name1="1.1procure";
        String name2="2procure";
        Assert.assertTrue((name1.compareTo(name2))<0);
        Assert.assertTrue((AlphaNumericComparator.compareTo(name1,name2))<0);

        name1="1.10procure";
        name2="2procure";
        Assert.assertTrue((name1.compareTo(name2))<0);
        Assert.assertTrue((AlphaNumericComparator.compareTo(name1,name2))<0);

    }
    @Test
    public void prefixDecimalWithDifferentOrderAndStringCompare(){
        String name1="1.3procure";
        String name2="1.10procure";

        //Regular compare not works here
        Assert.assertFalse((name1.compareTo(name2))<0);

        Assert.assertTrue((AlphaNumericComparator.compareTo(name1,name2))<0);

    }


    @Test
    public void suffixDecimalWithDifferentOrderAndStringCompare(){
        String name1="procure1.3";
        String name2="procure1.10";
        //Regular compare not works here
        Assert.assertFalse((name1.compareTo(name2))<0);

        Assert.assertTrue((AlphaNumericComparator.compareTo(name1 ,name2))<0);

    }

    @Test
    public void middlePartDecimalWithDifferentOrderAndStringCompare(){
        String name1="procure1.3payment";
        String name2="procure1.10payment";
        //Regular compare not works here
        Assert.assertFalse((name1.compareTo(name2))<0);

        Assert.assertTrue((AlphaNumericComparator.compareTo(name1,name2))<0);

    }
}
