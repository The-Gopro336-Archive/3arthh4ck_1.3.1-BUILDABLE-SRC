/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.particles;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.util.Log;

public class ParticleSystem {
    protected SGL GL = Renderer.get();
    public static final int BLEND_ADDITIVE = 1;
    public static final int BLEND_COMBINE = 2;
    private static final int DEFAULT_PARTICLES = 100;
    private ArrayList removeMe = new ArrayList();
    protected HashMap particlesByEmitter = new HashMap();
    protected int maxParticlesPerEmitter;
    protected ArrayList emitters = new ArrayList();
    protected Particle dummy;
    private int blendingMode = 2;
    private int pCount;
    private boolean usePoints;
    private float x;
    private float y;
    private boolean removeCompletedEmitters = true;
    private Image sprite;
    private boolean visible = true;
    private String defaultImageName;
    private Color mask;

    public static void setRelativePath(String path) {
        ConfigurableEmitter.setRelativePath(path);
    }

    public ParticleSystem(Image defaultSprite) {
        this(defaultSprite, 100);
    }

    public ParticleSystem(String defaultSpriteRef) {
        this(defaultSpriteRef, 100);
    }

    public void reset() {
        for (ParticlePool pool : this.particlesByEmitter.values()) {
            pool.reset(this);
        }
        for (int i = 0; i < this.emitters.size(); ++i) {
            ParticleEmitter emitter = (ParticleEmitter)this.emitters.get(i);
            emitter.resetState();
        }
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setRemoveCompletedEmitters(boolean remove) {
        this.removeCompletedEmitters = remove;
    }

    public void setUsePoints(boolean usePoints) {
        this.usePoints = usePoints;
    }

    public boolean usePoints() {
        return this.usePoints;
    }

    public ParticleSystem(String defaultSpriteRef, int maxParticles) {
        this(defaultSpriteRef, maxParticles, null);
    }

    public ParticleSystem(String defaultSpriteRef, int maxParticles, Color mask) {
        this.maxParticlesPerEmitter = maxParticles;
        this.mask = mask;
        this.setDefaultImageName(defaultSpriteRef);
        this.dummy = this.createParticle(this);
    }

    public ParticleSystem(Image defaultSprite, int maxParticles) {
        this.maxParticlesPerEmitter = maxParticles;
        this.sprite = defaultSprite;
        this.dummy = this.createParticle(this);
    }

    public void setDefaultImageName(String ref) {
        this.defaultImageName = ref;
        this.sprite = null;
    }

    public int getBlendingMode() {
        return this.blendingMode;
    }

    protected Particle createParticle(ParticleSystem system) {
        return new Particle(system);
    }

    public void setBlendingMode(int mode) {
        this.blendingMode = mode;
    }

    public int getEmitterCount() {
        return this.emitters.size();
    }

    public ParticleEmitter getEmitter(int index) {
        return (ParticleEmitter)this.emitters.get(index);
    }

    public void addEmitter(ParticleEmitter emitter) {
        this.emitters.add(emitter);
        ParticlePool pool = new ParticlePool(this, this.maxParticlesPerEmitter);
        this.particlesByEmitter.put(emitter, pool);
    }

    public void removeEmitter(ParticleEmitter emitter) {
        this.emitters.remove(emitter);
        this.particlesByEmitter.remove(emitter);
    }

    public void removeAllEmitters() {
        for (int i = 0; i < this.emitters.size(); ++i) {
            this.removeEmitter((ParticleEmitter)this.emitters.get(i));
            --i;
        }
    }

    public float getPositionX() {
        return this.x;
    }

    public float getPositionY() {
        return this.y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void render() {
        this.render(this.x, this.y);
    }

    public void render(float x, float y) {
        if (this.sprite == null && this.defaultImageName != null) {
            this.loadSystemParticleImage();
        }
        if (!this.visible) {
            return;
        }
        this.GL.glTranslatef(x, y, 0.0f);
        if (this.blendingMode == 1) {
            this.GL.glBlendFunc(770, 1);
        }
        if (this.usePoints()) {
            this.GL.glEnable(2832);
            TextureImpl.bindNone();
        }
        for (int emitterIdx = 0; emitterIdx < this.emitters.size(); ++emitterIdx) {
            ParticleEmitter emitter = (ParticleEmitter)this.emitters.get(emitterIdx);
            if (!emitter.isEnabled()) continue;
            if (emitter.useAdditive()) {
                this.GL.glBlendFunc(770, 1);
            }
            ParticlePool pool = (ParticlePool)this.particlesByEmitter.get(emitter);
            Image image = emitter.getImage();
            if (image == null) {
                image = this.sprite;
            }
            if (!emitter.isOriented() && !emitter.usePoints(this)) {
                image.startUse();
            }
            for (int i = 0; i < pool.particles.length; ++i) {
                if (!pool.particles[i].inUse()) continue;
                pool.particles[i].render();
            }
            if (!emitter.isOriented() && !emitter.usePoints(this)) {
                image.endUse();
            }
            if (!emitter.useAdditive()) continue;
            this.GL.glBlendFunc(770, 771);
        }
        if (this.usePoints()) {
            this.GL.glDisable(2832);
        }
        if (this.blendingMode == 1) {
            this.GL.glBlendFunc(770, 771);
        }
        Color.white.bind();
        this.GL.glTranslatef(-x, -y, 0.0f);
    }

    private void loadSystemParticleImage() {
        AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                try {
                    if (ParticleSystem.this.mask != null) {
                        ParticleSystem.this.sprite = new Image(ParticleSystem.this.defaultImageName, ParticleSystem.this.mask);
                    } else {
                        ParticleSystem.this.sprite = new Image(ParticleSystem.this.defaultImageName);
                    }
                }
                catch (SlickException e) {
                    Log.error(e);
                    ParticleSystem.this.defaultImageName = null;
                }
                return null;
            }
        });
    }

    public void update(int delta) {
        if (this.sprite == null && this.defaultImageName != null) {
            this.loadSystemParticleImage();
        }
        this.removeMe.clear();
        ArrayList emitters = new ArrayList(this.emitters);
        for (int i = 0; i < emitters.size(); ++i) {
            ParticleEmitter emitter = (ParticleEmitter)emitters.get(i);
            if (!emitter.isEnabled()) continue;
            emitter.update(this, delta);
            if (!this.removeCompletedEmitters || !emitter.completed()) continue;
            this.removeMe.add(emitter);
            this.particlesByEmitter.remove(emitter);
        }
        this.emitters.removeAll(this.removeMe);
        this.pCount = 0;
        if (!this.particlesByEmitter.isEmpty()) {
            for (ParticleEmitter emitter : this.particlesByEmitter.keySet()) {
                if (!emitter.isEnabled()) continue;
                ParticlePool pool = (ParticlePool)this.particlesByEmitter.get(emitter);
                for (int i = 0; i < pool.particles.length; ++i) {
                    if (!(pool.particles[i].life > 0.0f)) continue;
                    pool.particles[i].update(delta);
                    ++this.pCount;
                }
            }
        }
    }

    public int getParticleCount() {
        return this.pCount;
    }

    public Particle getNewParticle(ParticleEmitter emitter, float life) {
        ParticlePool pool = (ParticlePool)this.particlesByEmitter.get(emitter);
        ArrayList available = pool.available;
        if (available.size() > 0) {
            Particle p = (Particle)available.remove(available.size() - 1);
            p.init(emitter, life);
            p.setImage(this.sprite);
            return p;
        }
        Log.warn("Ran out of particles (increase the limit)!");
        return this.dummy;
    }

    public void release(Particle particle) {
        if (particle != this.dummy) {
            ParticlePool pool = (ParticlePool)this.particlesByEmitter.get(particle.getEmitter());
            pool.available.add(particle);
        }
    }

    public void releaseAll(ParticleEmitter emitter) {
        if (!this.particlesByEmitter.isEmpty()) {
            for (ParticlePool pool : this.particlesByEmitter.values()) {
                for (int i = 0; i < pool.particles.length; ++i) {
                    if (!pool.particles[i].inUse() || pool.particles[i].getEmitter() != emitter) continue;
                    pool.particles[i].setLife(-1.0f);
                    this.release(pool.particles[i]);
                }
            }
        }
    }

    public void moveAll(ParticleEmitter emitter, float x, float y) {
        ParticlePool pool = (ParticlePool)this.particlesByEmitter.get(emitter);
        for (int i = 0; i < pool.particles.length; ++i) {
            if (!pool.particles[i].inUse()) continue;
            pool.particles[i].move(x, y);
        }
    }

    public ParticleSystem duplicate() throws SlickException {
        for (int i = 0; i < this.emitters.size(); ++i) {
            if (this.emitters.get(i) instanceof ConfigurableEmitter) continue;
            throw new SlickException("Only systems contianing configurable emitters can be duplicated");
        }
        ParticleSystem theCopy = null;
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ParticleIO.saveConfiguredSystem(bout, this);
            ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
            theCopy = ParticleIO.loadConfiguredSystem(bin);
        }
        catch (IOException e) {
            Log.error("Failed to duplicate particle system");
            throw new SlickException("Unable to duplicated particle system", e);
        }
        return theCopy;
    }

    private class ParticlePool {
        public Particle[] particles;
        public ArrayList available;

        public ParticlePool(ParticleSystem system, int maxParticles) {
            this.particles = new Particle[maxParticles];
            this.available = new ArrayList();
            for (int i = 0; i < this.particles.length; ++i) {
                this.particles[i] = ParticleSystem.this.createParticle(system);
            }
            this.reset(system);
        }

        public void reset(ParticleSystem system) {
            this.available.clear();
            for (int i = 0; i < this.particles.length; ++i) {
                this.available.add(this.particles[i]);
            }
        }
    }
}

