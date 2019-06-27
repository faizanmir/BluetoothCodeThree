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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import avishkaar.com.bluetoothcodethree.ModelClasses.ConfigClass;
import avishkaar.com.bluetoothcodethree.ModelClasses.DataStringClass;
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
    CardView colorButtonOne, colorButtonTwo, colorButtonThree, colorButtonFour, configureCard, statusCard, back;
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

//        firebaseDatabase.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Log.e(TAG, "onDataChange: "  + dataSnapshot.getValue());
//                RemoteModelClass test = dataSnapshot.getValue(RemoteModelClass.class);
//                if (test != null) {
//                    Log.e(TAG, "onChildAdded: " +  test.getConfig().getRedButton().getOnPressed() );
//                }
//                arrayList.add(test);
//                Log.e(TAG, "onChildAdded: " + arrayList.size() );
//
//               // Log.e(TAG, "onChildAdded:Data from Array "+arrayList.get(3).getConfig().getRedButton().getOnPressed() );
//
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//       ConfigClass test = new ConfigClass("Remote0"
//               ,new DataStringClass("L","R")
//               ,new DataStringClass("P","Q")
//               ,new DataStringClass("R","S")
//               ,new DataStringClass("K","L")
//       );
//
//        ConfigClass test1 = new ConfigClass("RemoteK"
//                ,new DataStringClass("R","R")
//                ,new DataStringClass("P","Q")
//                ,new DataStringClass("R","S")
//                ,new DataStringClass("K","L")
//        );
//
//        ConfigClass test2 = new ConfigClass("RemoteKH"
//                ,new DataStringClass("R","R")
//                ,new DataStringClass("P","Q")
//                ,new DataStringClass("R","S")
//                ,new DataStringClass("K","L")
//        );
//
//        ConfigClass test3 = new ConfigClass("RemoteKH"
//                ,new DataStringClass("R","R")
//                ,new DataStringClass("P","Q")
//                ,new DataStringClass("R","S")
//                ,new DataStringClass("K","L")
//        );
//
//       RemoteModelClass remoteModelClass =  new RemoteModelClass(test);
//       RemoteModelClass remoteModelClass1 = new RemoteModelClass(test1);
//       RemoteModelClass remoteModelClass2 = new RemoteModelClass(test2);
//       RemoteModelClass remoteModelClass3 = new RemoteModelClass(test3);
//
//
//        firebaseDatabase.child("Douglas Remote").setValue(remoteModelClass);
//        firebaseDatabase.child("Jake's Remote").setValue(remoteModelClass1);
//        firebaseDatabase.child("Frank's Remote").setValue(remoteModelClass2);
//        firebaseDatabase.child("James's Remote").setValue(remoteModelClass3);









    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        switch (v.getId())
        {
            case(R.id.upMotion):
                actionDetection(event, "X", "F",R.id.upMotion);
                break;
            case (R.id.downMotion):
                actionDetection(event, "X", "B",R.id.downMotion);
                break;
            case(R.id.leftMotion):
                actionDetection(event, "X", "L",R.id.leftMotion);
                break;
            case(R.id.rightMotion):
                actionDetection(event, "X", "R",R.id.rightMotion);
                break;
            case(R.id.custom1):
                actionDetection(event,sharedPreferences.getString(Constants.button1Released,""),sharedPreferences.getString(Constants.button1Pressed,""),R.id.custom1);
                break;
            case(R.id.custom2):
                actionDetection(event,sharedPreferences.getString(Constants.button2Released,""),sharedPreferences.getString(Constants.button2Pressed,""),R.id.custom2);
                break;
            case(R.id.custom3):
                actionDetection(event,sharedPreferences.getString(Constants.button4Released,""),sharedPreferences.getString(Constants.button4Pressed,""),R.id.custom3);

                break;
            case(R.id.custom4):
                actionDetection(event,sharedPreferences.getString(Constants.button3Released,""),sharedPreferences.getString(Constants.button3Pressed,""),R.id.custom4);
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
        if(socket!=null)
        {
            try {
                socket.getOutputStream().write(instruction.getBytes());
                socket.getOutputStream().flush();
                Log.e(TAG, "writeToBluetooth: " +  "  " + "Command Written"  + instruction );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    void actionDetection(MotionEvent event,String instructionUp,String instructionDown,int viewId)
    {
        if(!((instructionDown.isEmpty())&&(instructionUp.isEmpty()))) {

            CardView c = findViewById(viewId);


            if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(viewId == R.id.upMotion|| viewId ==R.id.downMotion || viewId ==R.id.leftMotion || viewId == R.id.rightMotion) {
                c.setCardBackgroundColor(android.graphics.Color.parseColor("#000000"));
            }

            else if(viewId == colorButtonOne.getId())
            {
                c.setCardBackgroundColor(Color.parseColor("#800064ab"));
            }
            else if(viewId == colorButtonTwo.getId())
            {
                c.setCardBackgroundColor(Color.parseColor("#80ff6100"));
            }
            else if(viewId ==  colorButtonThree.getId())
            {
                c.setCardBackgroundColor(Color.parseColor("#80fc0014"));
            }
            else if(viewId == colorButtonFour.getId())
            {
                c.setCardBackgroundColor(Color.parseColor("#80ffaa00"));
            }

            writeToBluetooth(instructionDown);
        }
            else if (event.getAction() == MotionEvent.ACTION_UP) {


            if(viewId == R.id.upMotion|| viewId ==R.id.downMotion || viewId ==R.id.leftMotion || viewId == R.id.rightMotion){
            c.setCardBackgroundColor(Color.parseColor("#353535"));
            }
            else if(viewId == colorButtonOne.getId())
            {
                c.setCardBackgroundColor(Color.parseColor("#0064ab"));
            }
            else if(viewId == colorButtonTwo.getId())
            {
                c.setCardBackgroundColor(Color.parseColor("#ff6100"));
            }
            else if(viewId ==  colorButtonThree.getId())
            {
                c.setCardBackgroundColor(Color.parseColor("#fc0014"));
            }
            else if(viewId == colorButtonFour.getId())
            {
                c.setCardBackgroundColor(Color.parseColor("#ffaa00"));
            }

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
        colorButtonOne = findViewById(R.id.custom1);
        colorButtonTwo = findViewById(R.id.custom2);
        colorButtonThree = findViewById(R.id.custom3);
        colorButtonFour = findViewById(R.id.custom4);
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
        colorButtonOne.setOnTouchListener(this);
        colorButtonTwo.setOnTouchListener(this);
        colorButtonThree.setOnTouchListener(this);
        colorButtonFour.setOnTouchListener(this);
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

