package com.mycompany.sip;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Emily Fletcher on 8/30/2017.
 * Model Object for Sites
 */

public class Site implements Parcelable {

    private String name;
    private String number;
    private String dateOpened;
    private String description;
    private String id;
    private LatLng datum;
    private Bundle roles = new Bundle();
    private Bundle crew = new Bundle();

    public Site(String i, String n, String nu, String desc, String date, double latude, double lotude, HashMap<String, ArrayList> r)
    {
        name = n;
        id = i;
        number = nu;
        description = desc;
        dateOpened = date;
        datum = new LatLng(latude, lotude);
        if(r!= null) {
            addRoles(r);
        }
    }

    public Site(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.number = in.readString();
        this.dateOpened = in.readString();
        this.description = in.readString();
        this.datum = new LatLng(in.readDouble(), in.readDouble());
        this.roles = in.readBundle();
        this.crew = in.readBundle();
    }

    public String getName()
    {
        return name;
    }

    public String getID()
    {
        return id;
    }

    public String getNumber()
    {
        return number;
    }

    public String getDateOpened()
    {
        return dateOpened;
    }

    public String getDescription()
    {
        return description;
    }

    public LatLng getDatum() { return datum; }

    public Bundle getRoles() { return roles; }

    public void addRoles(HashMap<String, ArrayList> r)
    {
        Object[] keys = r.keySet().toArray();
        for(int i=0; i<r.size(); i++)
        {
            roles.putStringArrayList(keys[i].toString(), r.get(keys[i]));
        }
    }

    public void addCrewMember(String id, String name, String email)
    {
        crew.putString(id + "NAME", name);
        crew.putString(id + "EMAIL", email);
    }

    public Bundle getCrew()
    {
        return crew;
    }

    public boolean userIsOneOfRoles(String userID, ArrayList<String> r)
    {
        boolean flag = false;

        for(int i = 0; i<r.size(); i++)
        {
            flag = flag || roles.getStringArrayList(userID).indexOf(r.get(i)) > -1;
        }

        return flag;
    }

    public boolean userIsUnitExcavator(String userID, String unitID)
    {
        boolean flag = false;
        flag = flag || roles.getStringArrayList(userID).indexOf(unitID) > -1;
        return flag;
    }

    @Override
    public String toString()
    {
        return number + " " + name;
    }

    public void setName(String n)
    {
        name=n;
    }

    public void setNumber(String n)
    {
        number=n;
    }

    public void setDateOpened(String d)
    {
        dateOpened=d;
    }

    public void setDescription(String des)
    {
        description=des;
    }

    public void setDatum(double la, double lo)
    {
        datum = new LatLng(la, lo);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(number);
        dest.writeString(dateOpened);
        dest.writeString(description);
        dest.writeDouble(datum.latitude);
        dest.writeDouble(datum.longitude);
        dest.writeBundle(roles);
        dest.writeBundle(crew);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Site> CREATOR = new Parcelable.Creator<Site>() {

        public Site createFromParcel(Parcel in) {
            return new Site(in);
        }

        public Site[] newArray(int size) {
            return new Site[size];
        }
    };

    @Override
    public boolean equals(Object o)
    {
        try {
            String id = ((Site) o).getID();
            Boolean same = this.getID().equals(id);
            return same;
            //String num = ((Site) o).getNumber();
            //return this.getNumber().equals(num);
        }catch(Exception e)
        {
            return false;
        }
    }
}
