package be.kul.gantry.domain;



/**
 * Created by Wim on 12/05/2015.
 */
public class Item {

    private final int id;
    // je mag pas dit item vastpakken als tijd van de kraan + move ernaar toe verder in de tijd is dan deze timestamp
    private double timestamp;

    public Item(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


}
