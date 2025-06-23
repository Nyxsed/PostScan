package ru.nyxsed.postscan.features.imagepager.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.nyxsed.postscan.core.domain.models.Content
import ru.nyxsed.postscan.core.domain.repository.VkRepository

class ChangeContentLikeStatusUseCaseTest {

    private val vkRepository: VkRepository = mock()
    private lateinit var useCase: ChangeContentLikeStatusUseCase

    @Before
    fun setUp() {
        useCase = ChangeContentLikeStatusUseCase(vkRepository)
    }

    @Test
    fun `should add like if content not liked`() = runTest {
        val content = Content(
            contentId = 1L,
            ownerId = 1L,
            type = "album", // должно преобразоваться в "photo"
            isLiked = false,
            urlSmall = "url",
            urlMedium = "url",
            urlBig = "url",
            title = "title"
        )
        whenever(vkRepository.getAccessToken()).thenReturn("123")

        useCase(content)

        verify(vkRepository).addLike(1L,1L,"photo","123")
        verify(vkRepository, never()).deleteLike(any(),any(),any(),any())
    }

    @Test
    fun `should delete like if content is liked`() = runTest {
        val content = Content(
            contentId = 2L,
            ownerId = 2L,
            type = "photo",
            isLiked = true,
            urlSmall = "url",
            urlMedium = "url",
            urlBig = "url",
            title = "title"
        )
        whenever(vkRepository.getAccessToken()).thenReturn("abc")

        useCase(content)

        verify(vkRepository).deleteLike(2L, 2L, "photo", "abc")
        verify(vkRepository, never()).addLike(any(), any(), any(), any())
    }
}