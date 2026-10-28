package com.lab.myapplication.data

import com.lab.myapplication.search.SearchRes

fun SearchRes.toFavoriteEntity(): FavoriteEntity {
    return FavoriteEntity(
        id = "${latitude},${longitude}",
        name = name,
        latitude = latitude,
        longitude = longitude
    )
}

fun FavoriteEntity.toSearchRes(): SearchRes {
    return SearchRes(
        name = name,
        latitude = latitude,
        longitude = longitude
    )
}