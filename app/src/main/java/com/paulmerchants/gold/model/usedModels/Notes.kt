package com.paulmerchants.gold.model.usedModels

import com.google.gson.annotations.SerializedName

data class Notes(
    @SerializedName("notes_key_1") val notes_key_1: String,
    @SerializedName("notes_key_2") val notes_key_2: String,
)
