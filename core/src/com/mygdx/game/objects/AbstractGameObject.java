package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Holds all the common functionalities and attributes between all of the game objects
 * 
 * @author Kalan Kriner
 */
public abstract class AbstractGameObject
{
    public Vector2 position;
    public Vector2 dimension;
    public Vector2 origin;
    public Vector2 scale;
    public float rotation;
    
    public Vector2 velocity;
    public Vector2 terminalVelocity;
    public Vector2 friction;
    
    public Vector2 acceleration;
    public Rectangle bounds;
    
    public Body body;
    
    public AbstractGameObject()
    {
        position = new Vector2();
        dimension = new Vector2(1,1);
        origin = new Vector2();
        scale = new Vector2(1,1);
        rotation =0;
        velocity = new Vector2();
        terminalVelocity= new Vector2(1,1);
        friction = new Vector2();
        acceleration = new Vector2();
        bounds = new Rectangle();
    }
    
    /**
     * Updates the motion of the object
     * @param deltaTime time since last update
     */
    public void update(float deltaTime)
    {
        if(body==null)
        {
            updateMotionX(deltaTime);
            updateMotionY(deltaTime);
            //Move to new position
            position.x += velocity.x * deltaTime;
            position.y += velocity.y * deltaTime;
        } 
        else
        {
            position.set(body.getPosition());
            rotation = body.getAngle() * MathUtils.radiansToDegrees;
        }
    }
    
    /**
     * Each object needs to implement its own rendering method
     * @param batch used for drawing
     */
    public abstract void render(SpriteBatch batch);
    
    /**
     * Updates the horizontal motion
     * @param deltaTime time since last update
     */
    protected void updateMotionX(float deltaTime)
    {
        if(velocity.x!=0)
        {
            //apply friction
            if(velocity.x >0)
            {
                velocity.x = Math.max(velocity.x-friction.x * deltaTime, 0);
            }
            else
            {
                velocity.x= Math.min(velocity.x + friction.x * deltaTime, 0);
            }
        }
        
        //Apply Acceleration
        velocity.x +=acceleration.x*deltaTime;
        //Make sure object's velocity does not exceed the positive or negative terminal velocity
        velocity.x= MathUtils.clamp(velocity.x, -terminalVelocity.x, terminalVelocity.x);
    }
    
    /**
     * Updates the vertical motion
     * @param deltaTime time since last update
     */
    protected void updateMotionY(float deltaTime)
    {
        if(velocity.y!=0)
        {
            //apply friction
            if(velocity.y >0)
            {
                velocity.y = Math.max(velocity.y - friction.y * deltaTime, 0);
            }
            else
            {
                velocity.y= Math.min(velocity.y + friction.y * deltaTime, 0);
            }
        }
        
        //Apply Acceleration
        velocity.y +=acceleration.y * deltaTime;
        //Make sure object's velocity does not exceed the positive or negative terminal velocity
        velocity.y = MathUtils.clamp(velocity.y, -terminalVelocity.y, terminalVelocity.y);
    }

}
