/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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
package org.apromore.portal;

import java.io.IOException;
import java.io.Reader;
import org.zkoss.idom.Document;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.ShadowInfo;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.ServerPush;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.util.Composer;

/**
 * Wrapper around a {@link UiFactory} instance.
 *
 * Behaves identically to the wrapped instance insofar as the {@link UiFactory} interface.
 * It's useful because it allows subclasses to override individual methods.
 *
 * In theory this could be a general-purpose ZK utility unrelated to the Apromore Portal, but in
 * practice I doubt we'll reuse it elsewhere.  Thus it resides in {@link org.apromore.portal}.
 */
class WrappedUiFactory implements UiFactory {

    private final UiFactory wrapped;

    WrappedUiFactory(final UiFactory wrapped) {
        this.wrapped = wrapped;
    }

    // Implementation of the UiFactory interface

    @Override
    public PageDefinition getPageDefinition(RequestInfo ri, String path) {
        return wrapped.getPageDefinition(ri, path);
    }

    @Override
    public PageDefinition getPageDefinitionDirectly(RequestInfo ri, Document content, String extension) {
        return wrapped.getPageDefinitionDirectly(ri, content, extension);
    }

    @Override
    public PageDefinition getPageDefinitionDirectly(RequestInfo ri, Reader reader, String extension) throws IOException {
        return wrapped.getPageDefinitionDirectly(ri, reader, extension);
    }

    @Override
    public PageDefinition getPageDefinitionDirectly(RequestInfo ri, String content, String extension) {
        return wrapped.getPageDefinitionDirectly(ri, content, extension);
    }

    @Override
    public Richlet getRichlet(RequestInfo ri, String path) {
        return wrapped.getRichlet(ri, path);
    }

    @Override
    public boolean isRichlet(RequestInfo ri, boolean bRichlet) {
        return wrapped.isRichlet(ri, bRichlet);
    }

    @Override
    public Component newComponent(Page page, Component parent, ComponentDefinition compdef, String clsnm) {
        return wrapped.newComponent(page, parent, compdef, clsnm);
    }

    @Override
    public Component newComponent(Page page, Component parent, ComponentInfo compInfo, Component insertBefore) {
        return wrapped.newComponent(page, parent, compInfo, insertBefore);
    }

    @Override
    public Component newComponent(Page page, Component parent, ShadowInfo compInfo, Component insertBefore) {
        return wrapped.newComponent(page, parent, compInfo, insertBefore);
    }

    @Override
    public Composer newComposer(Page page, Class klass) {
        return wrapped.newComposer(page, klass);
    }

    @Override
    public Composer newComposer(Page page, String className) throws ClassNotFoundException {
        return wrapped.newComposer(page, className);
    }

    @Override
    public Desktop newDesktop(RequestInfo ri, String updateURI, String path) {
        return wrapped.newDesktop(ri, updateURI, path);
    }

    @Override
    public Page newPage(RequestInfo ri, PageDefinition pagedef, String path) {
        return wrapped.newPage(ri, pagedef, path);
    }

    @Override
    public Page newPage(RequestInfo ri, Richlet richlet, String path) {
        return wrapped.newPage(ri, richlet, path);
    }

    @Override
    public ServerPush newServerPush(Desktop desktop, Class klass) {
        return wrapped.newServerPush(desktop, klass);
    }

    @Override
    public Session newSession(WebApp wapp, Object nativeSess, Object request) {
        return wrapped.newSession(wapp, nativeSess, request);
    }

    @Override
    public void start(WebApp wapp) {
        wrapped.start(wapp);
    }

    @Override
    public void stop(WebApp wapp) {
        wrapped.stop(wapp);
    }
}
