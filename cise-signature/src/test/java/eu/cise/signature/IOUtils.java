package eu.cise.signature;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class IOUtils {
    public IOUtils() {
    }

    public static byte[] readFully(InputStream var0, int var1, boolean var2) throws IOException {
        byte[] var3 = new byte[0];
        if (var1 == -1) {
            var1 = 2147483647;
        }

        int var6;
        for (int var4 = 0; var4 < var1; var4 += var6) {
            int var5;
            if (var4 >= var3.length) {
                var5 = Math.min(var1 - var4, var3.length + 1024);
                if (var3.length < var4 + var5) {
                    var3 = Arrays.copyOf(var3, var4 + var5);
                }
            } else {
                var5 = var3.length - var4;
            }

            var6 = var0.read(var3, var4, var5);
            if (var6 < 0) {
                if (var2 && var1 != 2147483647) {
                    throw new EOFException("Detect premature EOF");
                }

                if (var3.length != var4) {
                    var3 = Arrays.copyOf(var3, var4);
                }
                break;
            }
        }

        return var3;
    }
}
