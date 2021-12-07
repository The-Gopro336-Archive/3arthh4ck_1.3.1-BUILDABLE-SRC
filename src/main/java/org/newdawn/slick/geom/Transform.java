/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.geom;

import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.util.FastTrig;

public class Transform {
    private float[] matrixPosition;

    public Transform() {
        this.matrixPosition = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    }

    public Transform(Transform other) {
        this.matrixPosition = new float[9];
        for (int i = 0; i < 9; ++i) {
            this.matrixPosition[i] = other.matrixPosition[i];
        }
    }

    public Transform(Transform t1, Transform t2) {
        this(t1);
        this.concatenate(t2);
    }

    public Transform(float[] matrixPosition) {
        if (matrixPosition.length != 6) {
            throw new RuntimeException("The parameter must be a float array of length 6.");
        }
        this.matrixPosition = new float[]{matrixPosition[0], matrixPosition[1], matrixPosition[2], matrixPosition[3], matrixPosition[4], matrixPosition[5], 0.0f, 0.0f, 1.0f};
    }

    public Transform(float point00, float point01, float point02, float point10, float point11, float point12) {
        this.matrixPosition = new float[]{point00, point01, point02, point10, point11, point12, 0.0f, 0.0f, 1.0f};
    }

    public void transform(float[] source, int sourceOffset, float[] destination, int destOffset, int numberOfPoints) {
        int i;
        float[] result = source == destination ? new float[numberOfPoints * 2] : destination;
        for (i = 0; i < numberOfPoints * 2; i += 2) {
            for (int j = 0; j < 6; j += 3) {
                result[i + j / 3] = source[i + sourceOffset] * this.matrixPosition[j] + source[i + sourceOffset + 1] * this.matrixPosition[j + 1] + 1.0f * this.matrixPosition[j + 2];
            }
        }
        if (source == destination) {
            for (i = 0; i < numberOfPoints * 2; i += 2) {
                destination[i + destOffset] = result[i];
                destination[i + destOffset + 1] = result[i + 1];
            }
        }
    }

    public Transform concatenate(Transform tx) {
        float[] mp = new float[9];
        float n00 = this.matrixPosition[0] * tx.matrixPosition[0] + this.matrixPosition[1] * tx.matrixPosition[3];
        float n01 = this.matrixPosition[0] * tx.matrixPosition[1] + this.matrixPosition[1] * tx.matrixPosition[4];
        float n02 = this.matrixPosition[0] * tx.matrixPosition[2] + this.matrixPosition[1] * tx.matrixPosition[5] + this.matrixPosition[2];
        float n10 = this.matrixPosition[3] * tx.matrixPosition[0] + this.matrixPosition[4] * tx.matrixPosition[3];
        float n11 = this.matrixPosition[3] * tx.matrixPosition[1] + this.matrixPosition[4] * tx.matrixPosition[4];
        float n12 = this.matrixPosition[3] * tx.matrixPosition[2] + this.matrixPosition[4] * tx.matrixPosition[5] + this.matrixPosition[5];
        mp[0] = n00;
        mp[1] = n01;
        mp[2] = n02;
        mp[3] = n10;
        mp[4] = n11;
        mp[5] = n12;
        this.matrixPosition = mp;
        return this;
    }

    public String toString() {
        String result = "Transform[[" + this.matrixPosition[0] + "," + this.matrixPosition[1] + "," + this.matrixPosition[2] + "][" + this.matrixPosition[3] + "," + this.matrixPosition[4] + "," + this.matrixPosition[5] + "][" + this.matrixPosition[6] + "," + this.matrixPosition[7] + "," + this.matrixPosition[8] + "]]";
        return result.toString();
    }

    public float[] getMatrixPosition() {
        return this.matrixPosition;
    }

    public static Transform createRotateTransform(float angle) {
        return new Transform((float)FastTrig.cos(angle), -((float)FastTrig.sin(angle)), 0.0f, (float)FastTrig.sin(angle), (float)FastTrig.cos(angle), 0.0f);
    }

    public static Transform createRotateTransform(float angle, float x, float y) {
        Transform temp = Transform.createRotateTransform(angle);
        float sinAngle = temp.matrixPosition[3];
        float oneMinusCosAngle = 1.0f - temp.matrixPosition[4];
        temp.matrixPosition[2] = x * oneMinusCosAngle + y * sinAngle;
        temp.matrixPosition[5] = y * oneMinusCosAngle - x * sinAngle;
        return temp;
    }

    public static Transform createTranslateTransform(float xOffset, float yOffset) {
        return new Transform(1.0f, 0.0f, xOffset, 0.0f, 1.0f, yOffset);
    }

    public static Transform createScaleTransform(float xScale, float yScale) {
        return new Transform(xScale, 0.0f, 0.0f, 0.0f, yScale, 0.0f);
    }

    public Vector2f transform(Vector2f pt) {
        float[] in = new float[]{pt.x, pt.y};
        float[] out = new float[2];
        this.transform(in, 0, out, 0, 1);
        return new Vector2f(out[0], out[1]);
    }
}

