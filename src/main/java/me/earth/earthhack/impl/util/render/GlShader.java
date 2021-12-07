/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.Vec2f
 *  net.minecraft.util.math.Vec3d
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.util.vector.Vector4f
 */
package me.earth.earthhack.impl.util.render;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector4f;

public class GlShader {
    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private Map<String, Integer> uniforms;

    public GlShader(String name) {
        String source;
        InputStream sourceStream = GlShader.class.getResourceAsStream("/shaders/" + name + ".shader");
        if (sourceStream == null) {
            return;
        }
        try {
            source = GlShader.readStreamToString(sourceStream);
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }
        StringBuilder vertexSource = new StringBuilder(source.length() / 2);
        StringBuilder fragmentSource = new StringBuilder(source.length() / 2);
        int mode = -1;
        for (String line : source.split("\n")) {
            if (line.contains("#shader vert")) {
                mode = 0;
                continue;
            }
            if (line.contains("#shader frag")) {
                mode = 1;
                continue;
            }
            if (mode == 0) {
                vertexSource.append(line).append("\n");
                continue;
            }
            if (mode != 1) continue;
            fragmentSource.append(line).append("\n");
        }
        int vertId = GL20.glCreateShader((int)35633);
        int fragId = GL20.glCreateShader((int)35632);
        GL20.glShaderSource((int)vertId, (CharSequence)vertexSource);
        GL20.glShaderSource((int)fragId, (CharSequence)fragmentSource);
        GL20.glCompileShader((int)vertId);
        GL20.glCompileShader((int)fragId);
        if (GL20.glGetShaderi((int)vertId, (int)35713) == 0) {
            String error = GL20.glGetShaderInfoLog((int)vertId, (int)1024);
            System.err.println("Vertex shader " + name + " could not compile: " + error);
        }
        if (GL20.glGetShaderi((int)fragId, (int)35713) == 0) {
            String error = GL20.glGetShaderInfoLog((int)fragId, (int)1024);
            System.err.println("Fragment shader " + name + " could not compile: " + error);
        }
        int programId = GL20.glCreateProgram();
        GlShader shader = new GlShader(programId, vertId, fragId);
        GL20.glAttachShader((int)programId, (int)vertId);
        GL20.glAttachShader((int)programId, (int)fragId);
        GL20.glLinkProgram((int)programId);
        if (GL20.glGetProgrami((int)programId, (int)35714) == 0) {
            String error = GL20.glGetShaderInfoLog((int)programId, (int)1024);
            System.err.println("Shader " + name + " could not be linked: " + error);
        }
        GL20.glDetachShader((int)programId, (int)vertId);
        GL20.glDetachShader((int)programId, (int)fragId);
        GL20.glValidateProgram((int)programId);
    }

    public GlShader(int programId, int vertexShaderId, int fragmentShaderId) {
        this.programId = programId;
        this.vertexShaderId = vertexShaderId;
        this.fragmentShaderId = fragmentShaderId;
        this.uniforms = new HashMap<String, Integer>();
    }

    public void bind() {
        GL20.glUseProgram((int)this.programId);
    }

    public void unbind() {
        GL20.glUseProgram((int)0);
    }

    public void finalize() {
        this.unbind();
        GL20.glDeleteProgram((int)this.programId);
    }

    public int createUniform(String uniformName) {
        if (this.uniforms.containsKey(uniformName)) {
            return this.uniforms.get(uniformName);
        }
        int location = GL20.glGetUniformLocation((int)this.programId, (CharSequence)uniformName);
        this.uniforms.put(uniformName, location);
        return location;
    }

    public void set(String uniformName, int value) {
        GL20.glUniform1i((int)this.createUniform(uniformName), (int)value);
    }

    public void set(String uniformName, float value) {
        GL20.glUniform1f((int)this.createUniform(uniformName), (float)value);
    }

    public void set(String uniformName, boolean value) {
        GL20.glUniform1i((int)this.createUniform(uniformName), (int)(value ? 1 : 0));
    }

    public void set(String uniformName, Vec2f value) {
        GL20.glUniform2f((int)this.createUniform(uniformName), (float)value.x, (float)value.y);
    }

    public void set(String uniformName, Vec3d value) {
        GL20.glUniform3f((int)this.createUniform(uniformName), (float)((float)value.x), (float)((float)value.y), (float)((float)value.z));
    }

    public void set(String uniformName, Vector4f value) {
        GL20.glUniform4f((int)this.createUniform(uniformName), (float)value.x, (float)value.y, (float)value.z, (float)value.w);
    }

    public void set(String uniformName, Color value) {
        GL20.glUniform4f((int)this.createUniform(uniformName), (float)((float)value.getRed() / 255.0f), (float)((float)value.getBlue() / 255.0f), (float)((float)value.getGreen() / 255.0f), (float)((float)value.getAlpha() / 255.0f));
    }

    public int getVertexShaderId() {
        return this.vertexShaderId;
    }

    public int getFragmentShaderId() {
        return this.fragmentShaderId;
    }

    private static String readStreamToString(InputStream inputStream) throws IOException {
        int read;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[512];
        while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, read);
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    public static GlShader createShader(String name) {
        String source;
        InputStream sourceStream = GlShader.class.getResourceAsStream("/shaders/" + name + ".shader");
        if (sourceStream == null) {
            return null;
        }
        try {
            source = GlShader.readStreamToString(sourceStream);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        StringBuilder vertexSource = new StringBuilder(source.length() / 2);
        StringBuilder fragmentSource = new StringBuilder(source.length() / 2);
        int mode = -1;
        for (String line : source.split("\n")) {
            if (line.contains("#shader vert")) {
                mode = 0;
                continue;
            }
            if (line.contains("#shader frag")) {
                mode = 1;
                continue;
            }
            if (mode == 0) {
                vertexSource.append(line).append("\n");
                continue;
            }
            if (mode != 1) continue;
            fragmentSource.append(line).append("\n");
        }
        int vertId = GL20.glCreateShader((int)35633);
        int fragId = GL20.glCreateShader((int)35632);
        GL20.glShaderSource((int)vertId, (CharSequence)vertexSource);
        GL20.glShaderSource((int)fragId, (CharSequence)fragmentSource);
        GL20.glCompileShader((int)vertId);
        GL20.glCompileShader((int)fragId);
        if (GL20.glGetShaderi((int)vertId, (int)35713) == 0) {
            String error = GL20.glGetShaderInfoLog((int)vertId, (int)1024);
            System.err.println("Vertex shader " + name + " could not compile: " + error);
        }
        if (GL20.glGetShaderi((int)fragId, (int)35713) == 0) {
            String error = GL20.glGetShaderInfoLog((int)fragId, (int)1024);
            System.err.println("Fragment shader " + name + " could not compile: " + error);
        }
        int programId = GL20.glCreateProgram();
        GlShader shader = new GlShader(programId, vertId, fragId);
        GL20.glAttachShader((int)programId, (int)vertId);
        GL20.glAttachShader((int)programId, (int)fragId);
        GL20.glLinkProgram((int)programId);
        if (GL20.glGetProgrami((int)programId, (int)35714) == 0) {
            String error = GL20.glGetShaderInfoLog((int)programId, (int)1024);
            System.err.println("Shader " + name + " could not be linked: " + error);
        }
        GL20.glDetachShader((int)programId, (int)vertId);
        GL20.glDetachShader((int)programId, (int)fragId);
        GL20.glValidateProgram((int)programId);
        return shader;
    }
}

