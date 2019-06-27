package avishkaar.com.bluetoothcodethree.ModelClasses;

public class RemoteModelClass {

    ConfigClass config;

    public RemoteModelClass() {
    }

    public RemoteModelClass( ConfigClass config) {
        this.config = config;
    }


    public ConfigClass getConfig() {
        return config;
    }

    public void setConfig(ConfigClass config) {
        this.config = config;
    }
}
