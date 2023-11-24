package hr.algebra.mastermind.enums;

public enum ConfigurationKey {
    HOST("host"),
    SERVER_PORT("server.port"),
    CLIENT_PORT("client.port"),
    RMI_PORT("rmi.port"),
    RANDOM_PORT_HINT("random.port.hint");

    private String key;

    private ConfigurationKey(String key){
        this.key = key;
    }

    public String getKey(){
        return key;
    }
}
