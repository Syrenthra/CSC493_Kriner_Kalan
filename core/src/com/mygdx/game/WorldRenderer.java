package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.util.Constants;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 *  @author Kalan Kriner
 *  
 *  World Renderer is where the game's rendering is handled with calling
 *  updates and initializations. 
 */

public class WorldRenderer implements Disposable
{
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private WorldController worldController;
    
    /**
     * Constructor for WorldRenderer to get its WorldController and initialize
     * @param worldController worldController to work with in game
     */
    public WorldRenderer(WorldController worldController)
    {
        this.worldController = worldController;
        init();
    }
private OrthographicCamera cameraGUI;
    
    /**
     * Allocates the sprite batch for use with rendering and creates and centers the camera
     */
    private void init()
    {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        camera.position.set(0, 0, 0);
        camera.update();
        cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH,Constants.VIEWPORT_GUI_HEIGHT);
        cameraGUI.position.set(0,0,0);
        cameraGUI.setToOrtho(true);  //Flip y-axis
        cameraGUI.update();
    }


    /**
     * Calls all of the methods that are used for rendering specific objects
     */
    public void render()
    {
        renderWorld(batch);
        renderGui(batch);
    }
    
    /**
     * Draws the level that is loaded from the file
     * @param batch Group of assets to be rendered
     */
    private void renderWorld(SpriteBatch batch)
    {
        worldController.cameraHelper.applyTo(camera);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        worldController.level.render(batch);
        batch.end();
    }
    
    /**
     * Draws the GUI score in the top left
     * @param batch Group of assets to be rendered
     */
    private void renderGuiScore(SpriteBatch batch)
    {
        float x = -15;
        float y = -15;
        batch.draw(Assets.instance.crate.crate, x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0);
        Assets.instance.fonts.defaultBig.draw(batch, ""+ worldController.score, x+75,y+37);
    }
    
    /**
     * Draws the extra live icons in the top right edge
     * @param batch Group of assets to be rendered
     */
    private void renderGuiExtraLive(SpriteBatch batch)
    {
        float x= cameraGUI.viewportWidth - 50 - Constants.LIVES_START * 50;
        float y= -15;
        for(int i=0; i<Constants.LIVES_START;i++)
        {
            if(worldController.lives<=1)
                batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            batch.draw(Assets.instance.tank.tank, x+i*50, y, 50, 50, 120, 100, 0.35f, -0.35f, 0);
            batch.setColor(1, 1, 1, 1);
        }
    }
    
    /**
     * Draws the FPS counter in the bottom right
     * @param batch Group of assets to be rendered
     */
    private void renderGuiFpsCounter(SpriteBatch batch)
    {
        float x = cameraGUI.viewportWidth -55;
        float y = cameraGUI.viewportHeight -15;
        int fps = Gdx.graphics.getFramesPerSecond();
        BitmapFont fpsFont = Assets.instance.fonts.defaultNormal;
        if(fps >=45)
        {
            //45 or more fps shows up in green
            fpsFont.setColor(0, 1, 0, 1);
        }
        else if(fps >=30)
        {
            //30 or more fps shows up in yellow
            fpsFont.setColor(1, 1, 0, 1);
        }
        else
        {
            //Less than 30 FPS shows up in red
            fpsFont.setColor(1, 0, 0, 1);
        }
        fpsFont.draw(batch, "FPS: " + fps, x, y);
        fpsFont.setColor(1, 1, 1, 1); //white
        
    }
    
    /**
     * Calls all of the render methods involving the GUI on the game screen
     * @param batch Group of assets to be rendered
     */
    private void renderGui(SpriteBatch batch)
    {
        batch.setProjectionMatrix(cameraGUI.combined);
        batch.begin();
        //Draw collected gold coins icon + text (anchored to top left edge)
        renderGuiScore(batch);
        //Draw extra lives icon + text (anchored to top right edge)
        renderGuiExtraLive(batch);
        //Draw FPS text (anchored to bottom right edge)
        renderGuiFpsCounter(batch);
        batch.end();
        
    }

    /**
     * Handles changing the camera when the window is resized
     * @param width new width of window
     * @param height new height of window
     */
    public void resize(int width, int height)
    {
        camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width;
        camera.update();
        
        cameraGUI.viewportHeight=Constants.VIEWPORT_GUI_HEIGHT;
        cameraGUI.viewportWidth=(Constants.VIEWPORT_GUI_HEIGHT/(float)height) *(float) width;
        cameraGUI.position.set(cameraGUI.viewportWidth/2,cameraGUI.viewportHeight/2,0);
        cameraGUI.update();

    }

    /**
     * Disposes of objects when told to
     */
    @Override
    public void dispose()
    {
        batch.dispose();
    }

}
