package apromore.plugin.portal.logfilteree;

import java.time.ZonedDateTime;

public class ZDTIntPair {
    private ZonedDateTime key;
    private Integer value;
    public ZDTIntPair(ZonedDateTime key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public ZonedDateTime getKey() {
        return key;
    }

    public Integer getValue() {
        return value;
    }
}
