package de.unipassau.isl.evs.ssh.core;

import de.ncoder.typedmap.Key;
import de.unipassau.isl.evs.ssh.core.container.AbstractComponent;
import de.unipassau.isl.evs.ssh.core.container.Container;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;

import java.util.NoSuchElementException;

/**
 * Created by popeye on 6/21/16.
 */
public class CoreConfiguration extends AbstractComponent {
    public static final Key<CoreConfiguration> KEY = new Key<>(CoreConfiguration.class);

    protected HierarchicalINIConfiguration config;

    @Override
    public void init(Container container) {
        try {
            config = new HierarchicalINIConfiguration("/etc/securesmarthome.conf");
        } catch (ConfigurationException ignored) {
            config = new HierarchicalINIConfiguration();
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

        return path != null ? path : "/var/lib/SecureSmartHome/keystore";

    }

    public String getLocation() {
        try {
            return config.getSection("Settings").getString("location");
        } catch (NoSuchElementException e) {
            return null;
        }
    }

}
