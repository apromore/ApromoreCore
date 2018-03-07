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

import java.io.File;

import java.util.Properties;

/**
 * A <code>CommandLineArguments</code> object represents a the
 * command-line arguments.  It is constructed with an array of
 * command-line arguments and an optional properties value specifying
 * default values.
 *
 * <p> Arguments of the form
 * <code>-<i>Property</i>=<i>Value</i></code> induce the property
 * value pair specified.  Argument values may be empty, in which
 * case the value will be the empty string.

 * <p>Arguments of the form <code>-<i>Flag</i></code> not matching the
 * previous form induce properties <code><i>Flag</i>=yes</code>.  Bare
 * arguments with of the form <code><i>BareArgument</i></code> induce
 * properties <code>BARE_ARG_<i>i</i>=<i>BareArgument</i></code> so
 * that bare arguments may be retrieved by position; bare arguments
 * equal to the empty string are ignored.</p>
 *
 * <P>Because commands are based on the {@link #run()} method, they
 * have been defined to implement the {@link Runnable} interface.
 *
 * @author  Bob Carpenter
 * @version 4.0.0
 * @since   LingPipe1.0
 */
public abstract class AbstractCommand implements Runnable {

    private final long mStartTime;

    private final Properties mProperties;

    /**
     * Counter for bare arguments.
     */
    private int mBareArgCount = 0;

    /**
     * The default properties values for this command.
     */
    private Properties mDefaultProperties = null;

    /**
     * Constructs a command-line arguments object from the specified
     * arguments list using no default values.
     *
     * @param args Command-line arguments.
     * @throws IllegalArgumentException If the arguments are not well
     * formed.
     */
    public AbstractCommand(String[] args) {
        this(args,new Properties());
    }

    /**
     * Constructs a command-line arguments object from the specified
     * arguments using the specified properties as default values.
     *
     * @param args Command-line arguments.
     * @param defaultProperties Default values for properties.
     * @throws IllegalArgumentException If the arguments are not well
     * formed.
     */
    public AbstractCommand(String[] args,
                           Properties defaultProperties) {
        mDefaultProperties = defaultProperties;
        mProperties = new Properties(defaultProperties);
        parse(args);
        mStartTime = System.currentTimeMillis();
    }

    /**
     * Adds the specified key and value as default arguments.
     *
     * @param property Name of property.
     * @param defaultValue Default value for property.
     */
    public void addDefaultProperty(String property, String defaultValue) {
        mDefaultProperties.setProperty(property,defaultValue);
    }


    /**
     * Return the elapsed time since the construction of
     * this command in milliseconds.
     *
     * @return The elapsed time since the construction of
     * this command in milliseconds.
     */
    public long elapsedTimeMillis() {
        return System.currentTimeMillis() - mStartTime;
    }

    /**
     * Return the time at which this command was constructed.
     *
     * @return The time at which this command was constructed.
     */
    public long startTimeMillis() {
        return mStartTime;
    }


    /**
     * Runs the abstract command.
     */
    public abstract void run();

    /**
     * Returns the number of bare arguments supplied on the command
     * line.
     *
     * @return Number of bare arguments supplied on the command line.
     */
    public int numBareArguments() {
        return mBareArgCount;
    }

    /**
     * Returns the array of bare arguments, in order, supplied on the
     * command line.  Bare arguments are arguments that do not begin
     * with a minus sign (<code>-</code>) indicating a flag or
     * property.
     *
     * @return The array of bare arguments.
     */
    public String[] bareArguments() {
    String[] arguments = new String[numBareArguments()];
    for (int i = 0; i < arguments.length; ++i)
        arguments[i] = getBareArgument(i);
    return arguments;
    }

    /**
     * Returns <code>true</code> if the arguments set the specified
     * flag.  Note that this method returns <code>false</code> for
     * properties; see {@link #hasProperty(String)}.
     *
     * @param arg The argument to test for existence on the command line.
     * @return <code>true</code> if the argument was specified on the
     * command line preceded by a <code>'-'</code>.
     */
    public boolean hasFlag(String arg) {
        String value = mProperties.getProperty(arg);
        return value != null
            && value.equals(HAS_PROPERTY_VALUE);
    }

