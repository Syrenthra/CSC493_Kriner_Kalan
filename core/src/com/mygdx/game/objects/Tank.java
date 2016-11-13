package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.mygdx.game.Assets;
import com.mygdx.util.Constants;
import com.mygdx.util.GamePreferences;
import com.mygdx.util.AudioManager;
import com.mygdx.util.CharacterSkin;

/**
 * Player character that moves and jumps
 * @author Kalan Kriner
 */
public class Tank extends AbstractGameObject
{
    public static final String TAG = Tank.class.getName();
    
    public ParticleEffect dustParticles = new ParticleEffect();
    
    private final float JUMP_TIME_MAX = 0.1f;
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

    private TextureRegion animPause;
    private boolean stopped;
    private Animation animRight;
    
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
     * Sets the character image, dimensions and the physics information
     */
    public void init()
    {
        dimension.set(1,1);
        animRight=Assets.instance.tank.animRight;
        stopped=false;
        
        setAnimation(animRight);
        // Center image on game object
        origin.set(dimension.x/2, dimension.y/2);
        // Bounding box for collision detection
        bounds.set(0,0, dimension.x, dimension.y);
        // Set physics values
        terminalVelocity.set(3.5f, 7.0f);
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
        
        //Particles
        dustParticles.load(Gdx.files.internal("particles/dust.pfx"), Gdx.files.internal("particles"));
    }
    
    /**
     * Updates the player to jump if pressed
     * @param jumpKeyPressed Tells if the jump key was pressed
     * @param deltaTime to know how long the key was pressed for
     */
    public void setJumping(boolean jumpKeyPressed,float deltaTime)
    {
        if(jumpKeyPressed)
        {
            dustParticles.allowCompletion();
            // Keep track of jump time
            timeJumping += deltaTime;
            //Jump time left?
            if(timeJumping <= JUMP_TIME_MAX)
            {
                //Still Jumping
                velocity.y=terminalVelocity.y;
                body.setLinearVelocity(velocity);
                position.set(body.getPosition());
                if(hasBarrelPowerup)
                {
                    velocity.y=velocity.y;
                }
            }
        }
    }
    
    public void resetJump()
    {
        if(timeJumping>0)
        {
            timeJumping=0;
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
        if (body != null)
        {
            //Gdx.app.log(TAG, "velY: "+velocity.y+" state: "+jumpState);
            body.setLinearVelocity(velocity);
            position.set(body.getPosition());
        }

        if(body.getLinearVelocity().x!=0)
        {
            viewDirection = body.getLinearVelocity().x < 0 ? VIEW_DIRECTION.LEFT : VIEW_DIRECTION.RIGHT;
            dustParticles.start();
            stopped=false;
        }
        //If the tank is stopped, set a paused animation
        else
        {
            //If the tank has been paused, we don't get a new frame
            if(stopped!=true)
                animPause=animation.getKeyFrame(stateTime);
            stopped=true;
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
        dustParticles.setPosition(position.x + dimension.x /2, position.y);
        dustParticles.update(deltaTime);
    }
    
    /**
     * Draws the character normally, unless the powerup was picked up then a special color is used.
     */
    @Override
    public void render(SpriteBatch batch)
    {
        TextureRegion reg = null;
        
        //Draw Particles
        dustParticles.draw(batch);
        
        //Apply Skin Color
        batch.setColor(CharacterSkin.values()[GamePreferences.instance.charSkin].getColor());

        
        // Set special color when game object has a barrel power-up
        if(hasBarrelPowerup)
        {
            batch.setColor(1.0f, 0.8f, 0.0f, 1.0f);
        }
        
        //Draw image
        if(stopped)
        {
            reg=animPause;
        }
        else
        {
            reg= animation.getKeyFrame(stateTime, true);
        }
        batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y,
                scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
                viewDirection == VIEW_DIRECTION.LEFT,false);
        
        //Reset Color to white
        batch.setColor(1, 1, 1, 1);
    }
}
