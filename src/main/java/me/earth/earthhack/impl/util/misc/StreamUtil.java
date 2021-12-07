package me.earth.earthhack.impl.util.misc;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class StreamUtil {
    public static void copy(URL from, URL to) throws IOException {
        try (ReadableByteChannel rbc = Channels.newChannel(from.openStream());
             FileOutputStream fos = new FileOutputStream(to.getFile());){
            fos.getChannel().transferFrom(rbc, 0L, Long.MAX_VALUE);
        }
    }

    public static byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        StreamUtil.copy(is, buffer);
        return buffer.toByteArray();
    }

    public static void copy(InputStream is, OutputStream os) throws IOException {
        int length;
        byte[] bytes = new byte[1024];
        while ((length = is.read(bytes)) != -1) {
            os.write(bytes, 0, length);
        }
    }
}
