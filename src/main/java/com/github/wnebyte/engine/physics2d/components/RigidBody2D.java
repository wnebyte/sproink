package com.github.wnebyte.engine.physics2d.components;

import org.joml.Vector2f;
import org.jbox2d.dynamics.Body;
import com.github.wnebyte.engine.physics2d.enums.BodyType;
import com.github.wnebyte.engine.core.ecs.Component;

public class RigidBody2D extends Component {

    private Vector2f velocity = new Vector2f();

    private float angularDamping = 0.8f;

    private float linearDamping = 0.9f;

    private float mass = 0;

    private BodyType bodyType = BodyType.DYNAMIC;

    private boolean fixedRotation = false;

    private boolean continuousCollision = true;

    private Body rawBody = null;

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
        this.velocity = velocity;
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
}
