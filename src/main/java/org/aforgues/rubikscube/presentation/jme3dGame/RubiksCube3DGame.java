package org.aforgues.rubikscube.presentation.jme3dGame;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import org.aforgues.rubikscube.core.*;
import org.aforgues.rubikscube.presentation.ascii.RubiksCubeAsciiFormat;
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

    private static final Trigger TRIGGER_PICK_CUBIE   = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private static final String MAPPING_PICK_CUBIE    = "ray pick cubie";

    // For manual testing (through keyboard) of faces rotation
    private static final Trigger TRIGGER_YAW3_ROTATE   = new KeyTrigger(KeyInput.KEY_A);
    private static final Trigger TRIGGER_YAW2_ROTATE   = new KeyTrigger(KeyInput.KEY_Q);
    private static final Trigger TRIGGER_YAW1_ROTATE   = new KeyTrigger(KeyInput.KEY_W);
    private static final Trigger TRIGGER_ROLL3_ROTATE  = new KeyTrigger(KeyInput.KEY_Z);
    private static final Trigger TRIGGER_ROLL2_ROTATE  = new KeyTrigger(KeyInput.KEY_S);
    private static final Trigger TRIGGER_ROLL1_ROTATE  = new KeyTrigger(KeyInput.KEY_X);
    private static final Trigger TRIGGER_PITCH3_ROTATE = new KeyTrigger(KeyInput.KEY_C);
    private static final Trigger TRIGGER_PITCH2_ROTATE = new KeyTrigger(KeyInput.KEY_D);
    private static final Trigger TRIGGER_PITCH1_ROTATE = new KeyTrigger(KeyInput.KEY_E);

    private static final String MAPPING_YAW1_ROTATE    = "Yaw 1 rotation";
    private static final String MAPPING_YAW2_ROTATE    = "Yaw 2 rotation";
    private static final String MAPPING_YAW3_ROTATE    = "Yaw 3 rotation";
    private static final String MAPPING_ROLL1_ROTATE   = "Roll 1 rotation";
    private static final String MAPPING_ROLL2_ROTATE   = "Roll 2 rotation";
    private static final String MAPPING_ROLL3_ROTATE   = "Roll 3 rotation";
    private static final String MAPPING_PITCH1_ROTATE  = "Pitch 1 rotation";
    private static final String MAPPING_PITCH2_ROTATE  = "Pitch 2 rotation";
    private static final String MAPPING_PITCH3_ROTATE  = "Pitch 3 rotation";

    // For RubiksCube AI solving
    private static final Trigger TRIGGER_SHUFFLE           = new KeyTrigger(KeyInput.KEY_M);
    private static final Trigger TRIGGER_SOLVE             = new KeyTrigger(KeyInput.KEY_T);
    private static final Trigger TRIGGER_NEXT_SOLVING_PATH = new KeyTrigger(KeyInput.KEY_N);

    private static final String MAPPING_SHUFFLE            = "Shuffle RubiksCube";
    private static final String MAPPING_SOLVE              = "Solve RubiksCube";
    private static final String MAPPING_NEXT_SOLVING_PATH  = "Next Solving Path";


    private RubiksCube rubiksCube;

    // Main node with all the cubies
    protected Node rubiksCubeNode;

    // Handle rotation animation
    private RotationHandler currentRotation;

    private Spatial selectedSpatialWithLeftMouseClick;

    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float intensity, float tpf) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Trigger analog : {}", name);
            }

            // FIXME : replace RubiksCube rotations with camera rotations (to keep the cubie nodes location stable)
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

    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Trigger action : {} (isPressed : {})", name, isPressed);
            }

            if (!isPressed) {
                if (selectedSpatialWithLeftMouseClick != null) {
                    selectedSpatialWithLeftMouseClick.setLocalScale(1.0f);
                    selectedSpatialWithLeftMouseClick = null;
                }

                if (currentRotation != null) {
                    currentRotation.start();
                }

                return;
            }

            // TODO : to allow multiple actions, we need to manage a queue of input handling
            if (currentRotation != null) {
                LOGGER.warn("Rotation already on going => ignoring new action {}", name);
                return;
            }

            // Keyboard inputs
            if (MAPPING_YAW1_ROTATE.equals(name)) {
                handleRotation(1, Move.YAW);
            }
            else if (MAPPING_YAW2_ROTATE.equals(name)) {
                handleRotation(2, Move.YAW);
            }
            else if (MAPPING_YAW3_ROTATE.equals(name)) {
                handleRotation(3, Move.YAW);
            }
            else if (MAPPING_ROLL1_ROTATE.equals(name)) {
                handleRotation(1, Move.ROLL);
            }
            else if (MAPPING_ROLL2_ROTATE.equals(name)) {
                handleRotation(2, Move.ROLL);
            }
            else if (MAPPING_ROLL3_ROTATE.equals(name)) {
                handleRotation(3, Move.ROLL);
            }
            else if (MAPPING_PITCH1_ROTATE.equals(name)) {
                handleRotation(1, Move.PITCH);
            }
            else if (MAPPING_PITCH2_ROTATE.equals(name)) {
                handleRotation(2, Move.PITCH);
            }
            else if (MAPPING_PITCH3_ROTATE.equals(name)) {
                handleRotation(3, Move.PITCH);
            }
            else if (MAPPING_SHUFFLE.equals(name)) {
                shuffleRubiksCube();
            }
            else if (MAPPING_SOLVE.equals(name)) {
                solveRubiksCube();
            }
            else if (MAPPING_NEXT_SOLVING_PATH.equals(name)) {
                moveToNextSolvingPath();
            }
            // Mouse inputs
            else if (MAPPING_PICK_CUBIE.equals(name)) {
                CollisionResults results = new CollisionResults();
                Vector2f click2d = inputManager.getCursorPosition();
                Vector3f click3d = cam.getWorldCoordinates(click2d, 0f);
                Vector3f dir = cam.getWorldCoordinates(click2d, 1f).subtractLocal(click3d);
                Ray ray = new Ray(click3d, dir);
                rubiksCubeNode.collideWith(ray, results);

                if (results.size() > 0) {
                    Spatial target = results.getClosestCollision().getGeometry();

                    while(target.getName() == null || ! target.getName().startsWith("cubie_")) {
                        target = target.getParent();
                    }

                    selectedSpatialWithLeftMouseClick = target;

                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Mouse selection : {}", selectedSpatialWithLeftMouseClick.getName());
                        float offset = getDisplayOffsetToCenterRubiksCube();
                        LOGGER.debug("3D position : {}", selectedSpatialWithLeftMouseClick.getWorldBound().getCenter().add(offset, offset, offset));
                        RubiksCube3DUtility.logQuat(selectedSpatialWithLeftMouseClick.getLocalRotation(), "left click mouse selected cubie");
                    }

                    selectedSpatialWithLeftMouseClick.setLocalScale(1.1f);
                }
            }
        }

        private void shuffleRubiksCube() {
            rubiksCube.shuffle(20);
            initRubiksCubeNodes();
        }

        private void moveToNextSolvingPath() {
            DefinedMove nextMove = rubiksCube.simulateNextPosition();
            if (nextMove != null) {
                handleRotation(nextMove.getFaceIndex(), nextMove.getMove());
            }
        }

        private void solveRubiksCube() {
            rubiksCube.solve(false);
        }

        private void handleRotation(int index, Move move) {
            LOGGER.trace("Before HandleRotation : rubiksCube =>");
            new RubiksCubeAsciiFormat(rubiksCube).show();
            currentRotation = new RotationHandler(rubiksCubeNode, rubiksCube, index, move);
        }
    };

    public static void main(String[] args){
        AppSettings settings = new AppSettings(true);
        settings.setTitle("aforgues's RubiksCube 3D Game");
        settings.setSettingsDialogImage("assets/Interface/rubiks-cube-logo.jpg");

        RubiksCube3DGame app = new RubiksCube3DGame();
        app.setSettings(settings);
        app.start(); // start the game
    }


    @Override
    public void simpleInitApp() {
        assetManager.registerLocator("src/main/resources/assets", FileLocator.class);

        rubiksCube = new RubiksCube(3);
        initRubiksCubeNodes();

        // Set initial camera position and rotation so that we can see three faces of the RubiksCube
        cam.setLocation(new Vector3f(5.4f, 4f, 7.63f));
        cam.setRotation(new Quaternion(-0.025575094f, 0.93635076f, -0.20711282f, -0.2823074f));

        // Activate / Deactivate HUD stats
        if (!LOGGER.isDebugEnabled()) {
            setDisplayFps(false);
            setDisplayStatView(false);
        }

        // init trigger and mappings
        inputManager.addMapping(MAPPING_YAW_ROTATE,   TRIGGER_YAW_ROTATE);
        inputManager.addMapping(MAPPING_ROLL_ROTATE,  TRIGGER_ROLL_ROTATE);
        inputManager.addMapping(MAPPING_PITCH_ROTATE, TRIGGER_PITCH_ROTATE);
        inputManager.addMapping(MAPPING_PICK_CUBIE,   TRIGGER_PICK_CUBIE);

        inputManager.addMapping(MAPPING_YAW1_ROTATE,   TRIGGER_YAW1_ROTATE);
        inputManager.addMapping(MAPPING_YAW2_ROTATE,   TRIGGER_YAW2_ROTATE);
        inputManager.addMapping(MAPPING_YAW3_ROTATE,   TRIGGER_YAW3_ROTATE);
        inputManager.addMapping(MAPPING_ROLL1_ROTATE,  TRIGGER_ROLL1_ROTATE);
        inputManager.addMapping(MAPPING_ROLL2_ROTATE,  TRIGGER_ROLL2_ROTATE);
        inputManager.addMapping(MAPPING_ROLL3_ROTATE,  TRIGGER_ROLL3_ROTATE);
        inputManager.addMapping(MAPPING_PITCH1_ROTATE, TRIGGER_PITCH1_ROTATE);
        inputManager.addMapping(MAPPING_PITCH2_ROTATE, TRIGGER_PITCH2_ROTATE);
        inputManager.addMapping(MAPPING_PITCH3_ROTATE, TRIGGER_PITCH3_ROTATE);

        inputManager.addMapping(MAPPING_SHUFFLE,           TRIGGER_SHUFFLE);
        inputManager.addMapping(MAPPING_SOLVE,             TRIGGER_SOLVE);
        inputManager.addMapping(MAPPING_NEXT_SOLVING_PATH, TRIGGER_NEXT_SOLVING_PATH);

        // init Listener
        stateManager.getState(FlyCamAppState.class).setEnabled(false); // disable default key input (WASD ...)
        inputManager.addListener(analogListener, MAPPING_PITCH_ROTATE, MAPPING_ROLL_ROTATE, MAPPING_YAW_ROTATE);
        inputManager.addListener(actionListener, MAPPING_YAW1_ROTATE, MAPPING_YAW2_ROTATE, MAPPING_YAW3_ROTATE, MAPPING_ROLL1_ROTATE, MAPPING_ROLL2_ROTATE, MAPPING_ROLL3_ROTATE, MAPPING_PITCH1_ROTATE, MAPPING_PITCH2_ROTATE, MAPPING_PITCH3_ROTATE);
        inputManager.addListener(actionListener, MAPPING_PICK_CUBIE);
        inputManager.addListener(actionListener, MAPPING_SHUFFLE, MAPPING_SOLVE, MAPPING_NEXT_SOLVING_PATH);

        // init mouse target picker
        inputManager.setCursorVisible(true);
        flyCam.setDragToRotate(true);

    }

    private void initRubiksCubeNodes() {
        rootNode.detachChildNamed("pivot");
        rubiksCubeNode = new Node("pivot");
        rootNode.attachChild(rubiksCubeNode);

        for (Cubie cubie : rubiksCube.getAllCubies()) {
            Node nCubie = createCubieV2(cubie);
            rubiksCubeNode.attachChild(nCubie);
        }
    }

    /* Use the main event loop to trigger repeating actions. */
    @Override
    public void simpleUpdate(float tpf) {
        // Handle rotation animation
        if (currentRotation != null && currentRotation.isOnGoing()) {

            boolean isRotationEnded = currentRotation.processRotation(tpf);

            if (isRotationEnded) {
                this.rubiksCube.move(currentRotation.getDefinedMove());
                currentRotation = null;

                if (LOGGER.isDebugEnabled()) {
                    new RubiksCubeAsciiFormat(this.rubiksCube).show();
                }
            }
        }
    }

    // Create Cubie geometry by creating 6 facelet using following method
    private Node createCubieV2(Cubie cubie) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(cubie.toString());
        }

        Node nCubie = new Node("cubie_" + cubie.toString());

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

        float offset = getDisplayOffsetToCenterRubiksCube();

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

    private float getDisplayOffsetToCenterRubiksCube() {
        return RubiksCube3DUtility.getDisplayOffsetToCenterRubiksCube(this.rubiksCube.getSize());
    }

    // FIXME: refactor to Facelet enum
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
