/**
 * Copyright (c) 2010
 * Signavio GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
if(!Apromore) var Apromore = {};

if(!Apromore.CONFIG) Apromore.CONFIG = {};

/**
 * This file contains URI constants that may be used for XMLHTTPRequests.
 */

Apromore.CONFIG.ROOT_PATH =					(Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX) ? Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX + "/editor/" : "../editor/"; //TODO: Remove last slash!!
Apromore.CONFIG.EXPLORER_PATH =				(Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX) ? Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX + "/explorer/" : "../explorer";
Apromore.CONFIG.LIBS_PATH =					(Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX) ? Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX + "/libs/" : "../libs";
Apromore.PATH = Apromore.CONFIG.ROOT_PATH;

/**
 * Regular Config
 */
//Apromore.CONFIG.SERVER_HANDLER_ROOT = 		(Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX) ? Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX + "/p" : "../p";
Apromore.CONFIG.SERVER_HANDLER_ROOT = 		Apromore.CONFIG.SERVER_HANDLER_ROOT_PREFIX;
Apromore.CONFIG.SERVER_EDITOR_HANDLER =		Apromore.CONFIG.SERVER_HANDLER_ROOT + "/editor";
Apromore.CONFIG.SERVER_MODEL_HANDLER =		Apromore.CONFIG.SERVER_HANDLER_ROOT + "/model";
Apromore.CONFIG.PLUGINS_CONFIG =			Apromore.CONFIG.SERVER_HANDLER_ROOT + "/bpmneditor_plugins";
Apromore.CONFIG.SYNTAXCHECKER_URL =			Apromore.CONFIG.SERVER_HANDLER_ROOT + "/syntaxchecker";

Apromore.CONFIG.SS_EXTENSIONS_FOLDER =		Apromore.CONFIG.ROOT_PATH + "stencilsets/extensions/";
Apromore.CONFIG.SS_EXTENSIONS_CONFIG =		Apromore.CONFIG.SERVER_HANDLER_ROOT + "/editor_ssextensions";
Apromore.CONFIG.Apromore_NEW_URL =				"/new";
Apromore.CONFIG.BPMN_LAYOUTER =				Apromore.CONFIG.ROOT_PATH + "bpmnlayouter";

Apromore.CONFIG.GLOSSARY_PATH = "/glossary";
Apromore.CONFIG.GLOSSARY_PROPERTY_SUFFIX = "_glossary";
Apromore.CONFIG.GLOSSARY_PROPERTY_DIRTY_SUFFIX = "_glossary_dirty";

