package com.example.practica2.api


import retrofit2.Call

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface TvMazeApi {
        @GET("search/shows")
        fun searchShows(@Query("q") query: String): Call<List<ShowResponse>>
    //para obtener el cast de un show
    @GET("shows/{id}/cast")
    fun getShowCast(@Path("id") showId: Int): Call<List<CastMember>>
    //para obtener los shows
    @GET("shows/{id}")
    fun getShowDetails(@Path("id") showId: Int): Call<Show>
    //para obtener las temporadas
    @GET("shows/{id}/seasons")
    fun getShowSeasons(@Path("id") showId: Int): Call<List<Season>>

    @GET("seasons/{id}/episodes")
    fun getSeasonEpisodes(@Path("id") seasonId: Int): Call<List<Episode>>
    }



