package dashboard;

public class LongIntPairImpl implements LongIntPair{
    private Long key;
    private Integer value;
    public LongIntPairImpl(Long key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public Long getKey() {
        return key;
    }

    public Integer getValue() {
        return value;
    }
}
