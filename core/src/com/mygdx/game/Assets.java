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
        public AssetBunny(TextureAtlas atlas)
        {
            head=atlas.findRegion("bunny_head");
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
        public AssetGoldCoin(TextureAtlas atlas)
        {
            goldCoin=atlas.findRegion("item_gold_coin");
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
        }
    }
    
}
