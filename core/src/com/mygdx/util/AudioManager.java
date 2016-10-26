package com.mygdx.util;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Handles the control of the sound and music for playing a sound or having the music on
 * @author Kalan Kriner
 */
public class AudioManager
{
    public static final AudioManager instance = new AudioManager();
    
    private Music playingMusic;
    
    //singleton: prevent instantiation from other classes
    private AudioManager() {}
    
    /**
     * Plays a certain sound
     * @param sound sound to be played
     */
    public void play(Sound sound)
    {
        play(sound, 1);
    }
    
    /**
     * Plays a certain sound at a specific volume
     * @param sound sound to be played
     * @param volume volume of sound
     */
    public void play(Sound sound, float volume)
    {
        play(sound,volume, 1);
    }
    
    /**
     * Plays a certain sound at a specific volume and pitch
     * @param sound sound to be played
     * @param volume volume of sound
     * @param pitch pitch of the sound
     */
    public void play(Sound sound, float volume, float pitch)
    {
        play(sound, volume, pitch, 0);
    }
    
    /**
     * Plays a certain sound at a specific volume, pitch and pan
     * @param sound sound to be played
     * @param volume volume of sound
     * @param pitch pitch of sound
     * @param pan pan of the sound
     */
    public void play(Sound sound, float volume, float pitch, float pan)
    {
        if(!GamePreferences.instance.sound) return;
        sound.play(GamePreferences.instance.volSound * volume, pitch, pan);
    }
    
    /**
     * Plays the given music if music is enabled
     * @param music music to be played
     */
    public void play(Music music)
    {
        stopMusic();
        playingMusic = music;
        if(GamePreferences.instance.music);
        {
            music.setLooping(true);
            music.setVolume(GamePreferences.instance.volMusic);
            music.play();
        }
    }
    
    /**
     * Stops the play of music
     */
    public void stopMusic()
    {
        if(playingMusic !=null)
            playingMusic.stop();
    }
    
    /**
     * Changes if the music is playing or not based on setting changes
     */
    public void onSettingsUpdated()
    {
        if(playingMusic == null) 
            return;
        
        playingMusic.setVolume(GamePreferences.instance.volMusic);
        if(GamePreferences.instance.music)
        {
            if(!playingMusic.isPlaying())
            {
                playingMusic.play();
            }
        }
        else
        {
            playingMusic.pause();
        }
    }

}
