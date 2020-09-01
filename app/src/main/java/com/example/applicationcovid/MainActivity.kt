package com.example.applicationcovid

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.applicationcovid.Network.ApiService
import com.example.applicationcovid.Network.RetrofitBuilder.retrofit
import com.example.applicationcovid.adapter.CountryAdapter
import com.example.applicationcovid.model.AllNegara
import com.example.applicationcovid.model.Negara
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {
    private var progressBar: ProgressBar? = null

    //untuk mengurutkan list negara sesuai huruf dan bisa diubah urutannya
    private var ascending = true

    companion object{
        //agar adapternya bisa berjalan
        lateinit var adapters: CountryAdapter
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progress_bar)
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapters.filter.filter(newText)
                return false
            }
        })

        swipe_refresh.setOnRefreshListener {
            getCountry()
            swipe_refresh.isRefreshing = false
        }

        getCountry()
        initializeViews()
    }



    private fun initializeViews() {
        sequence.setOnClickListener {
            sequenceWithoutInternet(ascending)
            ascending =! ascending
        }
      }

    private fun sequenceWithoutInternet(asc: Boolean) {
        rv_country.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            if (asc){
                (layoutManager as LinearLayoutManager).reverseLayout = true
                (layoutManager as LinearLayoutManager).stackFromEnd = true
                Toast.makeText( this@MainActivity, "A-Z", Toast.LENGTH_SHORT).show()

            }else{
                (layoutManager as LinearLayoutManager).reverseLayout = false
                (layoutManager as LinearLayoutManager).stackFromEnd = false
                Toast.makeText( this@MainActivity, "A-Z", Toast.LENGTH_SHORT).show()
            }
            adapter = adapters
        }
    }
    private fun getCountry() {
        val api = retrofit.create(ApiService::class.java)
        api.getAllNegara().enqueue(object  : Callback<AllNegara>{
            override fun onResponse(call: Call<AllNegara>, response: Response<AllNegara>) {
                if (response.isSuccessful){
                    val getListDataCorona = response.body()!!.Global
                    val formatter: NumberFormat = DecimalFormat("#,###")
                    txt_confirmed_globe.text = formatter.format(getListDataCorona?.TotalConfirmed?.toDouble())
                    txt_recovered_globe.text = formatter.format(getListDataCorona?.TotalRecovered?.toDouble())
                    txt_deaths_globe.text = formatter.format(getListDataCorona?.TotalDeaths?.toDouble())
                    rv_country.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        progressBar?.visibility = View.GONE
                        adapters = CountryAdapter(response.body()!!.Countries as ArrayList<Negara>)
                        {negara -> itemClicked(negara) }

                        adapter = adapters
                    }
                } else {
                    progressBar?.visibility = View.GONE
                    errorLoading(this@MainActivity)
                }
            }

            override fun onFailure(call: Call<AllNegara>, t: Throwable) {
                progressBar?.visibility = View.GONE
                errorLoading(this@MainActivity)
            }
        })
    }

    private fun itemClicked(negara: Negara) {
        val moveWithData = Intent(this@MainActivity, ChartCouintryActivity::class.java)
        moveWithData.putExtra(ChartCouintryActivity.EXTRA_COUNTRY, negara)
        startActivity(moveWithData)
    }

    private fun errorLoading(context: Context) {
        val builder = AlertDialog.Builder(context)
        with(builder){
            setTitle("Network Error!")
            setCancelable(false)
            setPositiveButton("REFRESH"){ _, _ ->
                super.onRestart()
                val refresh = Intent(this@MainActivity, MainActivity::class.java)
                startActivity(refresh)
                finish()
            }

            setNegativeButton("EXIT"){_, _ ->
                finish()
            }
            create()
            show()
        }
    }
}

