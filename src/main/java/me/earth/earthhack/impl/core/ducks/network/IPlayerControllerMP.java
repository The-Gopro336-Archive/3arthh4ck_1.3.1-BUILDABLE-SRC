package me.earth.earthhack.impl.core.ducks.network;

public interface IPlayerControllerMP {
    public void syncItem();

    public int getItem();

    public void setBlockHitDelay(int var1);

    public int getBlockHitDelay();

    public float getCurBlockDamageMP();

    public void setCurBlockDamageMP(float var1);

    public void setIsHittingBlock(boolean var1);

    public boolean getIsHittingBlock();
}
