package com.example.practica2

import android.content.Context
import android.webkit.CookieManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoriteManager (private val context: Context) {

    private val cookieManager: CookieManager = CookieManager.getInstance()
    private val gson = Gson()

    // Nombre de la cookie donde se almacenarán los favoritos
    private val favoritesCookieName = "favorites"

    // Obtener la lista de favoritos desde las cookies
    fun getFavorites(): MutableList<String> {
        val cookieValue = cookieManager.getCookie(context.packageName)
        val favoritesJson = cookieValue?.substringAfter("$favoritesCookieName=")?.substringBefore(";")
        return if (!favoritesJson.isNullOrEmpty()) {
            gson.fromJson(favoritesJson, object : TypeToken<MutableList<String>>() {}.type)
        } else {
            mutableListOf()
        }
    }

    // Agregar un nuevo favorito
    fun addFavorite(movieId: String) {
        val favorites = getFavorites()
        if (!favorites.contains(movieId)) {
            favorites.add(movieId)
            saveFavorites(favorites)
        }
    }

    // Eliminar un favorito
    fun removeFavorite(movieId: String) {
        val favorites = getFavorites()
        if (favorites.contains(movieId)) {
            favorites.remove(movieId)
            saveFavorites(favorites)
        }
    }

    // Guardar la lista de favoritos en las cookies
    private fun saveFavorites(favorites: List<String>) {
        val favoritesJson = gson.toJson(favorites)
        cookieManager.setCookie(context.packageName, "$favoritesCookieName=$favoritesJson;")
    }

    // Verificar si un elemento está en favoritos
    fun isFavorite(movieId: String): Boolean {
        return getFavorites().contains(movieId)
    }
}


