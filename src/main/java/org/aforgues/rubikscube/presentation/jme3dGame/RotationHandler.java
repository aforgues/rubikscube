package org.aforgues.rubikscube.presentation.jme3dGame;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.aforgues.rubikscube.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.jme3.math.FastMath.DEG_TO_RAD;
import static java.util.Collections.EMPTY_LIST;

public class RotationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RotationHandler.class);

    // Constants
    private static final float ROTATION_DURATION = 1f;
    private static final float KEYBOARD_MOVE_HIGHLIGHT_SCALE = 1.05f;

    // Fields
    private Collection<RotationNode> rotatingNodes;
    private DefinedMove move;
    private long rotationStart;
    private boolean isStarted;

    private class RotationNode {
        private Node node;
        private Quaternion startingRotation;
        private Quaternion targetRotation;

        public RotationNode(Node node, Move rotation) {
            this.node = node;
            this.startingRotation = node.getLocalRotation().normalizeLocal();
            //this.startingRotation = Quaternion.IDENTITY;
            this.targetRotation = RubiksCube3DUtility.convertMoveToQuaternion(rotation).mult(startingRotation).normalizeLocal();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Node selected : {}", node);
                RubiksCube3DUtility.logQuat(startingRotation, "startingRotation");
                RubiksCube3DUtility.logQuat(targetRotation, "targetRotation");
            }

        }

        public Node getNode() {
            return node;
        }

        public Quaternion getStartingRotation() {
            return startingRotation;
        }

        public Quaternion getTargetRotation() {
            return targetRotation;
        }
    }

    public RotationHandler(Node rubiksCubeNode, RubiksCube rubiksCube, int index, Move rotation) {
        this.move = new DefinedMove(rotation, index);

        computeRotationNodes(rubiksCubeNode, rubiksCube, index, rotation);
    }

    private void computeRotationNodes(Node rubiksCubeNode, RubiksCube rubiksCube, int index, Move rotation) {
        List<RotationNode> nodes = new ArrayList<>();

        List<Cubie> cubies = EMPTY_LIST;
        switch (rotation) {
            case PITCH:
            case UNPITCH:
            case DOUBLE_PITCH:
                cubies = rubiksCube.getCubies(index, Axis.X);
                break;
            case YAW:
            case UNYAW:
            case DOUBLE_YAW:
                cubies = rubiksCube.getCubies(index, Axis.Y);
                break;
            case ROLL:
            case UNROLL:
            case DOUBLE_ROLL:
                cubies = rubiksCube.getCubies(index, Axis.Z);
                break;
            default:
                LOGGER.warn("Unmanaged move : {}", move);
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Rotation handler :: computeRotationNodes :: cubies => {}", cubies);
        }

        float offset = RubiksCube3DUtility.getDisplayOffsetToCenterRubiksCube(rubiksCube.getSize());

        for (Spatial spatial : rubiksCubeNode.getChildren()) {
            Vector3f nodeLocation = spatial.getWorldBound().getCenter().add(offset, offset, offset);
            Cubie cubie = rubiksCube.getCubie(Math.round(nodeLocation.getX()), Math.round(nodeLocation.getY()), Math.round(nodeLocation.getZ()));

            if (cubie == null) {
                LOGGER.error("Unable to locate cubie in RubiksCube from node at x={},y={},z={}", Math.round(nodeLocation.getX()), Math.round(nodeLocation.getY()), Math.round(nodeLocation.getZ()));
                continue;
            }

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Rotation handler :: computeRotationNodes :: cubie found from node location {} => {}", nodeLocation, cubie);
                LOGGER.trace("Rotation handler :: computeRotationNodes :: cubie searched from node location => x={},y={},z={}", Math.round(nodeLocation.getX()), Math.round(nodeLocation.getY()), Math.round(nodeLocation.getZ()));
            }

            if (cubies.contains(cubie)) {
                nodes.add(new RotationNode ((Node) spatial, rotation));

                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Rotation handler :: computeRotationNodes :: cubie added to list => {}", cubie);
                }
            }
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Rotation handler :: computeRotationNodes => {}", nodes);
        }

        this.rotatingNodes = nodes;

        this.highlightSelectedNodes();
    }

    public void start() {
        this.isStarted = true;
        this.rotationStart = System.currentTimeMillis();

        this.releaseSelectedNodes();
    }

    public boolean isOnGoing() {
        return this.rotatingNodes != null && this.isStarted;
    }

    public boolean processRotation(float tpf) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Processing rotation : rotating nodes with a move {} at {} time per frame", this.move, tpf);
        }

        long rotationCurrent = System.currentTimeMillis();
        long currentDuration = rotationCurrent - rotationStart;
        float changeAmount = currentDuration / (ROTATION_DURATION * 1000);

        if (changeAmount > 1)
            changeAmount = 1;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Processing rotation : current rotation at {} changeAmount", changeAmount);
        }

        // FIXME : find out how to use tpf parameter
        // FIXME : the rotation duration is not correctly applied => the animation seems (visually) already finished whereas we are still looping in the processRotation method with finalChangeAmount < 1
        float finalChangeAmount = changeAmount;
        rotatingNodes.stream().forEach(n -> n.getNode().setLocalRotation(new Quaternion().slerp(n.getStartingRotation(), n.getTargetRotation(), finalChangeAmount)));

        if (changeAmount == 1) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Processing rotation : current rotation animation is finished (after a duration of {} s)", ROTATION_DURATION);
            }
            rotatingNodes = null;
            rotationStart = 0;
            isStarted = false;

            return true;
        }

        return false;
    }

    public DefinedMove getDefinedMove() {
        return move;
    }

    private void highlightSelectedNodes() {
        if (this.rotatingNodes != null) {
            for (RotationNode rotationNode : this.rotatingNodes) {
                rotationNode.getNode().setLocalScale(KEYBOARD_MOVE_HIGHLIGHT_SCALE);
            }
        }
    }

    private void releaseSelectedNodes() {
        if (this.rotatingNodes != null) {
            for (RotationNode rotationNode : this.rotatingNodes) {
                rotationNode.getNode().setLocalScale(1.0f);
            }
        }
    }
}
