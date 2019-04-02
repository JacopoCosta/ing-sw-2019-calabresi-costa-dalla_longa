package it.polimi.ingsw;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class TestAmmoCubes {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void constructorWithNegativeNumber() {
        boolean catchTaken = false;
        try {
            AmmoCubes ammoCubes = new AmmoCubes(-1, 0, 0);
        }
        catch(Exception e) {
            catchTaken = true;
       }
        assertTrue(catchTaken);
    }

    @Test
    public void constructorWithBigNumber() {
        boolean catchTaken = false;
        try {
            AmmoCubes ammoCubes = new AmmoCubes(2, 5, 1);
        }
        catch(Exception e) {
            catchTaken = true;
        }
        assertTrue(catchTaken);
    }

    @Test
    public void constructorWithoutArguments() {
        boolean catchTaken = false;
        try {
            AmmoCubes ammoCubes = new AmmoCubes();
        }
        catch(Exception e) {
            catchTaken = true;
        }
        assertFalse(catchTaken);
    }


    @Test
    public void sum() {

    }

    @Test
    public void takeZero() {
        boolean catchTaken = false;
        try {
            AmmoCubes ammoCubes1 = new AmmoCubes(3, 2, 0);
            AmmoCubes ammoCubes2 = new AmmoCubes(0, 0, 0);
            ammoCubes1.take(ammoCubes2);
        } catch (Exception e) {
            catchTaken = true;
        }
        assertFalse(catchTaken);
    }

    @Test
    public void takeZeroFromZero() {
        boolean catchTaken = false;
        try {
            AmmoCubes ammoCubes1 = new AmmoCubes(0, 0, 0);
            AmmoCubes ammoCubes2 = new AmmoCubes(0, 0, 0);
            ammoCubes1.take(ammoCubes2);
        } catch (Exception e) {
            catchTaken = true;
        }
        assertFalse(catchTaken);
    }

    @Test
    public void takeIllegal() {
        boolean catchTaken = false;
        try {
            AmmoCubes ammoCubes1 = new AmmoCubes(1, 3, 2);
            AmmoCubes ammoCubes2 = new AmmoCubes(2, 0, 1);
            ammoCubes1.take(ammoCubes2);
        } catch (Exception e) {
            catchTaken = true;
        }
        assertTrue(catchTaken);
    }
}