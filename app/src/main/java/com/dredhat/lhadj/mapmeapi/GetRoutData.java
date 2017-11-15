package com.dredhat.lhadj.mapmeapi;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lhadj on 3/5/2016.
 */
  public class GetRoutData {
    private String Data ;
    private String LOG_TAG=GetRoutData.class.getSimpleName();


    public String getData() {
        return Data;
    }



    public class DownloadData extends AsyncTask<String,Void,String> {
        HttpURLConnection conn = null ;
        BufferedReader reader = null ;
        @Override
        protected void onPostExecute(String Rinformation) {
            Data = Rinformation ;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                InputStream inputStream = conn.getInputStream();

                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line ;

                while((line = reader.readLine())!=null){
                    buffer.append(line+"\n");
                }
                String res = buffer.toString();
                return res;

            }catch (Exception e){
                e.printStackTrace();
                Log.e(LOG_TAG,"Error Dowloading Json Data");
                return null;

            }finally {
                if (conn != null){
                    conn.disconnect();
                }
                if (reader != null){
                    try {
                        reader.close();
                    }
                    catch (final IOException e){
                        Log.e(LOG_TAG, "Error closing Stream", e);
                    }
                }
        }
    }

    }
}
