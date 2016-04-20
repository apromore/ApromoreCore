package au.edu.qut.structuring.graph;

/**
 * Created by Adriano on 29/02/2016.
 */
public class Move {

    public enum MoveType {PULLUP, PUSHDOWN}

    private int cost;
    private MoveType type;
    private int toExtend;
    private int extension;

    public Move(int toExtend, int extension, MoveType type) {
        this.toExtend = toExtend;
        this.extension = extension;
        this.type = type;
        cost = Integer.MAX_VALUE;
    }

    public void setCost(int cost) { this.cost = cost; }

    public int getCost() { return cost; }
    public MoveType getType() { return type; }

    public int getExtension() { return extension; }
    public int getToExtend() { return toExtend; }


}
