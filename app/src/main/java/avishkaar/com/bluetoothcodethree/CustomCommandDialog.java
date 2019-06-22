package avishkaar.com.bluetoothcodethree;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;



public class CustomCommandDialog extends DialogFragment {
    CardView done;
    EditText buttonPress,buttonRelease;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;
    int flag;
    interface dataFromDialog{
        void triggerChange();
    }
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
        setTextViews();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==1)
                {
                    edit.putString(Constants.button1Pressed,buttonPress.getText().toString()).apply();
                    edit.putString(Constants.button1Released,buttonRelease.getText().toString()).apply();
                }
                else if (flag ==2)
                {
                    edit.putString(Constants.button2Pressed,buttonPress.getText().toString()).apply();
                    edit.putString(Constants.button2Released,buttonRelease.getText().toString()).apply();
                }
                else if (flag==3)
                {
                    edit.putString(Constants.button4Pressed,buttonPress.getText().toString()).apply();
                    edit.putString(Constants.button4Released,buttonRelease.getText().toString()).apply();
                }
                else if(flag ==4)
                {
                    edit.putString(Constants.button3Pressed,buttonPress.getText().toString()).apply();
                    edit.putString(Constants.button3Released,buttonRelease.getText().toString()).apply();
                }
                refer.triggerChange();
                dismiss();
            }
        });

    }
    void setTextViews()
    {
        if(flag==1)
        {   buttonPress.setText(sharedPreferences.getString(Constants.button1Pressed,""));
            buttonRelease.setText(sharedPreferences.getString(Constants.button1Released,""));

        }
        else if (flag ==2)
        {   buttonPress.setText(sharedPreferences.getString(Constants.button2Pressed,""));
            buttonRelease.setText(sharedPreferences.getString(Constants.button2Released,""));

        }
        else if (flag==3)
        {   buttonPress.setText(sharedPreferences.getString(Constants.button4Pressed,""));
            buttonRelease.setText(sharedPreferences.getString(Constants.button4Released,""));

        }
        else if(flag ==4)
        {   buttonRelease.setText(sharedPreferences.getString(Constants.button3Released,""));
            buttonPress.setText(sharedPreferences.getString(Constants.button3Pressed,""));

        }
    }
}
