package com.rm.darya.util.view;

/**
 * Created by alex
 */

import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ImageView;

import java.lang.reflect.Field;

/**
 * Created by ex3ndr on 25.09.14.
 */
public class SearchViewHacker {

    private static View findView(View root, String name) {
        try {
            Field field = root.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return (View) field.get(root);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }
    public static void setCloseIcon(SearchView searchView, int res) {
        ImageView searchImageView = (ImageView) findView(searchView, "mCloseButton");
        searchImageView.setVisibility(View.VISIBLE);
        searchImageView.setAdjustViewBounds(false);
        searchImageView.setImageResource(res);
    }
    public static void disableCloseButton(SearchView searchView){
        ImageView searchImageView = (ImageView) findView(searchView, "mCloseButton");
        // searchImageView.setMaxWidth(0);
        searchImageView.setVisibility(View.GONE);
        searchImageView.setImageBitmap(null);
        searchImageView.setAdjustViewBounds(true);
    }


}