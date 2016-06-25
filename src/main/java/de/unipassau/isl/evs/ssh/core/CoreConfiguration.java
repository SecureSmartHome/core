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

package de.unipassau.isl.evs.ssh.core;

import de.ncoder.typedmap.Key;
import de.unipassau.isl.evs.ssh.core.container.AbstractComponent;
import de.unipassau.isl.evs.ssh.core.container.Container;
import de.unipassau.isl.evs.ssh.core.container.StartupException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

/**
 * The CoreConfiguration loads configuration details from an INI style configuration file.
 */
public abstract class CoreConfiguration extends AbstractComponent {
    public static final Key<CoreConfiguration> KEY = new Key<>(CoreConfiguration.class);

    protected HierarchicalINIConfiguration config;
    protected ConfigurationDefaults defaults;

    @Override
    public void init(Container container) {
        defaults = loadDefaults();
        try {
            config = new HierarchicalINIConfiguration(defaults.mainConfigFile);
        } catch (ConfigurationException e) {
            throw new StartupException(e);
        }
        super.init(container);
    }

    /**
     * Gets the keystore password. If the password is not set in the config file, the default password is returned.
     *
     * @return the keystore password
     * @see ConfigurationDefaults
     */
    public String getKeyStorePassword() {
        String password = null;

        try {
            password = config.getSection("keystore").getString("password");
        } catch (NoSuchElementException ignored) {

        }

        return password != null ? password : defaults.keystorePassword;
    }

    /**
     * Gets the keystore path.
     *
     * @return the keystore path or null if the path is not set in the config file
     */
    public String getKeyStorePath() {
        String path = null;

        try {
            path = config.getSection("keystore").getString("path");
        } catch (NoSuchElementException ignored) {

        }

        return path != null ? path : defaults.keystoreFile;

    }

    /**
     * Gets the current geographical location.
     *
     * @return the location that is set in the config file or null if the location entry does not exist
     */
    public String getLocation() {
        try {
            return config.getSection("settings").getString("location");
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /**
     * Loads platform specific defaults.
     */
    protected abstract ConfigurationDefaults loadDefaults();

    /**
     * Platform specific defaults.
     */
    public class ConfigurationDefaults {
        private final String mainConfigFile;
        private final String keystoreFile;
        private final String keystorePassword;


        /**
         * Constructs a new ConfigurationDefaults object.
         *
         * @param mainConfigFile   the path to the main configuration file
         * @param keystoreFile     the path to the keystore file
         * @param keystorePassword the default keystore password generated from e.g. a phone's IMEI
         */
        public ConfigurationDefaults(@NotNull String mainConfigFile, @NotNull String keystoreFile, String keystorePassword) {
            this.mainConfigFile = mainConfigFile;
            this.keystoreFile = keystoreFile;
            this.keystorePassword = keystorePassword;
        }
    }

}
