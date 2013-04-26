package org.apromore.service.impl;

import org.apromore.dao.AnnotationRepository;
import org.apromore.dao.CanonicalRepository;
import org.apromore.dao.NativeRepository;
import org.apromore.dao.NativeTypeRepository;
import org.apromore.dao.model.NativeType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Unit test the UserService Implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class FormatServiceImplUnitTest {

    private FormatServiceImpl formatServiceImpl;

    private AnnotationRepository annotationRepository;
    private CanonicalRepository canonicalRepository;
    private NativeRepository nativeRepository;
    private NativeTypeRepository nativeTypeRepository;

    @Before
    public void setUp() {
        annotationRepository = createMock(AnnotationRepository.class);
        canonicalRepository = createMock(CanonicalRepository.class);
        nativeRepository = createMock(NativeRepository.class);
        nativeTypeRepository = createMock(NativeTypeRepository.class);

        formatServiceImpl = new FormatServiceImpl(annotationRepository, canonicalRepository, nativeRepository, nativeTypeRepository);
    }


    @Test
    public void getAllFormats() {
        List<NativeType> natTypes = new ArrayList<NativeType>();

        expect(nativeTypeRepository.findAll()).andReturn(natTypes);
        replay(nativeTypeRepository);

        List<NativeType> serviceNatTypes = formatServiceImpl.findAllFormats();
        verify(nativeTypeRepository);
        assertThat(serviceNatTypes, equalTo(natTypes));
    }

    @Test
    public void getFormat() {
        String type = "bobType";
        NativeType natType = new NativeType();
        natType.setNatType(type);

        expect(nativeTypeRepository.findNativeType(type)).andReturn(natType);
        replay(nativeTypeRepository);

        NativeType serviceNatType = formatServiceImpl.findNativeType(type);
        verify(nativeTypeRepository);
        assertThat(serviceNatType, equalTo(natType));
    }
}
