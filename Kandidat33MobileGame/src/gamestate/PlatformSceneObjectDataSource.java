/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestate;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import variables.P;

/**
 *
 * @author dagen
 */
public class PlatformSceneObjectDataSource implements SceneObjectDataSource{
        AssetManager assetManager;
    
    private static int counter = 0;
            
    public PlatformSceneObjectDataSource(AssetManager assetManager){
        this.assetManager = assetManager;
    }
    public Spatial getSceneObject(){
        Box model = new Box(
                Vector3f.ZERO, 
                P.platformLength , 
                P.platformHeight, 
                P.platformWidth);
        
        Geometry geometry = new Geometry("Platform" , model);
        
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        ColorRGBA color = ColorRGBA.Blue;
        material.setColor("Color", color);
        
        geometry.setMaterial(material);
        //geometry.setLocalTranslation(0, 0 - 0.1f, 0);
        return geometry;
    }
}
