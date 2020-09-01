package com.example.applicationcovid.Network

import com.example.applicationcovid.model.AllNegara
import com.example.applicationcovid.model.InfoNegara
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

//untuk meng-get data yang ada di dalam directory summary
interface ApiService {
    @GET("summary")
    fun getAllNegara(): Call<AllNegara>
}

//untuk akses tabel yang tersedia di model
interface InfoService{
    @GET
    fun getInfoService(@Url url: String): Call<List<InfoNegara>>
}

//untuk koneksi
object RetrofitBuilder{

    //untuk mengecek koneksi internet
    private val okhttp = OkHttpClient().newBuilder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    //menghubungkan ke api
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.covid19api.com/")
        .client(okhttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}