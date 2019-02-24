package org.appspot.apprtc;

import java.text.SimpleDateFormat;

/**
 * Created by hsushiaoyi on 2018/5/6.
 */

public class Precord {
    private String name;
    private String dollar;
    private String time;
    private String date;

    public Precord() {
        super();
    }

    public Precord( String name, String date, String time, String dollar) {
        super();
        this.date = date;
        this.name = name;
        this.dollar = dollar;
        this.time = time;

    }

    public Precord( String name, String date,String time) {
        super();
        this.time = time;
        this.date = date;
        this.name = name;

    }


    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
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
