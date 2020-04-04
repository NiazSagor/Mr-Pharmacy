package com.angik.android.mrpharmacy.Classes;

public class Upload {
    private String mName;
    private String mDetails;
    private String mImageUrl;
    private String mUserName;
    private String mUserAddress;
    private String mUserPhone;
    private String mStatus;
    private String mReached;


    public Upload() {
        //empty constructor needed
    }

    public Upload(String name, String details, String imageUrl, String userName, String userPhone, String userAddress, String status, String reached) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mDetails = details;
        mImageUrl = imageUrl;
        mUserName = userName;
        mUserPhone = userPhone;
        mUserAddress = userAddress;

        mStatus = status;
        mReached = reached;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDetails()
    {
        return mDetails;
    }

    public void setDetails(String details)
    {
        mDetails = details;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getUserPhone() {
        return mUserPhone;
    }

    public void setUserPhone(String userPhone) {
        mUserPhone = userPhone;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmUserAddress() {
        return mUserAddress;
    }

    public void setmUserAddress(String mUserAddress) {
        this.mUserAddress = mUserAddress;
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
