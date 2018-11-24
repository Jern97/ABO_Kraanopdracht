package be.kul.gantry.domain;

public class Move {

    private Gantry gantry;
    private double time;
    private int x;
    private int y;
    private Integer itemInCraneID;

    public Move(Gantry gantry, int x_destination, int y_destination, Integer itemInCraneID, double additionalTime, boolean updateGantry) {
        this.gantry = gantry;

        //de totale tijd hiervoor nodig hangt af van de langst durende beweging (X of Y)
        this.time = gantry.getTime() + additionalTime + Math.max(Math.abs(gantry.getX()-x_destination)/ gantry.getXSpeed(),Math.abs(gantry.getY()-y_destination)/ gantry.getYSpeed());
        this.x = x_destination;
        this.y = y_destination;
        this.itemInCraneID = itemInCraneID;

        if(updateGantry) {
            //Kraan updaten;
            gantry.setTime(time);
            gantry.setX(x);
            gantry.setY(y);
        }
    }

    public String toString(){
        return gantry.getId()+";"+time+";"+x+";"+y+";"+itemInCraneID;
    }

    public Gantry getGantry() {
        return gantry;
    }

    public void setGantry(Gantry gantry) {
        this.gantry = gantry;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Integer getItemInCraneID() {
        return itemInCraneID;
    }

    public void setItemInCraneID(Integer itemInCraneID) {
        this.itemInCraneID = itemInCraneID;
    }
}
