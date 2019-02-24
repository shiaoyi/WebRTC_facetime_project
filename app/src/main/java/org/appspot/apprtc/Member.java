package org.appspot.apprtc;

/**
 * Created by hsushiaoyi on 2018/3/6.
 */

public class Member { // VO- Value Object
    private String id;
    private String image;
    private String name;
    private String experience;
    private int numComment;
    private int numService;
    private double score;
    private String language;

    public Member() {
        super();
    }

    public Member(String id, String image, String name, double score, String experience, int numService, int numComment) {
        super();
        this.id = id;
        this.image = image;
        this.name = name;
        this.score = score;
        this.experience = experience;
        this.numService = numService;
        this.numComment = numComment;
    }

    public Member(String id, String image, String name, String language, double score, int numService, int numComment) {
        super();
        this.id = id;
        this.image = image;
        this.name = name;
        this.score = score;
        this.language = language;
        this.numService = numService;
        this.numComment = numComment;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getNumComment() {
        return numComment;
    }

    public void setNumComment(int numComment) {
        this.numComment = numComment;
    }

    public int getNumService() {
        return numService;
    }

    public void setNumService(int numService) {
        this.numService = numService;
    }



}
