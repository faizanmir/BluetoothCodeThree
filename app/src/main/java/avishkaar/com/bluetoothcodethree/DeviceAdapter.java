package avishkaar.com.bluetoothcodethree;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
   private ArrayList <BluetoothDevice> bluetoothDeviceArrayList;
    private bluetoothInterface bluetoothInterface;
    public DeviceAdapter(ArrayList<BluetoothDevice> bluetoothDeviceArrayList,bluetoothInterface bluetoothInterface) {
        this.bluetoothDeviceArrayList = bluetoothDeviceArrayList;
        this.bluetoothInterface = bluetoothInterface;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DeviceViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_layout,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DeviceViewHolder deviceViewHolder, int i) {
            deviceViewHolder.deviceName.setText( bluetoothDeviceArrayList.get(i).getName());
            deviceViewHolder.deviceAddress.setText(bluetoothDeviceArrayList.get(i).getAddress());
            deviceViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothInterface.bluetoothAddress(bluetoothDeviceArrayList
                            .get(deviceViewHolder.getAdapterPosition())
                            .getName(),bluetoothDeviceArrayList
                            .get(deviceViewHolder.getAdapterPosition())
                            .getAddress(),bluetoothDeviceArrayList
                            .get(deviceViewHolder.getAdapterPosition()));
                    //Pass device to ListActivity
                }
            });
    }

    @Override
    public int getItemCount() {
        return bluetoothDeviceArrayList.size();
    }

    class  DeviceViewHolder extends RecyclerView.ViewHolder{
        TextView deviceAddress;
        TextView deviceName;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceAddress = itemView.findViewById(R.id.deviceAddress);
            deviceName = itemView.findViewById(R.id.deviceName);

        }
    }
}
