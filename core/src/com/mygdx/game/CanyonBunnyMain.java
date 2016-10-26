package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.game.Assets;
import com.mygdx.screens.MenuScreen;
import com.mygdx.util.AudioManager;
import com.mygdx.util.GamePreferences;


/**
 * Creates the initial window that the screens will handle drawing
 * @author Kalan Kriner
 */
public class CanyonBunnyMain extends Game
{

	private static final String TAG= CanyonBunnyMain.class.getName();
	
	private WorldController worldController;
	private WorldRenderer worldRenderer;
	private boolean paused;
	
	/**
	 * Creates the controller and renderer, sets the level of the debugger and creates the initial menu screen
	 */
	@Override 
	public void create() 
	{
		// Set Libgdx log level to DEBUG
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// Load Assets
		Assets.instance.init(new AssetManager());
		// Load preferences for audio settings and start playing music
        GamePreferences.instance.load();
        AudioManager.instance.play(Assets.instance.music.song01);
		// Start game at menu screen
		setScreen(new MenuScreen(this));
	}

}
