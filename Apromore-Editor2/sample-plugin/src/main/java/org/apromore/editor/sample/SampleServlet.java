package org.apromore.editor.sample;

// Java 2 Standard
import java.io.IOException;
import java.io.Writer;

// Java 2 Enterprise
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class SampleServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_OK);

        Writer w = response.getWriter();
        w.write("Hello, world!");
        w.close();
    }
}
