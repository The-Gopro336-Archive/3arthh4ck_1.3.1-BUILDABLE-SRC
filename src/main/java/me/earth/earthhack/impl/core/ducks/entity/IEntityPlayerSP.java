package me.earth.earthhack.impl.core.ducks.entity;

public interface IEntityPlayerSP {
    public double getLastReportedX();

    public double getLastReportedY();

    public double getLastReportedZ();

    public float getLastReportedYaw();

    public float getLastReportedPitch();

    public boolean getLastOnGround();

    public void setLastReportedYaw(float var1);

    public void setLastReportedPitch(float var1);

    public int getPositionUpdateTicks();

    public void superUpdate();

    public void invokeUpdateWalkingPlayer();

    public void setHorseJumpPower(float var1);
}
