package be.kul.gantry.domain;

import java.util.Objects;

/**
 * Created by Wim on 27/04/2015.
 */
public class Gantry {

    private final int id;
    private final int xMin,xMax;
    private final int startX,startY;
    private final double xSpeed,ySpeed;

    private boolean retired=false;

    private int x,y;
    private double time;

    public Gantry(int id,
                  int xMin, int xMax,
                  int startX, int startY,
                  double xSpeed, double ySpeed) {
        this.id = id;
        this.xMin = xMin;
        this.xMax = xMax;
        this.startX = startX;
        this.startY = startY;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.x = startX;
        this.y = startY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gantry gantry = (Gantry) o;
        return id == gantry.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public int getId() {
        return id;
    }

    public int getXMax() {
        return xMax;
    }

    public int getXMin() {
        return xMin;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public double getXSpeed() {
        return xSpeed;
    }

    public double getYSpeed() {
        return ySpeed;
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

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }


    public boolean overlapsGantryArea(Gantry g) {
        return g.xMin < xMax && xMin < g.xMax;
    }

    public int[] getOverlapArea(Gantry g) {

        int maxmin = Math.max(xMin, g.xMin);
        int minmax = Math.min(xMax, g.xMax);

        if (minmax < maxmin)
            return null;
        else
            return new int[]{maxmin, minmax};
    }

    public boolean canReachSlot(Slot s) {
        return xMin <= s.getCenterX() && s.getCenterX() <= xMax;
    }

    public boolean isRetired() {
        return retired;
    }

    public void setRetired(boolean retired) {
        this.retired = retired;
    }
}
