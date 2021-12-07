package org.spongepowered.asm.service;

public class ServiceNotAvailableError
extends Error {
    private static final long serialVersionUID = 1L;

    public ServiceNotAvailableError(String message) {
        super(message);
    }
}
