package com.eventbus.demo.eventBus;


import java.io.Serializable;

/**
 * 自定义的事件类
 */
public class Firends implements Serializable {
    private String name;
    private String password;

    public Firends(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Firends{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
