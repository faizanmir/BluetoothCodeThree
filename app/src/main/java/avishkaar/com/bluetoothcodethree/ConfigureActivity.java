package avishkaar.com.bluetoothcodethree;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import avishkaar.com.bluetoothcodethree.ModelClasses.ConfigClass;
import avishkaar.com.bluetoothcodethree.ModelClasses.DataStringClass;
import avishkaar.com.bluetoothcodethree.ModelClasses.RemoteModelClass;

public class ConfigureActivity extends AppCompatActivity implements CustomCommandDialog.dataFromDialog {
    SharedPreferences sharedPreferences;
    CardView blue,orange,yellow,red,done;
    TextView blueText,redText,orangeText,yellowText;
    String remoteName;
    LayoutInflater layoutInflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);
        sharedPreferences = getSharedPreferences(ControllerActivity.RemoteSharedPreference, Context.MODE_PRIVATE);
        blue = findViewById(R.id.blue);//blue
        orange = findViewById(R.id.orange);//orange
        yellow = findViewById(R.id.yellow);//red
        red= findViewById(R.id.red);//yellow
        redText = findViewById(R.id.redText);
        blueText = findViewById(R.id.blueText);
        yellowText = findViewById(R.id.yellowText);
        orangeText = findViewById(R.id.orangeText);
        done = findViewById(R.id.done);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        layoutInflater = LayoutInflater.from(this);
        final View alertView = layoutInflater.inflate(R.layout.alert_dialog_layout,null);
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        final EditText userInput = alertView
                .findViewById(R.id.editTextDialogUserInput);

        final DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        triggerChange();


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    alertBuilder.setView(alertView).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            remoteName = userInput.getText().toString();
                            if (remoteName.length() > 0) {
                                firebaseDatabase.child(firebaseUser.getUid()).child(remoteName).setValue(new RemoteModelClass(new ConfigClass(
                                        remoteName
                                        , new DataStringClass(sharedPreferences.getString(Constants.bluePressed, ""), sharedPreferences.getString(Constants.blueRelease, ""))
                                        , new DataStringClass(sharedPreferences.getString(Constants.orangePressed, ""), sharedPreferences.getString(Constants.orangeRelease, ""))
                                        , new DataStringClass(sharedPreferences.getString(Constants.yellowPress, ""), sharedPreferences.getString(Constants.yellowReleased, ""))
                                        , new DataStringClass(sharedPreferences.getString(Constants.redPressed, ""), sharedPreferences.getString(Constants.redReleased, ""))
                                )));
                                finish();
                            } else {
                                Toast.makeText(ConfigureActivity.this, "Cant have a blank name ", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    }).create().show();

                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }

            }

        });

        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomCommandDialog dialog = new CustomCommandDialog();
                dialog.sharedPrefPass(sharedPreferences,1);
                dialog.show(getSupportFragmentManager(),"");
            }
        });

        orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomCommandDialog dialog = new CustomCommandDialog();
                dialog.sharedPrefPass(sharedPreferences,2);
                dialog.show(getSupportFragmentManager(),"");
            }
        });
        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomCommandDialog dialog = new CustomCommandDialog();
                dialog.sharedPrefPass(sharedPreferences,3);
                dialog.show(getSupportFragmentManager(),"");
            }
        });
        red.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void triggerChange() {
        blueText.setText(sharedPreferences.getString(Constants.bluePressed,""));
        orangeText.setText(sharedPreferences.getString(Constants.orangePressed,""));
        yellowText.setText(sharedPreferences.getString(Constants.yellowPress,""));
        redText.setText(sharedPreferences.getString(Constants.redPressed,""));
    }
}
