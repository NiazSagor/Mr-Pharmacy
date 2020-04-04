package com.angik.android.mrpharmacy.Classes;

import java.util.ArrayList;

public class UploadCart {
    private ArrayList<String> mName;
    private ArrayList<String> mBrand;
    private ArrayList<String> mQuantity;
    private ArrayList<String> mType;
    private ArrayList<String> mUserDetails;
    private String mStatus;
    private String mReached;

    public UploadCart() {

    }

    public UploadCart(ArrayList<String> name, ArrayList<String> quantity, ArrayList<String> brand, ArrayList<String> type
            , ArrayList<String> userDetails, String status, String reached) {

        mName = name;
        mQuantity = quantity;
        mBrand = brand;
        mType = type;

        mUserDetails = userDetails;
        mStatus = status;
        mReached = reached;
    }

    public ArrayList<String> getmName() {
        return mName;
    }

    public void setmName(ArrayList<String> mName) {
        this.mName = mName;
    }

    public ArrayList<String> getmBrand() {
        return mBrand;
    }

    public void setmBrand(ArrayList<String> mBrand) {
        this.mBrand = mBrand;
    }

    public ArrayList<String> getmQuantity() {
        return mQuantity;
    }

    public void setmQuantity(ArrayList<String> mQuantity) {
        this.mQuantity = mQuantity;
    }

    public ArrayList<String> getmType() {
        return mType;
    }

    public void setmType(ArrayList<String> mType) {
        this.mType = mType;
    }

    public ArrayList<String> getmUserDetails() {
        return mUserDetails;
    }

    public void setmUserDetails(ArrayList<String> mUserDetails) {
        this.mUserDetails = mUserDetails;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmReached() {
        return mReached;
    }

    public void setmReached(String mReached) {
        this.mReached = mReached;
    }
}
