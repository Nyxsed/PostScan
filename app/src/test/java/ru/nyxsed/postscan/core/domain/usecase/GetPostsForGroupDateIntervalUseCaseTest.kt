package ru.nyxsed.postscan.core.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.nyxsed.postscan.core.data.mapper.VkMapper
import ru.nyxsed.postscan.core.data.models.response.ErrorResponse
import ru.nyxsed.postscan.core.data.models.response.newsfeedget.WallGetContentResponse
import ru.nyxsed.postscan.core.data.models.response.newsfeedget.WallGetResponse
import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.models.Post
import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.domain.repository.DataStoreRepository
import ru.nyxsed.postscan.core.domain.repository.VkRepository
import ru.nyxsed.postscan.stubGroup
import ru.nyxsed.postscan.stubGroupResponse
import ru.nyxsed.postscan.stubItemResponse
import ru.nyxsed.postscan.stubPost


class GetPostsForGroupDateIntervalUseCaseTest {

    private val vkRepository = mockk<VkRepository>()
    private val dataStoreRepository = mockk<DataStoreRepository>()
    private val mapper = mockk<VkMapper>()

    private lateinit var useCase: GetPostsForGroupDateIntervalUseCase

    @BeforeEach
    fun setup() {
        useCase = GetPostsForGroupDateIntervalUseCase(vkRepository, dataStoreRepository, mapper)
    }

    @Test
    fun `returns posts in range and not liked`() = runTest {
        val group: Group = stubGroup()
        val startDate = 1000L
        val endDate = 2000L

        val mappedPosts = listOf(
            stubPost(publicationDate = 1500L, isLiked = false), // должен остаться
            stubPost(publicationDate = 1500L, isLiked = true),  // отфильтруется
            stubPost(publicationDate = 900L, isLiked = false)  // вне диапазона
        )

        coEvery { vkRepository.getAccessToken() } returns "123"
        coEvery { dataStoreRepository.getBoolean(SettingKey.NOT_LOAD_LIKED_POSTS) } returns true
        coEvery { vkRepository.wallGetPosts(any(), any(), any()) } returns WallGetResponse(
            content = WallGetContentResponse(groups = listOf(stubGroupResponse()), items = listOf(stubItemResponse())),
            error = null
        )
        every { mapper.mapWallGetResponseToPosts(any()) } returns mappedPosts

        val result = useCase(group, startDate, endDate)

        assertEquals(1, result.size)
        assertEquals(1500L, result[0].publicationDate)
    }

    @Test
    fun `returns posts in range and liked`() = runTest {
        val group: Group = stubGroup()
        val startDate = 1000L
        val endDate = 2000L

        val mappedPosts = listOf(
            stubPost(publicationDate = 1500L, isLiked = false), // должен остаться
            stubPost(publicationDate = 1500L, isLiked = true),  // должен остаться
            stubPost(publicationDate = 900L, isLiked = false)  // вне диапазона
        )

        coEvery { vkRepository.getAccessToken() } returns "123"
        coEvery { dataStoreRepository.getBoolean(SettingKey.NOT_LOAD_LIKED_POSTS) } returns false
        coEvery { vkRepository.wallGetPosts(any(), any(), any()) } returns WallGetResponse(
            content = WallGetContentResponse(groups = listOf(stubGroupResponse()), items = listOf(stubItemResponse())),
            error = null
        )
        every { mapper.mapWallGetResponseToPosts(any()) } returns mappedPosts

        val result = useCase(group, startDate, endDate)

        assertEquals(2, result.size)
        assertTrue(result.all { it.publicationDate in startDate..endDate })
    }

    @Test
    fun `throws exception when API returns error`() = runTest {
        val group = stubGroup()
        coEvery { dataStoreRepository.getBoolean(any()) } returns true
        coEvery { vkRepository.getAccessToken() } returns "token"
        coEvery { vkRepository.wallGetPosts(any(), any(), any()) } returns WallGetResponse(
            content = null,
            error = ErrorResponse(errorMsg = "API error", errorText = "error text")
        )

        val exception = assertThrows(Exception::class.java) {
            runBlocking {
                useCase(group, 0L, 1000L)
            }
        }
        assertEquals("API error", exception.message)
    }

    @Test
    fun `stops when response content or items are empty`() = runTest {
        val group = stubGroup()
        coEvery { dataStoreRepository.getBoolean(any()) } returns true
        coEvery { vkRepository.getAccessToken() } returns "token"
        coEvery { vkRepository.wallGetPosts(any(), any(), any()) } returns WallGetResponse(
            content = WallGetContentResponse(groups = emptyList(), items = emptyList()),
            error = null
        )
        every { mapper.mapWallGetResponseToPosts(any()) } returns emptyList()

        val result = useCase(group, 0L, 1000L)

        assertEquals(emptyList<Post>(), result)
        coVerify(exactly = 1) { vkRepository.wallGetPosts(any(), any(), any()) }
    }

    @Test
    fun `returns empty list when no posts in date range and calls getBoolean and getAccessToken exactly once`() = runTest {
        val group = stubGroup()
        coEvery { dataStoreRepository.getBoolean(any()) } returns true
        coEvery { vkRepository.getAccessToken() } returns "token"

        val posts = listOf(
            stubPost(publicationDate = 500L),
            stubPost(publicationDate = 800L)
        )

        coEvery { vkRepository.wallGetPosts(any(), any(), any()) } returns WallGetResponse(
            content = WallGetContentResponse(groups = listOf(stubGroupResponse()), items = listOf(stubItemResponse())),
            error = null
        )
        every { mapper.mapWallGetResponseToPosts(any()) } returns posts

        val result = useCase(group, 1000L, 2000L)

        assertEquals(emptyList<Post>(), result)
        coVerify(exactly = 1) { dataStoreRepository.getBoolean(SettingKey.NOT_LOAD_LIKED_POSTS) }
        coVerify(exactly = 1) { vkRepository.getAccessToken() }
    }
}