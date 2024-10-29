package com.ssspvtltd.quick_app.utils

import com.ssspvtltd.quick_app.model.order.add.additem.PackType
import com.ssspvtltd.quick_app.model.order.add.additem.PackTypeItem

object Mocker {
    fun getPackDataList(): List<PackType> {
        return listOf(
            PackType(
                "1", "Bora","1", "100.00",
                 itemDetail = listOf(
                    PackTypeItem("Shirt 1", "Shirt 1", "10"),
                    PackTypeItem("Jeans 1", "Jeans 1", "10")
                )
            ),
            // PackType(
            //     "2", "Box", "150.00",
            //     items = listOf(
            //         PackTypeItem("Shirt 2", "Shirt 2", "10"),
            //         PackTypeItem("Jeans 2", "Jeans 2", "10")
            //     )
            // ),
            // PackType(
            //     "3", "Peti", "198.00",
            //     items = listOf(
            //         PackTypeItem("Shirt 3", "Shirt 3", "10"),
            //         PackTypeItem("Jeans 3", "Jeans 3", "10")
            //     )
            // ),
        )
    }
}