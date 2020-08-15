package com.bapplications.maplemobile.views;


import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.bapplications.maplemobile.R;

public class ChangeMapPopup {

    //PopupWindow display method
    View popupView;
    PopupWindow popupWindow;

    public void showPopupWindow(final View view) {


        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_change_map, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        Button buttonEdit = popupView.findViewById(R.id.popup_change_map_bt);
        buttonEdit.setOnClickListener(onClickListener);
    }

    public int getMapId() {
        return Integer.parseInt(((EditText)popupView.findViewById(R.id.popup_change_map_et)).getText().toString());
    }

    public void dismiss() {
        popupWindow.dismiss();
    }
}
