package com.kydw.webviewdemo.adapter

import android.os.Parcel
import android.os.Parcelable


data class Model(
    var keyword: String?,
    var sites: String?

    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()) {
    }

    override fun toString(): String {
        return "Model(keyword='$keyword', sites='$sites')"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(keyword)
        parcel.writeString(sites)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Model> {
        override fun createFromParcel(parcel: Parcel): Model {
            return Model(parcel)
        }

        override fun newArray(size: Int): Array<Model?> {
            return arrayOfNulls(size)
        }
    }
}