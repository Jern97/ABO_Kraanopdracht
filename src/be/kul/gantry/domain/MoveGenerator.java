package be.kul.gantry.domain;

import java.util.ArrayList;
import java.util.List;

import static be.kul.gantry.domain.Problem.pickupPlaceDuration;

public class MoveGenerator {

    private static MoveGenerator moveGenerator = new MoveGenerator();
    private static List<Gantry> gantries = Problem.gantries;
    private static int safetyDistance = Problem.safetyDistance;
    List<Move> gantry0Moves;
    List<Move> gantry1Moves;

    private MoveGenerator() {
        gantry0Moves = new ArrayList<>();
        gantry1Moves = new ArrayList<>();
        Gantry gantry0 = Problem.gantries.get(0);
        Gantry gantry1 = Problem.gantries.get(1);
        gantry0Moves.add(new Move(gantry0, gantry0.getX(), gantry0.getY(), null, 0, false));
        gantry0Moves.add(new Move(gantry0, gantry0.getX(), gantry0.getY(), null, 0, false));
        gantry1Moves.add(new Move(gantry1, gantry1.getX(), gantry1.getY(), null, 0, false));
        gantry1Moves.add(new Move(gantry1, gantry1.getX(), gantry1.getY(), null, 0, false));

    }

    public static MoveGenerator getInstance() {
        return moveGenerator;
    }

    /**
     * Deze methode maakt alle moves aan voor een bepaalde kraan met een bepaald pickup en delivery slot.
     *
     * @param g        Kraan die de move doet
     * @param pickup   Slot waar item ligt dat moet opgenomen worden
     * @param delivery Slot waar item moet terecht komen
     * @return een set van moves die nodig zijn om deze actie uit te voeren
     */

    public List<Move> createMoves(Gantry g, Slot pickup, Slot delivery) {

        List<Move> moves = new ArrayList<>();
        //Een basis sequentie van moves bestaat uit 4 verschillende moves:
        List<Move> otherGantryMoves;
        List<Move> thisGantryMoves;
        if (g.getId() == 0) {
            thisGantryMoves = gantry0Moves;
        } else {
            thisGantryMoves = gantry1Moves;
        }


        Move naarItem = new Move(g, pickup.getCenterX(), pickup.getCenterY(), null, 0, false);
        makeFeasible(g, thisGantryMoves.get(thisGantryMoves.size() - 1), naarItem);
        naarItem = new Move(g, pickup.getCenterX(), pickup.getCenterY(), null, 0, true);
        moves.add(naarItem);
        thisGantryMoves.add(naarItem);

        Move oppikkenItem = new Move(g, g.getX(), g.getY(), pickup.getItem().getId(), pickupPlaceDuration, true);
        moves.add(oppikkenItem);
        thisGantryMoves.add(oppikkenItem);


        Move vervoerItem = new Move(g, delivery.getCenterX(), delivery.getCenterY(), pickup.getItem().getId(), 0, false);
        makeFeasible(g, thisGantryMoves.get(thisGantryMoves.size() - 1), vervoerItem);
        vervoerItem = new Move(g, delivery.getCenterX(), delivery.getCenterY(), pickup.getItem().getId(), 0, true);
        moves.add(vervoerItem);
        thisGantryMoves.add(vervoerItem);

        Move dropItem = new Move(g, g.getX(), g.getY(), null, pickupPlaceDuration, true);
        moves.add(dropItem);
        thisGantryMoves.add(dropItem);


        return moves;
    }

