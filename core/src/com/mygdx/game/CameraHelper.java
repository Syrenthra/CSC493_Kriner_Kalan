package com.mygdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Kalan Kriner
 *
 * Camera for the game that can be moved or set to a specific
 * target and zoom in and out
 */
public class CameraHelper 
{
	private static final String TAG= CameraHelper.class.getName();

	private final float MAX_ZOOM_IN = 0.25f;
	private final float MAX_ZOOM_OUT= 10.0f;
	
	private Vector2 position;
	private float zoom;
	private Sprite target;
	
	/**
	 * Constructor for the CameraHelper class that sets the default zoom 
	 * and creates a vector for camera location
	 */
	public CameraHelper()
	{
		position =new Vector2();
		zoom =1.0f;
	}
	
	/**
	 * When this class is updated, if there is a target,
     * the camera position will follow the target
	 * @param deltaTime time since the last update
	 */
	public void update(float deltaTime)
	{
		if(!hasTarget()) return;
		
		position.x=target.getX() +target.getOriginX();
		position.y=target.getY() +target.getOriginY();
	}
	

	/**
	 * Sets the camera to a specific position
	 * @param x X coordinate in the game world
	 * @param y Y coordinate in the game world
	 */
	public void setPosition(float x, float y)
	{
		this.position.set(x,y);
	}
	
	/**
	 * @return the position in the world the camera is centered at
	 */
	public Vector2 getPosition() 
	{
		return position;
	}

	/**
	 * Increases the zoom
	 * @param amount amount to increase the zoom by
	 */
	public void addZoom(float amount)
	{
		setZoom(zoom+amount);
	}
	
	/**
	 * Sets zoom to specific value
	 * @param zoom zoom level to be set to
	 */
	public void setZoom(float zoom)
	{
		this.zoom=MathUtils.clamp(zoom, MAX_ZOOM_IN, MAX_ZOOM_OUT);
	}
	
	/**
	 * @return the level of zoom
	 */
	public float getZoom()
	{
		return zoom;
	}
	
	/**
	 * Sets the target of the camera
	 * @param target object the camera is centered on
	 */
	public void setTarget(Sprite target)
	{
		this.target=target;
	}
	
	/**
	 * @return the target the camera is centered on
	 */
	public Sprite getTarget()
	{
		return target;
	}
	
	/**
	 * @return tells if there is a target set or not
	 */
	public boolean hasTarget()
	{
		return target!=null;
	}
	
	/**
	 * Checks if the given target is the set one
	 * @param target target being tested for
	 * @return whether the target is the specified one or not
	 */
	public boolean hasTarget(Sprite target)
	{
		return hasTarget() && this.target.equals(target);
	}
	
	/**
	 * Sets the camera properties
	 * @param camera camera properties are to be applied to
	 */
	public void applyTo(OrthographicCamera camera)
	{
		camera.position.x= position.x;
		camera.position.y= position.y;
		camera.zoom=zoom;
		camera.update();
	}
}
