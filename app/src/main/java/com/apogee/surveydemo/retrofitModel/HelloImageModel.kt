package com.apogee.surveydemo.retrofitModel

class HelloImageModel (
        val service_no : String,
        val survey_type : String,
        val survey_date : String,
        val image : ArrayList<ImageModel>

)

class ImageModel(
        val byte_arr : String,
        val imgname : String,
        val type : String
)