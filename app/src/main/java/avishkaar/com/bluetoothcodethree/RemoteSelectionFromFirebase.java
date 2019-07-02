package avishkaar.com.bluetoothcodethree;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import avishkaar.com.bluetoothcodethree.Adapters.FirebaseAdapter;
import avishkaar.com.bluetoothcodethree.Interfaces.dataPassToSelectionActivity;
import avishkaar.com.bluetoothcodethree.ModelClasses.RemoteModelClass;

public class RemoteSelectionFromFirebase extends AppCompatActivity {
    private static final String TAG = "RemoteSelectionFromFire";
DatabaseReference firebaseDatabase;
ArrayList<RemoteModelClass> savedRemoteArrayList;
String deviceAddress;
FirebaseAdapter firebaseAdapter;
dataPassToSelectionActivity ref;
SharedPreferences sharedPreferences;
FirebaseUser firebaseUser;
    RecyclerView firebaseRecyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_selection_from_firebase);
        sharedPreferences = getSharedPreferences(ControllerActivity.RemoteSharedPreference, Context.MODE_PRIVATE);
        Intent intent  = getIntent();
        deviceAddress = intent.getStringExtra(DeviceListActivity.DEVICE_EXTRA);

        ref = new dataPassToSelectionActivity() {
            @Override
            public void passDataToSelectionActivity(RemoteModelClass remoteModelClass) {

                String redOnPressed = remoteModelClass.getConfig().getRedButton().getOnPressed();
                String redOnRelease  = remoteModelClass.getConfig().getRedButton().getOnRelease();

                String blueOnPressed = remoteModelClass.getConfig().getBlueButton().getOnPressed();
                String blueOnRelease  = remoteModelClass.getConfig().getBlueButton().getOnRelease();

                String yellowOnPressed = remoteModelClass.getConfig().getYellowButton().getOnPressed();
                String yellowOnRelease  = remoteModelClass.getConfig().getYellowButton().getOnRelease();

                String orangeOnPressed = remoteModelClass.getConfig().getOrangeButton().getOnPressed();
                String orangeOnRelease  = remoteModelClass.getConfig().getOrangeButton().getOnRelease();

                Log.e(TAG, "passDataToSelectionActivity: " + redOnPressed + redOnRelease );
                sharedPreferences.edit().putString(Constants.blueRelease,blueOnRelease).apply();
                sharedPreferences.edit().putString(Constants.bluePressed,blueOnPressed).apply();

                sharedPreferences.edit().putString(Constants.orangeRelease,orangeOnRelease).apply();
                sharedPreferences.edit().putString(Constants.orangePressed,orangeOnPressed).apply();


                sharedPreferences.edit().putString(Constants.redReleased,redOnRelease).apply();
                sharedPreferences.edit().putString(Constants.redPressed,redOnPressed).apply();

                sharedPreferences.edit().putString(Constants.yellowReleased,yellowOnRelease).apply();
                sharedPreferences.edit().putString(Constants.yellowPress,yellowOnPressed).apply();

                Intent intent = new Intent(RemoteSelectionFromFirebase.this,ControllerActivity.class);
                intent.putExtra(DeviceListActivity.DEVICE_EXTRA,deviceAddress);

                startActivity(intent);
            }
        };


        init();
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP | ItemTouchHelper.DOWN) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                Snackbar.make(findViewById(android.R.id.content), "Delete Remote ?", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Delete", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    firebaseDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(savedRemoteArrayList.get(position).getConfig().getRemoteName()).removeValue();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                                savedRemoteArrayList.remove(position);
                                firebaseAdapter.notifyDataSetChanged();
                            }
                        })
                        .setActionTextColor(Color.parseColor("#FFFFFF"))
                        .show();


            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);

        itemTouchHelper.attachToRecyclerView(firebaseRecyclerview);







        firebaseDatabase.child(firebaseUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                RemoteModelClass dataFromFireBase =  dataSnapshot.getValue(RemoteModelClass.class);

                savedRemoteArrayList.add(dataFromFireBase);
                firebaseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void init()
    {
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        savedRemoteArrayList = new ArrayList<>();
        firebaseRecyclerview = findViewById(R.id.firebaseRecyclerview);
        firebaseAdapter = new FirebaseAdapter(savedRemoteArrayList, ref, firebaseDatabase);
        RecyclerView.LayoutManager layoutManager  = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        firebaseRecyclerview.setAdapter(firebaseAdapter);
        firebaseRecyclerview.setLayoutManager(layoutManager);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            Log.e(TAG, "init: " + firebaseUser.getEmail() );
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


    public void goToRemote(View view) {
        Intent intent = new Intent(RemoteSelectionFromFirebase.this,ControllerActivity.class);
        intent.putExtra(DeviceListActivity.DEVICE_EXTRA,deviceAddress);
        startActivity(intent);
    }
}
