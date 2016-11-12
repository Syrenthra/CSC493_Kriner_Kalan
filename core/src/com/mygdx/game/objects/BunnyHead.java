package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.Assets;
import com.mygdx.util.Constants;
import com.mygdx.util.CharacterSkin;
import com.mygdx.util.GamePreferences;
import com.mygdx.util.AudioManager;
import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * The player character's main class where motion updates and drawing are done
 * @author Kalan Kriner
 */
public class BunnyHead extends AbstractGameObject
{
    public static final String Tag = BunnyHead.class.getName();
    
    public ParticleEffect dustParticles = new ParticleEffect();
    
    private final float JUMP_TIME_MAX = 0.3f;
    private final float JUMP_TIME_MIN = 0.1f;
    private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.018f;
    
    private Animation animNormal;
    private Animation animCopterTransform;
    private Animation animCopterTransformBack;
    private Animation animCopterRotate;
    
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
    public boolean hasFeatherPowerup;
    public float timeLeftFeatherPowerup;
    
    public BunnyHead()
    {
        init();
    }
    
    /**
     * Sets the bunny head image, dimensions and the physics information
     */
    public void init()
    {
        dimension.set(1,1);
        animNormal=Assets.instance.bunny.animNormal;
        animCopterTransform=Assets.instance.bunny.animCopterTransform;
        animCopterTransformBack=Assets.instance.bunny.animCopterTransformBack;
        animCopterRotate=Assets.instance.bunny.animCopterRotate;
        setAnimation(animNormal);
        
        // Center image on game object
        origin.set(dimension.x/2, dimension.y/2);
        // Bounding box for collision detection
        bounds.set(0,0, dimension.x, dimension.y);
        // Set physics values
        terminalVelocity.set(3.0f, 4.0f);
        friction.set(12.0f, 0.0f);
        acceleration.set(0.0f,-25.0f);
        // View Direction
        viewDirection = VIEW_DIRECTION.RIGHT;
        // Jump state
        jumpState = JUMP_STATE.FALLING;
        timeJumping=0;
        // Power-ups
        hasFeatherPowerup = false;
        timeLeftFeatherPowerup = 0;
        
        //Particles
        dustParticles.load(Gdx.files.internal("particles/dust.pfx"), Gdx.files.internal("particles"));
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
                AudioManager.instance.play(Assets.instance.sounds.jump);
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
            if(jumpKeyPressed && hasFeatherPowerup)
            {
                AudioManager.instance.play(Assets.instance.sounds.jumpWithFeather, 1, MathUtils.random(1.0f, 1.1f));
                timeJumping = JUMP_TIME_OFFSET_FLYING;
                jumpState = JUMP_STATE.JUMP_RISING;
            }
            break;
        }
    }
    
    /**
     * Bunny head is told if a feather was picked up
     * @param pickedUp true or false for if a feather is picked up
     */
    public void setFeatherPowerup(boolean pickedUp)
    {
        hasFeatherPowerup = pickedUp;
        if(pickedUp)
        {
            timeLeftFeatherPowerup = Constants.ITEM_FEATHER_POWERUP_DURATION;
        }
        
    }
    
    /**
     * Checks for powerup status and the time remaining
     * @return If a powerup was picked up and is still active
     */
    public boolean hasFeatherPowerup()
    {
        return hasFeatherPowerup && timeLeftFeatherPowerup >0;
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
        
        if(timeLeftFeatherPowerup > 0)
        {
            if(animation == animCopterTransformBack)
            {
                //Restart "Transform" animation if another feather power-up was picked up
                //during "TransformBack" animation. Otherwise the "TransformBack" animation
                //would be stuck while the power-up is still active.
                setAnimation(animCopterTransform);
            }
            timeLeftFeatherPowerup -= deltaTime;
            if(timeLeftFeatherPowerup < 0)
            {
                // Disable power-up
                timeLeftFeatherPowerup =0;
                setFeatherPowerup(false);
                setAnimation(animCopterTransformBack);
            }
        }
        dustParticles.update(deltaTime);
        
        //Change animation state according to feather power-up
        if(hasFeatherPowerup)
        {
            if(animation == animNormal)
            {
                setAnimation(animCopterTransform);
            }
            else if(animation == animCopterTransform)
            {
                if(animation.isAnimationFinished(stateTime))
                {
                    setAnimation(animCopterRotate);
                }
            }
        }
        else
        {
            if(animation == animCopterRotate)
            {
                if(animation.isAnimationFinished(stateTime))
                {
                    setAnimation(animCopterTransformBack);
                }
            }
            else if(animation == animCopterTransformBack)
            {
                if(animation.isAnimationFinished(stateTime))
                    setAnimation(animNormal);
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
            if(velocity.x!=0)
            {
                dustParticles.setPosition(position.x +dimension.x/2, position.y);
                dustParticles.start();
            }
            break;
        case JUMP_RISING:
            // Keep track of jump time
            timeJumping += deltaTime;
            //Jump time left?
            if(timeJumping <= JUMP_TIME_MAX)
            {
                //Still Jumping
                velocity.y = terminalVelocity.y;
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
                velocity.y =terminalVelocity.y;
            }
        }
        if (jumpState != JUMP_STATE.GROUNDED)
        {
            dustParticles.allowCompletion();
            super.updateMotionY(deltaTime);
        }
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
        
        float dimCorrectionX = 0;
        float dimCorrectionY = 0;
        if(animation !=animNormal)
        {
            dimCorrectionX = 0.05f;
            dimCorrectionY = 0.2f;
        }
        
        // Set special color when game object has a feather power-up
        if(hasFeatherPowerup)
        {
            batch.setColor(1.0f, 0.8f, 0.0f, 1.0f);
        }
        
        //Draw image
        reg= animation.getKeyFrame(stateTime,true);
        batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x+dimCorrectionX, 
                dimension.y+ dimCorrectionY,scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
                reg.getRegionWidth(), reg.getRegionHeight(), viewDirection == VIEW_DIRECTION.LEFT,false);
        
        //Reset Color to white
        batch.setColor(1, 1, 1, 1);
    }
}
