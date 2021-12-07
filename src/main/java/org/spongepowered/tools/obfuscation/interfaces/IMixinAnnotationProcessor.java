package org.spongepowered.tools.obfuscation.interfaces;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import org.spongepowered.asm.util.ITokenProvider;
import org.spongepowered.tools.obfuscation.interfaces.IJavadocProvider;
import org.spongepowered.tools.obfuscation.interfaces.IObfuscationManager;
import org.spongepowered.tools.obfuscation.interfaces.IOptionProvider;
import org.spongepowered.tools.obfuscation.interfaces.ITypeHandleProvider;

public interface IMixinAnnotationProcessor
extends Messager,
IOptionProvider {
    public CompilerEnvironment getCompilerEnvironment();

    public ProcessingEnvironment getProcessingEnvironment();

    public IObfuscationManager getObfuscationManager();

    public ITokenProvider getTokenProvider();

    public ITypeHandleProvider getTypeProvider();

    public IJavadocProvider getJavadocProvider();

    public static enum CompilerEnvironment {
        JAVAC,
        JDT;

    }
}
