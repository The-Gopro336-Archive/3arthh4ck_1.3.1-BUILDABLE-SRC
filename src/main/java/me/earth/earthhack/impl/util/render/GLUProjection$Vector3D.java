package me.earth.earthhack.impl.util.render;

public class GLUProjection$Vector3D {
    public double x;
    public double y;
    public double z;

    public GLUProjection$Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public GLUProjection$Vector3D add(GLUProjection$Vector3D v) {
        return new GLUProjection$Vector3D(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    public GLUProjection$Vector3D add(double x, double y, double z) {
        return new GLUProjection$Vector3D(this.x + x, this.y + y, this.z + z);
    }

    public GLUProjection$Vector3D sub(GLUProjection$Vector3D v) {
        return new GLUProjection$Vector3D(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    public GLUProjection$Vector3D sub(double x, double y, double z) {
        return new GLUProjection$Vector3D(this.x - x, this.y - y, this.z - z);
    }

    public GLUProjection$Vector3D normalized() {
        double len = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        return new GLUProjection$Vector3D(this.x / len, this.y / len, this.z / len);
    }

    public double dot(GLUProjection$Vector3D v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public GLUProjection$Vector3D cross(GLUProjection$Vector3D v) {
        return new GLUProjection$Vector3D(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
    }

    public GLUProjection$Vector3D mul(double m) {
        return new GLUProjection$Vector3D(this.x * m, this.y * m, this.z * m);
    }

    public GLUProjection$Vector3D div(double d) {
        return new GLUProjection$Vector3D(this.x / d, this.y / d, this.z / d);
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public GLUProjection$Vector3D sadd(GLUProjection$Vector3D v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
        return this;
    }

    public GLUProjection$Vector3D sadd(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public GLUProjection$Vector3D ssub(GLUProjection$Vector3D v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
        return this;
    }

    public GLUProjection$Vector3D ssub(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public GLUProjection$Vector3D snormalize() {
        double len = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        this.x /= len;
        this.y /= len;
        this.z /= len;
        return this;
    }

    public GLUProjection$Vector3D scross(GLUProjection$Vector3D v) {
        this.x = this.y * v.z - this.z * v.y;
        this.y = this.z * v.x - this.x * v.z;
        this.z = this.x * v.y - this.y * v.x;
        return this;
    }

    public GLUProjection$Vector3D smul(double m) {
        this.x *= m;
        this.y *= m;
        this.z *= m;
        return this;
    }

    public GLUProjection$Vector3D sdiv(double d) {
        this.x /= d;
        this.y /= d;
        this.z /= d;
        return this;
    }

    public String toString() {
        return "(X: " + this.x + " Y: " + this.y + " Z: " + this.z + ")";
    }
}
