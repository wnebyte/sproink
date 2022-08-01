package com.github.wnebyte.engine.physics2d;

import org.joml.Vector2f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.callbacks.RayCastCallback;
import com.github.wnebyte.engine.core.ecs.GameObject;

public class RaycastInfo implements RayCastCallback {

    public Fixture fixture;

    public Vector2f point;

    public Vector2f normal;

    public float fraction;

    public boolean hit;

    private GameObject reqGo;

    private GameObject hitGo;

    public RaycastInfo(GameObject reqGo) {
        this.fixture = null;
        this.point = new Vector2f();
        this.normal = new Vector2f();
        this.fraction = 0.0f;
        this.hit = false;
        this.hitGo = null;
        this.reqGo = reqGo;
    }

    @Override
    public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
        if (fixture.m_userData.equals(reqGo)) {
            return 1;
        }
        this.fixture = fixture;
        this.point = new Vector2f(point.x, point.y);
        this.normal = new Vector2f(normal.x, normal.y);
        this.fraction = fraction;
        this.hit = (fraction != 0);
        this.hitGo = (GameObject)fixture.m_userData;
        return fraction;
    }

    public GameObject getReqGo() {
        return reqGo;
    }

    public GameObject getHitGo() {
        return hitGo;
    }
}
