/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */
package org.apromore.portal.servlet;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.Arrays;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.easymock.Capture;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test suite for {@link PortalPluginResourceServlet}.
 */
class PortalPluginResourceServletUnitTest {

    /**
     * Instance under test.
     */
    private PortalPluginResourceServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletOutputStream servletOutputStream;

    @BeforeEach
    void beforeEach() {
        servlet = new PortalPluginResourceServlet();
        servlet.init();

        request = createMock(HttpServletRequest.class);
        response = createMock(HttpServletResponse.class);
        servletOutputStream = createMock(ServletOutputStream.class);
    }

    /**
     * Unit test the {@link PortalPluginResourceServlet#doGet} method with valid inputs.
     *
     * This test applies to servlet patterns with a trailing *.
     *
     * @param pathInfo  path to a test file in <code>src/test/resources/</code>
     * @param expectedContent  the content of the test file, always actually ASCII text
     * @param expectedContentType  the MIME type indicated by the test file's extension
     */
    @ParameterizedTest
    @CsvSource({"/test-icon.svg,             FAKE SVG CONTENT,   image/svg+xml",
                "/test-icon.png,             FAKE PNG CONTENT,   image/png",
                "/test-folder/test-icon.svg, FOLDER SVG CONTENT, image/svg+xml"})
    void testDoGet_ok(final String pathInfo,
                      final String expectedContent,
                      final String expectedContentType) throws Exception {

        final int expectedContentLength = expectedContent.getBytes(UTF_8).length;
        final Capture<byte[]> responseContent = newCapture();

        // Record the expected interactions with the mock objects
        expect(request.getPathInfo()).andReturn(pathInfo);
        expect(request.getPathInfo()).andReturn(pathInfo);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(expectedContentType);
        expect(response.getOutputStream()).andReturn(servletOutputStream);
        servletOutputStream.write(capture(responseContent), eq(0), eq(expectedContentLength + 1));
        servletOutputStream.close();
        replay(request, response, servletOutputStream);

        // Perform the test
        servlet.doGet(request, response);

        // Validate the response
        verify(response);
        assert responseContent.hasCaptured();
        assertEquals(expectedContent, new String(Arrays.copyOf(responseContent.getValue(), expectedContentLength), UTF_8));
    }

    /**
     * Unit test the {@link PortalPluginResourceServlet#doGet} method with valid inputs.
     *
     * This test applies to servlet patterns without a trailing *.
     *
     * @param requestURI  path to a test file in <code>src/test/resources/</code>
     * @param expectedContent  the content of the test file, always actually ASCII text
     * @param expectedContentType  the MIME type indicated by the test file's extension
     */
    @ParameterizedTest
    @CsvSource({"/test-favicon.ico, FAKE ICO CONTENT, image/x-icon"})
    void testDoGet_ok2(final String requestURI,
                       final String expectedContent,
                       final String expectedContentType) throws Exception {

        final int expectedContentLength = expectedContent.getBytes(UTF_8).length;
        final Capture<byte[]> responseContent = newCapture();

        // Record the expected interactions with the mock objects
        expect(request.getPathInfo()).andReturn(null);
        expect(request.getRequestURI()).andReturn(requestURI);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(expectedContentType);
        expect(response.getOutputStream()).andReturn(servletOutputStream);
        servletOutputStream.write(capture(responseContent), eq(0), eq(expectedContentLength + 1));
        servletOutputStream.close();
        replay(request, response, servletOutputStream);

        // Perform the test
        servlet.doGet(request, response);

        // Validate the response
        verify(response);
        assert responseContent.hasCaptured();
        assertEquals(expectedContent, new String(Arrays.copyOf(responseContent.getValue(), expectedContentLength), UTF_8));
    }

    /**
     * Unit test the {@link PortalPluginResourceServlet#doGet} method with invalid inputs.
     *
     * @param pathInfo  servlet path info that doesn't correspond to a resource on the classpath
     * @param expectedErrorMessage  the response content
     */
    @ParameterizedTest
    @CsvSource({"no-leading-slash.png, Unable to parse path no-leading-slash.png",
                "/no-extension,        Unable to parse path /no-extension",
                "/nonexistent.svg,     Unable to find resource for /nonexistent.svg",
                "/unsupported.ext,     Unsupported resource extension \"ext\" for /unsupported.ext",
                "/test-icon.svg.gz,    Unsupported resource extension \"gz\" for /test-icon.svg.gz"})
    void testDoGet_forbidden(final String pathInfo,
                             final String expectedErrorMessage) throws Exception {

        // Record the expected interactions with the mock objects
        expect(request.getPathInfo()).andReturn(pathInfo);
        expect(request.getPathInfo()).andReturn(pathInfo);
        expect(request.getPathInfo()).andReturn(pathInfo);  // read 2nd time for use in error message
        response.sendError(HttpServletResponse.SC_FORBIDDEN, expectedErrorMessage);
        replay(request, response, servletOutputStream);

        // Perform the test
        servlet.doGet(request, response);

        // Validate the response
        verify(response);
    }
}
