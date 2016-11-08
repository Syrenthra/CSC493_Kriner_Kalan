package com.mygdx.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.game.WorldController;
import com.mygdx.game.WorldRenderer;
import com.mygdx.util.GamePreferences;

/**
 * Screen for the game when it is displayed
 * @author Kalan Kriner
 */
public class GameScreen extends AbstractGameScreen
{
    private static final String TAG = GameScreen.class.getName();
    
    private WorldController worldController;
    private WorldRenderer worldRenderer;
    
    private boolean paused;
    
    
    /**
     * Constructor that passes the game object to the super class
     * @param game Game that keeps tracks of screens and can switch between them
     */
    public GameScreen(Game game)
    {
        super(game);
    }
    
    /**
     * Constantly refreshes the screen to black until a touch is detected then draws the game
     */
    @Override
    public void render(float deltaTime)
    {
        //Do not update game when paused.
        if(!paused)
        {
            //Update game world by the time that has passed since last rendered frame.
            worldController.update(deltaTime);
        }
        //Sets the clear screen color to: Cornflower Blue
        Gdx.gl.glClearColor(0x64 / 255.0f, 0x95 /255.0f, 0xed / 255.0f, 0xff/255.0f);
        //Clears screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //Render game world to screen
        worldRenderer.render();
        
    }
    
    /**
     * Resizes the game based on window size
     */
    @Override
    public void resize(int width, int height)
    {
        worldRenderer.resize(width, height);
    }
    
    /**
     * Loads the preferences and creates the game window which loads the menu screen
     */
    @Override
    public void show()
    {
        GamePreferences.instance.load();
        worldController = new WorldController(game);
        worldRenderer = new WorldRenderer(worldController);
        Gdx.input.setCatchBackKey(true);
    }
    
    /**
     * Disposes of the game renderer when the game screen is not active
     */
    @Override
    public void hide()
    {
        Gdx.app.postRunnable(()->{
        
        worldController.dispose();
        worldRenderer.dispose();
        Gdx.input.setCatchBackKey(false);
        });
    }
    
    /**
     * Pauses the game
     */
    @Override
    public void pause()
    {
        paused = true;
    }
    
    /**
     * Resumes the game, most only affected by android
     */
    @Override
    public void resume()
    {
        super.resume();
        //Only called on Andriod
        paused=false;
    }

}
