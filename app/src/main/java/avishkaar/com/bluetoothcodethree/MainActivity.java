package avishkaar.com.bluetoothcodethree;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    int REQUEST_ENABLE_BT = 0;
    BroadcastReceiver broadcastReceiver;
    private static final String TAG = "MainActivity";
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

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        final bluetoothInterface bluetoothInterface = new bluetoothInterface() {
            @Override
            public void bluetoothAddress(String deviceName, String deviceAddress, BluetoothDevice bluetoothDevice) {
                Log.e(TAG, "bluetoothAddress: InterfaceTriggered" + "    " + "DeviceReceived : " + deviceName);
                bluetoothAdapter.cancelDiscovery();
                EXTRA_DATA = deviceAddress;
                MainActivity.this.bluetoothDevice = bluetoothDevice;
                Intent intent = new Intent(MainActivity.this, ControllerActivity.class);
                intent.putExtra(DEVICE_EXTRA,deviceAddress);
                startActivity(intent);
            }
        };


        if (!bluetoothAdapter.isEnabled()) {
            Snackbar.make(findViewById(android.R.id.content), "Bluetooth not enabled", Snackbar.LENGTH_LONG)
                    .setAction("Enable", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        }
                    })
                    .setActionTextColor(Color.RED)
                    .show();
        } else if (bluetoothAdapter == null) {
            Toast.makeText(MainActivity.this, "Bluetooth not present", Toast.LENGTH_SHORT).show();
        }
        pairedBluetoothDevices = bluetoothAdapter.getBondedDevices();


        //new Device Adapter used for displaying paired devices too
        newBluetoothDevices.addAll(pairedList);
        final DeviceAdapter newDeviceAdapter = new DeviceAdapter(newBluetoothDevices, bluetoothInterface);
        RecyclerView.LayoutManager newDeviceLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        newDeviceRecyclerView.setLayoutManager(newDeviceLayoutManager);
        newDeviceRecyclerView.setAdapter(newDeviceAdapter);
        for (BluetoothDevice b : pairedBluetoothDevices
        ) {
            //pairedList.add(b);
//            deviceAdapter.notifyDataSetChanged();
            newBluetoothDevices.add(b);
            newDeviceAdapter.notifyDataSetChanged();

        }


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
//        pairedDeviceRecyclerView = findViewById(R.id.bondedRecyclerView);
        newDeviceRecyclerView = findViewById(R.id.newDeviceRecyclerView);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedList = new ArrayList<>();
        scan = findViewById(R.id.scanDevice);
        stop = findViewById(R.id.stopScan);
        newBluetoothDevices = new ArrayList<>();
        write = findViewById(R.id.write);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==REQUEST_ENABLE_BT)
        {

        }
    }
}


