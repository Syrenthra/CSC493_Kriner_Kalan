package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.objects.AbstractGameObject;
import com.mygdx.game.objects.Clouds;
import com.mygdx.game.objects.Mountains;
import com.mygdx.game.objects.Rock;
import com.mygdx.game.objects.WaterOverlay;
import com.mygdx.game.objects.BunnyHead;
import com.mygdx.game.objects.Feather;
import com.mygdx.game.objects.GoldCoin;
import com.mygdx.game.objects.Carrot;
import com.mygdx.game.objects.Goal;

/**
 * Level loader which places objects where they are color coded to on the level image
 * 
 * @author Kalan Kriner
 */
public class Level
{
    public static final String TAG = Level.class.getName();
    
    /**
     * Type created to distinguish between the colors used for blocks for the level loader
     */
    public enum BLOCK_TYPE
    {
        EMPTY(0,0,0), //Black
        ROCK(0,255,0), //Green
        PLAYER_SPAWNPOINT(255,255,255), //White
        ITEM_FEATHER(255,0,255), // Purple
        ITEM_GOLD_COIN(255,255,0), //Yellow
        GOAL(255 ,0 ,0); //Red
        
        private int color;
        
        /**
         * Makes the color of the object into an int
         * @param r red
         * @param g green
         * @param b blue
         */
        private BLOCK_TYPE(int r, int g, int b)
        {
            color= r<< 24 | g<< 16 | b<< 8 | 0xFF;
        }
        
        /**
         * Checks to see if the colors match
         * @param color to be checked against
         * @return true or false
         */
        public boolean sameColor(int color)
        {
            return this.color==color;
        }
        
        /**
         * @return color value
         */
        public int getColor()
        {
            return color;
        }
    }
    //Player Character
    public BunnyHead bunnyHead;
    
    //Objects
    public Array<GoldCoin> goldcoins;
    public Array<Feather> feathers;
    public Array<Rock> rocks;
    public Array<Carrot> carrots;
    public Goal goal;

    
    //Decoration
    public Clouds clouds;
    public Mountains mountains;
    public WaterOverlay waterOverlay;
    
    /**Loads in the level image to initialize the game based on it
     * @param filename name of the level image
     */
    public Level (String filename)
    {
        init(filename);
    }
    
    /**
     * Reads in the level file and places the game objects where the level map says to based on color coding
     * @param filename location of level map
     */
    private void init(String filename)
    {
        // Player character
        bunnyHead= null;
        //Objects
        rocks= new Array<Rock>();
        goldcoins = new Array<GoldCoin>();
        feathers = new Array<Feather>();
        carrots = new Array<Carrot>();
        
        // Load image file that represents the level data
        Pixmap pixmap =new Pixmap(Gdx.files.internal(filename));
        // Scan pixels from top-left to bottom-right
        int lastPixel= -1;
        for(int pixelY=0;pixelY<pixmap.getHeight();pixelY++)
        {
            for(int pixelX=0;pixelX<pixmap.getWidth();pixelX++)
            {
                AbstractGameObject obj=null;
                float offsetHeight=0;
                // Height grows from bottom to top
                float baseHeight = pixmap.getHeight()-pixelY;
                //Get color of current pixel as 32-bit RGBA value;
                int currentPixel= pixmap.getPixel(pixelX, pixelY);
                //Find matching color value to identify block type at (x,y) point and create
                // the corresponding game object if there is a match
                
                //Empty space
                if(BLOCK_TYPE.EMPTY.sameColor(currentPixel))
                {
                    //do nothing
                }
                
                //Empty space
                else if(BLOCK_TYPE.ROCK.sameColor(currentPixel))
                {
                    if(lastPixel != currentPixel)
                    {
                        obj = new Rock();
                        float heightIncreaseFactor = 0.25f;
                        offsetHeight = -2.5f;
                        obj.position.set(pixelX,baseHeight* obj.dimension.y* heightIncreaseFactor + offsetHeight);
                        rocks.add((Rock)obj);
                    }
                    else
                    {
                        rocks.get(rocks.size-1).increaseLength(1);
                    }
                }
                
                //Goal
                else if(BLOCK_TYPE.GOAL.sameColor(currentPixel))
                {
                    obj = new Goal();
                    offsetHeight = -7.0f;
                    obj.position.set(pixelX, baseHeight + offsetHeight);
                    goal = (Goal) obj;
                }
                
                //Player spawn point
                else if(BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel))
                {
                    obj = new BunnyHead();
                    offsetHeight = -3.0f;
                    obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
                    bunnyHead = (BunnyHead) obj;
                    
                }
                
                //Feather
                else if(BLOCK_TYPE.ITEM_FEATHER.sameColor(currentPixel))
                {
                    obj = new Feather();
                    offsetHeight = -1.5f;
                    obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
                    feathers.add((Feather) obj);
                    
                }
                
                //Gold Coin
                else if(BLOCK_TYPE.ITEM_GOLD_COIN.sameColor(currentPixel))
                {
                    obj = new GoldCoin();
                    offsetHeight = -1.5f;
                    obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
                    goldcoins.add((GoldCoin) obj);
                    
                }
                
                //Unknown object/pixel color
                else
                {
                    int r=0xff &(currentPixel >>>24); //red color channel
                    int g=0xff &(currentPixel >>>16); //green color channel
                    int b=0xff &(currentPixel >>>8); //blue color channel
                    int a=0xff & currentPixel; //alpha channel
                    Gdx.app.error(TAG,"Unknown object at x<" + pixelX + "> y<" +pixelY+ ">: r<" +r +"> g<"+
                    g +"> b<"+ b+ "> a<" + a +">");
                }
                
                lastPixel=currentPixel;   
            }
        }
        
        //Decoration
        clouds= new Clouds(pixmap.getWidth());
        clouds.position.set(0,2);
        mountains= new Mountains(pixmap.getWidth());
        mountains.position.set(-1,-1);
        waterOverlay= new WaterOverlay(pixmap.getWidth());
        waterOverlay.position.set(0,-3.75f);

        // Free memory
        pixmap.dispose();
        Gdx.app.debug(TAG,"level '"+ filename+ "' loaded");
    }
        
    
    /**
     * Draws the decorations and the platforms
     * @param batch Group for drawing
     */
    public void render(SpriteBatch batch)
    {
        //Draw Mountains
        mountains.render(batch);
        //Draw Goal
        goal.render(batch);
        //Draw Rocks
        for(Rock rock:rocks)
            rock.render(batch);
        //Draw Gold Coins
        for(GoldCoin goldCoin:goldcoins)
            goldCoin.render(batch);
        //Draw Feathers
        for(Feather feather:feathers)
            feather.render(batch);
        // Draw Carrots
        for(Carrot carrot : carrots)
            carrot.render(batch);
        //Draw Player Character
        bunnyHead.render(batch);
        //Draw Water Overlay
        waterOverlay.render(batch);
        //Draw Clouds
        clouds.render(batch);
    }
    
    /**
     * Updates all of the level specific items 
     * @param deltaTime
     */
    public void update (float deltaTime)
    {
        bunnyHead.update(deltaTime);
        for(Rock rock : rocks)
            rock.update(deltaTime);
        for(GoldCoin goldCoin:goldcoins)
            goldCoin.update(deltaTime);
        for(Feather feather: feathers)
            feather.update(deltaTime);
        for(Carrot carrot : carrots)
            carrot.update(deltaTime);
        clouds.update(deltaTime);
    }
}