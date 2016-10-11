package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.Assets;
import com.mygdx.util.Constants;
import com.mygdx.util.GamePreferences;
import com.mygdx.util.CharacterSkin;

/**
 * Player character that moves and jumps
 * @author Kalan Kriner
 */
public class Tank extends AbstractGameObject
{
    public static final String Tag = Tank.class.getName();
    
    private final float JUMP_TIME_MAX = 0.3f;
    private final float JUMP_TIME_MIN = 0.1f;
    private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.018f;
    
    public enum VIEW_DIRECTION 
    {
        LEFT, RIGHT
    }
    
    public enum JUMP_STATE 
    {
        GROUNDED, FALLING, JUMP_RISING, JUMP_FALLING
    }

    private TextureRegion regHead;
    
    public VIEW_DIRECTION viewDirection;
    public float timeJumping;
    public JUMP_STATE jumpState;
    public boolean hasBarrelPowerup;
    public float timeLeftBarrelPowerup;
    
    public Tank()
    {
        init();
    }
    
    /**
     * Sets the bunny head image, dimensions and the physics information
     */
    public void init()
    {
        dimension.set(1,1);
        regHead= Assets.instance.tank.tank;
        // Center image on game object
        origin.set(dimension.x/2, dimension.y/2);
        // Bounding box for collision detection
        bounds.set(0,0, dimension.x, dimension.y);
        // Set physics values
        terminalVelocity.set(3.0f, 8.0f);
        friction.set(12.0f, 0.0f);
        acceleration.set(0.0f,-25.0f);
        // View Direction
        viewDirection = VIEW_DIRECTION.RIGHT;
        // Jump state
        jumpState = JUMP_STATE.FALLING;
        timeJumping=0;
        // Power-ups
        hasBarrelPowerup = false;
        timeLeftBarrelPowerup = 0;
    }
    
    /**
     * Sets the state of the character of grounded, falling, powerup falling or rising
     * @param jumpKeyPressed Tells if the jump key was pressed
     */
    public void setJumping(boolean jumpKeyPressed)
    {
        switch(jumpState)
        {
        case GROUNDED: //Character is standing on a platform
            if(jumpKeyPressed)
            {
                //Start counting jump time from beginning
                timeJumping = 0;
                jumpState = JUMP_STATE.JUMP_RISING;
            }
            break;
        case JUMP_RISING: //Rising in the air
            if (!jumpKeyPressed)
                jumpState = JUMP_STATE.JUMP_FALLING;
            break;
        case FALLING: //Falling down
        case JUMP_FALLING: //Falling down after jump
            break;
        }
    }
    
    /**
     * Bunny head is told if a barrel was picked up
     * @param pickedUp true or false for if a feather is picked up
     */
    public void setBarrelPowerup(boolean pickedUp)
    {
        hasBarrelPowerup = pickedUp;
        if(pickedUp)
        {
            if(timeLeftBarrelPowerup <=0)
                terminalVelocity.x= (float)(terminalVelocity.x *2); //Increases speed, but only once per time limit
            timeLeftBarrelPowerup = Constants.ITEM_BARREL_POWERUP_DURATION;
        }
        else
        {
            terminalVelocity.x= (float) (terminalVelocity.x /2); //Resets terminal velocity
        }
        
    }
    
    /**
     * Checks for powerup status and the time remaining
     * @return If a powerup was picked up and is still active
     */
    public boolean hasBarrelPowerup()
    {
        return hasBarrelPowerup && timeLeftBarrelPowerup >0;
    }

    /**
     * Updates the view direction of the character based on velocity and changes time remaining on powerup
     */
    @Override
    public void update( float deltaTime)
    {
        super.update(deltaTime);
        if(velocity.x!=0)
        {
            viewDirection = velocity.x < 0 ? VIEW_DIRECTION.LEFT : VIEW_DIRECTION.RIGHT;
        }
        
        if(timeLeftBarrelPowerup > 0)
        {
            timeLeftBarrelPowerup -= deltaTime;
            if(timeLeftBarrelPowerup < 0)
            {
                // Disable power-up
                timeLeftBarrelPowerup =0;
                setBarrelPowerup(false);
            }
        }
    }
    
    /**
     * Different actions done for the y motion depending on the state the character is in.
     */
    @Override
    protected void updateMotionY(float deltaTime)
    {
        switch(jumpState)
        {
        case GROUNDED:
            jumpState = JUMP_STATE.FALLING;
            break;
        case JUMP_RISING:
            // Keep track of jump time
            timeJumping += deltaTime;
            //Jump time left?
            if(timeJumping <= JUMP_TIME_MAX)
            {
                //Still Jumping
                velocity.y = terminalVelocity.y;
                if(hasBarrelPowerup)
                {
                    velocity.y=velocity.y;
                }
            }
            break;
        case FALLING:
            break;
        case JUMP_FALLING:
            // Add delta time to track jump time
            timeJumping += deltaTime;
            // Jump to minimal height if jump key was pressed too short
            if(timeJumping>0 && timeJumping <= JUMP_TIME_MIN)
            {
                // Still jumping
                velocity.y = terminalVelocity.y;
                if(hasBarrelPowerup)
                {
                    velocity.y=velocity.y;
                }
            }
        }
        if (jumpState != JUMP_STATE.GROUNDED)
            super.updateMotionY(deltaTime);
    }

    /**
     * Draws the character normally, unless the powerup was picked up then a special color is used.
     */
    @Override
    public void render(SpriteBatch batch)
    {
        TextureRegion reg = null;
        
        //Apply Skin Color
        batch.setColor(CharacterSkin.values()[GamePreferences.instance.charSkin].getColor());

        
        // Set special color when game object has a barrel power-up
        if(hasBarrelPowerup)
        {
            batch.setColor(1.0f, 0.8f, 0.0f, 1.0f);
        }
        
        //Draw image
        reg= regHead;
        batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y,
                scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
                viewDirection == VIEW_DIRECTION.LEFT,false);
        
        //Reset Color to white
        batch.setColor(1, 1, 1, 1);
    }
}
