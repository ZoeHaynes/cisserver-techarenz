package edu.cis.Model;

import java.util.ArrayList;

public class CISUser {
    private String userID;
    private String name;
    private String yearLevel;
    private ArrayList<Order> orders = new ArrayList<>();
    private double money;

    public CISUser(String userID, String name, String yearLevel){
        this.userID=userID;
        this.name=name;
        this.yearLevel=yearLevel;
        this.money=50;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order order, double price) {
        orders.add(order);
        money=money-price;
    }

    public void removeOrder(int index) {
        orders.remove(index);
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }


    @Override
    public String toString() {
        String ordersString;
        if (orders == null || orders.isEmpty()) {
            ordersString = "none";
        } else if (orders.size() == 1) {
            ordersString = orders.get(0).toString();
        } else {
            ordersString = orders.toString();
        }

        return "CISUser{" +
                "userID='" + userID + '\'' +
                ", name='" + name + '\'' +
                ", yearLevel='" + yearLevel + '\'' +
                ", orders= " + ordersString +
                ", money=" + money +
                '}';
    }
}
