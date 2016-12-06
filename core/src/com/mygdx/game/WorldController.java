package com.mygdx.game;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Game;
import com.mygdx.screens.HighScoreScreen;
import com.mygdx.screens.MenuScreen;
import com.mygdx.game.objects.Rock;
import com.mygdx.game.objects.Tank;
import com.mygdx.game.objects.AbstractGameObject;
import com.mygdx.game.objects.Barrels;
import com.mygdx.game.objects.Bombs;
import com.mygdx.game.objects.SmallCrate;
import com.badlogic.gdx.math.MathUtils;
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
	public int currentLevel;
    public int lives;
    public float livesVisual;
    public int score;
    public float scoreVisual;
    
    private Array<String>levels;
    
    private boolean goalReached;
    public World b2world;
	
	 /**
	 * Initializes the level with a new score, map and character
	 */
    private void initLevel()
    {

       level=new Level(levels.get(currentLevel-1));
	   cameraHelper.setTarget(level.tank);
	   goalReached=false;
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
            //Sets the friction depending on if it is mud or not
            if(rock.getMud())
            {
                fixtureDef.friction=0.20f;
            }
            else
            {
                fixtureDef.friction=0.01f;
            }
            //Gdx.app.log("Rock Friction", ": " + fixtureDef.friction);
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
        //Goal
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.KinematicBody;
        bodyDef.position.set(level.goal.position);
        Body body = b2world.createBody(bodyDef);
        body.setUserData(level.goal);
        level.goal.body =body;
        PolygonShape polygonShape = new PolygonShape();
        origin.x = level.goal.bounds.width / 2.0f;
        origin.y = level.goal.bounds.height / 2.0f;
        polygonShape.setAsBox((level.goal.bounds.width/2.0f)-.5f, level.goal.bounds.height/2.0f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);
        polygonShape.dispose();
        
        //Player
        Tank tank = level.tank;
        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.position.set(tank.position);
        bodyDef2.fixedRotation = true;

        Body body2 = b2world.createBody(bodyDef2);
        body2.setType(BodyType.DynamicBody);
        body2.setGravityScale(0.0f);
        body2.setUserData(tank);
        tank.body = body2;

        PolygonShape polygonShape2 = new PolygonShape();
        origin.x = (tank.bounds.width) / 2.0f;
        origin.y = (tank.bounds.height) / 2.0f;
        polygonShape2.setAsBox((tank.bounds.width) / 2.0f, (tank.bounds.height) / 2.0f, origin, 0);

        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = polygonShape2;
        fixtureDef2.friction=1.0f;
        fixtureDef2.density=10f;
        body2.createFixture(fixtureDef2);
        polygonShape2.dispose();

        Vector2 centerPos = new Vector2(level.tank.position);
        centerPos.x += level.tank.bounds.width;
        spawnBombs(centerPos, Constants.BOMBS_SPAWN_MAX, Constants.BOMBS_SPAWN_RADIUS);
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
       levels= new Array<String>();
       levels.add(Constants.LEVEL_01);
       levels.add(Constants.LEVEL_02);
       currentLevel=1;
       score=0;
       scoreVisual=score;
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
	public boolean isObjectInWater(AbstractGameObject obj)
	{
	    return obj.position.y <-5;
	}
	  
    /**
     * Returns from the game screen to the main menu
     */
    private void toHighScore()
    {
        // Switch to menu screen
        game.setScreen((new HighScoreScreen(game, score)));
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
     * Handles the collision between the player and a bomb
     * @param bomb the piece hit
     */
    private void onCollisionTankWithBomb(Bombs bomb)
    {
        bomb.explode();
        score += bomb.getScore();        
        
    }
    
    /**
     * Resets the bomb to be above the character somewhere
     * @param bomb the bomb to be reset
     */
    private void bombReset(Bombs bomb)
    {
        bomb.body.setType(BodyType.DynamicBody);
        Vector2 centerPos = new Vector2(level.tank.position);
        centerPos.x += level.tank.bounds.width;
        float radius =Constants.BOMBS_SPAWN_RADIUS;
        float x= MathUtils.random(-radius, radius);
        float y= MathUtils.random(10f, 15f);
        centerPos.add(x, y);
        bomb.body.setTransform(centerPos,0);
        /*
         * Can't get the bomb animation to show only once its been reset fully
         */
        
        bomb.reset=false;
        bomb.exploded=false;
        bomb.resetAnimation();
    }
    
    /**
     * If a bomb is under water, it is to be reset
     * @param bomb
     */
    private void bombUnderWater(Bombs bomb)
    {
        Gdx.app.log("Bomb check","Bomb is under water");
        bomb.reset = true;
    }
    
    public void onCollisionWithGoal()
    {
        if(!goalReached)
        {
            score+=1000;
            timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
        }
        goalReached=true;
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
                else if (obj instanceof Bombs)
                {
                    if(isObjectInWater((Bombs)obj))
                        bombUnderWater((Bombs)obj);
                    else
                        onCollisionTankWithBomb((Bombs)obj);
                }
            }
            objectsToRemove.removeRange(0, objectsToRemove.size - 1);
        }

	    //Continues the updating of the game
		handleDebugInput(deltaTime);
		
		if(goalReached)
        {
            timeLeftGameOverDelay -= deltaTime;
            Gdx.app.log("Goal reached","counting down time");
            if(timeLeftGameOverDelay <0)
            {
                if(levels.size>currentLevel)
                {
                    currentLevel++;
                    initLevel();
                }
                else
                {
                    toHighScore();
                }
            }
            return;
            
        }
		
		if(isGameOver())
		{
		    timeLeftGameOverDelay -= deltaTime;
		    if(timeLeftGameOverDelay <0)
	 	    {
		        toHighScore();
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

		if( !isGameOver() && isObjectInWater(level.tank))
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
        else if(scoreVisual > score)
        {   
            scoreVisual = Math.min(scoreVisual,  score - 250 * deltaTime);
        }

        for(Bombs bomb: level.bombs)
        {
            if(bomb.reset)
            {
                bombReset(bomb);
            }
            else if(isObjectInWater(bomb))
            {
                flagForRemoval(bomb);
            }
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
		    toHighScore();
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
	
	/**
	 * Spawns bomb items around the character
	 * @param pos position of the character
	 * @param numBombs number of bombs to be spawned
	 * @param radius around the character where they will spawn
	 */
	private void spawnBombs(Vector2 pos, int numBombs, float radius)
	{
	    //Creates bombs with bod2d body
	    for(int i=0; i<numBombs;i++)
	    {
	        Bombs bomb = new Bombs();
	        //Calculates random spawn position
	        float x= MathUtils.random(-radius, radius);
	        float y= MathUtils.random(10f, 15f);
	        
	        BodyDef bodyDef = new BodyDef();
	        bodyDef.position.set(pos);
	        bodyDef.position.add(x, y);
	        Body body = b2world.createBody(bodyDef);
	        body.setType(BodyType.DynamicBody);
	        body.setGravityScale(0.18f);
	        body.setUserData(bomb);
	        bomb.body= body;
	        //Creates rectangular bounding box around the bomb for collision detection
	        PolygonShape polygonShape = new PolygonShape();
	        polygonShape.setAsBox(bomb.bounds.width/2.0f, bomb.bounds.height/2.0f);
	        //Set physics attributes
	        FixtureDef fixtureDef= new FixtureDef();
	        fixtureDef.shape= polygonShape;
	        fixtureDef.density=30;
	        fixtureDef.restitution=0f;
	        fixtureDef.friction=1.0f;
	        body.createFixture(fixtureDef);
	        polygonShape.dispose();
	        level.bombs.add(bomb); 
	        }
	}
}
