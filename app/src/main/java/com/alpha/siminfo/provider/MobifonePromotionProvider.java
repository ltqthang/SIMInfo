package com.alpha.siminfo.provider;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.alpha.siminfo.Promotion;

/**
 * Created by I Love Coding on 1/9/2015.
 */
public class MobifonePromotionProvider {

    public static List<Promotion> createAllPromotion(Context context) {
        List<Promotion> promotions = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(loadJSONFromAsset(context));
            JSONArray promotionsArray = root.getJSONArray("promotions");
            for (int i = 0; i < promotionsArray.length(); i++) {
                JSONObject promotionObject = promotionsArray.getJSONObject(i);
                // Read fields from JSON Object
                String name = promotionObject.getString("name");
                String desNumber = "999";

                String reg = promotionObject.getString("name");

                String cancel = "HUY " + promotionObject.getString("name");
                String description = promotionObject.getString("des");
                promotions.add(new Promotion(name, desNumber, reg, null, cancel, description));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return promotions;
    }

    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("promotions_mobifone.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}
