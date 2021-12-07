package me.earth.earthhack.impl.core.ducks.entity;

public interface IEntityNoInterp {
    public double getNoInterpX();

    public double getNoInterpY();

    public double getNoInterpZ();

    public void setNoInterpX(double var1);

    public void setNoInterpY(double var1);

    public void setNoInterpZ(double var1);

    public int getPosIncrements();

    public void setPosIncrements(int var1);

    public float getNoInterpSwingAmount();

    public float getNoInterpSwing();

    public float getNoInterpPrevSwing();

    public void setNoInterpSwingAmount(float var1);

    public void setNoInterpSwing(float var1);

    public void setNoInterpPrevSwing(float var1);

    public boolean isNoInterping();

    public void setNoInterping(boolean var1);
}
