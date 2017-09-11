package com.zgh.rxretrofitdemo.retrofit;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by ZGH on 2017/9/11.
 */

public class LoadingDialog {
    private static ProgressDialog dialog;

    private static ProgressDialog getInstance(Context context) {
        if (dialog == null) {
            createDialog(context);
        }
        return dialog;
    }

    private static void createDialog(Context context) {
        dialog = new ProgressDialog(context,ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void show(Context context) {
        getInstance(context);
    }

    public static void cancel() {
        if (dialog != null) {
            dialog.cancel();
        }
    }
}
