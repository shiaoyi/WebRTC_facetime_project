package org.appspot.apprtc;

import android.widget.RatingBar;

import java.text.SimpleDateFormat;

/**
 * Created by hsushiaoyi on 2018/5/1.
 */

public class Comment {
    private String image;
    private String name;
    private double score;
    private String comments;
    private String date;

    public Comment() {
        super();
    }

    public Comment(String image, String name, double score, String comments,String date) {
        super();
        this.date = date;
        this.image = image;
        this.name = name;
        this.score = score;
        this.comments = comments;

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

    public String getDate() {

//        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//        String date=sdf.format(new java.util.Date());
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getScore(){ return score; }

    public void setScore( double score){ this.score = score; }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}
