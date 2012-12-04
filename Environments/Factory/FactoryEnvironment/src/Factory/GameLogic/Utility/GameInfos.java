package Factory.GameLogic.Utility;

import Factory.GameLogic.Enums.Direction;
import Factory.GameLogic.Enums.Faction;
import Factory.GameLogic.TransportTypes.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 28.05.12
 * Time: 11:02
 * To change this template use File | Settings | File Templates.
 */
public abstract class GameInfos {
// -------------------------- STATIC METHODS --------------------------

    public static List<TUnit> getUnitsForFaction(TGameState gameState, Faction faction) {
        List<TUnit> unitList = new LinkedList<TUnit>();
        for (TAbstractField[] fields : gameState.getMapFields()) {
            for (TAbstractField field : fields) {
                if (field.isOccupied()) {
                    if (field.getOccupant().getControllingFaction() == faction) {
                        unitList.add(field.getOccupant());
                    }
                }
            }
        }
        return unitList;
    }

    public static TFactory getFactoryForInfluenceField(TGameState gameState, TInfluenceField field) {
        int IDtoSearch = field.getFactoryID();

        for (TFactory factory : gameState.getFactories()) {
            if (factory.getFactoryID() == IDtoSearch) {
                return factory;
            }
        }

        //no factory found : ( that is not supposed to happen
        return null;
    }

    public static TFactory getFactoryForFactoryField(TGameState gameState, TFactory field) {
        int IDtoSearch = field.getFactoryID();

        for (TFactory factory : gameState.getFactories()) {
            if (factory.getFactoryID() == IDtoSearch) {
                return factory;
            }
        }

        //no factory found : ( that is not supposed to happen
        return null;
    }

    public static boolean validateUnitMovement(TGameState gameState, TUnit unit, Direction direction, TPlayer activePlayer) {
        if(unit.getControllingFaction() != activePlayer.getFaction()) {
            return false;
        }

        List<Direction> legalDirections = getLegalDirectionForUnit(gameState, unit);

        if(!legalDirections.contains(direction)) {
            return false;
        }

        return true;
    }

    public static List<Direction> getLegalDirectionForUnit(TGameState gameState, TUnit unit) {
        List<Direction> possibleDirectionList = new ArrayList<Direction>();
        possibleDirectionList.add(Direction.UP);
        possibleDirectionList.add(Direction.LEFT);
        possibleDirectionList.add(Direction.DOWN);
        possibleDirectionList.add(Direction.RIGHT);
        possibleDirectionList.add(Direction.DOWN_LEFT);
        possibleDirectionList.add(Direction.DOWN_RIGHT);
        possibleDirectionList.add(Direction.UP_LEFT);
        possibleDirectionList.add(Direction.UP_RIGHT);

        TPosition positionOfUnit = GameInfos.getPositionForUnit(gameState, unit);

        //check if Unit is controlled by the mover
        if (!(gameState.getActivePlayer().getFaction() == unit.getControllingFaction())) {
            return new ArrayList<Direction>();
        }

        //check if Unit moves over the Boarder
        if (positionOfUnit.getY() == 0) {
            possibleDirectionList.remove(Direction.UP);
            possibleDirectionList.remove(Direction.UP_LEFT);
            possibleDirectionList.remove(Direction.UP_RIGHT);
        }
        if (positionOfUnit.getY() >= gameState.getMapFields().length - 1) {
            possibleDirectionList.remove(Direction.DOWN);
            possibleDirectionList.remove(Direction.DOWN_LEFT);
            possibleDirectionList.remove(Direction.DOWN_RIGHT);
        }
        if (positionOfUnit.getX() == 0) {
            possibleDirectionList.remove(Direction.LEFT);
            possibleDirectionList.remove(Direction.UP_LEFT);
            possibleDirectionList.remove(Direction.DOWN_LEFT);
        }
        if (positionOfUnit.getX() >= gameState.getMapFields().length - 1) {
            possibleDirectionList.remove(Direction.RIGHT);
            possibleDirectionList.remove(Direction.UP_RIGHT);
            possibleDirectionList.remove(Direction.DOWN_RIGHT);
        }


        //check if the Unit moves on a Unit of the same Faction


        TAbstractField fieldUnitIsStandingOn = GameInfos.getFieldForPosition(gameState, positionOfUnit);
        Iterator<Direction> possDirectionIterator = possibleDirectionList.iterator();
        while (possDirectionIterator.hasNext()) {
            Direction direction = possDirectionIterator.next();

            TAbstractField fieldUnitIsGoingTo = GameInfos.getNeighborFieldForPosition(gameState, positionOfUnit, direction);
            if (fieldUnitIsGoingTo.isOccupied()) {
                if (fieldUnitIsGoingTo.getOccupant().getControllingFaction() == unit.getControllingFaction()) {
                    possDirectionIterator.remove();
                }
            }
        }

        //check if the Unit is allowed to take that move
        possDirectionIterator = possibleDirectionList.iterator();
        while (possDirectionIterator.hasNext()) {
            Direction direction = possDirectionIterator.next();

            TAbstractField fieldUnitIsGoingTo = GameInfos.getNeighborFieldForPosition(gameState, positionOfUnit, direction);
            if (fieldUnitIsStandingOn instanceof TInfluenceField || fieldUnitIsStandingOn instanceof TNormalField) {
                if (fieldUnitIsGoingTo instanceof TFactoryField)
                    possDirectionIterator.remove();
            }
        }

        return possibleDirectionList;
    }

