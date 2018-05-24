package com.medicnet.android.authentication.selectorganization.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.medicnet.android.R
import com.medicnet.android.authentication.domain.model.OrganizationListModel
import com.medicnet.android.authentication.selectorganization.presentation.SelectOrganizationPresenter
import com.medicnet.android.authentication.selectorganization.presentation.SelectOrganizationView
import com.medicnet.android.util.extensions.inflate
import com.squareup.moshi.Moshi
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


/**
 * @author duc.nguyen
 * @since 5/24/2018
 */
class SelectOrganizationFragment : Fragment(), SelectOrganizationView {
    @Inject
    lateinit var presenter: SelectOrganizationPresenter
    //    lateinit var listView:ListView
    lateinit var organizationList: ArrayList<String>
    private var jsonData: String? = null

    companion object {
        private const val JSON_DATA = "JsonData"

        fun newInstance(json: String) = SelectOrganizationFragment().apply {
            arguments = Bundle().apply {
                putString(JSON_DATA, json)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        jsonData = arguments?.getString(JSON_DATA)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? = container?.inflate(R.layout.fragment_select_organization)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val moshi = Moshi.Builder().build();
        val jsonAdapter = moshi.adapter<OrganizationListModel>(OrganizationListModel::class.java)
        var organizationListModel: OrganizationListModel? = null
//        var organizations:Array<OrganizationModel>?=null
        organizationList = ArrayList<String>()
        try {
            organizationListModel = jsonAdapter.fromJson(jsonData)
            for (o in organizationListModel!!.organizations!!) {
                organizationList.add(o._value!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setupListView() {
//        val adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,organizationList)
//        lvOrganization.adapter=ad
    }

    override fun search() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}