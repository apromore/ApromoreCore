package org.apromore.plugin.exception;

public class PluginPropertyNotFoundException extends PluginException {

    private static final long serialVersionUID = 709970764651482793L;

    public PluginPropertyNotFoundException() {
        super();
    }

    public PluginPropertyNotFoundException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public PluginPropertyNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PluginPropertyNotFoundException(final String message) {
        super(message);
    }

    public PluginPropertyNotFoundException(final Throwable cause) {
        super(cause);
    }
}
