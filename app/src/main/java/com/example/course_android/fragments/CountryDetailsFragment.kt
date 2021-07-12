package com.example.course_android.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.course_android.AdapterLanguages
import com.example.course_android.Constants
import com.example.course_android.CountriesApp
import com.example.course_android.R
import com.example.course_android.api.CountryDescriptionApi
import com.example.course_android.api.RetrofitObj
import com.example.course_android.databinding.FragmentCountryDetailsBinding
import com.example.course_android.model.oneCountry.CountryDescriptionItem
import com.example.course_android.model.oneCountry.LanguageOfOneCountry
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_country_details.*
import kotlinx.android.synthetic.main.fragment_second.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CountryDetailsFragment : Fragment(R.layout.fragment_country_details) {

    private lateinit var mCountryName: String
    private lateinit var binding: FragmentCountryDetailsBinding
    private var mLanguageList: List<LanguageOfOneCountry>? = null
    lateinit var adapterLanguages: AdapterLanguages
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var countryDescriptionFromApi: MutableList<CountryDescriptionItem>

    private lateinit var googleMap: GoogleMap
    var mapFragment: SupportMapFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCountryName = arguments?.getString(Constants.COUNTRY_NAME_KEY) ?: Constants.ERROR
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCountryDetailsBinding.bind(view)
        getMyData()
        //название страны
        binding.mTvCountryName.text = mCountryName




    }


    private fun initMap(map: GoogleMap) {
        googleMap = map.apply {
            if (countryDescriptionFromApi[0].latlng.size == 2) {
                val startLocation = LatLng(
                    countryDescriptionFromApi[0].latlng[0],
                    countryDescriptionFromApi[0].latlng[1])
                val cameraLocation = CameraUpdateFactory.newLatLngZoom(startLocation, 7.0f)
                this.moveCamera(cameraLocation)
            }
        }
    }

    private fun AppCompatImageView.loadSvg(url: String) {
        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry { add(SvgDecoder(this@loadSvg.context)) }
            .build()

        val request = ImageRequest.Builder(this.context)
            .crossfade(true)
            .crossfade(500)
            .data(url)
            .target(this)
            .build()

        imageLoader.enqueue(request)
    }

    private fun getMyData() {
        RetrofitObj.getOkHttp()
        val countryDescrApi = CountriesApp.retrofit.create(CountryDescriptionApi::class.java)
        val countryDescrApiCall = countryDescrApi.getTopHeadlines(mCountryName)

        countryDescrApiCall.enqueue(object : Callback<List<CountryDescriptionItem>?> {
            override fun onResponse(
                call: Call<List<CountryDescriptionItem>?>,
                response: Response<List<CountryDescriptionItem>?>
            ) {
                if (response.body() != null) {
                    countryDescriptionFromApi =
                        (response.body() as MutableList<CountryDescriptionItem>)

                    //языки ресайкл
                    linearLayoutManager = LinearLayoutManager(context)
                    recycler_languages.layoutManager = linearLayoutManager
                    adapterLanguages = AdapterLanguages()

                    mLanguageList = countryDescriptionFromApi[0].languages

                    val params: ViewGroup.LayoutParams = recycler_languages.layoutParams
                    params.height =
                        mLanguageList?.size?.times(Constants.LANGUAGE_VIEW_HEIGHT) ?: Constants.DEFAULT_INT
                    recycler_languages.layoutParams = params

                    recycler_languages.adapter = adapterLanguages
                    adapterLanguages.repopulate(mLanguageList as MutableList<LanguageOfOneCountry>)

                    //флаг
                    binding.itemFlag.loadSvg(countryDescriptionFromApi[0].flag)

                    //карта гугл
                    mapFragment =
                        childFragmentManager.findFragmentById(R.id.mapFragmentContainer) as? SupportMapFragment?
                    mapFragment?.run {
                        getMapAsync { map -> initMap(map) }
                    }



                } else {
                    Log.d("RETROFIT_COUNTRIES", response.body().toString())
                }

            }

            override fun onFailure(call: Call<List<CountryDescriptionItem>?>, t: Throwable) {
                Log.d("RETROFIT_COUNTRIES", t.toString())
            }
        })
    }
}