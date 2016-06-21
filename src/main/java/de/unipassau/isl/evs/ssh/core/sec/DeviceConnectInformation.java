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

package de.unipassau.isl.evs.ssh.core.sec;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import de.unipassau.isl.evs.ssh.core.naming.DeviceID;
import de.unipassau.isl.evs.ssh.core.network.Client;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.security.SecureRandom;

import static io.netty.handler.codec.base64.Base64.decode;
import static io.netty.handler.codec.base64.Base64.encode;

/**
 * Class encapsulating all Information that is required for connecting to a specific device:
 * <ul>
 * <li>IP Address and Port</li>
 * <li>{@link DeviceID}</li>
 * <li>{@link Client#getActiveRegistrationToken() Active} or {@link Client#getPassiveRegistrationToken() Passive} Registration Token</li>
 * </ul>
 *
 * @author Niko Fink
 * @see de.unipassau.isl.evs.ssh.core.network.handshake.HandshakePacket.ServerAuthenticationResponse
 * @see de.unipassau.isl.evs.ssh.core.network.handshake.HandshakePacket.ActiveRegistrationRequest
 */
public class DeviceConnectInformation implements Serializable {
    /**
     * Length of the IP Address, which must be an IPv4 address
     */
    public static final int ADDRESS_LENGTH = 4;
    /**
     * Length of the registration token
     */
    public static final int TOKEN_LENGTH = 35;
    private static final String TAG = DeviceConnectInformation.class.getSimpleName();
    private static final QRCodeWriter writer = new QRCodeWriter();
    /**
     * Length of the byte array which will be encoded as String for the QR Code
     */
    public static final int TOKEN_BASE64_LENGTH = encodeToken(new byte[TOKEN_LENGTH]).length();
    private static final int DATA_LENGTH = 4 + 2 + TOKEN_BASE64_LENGTH + DeviceID.ID_LENGTH;
    private static SecureRandom random = null;
    private final InetAddress address;
    private final int port;
    private final DeviceID id;
    private final byte[] token;

    public DeviceConnectInformation(InetAddress address, int port, DeviceID id, byte[] token) {
        if (address.getAddress().length != ADDRESS_LENGTH) {
            throw new IllegalArgumentException("illegal address length");
        }
        this.address = address;
        this.port = port;
        this.id = id;
        if (token.length != TOKEN_LENGTH) {
            throw new IllegalArgumentException("illegal token length");
        }
        this.token = token;
    }

    /**
     * Read a DeviceConnectInformation from a Base64 encoded String, which was read from a QR Code.
     */
    public static DeviceConnectInformation fromDataString(String data) throws IOException {
        final ByteBuf base64 = UnpooledByteBufAllocator.DEFAULT.heapBuffer(data.length());
        ByteBufUtil.writeAscii(base64, data);
        final ByteBuf byteBuf = decode(base64);
        if (byteBuf.readableBytes() != DATA_LENGTH) {
            throw new IOException("too many bytes encoded");
        }

        final byte[] addressData = new byte[ADDRESS_LENGTH];
        byteBuf.readBytes(addressData);
        final InetAddress address = InetAddress.getByAddress(addressData);
        final int port = byteBuf.readUnsignedShort();
        final byte[] idData = new byte[DeviceID.ID_LENGTH];
        byteBuf.readBytes(idData);
        final DeviceID id = new DeviceID(idData);
        final byte[] encodedToken = new byte[TOKEN_BASE64_LENGTH];
        byteBuf.readBytes(encodedToken);
        final byte[] token = decodeToken(new String(encodedToken));

        return new DeviceConnectInformation(address, port, id, token);
    }

    /**
     * Generate a random token that can be used for registration.
     */
    public static byte[] getRandomToken() {
        byte[] token = new byte[TOKEN_LENGTH];
        if (random == null) {
            random = new SecureRandom();
        }
        random.nextBytes(token);
        return token;
    }

    @NotNull
    public static String encodeToken(@NotNull byte[] token) {
        return Base64.encodeBase64String(token);
    }

    @NotNull
    public static byte[] decodeToken(@Nullable String token) {
        if (Strings.isNullOrEmpty(token)) {
            return new byte[0];
        }
        return Base64.decodeBase64(token);
    }

    public static String trimAddress(Object obj) {
        String address = String.valueOf(obj).trim();
        if (address.startsWith("/")) {
            address = address.substring(1);
        }
        return address;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public DeviceID getID() {
        return id;
    }

    public byte[] getToken() {
        return token;
    }

    /**
     * Serialize this Object to a String which can be converted to a QR Code
     */
    public String toDataString() {
        final ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(DATA_LENGTH, DATA_LENGTH);
        byteBuf.writeBytes(address.getAddress());
        byteBuf.writeShort(port);
        byteBuf.writeBytes(id.getIDBytes());
        byteBuf.writeBytes(encodeToken(token).getBytes());
        return encode(byteBuf).toString(Charsets.US_ASCII);
    }

    /**
     * Serialize this Object to a BitMatrix representing a QR Code
     */
    public BitMatrix toQRBitMatrix() throws WriterException {
        return writer.encode(toDataString(), BarcodeFormat.QR_CODE, 0, 0);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("address", address)
                .add("port", port)
                .add("id", id)
                .add("token", encodeToken(token))
                .toString();
    }
}
