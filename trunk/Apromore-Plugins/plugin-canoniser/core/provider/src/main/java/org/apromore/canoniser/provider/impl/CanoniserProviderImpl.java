/**
 * Copyright 2012, Felix Mannhardt
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.canoniser.provider.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.provider.CanoniserProvider;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Providing the default CanoniserProvider implementation using OSGi services
 * 
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 * 
 */
@Service
public class CanoniserProviderImpl implements CanoniserProvider {

    /**
     * Will be injected by Gemini Blueprint OSGi Framework at runtime
     */
    @Resource
    private List<Canoniser> canoniserList;

    // Getter and Setter need to be public for DI

    public List<Canoniser> getCanoniserList() {
        return canoniserList;
    }

    public void setCanoniserList(final List<Canoniser> canoniserList) {
        this.canoniserList = canoniserList;
    }

    @Override
    public final void canonise(final String nativeType, final InputStream nativeInput, List<AnnotationsType> annotationFormat,
            List<CanonicalProcessType> canonicalFormat) throws CanoniserException, PluginNotFoundException {
        final Canoniser c = findByNativeTypeAndNameAndVersion(nativeType, null, null);
        c.canonise(nativeInput, annotationFormat, canonicalFormat);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.provider.CanoniserProvider#canonise(java.lang.String, java.lang.String, org.apromore.canoniser.NativeInput,
     * java.io.OutputStream, java.io.OutputStream)
     */
    @Override
    public final void canonise(final String nativeType, final String name, final InputStream nativeInput, List<AnnotationsType> annotationFormat,
            List<CanonicalProcessType> canonicalFormat) throws CanoniserException, PluginNotFoundException {
        final Canoniser c = findByNativeTypeAndNameAndVersion(nativeType, name, null);
        c.canonise(nativeInput, annotationFormat, canonicalFormat);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.provider.CanoniserProvider#canonise(java.lang.String, java.lang.String, java.lang.String,
     * org.apromore.canoniser.NativeInput, java.io.OutputStream, java.io.OutputStream)
     */
    @Override
    public final void canonise(final String nativeType, final String name, final String version, final InputStream nativeInput,
            List<AnnotationsType> annotationFormat, List<CanonicalProcessType> canonicalFormat) throws CanoniserException, PluginNotFoundException {
        final Canoniser c = findByNativeTypeAndNameAndVersion(nativeType, name, version);
        c.canonise(nativeInput, annotationFormat, canonicalFormat);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.provider.CanoniserProvider#deCanonise(java.lang.String, java.io.InputStream, java.io.InputStream,
     * org.apromore.canoniser.NativeOutput)
     */
    @Override
    public final void deCanonise(final String nativeType, final AnnotationsType annotationFormat, final CanonicalProcessType canonicalFormat,
            final OutputStream nativeOutput) throws CanoniserException, PluginNotFoundException {
        final Canoniser c = findByNativeTypeAndNameAndVersion(nativeType, null, null);
        c.deCanonise(canonicalFormat, annotationFormat, nativeOutput);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.provider.CanoniserProvider#deCanonise(java.lang.String, java.lang.String, java.io.InputStream, java.io.InputStream,
     * org.apromore.canoniser.NativeOutput)
     */
    @Override
    public final void deCanonise(final String nativeType, final String name, final AnnotationsType annotationFormat,
            final CanonicalProcessType canonicalFormat, final OutputStream nativeOutput) throws CanoniserException, PluginNotFoundException {
        final Canoniser c = findByNativeTypeAndNameAndVersion(nativeType, name, null);
        c.deCanonise(canonicalFormat, annotationFormat, nativeOutput);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.provider.CanoniserProvider#deCanonise(java.lang.String, java.lang.String, java.lang.String, java.io.InputStream,
     * java.io.InputStream, org.apromore.canoniser.NativeOutput)
     */
    @Override
    public final void deCanonise(final String nativeType, final String name, final String version, final AnnotationsType annotationFormat,
            final CanonicalProcessType canonicalFormat, final OutputStream nativeOutput) throws PluginNotFoundException, CanoniserException {
        final Canoniser c = findByNativeTypeAndNameAndVersion(nativeType, name, version);
        c.deCanonise(canonicalFormat, annotationFormat, nativeOutput);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.provider.CanoniserProvider#listAll()
     */
    @Override
    public final Collection<Canoniser> listAll() {
        return Collections.unmodifiableCollection(getCanoniserList());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.provider.CanoniserProvider#listByNativeType(java.lang.String)
     */
    @Override
    public final Collection<Canoniser> listByNativeType(final String nativeType) {
        return Collections.unmodifiableCollection(findAllCanoniser(nativeType, null, null));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.provider.CanoniserProvider#listByNativeTypeAndName(java.lang.String, java.lang.String)
     */
    @Override
    public final Collection<Canoniser> listByNativeTypeAndName(final String nativeType, final String name) {
        return Collections.unmodifiableCollection(findAllCanoniser(nativeType, name, null));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.provider.CanoniserProvider#findByNativeType(java.lang.String)
     */
    @Override
    public final Canoniser findByNativeType(final String nativeType) throws PluginNotFoundException {
        return findByNativeTypeAndNameAndVersion(nativeType, null, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.provider.CanoniserProvider#findByNativeTypeAndName(java.lang.String, java.lang.String)
     */
    @Override
    public final Canoniser findByNativeTypeAndName(final String nativeType, final String name) throws PluginNotFoundException {
        return findByNativeTypeAndNameAndVersion(nativeType, name, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.provider.CanoniserProvider#findByNativeTypeAndNameAndVersion(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public final Canoniser findByNativeTypeAndNameAndVersion(final String nativeType, final String name, final String version)
            throws PluginNotFoundException {
        final List<Canoniser> resultList = findAllCanoniser(nativeType, name, version);
        if (!resultList.isEmpty()) {
            // TODO decide which to take if there are more than 1 matching
            return resultList.get(0);
        }
        throw new PluginNotFoundException("Could not find canoniser with name: " + ((name != null) ? name : "null") + " version: "
                + ((version != null) ? version : "null") + " nativeType: " + ((nativeType != null) ? nativeType : "null"));
    }

    private List<Canoniser> findAllCanoniser(final String nativeType, final String name, final String version) {

        final List<Canoniser> canoniserList = new ArrayList<Canoniser>();

        for (final Canoniser c : getCanoniserList()) {
            if (compareNullable(nativeType, c.getNativeType()) && compareNullable(name, c.getName()) && compareNullable(version, c.getVersion())) {
                canoniserList.add(c);
            }
        }
        return canoniserList;
    }

    private boolean compareNullable(final String expectedType, final String actualType) {
        return expectedType == null ? true : expectedType.equals(actualType);
    }

}
