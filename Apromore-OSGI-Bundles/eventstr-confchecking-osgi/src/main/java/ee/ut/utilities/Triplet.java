package ee.ut.utilities;

public class Triplet<A,B,C> implements Comparable{
    A a;
    B b;
    C c;

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public C getC() {
        return c;
    }

    public Triplet(A a, B b, C c){
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public int compareTo(Object triplet2){
        return ((String)((Triplet<A,B,C>)triplet2).getA()).compareTo((String) this.a);
    }

    @Override
    public boolean equals(Object triplet2) {
        return ((String) ((Triplet<A, B, C>) triplet2).getA()).equals((String) this.a);
    }

    public int hashCode(){
        return ((String) (this.getA())).hashCode();
    }

    public String toString(){
        return "<" + a.toString() + ", " + b.toString() + ", " + c.toString() + ">";
    }
}
