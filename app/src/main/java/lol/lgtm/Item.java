package lol.lgtm;

import android.graphics.Bitmap;

/**
 * Created by dongri on 2017/11/26.
 */

public class Item {

    private String url;

    private int id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl(){
        return this.url;
    }

    public void setUrl(String url){
        this.url = url;
    }
}