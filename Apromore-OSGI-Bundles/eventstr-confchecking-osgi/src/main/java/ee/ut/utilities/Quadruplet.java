package ee.ut.utilities;

public class Quadruplet<A,B,C,D> implements Comparable{
    A a;
    B b;
    C c;
    D d;

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public C getC() {
        return c;
    }

    public D getD() {
        return d;
    }

    public Quadruplet(A a, B b, C c, D d){
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public int compareTo(Object triplet2){
        return ((String)((Quadruplet<A, B, C, D>)triplet2).getA()).compareTo((String) this.a);
    }

    @Override
    public boolean equals(Object triplet2) {
        return ((String) ((Quadruplet<A, B, C, D>) triplet2).getA()).equals((String) this.a);
    }

    public int hashCode(){
        return ((String) (this.getA())).hashCode();
    }

    public String toString(){
        return "<" + a.toString() + ", " + b.toString() + ", " + c.toString() + ", " + d.toString() + ">";
    }
}
