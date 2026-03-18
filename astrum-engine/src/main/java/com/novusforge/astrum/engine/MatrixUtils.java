package com.novusforge.astrum.engine;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class MatrixUtils {
    
    public static Matrix4f createProjectionMatrix(float fov, float aspect, float near, float far) {
        return new Matrix4f().setPerspective((float) Math.toRadians(fov), aspect, near, far, true);
    }
    
    public static Matrix4f createViewMatrix(Vector3fc position, float pitch, float yaw) {
        Matrix4f view = new Matrix4f();
        
        float cosPitch = (float) Math.cos(pitch);
        float sinPitch = (float) Math.sin(pitch);
        float cosYaw = (float) Math.cos(yaw);
        float sinYaw = (float) Math.sin(yaw);
        
        float x = -cosPitch * sinYaw;
        float y = sinPitch;
        float z = -cosPitch * cosYaw;
        
        float magnitude = (float) Math.sqrt(x*x + y*y + z*z);
        x /= magnitude;
        y /= magnitude;
        z /= magnitude;
        
        float lookX = position.x() + x;
        float lookY = position.y() + y;
        float lookZ = position.z() + z;
        
        view.setLookAt(
            position.x(), position.y(), position.z(),
            lookX, lookY, lookZ,
            0, 1, 0
        );
        
        return view;
    }
    
    public static Matrix4f createModelMatrix(Vector3fc offset) {
        return new Matrix4f().translate(offset.x(), offset.y(), offset.z());
    }
    
    public static void createMVPMatrices(Matrix4f result, Matrix4fc model, Matrix4fc view, Matrix4fc projection) {
        result.set(projection).mul(view).mul(model);
    }
    
    public static float[] toFloatArray(Matrix4fc matrix) {
        float[] arr = new float[16];
        matrix.get(arr);
        return arr;
    }
}
