package com.example.course_android.fragments.details

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.course_android.Constants.COUNTRY_NAME_KEY
import com.example.course_android.Constants.ERROR
import com.example.course_android.CountriesApp
import com.example.course_android.R
import com.example.course_android.adapters.AdapterLanguages
import com.example.course_android.api.CountryDescriptionApi
import com.example.course_android.api.RetrofitObj
import com.example.course_android.base.googlemap.getDistance
import com.example.course_android.base.googlemap.initMap2
import com.example.course_android.base.mvp.BaseMvpFragment
import com.example.course_android.databinding.FragmentCountryDetailsBinding
import com.example.course_android.dto.model.CountryDescriptionItemDto
import com.example.course_android.ext.showDialogWithOneButton
import com.example.course_android.utils.loadSvg
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.LatLng
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_country_details.*

private const val LOCATION_PERMISSION_CODE = 1000
//private const val LOG_TAG = "CountryDetailsFragment"

class CountryDetailsFragment : BaseMvpFragment<CountryDetailsView, CountryDetailsPresenter>(), CountryDetailsView {

    private lateinit var mCountryName: String
    private var binding: FragmentCountryDetailsBinding? = null
    private lateinit var countryDetailsDto: CountryDescriptionItemDto
    private var mSrCountryDetails: SwipeRefreshLayout? = null
    private var progressBar: FrameLayout? = null
    private val mCompositeDisposable = CompositeDisposable()
    private var googleMap: GoogleMap? = null
    var mapFragment: SupportMapFragment? = null
    private var adapterLanguages = AdapterLanguages()
    private lateinit var currentCountryLatLng: LatLng
    private var permissionGps = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mCountryName = arguments?.getString(COUNTRY_NAME_KEY) ?: ERROR

        binding = FragmentCountryDetailsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPresenter().attachView(this)
        setHasOptionsMenu(true)
        binding?.mTvCountryName?.text = mCountryName
        mSrCountryDetails = binding?.srCountryDetails
        progressBar = binding?.progress
        recycler_languages.layoutManager = LinearLayoutManager(context)
        recycler_languages.adapter = adapterLanguages

        mSrCountryDetails?.setOnRefreshListener {
            getPresenter().getMyData(mCountryName, true)
        }
        getPresenter().getMyData(mCountryName, false)

    }

//    private fun initMap(map: GoogleMap) {
//        currentCountryLatLng = LatLng(
//            countryDetailsDto.latlng[0],
//            countryDetailsDto.latlng[1]
//        )
//        googleMap = map.apply {
//
//            val cameraLocation = CameraUpdateFactory.newLatLngZoom(currentCountryLatLng, 7.0f)
//            moveCamera(cameraLocation)
//            if (checkLocationPermission()) {
//                isMyLocationEnabled = true
//                getDistance()
//            } else {
//                askLocationPermission()
//            }
//        }
////        addMarkerOnMap(currentCountryLatLng)
//    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_CODE && grantResults[0] == PERMISSION_GRANTED) {
            googleMap?.isMyLocationEnabled = true
            getDistance()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //проверяем permission
    private fun checkLocationPermission() =
        context?.let {
            ContextCompat.checkSelfPermission(
                it,
                ACCESS_FINE_LOCATION
            )
        } == PERMISSION_GRANTED

    //запрос permission
    private fun askLocationPermission() {
        requestPermissions(arrayOf(ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
    }

    //добавляем маркер
//    private fun addMarkerOnMap(markerPosition: LatLng) {
//        val markerOptions = MarkerOptions()
//            .position(markerPosition)
//            .title("$mCountryName")
//        googleMap?.addMarker(markerOptions)
//    }

    private fun getMyData(isRefresh: Boolean) {
        progressBar?.visibility = if (isRefresh) View.GONE else View.VISIBLE
        RetrofitObj.getOkHttp()
        val countryDescrApi = CountriesApp.retrofit.create(CountryDescriptionApi::class.java)

        val subscription = countryDescrApi.getTopHeadlines(mCountryName)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response ->
//                countryDescriptionFromApi =
//                    (response as MutableList<CountryDescriptionItem>)
                mSrCountryDetails?.isRefreshing = false
                //трансформируем в DTO
//                val countryDetailsDtoTransformer = CountryDetailsDtoTransformer()
//                countryDetailsDto =
//                    countryDetailsDtoTransformer.transform(response)



                //флаг
//                binding?.itemFlag?.loadSvg(countryDetailsDto.flag)

                //карта гугл
//                mapFragment =
//                    childFragmentManager.findFragmentById(R.id.mapFragmentContainer) as? SupportMapFragment?
//                mapFragment?.run {
//                    getMapAsync { map -> initMap(map) }
//                    progressBar?.visibility = View.GONE
//                }
            }, {
                progressBar?.visibility = View.GONE
            })
        mCompositeDisposable.add(subscription)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.country_description_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.gps_distance) {
            activity?.showDialogWithOneButton(
                null,
                getString(R.string.distanceToYou, mCountryName, getDistance()),
                R.string.dialog_ok,
                null
            )
        }
        return super.onOptionsItemSelected(item)
    }

//    @SuppressLint("MissingPermission")
//    private fun getDistance() {
//        val currentCountryLocation = Location(LocationManager.GPS_PROVIDER).apply {
//            latitude = currentCountryLatLng.latitude
//            longitude = currentCountryLatLng.longitude
//        }
//        LocationServices.getFusedLocationProviderClient(context)
//            .lastLocation
//            .addOnSuccessListener { location ->
//                distance = location.distanceTo(currentCountryLocation).toInt() / 1000
//                Log.d(LOG_TAG, location.toString())
//            }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        mCompositeDisposable.clear()
    }

    override fun createPresenter() {
        mPresenter = CountryDetailsPresenter()
    }

    override fun getPresenter(): CountryDetailsPresenter = mPresenter

    override fun showCountryInfo(country: CountryDescriptionItemDto) {
        //языки ресайкл
        adapterLanguages.repopulate(country.languages)

        binding?.srCountryDetails?.isRefreshing = false

        //флаг
        binding?.itemFlag?.loadSvg(country.flag)

        if (checkLocationPermission()) {
//                isMyLocationEnabled = true
    permissionGps = true
                getDistance()
            } else {
                askLocationPermission()
            }


        //карта гугл
        mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragmentContainer) as? SupportMapFragment?
        mapFragment?.run {
            getMapAsync { map -> activity?.let { initMap2(map, country, it.applicationContext, permissionGps) } }
        }
    }

    override fun showError(error: String, throwable: Throwable) {
//        showAlertDialog()
    }

    override fun showProgress() {
        binding?.progress?.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        binding?.progress?.visibility = View.GONE
    }

}