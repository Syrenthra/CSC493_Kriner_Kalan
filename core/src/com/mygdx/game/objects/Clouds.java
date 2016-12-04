package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Assets;

/**
 * Handles placement of clouds randomly along the map
 * 
 * @author Kalan Kriner
 */
public class Clouds extends AbstractGameObject
{
    private float length;
    
    private Array<TextureRegion> regClouds;
    private Array<Cloud> clouds;
    
    /**
     * Draws the cloud that was created by the Clouds class
     * @author Kalan Kriner
     *
     */
    private class Cloud extends AbstractGameObject
    {
        private TextureRegion regCloud;
        
        public Cloud()
        {
        }
        
        /**
         * Sets to one of the specific clouds
         * @param region cloud to be used
         */
        public void setRegion(TextureRegion region)
        {
            regCloud=region;
        }

        /**
         * Draws the cloud with the given location
         */
        @Override
        public void render(SpriteBatch batch)
        {
            TextureRegion reg = regCloud;
            batch.draw(reg.getTexture(), position.x+origin.x, position.y +origin.y, origin.x, origin.y,
                    dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(),reg.getRegionY(),
                    reg.getRegionWidth(),reg.getRegionHeight(),false,false);
           
        }
    }

    public Clouds(float length)
    {
        this.length=length;
        init();
    }
    
    /**
     * Sets up an array for cloud types to be chosen from and then spawns a set number of clouds
     */
    private void init()
    {
        dimension.set(3.0f,1.5f);
        regClouds= new Array<TextureRegion>();
        regClouds.add(Assets.instance.levelDecoration.cloud01);
        regClouds.add(Assets.instance.levelDecoration.cloud02);
        regClouds.add(Assets.instance.levelDecoration.cloud03);
        
        int distFac=5;
        int numClouds=(int)(length/distFac);
        clouds=new Array<Cloud>(2*numClouds);
        for(int i =0; i<numClouds;i++)
        {
            Cloud cloud= spawnCloud();
            cloud.position.x=i*distFac;
            clouds.add(cloud);
        }
    }
    
    /**
     * Randomly chooses a cloud image and then its location somewhere on the map
     * @return
     */
    private Cloud spawnCloud()
    {
        Cloud cloud = new Cloud();
        cloud.dimension.set(dimension);
        //Select random cloud image
        cloud.setRegion(regClouds.random());
        //Position
        Vector2 pos = new Vector2();
        pos.x=length+10; //position after end of level
        pos.y+=2.75; //base position
        pos.y+=MathUtils.random(0.0f,0.3f) *(MathUtils.randomBoolean() ? 1:-1); // Random additional position
        cloud.position.set(pos);
        // Speed
        Vector2 speed = new Vector2();
        speed.x += 0.5f; //Base speed
        //Random additional speed
        speed.x += MathUtils.random(0.0f, 0.75f);
        cloud.terminalVelocity.set(speed);
        speed.x *= -1; // move left
        cloud.velocity.set(speed);

        return cloud;
    }
    
    /**
     * Draws all of the clouds that were spawned
     */
    @Override
    public void render(SpriteBatch batch)
    {
        for(Cloud cloud:clouds)
        {
            cloud.render(batch);
        }
        
    }
    
    /**
     * Updates all of the clouds movement across the screen
     */
    @Override
    public void update (float deltaTime)
    {
        for(int i = clouds.size -1; i>= 0; i--)
        {
            Cloud cloud = clouds.get(i);
            cloud.update(deltaTime);
            if(cloud.position.x <-10)    
            {
                //Cloud moved outside of world, destroy and spawn new cloud at end of level
                clouds.removeIndex(i);
                clouds.add(spawnCloud());
            }
        }
    }


}
