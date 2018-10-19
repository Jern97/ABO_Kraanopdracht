package be.kul.gantry.domain;

public class Move {

    private int gID;
    private double time;
    private int x;
    private int y;
    private Integer itemInCraneID;

    public Move(Gantry g, int x_destination, int y_destination, Integer itemInCraneID, double additionalTime) {
        this.gID = g.getId();

        //de totale tijd hiervoor nodig hangt af van de langst durende beweging (X of Y)
        this.time = g.getTime() + additionalTime + Math.max(Math.abs(g.getX()-x_destination)/g.getXSpeed(),Math.abs(g.getY()-y_destination)/g.getYSpeed());
        this.x = x_destination;
        this.y = y_destination;
        this.itemInCraneID = itemInCraneID;

        //Kraan updaten;
        g.setTime(time);
        g.setX(x);
        g.setY(y);
    }
}