    /**
     * Returns <code>true</code> if the arguments have the
     * specified property defined.  Note that this method
     * returns false for flags; see {@link #hasFlag(String)}.
     */
    public boolean hasProperty(String arg) {
        String value = mProperties.getProperty(arg);
        return value != null
            && !value.equals(HAS_PROPERTY_VALUE);
    }

    /**
     * Returns the specified bare argument.
     *
     * @param n Index of bare argument to return.
     * @return Value of bare argument in specified position, or
     * <code>null</code> if not specified.
     */
    public String getBareArgument(int n) {
        return mProperties.getProperty(bareArgumentProperty(n));
    }

    /**
     * Returns the value of the command-line argument with the
     * specified key, or the value of the key in the default
     * properties, or <code>null</code> if it does not exist.  The
     * property exists in the command-line if it was specified by
     * <code>"-<i>Arg</i>=<i>Value</i></code>.
     *
     * @param key Name of command-line argument to return.
     * @return Value of command-line argument.
     */
    public String getArgument(String key) {
        return mProperties.getProperty(key);
    }

    /**
     * Returns the complete list of command-line parameters for this
     * abstract command.  The returned result encapsulates all of the
     * argument parameters and default parameters with values.  
     *
     * <p>Flags are included as properties with the flag as key and
     * value {@link #HAS_PROPERTY_VALUE}.  Bare arguments are included
     * with prefixes consisting of {@link #BARE_ARG_PREFIX} followed
     * by a number.
     *
     * @return The properties representing the arguments.
     */
    public Properties getArguments() {
        return mProperties;
    }

    /**
     * Returns the same value as {@link #getArgument(String)}, but
     * throws an exception if the argument does not exist.
     *
     * @param key Name of command-line argument to return.
     * @return Value of command-line argument.
     * @throws IllegalArgumentException If there is no value specified
     * for the given key.
     */
    public String getExistingArgument(String key)
        throws IllegalArgumentException{

        String result = mProperties.getProperty(key);
        if (result == null)
            illegalPropertyArgument("Require value.",
                                    key);
        return result;
    }

    /**
     * Returns <code>true</code> if there is a command-line
     * argument specified for the key.
     *
     * @return <code>true</code> if there is a command-line argument
     * specified for the key.
     */
    public boolean hasArgument(String key) {
        return mProperties.containsKey(key);
    }

    /**
     * Returns the value of the argument with the specified key
     * converted to an integer value.
     *
     * @param key Name of argument to convert to an integer and
     * return.
     * @return Integer value of specified command-line argument.
     * @throws IllegalArgumentException If there is a number format
     * exception converting the argument to an integer, or if there is
     * no value supplied.
     */
    public int getArgumentInt(String key) {
        String valString = getExistingArgument(key);
        try {
            return Integer.valueOf(valString);
        } catch (NumberFormatException e) {
            illegalPropertyArgument("Required integer.", key);
        // required for compiler; doen't know catch always throws
        return -1;
        }
    }

    /**
     * Returns the value of the argument with the specified key
     * converted to a long value.
     *
     * @param key Name of argument to convert to a long and
     * return.
     * @return Long value of specified command-line argument.
     * @throws IllegalArgumentException If there is a number format
     * exception converting the argument to an integer, or if there is
     * no value supplied.
     */
    public long getArgumentLong(String key) {
        String valString = getExistingArgument(key);
        try {
            return Long.valueOf(valString);
        } catch (NumberFormatException e) {
            illegalPropertyArgument("Required integer.", key);
        // required for compiler; doen't know catch always throws
        return -1l;
        }
    }


    /**
     * Returns the value of the argument with the specified key
     * converted to a double value.
     *
     * @param key Name of argument to convert to a double and return.
     * @return Double value of command-line argument.
     * @throws IllegalArgumentException If there is a number format
     * exception converting the argument to a double, or if there is
     * no value supplied.
     */
    public double getArgumentDouble(String key) {
        String valString = getArgument(key);
        if (valString == null)
                throw new
                    IllegalArgumentException("No value found for argument="
                                             + key);
            try {
                return Double.valueOf(valString);
            } catch (NumberFormatException e) {
                throw new
                    IllegalArgumentException("Required double value for arg="
                                             + key
                                             + " Found=" + valString);
            }
    }

