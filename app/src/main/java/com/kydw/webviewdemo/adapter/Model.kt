package com.kydw.webviewdemo.adapter

import android.os.Parcel
import android.os.Parcelable

const val SITE = 0
const val KEYWORD_SITE = 1

data class Model(
    var keyword: String?,
    var site: String?,
    val type: Int = KEYWORD_SITE
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(keyword)
        parcel.writeString(site)
        parcel.writeInt(type)
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


