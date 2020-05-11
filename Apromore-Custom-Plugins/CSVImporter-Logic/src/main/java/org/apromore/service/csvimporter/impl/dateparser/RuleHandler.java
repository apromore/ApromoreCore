package org.apromore.service.csvimporter.impl.dateparser;

import com.github.sisyphsu.retree.ReMatcher;

/**
 * This class represents the standard specification of rule's handler.
 * It should parse the specified substring to fill some fields of DateTime.
 *
 * @author sulin
 * @since 2019-09-14 14:25:45
 */
@FunctionalInterface
interface RuleHandler {

    /**
     * Parse substring[from, to) of the specified string
     *
     * @param chars   The original string in char[]
     * @param matcher The underline ReMatcher
     * @param dt      DateTime to accept parsed properties.
     */
    void handle(CharSequence chars, ReMatcher matcher, DateBuilder dt);

}
