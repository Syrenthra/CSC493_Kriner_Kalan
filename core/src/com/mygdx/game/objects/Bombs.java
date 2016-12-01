package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.game.Assets;

/**
 * Bomb item that is dropped around the character that needs to be avoided
 * @author Kalan Kriner
 */
public class Bombs extends AbstractGameObject
{
    public boolean exploded;
    public boolean reset;
    private Animation animExplosion;
    private Animation animBomb;
    
    
    public Bombs()
    {
        init();
    }

    /**
     * Initializes the bomb to its image and bounding size
     */
    private void init()
    {
        dimension.set(0.5f,0.5f);
        animExplosion= Assets.instance.bomb.animExplosion;
        animBomb= Assets.instance.bomb.bomb;
        
        // Set Bounding box for collision detection
        bounds.set(0,0, dimension.x-0.25f, dimension.y-0.1f);
        origin.set(dimension.x/2, dimension.y/2);
        setAnimation(animBomb);
        exploded = false;
    }
    
    /**
     * Draws the bomb until it explodes
     */
    public void render(SpriteBatch batch)
    {
        TextureRegion reg= null;
        //When the bomb has finished its explosion, it is reset to above the character
        if(reset)
            return;
        
        //If the bomb has exploded from contact, the animation is set
        if (exploded)
        {
            //If the animation for the explosion finishes, a blank frame is set till reset
            if(animation.isAnimationFinished(stateTime))
            {
                reg=animExplosion.getKeyFrame(stateTime);
                reset=true;
                return;
            }
            
        }
        
        reg=animation.getKeyFrame(stateTime);
        batch.draw(reg.getTexture(), position.x-origin.x, position.y-origin.y, origin.x, origin.y, dimension.x, dimension.y,
                scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(),
                reg.getRegionHeight(), false,false);
    }
    
    /**
     * Gives the score of the items
     * @return score to add
     */
    public int getScore()
    {
        return -250;
    }
    
    /**
     * Sets the explosion animation
     */
    public void explode()
    {
        if(!exploded)
        {
            setAnimation(animExplosion);
            exploded=true;
        }
    }

}
