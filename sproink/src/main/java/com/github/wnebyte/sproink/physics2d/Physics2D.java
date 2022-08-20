package com.github.wnebyte.sproink.physics2d;

import org.joml.Vector2f;
import org.jbox2d.dynamics.*;
import org.jbox2d.common.Vec2;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.core.Transform;
import com.github.wnebyte.sproink.components.Ground;
import com.github.wnebyte.sproink.physics2d.components.Box2DCollider;
import com.github.wnebyte.sproink.physics2d.components.CircleCollider;
import com.github.wnebyte.sproink.physics2d.components.PillboxCollider;
import com.github.wnebyte.sproink.physics2d.components.RigidBody2D;

public class Physics2D {

    private final Vec2 gravity = new Vec2(0, -10.0f);

    private final World world = new World(gravity);

    private float physicsTime = 0.0f;

    private float physicsTimeStep = 1.0f / 60.0f;

    private int velocityIterations = 8;

    private int positionIterations = 3;

    public Physics2D() {
        this.world.setContactListener(new EngineContactListener());
    }

    public void add(GameObject go) {
        RigidBody2D rb = go.getComponent(RigidBody2D.class);
        if (rb != null && rb.getRawBody() == null) {
            Transform transform = go.transform;

            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float)Math.toRadians(transform.rotation);
            bodyDef.position.set(transform.position.x, transform.position.y);
            bodyDef.angularDamping = rb.getAngularDamping();
            bodyDef.linearDamping = rb.getLinearDamping();
            bodyDef.fixedRotation = rb.isFixedRotation();
            bodyDef.bullet = rb.isContinuousCollision();
            bodyDef.gravityScale = rb.getGravityScale();
            bodyDef.angularVelocity = rb.getAngularVelocity();
            bodyDef.userData = rb.gameObject;

            switch (rb.getBodyType()) {
                case STATIC:
                    bodyDef.type = BodyType.STATIC;
                    break;
                case DYNAMIC:
                    bodyDef.type = BodyType.DYNAMIC;
                    break;
                case KINEMATIC:
                    bodyDef.type = BodyType.KINEMATIC;
                    break;
            }

            Body body = world.createBody(bodyDef);
            body.m_mass = rb.getMass();
            rb.setRawBody(body);

            CircleCollider circleCollider;
            Box2DCollider boxCollider;
            PillboxCollider pboxCollider;

            if ((circleCollider = go.getComponent(CircleCollider.class)) != null) {
               addCircleCollider(rb, circleCollider);
            }

            if ((boxCollider = go.getComponent(Box2DCollider.class)) != null) {
                addBox2DCollider(rb, boxCollider);
            }

            if ((pboxCollider = go.getComponent(PillboxCollider.class)) != null) {
                addPillboxCollider(rb, pboxCollider);
            }

        }
    }

    public void destroyGameObject(GameObject go) {
        RigidBody2D rb = go.getComponent(RigidBody2D.class);
        if (rb != null) {
            if (rb.getRawBody() != null) {
                world.destroyBody(rb.getRawBody());
                rb.setRawBody(null);
            }
        }
    }

    public void update(float dt) {
        physicsTime += dt;
        if (physicsTime >= 0.0f) {
            physicsTime -= physicsTimeStep;
            world.step(physicsTimeStep, velocityIterations, positionIterations);
        }
    }

    public void setIsSensor(RigidBody2D rb) {
        Body body = rb.getRawBody();
        if (body == null) return;

        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture.m_isSensor = true;
            fixture = fixture.getNext();
        }
    }

    public void setNotSensor(RigidBody2D rb) {
        Body body = rb.getRawBody();
        if (body == null) return;

        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture.m_isSensor = false;
            fixture = fixture.getNext();
        }
    }

    public boolean isLocked() {
        return world.isLocked();
    }

    public Vector2f getGravity() {
        return new Vector2f(gravity.x, gravity.y);
    }

    public RaycastInfo raycast(GameObject reqGo, Vector2f point1, Vector2f point2) {
        RaycastInfo callback = new RaycastInfo(reqGo);
        world.raycast(callback, new Vec2(point1.x, point1.y),
                new Vec2(point2.x, point2.y));
        return callback;
    }

    public void resetBox2DCollider(RigidBody2D rb, Box2DCollider collider) {
        Body body = rb.getRawBody();
        if (body == null) return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addBox2DCollider(rb, collider);
        body.resetMassData();
    }

    public void resetCircleCollider(RigidBody2D rb, CircleCollider collider) {
        Body body = rb.getRawBody();
        if (body == null) return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addCircleCollider(rb, collider);
        body.resetMassData();
    }

    public void resetPillboxCollider(RigidBody2D rb, PillboxCollider collider) {
        Body body = rb.getRawBody();
        if (body == null) return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addPillboxCollider(rb, collider);
        body.resetMassData();
    }

    private void addBox2DCollider(RigidBody2D rb, Box2DCollider collider) {
        Body body = rb.getRawBody();
        assert (body != null) : "Raw body must not be null";

        PolygonShape shape = new PolygonShape();
        Vector2f halfSize = new Vector2f(collider.getHalfSize()).mul(0.5f);
        Vector2f offset = new Vector2f(collider.getOffset());
        shape.setAsBox(halfSize.x, halfSize.y, new Vec2(offset.x, offset.y), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = collider.gameObject;
        fixtureDef.isSensor = rb.isSensor();
        body.createFixture(fixtureDef);
    }

    private void addCircleCollider(RigidBody2D rb, CircleCollider collider) {
        Body body = rb.getRawBody();
        assert (body != null) : "Raw body must not be null";

        CircleShape shape = new CircleShape();
        shape.setRadius(collider.getRadius());
        shape.m_p.set(collider.getOffset().x, collider.getOffset().y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rb.getFriction();
        fixtureDef.userData = collider.gameObject;
        fixtureDef.isSensor = rb.isSensor();
        body.createFixture(fixtureDef);
    }

    private void addPillboxCollider(RigidBody2D rb, PillboxCollider collider) {
        Body body = rb.getRawBody();
        assert (body != null) : "Raw body must not be null";

        addBox2DCollider(rb, collider.getBox());
        addCircleCollider(rb, collider.getBottomCircle());
    }

    private int fixtureListSize(Body body) {
        int size = 0;
        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture = fixture.getNext();
            size++;
        }
        return size;
    }

    public boolean checkOnGround(
            GameObject gameObject,
            float innerPlayerWidth,
            float height) {
        Vector2f raycastBegin = new Vector2f(gameObject.transform.position);
        raycastBegin.sub(innerPlayerWidth / 2.0f, 0.0f);
        Vector2f raycastEnd = new Vector2f(raycastBegin).add(0.0f, height);
        RaycastInfo info = raycast(gameObject, raycastBegin, raycastEnd);

        Vector2f raycast2Begin = new Vector2f(raycastBegin).add(innerPlayerWidth, 0.0f);
        Vector2f raycast2End = new Vector2f(raycastEnd).add(innerPlayerWidth, 0.0f);
        RaycastInfo info2 = raycast(gameObject, raycast2Begin, raycast2End);

       // DebugDraw.addLine2D(raycastBegin, raycastEnd, new Vector3f(1, 0, 0));
       // DebugDraw.addLine2D(raycast2Begin, raycast2End, new Vector3f(1, 0, 0));

        return (info.hit && info.getHitGo() != null && info.getHitGo().getComponent(Ground.class) != null) ||
                (info2.hit && info2.getHitGo() != null && info2.getHitGo().getComponent(Ground.class) != null);
    }
}
