package org.aforgues.rubikscube.presentation.jme3dGame;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.aforgues.rubikscube.core.Move;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jme3.math.FastMath.DEG_TO_RAD;

public class RubiksCube3DUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(RubiksCube3DUtility.class);

    private static final Quaternion YAW_90   = new Quaternion().fromAngleAxis(-90*DEG_TO_RAD, Vector3f.UNIT_Y);
    private static final Quaternion ROLL_90  = new Quaternion().fromAngleAxis(-90*DEG_TO_RAD, Vector3f.UNIT_Z);
    private static final Quaternion PITCH_90 = new Quaternion().fromAngleAxis(-90*DEG_TO_RAD, Vector3f.UNIT_X);


    public static Quaternion convertMoveToQuaternion(Move move) {
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

    public static void logQuat(Quaternion quat, String name) {
        float[] angles = new float[3];
        quat.toAngles(angles);
        LOGGER.debug("Quaternion angles for {} => x:{},y:{},z:{}", name, Math.round(FastMath.RAD_TO_DEG * angles[0]), Math.round(FastMath.RAD_TO_DEG * angles[1]), Math.round(FastMath.RAD_TO_DEG * angles[2]));
    }

    public static float getDisplayOffsetToCenterRubiksCube(int rubiksCubeSize) {
        return rubiksCubeSize/2f + 0.5f;
    }
}
