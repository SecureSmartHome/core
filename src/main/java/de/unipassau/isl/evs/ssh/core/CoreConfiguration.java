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
 * Created by popeye on 6/21/16.
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

    public String getKeyStorePassword() {
        String password = null;

        try {
            password = config.getSection("keystore").getString("password");
        } catch (NoSuchElementException ignored) {

        }

        return password != null ? password : "34r4oikj4oij239";
    }

    public String getKeyStorePath() {
        String path = null;

        try {
            path = config.getSection("keystore").getString("path");
        } catch (NoSuchElementException ignored) {

        }

        return path != null ? path : defaults.keystoreFile;

    }

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

    public class ConfigurationDefaults {
        private final String mainConfigFile;
        private final String keystoreFile;


        public ConfigurationDefaults(@NotNull String mainConfigFile, @NotNull String keystoreFile) {
            this.mainConfigFile = mainConfigFile;
            this.keystoreFile = keystoreFile;
        }
    }

}
