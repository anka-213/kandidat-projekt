package gamestate;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

/**
 * This class handles all the in-game things
 *
 * @author forssenm
 */
public class InGameState extends AbstractAppState {

    private SimpleApplication app;
    private Node rootNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private InputManager inputManager;
    private ViewPort viewPort;
    private BulletAppState physics;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort = this.app.getViewPort();
        this.physics = this.stateManager.getState(BulletAppState.class);
        
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            //Initiate the things that are needed when the state is active
            System.out.println("InGameState is now active");
        } else {
            //Remove the things not needed when the state is inactive
            System.out.println("InGameState is now inactive");
      }
    }
 
    @Override
    public void update(float tpf) {
        
    }
}
