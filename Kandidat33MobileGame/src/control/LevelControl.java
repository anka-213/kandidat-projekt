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
import spatial.Platform;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import spatial.Wall;
import variables.P;

/**
 * A class controlling the entire level – the background and the platforms.
 * Usage: attach to a Node called level and leave it running while the game
 * is running.
 * @author jonatankilhamn
 */
public class LevelControl implements Control {

    
    private Node levelNode;
    private LinkedList<Node> chunks;
    private AssetManager assetManager;
    private PhysicsSpace physicsSpace;
    private Spatial player;
    
    /**
     * Creates a new LevelControl.
     * 
     */
    public LevelControl(AssetManager assetManager, PhysicsSpace physicsSpace,
            Spatial player) {
        this.assetManager = assetManager;
        this.physicsSpace = physicsSpace;
        this.player = player;
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
        if (levelNode != null) {
            levelNode.detachAllChildren();
        }
        this.levelNode = (Node) spatial;
        generateStartingChunks();
    }

    /**
     * Checks if any chunk of the level is outside the view and needs moving.
     * If so, performs that move.
     * @param tpf 
     */
    public void update(float tpf) {
        if (this.player.getLocalTranslation().getX() >
                chunks.getFirst().getLocalTranslation().getX() + 60) {
            deleteChunk(chunks.removeFirst());
            generateNextChunk();
        }
    }
    
    private void deleteChunk(Node chunk) {
        removeChunkFromPhysicsSpace(chunk);
        levelNode.detachChild(chunk);
    }
    
    private void generateStartingChunks() {
        chunks = new LinkedList<Node>();
        // generate 5 chunks
        for (int i = 0; i<10; i++){
            generateNextChunk();
        }
    }
    
    /**
     * Generate a new chunk of the level, placing it directly after the
     * last chunk.
     * In the current implementation, a chunk is simply one platform.
     * @pre generateStartingChunks has been run once.
     * @return 
     */
    private Node generateNextChunk() {

        // generate the node to attach everything to
        Node chunk = new Node();

        // find the x position to place the new chunk in
        float xPos;
        if (chunks.isEmpty())  {
            xPos = -3;
        } else {
            xPos = this.chunks.getLast().getLocalTranslation().getX() + P.chunkLength;
        }

        // generate a new chunk position
        Random random = new Random();
        int randomNumber = (random.nextInt(6) - 3);
        Vector3f newChunkPosition =
                new Vector3f(xPos, randomNumber, 0f);
        
        // generate one platform
        Platform platform = new Platform(this.assetManager);
        
        // generate the background wall
        Wall wall = new Wall(this.assetManager);
        
        // attach everything physical to the node
        chunk.attachChild(platform);
        addChunkToPhysicsSpace(chunk);
        // attach everything else to the node
        chunk.attachChild(wall);
        
        moveChunkTo(chunk, newChunkPosition);
        
        levelNode.attachChild(chunk);
        chunks.addLast(chunk);
        return chunk;
    }
    
    /**
     * Moves one chunk to another position.
     * Use this instead of simply chunk.setLocalTranslation in order to
     * keep any physics objects in the chunk from decoupling with the physics
     * space.
     * @param chunk The chunk to move
     * @param position The position to move it to, relative to the node this
     * LevelControl is attached to.
     */
    private void moveChunkTo(Node chunk, Vector3f position) {
        disablePhysicsOfChunk(chunk);
        chunk.setLocalTranslation(position);
        enablePhysicsOfChunk(chunk);
    }
    
    /**
     * Adds all objects with a PhysicsControl in a chunk to the PhysicsSpace.
     * "In a chunk" is the same as "attached to a Node".
     * @param chunk 
     */
    private void addChunkToPhysicsSpace(Node chunk) {
        // traverse the scenegraph starting from the chunk node
        chunk.depthFirstTraversal(new SceneGraphVisitor() {
            public void visit(Spatial spatial) {
                physicsSpace.addAll(spatial);
            }
        });
    }

    /**
     * Removes all objects with a PhysicsControl in a chunk from the
     * PhysicsSpace. "In a chunk" is the same as "attached to a Node".
     *
     * @param chunk 
     */
    private void removeChunkFromPhysicsSpace(Node chunk) {
        // traverse the scenegraph starting from the chunk node
        chunk.depthFirstTraversal(new SceneGraphVisitor() {
            public void visit(Spatial spatial) {
                physicsSpace.removeAll(spatial);
            }
        });
    }   

    /**
     * Disables the physics of all objects in a chunk.
     * "In a chunk" is the same as "attached to a Node".
     * @param chunk 
     */
    private void disablePhysicsOfChunk(Node chunk) {
        // traverse the scenegraph starting from the chunk node
        chunk.depthFirstTraversal(new SceneGraphVisitor() {
            public void visit(Spatial spatial) {
                // get the PhysicsControl if there is any
                PhysicsControl physicsControl = spatial.getControl(PhysicsControl.class);
                if (physicsControl != null) {
                    physicsControl.setEnabled(false);
                }
            }
        });
    }

    /**
     * Enables the physics of all objects in a chunk.
     * "In a chunk" is the same as "attached to a Node".
     * @param chunk 
     */
    private void enablePhysicsOfChunk(Node chunk) {
        // traverse the scenegraph starting from the chunk node
        chunk.depthFirstTraversal(new SceneGraphVisitor() {
            public void visit(Spatial spatial) {
                // get the PhysicsControl if there is any
                PhysicsControl physicsControl = spatial.getControl(PhysicsControl.class);
                if (physicsControl != null) {
                    physicsControl.setEnabled(true);
                }
            }
        });
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
