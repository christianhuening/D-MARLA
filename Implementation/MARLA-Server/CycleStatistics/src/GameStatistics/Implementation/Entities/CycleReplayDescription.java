package GameStatistics.Implementation.Entities;

import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import Interfaces.IHasTransportType;
import TransportTypes.TCycleReplayDescription;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 07.06.12
 * Time: 15:24
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Access(AccessType.FIELD)
public class CycleReplayDescription implements IHasTransportType<TCycleReplayDescription>  {
// ------------------------------ FIELDS ------------------------------

    @javax.persistence.Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public long Id;

    //@Column
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime replayDate;

    public DateTime getReplayDate() {
        return replayDate;
    }

    private UUID replayID;

    public UUID getReplayID() {
        return replayID;
    }

    @ElementCollection
    private List<String> players;

    public List<String> getPlayers() {
        return players;
    }

    private String winningPlayer;

    public String getWinningPlayer() {
        return winningPlayer;
    }

    private int numberOfTurns;

    public int getNumberOfTurns() {
        return numberOfTurns;
    }

    private String gameReplayFileLocation;

    public String getGameReplayFileLocation() {
        return gameReplayFileLocation;
    }

    private String environmentDescription;

    public String getEnvironmentDescription() {
        return environmentDescription;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    // NHibernateConstructor
    public CycleReplayDescription() {

    }

   /* public CycleReplayDescription(DateTime dateTime, List<String> playersInReplay, String winningPlayer, int numberOfTurns) {
        this(dateTime, playersInReplay, winningPlayer, numberOfTurns, null);
    }*/

    public CycleReplayDescription(UUID gameReplayId, DateTime replayDate, List<String> players, String winningPlayer, int numberOfTurns, TEnvironmentDescription environmentDescription) {
        this.replayID = gameReplayId;
        this.replayDate = replayDate;
        this.players = players;
        this.winningPlayer = winningPlayer;
        this.numberOfTurns = numberOfTurns;
        this.environmentDescription = environmentDescription.toString();

        this.gameReplayFileLocation = "./GameReplays/Replay_" + replayID.toString() + ".replay";
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CycleReplayDescription)) return false;

        CycleReplayDescription that = (CycleReplayDescription) o;

        if (getReplayID() != null && that.getReplayID() != null) {
            if(!getReplayID().equals(that.getReplayID())) {
                return false;
            }
        }

        if (getNumberOfTurns() != that.getNumberOfTurns()) return false;

        for(int i = 0; i < getPlayers().size(); i++) {
            if(!getPlayers().get(i).equals(that.getPlayers().get(i))) {
                return false;
            }
        }

        if (getReplayDate() != null ? !getReplayDate().equals(that.getReplayDate()) : that.getReplayDate() != null) return false;
        if (getWinningPlayer() != null ? !getWinningPlayer().equals(that.getWinningPlayer()) : that.getWinningPlayer() != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (Id ^ (Id >>> 32));
        result = 31 * result + (getReplayDate() != null ? getReplayDate().hashCode() : 0);
        result = 31 * result + (getPlayers() != null ? getPlayers().hashCode() : 0);
        result = 31 * result + (getWinningPlayer() != null ? getWinningPlayer().hashCode() : 0);
        result = 31 * result + getNumberOfTurns();
        return result;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IHasTransportType ---------------------

    @Override
    public TCycleReplayDescription getTransportType() {
        List<String> playerList = new ArrayList<String>();
        for(String p : this.getPlayers()){
            playerList.add(p);
        }
        return new TCycleReplayDescription(this.getReplayID(), this.getReplayDate(), playerList, this.getWinningPlayer(), this.getNumberOfTurns());
    }

// -------------------------- PUBLIC METHODS --------------------------


}
