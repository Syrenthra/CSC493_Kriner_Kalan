package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.util.Constants;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;



/**
 * Handles the loading and basic managing of the assets and fonts for the game
 * @author Kalan Kriner
 */
public class Assets implements Disposable, AssetErrorListener
{
    public static final String TAG= Assets.class.getName();
    public static final Assets instance= new Assets();
    private AssetManager assetManager;
    
    public AssetTank tank;
    public AssetGround ground;
    public AssetCrate crate;
    public AssetBarrels barrels;
    public AssetBomb bomb;
    public AssetLevelDecoration levelDecoration;
    
    public AssetSounds sounds;
    public AssetMusic music;
    
    /**
     * Stores all of the sounds for the game
     * @author Kalan Kriner
     */
    public class AssetSounds
    {
        public final Sound jump;
        public final Sound pickupCrate;
        public final Sound pickupBarrel;
        public final Sound liveLost;
        public final Sound explosion;
        
        public AssetSounds(AssetManager am)
        {
            jump = am.get("sounds/jump.wav", Sound.class);
            pickupCrate = am.get("sounds/pickup_crate.wav", Sound.class);
            pickupBarrel = am.get("sounds/pickup_barrel.wav", Sound.class);
            liveLost = am.get("sounds/live_lost.wav", Sound.class);
            explosion= am.get("sounds/explosion.wav",Sound.class);
        }
    }
    
    /**
     * Music class that stores the music for the game
     * @author Kalan Kriner
     */
    public class AssetMusic
    {
        public final Music song01;
        
        public AssetMusic(AssetManager am)
        {
            song01= am.get("music/keith303_-_brand_new_highscore.mp3", Music.class);
        }
    }
    
    //Singleton : prevent instantiation from other classes
    private Assets() {}
    
public AssetFonts fonts;
    
    /**
     * Loads in the basic fonts and then creates 3 different sizes that can be used
     * @author Kalan Kriner
     */
    public class AssetFonts
    {
        public final BitmapFont defaultSmall;
        public final BitmapFont defaultNormal;
        public final BitmapFont defaultBig;
        
        /**
         * Creates the 3 fonts and sets their sizes and filtering
         */
        public AssetFonts()
        {
            // Create three fonts using Libgdx's 15px bitmap font
            defaultSmall= new BitmapFont( Gdx.files.internal("images/arial-15.fnt"),true);
            defaultNormal= new BitmapFont( Gdx.files.internal("images/arial-15.fnt"),true);
            defaultBig= new BitmapFont( Gdx.files.internal("images/arial-15.fnt"),true);
            
            //Set font sizes
            defaultSmall.getData().setScale(0.75f);
            defaultNormal.getData().setScale(1.0f);
            defaultBig.getData().setScale(2.0f);
            
            //Enable linear texture filtering for smooth fonts
            defaultSmall.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
            defaultNormal.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
            defaultBig.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
            
        }
    }
    
    /**
     * Loads in all of the assets into the manager
     * @param assetManager handles the calls to the assets for displaying
     */
    public void init(AssetManager assetManager)
    {
        this.assetManager = assetManager;
        // Set asset manager error handler
        assetManager.setErrorListener(this);
        //Load texture atlas
        assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS,TextureAtlas.class);
        
        //Load Sounds
        assetManager.load("sounds/jump.wav", Sound.class);
        assetManager.load("sounds/pickup_crate.wav", Sound.class);
        assetManager.load("sounds/pickup_barrel.wav", Sound.class);
        assetManager.load("sounds/live_lost.wav", Sound.class);
        assetManager.load("sounds/explosion.wav", Sound.class);
        //Load Music
        assetManager.load("music/keith303_-_brand_new_highscore.mp3", Music.class);
        
        //Start Loading assets and wait until finished
        assetManager.finishLoading();
     
