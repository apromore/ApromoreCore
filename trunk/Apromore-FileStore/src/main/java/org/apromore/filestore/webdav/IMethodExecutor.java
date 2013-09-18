package org.apromore.filestore.webdav;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apromore.filestore.webdav.exceptions.LockFailedException;

public interface IMethodExecutor {

    void execute(ITransaction transaction, HttpServletRequest req, HttpServletResponse resp) throws IOException, LockFailedException;

}
