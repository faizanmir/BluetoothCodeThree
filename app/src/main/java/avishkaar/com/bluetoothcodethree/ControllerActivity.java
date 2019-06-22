package avishkaar.com.bluetoothcodethree;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;

import static avishkaar.com.bluetoothcodethree.MainActivity.UUIDForARDUINO;

public class ControllerActivity extends AppCompatActivity implements View.OnTouchListener{
    BluetoothAdapter bluetoothAdapter;
    String deviceAddress;
    BluetoothSocket socket;
    BluetoothDevice bluetoothDevice;
    Button write;
    ProgressBar progressBar;
    private static final String TAG = "ControllerActivity";
    CardView up,down,right,left;
    ImageView upImg,dwnImg,leftImg,rightImg,edit;
    static final String RemoteSharedPreference = "REMOTE-PREFERENCE";
    CardView colorButtonOne,colorButtonTwo,colorButtonThree,colorButtonFour,configureCard;
    SharedPreferences sharedPreferences;
    TextView blueText,redText,orangeText,yellowText;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setContentView(R.layout.activity_controller_actvity);
        progressBar = findViewById(R.id.barForProgress);
        Intent intent = getIntent();
        deviceAddress =  intent.getStringExtra(MainActivity.DEVICE_EXTRA);
        bluetoothDevice =  bluetoothAdapter.getRemoteDevice(deviceAddress);
        upImg = findViewById(R.id.upImg);
        dwnImg =  findViewById(R.id.downImg);
        leftImg = findViewById(R.id.leftImg);
        rightImg = findViewById(R.id.rightImg);
        upImg.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        dwnImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        rightImg.setImageResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
        leftImg.setImageResource(R.drawable.ic_keyboard_arrow_left_black_24dp);
        edit =findViewById(R.id.edit);
        write = findViewById(R.id.write);
        colorButtonOne = findViewById(R.id.custom1);
        colorButtonTwo = findViewById(R.id.custom2);
        colorButtonThree = findViewById(R.id.custom3);
        colorButtonFour = findViewById(R.id.custom4);
        sharedPreferences =  getSharedPreferences(RemoteSharedPreference, Context.MODE_PRIVATE);
        up = findViewById(R.id.upMotion);
        down = findViewById(R.id.downMotion);
        left = findViewById(R.id.leftMotion);
        right =  findViewById(R.id.rightMotion);
        redText = findViewById(R.id.redText);
        blueText = findViewById(R.id.blueText);
        yellowText = findViewById(R.id.yellowText);
        orangeText = findViewById(R.id.orangeText);
        configureCard = findViewById(R.id.configureItem);


        //This is used for establishing the connection with the device as it is a blocking call  an Async Tsk is  used
        new ConnectionThread(ControllerActivity.this).execute();

        up.setOnTouchListener(this);
        down.setOnTouchListener(this);
        right.setOnTouchListener(this);
        left.setOnTouchListener(this);
        colorButtonOne.setOnTouchListener(this);
        colorButtonTwo.setOnTouchListener(this);
        colorButtonThree.setOnTouchListener(this);
        colorButtonFour.setOnTouchListener(this);
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (socket != null) {
                    try {
                        socket.getOutputStream().write("M".getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        configureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ControllerActivity.this,ConfigureActivity.class);
                startActivity(intent);
            }
        });
    }

    //********************************************ON Touch***************************************************************
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId())
        {
            case(R.id.upMotion):
                actionDetection(event,"0","S");
                break;
            case (R.id.downMotion):
                actionDetection(event,"0","M");
                break;
            case(R.id.leftMotion):
                actionDetection(event,"0","L");
                break;
            case(R.id.rightMotion):
                actionDetection(event,"0","R");
                break;
            case(R.id.custom1):
                actionDetection(event,sharedPreferences.getString(Constants.button1Released,""),sharedPreferences.getString(Constants.button1Pressed,""));
                break;
            case(R.id.custom2):
                actionDetection(event,sharedPreferences.getString(Constants.button2Released,""),sharedPreferences.getString(Constants.button2Pressed,""));
                break;
            case(R.id.custom3):
                actionDetection(event,sharedPreferences.getString(Constants.button4Released,""),sharedPreferences.getString(Constants.button4Pressed,""));
                break;
            case(R.id.custom4):
                actionDetection(event,sharedPreferences.getString(Constants.button3Released,""),sharedPreferences.getString(Constants.button3Pressed,""));
                break;
        }
        return true;
    }
    //*******************************************************************************************************************
    class ConnectionThread extends AsyncTask<Void,Void,Void>
    {
        WeakReference<ControllerActivity> weakReference;
        Activity activity;

        public ConnectionThread(ControllerActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ControllerActivity ref = weakReference.get();
                socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUIDForARDUINO);
                bluetoothAdapter.cancelDiscovery();
                socket.connect();
                ref.progressBar.setVisibility(View.INVISIBLE);

                Log.e(TAG, "doInBackground: " +  "Connection has been established..." );
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
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



    void writeToBluetooth(String instruction)
    {
        if(socket!=null)
        {
            try {
                socket.getOutputStream().write(instruction.getBytes());
                Log.e(TAG, "writeToBluetooth: " +  "  " + "Command Written"  + instruction );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    void actionDetection(MotionEvent event,String instructionUp,String instructionDown)
    {
        if(!((instructionDown.isEmpty())&&(instructionUp.isEmpty()))) {
        Log.e(TAG, "actionDetection: " + instructionUp + instructionDown);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            writeToBluetooth(instructionDown);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            writeToBluetooth(instructionUp);
        }
    }
        else {
            Toast.makeText(ControllerActivity.this,"Please configure the button ",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        blueText.setText(sharedPreferences.getString(Constants.button1Pressed,""));
        orangeText.setText(sharedPreferences.getString(Constants.button2Pressed,""));
        yellowText.setText(sharedPreferences.getString(Constants.button3Pressed,""));
        redText.setText(sharedPreferences.getString(Constants.button4Pressed,""));
    }
}
