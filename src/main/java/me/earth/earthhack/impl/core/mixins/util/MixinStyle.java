/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.text.Style
 *  net.minecraft.util.text.event.ClickEvent
 */
package me.earth.earthhack.impl.core.mixins.util;

import java.util.function.Supplier;
import me.earth.earthhack.impl.core.ducks.util.IStyle;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Style.class})
public abstract class MixinStyle
implements IStyle {
    private ClickEvent rightClickEvent;
    private ClickEvent middleClickEvent;
    private Supplier<String> leftSupplier;
    private Supplier<String> rightSupplier;
    private Supplier<String> middleSupplier;

    @Override
    public void setRightClickEvent(ClickEvent event) {
        this.rightClickEvent = event;
    }

    @Override
    public ClickEvent getRightClickEvent() {
        return this.rightClickEvent;
    }

    @Override
    public void setMiddleClickEvent(ClickEvent event) {
        this.middleClickEvent = event;
    }

    @Override
    public ClickEvent getMiddleClickEvent() {
        return this.middleClickEvent;
    }

    @Override
    public void setSuppliedInsertion(Supplier<String> insertion) {
        this.leftSupplier = insertion;
    }

    @Override
    public void setRightInsertion(Supplier<String> rightInsertion) {
        this.rightSupplier = rightInsertion;
    }

    @Override
    public void setMiddleInsertion(Supplier<String> middleInsertion) {
        this.middleSupplier = middleInsertion;
    }

    @Override
    public String getRightInsertion() {
        return this.rightSupplier == null ? null : this.rightSupplier.get();
    }

    @Override
    public String getMiddleInsertion() {
        return this.middleSupplier == null ? null : this.middleSupplier.get();
    }

    @Inject(method={"createDeepCopy"}, at={@At(value="RETURN")})
    public void createDeepCopyHook(CallbackInfoReturnable<Style> info) {
        this.copyDucks((IStyle)info.getReturnValue());
    }

    @Inject(method={"createShallowCopy"}, at={@At(value="RETURN")})
    public void createShallowCopyHook(CallbackInfoReturnable<Style> info) {
        this.copyDucks((IStyle)info.getReturnValue());
    }

    @Inject(method={"getInsertion"}, at={@At(value="HEAD")}, cancellable=true)
    public void getInsertionHook(CallbackInfoReturnable<String> info) {
        if (this.leftSupplier != null) {
            info.setReturnValue(this.leftSupplier.get());
        }
    }

    private void copyDucks(IStyle style) {
        style.setMiddleInsertion(this.middleSupplier);
        style.setRightInsertion(this.rightSupplier);
        style.setSuppliedInsertion(this.leftSupplier);
        style.setMiddleClickEvent(this.middleClickEvent);
        style.setRightClickEvent(this.rightClickEvent);
    }
}

