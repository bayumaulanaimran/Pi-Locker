package com.pilockerstable;

import android.os.AsyncTask;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by MY PC on 21/02/2018.
 */

public class TryInNSecondsTask extends AsyncTask<Integer,Integer,String> {

    WeakReference<DotsPatternActivity> rDPWReference;

    int count;

    public TryInNSecondsTask(DotsPatternActivity dotsPatternActivity){
        rDPWReference = new WeakReference<DotsPatternActivity>(dotsPatternActivity);
    }

    @Override
    protected String doInBackground(Integer... integers) {
        // Loop through the task
        for(int i = integers[0];i >= 0;i--){

            // Publish the async task progress
            publishProgress(i);

            // Sleep the thread for 1 second
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        return "OK";
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        super.onProgressUpdate(values);

        if(values[0]==1||values[0]==0){
            rDPWReference.get().textView.setText(String.valueOf(values[0]).concat(" Second to Try Again"));
        }else{
            rDPWReference.get().textView.setText(String.valueOf(values[0]).concat(" Seconds to Try Again"));
        }

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        rDPWReference.get().textView.setText("Select Dots");

        rDPWReference.get().confirm.setEnabled(true);
        rDPWReference.get().confirm.setVisibility(View.VISIBLE);

        rDPWReference.get().gridView.setClickable(true);
        rDPWReference.get().gridView.setEnabled(true);
        rDPWReference.get().gridView.setVisibility(View.VISIBLE);

    }
}
