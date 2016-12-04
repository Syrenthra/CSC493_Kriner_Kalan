package com.mygdx.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.WorldController;
import com.mygdx.game.Assets;
import com.mygdx.game.objects.AbstractGameObject;
import com.mygdx.game.objects.Barrels;
import com.mygdx.game.objects.Bombs;
import com.mygdx.game.objects.Goal;
import com.mygdx.game.objects.SmallCrate;
import com.mygdx.game.objects.Rock;
import com.mygdx.game.objects.Tank;
import com.mygdx.game.objects.Tank.JUMP_STATE;

public class CollisionHandler implements ContactListener
{
    private ObjectMap<Short, ObjectMap<Short, ContactListener>> listeners;

    private WorldController world;

    public CollisionHandler(WorldController w)
    {
    	world = w;
        listeners = new ObjectMap<Short, ObjectMap<Short, ContactListener>>();
    }

    public void addListener(short categoryA, short categoryB, ContactListener listener)
    {
        addListenerInternal(categoryA, categoryB, listener);
        addListenerInternal(categoryB, categoryA, listener);
    }

    @Override
    public void beginContact(Contact contact)
    {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        //Gdx.app.log("CollisionHandler-begin A", "begin");

        processContact(contact);

        ContactListener listener = getListener(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits);
        if (listener != null)
        {
            listener.beginContact(contact);
        }
    }

    @Override
    public void endContact(Contact contact)
    {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

       // Gdx.app.log("CollisionHandler-end A", "end");
        //processContact(contact);

        // Gdx.app.log("CollisionHandler-end A", fixtureA.getBody().getLinearVelocity().x+" : "+fixtureA.getBody().getLinearVelocity().y);
        // Gdx.app.log("CollisionHandler-end B", fixtureB.getBody().getLinearVelocity().x+" : "+fixtureB.getBody().getLinearVelocity().y);
        ContactListener listener = getListener(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits);
        if (listener != null)
        {
            listener.endContact(contact);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold)
    {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        ContactListener listener = getListener(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits);
        if (listener != null)
        {
            listener.preSolve(contact, oldManifold);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse)
    {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        ContactListener listener = getListener(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits);
        if (listener != null)
        {
            listener.postSolve(contact, impulse);
        }
    }

    private void addListenerInternal(short categoryA, short categoryB, ContactListener listener)
    {
        ObjectMap<Short, ContactListener> listenerCollection = listeners.get(categoryA);
        if (listenerCollection == null)
        {
            listenerCollection = new ObjectMap<Short, ContactListener>();
            listeners.put(categoryA, listenerCollection);
        }
        listenerCollection.put(categoryB, listener);
    }

    private ContactListener getListener(short categoryA, short categoryB)
    {
        ObjectMap<Short, ContactListener> listenerCollection = listeners.get(categoryA);
        if (listenerCollection == null)
        {
            return null;
        }
        return listenerCollection.get(categoryB);
    }

    private void processContact(Contact contact)
    {
    	Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        AbstractGameObject objA = (AbstractGameObject)fixtureA.getBody().getUserData();
        AbstractGameObject objB = (AbstractGameObject)fixtureB.getBody().getUserData();
        //Gdx.app.log("handler","Collided with rock");

        if (objA instanceof Bombs)
        {
            processInteractionContact(fixtureA, fixtureB);
        }
        else if (objB instanceof Bombs)
        {
            processInteractionContact(fixtureB, fixtureA);
        }
        
        if (objA instanceof Tank)
        {
        	processInteractionContact(fixtureA, fixtureB);
        }
        else if (objB instanceof Tank)
        {
        	processInteractionContact(fixtureB, fixtureA);
        }
    }

    private void processInteractionContact(Fixture playerFixture, Fixture objFixture)
    {
        //Checks if a bomb has touched the ground
        if (playerFixture.getBody().getUserData() instanceof Bombs)
        {
            final Bombs bomb = (Bombs)playerFixture.getBody().getUserData();
            bomb.explode();
            return;
        }
        
        //Starts the dust particles and resets the jump for the player
    	if (objFixture.getBody().getUserData() instanceof Rock)
    	{
    		Tank player = (Tank)playerFixture.getBody().getUserData();
    		player.dustParticles.setPosition(player.position.x + player.dimension.x /2, player.position.y);
    		player.dustParticles.start();
    		player.resetJump();
    	    
    	}
    	//Flags the crate for removal
    	else if (objFixture.getBody().getUserData() instanceof SmallCrate)
    	{
    		SmallCrate crate = (SmallCrate)objFixture.getBody().getUserData();
    		world.flagForRemoval(crate);
    	}
    	//Flags the barrel for removal
    	else if (objFixture.getBody().getUserData() instanceof Barrels)
        {
            Barrels barrel = (Barrels)objFixture.getBody().getUserData();
            world.flagForRemoval(barrel);
        }
    	//Flags the barrel for removal
        else if (objFixture.getBody().getUserData() instanceof Bombs)
        {
            Bombs bomb = (Bombs)objFixture.getBody().getUserData();
            world.flagForRemoval(bomb);
        }
        else if (objFixture.getBody().getUserData() instanceof Goal)
        {
            Goal goal = (Goal)objFixture.getBody().getUserData();
            world.onCollisionWithGoal();
        }
    }

}