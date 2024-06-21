package com.example.agronect.data.response

import com.google.gson.annotations.SerializedName

data class HistoryResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("dataHistoryUser")
	val dataHistory: List<DataHistoryItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataHistoryItem(

	@field:SerializedName("image")
	val image: Image? = null,

	@field:SerializedName("solution")
	val solution: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("id_pred")
	val idPred: Int? = null,

	@field:SerializedName("confidence")
	val confidence: Any? = null,

	@field:SerializedName("prediction")
	val prediction: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("plant_name")
	val plantName: String? = null
)

data class Image(

	@field:SerializedName("data")
	val data: List<Int?>? = null,

	@field:SerializedName("type")
	val type: String? = null
)
