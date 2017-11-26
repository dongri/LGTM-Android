package lol.lgtm;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by dongri on 2017/11/27.
 */

public class Loading {
    Context mContext;
    ProgressDialog mProgressDialog;

    public Loading(Context context){
        mContext = context;
        mProgressDialog = new ProgressDialog(context);
    }
    public void show(){
        mProgressDialog.show();
        mProgressDialog.setContentView(R.layout.loading);
        mProgressDialog.setCancelable(false);
    }
    public void close(){
        mProgressDialog.dismiss();
    }

}
