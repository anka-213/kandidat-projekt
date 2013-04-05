package control.fireball;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.Vector3f;
import control.HazardControl;
import control.PlayerControl;
import spatial.Player;

/**
 *
 * @author jonatankilhamn
 */
public abstract class FireballControl extends GhostControl implements HazardControl {
        
    public FireballControl(){
        super(new SphereCollisionShape(1f));
    }
    
    public void collideWithPlayer(Player player) {
            player.getControl(PlayerControl.class).pushBack();
    }
    
    @Override
    public void update(float tpf){
        super.setEnabled(false);
        positionUpdate(tpf);
        super.setEnabled(true);
    }
    
    public void move(Vector3f translation) {
        super.setEnabled(false);
        spatial.setLocalTranslation(spatial.getLocalTranslation().add(translation));
        super.setEnabled(true);
    }

    protected abstract void positionUpdate(float tpf);
    
}