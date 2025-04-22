package com.example.ecommerce_app.model;

public class DeliveryPerson extends User{

    private String phone;
    private String area;

    public DeliveryPerson() {

    }

    public DeliveryPerson(int id, String username, String area, String phone) {
        super(id, username);
        this.phone = phone;
        this.area = area;
    }

    public DeliveryPerson(int id, String username, String password, String role, String phone, String area) {
        super(id, username, password, role);
        this.phone = phone;
        this.area = area;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
