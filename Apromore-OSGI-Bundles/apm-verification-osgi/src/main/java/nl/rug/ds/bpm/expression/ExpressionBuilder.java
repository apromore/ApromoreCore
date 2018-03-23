package nl.rug.ds.bpm.expression;

public class ExpressionBuilder {
	
	public static Expression<?> parseExpression(String expression) {
		String operator = getOperator(expression);
		String name = expression.substring(0, expression.indexOf(operator)).trim();
		return parseExpression(name, expression);
	}
	
	public static Expression<?> parseExpression(String name, String expression) {		
		String operator;
		ExpressionType et;
		Expression<?> exp;

		expression = expression.replace(name, "").trim();
		operator = getOperator(expression);

		switch (operator) {
		case "==":
			et = ExpressionType.EQ; break;
		case "!=":
			et = ExpressionType.NEQ; break;
		case ">":
			et = ExpressionType.GT; break;
		case ">=":
			et = ExpressionType.GEQ; break;
		case "<":
			et = ExpressionType.LT; break;
		case "<=":
			et = ExpressionType.LEQ; break;
		default:
			et = ExpressionType.NEQ;
		}
		
		expression = expression.replace(operator, "").trim();
		
		if (isNumeric(expression)) {
			exp = new Expression<Double>(name, et, Double.parseDouble(expression));
		}
		else {
			exp = new Expression<String>(name, et, expression);
		}
		
		return exp;
	}
	
	private static String getOperator(String expression) {
		if (expression.contains("==")) return "==";
		if (expression.contains("!=")) return "!=";
		if (expression.contains(">=")) return ">=";
		if (expression.contains(">")) return ">";
		if (expression.contains("<=")) return "<=";
		if (expression.contains("<")) return "<";
		
		return "";
	}
	
	private static boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
}
