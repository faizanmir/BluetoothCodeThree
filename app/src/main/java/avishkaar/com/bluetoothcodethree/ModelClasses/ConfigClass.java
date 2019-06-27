package avishkaar.com.bluetoothcodethree.ModelClasses;

public class ConfigClass {
    String remoteName;
    DataStringClass redButton;
    DataStringClass blueButton;
    DataStringClass yellowButton;
    DataStringClass orangeButton;

    public ConfigClass(String remoteName, DataStringClass redButton, DataStringClass blueButton, DataStringClass yellowButton, DataStringClass orangeButton) {
        this.remoteName = remoteName;
        this.redButton = redButton;
        this.blueButton = blueButton;
        this.yellowButton = yellowButton;
        this.orangeButton = orangeButton;
    }

    public ConfigClass() {
    }

    public String getRemoteName() {
        return remoteName;
    }

    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

    public DataStringClass getRedButton() {
        return redButton;
    }

    public void setRedButton(DataStringClass redButton) {
        this.redButton = redButton;
    }

    public DataStringClass getBlueButton() {
        return blueButton;
    }

    public void setBlueButton(DataStringClass blueButton) {
        this.blueButton = blueButton;
    }

    public DataStringClass getYellowButton() {
        return yellowButton;
    }

    public void setYellowButton(DataStringClass yellowButton) {
        this.yellowButton = yellowButton;
    }

    public DataStringClass getOrangeButton() {
        return orangeButton;
    }

    public void setOrangeButton(DataStringClass orangeButton) {
        this.orangeButton = orangeButton;
    }
}
