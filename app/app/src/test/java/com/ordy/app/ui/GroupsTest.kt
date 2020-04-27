package com.ordy.app.ui

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.ordy.app.api.Repository
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.groups.create.CreateGroupActivity
import com.ordy.app.ui.groups.create.CreateGroupHandlers
import com.ordy.app.ui.groups.create.CreateGroupViewModel
import com.ordy.app.ui.groups.invite.InviteMemberActivity
import com.ordy.app.ui.groups.invite.InviteMemberHandlers
import com.ordy.app.ui.groups.invite.InviteMemberViewModel
import okhttp3.ResponseBody
import org.junit.Rule
import org.junit.Test

class GroupsTest {

    var faker = Faker()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockRepository: Repository = mock()

    private val createGroupViewModel = CreateGroupViewModel(mockRepository)
    private val mockCreateGroupActivity: CreateGroupActivity = mock()
    private val mockView: View = mock()
    private val createGroupHandlers =
        CreateGroupHandlers(mockCreateGroupActivity, createGroupViewModel, mockView)

    private val inviteMemberViewModel = InviteMemberViewModel(mockRepository)
    private val mockInviteMemberActivity: InviteMemberActivity = mock()
    private val inviteMemberHandlers =
        InviteMemberHandlers(mockInviteMemberActivity, inviteMemberViewModel, mockView, 1)

    @Test
    fun `create group calls repository with same groupName`() {
        val groupName = faker.name().name()
        createGroupViewModel.createGroup(groupName)

        verify(mockRepository).createGroup(groupName)
    }

    @Test
    fun `create button click calls repository with value from mutableLiveData`() {
        val groupName = faker.name().name()
        createGroupViewModel.getNameValueData().value = groupName
        whenever(mockRepository.getCreateGroupResult()).thenReturn(MutableLiveData(Query(QueryStatus.INITIALIZED)))

        createGroupHandlers.onCreateButtonClick()

        verify(mockRepository).createGroup(groupName)
    }

    @Test
    fun `invite button click calls repository`() {
        val liveData: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())
        val userId = faker.number().randomDigitNotZero()

        inviteMemberHandlers.onInviteButtonClick(liveData, userId)

        verify(mockRepository).sendInviteToUserFromGroup(any(), any(), any())
    }
}