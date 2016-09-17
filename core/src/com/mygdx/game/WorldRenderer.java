package com.mygdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.util.Constants;

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
    
    /**
     * Allocates the sprite batch for use with rendering and creates and centers the camera
     */
    private void init()
    {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        camera.position.set(0, 0, 0);
        camera.update();
    }

    /**
     * Calls all of the methods that are used for rendering specific objects
     */
    public void render()
    {
        renderTestObjects();
    }

    /**
     * Renders test sprites that are squares with X's in them
     */
    private void renderTestObjects()
    {
        worldController.cameraHelper.applyTo(camera);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Sprite sprite : worldController.testSprites)
        {
            sprite.draw(batch);
        }
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
    }

    /**
     * Disposes of objects when told to, in this case our sprites
     */
    @Override
    public void dispose()
    {
        batch.dispose();
    }

}
