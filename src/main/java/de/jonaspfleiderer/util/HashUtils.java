package de.jonaspfleiderer.util;

import de.jonaspfleiderer.Main;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class HashUtils {

    public static String getMd5Hash(String s) {
        try {
            byte[] buffer = s.getBytes(StandardCharsets.UTF_8);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return bytesToHexString(md5.digest(buffer));
        } catch (NoSuchAlgorithmException e) {
            Main.getLogger().logError("[HashUtils] " + e.getMessage());
            return "Error";
        }
    }

    public static String getMD5FileHash(String fileName) {
        try {
            Main.getLogger().log("[HashUtils] Hashing file: '" + fileName + "'");
            InputStream is = Files.newInputStream(Paths.get(fileName));

            byte[] buffer = new byte[1024];
            MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead;

            do {
                numRead = is.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);

            is.close();
            byte[] b = complete.digest();

            return bytesToHexString(b);
        } catch (IOException | NoSuchAlgorithmException ex) {
            Main.getLogger().logError("[HashUtils] " + ex.getMessage());
            return "Error";
        }
    }

    public static String getMD5FilesHash(List<String> files) {
        StringBuilder hashes = new StringBuilder();
        for (String fileName : files) {
            hashes.append(getMD5FileHash(fileName));
        }
        String hash = getMd5Hash(hashes.toString());
        Main.getLogger().log("[HashUtils] Resulting hash: " + hash);
        return hash;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte value : bytes) {
            result.append(Integer.toString((value & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }
}
