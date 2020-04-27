package com.ordy.app.ui.payments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.ordy.app.R
import com.ordy.app.api.RepositoryViewModelFactory
import com.ordy.app.ui.payments.debtors.PaymentsDebtorsFragment
import com.ordy.app.ui.payments.debts.PaymentsDebtsFragment
import com.ordy.app.util.TabsAdapter
import com.ordy.app.util.TabsEntry

class PaymentsFragment : Fragment() {

    private lateinit var tabsAdapter: TabsAdapter

    private val viewModel: PaymentsViewModel by activityViewModels {
        RepositoryViewModelFactory(
            requireContext()
        )
    }

    /**
     * Called when view is created.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment.
        val view = inflater.inflate(R.layout.fragment_payments, container, false)

        // Create the tabs adapter.
        tabsAdapter = TabsAdapter(childFragmentManager)
        tabsAdapter.addTabsEntry(
            TabsEntry(
                PaymentsDebtorsFragment(this),
                getString(R.string.debtors_tab_title)
            )
        )
        tabsAdapter.addTabsEntry(
            TabsEntry(
                PaymentsDebtsFragment(this),
                getString(R.string.debts_tab_title)
            )
        )

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Setup the tabsbar.
         * Link tabsAdapter to viewPager.
         * Link viewPager ot tabLayout.
         */
        val viewPager = view.findViewById<ViewPager>(R.id.tabs_view)
        viewPager.adapter = tabsAdapter

        val tabs = view.findViewById<TabLayout>(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }
}