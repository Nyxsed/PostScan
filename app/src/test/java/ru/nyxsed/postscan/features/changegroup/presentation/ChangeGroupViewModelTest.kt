package ru.nyxsed.postscan.features.changegroup.presentation

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import ru.nyxsed.postscan.core.domain.usecase.AddPostUseCase
import ru.nyxsed.postscan.core.domain.usecase.DeleteGroupPostsUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetPostsForGroupDateIntervalUseCase
import ru.nyxsed.postscan.core.domain.usecase.GetResourceUseCase
import ru.nyxsed.postscan.core.domain.usecase.IsInternetAvailableUseCase
import ru.nyxsed.postscan.core.domain.usecase.UpdateGroupUseCase
import ru.nyxsed.postscan.core.domain.util.NotificationHelper
import ru.nyxsed.postscan.core.event.UiEvent
import ru.nyxsed.postscan.stubGroup
import kotlin.test.Test
import kotlin.test.assertEquals

class ChangeGroupViewModelTest {

    private lateinit var vm: ChangeGroupViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val group = stubGroup()
    private val getResourceUseCase = mockk<GetResourceUseCase>(relaxed = true)
    private val isInternetAvailableUseCase = mockk<IsInternetAvailableUseCase>()
    private val getPostsForGroupDateIntervalUseCase = mockk<GetPostsForGroupDateIntervalUseCase>()
    private val addPostUseCase = mockk<AddPostUseCase>(relaxed = true)
    private val deleteGroupPostsUseCase = mockk<DeleteGroupPostsUseCase>(relaxed = true)
    private val updateGroupUseCase = mockk<UpdateGroupUseCase>(relaxed = true)
    private val notificationHelper = mockk<NotificationHelper>(relaxed = true)

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        vm = ChangeGroupViewModel(
            group = group,
            getResourceUseCase = getResourceUseCase,
            isInternetAvailableUseCase = isInternetAvailableUseCase,
            getPostsForGroupDateIntervalUseCase = getPostsForGroupDateIntervalUseCase,
            addPostUseCase = addPostUseCase,
            deleteGroupPostsUseCase = deleteGroupPostsUseCase,
            updateGroupUseCase = updateGroupUseCase,
            notificationHelper = notificationHelper
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `ChangeGroupName intent updates state`() = runTest {
        vm.processIntent(ChangeGroupIntent.ChangeGroupName("newName"))
        runCurrent() // запуск отложенных корутин
        assertEquals("newName", vm.state.value.groupName)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `ToggleDeleteDialog toggles flag`() = runTest {
        vm.processIntent(ChangeGroupIntent.ToggleDeleteDialog)
        runCurrent()
        assertTrue(vm.state.value.showDeleteDialog)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `UpdateGroup intent calls useCase and emits NavigateBack`() = runTest {
        val eventDeferred = async { vm.uiEventFlow.first() }
        vm.processIntent(ChangeGroupIntent.UpdateGroup)
        runCurrent()
        coVerify { updateGroupUseCase(any()) }
        assertTrue(eventDeferred.await() is UiEvent.NavigateBack)
    }
}