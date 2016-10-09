package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.CanyonBunnyMain;
import com.mygdx.game.KrinerGdxGame;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class DesktopLauncher
{
    private static boolean rebuildAtlas =false;
    private static boolean drawDebugOutline=true;

    
    public static void main(String[] arg)
    {
        //Will rebuild the texture atlas of the images that are in the desktop assets raw folder
        if(rebuildAtlas)
        {
            Settings settings= new Settings();
            settings.maxWidth=1024;
            settings.maxHeight=1024;
            settings.duplicatePadding=false;
            settings.debug= drawDebugOutline;
            TexturePacker.process(settings, "assets-raw/images", "../core/assets/images","canyonbunny.pack");
            TexturePacker.process(settings, "assets-raw/images-ui", "../core/assets/images","canyonbunny-ui.pack");
        }
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title= "CanyonBunny";
        config.width =800;
        config.height= 480;
        new LwjglApplication(new CanyonBunnyMain(), config);
    }
}
