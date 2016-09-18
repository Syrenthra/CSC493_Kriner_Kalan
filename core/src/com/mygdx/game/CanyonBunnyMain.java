package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.game.Assets;


/**
 * @author Kalan Kriner
 * 
 * Does initializations of the game by creating the renderer and
 * controller and sets the main window up
 */
public class CanyonBunnyMain implements ApplicationListener
{

	private static final String TAG= CanyonBunnyMain.class.getName();
	
	private WorldController worldController;
	private WorldRenderer worldRenderer;
	private boolean paused;
	
	/**
	 * Creates the controller and renderer and sets the level of the debugger
	 */
	@Override 
	public void create() 
	{
		// Set Libgdx log level to DEBUG
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// Load Assets
		Assets.instance.init(new AssetManager());
		// Initialize controller and renderer
		worldController = new WorldController();
		worldRenderer = new WorldRenderer(worldController);
		// Game world is active on start
		paused=false;
	}
	
	/**
	 * Sets the background of the window and updates the controller
	 */
	@Override 
	public void render() 
	{
		// Do not update while paused
		if(!paused)
		{
			// Update game world by the time that has passed since last rendered frame.
			worldController.update(Gdx.graphics.getDeltaTime());
		}
		// Sets the clear screen color to: Cornflower Blue
		Gdx.gl.glClearColor(0x64/255.0f, 0x95/255.0f, 0xed/255.0f, 0xff/255.0f);
		// Clears the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//Render game world to screen
		worldRenderer.render();
	}
	

	/**
	 * Tells the renderer what the new height and width for the images. 
	 */
	@Override 
	public void resize(int width, int height) 
	{
		worldRenderer.resize(width, height);
	}
	/**
	 * Sets the game to a paused state
	 */
	@Override 
	public void pause() 
	{
		paused=true;
	}
	
	/**
	 * Resumes the game from paused
	 */
	@Override 
	public void resume() 
	{
		paused = false;
	}
	
	/**
	 * Disposes the rendered items
	 */
	@Override 
	public void dispose() 
	{
		worldRenderer.dispose();
		Assets.instance.dispose();
	}
	
	
}
