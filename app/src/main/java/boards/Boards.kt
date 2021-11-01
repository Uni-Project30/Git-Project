package boards

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Boards(

    @SerializedName("name")
    val title:String,

    @SerializedName("created by")
    val release:String,

    @SerializedName("about")
    val overview:String

    ): Parcelable