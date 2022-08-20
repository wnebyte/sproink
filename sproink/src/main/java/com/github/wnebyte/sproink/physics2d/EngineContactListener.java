package com.github.wnebyte.sproink.physics2d;

import org.joml.Vector2f;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.dynamics.contacts.Contact;
import com.github.wnebyte.sproink.core.ecs.Component;
import com.github.wnebyte.sproink.core.ecs.GameObject;

/*
PreSolve -> BeginContact -> EndContact -> PostSolve
 */
public class EngineContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        GameObject goA = (GameObject)contact.getFixtureA().m_userData;
        GameObject goB = (GameObject)contact.getFixtureB().m_userData;
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c : goA.getAllComponents()) {
            c.beginCollision(goB, contact, aNormal);
        }

        for (Component c : goB.getAllComponents()) {
            c.beginCollision(goA, contact, bNormal);
        }
    }

    @Override
    public void endContact(Contact contact) {
        GameObject goA = (GameObject)contact.getFixtureA().m_userData;
        GameObject goB = (GameObject)contact.getFixtureB().m_userData;
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c : goA.getAllComponents()) {
            c.endCollision(goB, contact, aNormal);
        }

        for (Component c : goB.getAllComponents()) {
            c.endCollision(goA, contact, bNormal);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        GameObject goA = (GameObject)contact.getFixtureA().m_userData;
        GameObject goB = (GameObject)contact.getFixtureB().m_userData;
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c : goA.getAllComponents()) {
            c.preSolve(goB, contact, aNormal);
        }

        for (Component c : goB.getAllComponents()) {
            c.preSolve(goA, contact, bNormal);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        GameObject goA = (GameObject)contact.getFixtureA().m_userData;
        GameObject goB = (GameObject)contact.getFixtureB().m_userData;
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c : goA.getAllComponents()) {
            c.postSolve(goB, contact, aNormal);
        }

        for (Component c : goB.getAllComponents()) {
            c.postSolve(goA, contact, bNormal);
        }
    }
}
