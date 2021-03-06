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

package de.unipassau.isl.evs.ssh.core.messaging.payload;

import de.unipassau.isl.evs.ssh.core.naming.DeviceID;

/**
 * The SetUserGroupPayload is the payload used to set the group of a user.
 *
 * @author Wolfgang Popp.
 */
public class SetUserGroupPayload implements MessagePayload {
    private final String groupName;
    private final DeviceID user;

    /**
     * Constructs a new SetUserGroupPayload with the given user and group.
     *
     * @param user  the user whose group is set
     * @param group the group to set
     */
    public SetUserGroupPayload(DeviceID user, String group) {
        this.groupName = group;
        this.user = user;
    }

    /**
     * Gets the group name that will be set for the user.
     *
     * @return the group name
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Gets the device id of the user whose group is to be set.
     *
     * @return the user to edit
     */
    public DeviceID getUser() {
        return user;
    }
}
