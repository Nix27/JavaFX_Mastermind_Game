package hr.algebra.mastermind.utils;

import hr.algebra.mastermind.enums.ConfigurationKey;
import hr.algebra.mastermind.exceptions.InvalidConfigurationKeyException;
import hr.algebra.mastermind.jndi.InitialDirContextCloseable;

import javax.naming.Context;
import javax.naming.NamingException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

public class ConfigurationReader {

    private static Hashtable<String, String> environment;

    private ConfigurationReader() {}

    static {
        environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.fscontext.RefFSContextFactory");
        environment.put(Context.PROVIDER_URL,"file:network_configuration");
    }

    public static String getStringValueOfKey(ConfigurationKey configurationKey){
        try (InitialDirContextCloseable context = new InitialDirContextCloseable(environment)){
            return searchForValue(context, configurationKey);
        } catch (NamingException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static Integer getIntValueOfKey(ConfigurationKey configurationKey){
        String value = getStringValueOfKey(configurationKey);
        return Integer.parseInt(value);
    }

    private static String searchForValue(Context context, ConfigurationKey configurationKey)  {
        String fileName = "config.properties";

        try {
            Object object = context.lookup(fileName);
            Properties props = new Properties();
            props.load(new FileReader(object.toString()));

            String value = props.getProperty(configurationKey.getKey());

            if(value == null){
                throw new InvalidConfigurationKeyException("Key: " + configurationKey.getKey() + "does not exist!");
            }

            return props.getProperty(configurationKey.getKey());
        }
        catch(NamingException | IOException ex) {
            throw new RuntimeException("Unable to read configuration!", ex);
        }
    }
}
