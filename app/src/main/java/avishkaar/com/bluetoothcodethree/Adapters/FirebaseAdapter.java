package avishkaar.com.bluetoothcodethree.Adapters;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Objects;

import avishkaar.com.bluetoothcodethree.Interfaces.dataPassToSelectionActivity;
import avishkaar.com.bluetoothcodethree.ModelClasses.RemoteModelClass;
import avishkaar.com.bluetoothcodethree.R;

public class FirebaseAdapter extends  RecyclerView.Adapter<FirebaseAdapter.FirebaseViewHolder>{
    private ArrayList<RemoteModelClass> remoteModelClassArrayList;
    private dataPassToSelectionActivity ref;
    private DatabaseReference firebaseDatabase;

    public FirebaseAdapter(ArrayList<RemoteModelClass> remoteModelClassArrayList, dataPassToSelectionActivity ref, DatabaseReference firebaseDatabase) {
        this.remoteModelClassArrayList = remoteModelClassArrayList;
        this.ref=ref;
        this.firebaseDatabase = firebaseDatabase;
    }

    @NonNull
    @Override
    public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new FirebaseViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.remote_from_firebase,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final FirebaseViewHolder firebaseViewHolder, final int i) {
        try {
            firebaseViewHolder.textView.setText(remoteModelClassArrayList.get(i).getConfig().getRemoteName());
        } catch (NullPointerException ignored) {

        }
        firebaseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.passDataToSelectionActivity(remoteModelClassArrayList.get(firebaseViewHolder.getAdapterPosition()));
            }
        });
        firebaseViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    new AlertDialog.Builder(firebaseViewHolder.itemView.getContext()).setMessage("Do you want to delete this configuration?").setTitle("Delete Configuration").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().length() > 0) {

                                firebaseDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(remoteModelClassArrayList.get(firebaseViewHolder.getAdapterPosition()).getConfig().getRemoteName()).removeValue();
                                remoteModelClassArrayList.remove(remoteModelClassArrayList.remove(firebaseViewHolder.getAdapterPosition()));
                                notifyDataSetChanged();
                            }
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return remoteModelClassArrayList.size();
    }

    class FirebaseViewHolder extends RecyclerView.ViewHolder{
        TextView textView ;

        FirebaseViewHolder(@NonNull View itemView) {
            super(itemView);
            textView =  itemView.findViewById(R.id.nameFromFirebase);
        }
    }
}
