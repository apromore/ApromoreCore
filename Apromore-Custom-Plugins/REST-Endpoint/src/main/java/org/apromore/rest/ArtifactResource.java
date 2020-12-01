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
package org.apromore.rest;

import java.io.ByteArrayOutputStream;
import java.io.StringBufferInputStream;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeFactory;
import org.apromore.dao.LogRepository;
import org.apromore.dao.model.Log;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.UserType;
import org.apromore.service.EventLogService;
import org.apromore.service.WorkspaceService;
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

    /**
     * Download a log.
     *
     * <pre>curl http://localhost:9000/rest/Home/foo &gt; foo.xes.gz</pre>
     *
     * @return a GZIPped XES XML document
     */
    @GET
    @Path("{path:(.*/)*}{name}")
    public Response getLog(final @HeaderParam("Authorization") String authorization,
                           final @PathParam("path") String path,
                           final @PathParam("name") String name) throws Exception {

        // Only authorize admin accounts
        UserType user = authenticatedUser(authorization);
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
     * <pre>curl http://localhost:9000/rest/Home/foo --header "Content-Type: application/xml" --data @foo.xes</pre>
     *
     * @param body  the log to upload in uncompressed XES XML format
     * @return the actual event log created, including the generated id
     * @throws ResourceException if <var>logSummary</var> isn't suitable
     */
    @POST
    @Path("{path:(.*/)*}{name}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public LogSummaryType postLog(final @HeaderParam("Authorization") String authorization,
                                  final @PathParam("path") String path,
                                  final @PathParam("name") String name,
                                  final String body) throws Exception {

        // Only authorize admin accounts
        UserType user = authenticatedUser(authorization);
        authorize(user, "ROLE_ADMIN");

        // Try to access the folder using the given credentials
        int folderId = findFolderIdByPath(path, user.getId(), osgiService(WorkspaceService.class));

        // Import the event log
        Log log = osgiService(EventLogService.class).importLog(
            user.getUsername(),
            folderId,
            name,
            new StringBufferInputStream(body),
            "xes",  // extension; specifies uncompressed (no GZIP) XES format
            "",  // domain
            DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString(),
            true); // publicModel

        // Return a description of the created event log
        return (LogSummaryType) osgiService(UserInterfaceHelper.class).buildLogSummary(log);
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
