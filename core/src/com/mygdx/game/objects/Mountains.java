package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Assets;

/**
 * Handles the layout and drawing of the mountains for the background
 * 
 * @author Kalan Kriner
 */
public class Mountains extends AbstractGameObject
{
    private TextureRegion regMountainLeft;
    private TextureRegion regMountainRight;
    private int length;
    
    /**
     * Sets the length of the mountain ranges to be made across the screen
     * @param length of the mountain range
     */
    public Mountains(int length)
    {
        this.length=length;
        init();
    }
    
    /**
     * Initializes the variables to the size of the mountain asset
     */
    private void init()
    {
        dimension.set(10,2);
        
        regMountainLeft=Assets.instance.levelDecoration.mountain;
        regMountainRight=Assets.instance.levelDecoration.mountain;

        //Shift mountain and extend length
        origin.x = -dimension.x*2;
        length+= dimension.x*2;
    }
    
    /**
     * Draws the mountains end to end and with a specific color and offset
     * @param batch group to be drawn with
     * @param offsetX distance to be offset from other mountain chains
     * @param offsetY height difference between mountain chains
     * @param tintColor color of mountains
     */
    private void drawMountain(SpriteBatch batch, float offsetX,float offsetY, float tintColor, float parallaxSpeedX)
    {
        TextureRegion reg= null;
        batch.setColor(tintColor,tintColor,tintColor,1);
        float xRel= dimension.x*offsetX;
        float yRel= dimension.y*offsetY;
        
        //Mountains span the whole level
        int mountainLength=0;
        mountainLength+= MathUtils.ceil(length/(2*dimension.x)*(1-parallaxSpeedX));
        mountainLength+= MathUtils.ceil(0.5f+offsetX);
        for(int i=0;i<mountainLength;i++)
        {
            //Left mountain
            reg=regMountainLeft;
            batch.draw(reg.getTexture(), origin.x+xRel + position.x * parallaxSpeedX, position.y+ origin.y+ yRel, origin.x, origin.y, dimension.x,
                    dimension.y, scale.x, scale.y, rotation,reg.getRegionX(),reg.getRegionY(),reg.getRegionWidth(),
                    reg.getRegionHeight(),false,false);
            xRel+=dimension.x;
            
            //Right mountain
            reg=regMountainRight;
            batch.draw(reg.getTexture(), origin.x+xRel +position.x * parallaxSpeedX, position.y+ origin.y+ yRel, origin.x, origin.y, dimension.x,
                    dimension.y, scale.x, scale.y, rotation,reg.getRegionX(),reg.getRegionY(),reg.getRegionWidth(),
                    reg.getRegionHeight(),false,false);
            xRel+=dimension.x;
        }
        
        //Reset color to white
        batch.setColor(1,1,1,1);
    }
    
    /**
     * Updates the position of the camera's effect on the mountains
     * @param camPosition position
     */
    public void updateScrollPosition(Vector2 camPosition)
    {
        position.set(camPosition.x, position.y);
    }
    
    
    /**
     * Calls the draw mountain method with different colors and offsets to create a more dynamic scene
     */
    @Override
    public void render(SpriteBatch batch)
    {
        //Distant mountains (dark gray)
        drawMountain(batch,0.5f,0.2f,0.5f,0.8f);
        //Distant mountains (gray)
        drawMountain(batch,0.25f,0.1f,0.25f,0.5f);
        //Distant mountains(light gray)
        drawMountain(batch,0.0f,0.0f,0.9f,0.3f);
        
    }
    

}
