package com.lopez.julz.crmcrewhub.classes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

public class AlertHelpers {

    public static void showInfoDialog(Context context, String title, String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle(title);
            builder.setMessage(message);

            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showExitableInfoDialog(Activity activity, Context context, String title, String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle(title);
            builder.setMessage(message);

            builder.setCancelable(false);

            builder.setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    activity.finish();
                }
            });

            AlertDialog dialog = builder.create();

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
