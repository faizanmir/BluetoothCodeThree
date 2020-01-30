package avishkaar.com.bluetoothcodethree

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import avishkaar.com.bluetoothcodethree.ModelClasses.RemoteModelClass
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_controller_actvity.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.io.IOException
import java.util.*

class ControllerActivity : AppCompatActivity(), OnTouchListener {
    lateinit var bluetoothAdapter: BluetoothAdapter
    var deviceAddress: String? = null
    lateinit var socket: BluetoothSocket
    var bluetoothDevice: BluetoothDevice? = null
    var progressBar: ProgressBar? = null
    lateinit var up: CardView
    lateinit var down: CardView
    lateinit var right: CardView
    lateinit var left: CardView
    lateinit var upImg: ImageView
    lateinit var dwnImg: ImageView
    lateinit var leftImg: ImageView
    lateinit var rightImg: ImageView
    var edit: ImageView? = null
    lateinit var blue: CardView
    lateinit var orange: CardView
    lateinit var yellow: CardView
    lateinit var red: CardView
    var configureCard: CardView? = null
    var statusCard: CardView? = null
    var back: CardView? = null
    var sharedPreferences: SharedPreferences? = null
    var blueText: TextView? = null
    var redText: TextView? = null
    var orangeText: TextView? = null
    var yellowText: TextView? = null
    lateinit var status: TextView
    var bluetoothManager: BluetoothManager? = null
    var overlay: RelativeLayout? = null
    var connectingCard: CardView? = null
    var firebaseDatabase: DatabaseReference? = null
    var remoteModelClassArrayList: ArrayList<RemoteModelClass>? = null
    var handler: Handler? = null
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setTextViews()
        configureCard?.setOnClickListener {
            val intent = Intent(this@ControllerActivity, ConfigureActivity::class.java)
            startActivity(intent)
        }

        back?.setOnClickListener { finish() }
        sendUserText.setOnClickListener {
            writeToBluetooth(userText.text.toString())
            userText.text.clear()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (v.id) {
            R.id.upMotion -> actionDetection(event, "F", "X", R.id.upMotion)
            R.id.downMotion -> actionDetection(event, "B", "X", R.id.downMotion)
            R.id.leftMotion -> actionDetection(event, "L", "X", R.id.leftMotion)
            R.id.rightMotion -> actionDetection(event, "R", "X", R.id.rightMotion)
            R.id.blue -> actionDetection(event, sharedPreferences!!.getString(Constants.bluePressed, ""), sharedPreferences!!.getString(Constants.blueRelease, ""), R.id.blue)
            R.id.orange -> actionDetection(event, sharedPreferences!!.getString(Constants.orangePressed, ""), sharedPreferences!!.getString(Constants.orangeRelease, ""), R.id.orange)
            R.id.yellow -> actionDetection(event, sharedPreferences!!.getString(Constants.yellowPress, ""), sharedPreferences!!.getString(Constants.yellowReleased, ""), R.id.yellow)
            R.id.red -> actionDetection(event, sharedPreferences!!.getString(Constants.redPressed, ""), sharedPreferences!!.getString(Constants.redReleased, ""), R.id.red)
        }
        return true
    }

