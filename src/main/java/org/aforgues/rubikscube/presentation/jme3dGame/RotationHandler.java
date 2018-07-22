package org.aforgues.rubikscube.presentation.jme3dGame;

import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

public class RotationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RotationHandler.class);

    private Collection<Node> rotatingNodes;
    private Quaternion startingRotation;
    private Quaternion targetRotation;
    private static final float ROTATION_DURATION = 1f;
    private long rotationStart;


    public RotationHandler(Map<Integer, Collection<Node>> map, int index, Quaternion rotation) {
        rotatingNodes  = map.get(Integer.valueOf(index));
        startingRotation = rotatingNodes.stream().findFirst().get().getLocalRotation();
        targetRotation = startingRotation.mult(rotation);

        rotationStart = System.currentTimeMillis();
    }

    public boolean hasRotationOnGoing() {
        return this.rotatingNodes != null;
    }

    public void processRotation(float tpf) {
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

            // Redispatch Cubie node in rotation maps

            return;
        }
    }
}
