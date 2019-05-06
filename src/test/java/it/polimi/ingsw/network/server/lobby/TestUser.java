package it.polimi.ingsw.network.server.lobby;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestUser {

    //Users are the same if names are equals
    @Test
    public void equals1() {
        User u1 = new User("Mark");
        User u2 = new User("Mark");
        User u3 = new User("John");

        assertEquals(u1, u1);
        assertEquals(u1, u2);
        assertNotEquals(u1, u3);
    }
}