package it.polimi.ingsw.model.ammo;

import it.polimi.ingsw.model.exceptions.CannotAffordException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class tests {@link AmmoCubes} methods.
 */
public class TestAmmoCubes {

    /**
     * This method tests the construction of an {@link AmmoCubes} element having a negative (thus not valid) value as argument.
     * Since AmmoCubes throws a {@code IllegalArgumentException} in this case, the test is passed if such exception is thrown.
     */
    @Test
    public void constructorWithNegativeNumber() {
        boolean catchTaken = false;
        try {
            AmmoCubes ammoCubes = new AmmoCubes(-1, 0, 0);
        } catch (IllegalArgumentException e) {
            catchTaken = true;
        }
        assertTrue(catchTaken);
    }

    /**
     * This method tests the construction of an {@link AmmoCubes} element having a too big (thus not valid) value as argument.
     * Since AmmoCubes throws a {@code IllegalArgumentException} in this case, the test is passed if such exception is thrown.
     */
    @Test
    public void constructorWithBigNumber() {
        boolean catchTaken = false;
        try {
            AmmoCubes ammoCubes = new AmmoCubes(2, 5, 1);
        } catch (IllegalArgumentException e) {
            catchTaken = true;
        }
        assertTrue(catchTaken);
    }

    /**
     * This method tests the construction of a {@link AmmoCubes} element having no value as argument.
     * Since the {@code AmmoCubes} created will be the same of
     * {@code AmmoCubes = new AmmoCubes(0, 0, 0)}, the test is passed if no exception is thrown.
     */
    @Test
    public void constructorWithoutArguments() {
        boolean catchTaken = false;
        try {
            AmmoCubes ammoCubes = new AmmoCubes();
        } catch (IllegalArgumentException e) {
            catchTaken = true;
        }
        assertFalse(catchTaken);
    }

    /**
     * This method tests the {@link AmmoCubes#take(AmmoCubes)} method on a non-void {@link AmmoCubes} element from another
     * non-void {@code AmmoCubes} element containing enough cubes. Since this action is legal, this test is passed if
     * {@link CannotAffordException} is not thrown.
     */
    @Test
    public void take() {
        boolean catchTaken = false;

        try {
            AmmoCubes ammoCubes1 = new AmmoCubes(1, 2, 2);
            AmmoCubes ammoCubes2 = new AmmoCubes(0, 2, 1);

            assertEquals(new AmmoCubes(1, 0, 1), ammoCubes1.take(ammoCubes2));
        } catch (CannotAffordException e) {
            catchTaken = true;
        }

        assertFalse(catchTaken);
    }

    /**
     * This method tests the {@link AmmoCubes#take(AmmoCubes)} method on a non-void {@link AmmoCubes} element from another
     * {@code AmmoCubes} element containing no cubes. Since this action is always legal, this test is passed if
     * {@link CannotAffordException} is not thrown.
     */
    @Test
    public void takeZero() {
        boolean catchTaken = false;

        try {
            AmmoCubes ammoCubes1 = new AmmoCubes(3, 2, 0);
            AmmoCubes ammoCubes2 = new AmmoCubes(0, 0, 0);

            assertEquals(new AmmoCubes(3, 2, 0), ammoCubes1.take(ammoCubes2));

        } catch (CannotAffordException e) {
            catchTaken = true;
        }
        assertFalse(catchTaken);
    }

    /**
     * This method tests the {@link AmmoCubes#take(AmmoCubes)} method on a void {@link AmmoCubes} element from another void
     * {@code AmmoCubes} element. Since this action is always legal, this test is passed if {@link CannotAffordException} is not
     * thrown.
     */
    @Test
    public void takeZeroFromZero() {
        boolean catchTaken = false;
        try {
            AmmoCubes ammoCubes1 = new AmmoCubes(0, 0, 0);
            AmmoCubes ammoCubes2 = new AmmoCubes(0, 0, 0);
            ammoCubes1.take(ammoCubes2);

            assertEquals(new AmmoCubes(0, 0, 0), ammoCubes1);
        } catch (CannotAffordException e) {
            catchTaken = true;
        }
        assertFalse(catchTaken);
    }

    /**
     * This method tests the {@link AmmoCubes#take(AmmoCubes)} method on a {@link AmmoCubes} element from another {@code AmmoCubes}
     * element containing too many cubes to be taken. Since this action is not legal, this test is passed if
     * {@link CannotAffordException} is thrown.
     */
    @Test
    public void takeIllegal() {
        boolean catchTaken = false;
        try {
            AmmoCubes ammoCubes1 = new AmmoCubes(1, 3, 2);
            AmmoCubes ammoCubes2 = new AmmoCubes(2, 0, 1);

            ammoCubes1.take(ammoCubes2);    //the return value is ignored
        } catch (Exception e) {
            catchTaken = true;
        }
        assertTrue(catchTaken);
    }
}