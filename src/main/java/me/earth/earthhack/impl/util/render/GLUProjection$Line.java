package me.earth.earthhack.impl.util.render;

import me.earth.earthhack.impl.util.render.GLUProjection;

public class GLUProjection$Line {
    public GLUProjection.Vector3D sourcePoint = new GLUProjection.Vector3D(0.0, 0.0, 0.0);
    public GLUProjection.Vector3D direction = new GLUProjection.Vector3D(0.0, 0.0, 0.0);

    public GLUProjection$Line(double sx, double sy, double sz, double dx, double dy, double dz) {
        this.sourcePoint.x = sx;
        this.sourcePoint.y = sy;
        this.sourcePoint.z = sz;
        this.direction.x = dx;
        this.direction.y = dy;
        this.direction.z = dz;
    }

    public GLUProjection.Vector3D intersect(GLUProjection$Line line) {
        double a = this.sourcePoint.x;
        double b = this.direction.x;
        double c = line.sourcePoint.x;
        double d = line.direction.x;
        double e = this.sourcePoint.y;
        double f = this.direction.y;
        double g = line.sourcePoint.y;
        double h = line.direction.y;
        double te = -(a * h - c * h - d * (e - g));
        double be = b * h - d * f;
        if (be == 0.0) {
            return this.intersectXZ(line);
        }
        double t = te / be;
        GLUProjection.Vector3D result = new GLUProjection.Vector3D(0.0, 0.0, 0.0);
        result.x = this.sourcePoint.x + this.direction.x * t;
        result.y = this.sourcePoint.y + this.direction.y * t;
        result.z = this.sourcePoint.z + this.direction.z * t;
        return result;
    }

    private GLUProjection.Vector3D intersectXZ(GLUProjection$Line line) {
        double a = this.sourcePoint.x;
        double b = this.direction.x;
        double c = line.sourcePoint.x;
        double d = line.direction.x;
        double e = this.sourcePoint.z;
        double f = this.direction.z;
        double g = line.sourcePoint.z;
        double h = line.direction.z;
        double te = -(a * h - c * h - d * (e - g));
        double be = b * h - d * f;
        if (be == 0.0) {
            return this.intersectYZ(line);
        }
        double t = te / be;
        GLUProjection.Vector3D result = new GLUProjection.Vector3D(0.0, 0.0, 0.0);
        result.x = this.sourcePoint.x + this.direction.x * t;
        result.y = this.sourcePoint.y + this.direction.y * t;
        result.z = this.sourcePoint.z + this.direction.z * t;
        return result;
    }

    private GLUProjection.Vector3D intersectYZ(GLUProjection$Line line) {
        double a = this.sourcePoint.y;
        double b = this.direction.y;
        double c = line.sourcePoint.y;
        double d = line.direction.y;
        double e = this.sourcePoint.z;
        double f = this.direction.z;
        double g = line.sourcePoint.z;
        double h = line.direction.z;
        double te = -(a * h - c * h - d * (e - g));
        double be = b * h - d * f;
        if (be == 0.0) {
            return null;
        }
        double t = te / be;
        GLUProjection.Vector3D result = new GLUProjection.Vector3D(0.0, 0.0, 0.0);
        result.x = this.sourcePoint.x + this.direction.x * t;
        result.y = this.sourcePoint.y + this.direction.y * t;
        result.z = this.sourcePoint.z + this.direction.z * t;
        return result;
    }

    public GLUProjection.Vector3D intersectPlane(GLUProjection.Vector3D pointOnPlane, GLUProjection.Vector3D planeNormal) {
        GLUProjection.Vector3D result = new GLUProjection.Vector3D(this.sourcePoint.x, this.sourcePoint.y, this.sourcePoint.z);
        double d = pointOnPlane.sub(this.sourcePoint).dot(planeNormal) / this.direction.dot(planeNormal);
        result.sadd(this.direction.mul(d));
        if (this.direction.dot(planeNormal) == 0.0) {
            return null;
        }
        return result;
    }
}
