package com.unison.appartment.model;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta un achievement da sbloccare
 */
public class Achievement implements Serializable {

    private String name;
    private String description;
    private Uri image;

    private String unlockAttribute;
    private String unlockOperator;
    private int unlockValue;


    public Achievement(String name, String description/*, Uri image*/, String unlockAttribute, String unlockOperator, int unlockValue) {
        this.name = name;
        this.description = description;
        /*this.image = image;*/
        this.unlockAttribute = unlockAttribute;
        this.unlockOperator = unlockOperator;
        this.unlockValue = unlockValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public String getUnlockAttribute() {
        return unlockAttribute;
    }

    public void setUnlockAttribute(String unlockAttribute) {
        this.unlockAttribute = unlockAttribute;
    }

    public String getUnlockOperator() {
        return unlockOperator;
    }

    public void setUnlockOperator(String unlockOperator) {
        this.unlockOperator = unlockOperator;
    }

    public int getUnlockValue() {
        return unlockValue;
    }

    public void setUnlockValue(int unlockValue) {
        this.unlockValue = unlockValue;
    }

    //TODO da rimouvere
    public static final List<Achievement> achievementList = new ArrayList<Achievement>(){
        {
            /*add(new Achievement("Re del giardino", "Tagliare l'erba 10 volte"));
            add(new Achievement("Imparatore del bucato", "Fare il bucato 20 volte"));
            add(new Achievement("Lady dei piatti", "Lavare i piatti 10 volte di fila"));
            add(new Achievement("Re del giardino2", "Tagliare l'erba 10 volte"));
            add(new Achievement("Imparatore del bucato2", "Fare il bucato 20 volte"));
            add(new Achievement("Lady dei piatti2", "Lavare i piatti 10 volte di fila"));*/
        }
    };

}
