package com.mygdx.game.objects;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.Assets;

/**
 * Goal the character has to get to
 * @author Kalan Kriner
 */
public class Goal extends AbstractGameObject
{
    private TextureRegion regGoal;

    public Goal()
    {
        init();
    }
    
    /**
     * Initializes the goal to have a bounding box and texture
     */
    private void init()
    {
        dimension.set(4.0f, 2.0f);
        
        regGoal = Assets.instance.levelDecoration.goal;
        
        //Set Bounding box for collision detection
        bounds.set(0, 0, dimension.x, dimension.y);
        origin.set(dimension.x /2.0f, dimension.y/2f);
    }
    
    /**
     * Renders the goal
     * @param batch used for drawing
     */
    public void render(SpriteBatch batch)
    {
        TextureRegion reg=null;
        
        reg=regGoal;
        batch.draw(reg.getTexture(), position.x-origin.x, position.y-origin.y, origin.x, origin.y, dimension.x,
                dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(),
                reg.getRegionHeight(), false, false);
    }

}