package me.earth.earthhack.impl.commands.packet.util;

import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import me.earth.earthhack.impl.commands.packet.util.Dummy;

public class DummySecretKey
implements SecretKey,
Dummy {
    protected static final SecretKey SECRET_KEY;

    @Override
    public String getAlgorithm() {
        return SECRET_KEY.getAlgorithm();
    }

    @Override
    public String getFormat() {
        return SECRET_KEY.getFormat();
    }

    @Override
    public byte[] getEncoded() {
        return SECRET_KEY.getEncoded();
    }

    static {
        KeyGenerator generator;
        try {
            generator = KeyGenerator.getInstance("AES");
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No RSA-Algorithm!");
        }
        SECRET_KEY = generator.generateKey();
    }
}
