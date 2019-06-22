package avishkaar.com.bluetoothcodethree;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

public class ConfigureActivity extends AppCompatActivity implements CustomCommandDialog.dataFromDialog {
    SharedPreferences sharedPreferences;
    CardView colorButtonOne,colorButtonTwo,colorButtonThree,colorButtonFour,done,configureCard;
    TextView blueText,redText,orangeText,yellowText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);
        sharedPreferences = getSharedPreferences(ControllerActivity.RemoteSharedPreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        colorButtonOne = findViewById(R.id.custom1);//blue
        colorButtonTwo = findViewById(R.id.custom2);//orange
        colorButtonThree = findViewById(R.id.custom3);//red
        colorButtonFour = findViewById(R.id.custom4);//yellow
        redText = findViewById(R.id.redText);
        blueText = findViewById(R.id.blueText);
        yellowText = findViewById(R.id.yellowText);
        orangeText = findViewById(R.id.orangeText);
        done = findViewById(R.id.done);



        blueText.setText(sharedPreferences.getString(Constants.button1Pressed,""));
        orangeText.setText(sharedPreferences.getString(Constants.button2Pressed,""));
        yellowText.setText(sharedPreferences.getString(Constants.button3Pressed,""));
        redText.setText(sharedPreferences.getString(Constants.button4Pressed,""));






        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        colorButtonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomCommandDialog dialog = new CustomCommandDialog();
                dialog.sharedPrefPass(sharedPreferences,1);
                dialog.show(getSupportFragmentManager(),"");
            }
        });

        colorButtonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomCommandDialog dialog = new CustomCommandDialog();
                dialog.sharedPrefPass(sharedPreferences,2);
                dialog.show(getSupportFragmentManager(),"");
            }
        });
        colorButtonThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomCommandDialog dialog = new CustomCommandDialog();
                dialog.sharedPrefPass(sharedPreferences,3);
                dialog.show(getSupportFragmentManager(),"");
            }
        });
        colorButtonFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomCommandDialog dialog = new CustomCommandDialog();
                dialog.sharedPrefPass(sharedPreferences,4);
                dialog.show(getSupportFragmentManager(),"");
            }
        });










    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void triggerChange() {
        blueText.setText(sharedPreferences.getString(Constants.button1Pressed,""));
        orangeText.setText(sharedPreferences.getString(Constants.button2Pressed,""));
        yellowText.setText(sharedPreferences.getString(Constants.button3Pressed,""));
        redText.setText(sharedPreferences.getString(Constants.button4Pressed,""));
    }
}
