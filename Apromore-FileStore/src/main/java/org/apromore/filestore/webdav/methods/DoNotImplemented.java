package org.apromore.filestore.webdav.methods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.apromore.filestore.webdav.IMethodExecutor;
import org.apromore.filestore.webdav.ITransaction;
import org.apromore.filestore.webdav.WebDavStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoNotImplemented implements IMethodExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoNotImplemented.class.getName());
    private boolean _readOnly;


    public DoNotImplemented(boolean readOnly) {
        _readOnly = readOnly;
    }

    public void execute(ITransaction transaction, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOGGER.trace("-- " + req.getMethod());

        if (_readOnly) {
            resp.sendError(WebDavStatus.SC_FORBIDDEN);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
    }

}
