package ru.nyxsed.postscan.features.imagepager.domain.usecase

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.nyxsed.postscan.core.domain.repository.VkRepository
import ru.nyxsed.postscan.stubContent

class ChangeContentLikeStatusUseCaseTest {

    private val vkRepository: VkRepository = mockk<VkRepository>()
    private lateinit var useCase: ChangeContentLikeStatusUseCase

    @BeforeEach
    fun setup() {
        useCase = ChangeContentLikeStatusUseCase(vkRepository)
    }

    @Test
    fun `should add like if content not liked`() = runTest {
        val content = stubContent(type = "album") // должно преобразоваться в "photo"
        coEvery { vkRepository.getAccessToken() } returns "123"
        coEvery { vkRepository.addLike(any(), any(), any(), any()) } just Runs
        coEvery { vkRepository.deleteLike(any(), any(), any(), any()) } just Runs

        useCase(content)

        coVerify(exactly = 1) { vkRepository.addLike(1L, 1L, "photo", "123") }
        coVerify(exactly = 0) { vkRepository.deleteLike(any(), any(), any(), any()) }
    }

    @Test
    fun `should delete like if content is liked`() = runTest {
        val content = stubContent(isLiked = true)
        coEvery { vkRepository.getAccessToken() } returns "123"
        coEvery { vkRepository.addLike(any(), any(), any(), any()) } just Runs
        coEvery { vkRepository.deleteLike(any(), any(), any(), any()) } just Runs

        useCase(content)

        coVerify(exactly = 1) { vkRepository.deleteLike(1L, 1L, "photo", "123") }
        coVerify(exactly = 0) { vkRepository.addLike(any(), any(), any(), any()) }
    }
}