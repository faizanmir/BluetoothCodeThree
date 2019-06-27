package avishkaar.com.bluetoothcodethree.ModelClasses;

import java.util.HashMap;
import java.util.Map;

public class DataStringClass {
    String onPressed;
    String onRelease;

    public DataStringClass(String onPressed, String onRelease) {
        this.onPressed = onPressed;
        this.onRelease = onRelease;
    }

    public DataStringClass() {
    }

    public String getOnPressed() {
        return onPressed;
    }

    public String getOnRelease() {
        return onRelease;
    }

    public void setOnPressed(String onPressed) {
        this.onPressed = onPressed;
    }

    public void setOnRelease(String onRelease) {
        this.onRelease = onRelease;
    }

    Map<String,Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("onPressed",this.onPressed);
        map.put("onRelease",this.onRelease);
        return map;
    }
}
