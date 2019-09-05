
package com.hfad.smstest;

public class DataModel {

    public String name;
    boolean checked;

    DataModel(String name, boolean checked) {
        this.name = name;
        this.checked = checked;

    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DataModel) {
            return ((DataModel) obj).name == name;
        }
        return false;
    }
    /*@Override
    public int hashCode() {
        return this.name;
    }*/
}