    private fun writeToBluetooth(instruction: String?) {
        try {
            CoroutineScope(IO).launch {
                runBlocking {
                    Log.e(TAG,"Writing")
                    socket.outputStream.write(instruction?.toByteArray())
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    fun init() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        setContentView(R.layout.activity_controller_actvity)
        progressBar = findViewById(R.id.barForProgress)
        val intent = intent
        deviceAddress = intent.getStringExtra(DeviceListActivity.DEVICE_EXTRA)
        Log.e(TAG, "init: device Address$deviceAddress")
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)
        upImg = findViewById(R.id.upImg)
        dwnImg = findViewById(R.id.downImg)
        leftImg = findViewById(R.id.leftImg)
        rightImg = findViewById(R.id.rightImg)
        upImg.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
        dwnImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
        rightImg.setImageResource(R.drawable.ic_keyboard_arrow_right_black_24dp)
        leftImg.setImageResource(R.drawable.ic_keyboard_arrow_left_black_24dp)
        edit = findViewById(R.id.edit)
        blue = findViewById(R.id.blue)
        orange = findViewById(R.id.orange)
        yellow = findViewById(R.id.yellow)
        red = findViewById(R.id.red)
        sharedPreferences = getSharedPreferences(RemoteSharedPreference, Context.MODE_PRIVATE)
        up = findViewById(R.id.upMotion)
        down = findViewById(R.id.downMotion)
        left = findViewById(R.id.leftMotion)
        right = findViewById(R.id.rightMotion)
        redText = findViewById(R.id.redText)
        blueText = findViewById(R.id.blueText)
        yellowText = findViewById(R.id.yellowText)
        orangeText = findViewById(R.id.orangeText)
        configureCard = findViewById(R.id.configureItem)
        // new ServerClass().start(); /*** Server started **/
        CoroutineScope(IO).launch {
            runBlocking {
                socket = makeConnectionToBluetoothDevice()
                try {
                    socket.connect()
                } catch (e: IOException) {
                    makeUiChanges(socket)
                    e.printStackTrace()
                }
                Log.e(TAG, "doInBackground: Client Thread  :" + "Connection has been established...")
                Log.e(TAG, "doInBackground: " + "Is Socket Connected  ?  " + socket?.isConnected)
                if (socket.isConnected) {
                    makeUiChanges(socket)
                }
            }

        }
        up.setOnTouchListener(this)
        down.setOnTouchListener(this)
        right.setOnTouchListener(this)
        left.setOnTouchListener(this)
        blue.setOnTouchListener(this)
        orange.setOnTouchListener(this)
        yellow.setOnTouchListener(this)
        red.setOnTouchListener(this)
        status = findViewById(R.id.status)
        statusCard = findViewById(R.id.statusCard)
        back = findViewById(R.id.back)
        status.text = "Disconnected"
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        overlay = findViewById(R.id.overlay)
        connectingCard = findViewById(R.id.connectingCard)
        firebaseDatabase = FirebaseDatabase.getInstance().reference
        remoteModelClassArrayList = ArrayList()
        handler = Handler()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun actionDetection(event: MotionEvent, pressed: String?, released: String?, viewId: Int) {
        if (!(pressed!!.isEmpty() && released!!.isEmpty())) {
            val c = findViewById<CardView>(viewId)
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (viewId == R.id.upMotion || viewId == R.id.downMotion || viewId == R.id.leftMotion || viewId == R.id.rightMotion) {
                    c.setCardBackgroundColor(Color.parseColor("#000000"))
                    writeToBluetooth(pressed)
                } else if (viewId == blue.id) {
                    c.setCardBackgroundColor(Color.parseColor("#800064ab"))
                    writeToBluetooth(pressed)
                } else if (viewId == orange.id) {
                    c.setCardBackgroundColor(Color.parseColor("#80ff6100"))
                    writeToBluetooth(pressed)
                } else if (viewId == yellow.id) {
                    c.setCardBackgroundColor(Color.parseColor("#80ffaa00"))
                    writeToBluetooth(pressed)
                } else if (viewId == red.id) {
                    c.setCardBackgroundColor(Color.parseColor("#80fc0014"))
                    writeToBluetooth(pressed)
                }
            } else if (event.action == MotionEvent.ACTION_UP) {
                if (viewId == R.id.upMotion || viewId == R.id.downMotion || viewId == R.id.leftMotion || viewId == R.id.rightMotion) {
                    c.setCardBackgroundColor(Color.parseColor("#353535"))
                    writeToBluetooth(released)
                } else if (viewId == blue.id) {
                    c.setCardBackgroundColor(Color.parseColor("#0064ab"))
                    writeToBluetooth(released)
                } else if (viewId == orange.id) {
                    c.setCardBackgroundColor(Color.parseColor("#ff6100"))
                    writeToBluetooth(released)
                } else if (viewId == yellow.id) {
                    c.setCardBackgroundColor(Color.parseColor("#ffaa00"))
                    writeToBluetooth(released)
                } else if (viewId == red.id) {
                    c.setCardBackgroundColor(Color.parseColor("#fc0014"))
                    writeToBluetooth(released)
                }
            }
        } else {
            Toast.makeText(this@ControllerActivity, "Please configure the button ", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        setTextViews()
    }

    private fun setTextViews() {
        blueText!!.text = sharedPreferences!!.getString(Constants.bluePressed, "")
        orangeText!!.text = sharedPreferences!!.getString(Constants.orangePressed, "")
        yellowText!!.text = sharedPreferences!!.getString(Constants.yellowPress, "")
        redText!!.text = sharedPreferences!!.getString(Constants.redPressed, "")
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


 /*   class SendReceiveThread(var bluetoothSocket: BluetoothSocket?) : Thread() {
        fun sendToDevice(data: String?) {
            try {
                Log.e(TAG, "sendToDevice: $data")
                /*
                * This code can be executed on the main thread too but then why burden the UI with network tasks
                *
                *
                *
                * */
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun run() {                                           CODE FOR SERVER :commented due to optimization problems
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


    private suspend fun makeConnectionToBluetoothDevice(): BluetoothSocket {
        return withContext<BluetoothSocket>(IO) {
            val deferred = async { bluetoothDevice!!.createInsecureRfcommSocketToServiceRecord(DeviceListActivity.UUIDForARDUINO) }
            return@withContext deferred.await()
        }

    }


    private suspend fun makeUiChanges(socket: BluetoothSocket) {
        withContext(Main)
        {

            if (socket.isConnected) {
                statusCard!!.visibility = View.VISIBLE
                status.text = "Connected"
            }
            else{
                statusCard!!.visibility = View.VISIBLE
                status.text = "Error !"
            }
            overlay!!.visibility = View.INVISIBLE
            connectingCard!!.visibility = View.INVISIBLE
        }

    }

    companion object {
        const val RemoteSharedPreference = "REMOTE-PREFERENCE"
        private const val TAG = "ControllerActivity"

    }
}