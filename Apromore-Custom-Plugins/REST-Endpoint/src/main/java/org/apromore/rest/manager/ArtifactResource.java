/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.rest.manager;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeFactory;
import org.apromore.dao.LogRepository;
import org.apromore.dao.model.Log;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.UserType;
import org.apromore.rest.AbstractResource;
import org.apromore.rest.ResourceException;
import org.apromore.service.EventLogService;
import org.apromore.service.WorkspaceService;
import org.apromore.service.csvimporter.model.LogMetaData;
import org.apromore.service.csvimporter.services.ParquetFactoryProvider;
import org.apromore.service.csvimporter.services.legacy.LogImporterProvider;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.model.FolderTreeNode;
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST endpoint for artifact management.
 *
 * Artifacts are organized in a folder tree whose root URL path is <code>/rest/Home/</code>.
 */
@Path("/Home")
public final class ArtifactResource extends AbstractResource {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactResource.class);

    /** Used to check the media type of uploaded logs (CSV, Excel, Parquet, XES) */
    @Context private HttpHeaders httpHeaders;

    /**
     * Download a log.
     *
     * <pre>curl http://localhost:9000/rest/Home/foo -u admin:password &gt; foo.xes.gz</pre>
     *
     * @return a GZIPped XES XML document
     */
    @GET
    @Path("{path:(.*/)*}{name}")
    public Response getLog(final @PathParam("path") String path,
                           final @PathParam("name") String name) throws Exception {

        // Only authorize admin accounts
        UserType user = authenticatedUser();
        authorize(user, "ROLE_ADMIN");

        // Try to access the folder using the given credentials
        WorkspaceService workspaceService = osgiService(WorkspaceService.class);
        int folderId = findFolderIdByPath(path, user.getId(), workspaceService);

        // Look for the event log in the folder
        Log log = osgiService(LogRepository.class).findByNameAndFolderId(name, folderId == 0 ? null : folderId);
        if (log == null) {
            throw new ResourceException(Response.Status.NOT_FOUND,
                "No log named \"" + name + "\" in folder " + path);
        }

        // Obtain the serialization of the event log
        EventLogService eventLogService = osgiService(EventLogService.class);
        XLog xLog = eventLogService.getXLog(log.getId());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        eventLogService.exportToStream(baos, xLog);

        return Response.status(Response.Status.OK)
                       .header("Content-Encoding", "gzip")  // In JAX-RS 2, could use .encoding("gzip")
                       .type(MediaType.APPLICATION_XML)
                       .entity(baos.toByteArray())
                       .build();
    }

    /**
     * Logs may only be created, not modified.
     *
     * <pre>curl http://localhost:9000/rest/Home/foo \
     *     -u admin:password \
     *     --header "Content-Type: application/xml" \
     *     --data @foo.xes</pre>
     *
     * For CSV and parquet, additional log metadata must be passed in the Apromore-Log-Metadata header
     * in JSON format.
     *
     * <pre>curl http://localhost:9000/rest/Home/bar \
     *     -u admin:password \
     *     --header "Apromore-Log-Metadata: `tr '\n' ' ' &lt; bar.json`" \
     *     --header "Content-Type: text/csv" \
     *     --data @bar.csv</pre>
     *
     * <pre>curl http://localhost:9000/rest/Home/baz \
     *     -u admin:password \
     *     --header "Apromore-Log-Metadata: `tr '\n' ' ' &lt; baz.json`" \
     *     --header "Content-Type: application/x-parquet" \
     *     --data @baz.parquet</pre>
     *
     * @param body  the log to upload in uncompressed XES XML format
     * @return the actual event log created, including the generated id
     * @throws ResourceException if <var>logSummary</var> isn't suitable
     */
    @POST
    @Path("{path:(.*/)*}{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public LogSummaryType postLog(final @HeaderParam("Apromore-Log-Metadata") String logMetaDataHeader,
                                  final @PathParam("path") String path,
                                  final @PathParam("name") String name,
                                  final InputStream body) throws Exception {

        // Only authorize admin accounts
        UserType user = authenticatedUser();
        authorize(user, "ROLE_ADMIN");

        // Try to access the folder using the given credentials
        int folderId = findFolderIdByPath(path, user.getId(), osgiService(WorkspaceService.class));

        LOGGER.info("Media type " + httpHeaders.getMediaType());

        // Import the event log
        Log log;
        if (httpHeaders.getMediaType().isCompatible(MediaType.valueOf(MediaType.APPLICATION_XML))) {
            log = osgiService(EventLogService.class).importLog(
                user.getUsername(),
                folderId,
                name,
                body,
                "xes",  // extension; specifies uncompressed (no GZIP) XES format
                "",  // domain
                DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString(),
                true); // publicModel

        } else if (httpHeaders.getMediaType().isCompatible(new MediaType("text", "csv"))) {
            log = logWithMetadata("csv", body, logMetaDataHeader, user, name, folderId);

        } else if (httpHeaders.getMediaType().isCompatible(new MediaType("application", "x-parquet"))) {
            log = logWithMetadata("parquet", body, logMetaDataHeader, user, name, folderId);

        } else {
            throw new ResourceException(Response.Status.UNSUPPORTED_MEDIA_TYPE,
                "Request had content type " + httpHeaders.getMediaType() + " but only " +
                MediaType.APPLICATION_XML + ", text/csv, application/x-parquet are supported");
        }

        // Return a description of the created event log
        return (LogSummaryType) osgiService(UserInterfaceHelper.class).buildLogSummary(log);
    }

    private Log logWithMetadata(final String mediaFormat, final InputStream body, final String logMetaDataHeader,
                                final UserType user, final String name, final int folderId) throws Exception {

        if (logMetaDataHeader == null) {
            throw new ResourceException(Response.Status.BAD_REQUEST,
                "Requests with content type " + mediaFormat +
                " require an Apromore-Log-Metadata header containing log metadata in JSON format.");
        }

        LogMetaData logMetaData = osgiService(ParquetFactoryProvider.class)
            .getParquetFactory(mediaFormat)
            .getMetaDataService()
            .extractMetadata(new StringBufferInputStream(logMetaDataHeader), "UTF-8");

        return osgiService(LogImporterProvider.class)
            .getLogReader(mediaFormat)
            .importLog(body, logMetaData, "UTF-8", true, user.getUsername(), folderId, name)
            .getImportLog();
    }

    /**
     * Lookup a folder by its path.
     *
     * This method ought to be made part of the {@link WorkspaceService}.
     *
     * @param path  a slash-delimited folder path, e.g. "foo/bar/".  Empty string denotes the root folder.
     *     Beware that "/" would be treated as a subfolder inside the root folder with the name "".
     * @param userId  the row GUID of the user under whose authority the folder is accessed
     * @param workspaceService  used to access folders
     * @return the primary key of the folder at the given <var>path</var>
     * @throws ResourceException if the <var>path</var> is not an existing folder
     */
    private static int findFolderIdByPath(final String path,
                                          final String userId,
                                          final WorkspaceService workspaceService) throws ResourceException {

        int folderId = 0;  // the root folder, "Home"

        // Descend into the requested directory
        List<FolderTreeNode> nodes = workspaceService.getWorkspaceFolderTree(userId);
        if (!path.isEmpty()) {
            subfolder: for (String pathElement: path.split("/")) {
                for (FolderTreeNode node: nodes) {
                    if (node.getName().equals(pathElement)) {
                        folderId = node.getId();
                        nodes = node.getSubFolders();
                        continue subfolder;
                    }
                }
                throw new ResourceException(Response.Status.NOT_FOUND,
                    "Subfolder \"" + pathElement + "\" of \" " + path + "\" does not exist");
            }
        }

        return folderId;
    }
}
