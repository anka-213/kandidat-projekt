/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import leveldata.ChunkFactory;
import spatial.LevelChunk;
import leveldata.LevelContentGenerator;
import variables.P;

/**
 * A class controlling the entire level – the background and the platforms.
 * Usage: attach to a Node called level and leave it running while the game is
 * running.
 *
 * @author jonatankilhamn
 */
public class LevelControl implements Control {

    private Node gameNode;
    private Node movingObjectsNode;
    private LinkedList<LevelChunk> chunks;
    private AssetManager assetManager;
    private PhysicsSpace physicsSpace;
    private Spatial player;
    private ChunkFactory chunkFactory;

    /**
     * Creates a new LevelControl.
     *
     */
    public LevelControl(AssetManager assetManager, PhysicsSpace physicsSpace,
            Spatial player) {
        this.assetManager = assetManager;
        this.physicsSpace = physicsSpace;
        this.player = player;
        this.chunkFactory = new ChunkFactory(assetManager);
    }

    /**
     * Sets the spatial of this control. This spatial is used for reference
     * position and should not be moved.
     *
     * @param spatial The spatial to attach the entire level to - must be a
     * Node!
     */
    public void setSpatial(Spatial spatial) {
        assert (spatial instanceof Node);
        if (gameNode != null) {
            gameNode.detachAllChildren();
        }
        this.gameNode = (Node) spatial;
        this.movingObjectsNode = new Node();
        gameNode.attachChild(this.movingObjectsNode);
        generateStartingChunks();
    }

    /**
     * Checks if any chunk of the level is outside the view and needs moving. If
     * so, performs that move.
     *
     * @param tpf
     */
    public void update(float tpf) {
        final float destructionPoint = this.player.getLocalTranslation().getX() - 60;
        if (destructionPoint
                > chunks.getFirst().getLocalTranslation().getX() + P.chunkLength) {
            deleteChunk(chunks.removeFirst());
            generateNextChunk();
        }
        for (Spatial spatial : movingObjectsNode.getChildren()) {
            if (destructionPoint > spatial.getLocalTranslation().getX()) {
                movingObjectsNode.detachChild(spatial);
            }
        }
    }

    private void deleteChunk(LevelChunk chunk) {
        physicsSpace.removeAll(chunk);
        chunk.detachFromLevelNode();
    }

    private void generateStartingChunks() {
        chunks = new LinkedList<LevelChunk>();
        // generate chunks
        for (int i = 0; i < 5; i++) {
            generateNextChunk();
        }
    }

    /**
     * Generate a new chunk of the level, placing it directly after the last
     * chunk.
     *
     * @return
     */
    private void generateNextChunk() {

        // generate a chunk filled the chunk with content
        List<Spatial> list = chunkFactory.generateChunk();

        // find the x position to place the new chunk in
        float xPos;
        if (chunks.isEmpty()) {
            xPos = -3;
        } else {
            xPos = this.chunks.getLast().getLocalTranslation().getX() + P.chunkLength;
        }
        Vector3f newChunkPosition =
                new Vector3f(xPos, 0f, 0f);

        // static objects:
        LevelChunk staticObjects = (LevelChunk) list.remove(0);
        // connect to the physics space
        this.physicsSpace.addAll(staticObjects);
        // place static objects in the right place
        staticObjects.setLocalTranslation(newChunkPosition);
        // attach to scenegraph
        staticObjects.attachToLevelNode(gameNode);

        staticObjects.depthFirstTraversal(new SceneGraphVisitor() {
            public void visit(Spatial spatial) {
                int nbrOfCtrls = spatial.getNumControls();
                for (int i = 0; i < nbrOfCtrls; i++) {
                    if (spatial.getControl(i) instanceof LevelContentGenerator) {
                        ((LevelContentGenerator) spatial.getControl(i)).setLevelControl(LevelControl.this);
                    }
                }
            }
        });

        // the list now only contains moving objects:
        for (Spatial spatial : list) {
            // for the static objects, this is done in LevelChunk.setLocalTranslation
            // for these objects, we must do it ourselves:
            PhysicsControl physicsControl = spatial.getControl(PhysicsControl.class);
            if (physicsControl != null) {
                physicsControl.setEnabled(false);
                spatial.setLocalTranslation(newChunkPosition);
                physicsControl.setEnabled(true);
            } else {
                spatial.setLocalTranslation(newChunkPosition);
            }
            physicsSpace.addAll(spatial);
            movingObjectsNode.attachChild(spatial);
        }

        chunks.addLast(staticObjects);
    }

    public void addToLevel(Spatial spatial, final Vector3f position) {

        // physics-secure movement to the position where it's added
        PhysicsControl physicsControl = spatial.getControl(PhysicsControl.class);
        if (physicsControl != null) {
            physicsControl.setEnabled(false);
            spatial.setLocalTranslation(position);
            physicsControl.setEnabled(true);
        } else {
            spatial.setLocalTranslation(position);
        }

        /*
         * Give references to the LevelControl to anyone who wants to
         * bring friends (other spatials) to the level
         */
        int nbrOfCtrls = spatial.getNumControls();
        for (int i = 0; i < nbrOfCtrls; i++) {
            Control control = spatial.getControl(i);
            if (control instanceof LevelContentGenerator) {
                ((LevelContentGenerator) control).setLevelControl(LevelControl.this);
            }
        }



        physicsSpace.addAll(spatial);
        movingObjectsNode.attachChild(spatial);
    }

    public void render(RenderManager rm, ViewPort vp) {
    }

    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void write(JmeExporter ex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void read(JmeImporter im) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
