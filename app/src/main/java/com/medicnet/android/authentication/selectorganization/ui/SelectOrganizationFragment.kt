package com.medicnet.android.authentication.selectorganization.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.medicnet.android.authentication.selectorganization.presentation.SelectOrganizationPresenter
import com.medicnet.android.authentication.selectorganization.presentation.SelectOrganizationView
import javax.inject.Inject


/**
 * @author duc.nguyen
 * @since 5/24/2018
 */
class SelectOrganizationFragment : Fragment(), SelectOrganizationView {
    @Inject
    lateinit var presenter: SelectOrganizationPresenter

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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