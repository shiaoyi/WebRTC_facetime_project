package org.appspot.apprtc;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Created by hsushiaoyi on 2018/5/3.
 */

public class Schedule implements Serializable{


        private String image;
        private String name;
        private String comments;
        private String startTime;
        private String endTime;
        private String date;

        public Schedule() {
            super();
        }

        public Schedule(String image, String name, String comments,String date,String startTime,String endTime) {

            super();
            this.startTime = startTime;
            this.endTime = endTime;
            this.date = date;
            this.image = image;
            this.name = name;
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

        public String getStartTime() { return startTime; }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() { return endTime; }

        public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public String getDate() {
        return date;
    }

        public void setDate(String date) {
        this.date = date;
    }




}
