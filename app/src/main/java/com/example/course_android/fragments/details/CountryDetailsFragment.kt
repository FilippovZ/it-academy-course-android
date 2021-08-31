package com.example.course_android.fragments.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.course_android.Constants.COUNTRY_ALPHA_NAME_KEY
import com.example.course_android.Constants.COUNTRY_NAME_KEY
import com.example.course_android.Constants.ERROR
import com.example.course_android.R
import com.example.course_android.adapters.AdapterLanguages
import com.example.course_android.adapters.AdapterNews
import com.example.course_android.base.mvp.BaseMvpFragment
import com.example.course_android.databinding.FragmentCountryDetailsBinding
import com.example.domain.dto.model.CountryDescriptionItemDto
import com.example.course_android.ext.*
import com.example.course_android.utils.*
import com.example.domain.dto.news.NewsItemDto
import com.google.android.libraries.maps.SupportMapFragment
import org.koin.android.ext.android.inject

private const val LOCATION_PERMISSION_CODE = 1000

class CountryDetailsFragment : BaseMvpFragment<CountryDetailsView, CountryDetailsPresenter>(),
    CountryDetailsView {

    private lateinit var mCountryName: String
    private lateinit var mCountryAlphaName: String
    private var binding: FragmentCountryDetailsBinding? = null
    var mapFragment: SupportMapFragment? = null
    private var adapterLanguages = AdapterLanguages()
    private var adapterNews = AdapterNews()
    private var permissionGps = false
    private val mModulePresenter : CountryDetailsPresenter by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mCountryName = arguments?.getString(COUNTRY_NAME_KEY) ?: ERROR
        mCountryAlphaName = arguments?.getString(COUNTRY_ALPHA_NAME_KEY) ?: ERROR
        binding = FragmentCountryDetailsBinding.inflate(inflater, container, false)
        mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragmentContainer) as? SupportMapFragment?
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPresenter().attachView(this)
        setHasOptionsMenu(true)
        binding?.mTvCountryName?.text = mCountryName
        //адаптер стран
        binding?.recyclerLanguages?.layoutManager = LinearLayoutManager(context)
        binding?.recyclerLanguages?.adapter = adapterLanguages
        //адаптер новостей
        binding?.recyclerNews?.layoutManager = LinearLayoutManager(context)
        binding?.recyclerNews?.adapter = adapterNews

        binding?.srCountryDetails?.setOnRefreshListener {
            getPresenter().getCountryInfo(mCountryName, true)
            getPresenter().getNews(mCountryAlphaName, true)
        }
        getPresenter().getCountryInfo(mCountryName, false)
        getPresenter().getNews(mCountryAlphaName, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.country_description_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("StringFormatMatches")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.gps_distance) {
            activity?.showDialogWithOneButton(
                null,
                getString(R.string.distanceToYou, mCountryName, context?.let { getDistance(it) }),
                R.string.dialog_ok,
                null
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun createPresenter() {
        mPresenter = mModulePresenter
    }

    override fun getPresenter(): CountryDetailsPresenter = mPresenter

    override fun showCountryInfo(country: List<CountryDescriptionItemDto>) {
        //языки ресайкл
        adapterLanguages.repopulate(country[0].languages)

        binding?.srCountryDetails?.isRefreshing = false

        //флаг
        binding?.itemFlag?.loadSvg(country[0].flag)

        //проверяем и запрашиваем пермишен Gps
        if (context?.checkLocationPermission() == true) {
            permissionGps = true
        } else {
            activity?.askLocationPermission(LOCATION_PERMISSION_CODE)
        }

        //карта гугл
        mapFragment?.run {
            getMapAsync { map ->
                activity?.let {
                    initMapOfCountryDetails(
                        map,
                        country[0],
                        permissionGps
                    )
                }
            }
        }
    }

    override fun showNews(news: MutableList<NewsItemDto>) {

        adapterNews.repopulate(news)
    }

    override fun showError(error: String, throwable: Throwable) {
        if (context?.isOnline() == false) {
            context?.toast(getString(R.string.chek_inet))
        } else {
            activity?.showAlertDialog()
        }
    }

    override fun showProgress() {
        binding?.progress?.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        binding?.progress?.visibility = View.GONE
    }

}