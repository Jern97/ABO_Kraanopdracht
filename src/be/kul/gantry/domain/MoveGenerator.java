package be.kul.gantry.domain;

import java.util.ArrayList;
import java.util.List;

import static be.kul.gantry.domain.Problem.pickupPlaceDuration;

public class MoveGenerator {

    private static MoveGenerator moveGenerator=new MoveGenerator();
    private static List<Gantry> gantries=Problem.gantries;
    private static int safetyDistance= Problem.safetyDistance;
    List<Move> gantryMoves1;
    List<Move> gantryMoves2;

    private MoveGenerator(){
       gantryMoves1=new ArrayList<>();
       gantryMoves2=new ArrayList<>();
    }

    public static MoveGenerator getInstance(){
        return moveGenerator;
    }

    /**
     * Deze methode maakt alle moves aan voor een bepaalde kraan met een bepaald pickup en delivery slot.
     * @param g Kraan die de move doet
     * @param pickup Slot waar item ligt dat moet opgenomen worden
     * @param delivery Slot waar item moet terecht komen
     * @return een set van moves die nodig zijn om deze actie uit te voeren
     */

    public List<Move> createMoves(Gantry g, Slot pickup ,Slot delivery){

        List<Move> moves = new ArrayList<>();
        //Een basis sequentie van moves bestaat uit 4 verschillende moves:




        if(g.getId()==1){
            //De kraan bewegen naar het te verplaatsen item;
            Move naarItem=new Move(g, pickup.getCenterX(), pickup.getCenterY(), null, 0);
            moves.add(naarItem);
            gantryMoves1.add(naarItem );
            //Item oppikken in de kraan;
            Move oppikkenItem=new Move(g, g.getX(), g.getY(), pickup.getItem().getId(), pickupPlaceDuration);
            moves.add(oppikkenItem);
            gantryMoves1.add( oppikkenItem);
            //Item vervoeren naar destination;
            Move vervoerItem=new Move(g, delivery.getCenterX(), delivery.getCenterY(), pickup.getItem().getId(), 0);
            moves.add(vervoerItem);
            gantryMoves1.add( vervoerItem);
            //Item droppen op destination;
            Move dropItem=new Move(g, g.getX(), g.getY(), null, pickupPlaceDuration);
            moves.add(dropItem);
            gantryMoves1.add(dropItem);


        }
        else{
            //De kraan bewegen naar het te verplaatsen item;
            Move naarItem=new Move(g, pickup.getCenterX(), pickup.getCenterY(), null, 0);
            moves.add(naarItem);
            gantryMoves2.add(naarItem );
            //Item oppikken in de kraan;
            Move oppikkenItem=new Move(g, g.getX(), g.getY(), pickup.getItem().getId(), pickupPlaceDuration);
            moves.add(oppikkenItem);
            gantryMoves2.add( oppikkenItem);
            //Item vervoeren naar destination;
            Move vervoerItem=new Move(g, delivery.getCenterX(), delivery.getCenterY(), pickup.getItem().getId(), 0);
            moves.add(vervoerItem);
            gantryMoves2.add( vervoerItem);
            //Item droppen op destination;
            Move dropItem=new Move(g, g.getX(), g.getY(), null, pickupPlaceDuration);
            moves.add(dropItem);
            gantryMoves2.add(dropItem);


        }
        return moves;
    }

