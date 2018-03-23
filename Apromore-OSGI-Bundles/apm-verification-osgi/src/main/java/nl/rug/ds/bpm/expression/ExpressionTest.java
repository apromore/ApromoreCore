package nl.rug.ds.bpm.expression;

public class ExpressionTest {

	public static void main(String[] args) {
		Expression<String> e = new Expression<String>("X", ExpressionType.EQ, "Bla");
		Expression<String> e2 = new Expression<String>("X", ExpressionType.NEQ, "Bla");
		
		Expression<Double> e3 = new Expression<Double>("Y", ExpressionType.LT, 6.0);
		Expression<Double> e4 = new Expression<Double>("Y", ExpressionType.EQ, 5.0);
		Expression<Double> e5 = new Expression<Double>("Y", ExpressionType.NEQ, 4.0);
		
		System.out.println(e.accepts("Bo"));
		System.out.println(e.accepts("Bla"));
		System.out.println();
		
		System.out.println(e2.accepts("Bo"));
		System.out.println(e2.accepts("Bla"));
		System.out.println();
		
		System.out.println(e.contradicts(e3));		
		System.out.println();
		
		System.out.println(e3.getValue());
		System.out.println(e4.getValue());
		System.out.println(e3.contradicts(e4));
		System.out.println(e3.canContradict(e4));
		
		System.out.println();
		System.out.println(e3.contradicts(e5));
		System.out.println(e3.canContradict(e5));
	}

}
