package com.mygdx.game;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.objects.Rock;
import com.mygdx.util.Constants;


/**
 * @author Kalan Kriner
 * 
 * WorldController handles the running of the game and handles
 * calls to many other classes
 */
public class WorldController extends InputAdapter
{
	public CameraHelper cameraHelper;
	private static final String TAG=WorldController.class.getName();
	
	public Level level;
    public int lives;
    public int score;

    private void initLevel()
    {
        score=0;
        level=new Level(Constants.LEVEL_01);
    }
	
	/**
	 * Constructor that just calls the initialize method
	 */
	public WorldController() 
	{
		init();
	}
	
	/**
	 * Creates a thread for the game, the camera and initializes the level
	 */
	private void init() 
	{
	    Gdx.input.setInputProcessor(this);
        cameraHelper= new CameraHelper();
        lives= Constants.LIVES_START;
        initLevel();
	}
	
	/**
	 * Creates the image the sprites will use which is a square with an X through it
	 * @param width of the square
	 * @param height of the square
	 * @return the Pixmap of the image that will be made into a texture
	 */
	private Pixmap createProceduralPixmap(int width, int height)
	{
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		// Fill square with red color at 50% opacity
		pixmap.setColor(1, 0, 0, 0.5f);
		pixmap.fill();
		// Draw a yellow-colored X shape on square
		pixmap.setColor(1, 1, 0,1);
		pixmap.drawLine(0, 0, width, height);
		pixmap.drawLine(width, 0, 0, height);
		// Draw cyan-colored border around square
		pixmap.setColor(0,1,1,1);
		pixmap.drawRectangle(0, 0, width, height);
		return pixmap;
	}
	
	/**
	 * Updates different classes with delta time
	 * @param deltaTime the time between updates
	 */
	public void update (float deltaTime) 
	{
		handleDebugInput(deltaTime);
		cameraHelper.update(deltaTime);
	}

	/**
	 * Handles key inputs to control the camera, zooming, and sprite
	 * @param deltaTime time since the last update
	 */
	private void handleDebugInput(float deltaTime) 
	{
		if(Gdx.app.getType() != ApplicationType.Desktop) return;
		
		//Camera Controls(move)
		float camMoveSpeed= 5 * deltaTime;
		float camMoveSpeedAccelerationFactor= 5;
		if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camMoveSpeed*=camMoveSpeedAccelerationFactor;
		if(Gdx.input.isKeyPressed(Keys.LEFT)) moveCamera(-camMoveSpeed,0);
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) moveCamera(camMoveSpeed,0);
		if(Gdx.input.isKeyPressed(Keys.UP)) moveCamera(0, camMoveSpeed);
		if(Gdx.input.isKeyPressed(Keys.DOWN)) moveCamera(0, -camMoveSpeed);
		if(Gdx.input.isKeyPressed(Keys.BACKSPACE)) cameraHelper.setPosition(0, 0);
		
		//Camera Controls (zoom)
		float camZoomSpeed = 1* deltaTime;
		float camZoomSpeedAccelerationFactor = 5;
		if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camZoomSpeed*=camZoomSpeedAccelerationFactor;
		if(Gdx.input.isKeyPressed(Keys.COMMA)) cameraHelper.addZoom(camZoomSpeed);
		if(Gdx.input.isKeyPressed(Keys.PERIOD)) cameraHelper.addZoom(-camZoomSpeed);
		if(Gdx.input.isKeyPressed(Keys.SLASH)) cameraHelper.setZoom(1);
		}

	/**
	 * Moves the camera from the previous position to the next one
	 * @param x amount moved in X direction
	 * @param y amount moved in Y direction
	 */
	private void moveCamera(float x, float y) 
	{
		x+=cameraHelper.getPosition().x;
		y+=cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}

	/**
	 * Handles different commands and actions for different keys
	 */
	@Override
	public boolean keyUp(int keycode)
	{
		//Reset game world
		if(keycode== Keys.R)
		{
			init();
			Gdx.app.debug(TAG,"Game world resetted");
		}
		return false;
	}
}
