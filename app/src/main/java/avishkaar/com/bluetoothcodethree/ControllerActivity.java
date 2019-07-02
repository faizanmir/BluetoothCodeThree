package avishkaar.com.bluetoothcodethree;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import avishkaar.com.bluetoothcodethree.ModelClasses.RemoteModelClass;

import static avishkaar.com.bluetoothcodethree.DeviceListActivity.UUIDForARDUINO;

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
    CardView blue, orange, yellow, red, configureCard, statusCard, back;
    SharedPreferences sharedPreferences;
    TextView blueText,redText,orangeText,yellowText;
    TextView status;
    BluetoothManager bluetoothManager;
    RelativeLayout overlay;
    CardView connectingCard;
    DatabaseReference firebaseDatabase;
    ArrayList<RemoteModelClass> arrayList;
    Switch aSwitch;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setTextViews();


        configureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ControllerActivity.this,ConfigureActivity.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    writeToBluetooth("X");//place holder
                }
                else {
                    writeToBluetooth("X");//place Holder
                }
            }
        });

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        switch (v.getId())
        {
            case(R.id.upMotion):
                actionDetection(event, "F", "X", R.id.upMotion);
                break;
            case (R.id.downMotion):
                actionDetection(event, "B", "X", R.id.downMotion);
                break;
            case(R.id.leftMotion):
                actionDetection(event, "L", "X", R.id.leftMotion);
                break;
            case(R.id.rightMotion):
                actionDetection(event, "R", "X", R.id.rightMotion);
                break;
            case(R.id.blue):
                actionDetection(event,sharedPreferences.getString(Constants.bluePressed,""),sharedPreferences.getString(Constants.blueRelease,""),R.id.blue);
                break;
            case(R.id.orange):
                actionDetection(event,sharedPreferences.getString(Constants.orangePressed,""),sharedPreferences.getString(Constants.orangeRelease,""),R.id.orange);
                break;
            case(R.id.yellow):
                actionDetection(event,sharedPreferences.getString(Constants.yellowPress,""),sharedPreferences.getString(Constants.yellowReleased,""),R.id.yellow);

                break;
            case(R.id.red):
                actionDetection(event,sharedPreferences.getString(Constants.redPressed,""),sharedPreferences.getString(Constants.redReleased,""),R.id.red);
                break;
        }
        return true;
    }
    //*******************************************************************************************************************
    static class ConnectionThread extends AsyncTask<Void, Void, Void>
    {
        WeakReference<ControllerActivity> weakReference;

        ConnectionThread(ControllerActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ControllerActivity ref = weakReference.get();
                ref.socket = ref.bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUIDForARDUINO);
                ref.bluetoothAdapter.cancelDiscovery();
                ref.socket.connect();
                Log.e(TAG, "doInBackground: " +  "Connection has been established..." );
                Log.e(TAG, "doInBackground: " + "Is Socket Connected  ?  " + ref.socket.isConnected());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ControllerActivity ref = weakReference.get();
            if (ref.socket.isConnected()) {
                ref.statusCard.setVisibility(View.VISIBLE);
                ref.status.setText("Connected");
            }
            ref.overlay.setVisibility(View.INVISIBLE);
            ref.connectingCard.setVisibility(View.INVISIBLE);

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
    void writeToBluetooth(String instruction)
    {
        Log.e(TAG, "writeToBluetooth: " + instruction );
        if(socket!=null)
        {
            try {
                socket.getOutputStream().write(instruction.getBytes());
                socket.getOutputStream().flush();
               // Log.e(TAG, "writeToBluetooth: " +  "  " + "Command Written"  + instruction );
            } catch (IOException e) {
               // e.printStackTrace();
            }
        }


    }

    void actionDetection(MotionEvent event,String pressed,String released,int viewId)
    {
        if(!((pressed.isEmpty())&&(released.isEmpty()))) {
            Log.e(TAG, "actionDetection: " +"Pressed : " +pressed + "Released" + released );
            CardView c = findViewById(viewId);

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(viewId == R.id.upMotion|| viewId ==R.id.downMotion || viewId ==R.id.leftMotion || viewId == R.id.rightMotion) {
                c.setCardBackgroundColor(android.graphics.Color.parseColor("#000000"));
                writeToBluetooth(pressed);
            }

            else if(viewId == blue.getId())
            {
                c.setCardBackgroundColor(Color.parseColor("#800064ab"));
                writeToBluetooth(pressed);
            }
            else if(viewId == orange.getId())
            {
                c.setCardBackgroundColor(Color.parseColor("#80ff6100"));
                writeToBluetooth(pressed);
            }
            else if(viewId ==  yellow.getId())
            {
                c.setCardBackgroundColor(Color.parseColor("#80ffaa00"));
                writeToBluetooth(pressed);
            }
            else if(viewId == red.getId())
            {
                c.setCardBackgroundColor(Color.parseColor("#80fc0014"));
                writeToBluetooth(pressed);
            }


        }
            else if (event.getAction() == MotionEvent.ACTION_UP) {

            if(viewId == R.id.upMotion|| viewId ==R.id.downMotion || viewId ==R.id.leftMotion || viewId == R.id.rightMotion){
            c.setCardBackgroundColor(Color.parseColor("#353535"));
                writeToBluetooth(released);
            }
            else if(viewId == blue.getId())
            {
                c.setCardBackgroundColor(Color.parseColor("#0064ab"));
                writeToBluetooth(released);
            }
            else if(viewId == orange.getId())
            {
                c.setCardBackgroundColor(Color.parseColor("#ff6100"));
                writeToBluetooth(released);
            }
            else if(viewId ==  yellow.getId())
            {
                c.setCardBackgroundColor(Color.parseColor("#ffaa00"));
                writeToBluetooth(released);
            }
            else if(viewId == red.getId())
            {
                c.setCardBackgroundColor(Color.parseColor("#fc0014"));
                writeToBluetooth(released);
            }


        }
    }
        else {
            Toast.makeText(ControllerActivity.this,"Please configure the button ",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
       setTextViews();

    }


    @SuppressLint("ClickableViewAccessibility")
    void init() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        setContentView(R.layout.activity_controller_actvity);
        progressBar = findViewById(R.id.barForProgress);
        Intent intent = getIntent();
        deviceAddress = intent.getStringExtra(DeviceListActivity.DEVICE_EXTRA);
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
        upImg = findViewById(R.id.upImg);
        dwnImg = findViewById(R.id.downImg);
        leftImg = findViewById(R.id.leftImg);
        rightImg = findViewById(R.id.rightImg);
        upImg.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        dwnImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        rightImg.setImageResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
        leftImg.setImageResource(R.drawable.ic_keyboard_arrow_left_black_24dp);
        edit = findViewById(R.id.edit);
        write = findViewById(R.id.write);
        blue = findViewById(R.id.blue);
        orange = findViewById(R.id.orange);
        yellow = findViewById(R.id.yellow);
        red = findViewById(R.id.red);
        sharedPreferences = getSharedPreferences(RemoteSharedPreference, Context.MODE_PRIVATE);
        up = findViewById(R.id.upMotion);
        down = findViewById(R.id.downMotion);
        left = findViewById(R.id.leftMotion);
        right = findViewById(R.id.rightMotion);
        redText = findViewById(R.id.redText);
        blueText = findViewById(R.id.blueText);
        yellowText = findViewById(R.id.yellowText);
        orangeText = findViewById(R.id.orangeText);
        configureCard = findViewById(R.id.configureItem);
        new ConnectionThread(ControllerActivity.this).execute();
        up.setOnTouchListener(this);
        down.setOnTouchListener(this);
        right.setOnTouchListener(this);
        left.setOnTouchListener(this);
        blue.setOnTouchListener(this);
        orange.setOnTouchListener(this);
        yellow.setOnTouchListener(this);
        red.setOnTouchListener(this);
        status = findViewById(R.id.status);
        statusCard = findViewById(R.id.statusCard);
        back = findViewById(R.id.back);
        status.setText("Disconnected");
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        overlay = findViewById(R.id.overlay);
        connectingCard = findViewById(R.id.connectingCard);
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        arrayList = new ArrayList<>();
        aSwitch = findViewById(R.id.aSwitch);

    }

    void setTextViews()
    {
        blueText.setText(sharedPreferences.getString(Constants.bluePressed,""));
        orangeText.setText(sharedPreferences.getString(Constants.orangePressed,""));
        yellowText.setText(sharedPreferences.getString(Constants.yellowPress,""));
        redText.setText(sharedPreferences.getString(Constants.redPressed,""));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

