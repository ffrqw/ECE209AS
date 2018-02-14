package com.rachio.iro.gen2.model;

public final class KeyPair {
    public static final int KEYLEN = 32;
    public final byte[] privatekey;
    public final byte[] publickey;
    public final String publickeyAsHex;

    public KeyPair(byte[] privateKey, byte[] publicKey) {
        this.privatekey = privateKey;
        this.publickey = publicKey;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sb.append(String.format("%02x", new Object[]{Byte.valueOf(publicKey[i])}));
        }
        this.publickeyAsHex = sb.toString();
    }
}
