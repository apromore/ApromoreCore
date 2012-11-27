package org.apromore.mapper;

import org.apromore.model.DomainsType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Domain Mapper Unit test.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class DomainMapperUnitTest {

    DomainMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new DomainMapper();
    }

    @Test
    public void testConvertFromDomains() throws Exception {
        List<String> domains = new ArrayList<String>();
        String typ1 = "jack";
        String typ2 = "john";
        domains.add(typ1);
        domains.add(typ2);

        DomainsType type = mapper.convertFromDomains(domains);
        assertThat(type.getDomain().size(), equalTo(domains.size()));
        assertThat(type.getDomain().get(0), equalTo(domains.get(0)));
        assertThat(type.getDomain().get(0), equalTo(domains.get(0)));
    }

}
