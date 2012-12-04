package Factory.GameLogic;

import Factory.GameLogic.Enums.Direction;
import Factory.GameLogic.Enums.Faction;
import Factory.GameLogic.GameActors.*;
import Factory.GameLogic.TransportTypes.TPosition;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 22.05.12
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */
public class RuleBook implements java.io.Serializable {
// -------------------------- OTHER METHODS --------------------------

    public boolean validateEndTurn(Game game) {
        //there is currently no rule involving endTurn Actions
        return true;
    }

    public boolean validateMoveUnit(Faction mover, Unit unit, Direction direction, Game game) {
        TPosition positionOfUnit = game.getPositionForUnit(unit);

        //check if Unit is controlled by the mover
        if (!(mover == unit.getFaction())) {
            return false;
        }


        if (game.getTurn() == unit.getExhaustedForTurn()) {
            return false;
        }


        //check if Unit moves over the Boarder
        if (positionOfUnit.getY() == 0) {
            if (direction == Direction.UP || direction == Direction.UP_LEFT || direction == Direction.UP_RIGHT) {
                return false;
            }
        }
        if (positionOfUnit.getY() >= game.getMapMetaData().getEdgeLength() - 1) {
            if (direction == Direction.DOWN || direction == Direction.DOWN_LEFT || direction == Direction.DOWN_RIGHT) {
                return false;
            }
        }
        if (positionOfUnit.getX() == 0) {
            if (direction == Direction.LEFT || direction == Direction.DOWN_LEFT || direction == Direction.UP_LEFT) {
                return false;
            }
        }
        if (positionOfUnit.getX() >= game.getMapMetaData().getEdgeLength() - 1) {
            if (direction == Direction.RIGHT || direction == Direction.UP_RIGHT || direction == Direction.DOWN_RIGHT) {
                return false;
            }
        }


        //check if the Unit moves on a Unit of the same Faction
        AbstractField fieldUnitIsStandingOn = game.getFieldForPosition(positionOfUnit);
        AbstractField fieldUnitIsGoingTo = game.getNeighborFieldForPosition(positionOfUnit, direction);
        if (fieldUnitIsGoingTo.isOccupied()) {
            if (fieldUnitIsGoingTo.getOccupant().getFaction() == unit.getFaction()) {
                return false;
            }
        }


        //check if the Unit is allowed to take that move
        if (fieldUnitIsStandingOn instanceof NormalField || fieldUnitIsGoingTo instanceof InfluenceField) {
            if (fieldUnitIsGoingTo instanceof FactoryField) {
                return false;
            }
        }


        return true;
    }

    public Player validateWinner(Game game) {
        Player potentialWinner = game.getActivePlayer();
        Faction potentialWinningFaction = potentialWinner.getFaction();
        List<Factory> factoryList = game.getFactories();
        AbstractField[][] board = game.getBoard();

        //only the active player can win

        //check if there is a unit the active player does not control
        for (AbstractField[] fields : board) {
            for (AbstractField field : fields) {
                if (field.isOccupied()) {
                    if (field.getOccupant().getFaction() != potentialWinningFaction) {
                        return null;
                    }
                }
            }
        }

        // THIS WINNING CONDITION HAS BEEN REMOVED. THE ACTIVE PLAYER NOW ONLY NEEDS TO KILL ALL ENEMY UNITS.
        //check if there is a factory that is under control of an opponent
        //for (Factory factory : factoryList) {
        //    if (factory.getController() != potentialWinningFaction && factory.getController() != Faction.NEUTRAL)
        //        return null;
        //}

        return game.getActivePlayer();
    }
}
