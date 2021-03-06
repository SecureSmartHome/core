/*
 * MIT License
 *
 * Copyright (c) 2016.
 * Bucher Andreas, Fink Simon Dominik, Fraedrich Christoph, Popp Wolfgang,
 * Sell Leon, Werli Philemon
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.unipassau.isl.evs.ssh.core.naming;

import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * Unique id for all devices (user devices, master, slaves).
 *
 * @author Wolfgang Popp
 */
public final class DeviceID implements Serializable {
    private static final String ID_MD_ALG = "SHA-256";

    /**
     * The length of a device id in bytes
     */
    public static final int ID_LENGTH = 32;

    private static final byte[] NO_DEVICE_BYTES = new byte[ID_LENGTH];

    static {
        Arrays.fill(NO_DEVICE_BYTES, ((byte) 0));
    }

    /**
     * A device id for a dummy device. Can be used if no device is available but a device id is needed. This approach
     * fixes a bootstrapping problem during the very first handshake.
     */
    public static final DeviceID NO_DEVICE = new DeviceID(NO_DEVICE_BYTES);

    private final String id;
    private final byte[] bytes;

    /**
     * Creates a new DeviceID from the given string.
     *
     * @param id the id as string
     */
    public DeviceID(String id) {
        this.id = id.trim();
        this.bytes = Base64.decodeBase64(id);
        validateLength();
    }

    /**
     * Constructs a new DeviceID from the given byte array.
     *
     * @param bytes a
     */
    public DeviceID(byte[] bytes) {
        this.id = Base64.encodeBase64String(bytes).trim();
        this.bytes = Arrays.copyOf(bytes, bytes.length);
        validateLength();
    }

    private void validateLength() {
        if (bytes.length != ID_LENGTH) {
            throw new IllegalArgumentException("ID '" + id + "' has invalid length " + bytes.length + "!=" + ID_LENGTH);
        }
    }

    /**
     * Creates a new Device id from the given certificate.
     *
     * @param cert the certificate to create the id from
     * @return the device id corresponding to the certificate
     */
    public static DeviceID fromCertificate(X509Certificate cert) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(ID_MD_ALG, "SC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(ID_MD_ALG + " is not available from SpongyCastle", e);
        }
        if (md.getDigestLength() != ID_LENGTH) {
            throw new AssertionError("Message digest " + ID_MD_ALG + " returns invalid length " + md.getDigestLength() + "!=" + ID_LENGTH);
        }
        md.update(cert.getPublicKey().getEncoded());
        byte[] digest = md.digest();
        return new DeviceID(digest);
    }

    /**
     * Returns the device id string.
     *
     * @return the device id
     * @deprecated use {@link #getIDString()} instead
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the device id string.
     *
     * @return the device id
     */
    public String getIDString() {
        return id;
    }

    /**
     * Gets a byte representation of this device id.
     *
     * @return the byte representation of this id
     */
    public byte[] getIDBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DeviceID deviceID = (DeviceID) o;
        return Arrays.equals(bytes, deviceID.bytes);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Returns a short description of this device id.
     *
     * @return the short description
     */
    public String toShortString() {
        return id.substring(0, Math.min(id.length(), 7));
    }

    @Override
    public String toString() {
        return getIDString();
    }
}
