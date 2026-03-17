package com.novusforge.astrum.infiniminer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Camera - Handles 3D camera and view matrix.
 * Ported from C# XNA to Java 21 LWJGL3.
 */
public class Camera {
    
    public Vector3f position = new Vector3f(0, 10, 0);
    public Vector3f velocity = new Vector3f(0, 0, 0);
    
    public float rotationY = 0; // Yaw
    public float rotationX = 0;  // Pitch
    
    private float fov = 70.0f;
    private float aspectRatio = 16.0f / 9.0f;
    private float nearPlane = 0.1f;
    private float farPlane = 1000.0f;
    
    public Camera() {
        System.out.println("Camera initialized");
    }
    
    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }
    
    public void setRotation(float yaw, float pitch) {
        this.rotationY = yaw;
        this.rotationX = pitch;
    }
    
    public void rotate(float deltaYaw, float deltaPitch) {
        this.rotationY += deltaYaw;
        this.rotationX += deltaPitch;
        
        // Clamp pitch
        this.rotationX = Math.max(-(float) Math.PI / 2 + 0.01f, 
            Math.min((float) Math.PI / 2 - 0.01f, this.rotationX));
    }
    
    public Vector3f getForward() {
        Vector3f forward = new Vector3f();
        forward.x = (float) (Math.sin(rotationY) * Math.cos(rotationX));
        forward.y = (float) Math.sin(rotationX);
        forward.z = (float) (Math.cos(rotationY) * Math.cos(rotationX));
        return forward;
    }
    
    public Vector3f getRight() {
        Vector3f right = new Vector3f();
        right.x = (float) Math.cos(rotationY);
        right.y = 0;
        right.z = (float) -Math.sin(rotationY);
        return right;
    }
    
    public Vector3f getUp() {
        Vector3f forward = getForward();
        Vector3f right = getRight();
        Vector3f up = new Vector3f();
        up.cross(right, forward);
        return up;
    }
    
    public Matrix4f getViewMatrix() {
        Matrix4f view = new Matrix4f();
        
        Vector3f target = new Vector3f(
            position.x + getForward().x,
            position.y + getForward().y,
            position.z + getForward().z
        );
        
        view.lookAt(position, target, getUp());
        
        return view;
    }
    
    public Matrix4f getProjectionMatrix() {
        Matrix4f projection = new Matrix4f();
        float fovRad = (float) Math.toRadians(fov);
        projection.perspective(fovRad, aspectRatio, nearPlane, farPlane);
        return projection;
    }
    
    public Matrix4f getViewProjectionMatrix() {
        return getProjectionMatrix().mul(getViewMatrix());
    }
    
    public void setFOV(float fov) {
        this.fov = fov;
    }
    
    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }
    
    public void setNearPlane(float near) {
        this.nearPlane = near;
    }
    
    public void setFarPlane(float far) {
        this.farPlane = far;
    }
    
    public float getFOV() {
        return fov;
    }
    
    public float getAspectRatio() {
        return aspectRatio;
    }
}
