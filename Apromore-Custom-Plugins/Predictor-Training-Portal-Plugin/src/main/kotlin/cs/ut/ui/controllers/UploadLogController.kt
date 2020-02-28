/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package cs.ut.ui.controllers

import cs.ut.configuration.ConfigurationReader
import cs.ut.logging.NirdizatiLogger
import cs.ut.providers.Dir
import cs.ut.providers.DirectoryConfiguration
import cs.ut.ui.UIComponent
import cs.ut.ui.controllers.modal.ParameterModalController.Companion.FILE
import cs.ut.util.NirdizatiInputStream
import cs.ut.util.NirdizatiReader
import org.apache.commons.io.FilenameUtils
import org.zkoss.util.media.Media
import org.zkoss.util.resource.Labels
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.UploadEvent
import org.zkoss.zk.ui.select.SelectorComposer
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Button
import org.zkoss.zul.Label
import org.zkoss.zul.Vbox
import org.zkoss.zul.Window
import java.io.File

class UploadLogController : SelectorComposer<Component>(), Redirectable, UIComponent {
    private val log = NirdizatiLogger.getLogger(UploadLogController::class, getSessionId())

    @Wire
    private lateinit var fileName: Label

    @Wire
    private lateinit var upload: Button

    @Wire
    private lateinit var fileNameCont: Vbox

    private lateinit var media: Media

    private val allowedExtensions = ConfigurationReader.findNode("fileUpload/extensions").itemListValues()

    /**
     * Method that analyzes uploaded file. Checks that the file has required extension.
     *
     * @param event upload event where media should be retrieved from
     */
    @Listen("onUpload = #chooseFile, #dropArea")
    fun analyzeFile(event: UploadEvent) {
        upload.isDisabled = true
        log.debug("Upload event. Analyzing file")

        val uploaded = event.media ?: return

        if (FilenameUtils.getExtension(uploaded.name) in allowedExtensions) {
            log.debug("Log is in allowed format")
            fileNameCont.sclass = "file-upload"
            fileName.value = uploaded.name
            media = uploaded
            upload.isDisabled = false
        } else {
            log.debug("Log is not in allowed format -> showing error")
            fileNameCont.sclass = "file-upload-err"
            fileName.value = Labels.getLabel(
                    "upload.wrong.format",
                    arrayOf(uploaded.name, FilenameUtils.getExtension(uploaded.name))
            )
            upload.isDisabled = true
        }
    }

    /**
     * Listener - user log has been accepted and now we need to generate data set parameters for it
     */
    @Listen("onClick = #upload")
    fun processLog() {
        val runnable = Runnable {
            val tmpDir = DirectoryConfiguration.dirPath(Dir.TMP_DIR)
            val file = File(tmpDir + media.name.replace(File.separator, "_"))
            log.debug("Creating file: ${file.absolutePath}")
            file.createNewFile()

            val uploadItem = if (media.isBinary) NirdizatiInputStream(media.streamData) else NirdizatiReader(media.readerData)
            uploadItem.write(file)

            val args = mapOf(FILE to file)

            // Detach all of the old windows
            self.getChildren<Component>()
                    .asSequence()
                    .filter { it is Window }
                    .forEach { it.detach() }
            val window: Window = Executions.createComponents(
                    "/views/modals/params.zul",
                    self,
                    args
            ) as Window
            if (self.getChildren<Component>().contains(window)) {
                window.doModal()
                upload.isDisabled = true
            }
        }
        runnable.run()
        log.debug("Started training file generation thread")
    }
}

