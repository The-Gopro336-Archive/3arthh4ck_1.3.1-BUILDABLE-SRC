package me.earth.earthhack.impl.core.ducks.entity;

public interface IEntityLivingBase {
    public int armSwingAnimationEnd();

    public int getTicksSinceLastSwing();

    public int getActiveItemStackUseCount();

    public void setTicksSinceLastSwing(int var1);

    public void setActiveItemStackUseCount(int var1);

    public boolean getElytraFlag();

    public void setLowestDura(float var1);

    public float getLowestDurability();
}