    /**
     * Toevoegen van moves aan beide gantries om te garanderen dat bij toevoegen van current
     * geen collision optreedt
     *
     * @param g        kraan waarop current move uitgevoerd moet worden
     * @param previous laatste move die al toegevoegd is
     * @param current  move die we willen mogelijk maken
     */
    public void makeFeasible(Gantry g, Move previous, Move current) {
        List<Move> otherGantryMoves;
        List<Move> thisGantryMoves;
        if (g.getId() == 0) {
            otherGantryMoves = gantry1Moves;
            thisGantryMoves = gantry0Moves;
        } else {
            otherGantryMoves = gantry0Moves;
            thisGantryMoves = gantry1Moves;
        }

        // opvragen van alle moves die kraan 2 tussen deze tijdstippen doet
        List<Move> overlappingMoves = new ArrayList<>();
        int beginTime = (int) previous.getTime();
        int endTime = (int) current.getTime();

        int firstMoveInRange = -1;
        int lastMoveInRange = -1;
        boolean firstMoveFound = false;

        for (Move move : otherGantryMoves) {
            if(move.getTime() >= beginTime && move.getTime() <= endTime) {
                if (!firstMoveFound) {
                    firstMoveFound = true;
                    firstMoveInRange = otherGantryMoves.indexOf(move);
                }
                lastMoveInRange = otherGantryMoves.indexOf(move);
            }
        }
        //Indien er wel moves overlappen:
        if(firstMoveInRange != -1 && lastMoveInRange != -1) {

            int indexOfFirstMove = firstMoveInRange > 0 ? firstMoveInRange - 1 : 0;
            int indexOfLastMove = lastMoveInRange < otherGantryMoves.size() - 1 ? lastMoveInRange + 2 : otherGantryMoves.size();

            overlappingMoves.addAll(otherGantryMoves.subList(indexOfFirstMove, indexOfLastMove));

        }
        // We zoeken snijpunt tussen moves van other gantry en current gantry die move wil uitvoeren
        //  (x2 - x1)
        //  -------- * t + offset = x
        //  (t2 - t1)

        double ricoA = (current.getX() - previous.getX()) / (current.getTime() - previous.getTime());
        double offsetA = current.getX() - ricoA * current.getTime();


        for (int i = 1; i < overlappingMoves.size(); i++) {
            Move previousOther = overlappingMoves.get(i - 1);
            Move currentOther = overlappingMoves.get(i);

            double denominator = (currentOther.getTime() - previousOther.getTime());
            double ricoB = denominator == 0 ? 0 : (currentOther.getX() - previousOther.getX()) / denominator;
            double offsetB = currentOther.getX() - ricoB * currentOther.getTime();

            double tijdSnijpunt;
            //evenwijdig
            if (ricoA == ricoB) {
                //Arbitrair op 0 zetten
                tijdSnijpunt = 0;
            } else tijdSnijpunt = (offsetB - offsetA) / (ricoA - ricoB);

            Move closestCurrent = current.getTime() < currentOther.getTime() ? current : currentOther;
            Move furthestPrevious = previous.getTime() > previousOther.getTime() ? previous : previousOther;
            //snijpunt ligt binnen de grenzen van other move
            if (tijdSnijpunt > furthestPrevious.getTime() && tijdSnijpunt < closestCurrent.getTime()) {
                // TODO Collision detected

                // zoek punt vanaf collision tot einde van currentother waar distance > safety, en anders gewoon op current other
                double timePlus = ((previous.getX() + safetyDistance) - offsetB) / ricoB;
                double timeMin = ((previous.getX() - safetyDistance) - offsetB) / ricoB;

                double time = Math.max(timeMin, timePlus);

                //Als ricoB 0 is dan snijden we het nooit
                double addedTime = time < closestCurrent.getTime() && ricoB != 0 ? time - previous.getTime() : currentOther.getTime() - previous.getTime();
                if(addedTime < 0){
                    System.out.println("stop");
                }

                Move waiting = new Move(g, previous.getX(), previous.getY(), previous.getItemInCraneID(), addedTime, true);
                thisGantryMoves.add(waiting);
                Move updatedMove = new Move(current);

                makeFeasible(g, waiting, updatedMove);

            }
            // snijpunt valt voor grenzen van other move
            else if (tijdSnijpunt < previousOther.getTime()) {
                // we zoeken het beginpunt die het verst in tijd gelegen: van othermove OF van thismove
                //rico en offset is van de move die we willen doen
                double rico = furthestPrevious == previous ? ricoB : ricoA;
                double offset = furthestPrevious == previous ? offsetB : offsetA;
                //distance tussen move die we willen aanmaken en othermove
                double distance = furthestPrevious.getX() - rico * furthestPrevious.getTime() - offset;
                if (Math.abs(distance) >= safetyDistance) {
                    //Alles in orde, we kunnen de volgende move checken
                    continue;
                } else {
                    //TODO Collision detected

                    // als currentOther move de laatste is van otherGantry, dan moeten we hieraan een move toevoegen
                    // (aan de kant zetten, zodat deze gantry zijn move kan uitvoeren)
                    if (otherGantryMoves.get(otherGantryMoves.size() - 1) == currentOther) {
                        int xDestination = ricoA > 0 ? current.getX() + safetyDistance : current.getX() - safetyDistance;
                        otherGantryMoves.add(new Move(currentOther.getGantry(), xDestination, currentOther.getY(), currentOther.getItemInCraneID(), 0, true));

                        makeFeasible(g, previous, current);

                    } else {
                        // schuif de tijd op en probeer opnieuw (recursion bitch)
                        // hoeveel tijd? => zoek het punt op de othermove die een x-waarde heeft van 20 meer dan
                        // de previous move (x) van thisGantry (de gantry die de move wil uitvoeren)
                        double timePlus = ((previous.getX() + safetyDistance) - offsetB) / ricoB;
                        double timeMin = ((previous.getX() - safetyDistance) - offsetB) / ricoB;

                        double time = Math.max(timeMin, timePlus);

                        // ligt tijd tussen "nu" en einde van de othermove? => ja = goed, nee: tijd gelijk aan einde van othermove
                        //Als ricoB 0 is dan snijden we het nooit
                        double addedTime = time > furthestPrevious.getTime() && time < currentOther.getTime() && ricoB != 0? time - previous.getTime() : currentOther.getTime() - previous.getTime();

                        if(addedTime < 0){
                            System.out.println("stop");
                        }
                        Move waiting = new Move(g, previous.getX(), previous.getY(), previous.getItemInCraneID(), addedTime, true);
                        thisGantryMoves.add(waiting);
                        Move updatedMove = new Move(current);


                        makeFeasible(g, waiting, updatedMove);
                    }

                    break;


                }

            } else if (tijdSnijpunt > currentOther.getTime()) {
                // controleer tweede punt (currentMove)
                double rico = closestCurrent == current ? ricoB : ricoA;
                double offset = closestCurrent == current ? offsetB : offsetA;
                double distance = closestCurrent.getX() - rico * closestCurrent.getTime() - offset;
                if (Math.abs(distance) >= safetyDistance) {
                    continue;
                } else {
                    //TODO Collision detected

                    if (otherGantryMoves.get(otherGantryMoves.size() - 1) == currentOther) {
                        int xDestination = ricoA > 0 ? current.getX() + safetyDistance : current.getX() - safetyDistance;
                        otherGantryMoves.add(new Move(currentOther.getGantry(), xDestination, currentOther.getY(), currentOther.getItemInCraneID(), 0, true));

                        makeFeasible(g, previous, current);
                    }
                    // niet de laatste move
                    else {
                        //zoek de tijd waarbij distance van closestmove tot andere move > safety
                        double addedTime = currentOther.getTime() - previous.getTime();
                        if(addedTime < 0){
                            System.out.println("stop");
                        }

                        Move waiting = new Move(g, previous.getX(), previous.getY(), previous.getItemInCraneID(), addedTime, true);
                        thisGantryMoves.add(waiting);
                        Move updatedMove = new Move(current);


                        makeFeasible(g, waiting, updatedMove);

                    }


                    break;
                }
            }

        }

    }
}
