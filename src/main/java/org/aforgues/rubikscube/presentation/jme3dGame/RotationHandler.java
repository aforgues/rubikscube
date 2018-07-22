package org.aforgues.rubikscube.presentation.jme3dGame;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import org.aforgues.rubikscube.core.DefinedMove;
import org.aforgues.rubikscube.core.Move;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

import static com.jme3.math.FastMath.DEG_TO_RAD;

public class RotationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RotationHandler.class);

    private static final Quaternion YAW_90   = new Quaternion().fromAngleAxis(90*DEG_TO_RAD, Vector3f.UNIT_Y);
    private static final Quaternion ROLL_90  = new Quaternion().fromAngleAxis(90*DEG_TO_RAD, Vector3f.UNIT_Z);
    private static final Quaternion PITCH_90 = new Quaternion().fromAngleAxis(90*DEG_TO_RAD, Vector3f.UNIT_X);

    private Collection<Node> rotatingNodes;
    private DefinedMove move;
    private Quaternion startingRotation;
    private Quaternion targetRotation;
    private static final float ROTATION_DURATION = 1f;
    private long rotationStart;


    public RotationHandler(Map<Integer, Collection<Node>> map, int index, Move rotation) {
        rotatingNodes  = map.get(Integer.valueOf(index));
        startingRotation = rotatingNodes.stream().findFirst().get().getLocalRotation();
        move = new DefinedMove(rotation, index);
        targetRotation = startingRotation.mult(convertMoveToQuaternion(rotation));

        rotationStart = System.currentTimeMillis();
    }

    private Quaternion convertMoveToQuaternion(Move move) {
        if (move == null)
            return null;

        switch (move) {
            case YAW:
                return YAW_90;
            case ROLL:
                return ROLL_90;
            case PITCH:
                return PITCH_90;
            default:
                LOGGER.warn("Unmanaged move : {}", move);
        }
        return null;
    }

    public boolean hasRotationOnGoing() {
        return this.rotatingNodes != null;
    }

    public boolean processRotation(float tpf) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Processing rotation : rotating nodes to target : {} at {} time per frame", targetRotation, tpf);
        }

        long rotationCurrent = System.currentTimeMillis();
        long currentDuration = rotationCurrent - rotationStart;
        float changeAmount = currentDuration / (ROTATION_DURATION * 1000);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Processing rotation : current rotation at {} changeAmount", changeAmount);
        }

        // FIXME : find out how to use tpf parameter
        rotatingNodes.stream().forEach(n -> n.setLocalRotation(new Quaternion().slerp(startingRotation, targetRotation, changeAmount)));

        if (changeAmount > 1) {
            LOGGER.debug("Processing rotation : current rotation animation is finished (after a duration of {} s)", ROTATION_DURATION);
            rotatingNodes = null;
            targetRotation = null;
            rotationStart = 0;

            return true;
        }

        return false;
    }

    public DefinedMove getDefinedMove() {
        return move;
    }
}
