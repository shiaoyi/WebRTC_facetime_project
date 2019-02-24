package org.appspot.apprtc;

/**
 * Created by hsushiaoyi on 2018/5/6.
 */

public class Receipt {
    private String time;
    private String id;
    private String situate;
    private String dollar;
    private String payday;

    public Receipt() {
        super();
    }

    public Receipt(String time, String id, String situate, String dollar,String payday) {
        super();
        this.time = time;
        this.id = id;
        this.situate = situate;
        this.dollar = dollar;
        this.payday = payday;

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() { return id; }

    public void setId(String id) {
        this.id = id;
    }

    public String getSituate() { return situate; }

    public void setSituate(String situate) { this.situate = situate; }

    public String getDollar(){ return dollar; }

    public void setDollar( String dollar){ this.dollar = dollar; }

    public String getPayday() {
        return payday;
    }

    public void setPayday(String payday) {
        this.payday = payday;
    }

}
