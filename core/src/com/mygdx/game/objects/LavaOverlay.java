package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.Assets;

/**
 * Handles the placement of the lava overlay along the bottom of the map
 * 
 * @author Kalan Kriner
 */
public class LavaOverlay extends AbstractGameObject
{

    private TextureRegion regWaterOverlay;
    private float length;
    
    public LavaOverlay(float length)
    {
        this.length=length;
        init();
    }
    
    /**
     * Sets up the overlay to be longer than the map and fit the height
     */
    private void init()
    {
        dimension.set(length*10,3);
        
        regWaterOverlay=Assets.instance.levelDecoration.lavaOverlay;
        
        origin.x= -dimension.x /2;
    }
    
    /**
     * Draws the overlay onto the game
     */
    @Override
    public void render(SpriteBatch batch)
    {
        TextureRegion reg=null;
        reg=regWaterOverlay;
        batch.draw(reg.getTexture(), position.x+origin.x, position.y +origin.y, origin.x, origin.y,
                dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(),reg.getRegionY(),
                reg.getRegionWidth(),reg.getRegionHeight(),false,false);
        
    }

}
