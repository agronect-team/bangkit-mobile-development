package com.example.agronect.data.response

import com.google.gson.annotations.SerializedName

data class CornResponse(

	@field:SerializedName("solution")
	val solution: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("confidence")
	val confidence: Any? = null,

	@field:SerializedName("prediction")
	val prediction: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("plant_name")
	val plantName: String? = null
)
