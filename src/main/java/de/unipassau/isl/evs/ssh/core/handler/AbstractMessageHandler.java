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

package de.unipassau.isl.evs.ssh.core.handler;

import java.util.HashSet;
import java.util.Set;

import de.ncoder.typedmap.Key;
import de.unipassau.isl.evs.ssh.core.container.Component;
import de.unipassau.isl.evs.ssh.core.container.Container;
import de.unipassau.isl.evs.ssh.core.messaging.IncomingDispatcher;
import de.unipassau.isl.evs.ssh.core.messaging.Message;
import de.unipassau.isl.evs.ssh.core.messaging.OutgoingRouter;
import de.unipassau.isl.evs.ssh.core.messaging.RoutingKey;
import de.unipassau.isl.evs.ssh.core.naming.DeviceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base implementation for {@link MessageHandler}s providing many convenience functions for accessing the
 * IncomingDispatcher and the respective Container this Object is registered to.
 * If a subclass also implements {@link Component} also provides implementations of Methods in that interface,
 * automatically registering the Handler to the IncomingDispatcher once the Handler Component is registered to the Container.
 *
 * @author Team
 */
public abstract class AbstractMessageHandler implements MessageHandler {
    private final Set<RoutingKey> registeredKeys = new HashSet<>(getRoutingKeys().length);
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Container container;
    private IncomingDispatcher dispatcher;


    /**
     * @return all the RoutingKeys this MessageHandler can handle
     */
    public abstract RoutingKey[] getRoutingKeys();

    /**
     * Provides a standard implementation of {@link Component#init(Container)} if a child class implements {@link Component}
     * and is used as such.
     * Once registered to the Container, the handler is also registered to the {@link IncomingDispatcher} to handle all
     * RoutingKeys returned by {@link #getRoutingKeys()}.
     */
    public void init(Container container) {
        logger.debug("init");
        this.container = container;
        container.require(IncomingDispatcher.KEY).registerHandler(this, getRoutingKeys());
    }

    @Override
    public void handlerAdded(IncomingDispatcher dispatcher, RoutingKey routingKey) {
        logger.debug("registered");
        registeredKeys.add(routingKey);
        this.dispatcher = dispatcher;
    }

    @Override
    public void handlerRemoved(RoutingKey routingKey) {
        logger.debug("unregistered");
        registeredKeys.remove(routingKey);
        if (registeredKeys.isEmpty()) {
            dispatcher = null;
        }
    }

    /**
     * Provides a standard implementation of {@link Component#destroy()} if a child class implements {@link Component}
     * and is used as such.
     * Before being removed from the Container, the handler is also unregistered from the {@link IncomingDispatcher}.
     */
    public void destroy() {
        logger.debug("init");
        final IncomingDispatcher dispatcher = getDispatcher();
        if (dispatcher != null) {
            dispatcher.unregisterHandler(this, getRoutingKeys());
        }
        this.container = null;
    }

    @Nullable
    protected IncomingDispatcher getDispatcher() {
        return container == null ? dispatcher : container.get(IncomingDispatcher.KEY);
    }

    @Nullable
    protected Container getContainer() {
        return dispatcher == null ? container : dispatcher.getContainer();
    }

    /**
     * @return {@code true} if this Handler is used as Component and is registered to a Container.
     */
    protected boolean isActive() {
        return container != null;
    }

    /**
     * @return {@code true} if this Handler is registered to an IncomingDispatcher.
     */
    protected boolean isRegistered() {
        return dispatcher != null;
    }

    /**
     * Fetch the Component from the Container or return {@code null} if the Component or the Container itself are not available.
     *
     * @see Container#get(Key)
     */
    @Nullable
    protected <T extends Component> T getComponent(Key<T> key) {
        Container container = getContainer();
        if (container != null) {
            return container.get(key);
        } else {
            return null;
        }
    }

    /**
     * Fetch the Component from the Container or throw an {@link IllegalStateException} if the Component or the
     * Container itself are not available.
     *
     * @see Container#require(Key)
     */
    @NotNull
    protected <T extends Component> T requireComponent(Key<T> key) {
        Container container = getContainer();
        if (container != null) {
            return container.require(key);
        } else {
            throw new IllegalStateException("Handler not registered to a IncomingDispatcher");
        }
    }

    /**
     * Convenience Method delegating to {@link OutgoingRouter#sendMessage(DeviceID, RoutingKey, Message)} of the current Container.
     */
    protected Message.AddressedMessage sendMessage(DeviceID toID, String routingKey, Message msg) {
        return requireComponent(OutgoingRouter.KEY).sendMessage(toID, routingKey, msg);
    }

    /**
     * Convenience Method delegating to {@link OutgoingRouter#sendMessage(DeviceID, RoutingKey, Message)} of the current Container.
     */
    protected Message.AddressedMessage sendMessageLocal(String routingKey, Message msg) {
        return requireComponent(OutgoingRouter.KEY).sendMessageLocal(routingKey, msg);
    }

    /**
     * Convenience Method delegating to {@link OutgoingRouter#sendMessage(DeviceID, RoutingKey, Message)} of the current Container.
     */
    protected Message.AddressedMessage sendMessageToMaster(String routingKey, Message msg) {
        return requireComponent(OutgoingRouter.KEY).sendMessageToMaster(routingKey, msg);
    }

    /**
     * Convenience Method delegating to {@link OutgoingRouter#sendMessage(DeviceID, RoutingKey, Message)} of the current Container.
     */
    protected Message.AddressedMessage sendMessage(DeviceID toID, RoutingKey routingKey, Message msg) {
        return requireComponent(OutgoingRouter.KEY).sendMessage(toID, routingKey, msg);
    }

    /**
     * Convenience Method delegating to {@link OutgoingRouter#sendMessage(DeviceID, RoutingKey, Message)} of the current Container.
     */
    protected Message.AddressedMessage sendMessageLocal(RoutingKey routingKey, Message msg) {
        return requireComponent(OutgoingRouter.KEY).sendMessageLocal(routingKey, msg);
    }

    /**
     * Convenience Method delegating to {@link OutgoingRouter#sendMessage(DeviceID, RoutingKey, Message)} of the current Container.
     */
    protected Message.AddressedMessage sendMessageToMaster(RoutingKey routingKey, Message msg) {
        return requireComponent(OutgoingRouter.KEY).sendMessageToMaster(routingKey, msg);
    }

    /**
     * Convenience Method delegating to {@link OutgoingRouter#sendReply(Message.AddressedMessage, Message)} of the current Container.
     */
    protected Message.AddressedMessage sendReply(Message.AddressedMessage original, Message reply) {
        return requireComponent(OutgoingRouter.KEY).sendReply(original, reply);
    }

    /**
     * Called if this Handler received a Message it can't handle.
     */
    protected void invalidMessage(Message.AddressedMessage message) {
        for (RoutingKey routingKey : registeredKeys) {
            if (routingKey.matches(message)) {
                throw new IllegalStateException("Handler did not accept message for RoutingKey " + routingKey
                        + " even though being registered for handling it. The message was " + message);
            }
        }
        throw new IllegalArgumentException("Handler is not registered for Handling message " + message);
    }

    @Override
    public String toString() {
        String name = getClass().getSimpleName();
        if (name.isEmpty()) {
            name = getClass().getName();
            int index = name.lastIndexOf(".");
            if (index >= 0 && index + 1 < name.length()) {
                name = name.substring(index + 1);
            }
        }
        return name;
    }
}
