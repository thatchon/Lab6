package com.example.lab6.pojo;

import java.util.ArrayList;
import java.util.List;

public class Wizards {
    private List<Wizard> wizards;

    public Wizards() {

        this.wizards = new ArrayList<Wizard>();
    }

    public List<Wizard> getWizards() {

        return wizards;
    }

    public void setWizards(List<Wizard> wizards) {

        this.wizards = wizards;
    }
}