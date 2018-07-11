package org.aforgues.rubikscube.core;

import org.aforgues.rubikscube.presentation.ascii.RubiksCubeAsciiFormat;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RubiksCubeTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RubiksCubeTest.class);

    private static final int RUBIKS_CUBE_SIZE = 3;
    private static final int NB_SHUFFLE_MOVE = 20;

    private RubiksCube rc;

    @Before
    public void init() {
        rc = new RubiksCube(RUBIKS_CUBE_SIZE);
        printRubiksCubeState("Init");
    }

    @Test
    public void test_solve_rubikscube() {
        // Given a randomised RubiksCube
        rc.shuffle(NB_SHUFFLE_MOVE);
        printRubiksCubeState("Shuffled");

        // When I run the AI to solve it
        rc.solve(false);
        printRubiksCubeState("AI");

        // Then
        boolean next = true;
        while(next) {
            next = rc.moveToNextPosition();
        }
        printRubiksCubeState("Path applied");

        Assert.assertTrue(rc.isSolved());
    }

    @After
    public void clean() {
        this.rc = null;
    }

    private void printRubiksCubeState(String phase) {
        LOGGER.info(phase + " : {}", rc);

        if (LOGGER.isDebugEnabled())
            new RubiksCubeAsciiFormat(rc).show();
    }
}
