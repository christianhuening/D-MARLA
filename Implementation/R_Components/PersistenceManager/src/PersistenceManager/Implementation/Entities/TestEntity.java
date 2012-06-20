package PersistenceManager.Implementation.Entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 14.05.12
 * Time: 21:35
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class TestEntity {

    @javax.persistence.Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public long Id;

    public int testValue1;

    public TestEntity() {

    }

    public TestEntity(int testValue1) {
        this.testValue1 = testValue1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestEntity)) return false;

        TestEntity that = (TestEntity) o;

        if (testValue1 != that.testValue1) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return testValue1;
    }
}
