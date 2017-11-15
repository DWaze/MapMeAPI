package com.dredhat.lhadj.mapmeapi;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mapsforge.core.model.LatLong;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lhadj on 3/5/2016.
 */
public class GetJsonRouteData extends GetRoutData {

    List<LatLong> list;
    String DistanceStr;
    String DurationStr;
    private String LOG_TAG=GetJsonRouteData.class.getSimpleName();


    public GetJsonRouteData() {
    }

    /*Fetching teh distance and the duration from the JSON Google server result*/


    public List<LatLong> getSteps() {
        return list;
    }

    public String getDistanceStr() {
        return DistanceStr;
    }

    public String getDurationStr() {
        return DurationStr;
    }

    public void parseData (){

        try {
            JSONObject obj = new JSONObject(getData());
            JSONArray roots = obj.getJSONArray("routes");
            JSONObject step = roots.getJSONObject(0);
            JSONObject overviewPolylines = step
                    .getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            list = decodePoly(encodedString);

            JSONArray legs = step.getJSONArray("legs");
            JSONObject dist = legs.getJSONObject(0);
            JSONObject Distance = dist
                    .getJSONObject("distance");
            JSONObject Duration = dist
                    .getJSONObject("duration");
            DistanceStr = Distance.getString("text");
            DurationStr = Duration.getString("text");

        }catch (Exception e){
            return;
        }
    }


    private List<LatLong> decodePoly(String encoded) {

        List<LatLong> poly = new ArrayList<LatLong>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLong p = new LatLong((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


   public class getData extends DownloadData {

        @Override
        protected void onPostExecute(String Rinformation) {
            super.onPostExecute(Rinformation);
            parseData();
        }

        @Override
        protected String doInBackground(String... params) {
            return super.doInBackground(params);
        }
    }
}
