package com.ordy.app.ui

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.*
import com.ordy.app.api.Repository
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.groups.create.CreateGroupActivity
import com.ordy.app.ui.groups.create.CreateGroupHandlers
import com.ordy.app.ui.groups.create.CreateGroupViewModel
import com.ordy.app.ui.groups.invite.InviteMemberActivity
import com.ordy.app.ui.groups.invite.InviteMemberHandlers
import com.ordy.app.ui.groups.invite.InviteMemberViewModel
import okhttp3.ResponseBody
import org.apache.commons.lang3.mutable.Mutable
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.spy

class GroupsTest {

    var faker = Faker()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    /**
     * Mock data
     */
    private val mockRepository: Repository = mock()
    private val mockCreateGroupActivity: CreateGroupActivity = mock()
    private val mockCreateGroupViewModel: CreateGroupViewModel = mock()
    private val mockInviteMemberActivity: InviteMemberActivity = mock()
    private val mockInviteMemberViewModel: InviteMemberViewModel = mock()
    private val mockView: View = mock()

    /**
     * Actual data
     */
    private val createGroupViewModel = CreateGroupViewModel(mockRepository)
    private val createGroupHandlers =
        CreateGroupHandlers(mockCreateGroupActivity, mockCreateGroupViewModel, mockView)
    private val inviteMemberHandlers =
        InviteMemberHandlers(mockInviteMemberActivity, mockInviteMemberViewModel, mockView, 1)

    @Test
    fun `Create group calls repository with same groupName`() {
        val groupName = faker.name().name()

        // Call the create group function in the viewmodel.
        createGroupViewModel.createGroup(groupName)

        // Verify if the action calls the correct repository action.
        verify(mockRepository).createGroup(any(), eq(groupName))
    }

    @Test
    fun `Create button click calls viewmodel with value from mutableLiveData`() {
        val groupName = faker.name().name()
        val query: Query<Group> = Query(QueryStatus.SUCCESS)

        createGroupViewModel.getNameValueData().value = groupName

        // Mock the data
        whenever(mockCreateGroupViewModel.getNameValue()).thenReturn(groupName)
        whenever(mockCreateGroupViewModel.getCreateGroup()).thenReturn(query)

        // Call the click action.
        createGroupHandlers.onCreateButtonClick()

        // Verify if the action calls the correct repository action.
        verify(mockCreateGroupViewModel).createGroup(groupName)
    }

    @Test
    fun `Invite button click calls viewmodel`() {
        val liveData: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())
        val userId = faker.number().randomDigitNotZero()

        // Call the click action.
        inviteMemberHandlers.onInviteButtonClick(liveData, userId)

        // Verify if the action calls the correct repository action.
        verify(mockInviteMemberViewModel).sendInviteToUserFromGroup(any(), any(), any())
    }
}