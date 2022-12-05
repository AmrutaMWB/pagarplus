package com.pagarplus.app.network.models.userdashboard

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserBannerResponseItem(

	@field:SerializedName("ImageName")
	val imageName: String? = null,

	@field:SerializedName("CreatedBy")
	val createdBy: Int? = null,

	@field:SerializedName("CreatedDate")
	val createdDate: String? = null,

	@field:SerializedName("BannerImage")
	val imageURL: String? = null,

	@field:SerializedName("PromoType")
	val promoType: String? = null,

	@field:SerializedName("ID")
	val iD: Int? = null,

	@field:SerializedName("PromoText")
	val promoText: Any? = null,

	@field:SerializedName("ModifiedBy")
	val modifiedBy: Int? = null,

	@field:SerializedName("ModifiedDate")
	val modifiedDate: String? = null
):Serializable