    public static TPosition getPositionForUnit(TGameState gameState, TUnit unit) {
        TAbstractField[][] board = gameState.getMapFields();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[j][i].isOccupied()) {
                    if (board[j][i].getOccupant().getUnitId() == unit.getUnitId()) {
                        return new TPosition(i, j);
                    }
                }
            }
        }
        //No unit was found
        return null;
    }

    public static TAbstractField getFieldForPosition(TGameState gameState, TPosition position) {
        TAbstractField[][] board = gameState.getMapFields();
        return board[position.getY()][position.getX()];
    }

    public static TAbstractField getNeighborFieldForPosition(TGameState gameState, TPosition position, Direction direction) {
        TAbstractField[][] board = gameState.getMapFields();
        int x = position.getX();
        int y = position.getY();
        switch (direction) {
            case UP:
                return board[y - 1][x];
            case DOWN:
                return board[y + 1][x];
            case LEFT:
                return board[y][x - 1];
            case RIGHT:
                return board[y][x + 1];
            case UP_LEFT:
                return board[y - 1][x - 1];
            case UP_RIGHT:
                return board[y - 1][x + 1];
            case DOWN_LEFT:
                return board[y + 1][x - 1];
            case DOWN_RIGHT:
                return board[y + 1][x + 1];
        }
        return null;
    }

    public static boolean checkIfActivePlayerHasWon(TGameState gameState) {
        HashMap<Faction, List<TUnit>> unitsForFaction = getUnitsForAllFactions(gameState);

        if(!unitsForFaction.containsKey(gameState.getActivePlayer().getFaction())) {
            return false;
        }

        if(unitsForFaction.keySet().size() > 1) {
            return false;
        }

        return true;
    }

    public static HashMap<Faction, List<TUnit>> getUnitsForAllFactions(TGameState gameState) {
        HashMap<Faction, List<TUnit>> unitList = new HashMap<Faction, List<TUnit>>();
        for (TAbstractField[] fields : gameState.getMapFields()) {
            for (TAbstractField field : fields) {
                if (field.isOccupied()) {
                    if (unitList.containsKey(field.getOccupant().getControllingFaction())) {
                        unitList.get(field.getOccupant().getControllingFaction()).add(field.getOccupant());
                    } else {
                        List<TUnit> tmpList = new LinkedList<TUnit>();

                        tmpList.add(field.getOccupant());

                        unitList.put(field.getOccupant().getControllingFaction(), tmpList);
                    }
                }
            }
        }

        return unitList;
    }
}