    /**
     * Returns the value of the argument with the specified key
     * converted to a file.  The argument must be specified.
     *
     * @param key Name of argument to convert to a file and return.
     * @return File specified by value of argument specified by key.
     * @throws IllegalArgumentException If the argument is not
     * specified.
     */
    public File getArgumentFile(String key) {
        String fileName = getExistingArgument(key);
        return new File(fileName);
    }

    /**
     * Returns the existing normal file named by the value of
     * the specified key.  The argument must be specified.
     *
     * @param key Name of argument to convert to file, check for
     * existence, and return.
     * @return File named by the value of the specified key.
     * @throws IllegalArgumentException If the argument has no value
     * or is not an existing file.
     */
    public File getArgumentExistingNormalFile(String key) {
        File file = getArgumentFile(key);
        if (!file.isFile())
            illegalPropertyArgument("Require existing normal file.",key);
        return file;
    }

    /**
     * Returns the value of the argument with the specified key
     * converted to a directory.  The directory must exist and
     * be a directory.
     *
     * @param key Name of argument to convert to a directory and return.
     * @return Directory specified by value of argument specified by key.
     * @throws IllegalArgumentException If the argument is not
     * specified, or is not an existing directory.
     */
    public File getArgumentDirectory(String key) {
        File dir = getArgumentFile(key);
        try {
            if (!dir.isDirectory())
                illegalPropertyArgument("Require existing directory.",
                                        key);
        } catch (SecurityException e) {
            illegalPropertyArgument("Security exception accessing directory.",
                                    key);
        }
        return dir;
    }

    /**
     * Returns the value of the argument with the specified key
     * converted to a directory.  The directory must either exist
     * or be creatable.  The return value will be an existing
     * directory.
     *
     * @param key Name of argument to convert to a directory and return.
     * @return Directory specified by value of argument specified by key.
     * @throws IllegalArgumentException If the argument is not
     * specified, and is not an existing or creatable directory.
     */
    public File getOrCreateArgumentDirectory(String key) {
        File dir = getArgumentFile(key);
        try {
            if (dir.isFile())
                illegalPropertyArgument("Must be existing or creatable directory.",
                                        key);
            if (!dir.isDirectory() && !dir.mkdirs())
                illegalPropertyArgument("Could not create directory.",
                                        key);
        } catch (SecurityException e) {
            illegalPropertyArgument("Security exception inspecting or creating directory.",
                                    key);
        }
        return dir;
    }

    /**
     * Returns the model file specified by the command line
     * after guaranteeing that the file can be created.
     *
     * @return Normal file containing the model.
     * @throws IllegalArgumentException If the model is not specified
     * on the command line or is not a file that can be created.
     */
    protected File getArgumentCreatableFile(String fileParam) {
        File file = getArgumentFile(fileParam);
        if (file.isDirectory())
            illegalPropertyArgument("File must be normal.  Found directory=",fileParam);
        File parentDir = file.getParentFile();
        if (parentDir == null)
            parentDir = new File(".");
        if (parentDir.isFile())
            illegalPropertyArgument("Parent cannot be ordinary file.",
                                    fileParam);
        if (!parentDir.isDirectory()) {
            System.out.println("Creating model parent directory=" + parentDir);
            parentDir.mkdirs();
        }
        return file;
    }

    /**
     * Parses a sequence of command-line argument, adding them to
     * these arguments.
     *
     * @param args Command-line arguments.
     * @throws IllegalArgumentException If the arguments are not well formed.
     */
    private final void parse(String[] args) {
        for (int i = 0; i < args.length; ++i)
            parseSingleArg(args[i]);
    }

    /**
     * Parses a single command-line argument, adding the result to these
     * arguments.
     *
     * @param arg Command-line argument.
     * @throws IllegalArgumentException If the arguments are not well formed.
     */
    private final void parseSingleArg(String arg) {
        if (arg.length() < 1)
            return;
        else if (arg.charAt(0) == '-')
            parseSingleBody(arg.substring(1));
        else
            mProperties.setProperty(bareArgumentProperty(mBareArgCount++),
                                    arg);
    }

