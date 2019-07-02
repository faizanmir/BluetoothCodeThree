package avishkaar.com.bluetoothcodethree;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import avishkaar.com.bluetoothcodethree.Adapters.DeviceAdapter;
import avishkaar.com.bluetoothcodethree.Interfaces.bluetoothInterface;

public class DeviceListActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    int REQUEST_ENABLE_BT = 0;
    BroadcastReceiver broadcastReceiver;
    private static final String TAG = "DeviceListActivity";
    Set<BluetoothDevice> pairedBluetoothDevices;
    ArrayList<BluetoothDevice> pairedList;
    RecyclerView newDeviceRecyclerView;
    ArrayList<BluetoothDevice> newBluetoothDevices;
    String EXTRA_DATA;
    Button scan, stop,write;
    BluetoothDevice bluetoothDevice;
    static final UUID UUIDForARDUINO = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static final  String DEVICE_EXTRA= "dev";
    IntentFilter filter;
    DeviceAdapter newDeviceAdapter;
    RecyclerView.LayoutManager newDeviceLayoutManager;
     avishkaar.com.bluetoothcodethree.Interfaces.bluetoothInterface bluetoothInterface;
     DatabaseReference firebaseReference ;

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseReference = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(DeviceListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(DeviceListActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
        }

        bluetoothInterface= new bluetoothInterface() {
            @Override
            public void bluetoothAddress(String deviceName, String deviceAddress, BluetoothDevice bluetoothDevice) {
                Log.e(TAG, "bluetoothAddress: InterfaceTriggered" + "    " + "DeviceReceived : " + deviceName);
                bluetoothAdapter.cancelDiscovery();
                EXTRA_DATA = deviceAddress;
                DeviceListActivity.this.bluetoothDevice = bluetoothDevice;
                Intent intent = new Intent(DeviceListActivity.this, RemoteSelectionFromFirebase.class);
                intent.putExtra(DEVICE_EXTRA,deviceAddress);
                startActivity(intent);
            }
        };
        init();


        if (!bluetoothAdapter.isEnabled()) {
            Snackbar.make(findViewById(android.R.id.content), "Bluetooth not enabled", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Enable", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        }
                    })
                    .setActionTextColor(Color.WHITE)
                    .show();
        } else if (bluetoothAdapter == null) {
            Toast.makeText(DeviceListActivity.this, "Bluetooth not present", Toast.LENGTH_SHORT).show();
        }
        pairedBluetoothDevices = bluetoothAdapter.getBondedDevices();


        //new Device Adapter used for displaying paired devices too
        newBluetoothDevices.addAll(pairedBluetoothDevices);
        newDeviceRecyclerView.setAdapter(newDeviceAdapter);
        newDeviceRecyclerView.setLayoutManager(newDeviceLayoutManager);
        triggerDeviceAddition();




        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        Log.e(TAG, "onReceive: New Device Received" + device.getName() + "with Address " + device.getAddress());
                        newBluetoothDevices.add(device);
                        newDeviceAdapter.notifyDataSetChanged();
                    }
                }
            }
        };


        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(broadcastReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(broadcastReceiver, filter);


        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                bluetoothAdapter.startDiscovery();

            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothAdapter.cancelDiscovery();
            }
        });


    }


    private void init() {
        newDeviceRecyclerView = findViewById(R.id.newDeviceRecyclerView);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedList = new ArrayList<>();
        scan = findViewById(R.id.scanDevice);
        stop = findViewById(R.id.stopScan);
        newBluetoothDevices = new ArrayList<>();
        write = findViewById(R.id.write);
        newDeviceAdapter  = new DeviceAdapter(newBluetoothDevices, bluetoothInterface);
        newDeviceLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.cancelDiscovery();

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_ENABLE_BT)
        {
            triggerDeviceAddition();
        }
    }


    void triggerDeviceAddition()
    {   bluetoothAdapter.startDiscovery();
        pairedBluetoothDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice b : pairedBluetoothDevices
        ) {
            if(!newBluetoothDevices.contains(b)) {
                newBluetoothDevices.add(b);
                newDeviceAdapter.notifyDataSetChanged();
            }

        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }
}