    /**
     * Deze methode controleert of een move mogelijk is
     *
     * @param g gantry
     * @param previous is completed
     * @param current to check
     * @return indien move niet mogelijk is, tijd om te wachten, anders 0
     */
    public int checkFeasible(Gantry g, Move previous, Move current){
        List<Move> otherGantryMoves;
        List<Move> thisGantryMoves;
        if(g.getId()==1) {
            otherGantryMoves= gantryMoves2;
            thisGantryMoves = gantryMoves1;
        }
        else {
            otherGantryMoves=gantryMoves1;
            thisGantryMoves = gantryMoves2;
        }

        // opvragen van alle moves die kraan 2 tussen deze tijdstippen doet
        List<Move> overlappingMoves= new ArrayList<>();
        int beginTime= (int) previous.getTime();
        int endTime= (int) current.getTime();

        int firstMoveInRange = -1;
        int lastMoveInRange = -1;
        boolean firstMoveFound = false;

        for (Move move : otherGantryMoves) {
            if(move.getTime()>=beginTime && !firstMoveFound){
                firstMoveFound = true;
                firstMoveInRange = otherGantryMoves.indexOf(move);
            }
            if(move.getTime() <= endTime){
                lastMoveInRange = otherGantryMoves.indexOf(move);
            }
        }

        int indexOfFirstMove = firstMoveInRange > 0 ? firstMoveInRange - 1 : 0;
        int indexOfLastMove = lastMoveInRange < otherGantryMoves.size()-1 ? lastMoveInRange+2 : otherGantryMoves.size();

        overlappingMoves.addAll(otherGantryMoves.subList(indexOfFirstMove, indexOfLastMove));

        // We zoeken snijpunt tussen moves van other gantry en current gantry die move wil uitvoeren
        //  (x2 - x1)
        //  -------- * t + offset = x
        //  (t2 - t1)

        double ricoA= (current.getX()-previous.getX())/(current.getTime()-previous.getTime());
        double offsetA= current.getX()-ricoA*current.getTime();


        for (int i = 1; i < overlappingMoves.size(); i++) {
            Move previousOther=overlappingMoves.get(i-1);
            Move currentOther=overlappingMoves.get(i);

            double ricoB= (currentOther.getX()-previousOther.getX())/(currentOther.getTime()-previousOther.getTime());
            double offsetB= currentOther.getX()-ricoA*currentOther.getTime();

            double tijdSnijpunt;
            if(ricoA != ricoB){
                //Arbitrair op 0 zetten
                tijdSnijpunt = 0;
            }
            else tijdSnijpunt= (offsetB - offsetA) / (ricoA - ricoB);


            if(tijdSnijpunt>previousOther.getTime() && tijdSnijpunt<currentOther.getTime()){
                //zeker niet goed
            }
            else if(tijdSnijpunt<previousOther.getTime()){
                // controleer eerste punt (previousOther)
                Move furthestMove = previous.getTime() > previousOther.getTime() ? previous : previousOther;
                double rico = furthestMove == previous ? ricoA : ricoB;
                double offset = furthestMove == previous ? offsetA : offsetB;
                double distance = furthestMove.getX() - rico*furthestMove.getTime() - offset;
                if(Math.abs(distance) >= safetyDistance){
                    //Alles in orde, we kunnen de volgende move checken
                }
                else{
                    //TODO
                    // Collision detected

                    // als currentOther move de laatste is van otherGantry, dan moeten we hieraan een move toevoegen
                    // (aan de kant zetten, zodat deze gantry zijn move kan uitvoeren)
                    if(otherGantryMoves.get(otherGantryMoves.size()-1)== currentOther){
                        int xDestination= ricoA>0 ? current.getX()+safetyDistance : current.getX()-safetyDistance;
                        otherGantryMoves.add(new Move(currentOther.getGantry(), xDestination, currentOther.getY(),currentOther.getItemInCraneID(), 0));
                    }


                    // schuif de tijd op en probeer opnieuw (recursion bitch)
                    // hoeveel tijd? => zoek het punt op de othermove die een x-waarde heeft van 20 meer dan
                    // de previous move (x) van thisGantry (de gantry die de move wil uitvoeren)
                    double time= ((previous.getX()+20)-offsetB)/ricoB;
                    double addedTime= time-previous.getTime();
                    thisGantryMoves.add(new Move(g, previous.getX(), previous.getY(), previous.getItemInCraneID(), addedTime));
                    //recursief opnieuw proberen


                }

            }
            else if(tijdSnijpunt>currentOther.getTime()){
                // controleer tweede punt (currentMove)
                Move closestMove = current.getTime() < currentOther.getTime() ? current : currentOther;
                double rico = closestMove == current ? ricoA : ricoB;
                double offset = closestMove == current ? offsetA : offsetB;
                double distance = closestMove.getX() - rico*closestMove.getTime() - offset;
                if(Math.abs(distance) >= safetyDistance){

                }
                else{
                    //TODO
                }
            }

        }

        return 0;
    }
}
