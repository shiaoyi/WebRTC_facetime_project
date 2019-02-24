package org.appspot.apprtc;

import java.text.SimpleDateFormat;

/**
 * Created by hsushiaoyi on 2018/5/6.
 */

public class Record {
    private int image;
    private String name;
    private String dollar;
    private String time;
    private String date;
    private String lang;

    public Record() {
        super();
    }

    public Record(String name, String date, String time, String dollar,String lang) {
        super();
        this.lang = lang;
        this.date = date;
        this.name = name;
        this.dollar = dollar;
        this.time = time;

    }

    public Record(int image, String name, String date,String lang) {
        super();
        this.lang = lang;
        this.date = date;
        this.image = image;
        this.name = name;
    }


    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getLang() { return lang; }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getDate() {

//        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//        String date=sdf.format(new java.util.Date());
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDollar(){ return dollar; }

    public void setDollar( String dollar){ this.dollar = dollar; }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
