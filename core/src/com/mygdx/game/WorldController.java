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
import com.badlogic.gdx.Game;
import com.mygdx.screens.MenuScreen;
import com.mygdx.game.objects.Rock;
import com.mygdx.game.objects.Tank;
import com.mygdx.game.objects.Tank.JUMP_STATE;
import com.mygdx.game.objects.AbstractGameObject;
import com.mygdx.game.objects.Barrels;
import com.mygdx.game.objects.SmallCrate;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.util.AudioManager;
import com.mygdx.util.CollisionHandler;
import com.mygdx.util.Constants;


/**
 * WorldController handles the running of the game and handles
 * calls to many other classes
 * @author Kalan Kriner
 */
public class WorldController extends InputAdapter
{
    public CameraHelper cameraHelper;
    private static final String TAG=WorldController.class.getName();
	
	private Game game;
	
	public Array<AbstractGameObject> objectsToRemove;
	

    private float timeLeftGameOverDelay;
    
	public Level level;
    public int lives;
    public float livesVisual;
    public int score;
    public float scoreVisual;
    
    private boolean goalReached;
    public World b2world;
	
	 /**
	 * Initializes the level with a new score, map and character
	 */
    private void initLevel()
    {
       score=0;
       scoreVisual=score;
       level=new Level(Constants.LEVEL_01);
	   cameraHelper.setTarget(level.tank);
	   initPhysics();
    }
    
