/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.server.api;

public class UnknownProtocolException
extends Exception {
    public UnknownProtocolException(int id) {
        super("Received packet with unknown id: " + id);
    }
}

