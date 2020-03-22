package com.ordy.app.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class TabsAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val tabs: MutableList<TabsEntry> = mutableListOf()

    /**
     * Add a tab entry to the tabs.
     * @param tabsEntry TabsEntry to add.
     */
    fun addTabsEntry(tabsEntry: TabsEntry) = tabs.add(tabsEntry)

    /**
     * Total amount of tabs to display.
     */
    override fun getCount(): Int = tabs.size

    /**
     * Get the current fragment for the given tab.
     * @param position Number of the tab to display.
     */
    override fun getItem(position: Int): Fragment = tabs[position].fragment

    /**
     * Get the current fragment name for the given tab.
     * @param position Number of the tab to display.
     */
    override fun getPageTitle(position: Int): String = tabs[position].title
}

data class TabsEntry(val fragment: Fragment, val title: String)