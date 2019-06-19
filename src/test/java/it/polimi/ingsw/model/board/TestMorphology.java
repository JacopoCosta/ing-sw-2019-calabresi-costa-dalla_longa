package it.polimi.ingsw.model.board;

import it.polimi.ingsw.view.remote.ContentType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.view.remote.ContentType.*;
import static org.junit.Assert.*;

public class TestMorphology {

    private List<ContentType> morphology1 = new ArrayList<>();  //relative to board1
    private List<ContentType> morphology2 = new ArrayList<>();  //relative to board2
    private List<ContentType> morphology3 = new ArrayList<>();  //relative to board3
    private List<ContentType> morphology4 = new ArrayList<>();  //relative to board4

    @Before
    public void setUp() {

        Board board1 = Board.generate(null, 1);
        Board board2 = Board.generate(null, 2);
        Board board3 = Board.generate(null, 3);
        Board board4 = Board.generate(null, 4);

        morphology1 = board1.getMorphology();
        morphology2 = board2.getMorphology();
        morphology3 = board3.getMorphology();
        morphology4 = board4.getMorphology();
    }

    @Test
    public void getMorphology1() {

        List<ContentType> test = new ArrayList<>();

        //1st row
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_VOID);
        test.add(ANGLE);
        //2nd row
        test.add(VER_FULL);
        test.add(CELL);
        test.add(VER_VOID);
        test.add(CELL);
        test.add(VER_VOID);
        test.add(CELL);
        test.add(VER_FULL);
        test.add(NONE);
        test.add(VER_VOID);
        //3rd row
        test.add(ANGLE);
        test.add(HOR_DOOR);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_DOOR);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        //4th row
        test.add(VER_FULL);
        test.add(CELL);
        test.add(VER_VOID);
        test.add(CELL);
        test.add(VER_VOID);
        test.add(CELL);
        test.add(VER_DOOR);
        test.add(CELL);
        test.add(VER_FULL);
        //5th row
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_DOOR);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_VOID);
        test.add(ANGLE);
        //6th row
        test.add(VER_VOID);
        test.add(NONE);
        test.add(VER_FULL);
        test.add(CELL);
        test.add(VER_VOID);
        test.add(CELL);
        test.add(VER_DOOR);
        test.add(CELL);
        test.add(VER_FULL);
        //7th row
        test.add(ANGLE);
        test.add(HOR_VOID);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);

        assertEquals(test.size(), morphology1.size());

        for(int i=0; i<morphology1.size(); i++) {
            assertEquals(test.get(i), morphology1.get(i));
        }
    }

    @Test
    public void getMorphology2() {

        List<ContentType> test = new ArrayList<>();

        //1st row
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        //2nd row
        test.add(VER_FULL);
        test.add(CELL);
        test.add(VER_VOID);
        test.add(CELL);
        test.add(VER_VOID);
        test.add(CELL);
        test.add(VER_DOOR);
        test.add(CELL);
        test.add(VER_FULL);
        //3rd row
        test.add(ANGLE);
        test.add(HOR_DOOR);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_DOOR);
        test.add(ANGLE);
        test.add(HOR_DOOR);
        test.add(ANGLE);
        //4th row
        test.add(VER_FULL);
        test.add(CELL);
        test.add(VER_VOID);
        test.add(CELL);
        test.add(VER_FULL);
        test.add(CELL);
        test.add(VER_VOID);
        test.add(CELL);
        test.add(VER_FULL);
        //5th row
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_DOOR);
        test.add(ANGLE);
        test.add(HOR_VOID);
        test.add(ANGLE);
        test.add(HOR_VOID);
        test.add(ANGLE);
        //6th row
        test.add(VER_VOID);
        test.add(NONE);
        test.add(VER_FULL);
        test.add(CELL);
        test.add(VER_DOOR);
        test.add(CELL);
        test.add(VER_VOID);
        test.add(CELL);
        test.add(VER_FULL);
        //7th row
        test.add(ANGLE);
        test.add(HOR_VOID);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);

        assertEquals(test.size(), morphology2.size());

        for(int i=0; i<morphology2.size(); i++) {
            assertEquals(test.get(i), morphology2.get(i));
        }
    }

    @Test
    public void getMorphology3() {

        List<ContentType> test = new ArrayList<>();

        //1st row
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_VOID);
        test.add(ANGLE);
        //2nd row
        test.add(VER_FULL);
        test.add(CELL);
        test.add(VER_DOOR);
        test.add(CELL);
        test.add(VER_VOID);
        test.add(CELL);
        test.add(VER_FULL);
        test.add(NONE);
        test.add(VER_VOID);
        //3rd row
        test.add(ANGLE);
        test.add(HOR_VOID);
        test.add(ANGLE);
        test.add(HOR_DOOR);
        test.add(ANGLE);
        test.add(HOR_DOOR);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        //4th row
        test.add(VER_FULL);
        test.add(CELL);
        test.add(VER_FULL);
        test.add(CELL);
        test.add(VER_VOID);
        test.add(CELL);
        test.add(VER_DOOR);
        test.add(CELL);
        test.add(VER_FULL);
        //5th row
        test.add(ANGLE);
        test.add(HOR_DOOR);
        test.add(ANGLE);
        test.add(HOR_DOOR);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_VOID);
        test.add(ANGLE);
        //6th row
        test.add(VER_FULL);
        test.add(CELL);
        test.add(VER_VOID);
        test.add(CELL);
        test.add(VER_VOID);
        test.add(CELL);
        test.add(VER_DOOR);
        test.add(CELL);
        test.add(VER_FULL);
        //7th row
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);

        assertEquals(test.size(), morphology3.size());

        for(int i=0; i<morphology3.size(); i++) {
            assertEquals(test.get(i), morphology3.get(i));
        }
    }

    @Test
    public void getMorphology4() {

        List<ContentType> test = new ArrayList<>();

        //1st row
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        //2nd row
        test.add(VER_FULL);
        test.add(CELL);
        test.add(VER_DOOR);
        test.add(CELL);
        test.add(VER_VOID);
        test.add(CELL);
        test.add(VER_DOOR);
        test.add(CELL);
        test.add(VER_FULL);
        //3rd row
        test.add(ANGLE);
        test.add(HOR_VOID);
        test.add(ANGLE);
        test.add(HOR_DOOR);
        test.add(ANGLE);
        test.add(HOR_DOOR);
        test.add(ANGLE);
        test.add(HOR_DOOR);
        test.add(ANGLE);
        //4th row
        test.add(VER_FULL);
        test.add(CELL);
        test.add(VER_FULL);
        test.add(CELL);
        test.add(VER_FULL);
        test.add(CELL);
        test.add(VER_VOID);
        test.add(CELL);
        test.add(VER_FULL);
        //5th row
        test.add(ANGLE);
        test.add(HOR_DOOR);
        test.add(ANGLE);
        test.add(HOR_DOOR);
        test.add(ANGLE);
        test.add(HOR_VOID);
        test.add(ANGLE);
        test.add(HOR_VOID);
        test.add(ANGLE);
        //6th row
        test.add(VER_FULL);
        test.add(CELL);
        test.add(VER_VOID);
        test.add(CELL);
        test.add(VER_DOOR);
        test.add(CELL);
        test.add(VER_VOID);
        test.add(CELL);
        test.add(VER_FULL);
        //7th row
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);
        test.add(HOR_FULL);
        test.add(ANGLE);

        assertEquals(test.size(), morphology4.size());

        for(int i=0; i<morphology4.size(); i++) {
            assertEquals(test.get(i), morphology4.get(i));
        }
    }

}
