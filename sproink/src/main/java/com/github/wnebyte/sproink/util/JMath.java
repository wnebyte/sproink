package com.github.wnebyte.sproink.util;

import java.lang.Math;
import org.joml.*;

public class JMath {

    public static float sum(Vector4f vec) {
        return (vec.x + vec.y + vec.z + vec.w);
    }

    public static float sum(Vector3f vec) {
        return (vec.x + vec.y + vec.z);
    }

    public static float sum(Vector2f vec) {
        return (vec.x + vec.y);
    }

    public static int sum(Vector4i vec) {
        return (vec.x + vec.y + vec.z + vec.w);
    }

    public static int sum(Vector3i vec) {
        return (vec.x + vec.y + vec.z);
    }

    public static int sum(Vector2i vec) {
        return (vec.x + vec.y);
    }

    public static float multiply(Vector4f vec) {
        return (vec.x * vec.y * vec.z * vec.w);
    }

    public static float multiply(Vector3f vec) {
        return (vec.x * vec.y * vec.z);
    }

    public static float multiply(Vector2f vec) {
        return (vec.x * vec.y);
    }

    public static int multiply(Vector4i vec) {
        return (vec.x * vec.y * vec.z * vec.w);
    }

    public static int multiply(Vector3i vec) {
        return (vec.x * vec.y * vec.z);
    }

    public static int multiply(Vector2i vec) {
        return (vec.x * vec.y);
    }

    public static int sum(int[] array) {
        if (array == null) return 0;
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum;
    }

    public static float sum(float[] array) {
        if (array == null) return 0;
        float sum = 0.0f;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum;
    }

    public static void sub(int[] array, int value) {
        if (array == null) return;
        for (int i = 0; i < array.length; i++) {
            array[i] -= value;
        }
    }

    public static void sub(float[] array, float value) {
        if (array == null) return;
        for (int i = 0; i < array.length; i++) {
            array[i] -= value;
        }
    }

    public static void add(int[] array, int value) {
        if (array == null) return;
        for (int i = 0; i < array.length; i++) {
            array[i] += value;
        }
    }

    public static void add(float[] array, float value) {
        if (array == null) return;
        for (int i = 0; i < array.length; i++) {
            array[i] += value;
        }
    }

    public static void multiply(int[] array, int value) {
        if (array == null) return;
        for (int i = 0; i < array.length; i++) {
            array[i] *= value;
        }
    }

    public static void multiply(float[] array, float value) {
        if (array == null) return;
        for (int i = 0; i < array.length; i++) {
            array[i] *= value;
        }
    }

    public static void divide(int[] array, int value) {
        if (array == null) return;
        for (int i = 0; i < array.length; i++) {
            array[i] /= value;
        }
    }

    public static void divide(float[] array, float value) {
        if (array == null) return;
        for (int i = 0; i < array.length; i++) {
            array[i] /= value;
        }
    }

    public static void rotate(Vector2f vec, float angleDeg, Vector2f origin) {
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float cos = (float)Math.cos(Math.toRadians(angleDeg));
        float sin = (float)Math.sin(Math.toRadians(angleDeg));

        float xPrime = (x * cos) - (y * sin);
        float yPrime = (x * sin) + (y * cos);

        xPrime += origin.x;
        yPrime += origin.y;

        vec.x = xPrime;
        vec.y = yPrime;
    }

    public static boolean compare(float x, float y, float epsilon) {
        return Math.abs(x - y) <= epsilon * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2, float epsilon) {
        return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon);
    }

    public static boolean compare(float x, float y) {
        return Math.abs(x - y) <= Float.MIN_VALUE * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2) {
        return compare(vec1.x, vec2.x) && compare(vec1.y, vec2.y);
    }
}
