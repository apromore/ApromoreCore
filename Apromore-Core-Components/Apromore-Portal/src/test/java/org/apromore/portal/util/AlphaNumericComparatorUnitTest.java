package org.apromore.portal.util;

import org.junit.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AlphaNumericComparatorUnitTest {

    @ParameterizedTest
    @CsvSource({
        "procure, procurePay",
        "procureMent, procurePay",
        "1procure, 2procure",
        "1.1procure, 2procure",
        "1.10procure, 2procure"
    })
    void similarResultWithRegularComparison(String name1, String name2) {
        Assert.assertTrue((name1.compareTo(name2)) < 0);
        Assert.assertTrue((AlphaNumericComparator.compareTo(name1, name2)) < 0);
    }

    @ParameterizedTest
    @CsvSource({
        "1.3procure, 1.10procure",
        "procure1.3, procure1.10",
        "procure1.3payment, procure1.10payment"
    })
    void differentResultWithRegularComparison(String name1, String name2) {
        Assert.assertFalse((name1.compareTo(name2)) < 0);

        Assert.assertTrue((AlphaNumericComparator.compareTo(name1, name2)) < 0);
    }


}
