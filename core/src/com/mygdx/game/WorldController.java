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

/**
 * @author Kalan Kriner
 * 
 * WorldController handles the running of the game and handles
 * calls to many other classes
 */
public class WorldController extends InputAdapter
{
	public Sprite[] testSprites;
	public int selectedSprite;
	public CameraHelper cameraHelper;
	private static final String TAG=WorldController.class.getName();
	
	/**
	 * Constructor that just calls the initialize method
	 */
	public WorldController() 
	{
		init();
	}
	
	/**
	 * Creates a thread for the game, the camera and makes the test sprites
	 */
	private void init() 
	{
		Gdx.input.setInputProcessor(this);
		cameraHelper= new CameraHelper();
		initTestObjects();
	}
	
	/**
	 * Creates and array of Sprites that are made of the texture from
	 * Procedural Pixmap and then places them randomly in the game world
	 */
	private void initTestObjects()
	{
		// Create new array for 5 sprites
		testSprites= new Sprite[5];
		// Create empty POT-sized Pixmap with 8 bit RGBA pixel data
		int width = 32;
		int height = 32;
		Pixmap pixmap= createProceduralPixmap(width,height);
		// Create a new texture from picmap data
		Texture texture = new Texture(pixmap);
		// Create new sprites using the just created texture
		for (int i=0;i< testSprites.length;i++)
		{
			Sprite spr= new Sprite(texture);
			// Define sprite sizer to be 1mx1m in game world
			spr.setSize(1, 1);
			// Set origin to the sprite's center
			spr.setOrigin(spr.getWidth()/2.0f, spr.getHeight()/2.0f);
			// Calculate random position for sprite
			float randomX = MathUtils.random(-2.0f,2.0f);
			float randomY = MathUtils.random(-2.0f,2.0f);
			spr.setPosition(randomX, randomY);
			// Put new sprite into array
			testSprites[i]=spr;
		}
		// Set first sprite as selected one
		selectedSprite=0;
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
		updateTestObjects(deltaTime);
		cameraHelper.update(deltaTime);
	}

	/**
	 * Handles key inputs to control the camera, zooming, and sprite
	 * @param deltaTime time since the last update
	 */
	private void handleDebugInput(float deltaTime) 
	{
		if(Gdx.app.getType() != ApplicationType.Desktop) return;
		
		// Selected Sprite Controls
		float sprMoveSpeed = 5* deltaTime;
		if(Gdx.input.isKeyPressed(Keys.A)) moveSelectedSprite(-sprMoveSpeed,0);
		if(Gdx.input.isKeyPressed(Keys.D)) moveSelectedSprite( sprMoveSpeed,0);
		if(Gdx.input.isKeyPressed(Keys.W)) moveSelectedSprite(0, sprMoveSpeed);
		if(Gdx.input.isKeyPressed(Keys.S)) moveSelectedSprite(0, -sprMoveSpeed);
		
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
	 * Moves the camera from the previous positon to the next one
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
	 * Moves the selected sprite to a new location
	 * @param x amount moved in X direction
	 * @param y amount moved in Y direction
	 */
	private void moveSelectedSprite(float x, float y) 
	{
		testSprites[selectedSprite].translate(x,y);
	}

	/**
	 * When an update is called, the sprites are rotated
	 * @param deltaTime time since last update to change rotation
	 */
	private void updateTestObjects(float deltaTime) 
	{
		// Get current rotation from the selected sprite
		float rotation = testSprites[selectedSprite].getRotation();
		//Rotate sprite by 90 degrees per second
		rotation +=90*deltaTime;
		// Wrap around at 360 degrees
		rotation %=360;
		//Set new rotation value to selected sprite
		testSprites[selectedSprite].setRotation(rotation);
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
		//Select the next Sprite
		else if(keycode==Keys.SPACE)
		{
			selectedSprite=(selectedSprite+1)% testSprites.length;
			// Update camera's target to follow the currently selected sprite
			if(cameraHelper.hasTarget())
			{
				cameraHelper.setTarget(testSprites[selectedSprite]);
			}
			Gdx.app.debug(TAG,"Sprite #"+ selectedSprite + " selected");
		}
		//Toggle camera follow
		else if(keycode ==Keys.ENTER)
		{
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null: testSprites[selectedSprite]);
			Gdx.app.debug(TAG,"Camera follow enabled: "+ cameraHelper.hasTarget());
		}
		return false;
	}
}
