package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.util.Constants;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;


/**
 * Handles the loading and basic managing of the assets and fonts for the game
 * 
 * @author Kalan Kriner
 */
public class Assets implements Disposable, AssetErrorListener
{
    public static final String TAG= Assets.class.getName();
    public static final Assets instance= new Assets();
    private AssetManager assetManager;
    
    public AssetBunny bunny;
    public AssetRock rock;
    public AssetGoldCoin goldCoin;
    public AssetFeather feather;
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
        public final Sound jumpWithFeather;
        public final Sound pickupCoin;
        public final Sound pickupFeather;
        public final Sound liveLost;
        
        public AssetSounds(AssetManager am)
        {
            jump = am.get("sounds/jump.wav", Sound.class);
            jumpWithFeather = am.get("sounds/jump_with_feather.wav", Sound.class);
            pickupCoin = am.get("sounds/pickup_coin.wav", Sound.class);
            pickupFeather = am.get("sounds/pickup_feather.wav", Sound.class);
            liveLost = am.get("sounds/live_lost.wav", Sound.class);
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
     * 
     * @author Kalan Kriner
     */
    public class AssetFonts
    {
        public final BitmapFont defaultSmall;
        public final BitmapFont defaultNormal;
        public final BitmapFont defaultBig;
        
        /**
         * Creates the 3 fonts and does their settings
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
        assetManager.load("sounds/jump_with_feather.wav", Sound.class);
        assetManager.load("sounds/pickup_coin.wav", Sound.class);
        assetManager.load("sounds/pickup_feather.wav", Sound.class);
        assetManager.load("sounds/live_lost.wav", Sound.class);
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
        bunny=new AssetBunny(atlas);
        rock= new AssetRock(atlas);
        goldCoin = new AssetGoldCoin(atlas);
        feather= new AssetFeather(atlas);
        levelDecoration = new AssetLevelDecoration(atlas);
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
     * Bunny asset class for storing of the bunny head after lookup
     * 
     * White denotes spawn point on level map
     */
    public class AssetBunny
    {
        public final AtlasRegion head;
        public final Animation animNormal;
        public final Animation animCopterTransform;
        public final Animation animCopterTransformBack;
        public final Animation animCopterRotate;
        
        public AssetBunny(TextureAtlas atlas)
        {
            head=atlas.findRegion("bunny_head");
            
            Array<AtlasRegion> regions = null;
            AtlasRegion region = null;
            
            //Animation: Bunny Normal
            regions = atlas.findRegions("anim_bunny_normal");
            animNormal = new Animation(1.0f /10.f, regions, Animation.PlayMode.LOOP_PINGPONG);
            
            //Animation: Bunny Copter -knot ears
            regions = atlas.findRegions("anim_bunny_copter");
            animCopterTransform = new Animation(1.0f /10.f, regions);
            
            //Animation: Bunny Copter - unknot ears
            regions = atlas.findRegions("anim_bunny_copter");
            animCopterTransformBack = new Animation(1.0f /10.f, regions, Animation.PlayMode.REVERSED);
            
            //Animation: Bunny Copter -rotate ears
            regions = new Array<AtlasRegion>();
            regions.add(atlas.findRegion("anim_bunny_copter", 4));
            regions.add(atlas.findRegion("anim_bunny_copter", 5));
            animCopterRotate = new Animation (1.0f/ 15.0f, regions);
        }
    }
    
    /**
     * Rock asset class for storing of the ground pieces after lookup
     * 
     * Green denotes ground block on level map
     */
    public class AssetRock
    {
        public final AtlasRegion edge;
        public final AtlasRegion middle;
        public AssetRock (TextureAtlas atlas)
        {
            edge = atlas.findRegion("rock_edge");
            middle= atlas.findRegion("rock_middle");
        }
    }
    
    /**
     * Gold Coin asset class for storing of the gold coin after lookup
     * 
     * Yellow denotes gold coin on level map
     */
    public class AssetGoldCoin
    {
        public final AtlasRegion goldCoin;
        public Animation animGoldCoin;
        public AssetGoldCoin(TextureAtlas atlas)
        {
            goldCoin=atlas.findRegion("item_gold_coin");
            
            //Animation: Gold Coin
            Array<AtlasRegion> regions = atlas.findRegions("anim_gold_coin");
            AtlasRegion region = regions.first();
            for(int i = 0; i<10; i++)
            {
                regions.insert(0, region);
                animGoldCoin = new Animation(1.0f /20.f, regions, Animation.PlayMode.LOOP_PINGPONG);
            }
        }
    }
    
    /**
     * Feather asset class for storing of the feather after lookup
     * 
     * Purple denotes feather on level map
     */
    public class AssetFeather
    {
        public final AtlasRegion feather;
        public AssetFeather(TextureAtlas atlas)
        {
            feather=atlas.findRegion("item_feather");
        }
    }
    
    /**
     * Level decoration asset class for storing of the decorations after lookup
     */
    public class AssetLevelDecoration
    {
        public final AtlasRegion cloud01;
        public final AtlasRegion cloud02;
        public final AtlasRegion cloud03;
        public final AtlasRegion mountainLeft;
        public final AtlasRegion mountainRight;
        public final AtlasRegion waterOverlay;
        
        public final AtlasRegion carrot;
        public final AtlasRegion goal;
        
        
        /**
         * Sets the texture of each object to its image from within the texture atlas
         * @param atlas
         */
        public AssetLevelDecoration (TextureAtlas atlas)
        {
            cloud01= atlas.findRegion("cloud01");
            cloud02= atlas.findRegion("cloud02");
            cloud03= atlas.findRegion("cloud03");
            mountainLeft=atlas.findRegion("mountain_left");
            mountainRight=atlas.findRegion("mountain_right");
            waterOverlay=atlas.findRegion("water_overlay");
            carrot = atlas.findRegion("carrot");
            goal = atlas.findRegion("goal");
        }
    }
    
}
