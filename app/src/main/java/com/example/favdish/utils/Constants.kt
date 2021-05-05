package com.example.favdish.utils

object Constants {

    const val DISH_TYPE : String = "DishType"
    const val DISH_CATEGORY : String = "DishCategory"
    const val DISH_COOKING_TIME: String = "DishCookingTime"

    const val DISH_IMAGE_SOURCE_LOCAL: String = "Local"
    const val DISH_IMAGE_SOURCE_ONLINE: String = "Online"

    const val EXTRA_DISH_DETAIL: String = "DishDetails"
    const val ALL_ITEMS: String = "All"
    const val FILTER_SELECTION:String = "FilterSelection"

    const val API_ENDPOINT : String = "recipes/random"

    const val API_KEY: String = "apiKey"
    const val LIMIT_LICENSE: String = "limitLicense"
    const val TAGS: String = "tags"
    const val NUMBER: String = "number"

    const val BASE_URL = "https://api.spoonacular.com/"
    const val LIMIT_LICENSE_VALUE: Boolean = true
    const val API_KEY_VALUE:String ="4519873b6b584457830c1826e5c2a993"
    const val NUMBER_VALUE: Int = 1
    const val TAGS_VALUE: String = "vegetarian, dessert"

    const val NOTIFICATION_ID = "FavDish_notification_id"
    const val NOTIFICATION_NAME = "FavDish"
    const val NOTIFICATION_CHANNEL = "FavDish_channel_01"

    fun disTypes():ArrayList<String>{
        val list = ArrayList<String>()
        list.add("breakfast")
        list.add("lunch")
        list.add("snacks")
        list.add("dinner")
        list.add("salad")
        list.add("side dish")
        list.add("dessert")
        list.add("other")
        return list
    }

    fun disCategories(): ArrayList<String>{
        val list = ArrayList<String>()
        list.add("Pizza")
        list.add("Drinks")
        list.add("Chicken")
        list.add("Wraps")
        list.add("Cafe")
        list.add("Burger")
        list.add("Hot Dogs")
        list.add("Sandwich")
        list.add("Other")


        return list
    }

        fun dishCookTime():ArrayList<String>{
            val list = ArrayList<String>()
            list.add("10")
            list.add("15")
            list.add("20")
            list.add("30")
            list.add("40")
            list.add("50")
            list.add("60")
            list.add("90")
            list.add("120")
            list.add("150")
            list.add("180")
            return list
        }
}