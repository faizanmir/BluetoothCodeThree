package avishkaar.com.bluetoothcodethree.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import avishkaar.com.bluetoothcodethree.Interfaces.dataPassToSelectionActivity;
import avishkaar.com.bluetoothcodethree.ModelClasses.RemoteModelClass;
import avishkaar.com.bluetoothcodethree.R;

public class FirebaseAdapter extends  RecyclerView.Adapter<FirebaseAdapter.FirebaseViewHolder>{
    ArrayList<RemoteModelClass> remoteModelClassArrayList;
    dataPassToSelectionActivity ref;
    public FirebaseAdapter(ArrayList<RemoteModelClass> remoteModelClassArrayList,dataPassToSelectionActivity ref) {
        this.remoteModelClassArrayList = remoteModelClassArrayList;
        this.ref=ref;
    }

    @NonNull
    @Override
    public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new FirebaseViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.remote_from_firebase,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FirebaseViewHolder firebaseViewHolder, final int i) {
        firebaseViewHolder.textView.setText(remoteModelClassArrayList.get(i).getConfig().getRemoteName());
        firebaseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.passDataToSelectionActivity(remoteModelClassArrayList.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return remoteModelClassArrayList.size();
    }

    class FirebaseViewHolder extends RecyclerView.ViewHolder{
        TextView textView ;
        public FirebaseViewHolder(@NonNull View itemView) {
            super(itemView);
            textView =  itemView.findViewById(R.id.nameFromFirebase);
        }
    }
}
