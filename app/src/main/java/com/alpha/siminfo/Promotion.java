package com.alpha.siminfo;

/**
 * Created by I Love Coding on 1/7/2015.
 */
public class Promotion {
    private String name;
    private String desNumber;
    private String messageRegister;
    private String messageCheck;
    private String messageCancel;
    private String description;

    public Promotion() {
    }

    public Promotion(String name, String desNumber, String messageRegister, String messageCheck, String messageCancel, String description) {
        this.name = name;
        this.desNumber = desNumber;
        this.messageRegister = messageRegister;
        this.messageCheck = messageCheck;
        this.messageCancel = messageCancel;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesNumber() {
        return desNumber;
    }

    public void setDesNumber(String desNumber) {
        this.desNumber = desNumber;
    }

    public String getMessageRegister() {
        return messageRegister;
    }

    public void setMessageRegister(String messageRegister) {
        this.messageRegister = messageRegister;
    }

    public String getMessageCheck() {
        return messageCheck;
    }

    public void setMessageCheck(String messageCheck) {
        this.messageCheck = messageCheck;
    }

    public String getMessageCancel() {
        return messageCancel;
    }

    public void setMessageCancel(String messageCancel) {
        this.messageCancel = messageCancel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
