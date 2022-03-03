/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.commons.utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class HTTPClient {
    public static final String APPLICATION_JSON = "application/json";

    /**
     * This method invoke the HTTP request to get with basic authentication.
     *
     * @param url
     * @param username
     * @param password
     * @return
     * @throws IOException
     */
    public static String getWithAuth(final String url, final String username, final String password)
            throws IOException {
        HttpClient client = getHttpClient(username, password);
        HttpGet get = new HttpGet(url);
        get.setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
        return IOUtils.toString(client.execute(get).getEntity().getContent(), StandardCharsets.UTF_8);
    }

    /**
     * This method invoke the HTTP request to post with basic authentication.
     *
     * @param url
     * @param body
     * @param username
     * @param password
     * @return
     * @throws IOException
     */
    public static String postWithAuth(final String url, final String body, final String username,
                                      final String password) throws IOException {
        HttpClient client = getHttpClient(username, password);
        HttpPost post = new HttpPost(url);
        post.setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
        post.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
        return IOUtils.toString(client.execute(post).getEntity().getContent(), StandardCharsets.UTF_8);
    }

    /**
     * This method invoke the HTTP request to patch with basic authentication.
     *
     * @param url
     * @param body
     * @param username
     * @param password
     * @return
     * @throws IOException
     */
    public static String patchWithAuth(final String url, final String body, final String username,
                                       final String password) throws IOException {
        HttpClient client = getHttpClient(username, password);
        HttpPatch patch = new HttpPatch(url);
        patch.setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
        patch.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
        return IOUtils.toString(client.execute(patch).getEntity().getContent(), StandardCharsets.UTF_8);
    }

    /**
     * This method invoke the HTTP request to delete with basic authentication.
     *
     * @param url
     * @param username
     * @param password
     * @return
     * @throws IOException
     */
    public static int deleteWithAuth(final String url, final String username, final String password)
            throws IOException {
        HttpClient client = getHttpClient(username, password);
        HttpDelete delete = new HttpDelete(url);
        delete.setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
        return client.execute(delete).getStatusLine().getStatusCode();
    }

    /**
     * This method prepares the Http client for the given username and possword.
     *
     * @param username
     * @param password
     * @return
     */
    public static HttpClient getHttpClient(final String username, final String password) {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
            username, password
        );
        provider.setCredentials(AuthScope.ANY, credentials);
        return HttpClientBuilder.create()
        .setDefaultCredentialsProvider(provider)
        .build();
    }

    /**
     * Dummy constructor.
     */
    private HTTPClient() {
    }
}