        Gdx.app.debug(TAG,"# of assets loaded:" + assetManager.getAssetNames().size);
        for( String a: assetManager.getAssetNames())
        {
            Gdx.app.debug(TAG,"asset: " + a);
        }
    
    
        TextureAtlas atlas= assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);
        //Enable texture filtering for pixel smoothing
        for(Texture t: atlas.getTextures())
        {
            t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        }

        //Create game resources objects
        fonts= new AssetFonts();
        tank=new AssetTank(atlas);
        ground= new AssetGround(atlas);
        crate = new AssetCrate(atlas);
        barrels= new AssetBarrels(atlas);
        levelDecoration = new AssetLevelDecoration(atlas);
        bomb = new AssetBomb(atlas);
        sounds = new AssetSounds(assetManager);
        music = new AssetMusic(assetManager);
    }
    
    
    /**
     * Will dispose of the objects in assetManager
     */
    @Override
    public void dispose()
    {
        assetManager.dispose();
        fonts.defaultSmall.dispose();
        fonts.defaultNormal.dispose();
        fonts.defaultBig.dispose();

    }

    /**
     * When an error occurs the specific asset not able to load is written to the console
     */
    @Override
    public void error(AssetDescriptor asset, Throwable throwable)
    {
        Gdx.app.error(TAG,"Couldn't load asset '" + asset.fileName + " ' ", (Exception) throwable);
    }
    
    /**
     * Tank asset class for storing of the tank after lookup
     * 
     * White denotes spawn point on level map
     */
    public class AssetTank
    {
        public final AtlasRegion tank;
        public final Animation animRight;
        
        public AssetTank(TextureAtlas atlas)
        {
            tank=atlas.findRegion("tank");
            
            Array<AtlasRegion> regions =null;
            //Animation: Tank right
            regions= atlas.findRegions("tank_right");
            animRight = new Animation(1.0f/16.0f, regions, Animation.PlayMode.LOOP);
        }
    }
    
    /**
     * Ground asset class for storing of the ground pieces after lookup
     * 
     * Green denotes ground piece on level map
     */
    public class AssetGround
    {
        public final AtlasRegion edgeLGround;
        public final AtlasRegion topGround;
        public final AtlasRegion edgeLMud;
        public final AtlasRegion topMud;
        public AssetGround (TextureAtlas atlas)
        {
            edgeLGround = atlas.findRegion("Basic_Ground_CornerL_Pixel");
            topGround= atlas.findRegion("Basic_Ground_Top_Pixel");
            edgeLMud= atlas.findRegion("Basic_Mud_CornerL_Pixel");
            topMud = atlas.findRegion("Basic_Mud_Top_Pixel");
        }
    }
    
    /**
     * Crate asset class for storing of the crate after lookup
     * 
     * Yellow denotes crate piece on level map
     */
    public class AssetCrate
    {
        public final AtlasRegion crate;
        public AssetCrate(TextureAtlas atlas)
        {
            crate=atlas.findRegion("SmallCrate");
        }
    }
    
    /**
     * Barrel asset class for storing of the barrels after lookup 
     * 
     * Purple denotes barrel piece on level map
     */      
    public class AssetBarrels
    {
        public final AtlasRegion barrels;
        public AssetBarrels(TextureAtlas atlas)
        {
            barrels=atlas.findRegion("Barrels");
        }
    }
    
    /**
     * Holds instances of all of the different decorations that will be used
     * @author Kalan Kriner
     */
    public class AssetLevelDecoration
    {
        public final AtlasRegion cloud01;
        public final AtlasRegion cloud02;
        public final AtlasRegion cloud03;
        public final AtlasRegion mountain;
        public final AtlasRegion lavaOverlay;
        public final AtlasRegion goal;
        
        /**
         * Sets the decorations to their correct images
         * @param atlas atlas to get the information from
         */
        public AssetLevelDecoration (TextureAtlas atlas)
        {
            cloud01= atlas.findRegion("whitecloud01");
            cloud02= atlas.findRegion("whitecloud02");
            cloud03= atlas.findRegion("whitecloud03");
            mountain=atlas.findRegion("mountain");
            lavaOverlay=atlas.findRegion("lava_overlay");
            goal=atlas.findRegion("goal");
        }
    }
    
    public class AssetBomb
    {
        public final Animation animExplosion;
        public final Animation animBomb;
        public final Animation animPause;
        
        public AssetBomb(TextureAtlas atlas)
        {
            Array<AtlasRegion> regions =null;
            //Animation: bomb
            regions= atlas.findRegions("bomb");
            animBomb= new Animation(1.0f/10.f,regions,Animation.PlayMode.LOOP);
            
            //Animation: Explosion
            regions= atlas.findRegions("exp");
            animExplosion = new Animation(1.0f/16.0f, regions, Animation.PlayMode.NORMAL);
            //Animation: Paused explosion
            regions=new Array<AtlasRegion>();
            regions.add(atlas.findRegion("exp",13));
            animPause = new Animation(1.0f/1.0f,regions,Animation.PlayMode.LOOP);
        }
    }
}
