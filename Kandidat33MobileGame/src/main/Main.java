package main;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import menustate.InMainMenuState;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        stateManager.attach(new InMainMenuState());
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }
    // En annan test kommentar
    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    // BAra skriver en kommentar för att testa push
}
