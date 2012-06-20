package GameStatistics.Implementation.Entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 27.05.12
 * Time: 22:41
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class ClientName {
    @javax.persistence.Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public long Id;

    public String name;

    public ClientName() {

    }

    public ClientName(String name) {
        this.name = name;
    }
}
