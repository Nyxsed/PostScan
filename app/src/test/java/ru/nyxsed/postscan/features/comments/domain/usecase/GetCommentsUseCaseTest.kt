package ru.nyxsed.postscan.features.comments.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.nyxsed.postscan.core.domain.repository.VkRepository
import ru.nyxsed.postscan.stubComment
import ru.nyxsed.postscan.stubPost

class GetCommentsUseCaseTest {

    private val vkRepository = mockk<VkRepository>()
    private lateinit var useCase: GetCommentsUseCase
    val post = stubPost()
    val comments = listOf(
        stubComment(commentId = 1L),
        stubComment(commentId = 2L)
    )

    @BeforeEach
    fun setup() {
        useCase = GetCommentsUseCase(vkRepository)
    }

    @Test
    fun `invoke emits comments from repository`() = runTest {
        every { vkRepository.getAccessToken() } returns "token"
        coEvery { vkRepository.getComments(post.ownerId, post.postId, "token") } returns comments

        val emitted = useCase(post).first()

        assertEquals(comments, emitted)
        verify { vkRepository.getAccessToken() }
        coVerify { vkRepository.getComments(post.ownerId, post.postId, "token") }
        confirmVerified(vkRepository)
    }
}