package com.example.mert.inspirationrewards;

public class Leaderboard implements Comparable<Leaderboard> {

    public String Name;
    public String PositionAndDept;
    public String Image;
    public String Points;

    public Leaderboard(String name, String PositionAndDept, String image, String points) {
        this.Name = name;
        this.PositionAndDept = PositionAndDept;
        this.Image = image;
        this.Points = points;
    }

    public String getNames() {
        return Name;
    }

    public void setNames(String name) {
        this.Name = name;
    }

    public String getPositionAndDept() {
        return PositionAndDept;
    }

    public void setPositionAndDept(String PositionAndDept) {
        this.PositionAndDept = PositionAndDept;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        this.Image = image;
    }

    public String getPoints() {
        return Points;
    }

    public void setPoints(String points) {
        this.Points = points;
    }

    @Override
    public int compareTo(Leaderboard o) {
        return getPoints().compareTo(o.getPoints());
    }
}