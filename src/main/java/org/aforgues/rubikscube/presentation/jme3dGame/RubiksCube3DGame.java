package org.aforgues.rubikscube.presentation.jme3dGame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import org.aforgues.rubikscube.core.Cubie;
import org.aforgues.rubikscube.core.Facelet;
import org.aforgues.rubikscube.core.RubiksCube;
import org.aforgues.rubikscube.core.ThreeDimCoordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RubiksCube3DGame extends SimpleApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(RubiksCube3DGame.class);

    private static Box faceletFrontOrBackMesh = new Box(0.48f, 0.48f, 0.01f);
    private static Box faceletTopOrBottomMesh = new Box(0.48f, 0.01f, 0.48f);
    private static Box faceletLeftOrRightMesh = new Box(0.01f, 0.48f, 0.48f);

    private static Box faceletUnitXEdge = new Box( 0.5f,  0.02f, 0.02f);
    private static Box faceletUnitYEdge = new Box( 0.02f, 0.5f,  0.02f);
    private static Box faceletUnitZEdge = new Box( 0.02f, 0.02f, 0.5f);

    private static final Trigger TRIGGER_YAW_ROTATE   = new KeyTrigger(KeyInput.KEY_Y);
    private static final Trigger TRIGGER_ROLL_ROTATE  = new KeyTrigger(KeyInput.KEY_R);
    private static final Trigger TRIGGER_PITCH_ROTATE = new KeyTrigger(KeyInput.KEY_P);

    private static final String MAPPING_YAW_ROTATE    = "Yaw rotation";
    private static final String MAPPING_ROLL_ROTATE   = "Roll rotation";
    private static final String MAPPING_PITCH_ROTATE  = "Pitch rotation";

    private RubiksCube rubiksCube;

    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Trigger analog : {}", name);
            }

            if (MAPPING_PITCH_ROTATE.equals(name)) {
                rubiksCubeNode.rotate(1.0f*tpf, 0, 0);
            }
            else if (MAPPING_YAW_ROTATE.equals(name)) {
                rubiksCubeNode.rotate(0, 1.0f*tpf, 0);
            }
            else if (MAPPING_ROLL_ROTATE.equals(name)) {
                rubiksCubeNode.rotate(0, 0, 1.0f*tpf);
            }
        }
    };

    public static void main(String[] args){
        AppSettings settings = new AppSettings(true);
        settings.setTitle("aforgues's RubiksCube 3D Game");
        settings.setSettingsDialogImage("assets/Interface/rubiks-cube-logo.jpg");
        //settings.setUseInput(false); // Disable default WASD navigation input

        RubiksCube3DGame app = new RubiksCube3DGame();
        app.setSettings(settings);
        app.start(); // start the game
    }

    protected Node rubiksCubeNode;

    @Override
    public void simpleInitApp() {
        assetManager.registerLocator("src/main/resources/assets", FileLocator.class);

        rubiksCube = new RubiksCube(3);

        rubiksCubeNode = new Node("pivot");
        rootNode.attachChild(rubiksCubeNode);

        for (Cubie cubie : rubiksCube.getAllCubies()) {
            Node nCubie = createCubieV2(cubie);
            rubiksCubeNode.attachChild(nCubie);
        }

        //pivot.getLocalRotation().slerp(pivot.getLocalRotation().fromAngleAxis(20 * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y), 1f);

        // Set initial camera position and rotation so that we can see three faces of the RubiksCube
        cam.setLocation(new Vector3f(5.4f, 4f, 7.63f));
        cam.setRotation(new Quaternion(-0.025575094f, 0.93635076f, -0.20711282f, -0.2823074f));

        // Activate / Deactivate HUD stats
        if (!LOGGER.isDebugEnabled()) {
            setDisplayFps(false);
            setDisplayStatView(false);
        }

        // init trigger and mappings
        //inputManager.clearMappings();
        inputManager.addMapping(MAPPING_YAW_ROTATE, TRIGGER_YAW_ROTATE);
        inputManager.addMapping(MAPPING_ROLL_ROTATE, TRIGGER_ROLL_ROTATE);
        inputManager.addMapping(MAPPING_PITCH_ROTATE, TRIGGER_PITCH_ROTATE);

        // init Listener
        inputManager.addListener(analogListener, MAPPING_PITCH_ROTATE, MAPPING_ROLL_ROTATE, MAPPING_YAW_ROTATE);

        // TODO : init mouse target picker

    }

    /* Use the main event loop to trigger repeating actions. */
    @Override
    public void simpleUpdate(float tpf) {
        // make the rubikscube rotate:
        //rubiksCubeNode.rotate(0.3f*tpf, 0.6f*tpf, 0.9f*tpf);
    }

    // Create Cubie geometry by creating 6 facelet using following method
    private Node createCubieV2(Cubie cubie) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(cubie.toString());
        }

        Node nCubie = new Node();

        nCubie.attachChild(createFacelet(faceletTopOrBottomMesh, "topFace",    cubie.getCoordinates(), new Vector3f(0,    0.5f, 0),     cubie.getTopFace()));
        nCubie.attachChild(createFacelet(faceletTopOrBottomMesh, "bottomFace", cubie.getCoordinates(), new Vector3f(0,    -0.5f,0),     cubie.getBottomFace()));
        nCubie.attachChild(createFacelet(faceletFrontOrBackMesh, "frontFace",  cubie.getCoordinates(), new Vector3f(0,    0,    0.5f),  cubie.getFrontFace()));
        nCubie.attachChild(createFacelet(faceletFrontOrBackMesh, "backFace",   cubie.getCoordinates(), new Vector3f(0,    0,    -0.5f), cubie.getBackFace()));
        nCubie.attachChild(createFacelet(faceletLeftOrRightMesh, "leftFace",   cubie.getCoordinates(), new Vector3f(-0.5f,0,    0),     cubie.getLeftFace()));
        nCubie.attachChild(createFacelet(faceletLeftOrRightMesh, "rightFace",  cubie.getCoordinates(), new Vector3f(0.5f, 0,    0),     cubie.getRightFace()));

        return nCubie;
    }

    private Spatial createFacelet(Box faceletMesh, String name, ThreeDimCoordinate coord, Vector3f location, Facelet color) {
        Node faceletNode = new Node();

        // Main facelet content

        float offset = this.rubiksCube.getSize()/2f + 0.5f;

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Display of face {} with color {} at coord {} with offset = {}", name, color, coord, offset);
        }

        Vector3f faceletLocation = new Vector3f(coord.getX() - offset + location.getX(),
                                                coord.getY() - offset + location.getY(),
                                                coord.getZ() - offset + location.getZ());

        faceletNode.attachChild(createBasicGeometry(faceletMesh, name, faceletLocation, convertColor(color)));

        // Add 4 edges for each facelet to fill the blanks beetween facelet meshes
        if (faceletTopOrBottomMesh.equals(faceletMesh)) {
            faceletNode.attachChild(createEdgeGeometry(faceletUnitXEdge, "bottom_edge_" + name, faceletLocation.add(new Vector3f(0, 0, -0.5f))));
            faceletNode.attachChild(createEdgeGeometry(faceletUnitXEdge, "top_edge_" + name, faceletLocation.add(new Vector3f(0, 0, 0.5f))));
            faceletNode.attachChild(createEdgeGeometry(faceletUnitZEdge, "left_edge_" + name, faceletLocation.add(new Vector3f(-0.5f, 0, 0))));
            faceletNode.attachChild(createEdgeGeometry(faceletUnitZEdge, "right_edge_" + name, faceletLocation.add(new Vector3f(0.5f, 0, 0))));
        }
        else if (faceletFrontOrBackMesh.equals(faceletMesh)) {
            faceletNode.attachChild(createEdgeGeometry(faceletUnitXEdge, "bottom_edge_" + name, faceletLocation.add(new Vector3f(0, -0.5f, 0))));
            faceletNode.attachChild(createEdgeGeometry(faceletUnitXEdge, "top_edge_" + name, faceletLocation.add(new Vector3f(0, 0.5f, 0))));
            faceletNode.attachChild(createEdgeGeometry(faceletUnitYEdge, "left_edge_" + name, faceletLocation.add(new Vector3f(-0.5f, 0, 0))));
            faceletNode.attachChild(createEdgeGeometry(faceletUnitYEdge, "right_edge_" + name, faceletLocation.add(new Vector3f(0.5f, 0, 0))));
        }
        else if (faceletLeftOrRightMesh.equals(faceletMesh)) {
            faceletNode.attachChild(createEdgeGeometry(faceletUnitZEdge, "bottom_edge_" + name, faceletLocation.add(new Vector3f(0, -0.5f, 0))));
            faceletNode.attachChild(createEdgeGeometry(faceletUnitZEdge, "top_edge_" + name, faceletLocation.add(new Vector3f(0, 0.5f, 0))));
            faceletNode.attachChild(createEdgeGeometry(faceletUnitYEdge, "left_edge_" + name, faceletLocation.add(new Vector3f(0, 0, -0.5f))));
            faceletNode.attachChild(createEdgeGeometry(faceletUnitYEdge, "right_edge_" + name, faceletLocation.add(new Vector3f(0, 0, 0.5f))));
        }

        return faceletNode;
    }

    private ColorRGBA convertColor(Facelet facelet) {
        switch (facelet) {
            case BLUE:
                return ColorRGBA.Blue;

            case RED:
                return ColorRGBA.Red;

            case GREEN:
                return ColorRGBA.Green;

            case WHITE:
                return ColorRGBA.White;

            case ORANGE:
                return ColorRGBA.Orange;

            case YELLOW:
                return ColorRGBA.Yellow;

            case NONE:
                return ColorRGBA.Black;
        }
        return ColorRGBA.Gray;
    }

    private Geometry createEdgeGeometry(Box edgeMesh, String name, Vector3f location) {
        return createBasicGeometry(edgeMesh, name, location, ColorRGBA.DarkGray);
    }

    private Geometry createBasicGeometry(Box edgeMesh, String name, Vector3f location, ColorRGBA color) {
            Geometry geom = new Geometry(name, edgeMesh);

        geom.setLocalTranslation(location);

        Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");

        mat.setColor("Color", color);
        geom.setMaterial(mat);
        return geom;
    }
}
