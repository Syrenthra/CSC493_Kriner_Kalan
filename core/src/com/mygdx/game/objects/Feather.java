package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.Assets;

public class Feather extends AbstractGameObject
{
private TextureRegion regFeather;
    
    public boolean collected;
    
    public Feather()
    {
        init();
    }

    /**
     * Initializes the power up to its image and bounding size
     */
    private void init()
    {
        dimension.set(0.5f,0.5f);
        
        regFeather = Assets.instance.feather.feather;
        
        // Set Bounding box for collision detection
        bounds.set(0,0, dimension.x, dimension.y);
        
        collected = false;
    }
    
    /**
     * Draws the power up if it is not collected
     */
    public void render(SpriteBatch batch)
    {
        if (collected) return;
        
        TextureRegion reg= null;
        reg= regFeather;
        batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y,
                rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false,false);
    }
    
    /**
     * Gives the score of the items
     * @return score to add
     */
    public int getScore()
    {
        return 250;
    }

}
