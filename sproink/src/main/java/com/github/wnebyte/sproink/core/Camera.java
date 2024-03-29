package com.github.wnebyte.sproink.core;

import java.util.Objects;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Matrix4f;

public class Camera {

    private static final float PROJECTION_WIDTH = 6;

    private static final float PROJECTION_HEIGHT = 3;

    private Vector2f position;

    private Matrix4f projectionMatrix;

    private Matrix4f viewMatrix;

    private Matrix4f inverseProjection;

    private Matrix4f inverseView;

    private final Vector2f projectionSize = new Vector2f(PROJECTION_WIDTH, PROJECTION_HEIGHT);

    private final Vector4f clearColor = new Vector4f(1, 1, 1, 1);

    private float zoom = 1.0f;

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjection = new Matrix4f();
        this.inverseView = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, projectionSize.x * zoom, 0.0f, projectionSize.y * zoom,
                0.0f, 100.0f);
        projectionMatrix.invert(inverseProjection);
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        viewMatrix.identity();
        viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f),
                cameraFront.add(position.x, position.y, 0.0f),
                cameraUp);
        viewMatrix.invert(inverseView);
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getInverseProjection() {
        return inverseProjection;
    }

    public Matrix4f getInverseView() {
        return inverseView;
    }

    public Vector2f getProjectionSize() {
        return projectionSize;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector4f getClearColor() {
        return clearColor;
    }

    public void setClearColor(Vector4f color) {
        clearColor.set(color);
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public void addZoom(float value) {
        this.zoom += value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Camera)) return false;
        Camera camera = (Camera) o;
        return Objects.equals(camera.position, this.position) &&
                Objects.equals(camera.viewMatrix, this.viewMatrix) &&
                Objects.equals(camera.projectionMatrix, this.projectionMatrix) &&
                super.equals(camera);
    }

    @Override
    public int hashCode() {
        int result = 98;
        return result +
                2 *
                15 +
                Objects.hashCode(this.position) +
                Objects.hashCode(this.viewMatrix) +
                Objects.hashCode(this.projectionMatrix) +
                super.hashCode();
    }

    @Override
    public String toString() {
        return String.format(
                "Camera[position: %s, viewMatrix: %s, projectionMatrix: %s]", position, viewMatrix, projectionMatrix
        );
    }
}
