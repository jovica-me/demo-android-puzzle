package me.jovica.myapplication


class Photo(
    val id: Int,
    val url: Int,
    val description: String = "Photo $id"
)

val photos = listOf(
    Photo(id = 1, url = R.drawable.android00),
    Photo(id = 2, url = R.drawable.android01),
    Photo(id = 3, url = R.drawable.android02),

    Photo(id = 11, url = R.drawable.android10),
    Photo(id = 12, url = R.drawable.android11),
    Photo(id = 13, url = R.drawable.android12),

    Photo(id = 21, url = R.drawable.android20),
    Photo(id = 22, url = R.drawable.android21),
    Photo(id = 23, url = R.drawable.android22),

    Photo(id = 31, url = R.drawable.android30),
    Photo(id = 32, url = R.drawable.android31),
    Photo(id = 33, url = R.drawable.android32),

//    Photo(id = 41, url = R.drawable.android00),
//    Photo(id = 42, url = R.drawable.android01),
//    Photo(id = 43, url = R.drawable.android02),
//
//    Photo(id = 51, url = R.drawable.android10),
//    Photo(id = 52, url = R.drawable.android11),
//    Photo(id = 53, url = R.drawable.android12),
//
//    Photo(id = 61, url = R.drawable.android20),
//    Photo(id = 62, url = R.drawable.android21),
//    Photo(id = 63, url = R.drawable.android22),
//
//    Photo(id = 71, url = R.drawable.android30),
//    Photo(id = 72, url = R.drawable.android31),
//    Photo(id = 73, url = R.drawable.android32),

)