    /**
     * Creates the physics world for box2d
     */
    private void initPhysics()
    {
        if(b2world !=null) 
            b2world.dispose();
        
        b2world = new World(new Vector2(0,-9.81f), true);
        b2world.setContactListener(new CollisionHandler(this));
        //Rocks
        Vector2 origin = new Vector2();
        for(Rock rock: level.rocks)
        {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyType.KinematicBody;
            bodyDef.position.set(rock.position);
            Body body = b2world.createBody(bodyDef);
            body.setUserData(rock);
            rock.body =body;
            PolygonShape polygonShape = new PolygonShape();
            origin.x = rock.bounds.width / 2.0f;
            origin.y = rock.bounds.height / 2.0f;
            polygonShape.setAsBox(rock.bounds.width/2.0f, rock.bounds.height/2.0f, origin, 0);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            body.createFixture(fixtureDef);
            polygonShape.dispose();
        }
        //Crates
        origin = new Vector2();
        for(SmallCrate crate: level.crates)
        {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyType.KinematicBody;
            bodyDef.position.set(crate.position);
            Body body = b2world.createBody(bodyDef);
            body.setUserData(crate);
            crate.body =body;
            PolygonShape polygonShape = new PolygonShape();
            origin.x = crate.bounds.width / 2.0f;
            origin.y = crate.bounds.height / 2.0f;
            polygonShape.setAsBox(crate.bounds.width/2.0f, crate.bounds.height/2.0f, origin, 0);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            body.createFixture(fixtureDef);
            polygonShape.dispose();
        }
        //Barrels
        origin = new Vector2();
        for(Barrels barrel: level.barrels)
        {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyType.KinematicBody;
            bodyDef.position.set(barrel.position);
            Body body = b2world.createBody(bodyDef);
            body.setUserData(barrel);
            barrel.body =body;
            PolygonShape polygonShape = new PolygonShape();
            origin.x = barrel.bounds.width / 2.0f;
            origin.y = barrel.bounds.height / 2.0f;
            polygonShape.setAsBox(barrel.bounds.width/2.0f, barrel.bounds.height/2.0f, origin, 0);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            body.createFixture(fixtureDef);
            polygonShape.dispose();
        }
        
        //Player
        Tank tank = level.tank;
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(tank.position);
        bodyDef.fixedRotation = true;

        Body body = b2world.createBody(bodyDef);
        body.setType(BodyType.DynamicBody);
        body.setGravityScale(0.0f);
        body.setUserData(tank);
        tank.body = body;

        PolygonShape polygonShape = new PolygonShape();
        origin.x = (tank.bounds.width) / 2.0f;
        origin.y = (tank.bounds.height) / 2.0f;
        polygonShape.setAsBox((tank.bounds.width) / 2.0f, (tank.bounds.height) / 2.0f, origin, 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        body.createFixture(fixtureDef);
        polygonShape.dispose();

    }

	
	/**
	 * Constructor that just calls the initialize method
	 */
	public WorldController(Game game) 
	{
		this.game = game;
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
	   livesVisual=lives;
       timeLeftGameOverDelay =0;
       objectsToRemove = new Array<AbstractGameObject>();
       initLevel();
	}
	
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
	  
    /**
     * Returns from the game screen to the main menu
     */
    private void backToMenu()
    {
        // Switch to menu screen
        game.setScreen(new MenuScreen(game));
    }
    
    /**
     * Handles the collision between the character and a score piece 
     * @param crate the piece being checked
     */
    private void onCollisionTankWithCrate(SmallCrate crate)
    {
        crate.collected = true;
        score += crate.getScore();
        b2world.destroyBody(crate.body);
        Gdx.app.log(TAG, "Gold Coin collected");
        AudioManager.instance.play(Assets.instance.sounds.pickupCrate);
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
        b2world.destroyBody(barrel.body);
        Gdx.app.log(TAG, "Gold Coin collected");
        AudioManager.instance.play(Assets.instance.sounds.pickupBarrel,1.5f);
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
            
            //Tank Jump
            if(Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE))
            {
                level.tank.setJumping(true,deltaTime);
            }
            else
            {
                level.tank.setJumping(false,deltaTime);
            }
        }
    }

	/**
	 * Updates different classes with delta time and removes collectables that were hit
	 * @param deltaTime the time between updates
	 */
	public void update (float deltaTime) 
	{
	    //Removes all objects that need to be removed
	    if (objectsToRemove.size > 0)
        {
            for (AbstractGameObject obj : objectsToRemove)
            {
                if (obj instanceof SmallCrate)
                {
                    onCollisionTankWithCrate((SmallCrate)obj);
                }
                else if (obj instanceof Barrels)
                {
                    onCollisionTankWithBarrel((Barrels)obj);
                }
            }
            objectsToRemove.removeRange(0, objectsToRemove.size - 1);
        }

	    //Continues the updating of the game
		handleDebugInput(deltaTime);
		if(isGameOver())
		{
		    timeLeftGameOverDelay -= deltaTime;
		    if(timeLeftGameOverDelay <0)
	 	    {
		        backToMenu();
		    }
		}
		else
		{
		    handleInputGame(deltaTime);
		}
	    b2world.step(deltaTime, 8, 3);  // Tell the Box2D world to update.
		level.update(deltaTime);
		//testCollisions();
		cameraHelper.update(deltaTime);
		if( !isGameOver() && isPlayerInWater())
		{
		    AudioManager.instance.play(Assets.instance.sounds.liveLost);
		    lives--;
		    if(isGameOver())
		    {
		        timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
		    }
		    else
		        initLevel();
		}
		level.mountains.updateScrollPosition(cameraHelper.getPosition());
		
		if(livesVisual > lives)
        {
            livesVisual = Math.max(lives,  livesVisual - 1 * deltaTime);
        }
        if(scoreVisual < score)
        {
            scoreVisual = Math.min(score,  scoreVisual + 250 * deltaTime);
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

		//Back to Menu
		else if(keycode == Keys.ESCAPE || keycode == Keys.BACK)
		{
		    backToMenu();
		}
		return false;
	}

	/**
	 * Adds to the list of objects needing to be removed
	 * @param obj object that is to be removed
	 */
	public void flagForRemoval(AbstractGameObject obj)
	{
	    objectsToRemove.add(obj);
	}
}
