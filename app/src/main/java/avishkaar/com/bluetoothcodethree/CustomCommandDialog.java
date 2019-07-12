package avishkaar.com.bluetoothcodethree;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;



public class CustomCommandDialog extends DialogFragment {
    CardView done,identifier;
    EditText buttonPress,buttonRelease;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;
    int flag;
    dataFromDialog refer;

   @SuppressLint("CommitPrefEdits")
   void sharedPrefPass(SharedPreferences sharedPreferences, int flag){
       this.sharedPreferences = sharedPreferences;
       this.flag = flag;
       edit =  sharedPreferences.edit();
   }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.edit_dialog_layout,container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            refer = (dataFromDialog) getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();

        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        done = view.findViewById(R.id.done);
        buttonPress = view.findViewById(R.id.buttonPress);
        buttonRelease = view.findViewById(R.id.buttonRelease);
        identifier = view.findViewById(R.id.identifier);

        setTextViews();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==1)
                {
                    edit.putString(Constants.bluePressed,buttonPress.getText().toString()).apply();
                    edit.putString(Constants.blueRelease,buttonRelease.getText().toString()).apply();

                }
                else if (flag ==2)
                {
                    edit.putString(Constants.orangePressed,buttonPress.getText().toString()).apply();
                    edit.putString(Constants.orangeRelease,buttonRelease.getText().toString()).apply();
                }
                else if (flag==3)
                {
                    edit.putString(Constants.yellowPress,buttonPress.getText().toString()).apply();
                    edit.putString(Constants.yellowReleased,buttonRelease.getText().toString()).apply();
                }
                else if(flag ==4)
                {
                    edit.putString(Constants.redPressed,buttonPress.getText().toString()).apply();
                    edit.putString(Constants.redReleased,buttonRelease.getText().toString()).apply();
                }
                refer.triggerChange();
                dismiss();
            }
        });

    }

    void setTextViews()
    {
        if(flag==1)
        {   buttonPress.setText(sharedPreferences.getString(Constants.bluePressed,""));
            buttonRelease.setText(sharedPreferences.getString(Constants.blueRelease,""));
            identifier.setCardBackgroundColor(Color.parseColor("#0064ab"));

        }
        else if (flag ==2)
        {   buttonPress.setText(sharedPreferences.getString(Constants.orangePressed,""));
            buttonRelease.setText(sharedPreferences.getString(Constants.orangeRelease,""));
            identifier.setCardBackgroundColor(Color.parseColor("#ff6100"));
        }
        else if (flag==3)
        {   buttonPress.setText(sharedPreferences.getString(Constants.yellowPress,""));
            buttonRelease.setText(sharedPreferences.getString(Constants.yellowReleased,""));
            identifier.setCardBackgroundColor(Color.parseColor("#ffaa00"));

        }
        else if(flag ==4)
        {   buttonRelease.setText(sharedPreferences.getString(Constants.redReleased,""));
            buttonPress.setText(sharedPreferences.getString(Constants.redPressed,""));
            identifier.setCardBackgroundColor(Color.parseColor("#fc0014"));

        }
    }

    interface dataFromDialog {
        void triggerChange();
    }
}