    /**
     * Parses the body of a single argument that was provided
     * beginning with a <code>-</code>, adding it to these arguments.
     * The argument may contain an <code>=</code> sign or not.  The
     * argument may be empty, in which case the empty string is returned.
     *
     * @param arg Command-line argument.
     * @throws IllegalArgumentException If the arguments are not well formed.
     */
    private void parseSingleBody(String arg) {
        if (arg.length() < 1) return;
        int pos = arg.indexOf('=');
        if (pos < 0) {
            mProperties.setProperty(arg,HAS_PROPERTY_VALUE);
            return;
        }
        String property = arg.substring(0,pos);
        String value = arg.substring(pos+1);
        if (property.length() <= 0)
            illegalArgument("Property must have non-zero-length.",
                            '-'+arg+'='+value);

    // made this OK after 2.2.1
        // if (value.length() <= 0)
    // illegalArgument("Value must have non-zero length.",
    // '-'+arg+'='+value);
        mProperties.setProperty(property,value);
    }

    /**
     * Throw an illegal argument message with the specified message
     * and a report of the specified key and its value.
     *
     * @param msg Message to display.
     * @param key Key required or found on the command line.
     * @throws IllegalArgumentException Always, with specified message and
     * a report of the found argument.
     */
    protected void illegalPropertyArgument(String msg, String key) {

        throw new IllegalArgumentException(msg
                                           + " Found -" + key
                                           + "=" + getArgument(key));
    }

    /**
     * Throw an illegal argument exception with the specified message
     * and a report of the argument found.
     *
     * @param msg Message to display.
     * @param arg The argument found on the command line.
     * @throws IllegalArgumentException Always, with specified message and
     * a report of the found argument.
     */
    protected void illegalArgument(String msg, String arg) {

        illegalArgument(msg + "Found:" + arg);
    }

    /**
     * Throw an illegal argument exception with the specified message
     * with a source provided by the specified exception.
     *
     * @param msg Message to display.
     * @param e Exception causing the illegal argument exception.
     * @throws IllegalArgumentException Always, with specified message and
     * a report of embedded exception.
     */
    protected void illegalArgument(String msg, Exception e) {
        illegalArgument(msg + " Contained exception =" + e);
    }

    /**
     * Throw an illegal argument exception with the specified message.
     *
     * @param msg Message to include in the exception.
     * @throws IllegalArgumentException Always, with the specified message.
     */
    protected void illegalArgument(String msg) {
        throw new IllegalArgumentException(msg);
    }

    /**
     * Throws an illegal argument exception if the first parameter is
     * defined in the command the second parameter is not.  This is
     * used to check implicational constraints between parameters.
     *
     * @param ifParam Parameter to test for definedness.
     * @param thenParam Parameter to test for definedness.
     * @throws IllegalArgumentException If the first parameter is
     * defined and the second is not.
     */
    public void checkParameterImplication(String ifParam, String thenParam) {
        String ifVal = getArgument(ifParam);
        String thenVal = getArgument(thenParam);
        if (ifVal != null && thenVal == null)
            illegalArgument("If param=" + ifParam
                            + " is defined, then param=" + thenParam
                            + " should be defined.");
    }

    /**
     * Returns the name of the property for the <code>n</code>th
     * bare argument.
     *
     * @param n Index of bare property.
     * @return Name of bare argument property.
     */
    private static String bareArgumentProperty(int n) {
        return BARE_ARG_PREFIX + n;
    }

    /**
     * The value assigned to arguments beginning with <code>'-'</code>
     * and not containing an <code>'='</code>.
     */
    public static final String HAS_PROPERTY_VALUE = "*HAS_PROPERTY_VALUE*";

    /**
     * The string prefixed before a number to indicate bare arguments.
     * For instance, the fifth bare argument will be the value of the
     * property <code>{@link #BARE_ARG_PREFIX}&nbsp;+&nbsp;5</code>.
     */
    public static final String BARE_ARG_PREFIX = "BARE_ARG_";

}
