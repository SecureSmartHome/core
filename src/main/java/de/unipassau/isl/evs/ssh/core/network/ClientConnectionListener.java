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

package de.unipassau.isl.evs.ssh.core.network;


import org.jetbrains.annotations.Nullable;

/**
 * A Listener that can be notified when the Connected State of the {@link Client} changes.
 *
 * @author Niko Fink
 */
public interface ClientConnectionListener {
    /**
     * Called when a new possible address for the Master was found, e.g. by UDP discovery.
     */
    void onMasterFound();

    /**
     * Called when the Client is attempting to establish a connection with the Master listening at the given address.
     */
    void onClientConnecting(String host, int port);

    /**
     * Called as soon as the connection is established and the Client is authenticated and
     * {@link Client#isConnectionEstablished()} will return {@code true}.
     */
    void onClientConnected();

    /**
     * Called as soon as the connection broke and
     * {@link Client#isConnectionEstablished()} will return {@code false}.
     */
    void onClientDisconnected();

    /**
     * Called when the Master rejected this Client with a given message.
     */
    void onClientRejected(@Nullable String message);
}
