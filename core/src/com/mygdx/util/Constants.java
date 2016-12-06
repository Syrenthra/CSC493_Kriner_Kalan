package com.mygdx.util;

/**
 * Class to store constants that can be used in in multiple locations.
 * @author Kalan Kriner
 */
public class Constants 
{
	// Visible game world is 5 meters wide
	public static final float VIEWPORT_WIDTH = 5.0f;
	
	// Visible game world is 5 meters tall
	public static final float VIEWPORT_HEIGHT = 5.0f;
	
	//GUI Width
	public static final float VIEWPORT_GUI_WIDTH=800.0f;
	
	//GuI HEIGHT
	public static final float VIEWPORT_GUI_HEIGHT=480.0f;
	
	//Location of description file for texture atlas
	public static final String TEXTURE_ATLAS_OBJECTS = "images/tankrun.pack.atlas";

	public static final String TEXTURE_ATLAS_UI= "images/canyonbunny-ui.pack.atlas";
	
	public static final String TEXTURE_ATLAS_LIBGDX_UI = "images/uiskin.atlas";
	
	//Location of description files for skins
	public static final String SKIN_LIBGDX_UI = "images/uiskin.json";
	
	public static final String SKIN_TANK_RUN_UI = "images/canyonbunny-ui.json";
	
	//Location of image file for level 01
	public static final String LEVEL_01 = "levels/level - 01.png";
	//Location of image file for level 01
    public static final String LEVEL_02 = "levels/level - 02.png";
	
	//Amount of extra lives at level start
	public static final int LIVES_START =3;

	//Duration of feather power-up in seconds
	public static final float ITEM_BARREL_POWERUP_DURATION = 9;
	
	//Delay after game over
	public static final float TIME_DELAY_GAME_OVER = 3;

	//File for storing game preferences
	public static final String PREFERENCES = "canyonbunny.prefs";
	
	//Maximum number of bombs to spawn
	public static final int BOMBS_SPAWN_MAX = 5;
	
	//Spawn radius for carrots
	public static final float BOMBS_SPAWN_RADIUS= 2.5f;

}
