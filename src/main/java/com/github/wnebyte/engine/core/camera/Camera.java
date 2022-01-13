package com.github.wnebyte.engine.core.camera;

import java.util.Objects;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Matrix4f;

public class Camera {

    private static final float WIDTH = 32.0f * 40.0f;

    public Vector2f position;

    private Matrix4f projectionMatrix;

    private Matrix4f viewMatrix;

    private Matrix4f inverseProjection;

    private Matrix4f inverseView;

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
        projectionMatrix.ortho(0.0f, 32.0f * 40.0f, 0.0f, 32.0f * 21.0f, 0.0f, 100.0f);
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
