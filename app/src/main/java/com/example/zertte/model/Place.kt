package com.example.zertte.model

class PlaceData {
    var placeName: String? = null
    var placeInfo: String? = null
    var placeImg: String? = null
    constructor(){}

    constructor(
        placeName:String?,
        placeInfo:String?,
        placeImg:String?
    ){
        this.placeName = placeName
        this.placeInfo = placeInfo
        this.placeImg = placeImg
    }
}