package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.Assets;
import com.badlogic.gdx.math.MathUtils;

/**
 * Collectible item for score. 
 * @author Kalan Kriner
 */
public class GoldCoin extends AbstractGameObject
{
    private TextureRegion regGoldCoin;
    
    public boolean collected;
    
    /**
     * Constructor calling the initialize method
     */
    public GoldCoin()
    {
        init();
    }

    /**
     * Sets up the score items image and bounds
     */
    private void init()
    {
        dimension.set(0.5f,0.5f);
        
        setAnimation(Assets.instance.goldCoin.animGoldCoin);
        stateTime=MathUtils.random(0.0f, 1.0f);
        
        // Set Bounding box for collision detection
        bounds.set(0,0, dimension.x, dimension.y);
        
        collected = false;
    }
    
    /**
     * Renders the score item if it is not collected
     */
    public void render(SpriteBatch batch)
    {
        if (collected) return;
        
        TextureRegion reg= null;
        reg = animation.getKeyFrame(stateTime, true);
        batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y,
                rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false,false);
    }
    
    /**
     * Gives the score of the items
     * @return score to add
     */
    public int getScore()
    {
        return 100;
    }
}
