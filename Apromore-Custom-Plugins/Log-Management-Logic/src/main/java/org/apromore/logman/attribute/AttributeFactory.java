package org.apromore.logman.attribute;

public class AttributeFactory {
	public static DiscreteAttribute createDiscreteAttribute(String key, AttributeLevel level) {
		return new DiscreteAttribute(key, level);
	}
	
	public static ContinuousAttribute createContinuousAttribute(String key, AttributeLevel level) {
		return new ContinuousAttribute(key, level);
	}
	
	public static LiteralAttribute createLiteralAttribute(String key, AttributeLevel level) {
		return new LiteralAttribute(key, level);
	}
	
	public static BooleanAttribute createBooleanAttribute(String key, AttributeLevel level) {
		return new BooleanAttribute(key, level);
	}
	
	public static TimestampAttribute createTimestampAttribute(String key, AttributeLevel level) {
		return new TimestampAttribute(key, level);
	}
}
