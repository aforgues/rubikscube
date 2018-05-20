package org.aforgues.rubikscube.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class RubiksCube2DMouseWheelListener implements MouseWheelListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RubiksCube2DMouseWheelListener.class);

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("# Mouse wheel moved : {}", e.paramString());
        }

        RubiksCube2D rc2d = (RubiksCube2D) e.getSource();

        if (e.getWheelRotation() > 0)
            rc2d.zoomIn();
        else
            rc2d.zoomOut();
        rc2d.repaint();
    }
}
