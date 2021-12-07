/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.commands.packet.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import me.earth.earthhack.impl.commands.packet.util.Dummy;

public class DummyPublicKey
implements PublicKey,
Dummy {
    private static final PublicKey PUBLIC_KEY;

    @Override
    public String getAlgorithm() {
        return PUBLIC_KEY.getAlgorithm();
    }

    @Override
    public String getFormat() {
        return PUBLIC_KEY.getFormat();
    }

    @Override
    public byte[] getEncoded() {
        return PUBLIC_KEY.getEncoded();
    }

    static {
        KeyPairGenerator generator;
        try {
            generator = KeyPairGenerator.getInstance("RSA");
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No RSA-Algorithm!");
        }
        KeyPair pair = generator.generateKeyPair();
        PUBLIC_KEY = pair.getPublic();
    }
}

