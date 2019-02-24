package org.appspot.apprtc;

import java.io.Serializable;

/**
 * Created by hsushiaoyi on 2018/5/30.
 */

public class Call implements Serializable {


    private String image;
    private String name;

    public Call() {
        super();
    }

    public Call(String image, String name) {

        super();
        this.image = image;
        this.name = name;

    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}