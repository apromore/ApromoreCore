package nl.rug.ds.bpm.expression;

public class Expression<T> {
	private T value;
	private String name;
	private ExpressionType type;
	
	public Expression(String name, ExpressionType type, T value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
	public ExpressionType getExpressionType() {
		return type;
	}
	
	public Boolean accepts(T value) {
		switch (type) {
		case EQ:
			return (this.value.equals(value)); 
		case NEQ:
			return (!this.value.equals(value));
		case LT:
			if ((value instanceof Number) && (this.value instanceof Number)) {
				return ((Double) value).doubleValue() < ((Double) this.value).doubleValue();
			}
		case LEQ:
			if ((value instanceof Number) && (this.value instanceof Number)) {
				return ((Double) value).doubleValue() <= ((Double) this.value).doubleValue();
			}
		case GT:
			if ((value instanceof Number) && (this.value instanceof Number)) {
				return ((Double) value).doubleValue() > ((Double) this.value).doubleValue();
			}
		case GEQ:
			if ((value instanceof Number) && (this.value instanceof Number)) {
				return ((Double) value).doubleValue() >= ((Double) this.value).doubleValue();
			}
		}
		return false;
	}
	
	// This method checks whether this ALWAYS contradicts other
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Boolean contradicts(Expression other) {
		if ((this.getClass().equals(other.getClass())) && (this.getName().equals(other.getName()))) {
			switch (type) {
			case EQ:
				return (!other.accepts(this.value)); 
			case NEQ:
				if ((other.getExpressionType() == ExpressionType.EQ) && (other.getValue().equals(this.value))) {
					return true;
				}
				else {
					return ((other.getExpressionType() == ExpressionType.NEQ) && (!other.getValue().equals(this.value)));
				}
			case LT:
				if ((other.value instanceof Number) && (this.value instanceof Number)) {
					if (other.getExpressionType() == ExpressionType.GT) {
						return (((Double) this.value).doubleValue() <= ((Double) other.value).doubleValue());
					}
					else if (other.getExpressionType() == ExpressionType.GEQ) {
						return (((Double) this.value).doubleValue() <= ((Double) other.value).doubleValue());
					}
					else {
						return ((other.getExpressionType() == ExpressionType.EQ) && (((Double)other.value).doubleValue() >= ((Double)this.value).doubleValue()));
					}
				}
			case LEQ:
				if ((other.value instanceof Number) && (this.value instanceof Number)) {
					if (other.getExpressionType() == ExpressionType.GT) {
						return (((Double) this.value).doubleValue() <= ((Double) other.value).doubleValue());
					}
					else if (other.getExpressionType() == ExpressionType.GEQ) {
						return (((Double) this.value).doubleValue() < ((Double) other.value).doubleValue());
					}
					else {
						return ((other.getExpressionType() == ExpressionType.EQ) && (((Double)other.value).doubleValue() > ((Double)this.value).doubleValue()));

					}
				}
			case GT:
			case GEQ:
				if ((other.value instanceof Number) && (this.value instanceof Number)) {
					if ((other.getExpressionType() == ExpressionType.GT) || (other.getExpressionType() == ExpressionType.GEQ)) {
						return false;
					}
					else {
						return other.contradicts(this);
					}
				}
			}
		}
		
		return false;
	}
	
	// This method checks whether this SOMETIMES contradicts other
	@SuppressWarnings({ "rawtypes" })
	public Boolean canContradict(Expression other) {		
		return ((contradicts(other)) || (other.type != this.type) || (!other.value.equals(this.value)));
	}
	
	@SuppressWarnings("rawtypes")
	public int overlaps(Expression other) {
		// this function returns:
		// 0 when ranges are identical (this+other)
		// 1 when this has a larger range than other (this+other, this)
		// -1 when this has a smaller range than other (this+other, other)
		// 2 when this and other overlap (this, this+other, other)
		// -2 when this and other are mutually exclusive (this, other)
		int overlaps = 0;
		
		switch(type) {
		case EQ:
		case NEQ:
			if (!contradicts(other)) 
				return 0;
			else
				return -2;
		case GEQ:
			if (contradicts(other)) {
				return -2;
			}
			else {
				if (other.type == ExpressionType.GEQ) {
					if (((Double)other.value) == ((Double)this.value))
						return 0;
					else if (((Double)other.value) > ((Double)this.value))
						return 1;
					else 
						return -1;
				}
				else if (other.type == ExpressionType.GT) {
					if (((Double)other.value) > ((Double)this.value))
						return 1;
					else 
						return -1;
				}
				else {
					return 2;
				}
			}
		case GT:
			if (contradicts(other)) {
				return -2;
			}
			else {
				if (other.type == ExpressionType.GT) {
					if (((Double)other.value) == ((Double)this.value))
						return 0;
					else if (((Double)other.value) > ((Double)this.value))
						return 1;
					else 
						return -1;
				}
				else if (other.type == ExpressionType.GEQ) {
					if (((Double)other.value) > ((Double)this.value))
						return 1;
					else 
						return -1;
				}
				else {
					return 2;
				}
			}
		case LEQ:
			if (contradicts(other)) {
				return -2;
			}
			else {
				if (other.type == ExpressionType.LEQ) {
					if (((Double)other.value) == ((Double)this.value))
						return 0;
					else if (((Double)other.value) > ((Double)this.value))
						return -1;
					else 
						return 1;
				}
				else if (other.type == ExpressionType.LT) {
					if (((Double)other.value) > ((Double)this.value))
						return -1;
					else 
						return 1;
				}
				else {
					return 2;
				}
			}
		case LT:
			if (contradicts(other)) {
				return -2;
			}
			else {
				if (other.type == ExpressionType.LT) {
					if (((Double)other.value) == ((Double)this.value))
						return 0;
					else if (((Double)other.value) > ((Double)this.value))
						return -1;
					else 
						return 1;
				}
				else if (other.type == ExpressionType.LEQ) {
					if (((Double)other.value) > ((Double)this.value))
						return -1;
					else 
						return 1;
				}
				else {
					return 2;
				}
			}
		}
		
		return overlaps;
	}
	
	@Override
	public String toString() {
		String s = name;
		
		switch (type) {
		case EQ: s += " == ";
			break;
		case GEQ: s += " >= ";
			break;
		case GT: s += " > ";
			break;
		case LEQ: s += " <= ";
			break;
		case LT: s += " < ";
			break;
		case NEQ: s += " != ";
		}
		
		s += value;
		
		return s;
	}
	
	public String getName() {
		return name;
	}
}
