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
import com.mygdx.game.objects.Tank;
import com.mygdx.game.objects.Tank.JUMP_STATE;
import com.mygdx.game.objects.Barrels;
import com.mygdx.game.objects.SmallCrate;
import com.badlogic.gdx.math.Rectangle;
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
	
	 /**
	 * Initializes the level with a new score, map and character
	 */
    private void initLevel()
    {
        score=0;
        level=new Level(Constants.LEVEL_01);
	   cameraHelper.setTarget(level.tank);

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
	   timeLeftGameOverDelay =0;
        initLevel();
	}

private float timeLeftGameOverDelay;
	
	/**
	 * Checks the lives left
	 * @return true if any lives are left
	 */
	public boolean isGameOver()
	{
	    return lives<0;
	}
	
	/**
	 * Checks if player is under the water
	 * @return true if under water
	 */
	public boolean isPlayerInWater()
	{
	    return level.tank.position.y <-5;
	}
	   
    //Rectangles for collision detection
    private Rectangle r1 = new Rectangle();
    private Rectangle r2 = new Rectangle();
	
    /**
     * Checks collisions between the player character and the ground to see where the character should be placed 
     * @param rock ground piece to check collision against
     */
    private void onCollisionTankWithRock(Rock rock)
    {
        Tank tank = level.tank;
        float heightDifference = Math.abs(tank.position.y - (rock.position.y +rock.bounds.height));
        if(heightDifference > 0.25f)
        {
            boolean hitRightEdge =tank.position.x > (rock.position.x + rock.bounds.width/2);
            if(hitRightEdge)
            {
                tank.position.x = rock.position.x + rock.bounds.width;
            }
            else 
            {
                tank.position.x = rock.position.x - tank.bounds.width;
            }
            return;
        }
        
        switch (tank.jumpState)
        {
        case GROUNDED:
            break;
        case FALLING:
        case JUMP_FALLING:
            tank.position.y = rock.position.y + tank.bounds.height + tank.origin.y;
            tank.jumpState = JUMP_STATE.GROUNDED;
            break;
        case JUMP_RISING:
            tank.position.y = rock.position.y + tank.bounds.height + tank.origin.y;
            break;
        }
    }
    
    /**
     * Handles the collision between the character and a score piece 
     * @param crate the piece being checked
     */
    private void onCollisionTankWithCrate(SmallCrate crate)
    {
        crate.collected = true;
        score += crate.getScore();
        Gdx.app.log(TAG, "Gold Coin collected");        
    }
    
    /**
     * Updates character with power up abilities
     * @param barrel the piece collected
     */
    private void onCollisionTankWithBarrel(Barrels barrel)
    {
        barrel.collected = true;
        score += barrel.getScore();
        level.tank.setBarrelPowerup(true);
        Gdx.app.log(TAG, "Gold Coin collected");
    }
    
    /**
     * Tests collisions with every piece of game object that can be hit
     */
    private void testCollisions()
    {
        r1.set(level.tank.position.x, level.tank.position.y, 
                level.tank.bounds.width, level.tank.bounds.height);
        
        // Test collision: Tank <-> Rocks
        for(Rock rock: level.rocks)
        {
            r2.set(rock.position.x, rock.position.y, rock.bounds.width, rock.bounds.height);
            if(!r1.overlaps(r2)) continue;
            
            onCollisionTankWithRock(rock);
            // IMPORTANT: must do all collisions for valid edge testing on rocks
        }
        
        //Test collision : Tank <-> Crates
        for(SmallCrate crate: level.crates)
        {
            if(crate.collected) continue;
            
            r2.set(crate.position.x, crate.position.y, crate.bounds.width, crate.bounds.height);
            if(!r1.overlaps(r2)) continue;
            
            onCollisionTankWithCrate(crate);
            break;
        }
        
        //Test collision : Tank <-> Barrel
        for(Barrels barrel: level.barrels)
        {
            if(barrel.collected) continue;
            
            r2.set(barrel.position.x, barrel.position.y, barrel.bounds.width, barrel.bounds.height);
            if(!r1.overlaps(r2)) continue;
            
            onCollisionTankWithBarrel(barrel);
            break;
        }
    }
    
    /**
     * Handles player movement and jumping
     * @param deltaTime time since last update
     */
    private void handleInputGame(float deltaTime)
    {
        if(cameraHelper.hasTarget(level.tank))
        {
            // Player movement
            if(Gdx.input.isKeyPressed(Keys.LEFT))
            {
                level.tank.velocity.x = -level.tank.terminalVelocity.x;
            }
            else if(Gdx.input.isKeyPressed(Keys.RIGHT))
            {
                level.tank.velocity.x = level.tank.terminalVelocity.x;
            }
            else 
            {
                //Execute auto-forward movement on non-desktop platform
                if(Gdx.app.getType() !=ApplicationType.Desktop)
                {
                    level.tank.velocity.x = level.tank.terminalVelocity.x;
                }
            }
            
            //Bunny Jump
            if(Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE))
            {
                level.tank.setJumping(true);
            }
            else
            {
                level.tank.setJumping(false);
            }
        }
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
		if(isGameOver())
		{
		    timeLeftGameOverDelay -= deltaTime;
		    if(timeLeftGameOverDelay <0)
		        init();
		}
		else
		{
		    handleInputGame(deltaTime);
		}
		level.update(deltaTime);
		testCollisions();
		cameraHelper.update(deltaTime);
		if( !isGameOver() && isPlayerInWater())
		{
		    lives--;
		    if(isGameOver())
		    {
		        timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
		    }
		    else
		        initLevel();
		}
	}


		/**
	 * Handles key inputs to control the camera, zooming, and sprite
	 * @param deltaTime time since the last update
	 */
	private void handleDebugInput(float deltaTime) 
	{
		if(Gdx.app.getType() != ApplicationType.Desktop) return;
		
		if(!cameraHelper.hasTarget(level.tank))
		{
		    //Camera Controls(move)
		    float camMoveSpeed= 5 * deltaTime;
		    float camMoveSpeedAccelerationFactor= 5;
		    if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camMoveSpeed*=camMoveSpeedAccelerationFactor;
		    if(Gdx.input.isKeyPressed(Keys.LEFT)) moveCamera(-camMoveSpeed,0);
		    if(Gdx.input.isKeyPressed(Keys.RIGHT)) moveCamera(camMoveSpeed,0);
		    if(Gdx.input.isKeyPressed(Keys.UP)) moveCamera(0, camMoveSpeed);
		    if(Gdx.input.isKeyPressed(Keys.DOWN)) moveCamera(0, -camMoveSpeed);
		    if(Gdx.input.isKeyPressed(Keys.BACKSPACE)) cameraHelper.setPosition(0, 0);
		}
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
		// Toggle camera follow
		else if( keycode== Keys.ENTER)
		{
		    cameraHelper.setTarget(cameraHelper.hasTarget() ? null : level.tank);
		    Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
		}
		return false;
	}
}
