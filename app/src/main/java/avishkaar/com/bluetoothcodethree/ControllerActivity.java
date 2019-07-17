package avishkaar.com.bluetoothcodethree;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import avishkaar.com.bluetoothcodethree.Fragments.JoyStickFragment;
import avishkaar.com.bluetoothcodethree.ModelClasses.RemoteModelClass;

import static avishkaar.com.bluetoothcodethree.DeviceListActivity.UUIDForARDUINO;
public class ControllerActivity extends AppCompatActivity implements View.OnTouchListener,JoyStickFragment.OnFragmentInteractionListener{
    BluetoothAdapter bluetoothAdapter;
    static SendReceiveThread sendReceiveThread;
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
    TextView status,autoCardText;
    BluetoothManager bluetoothManager;
    RelativeLayout overlay;
    CardView connectingCard;
    DatabaseReference firebaseDatabase;
    ArrayList<RemoteModelClass> remoteModelClassArrayList;
    Switch aSwitch;
    Handler handler;
    CardView autoCardView;
    int flagForAutoCardView;
    int flagForSectorOne,flagForSectorTwo,flagForSectorThree,flagForSectorFour;


    @Override
    public void onFragmentInteraction(int angle, int strength, int id) {
            try {
                /**
                // Ladies and Gentlemen WE HAVE MADE THE ROBOT DANCE 15 jul 19
                //

                 **/
                if(socket!=null) {
                    if (strength >=90) {

                        if ((angle > 45 && angle <= 135)) {

                            if (flagForSectorOne == 0) {
                                socket.getOutputStream().write("F".getBytes());
                                socket.getOutputStream().flush();
                                resetValues(0);
                            }

                        } else if (angle > 135 && angle <= 225) {
                            if (flagForSectorTwo == 0) {
                                socket.getOutputStream().write("L".getBytes());
                                socket.getOutputStream().flush();
                                resetValues(1);
                            }
                        } else if (angle > 225 && angle < 315) {
                            if (flagForSectorThree == 0) {
                                socket.getOutputStream().write("B".getBytes());
                                socket.getOutputStream().flush();
                                resetValues(2);
                            }
                        } else if (angle > 315 ) {
                            if (flagForSectorFour == 0) {
                                socket.getOutputStream().write("R".getBytes());
                                socket.getOutputStream().flush();
                                resetValues(3);
                            }
                        } else if (angle <= 45 && angle >= 0) {

                            if (flagForSectorFour == 0) {
                                Log.e(TAG, "onFragmentInteraction: " + "ANGLE IN SECTOR 5");
                                socket.getOutputStream().write("R".getBytes());
                                socket.getOutputStream().flush();
                                resetValues(3);
                            }
                        }

                    }
                    else if(strength ==0) {
                        socket.getOutputStream().write("X".getBytes());
                        socket.getOutputStream().flush();
                        resetValues(99);

                    }
                }

            }catch ( IOException e) {
                e.printStackTrace();
            }


    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setTextViews();


        findViewById(R.id.joystickSwitch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoyStickFragment joyStickFragment = JoyStickFragment.newInstance();
                joyStickFragment.show(getSupportFragmentManager(),"");

            }
        });


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


        autoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(flagForAutoCardView == 0 )
                {
                    writeToBluetooth("K");
                    autoCardView.setCardBackgroundColor(Color.parseColor("#000000"));
                    autoCardText.setTextColor(Color.parseColor("#FFFFFF"));
                    flagForAutoCardView=1;
                }
                else if(flagForAutoCardView ==1)
                {
                    writeToBluetooth("W");
                    autoCardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                    autoCardText.setTextColor(Color.parseColor("#000000"));
                    flagForAutoCardView=0;
                }
            }
        });





    }

    @SuppressLint("ClickableViewAccessibility")
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

    void writeToBluetooth(String instruction) {

        if (socket != null) {
            try {
                sendReceiveThread.sendToDevice(instruction);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }


    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    void init() {
        flagForAutoCardView =0;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setContentView(R.layout.activity_controller_actvity);
        progressBar = findViewById(R.id.barForProgress);
        Intent intent = getIntent();
        autoCardView = findViewById(R.id.autoCard);
        autoCardText = findViewById(R.id.autoCardText);
        deviceAddress = intent.getStringExtra(DeviceListActivity.DEVICE_EXTRA);
        Log.e(TAG, "init: " + "device Address" + deviceAddress);
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

       // new ServerClass().start(); /*** Server started **/
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
        remoteModelClassArrayList = new ArrayList<>();
        aSwitch = findViewById(R.id.aSwitch);
        handler = new Handler();
    }

    void resetValues(int identifier)
    {
        if(identifier == 0)
        {
            flagForSectorOne=1;
            flagForSectorTwo =0;
            flagForSectorThree=0;
            flagForSectorFour=0;
        }
        else if(identifier == 1)
        {
            flagForSectorOne=0;
            flagForSectorTwo =1;
            flagForSectorThree=0;
            flagForSectorFour=0;
        }
        else if(identifier==2)
        {
            flagForSectorOne=0;
            flagForSectorTwo =0;
            flagForSectorThree=1;
            flagForSectorFour=0;
        }
        else if(identifier ==3)
        {
            flagForSectorOne=0;
            flagForSectorTwo =0;
            flagForSectorThree=0;
            flagForSectorFour=1;
        }
        else
        {
            flagForSectorOne=0;
            flagForSectorTwo =0;
            flagForSectorThree=0;
            flagForSectorFour=0;
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

    //*******************************************************************************************************************
    @SuppressLint("StaticFieldLeak")
    class ConnectionThread extends AsyncTask<Void, Void, Void>
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
                ref.socket.connect();
                sendReceiveThread = new SendReceiveThread(socket);
                sendReceiveThread.start();
                Log.e(TAG, "doInBackground: Client Thread  :" +  "Connection has been established..." );
                Log.e(TAG, "doInBackground: " + "Is Socket Connected  ?  " + ref.socket.isConnected());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
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

    void actionDetection(MotionEvent event,String pressed,String released,int viewId)
    {
        if(!((pressed.isEmpty())&&(released.isEmpty()))) {
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

    private class ServerClass extends Thread
    {
        private BluetoothServerSocket serverSocket;

        ServerClass() {
            try {
                Log.e(TAG, "ServerClass: Starting Server...." );
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("Bt3", UUIDForARDUINO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            BluetoothSocket socket = null;

            while (socket == null) {
                try {
                    Log.e(TAG, "ServerThread " + "Accept Thread running...");
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();

                }

                if (socket != null) {
                    Log.e(TAG, "run: " + "Socket Acquired ...");
                    sendReceiveThread = new SendReceiveThread(socket);
                    sendReceiveThread.start();
                    break;
                }
            }
        }
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

    class SendReceiveThread extends Thread {
        BluetoothSocket bluetoothSocket;


        SendReceiveThread(BluetoothSocket bluetoothSocket) {
            this.bluetoothSocket = bluetoothSocket;
        }

        void sendToDevice(String data) {
            try {
                Log.e(TAG, "sendToDevice: " + data );

                /*
                * This code can be executed on the main thread too but then why burden the UI with network tasks
                *
                *
                *
                * */

                bluetoothSocket.getOutputStream().write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {

            /*                                          CODE FOR SERVER :commented due to optimization problems
             *      The thread used in this is an infinite loop causing problems
             *      need to find a way to make the loop run seamlessly without causing processes slowdown
             *      probably use a looper
             *
             *     16 Jul 19
             *
             *
             *
             *
             *
             * */


 //           byte[] buffer = new byte[1024];
//            int bytes;
//
//            while (true) {
//                try {
//                    if (bluetoothSocket.getInputStream().available() > 0) {
//                        bytes = bluetoothSocket.getInputStream().read(buffer);
//                        String readMessage = new String(buffer, 0, bytes);
//                        Log.e(TAG, "Message received......" + readMessage);
//
//
//                    } else {
//                        Log.e(TAG, "Sleeping....." );
//                        SystemClock.sleep(100);
//                    }
//                } catch (IOException e) {
//                    Log.e(TAG, "Error reading from bluetooth..." );
//                    e.printStackTrace();
 //               }
            //
            //        }
        }

    }
}

