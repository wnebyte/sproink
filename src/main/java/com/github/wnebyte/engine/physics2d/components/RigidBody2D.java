package com.github.wnebyte.engine.physics2d.components;

import org.joml.Vector2f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.physics2d.enums.BodyType;

public class RigidBody2D extends Component {

    private Vector2f velocity = new Vector2f();

    private float angularDamping = 0.8f;

    private float linearDamping = 0.9f;

    private float mass = 0.0f;

    private BodyType bodyType = BodyType.DYNAMIC;

    private float friction = 0.1f;

    private float angularVelocity = 0.0f;

    private float gravityScale = 1.0f;

    private boolean isSensor = false;

    private boolean fixedRotation = false;

    private boolean continuousCollision = true;

    private transient Body rawBody = null;

    @Override
    public void update(float dt) {
        if (rawBody != null) {
            gameObject.transform.position.set(rawBody.getPosition().x, rawBody.getPosition().y);
            gameObject.transform.rotation = (float)Math.toDegrees(rawBody.getAngle());
        }
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);
        if (rawBody != null) {
            rawBody.setLinearVelocity(new Vec2(velocity.x, velocity.y));
        }
    }

    public void addVelocity(Vector2f velocity) {
        if (rawBody != null) {
            rawBody.applyForceToCenter(new Vec2(velocity.x, velocity.y));
        }
    }

    public void addImpulse(Vector2f impulse) {
        if (rawBody != null) {
            rawBody.applyLinearImpulse(new Vec2(impulse.x, impulse.y), rawBody.getWorldCenter());
        }
    }

    public float getAngularDamping() {
        return angularDamping;
    }

    public void setAngularDamping(float angularDamping) {
        this.angularDamping = angularDamping;
    }

    public float getLinearDamping() {
        return linearDamping;
    }

    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public boolean isFixedRotation() {
        return fixedRotation;
    }

    public void setFixedRotation(boolean fixedRotation) {
        this.fixedRotation = fixedRotation;
    }

    public boolean isContinuousCollision() {
        return continuousCollision;
    }

    public void setContinuousCollision(boolean continuousCollision) {
        this.continuousCollision = continuousCollision;
    }

    public Body getRawBody() {
        return rawBody;
    }

    public void setRawBody(Body rawBody) {
        this.rawBody = rawBody;
    }

    public float getFriction() {
        return friction;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
        if (rawBody != null) {
            rawBody.setAngularVelocity(angularVelocity);
        }
    }

    public float getGravityScale() {
        return gravityScale;
    }

    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
        if (rawBody != null) {
            rawBody.setGravityScale(gravityScale);
        }
    }

    public boolean isSensor() {
        return isSensor;
    }

    public void setIsSensor() {
        this.isSensor = true;
        if (rawBody != null) {
            Window.getPhysics2d().setIsSensor(this);
        }
    }

    public void setNotSensor() {
        this.isSensor = false;
        if (rawBody != null) {
            Window.getPhysics2d().setIsSensor(this);
        }
    }
}
