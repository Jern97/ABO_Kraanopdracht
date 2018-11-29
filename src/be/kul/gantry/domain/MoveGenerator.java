package be.kul.gantry.domain;

import java.util.ArrayList;
import java.util.List;

import static be.kul.gantry.domain.Problem.pickupPlaceDuration;

/**
 * in de MoveGenerator wordt voor elk van de kranen een lijst van moves bijgehouden
 * de makeFeasible methode baseert zich hierop om moves mogelijk te maken
 */
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

        // we maken voor elke kraan 2 dummy moves om de makeFeasible te laten werken voor de eerste moves
        gantry0Moves.add(new Move(gantry0, gantry0.getX(), gantry0.getY(), null, -1, false));
        gantry0Moves.add(new Move(gantry0, gantry0.getX(), gantry0.getY(), null, 0, false));
        gantry1Moves.add(new Move(gantry1, gantry1.getX(), gantry1.getY(), null, -1, false));
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

        // dit is de lijst van moves die nodig is om met gantry g het item in slot "pickup" naar slot "delivery" te brengen
        List<Move> moves = new ArrayList<>();

        // extra info over de kranen
        List<Move> thisGantryMoves;
        if (g.getId() == 0) {
            thisGantryMoves = gantry0Moves;
        } else {
            thisGantryMoves = gantry1Moves;
        }

        //Een basis sequentie van moves bestaat uit 4 verschillende moves:
        Move naarItem = new Move(g, pickup.getCenterX(), pickup.getCenterY(), null, 0, false);
        makeFeasible(g, thisGantryMoves.get(thisGantryMoves.size() - 1), naarItem);
        naarItem = new Move(g, pickup.getCenterX(), pickup.getCenterY(), null, 0, true);
        moves.add(naarItem);
        thisGantryMoves.add(naarItem);

        Move oppikkenItem = new Move(g, g.getX(), g.getY(), pickup.getItem().getId(), pickupPlaceDuration, false);
        makeFeasible(g, thisGantryMoves.get(thisGantryMoves.size() - 1), oppikkenItem);
        oppikkenItem = new Move(g, g.getX(), g.getY(), pickup.getItem().getId(), pickupPlaceDuration, true);
        moves.add(oppikkenItem);
        thisGantryMoves.add(oppikkenItem);


        Move vervoerItem = new Move(g, delivery.getCenterX(), delivery.getCenterY(), pickup.getItem().getId(), 0, false);
        makeFeasible(g, thisGantryMoves.get(thisGantryMoves.size() - 1), vervoerItem);
        vervoerItem = new Move(g, delivery.getCenterX(), delivery.getCenterY(), pickup.getItem().getId(), 0, true);
        moves.add(vervoerItem);
        thisGantryMoves.add(vervoerItem);

        Move dropItem = new Move(g, g.getX(), g.getY(), null, pickupPlaceDuration, false);
        makeFeasible(g, thisGantryMoves.get(thisGantryMoves.size() - 1), dropItem);
        dropItem = new Move(g, g.getX(), g.getY(), null, pickupPlaceDuration, true);
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
        // basis informatie over gantries
        List<Move> otherGantryMoves;
        List<Move> thisGantryMoves;
        Gantry otherGantry;
        if (g.getId() == 0) {
            otherGantryMoves = gantry1Moves;
            thisGantryMoves = gantry0Moves;
            otherGantry= gantries.get(1);
        } else {
            otherGantryMoves = gantry0Moves;
            thisGantryMoves = gantry1Moves;
            otherGantry = gantries.get(0);
        }

        // opvragen van alle moves die kraan 2 tussen deze tijdstippen doet

        List<Move> overlappingMoves = getOverlappingMoves(otherGantry, previous.getTime(), current.getTime());
        // We zoeken snijpunt tussen moves van other gantry en current gantry die move wil uitvoeren
        //  (x2 - x1)
        //  -------- * t + offset = x
        //  (t2 - t1)

        double ricoA = (current.getX() - previous.getX()) / (current.getTime() - previous.getTime());
        //double offsetA = current.getX() - ricoA * current.getTime();


        for (int i = 1; i < overlappingMoves.size(); i++) {
            Move previousOther = overlappingMoves.get(i - 1);
            Move currentOther = overlappingMoves.get(i);

            double addedTime = calculateDelay(current, previous, currentOther, previousOther);

            if(addedTime != 0 && currentOther == otherGantryMoves.get(otherGantryMoves.size()-1)){
                //We kunnen de bestemming niet bereiken zonder de andere kraan te bewegen, we moeten hem verzetten
                int xDestination = ricoA > 0 ? current.getX() + safetyDistance : current.getX() - safetyDistance;
                otherGantryMoves.add(new Move(currentOther.getGantry(), xDestination, currentOther.getY(), currentOther.getItemInCraneID(), 0, true));
                //opnieuw proberen
                makeFeasible(g, previous, current);
                break;
            }
            //Er moet gewacht worden, omdat er anders een collision is
            else if(addedTime != 0){
                //Als we moeten wachten is het belangrijk om te checken als de andere kraan ons niet kruist tijdens het wachten
                double extremeOfOtherGantry = calculateExtreme(otherGantry, previous.getTime(), previous.getTime()+addedTime);
                if(extremeOfOtherGantry == Double.NEGATIVE_INFINITY){
                    System.out.println("stop");
                }
                if(g.getId() == 0 && extremeOfOtherGantry < previous.getX() ){
                    //Kraan 0 zal kraan 1 kruisen indien hij wacht
                    Move dodge = new Move(g, (int) extremeOfOtherGantry+safetyDistance, previous.getY(), previous.getItemInCraneID(), 0, true);
                    thisGantryMoves.add(dodge);
                    Move updatedMove = new Move(current);
                    makeFeasible(g, dodge, updatedMove);
                }
                if(g.getId() == 1 && extremeOfOtherGantry > previous.getX()){
                    //Kraan 1 zal kraan 0 kruisen indien hij wacht
                    Move dodge = new Move(g, (int) extremeOfOtherGantry+safetyDistance, previous.getY(), previous.getItemInCraneID(), 0, true);
                    thisGantryMoves.add(dodge);
                    Move updatedMove = new Move(current);
                    makeFeasible(g, dodge, updatedMove);
                }
                else {
                    //Wacht move toevoegen
                    Move waiting = new Move(g, previous.getX(), previous.getY(), previous.getItemInCraneID(), addedTime, true);
                    thisGantryMoves.add(waiting);
                    //Current move updaten naar de nieuwe tijd
                    Move updatedMove = new Move(current);
                    //Opnieuw proberen
                    makeFeasible(g, waiting, updatedMove);
                }
                break;
            }
        }

    }

    public double calculateExtreme(Gantry g, double startTime, double endTime){
        List<Move> overlappingMoves = getOverlappingMoves(g, startTime, endTime);
        int extreme;

        if(g.getId() == 0) {
            //Voor kraan 0 is het extrema het maximum
            extreme = Integer.MIN_VALUE;
            for (Move m : overlappingMoves) {
                if(m.getX() > extreme) extreme = m.getX();
            }
        }
        else{
            //Voor kraan 1 is het extrema het minimum
            extreme = Integer.MAX_VALUE;
            for(Move m : overlappingMoves){
                if(m.getX() < extreme) extreme = m.getX();
            }
        }
        return extreme;

    }

    public List<Move> getOverlappingMoves(Gantry g, double beginTime, double endTime){
        List<Move> gantryMoves;
        if (g.getId() == 0) {
            gantryMoves = gantry0Moves;
        } else {
            gantryMoves = gantry1Moves;
        }

        List<Move> overlappingMoves = new ArrayList<>();

        int firstMoveInRange = -1;
        int lastMoveInRange = -1;
        boolean firstMoveFound = false;

        for (Move move : gantryMoves) {
            // vanaf er een move gevonden is die verder in tijd is gelegen dan de begintijd is de eerste move geinitialiseerd
            if(move.getTime() >= beginTime){
                if (!firstMoveFound) {
                    firstMoveFound = true;
                    firstMoveInRange = gantryMoves.indexOf(move);
                    lastMoveInRange = gantryMoves.indexOf(move);
                }
            }
            // als de eerste move gevonden is, gaan we verder in de for loop, en zal de laatste move upgedate worden tot
            // de move uit de lijst van de other Gantry verder ligt dan de eindtijd
            if(firstMoveFound && move.getTime() <= endTime) {
                lastMoveInRange = gantryMoves.indexOf(move);
            }
        }
        //Indien er moves overlappen:
        if(firstMoveInRange != -1 && lastMoveInRange != -1) {

            // Voor de eerste move nemen we 1 move vroeger dan de bekomen index hierboven aangezien we het verloop van de move willen kennen
            // Voor de laatste move willen we een verder nemen (+2 omdat sublist upper bound exlusive is)
            int indexOfFirstMove = firstMoveInRange > 0 ? firstMoveInRange - 1 : 0;
            int indexOfLastMove = lastMoveInRange < gantryMoves.size() - 1 ? lastMoveInRange + 2 : gantryMoves.size();

            overlappingMoves.addAll(gantryMoves.subList(indexOfFirstMove, indexOfLastMove));

        }

        return overlappingMoves;
    }

    public double calculateDelay(Move current, Move previous, Move currentOther, Move previousOther) {
        //Deze boolean geeft aan als de "other" kraan op dit moment zich boven of onder this kraan bevindt
        boolean otherAbove = previous.getX() < previousOther.getX();

        double ricoA = (current.getX() - previous.getX()) / (current.getTime() - previous.getTime());
        double offsetA = current.getX() - ricoA * current.getTime();

        double ricoB = (currentOther.getX() - previousOther.getX()) / (currentOther.getTime() - previousOther.getTime());
        double offsetB = currentOther.getX() - ricoB * currentOther.getTime();

        double tijdSnijpunt = (offsetB - offsetA) / (ricoA - ricoB);

        Move closestCurrent = current.getTime() < currentOther.getTime() ? current : currentOther;
        Move furthestPrevious = previous.getTime() > previousOther.getTime() ? previous : previousOther;

        //Evenwijdig aan elkaar
        if(Double.isNaN(tijdSnijpunt)){
            //Eigenschap van evenwijdigheid: de afstand blijft gelijk. Dus als het begin punt feasible is,
            //dan blijven ze altijd met dezelfde afstand van elkaar gescheiden
            return 0;
        }

        //Snijpunt binnen de grenzen (closestCurrent en furthestPrevious)
        else if (tijdSnijpunt > furthestPrevious.getTime() && tijdSnijpunt < closestCurrent.getTime()) {
            double additionalTime;
            if(otherAbove){
                additionalTime = -(((currentOther.getX() - safetyDistance - offsetA) / ricoA)-currentOther.getTime());
            }
            else{
                additionalTime = -(((currentOther.getX() + safetyDistance - offsetA) / ricoA)-currentOther.getTime());
            }
            if(additionalTime == Double.NEGATIVE_INFINITY){
                System.out.println("stop");
            }
            return additionalTime;
        }

        //Snijpunt voor de grenzen
        else if (tijdSnijpunt <= furthestPrevious.getTime()) {
            //De 2 kranen bewegen weg van elkaar, geen probleem dus:
            return 0;

        }

        //Snijpunt na de grenzen
        else if (tijdSnijpunt >= closestCurrent.getTime()) {
            double rico = closestCurrent == current ? ricoB : ricoA;
            double offset = closestCurrent == current ? offsetB : offsetA;
            double distance = Math.abs(closestCurrent.getX() - (rico * closestCurrent.getTime() + offset));

            if(distance < safetyDistance){
                //Als dit de laatste move is van de andere kraan:
                double additionalTime;
                if(otherAbove){
                    additionalTime = -(((currentOther.getX() - safetyDistance - offsetA) / ricoA)-currentOther.getTime());
                }
                else{
                    additionalTime = -(((currentOther.getX() + safetyDistance - offsetA) / ricoA)-currentOther.getTime());
                }
                if(additionalTime == Double.NEGATIVE_INFINITY){
                    System.out.println("stop");
                }
                return additionalTime;
            }
            return 0;
        }

        //zou normaal niet mogen voorkomen
        return -1;
    }
}
