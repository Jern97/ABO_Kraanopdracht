package be.kul.gantry.domain;

//Wrapper class voor een 2D key voor hashmap
public class Coordinaat {
    int x;
    int y;

    public Coordinaat(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //2 methoden die nodig zijn om deze klasse als key te kunnen gebruiken

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinaat)) return false;
        Coordinaat coordinaat = (Coordinaat) o;
        return x == coordinaat.x && y == coordinaat.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
