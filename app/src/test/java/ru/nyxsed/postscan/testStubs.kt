package ru.nyxsed.postscan

import ru.nyxsed.postscan.core.data.models.response.newsfeedget.GroupResponse
import ru.nyxsed.postscan.core.data.models.response.newsfeedget.ItemResponse
import ru.nyxsed.postscan.core.data.models.response.newsfeedget.LikesResponse
import ru.nyxsed.postscan.core.domain.models.Comment
import ru.nyxsed.postscan.core.domain.models.Content
import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.models.Post

//entity
fun stubPost(
    publicationDate: Long = 0L,
    isLiked: Boolean = false,
    postId: Long = 1L,
    ownerId: Long = 1L,
    ownerName: String = "name",
    ownerImageUrl: String = "url",
    contentText: String = "",
    content: List<Content> = emptyList(),
    haveReposts: Boolean = false,
): Post = Post(
    publicationDate = publicationDate,
    isLiked = isLiked,
    postId = postId,
    ownerId = ownerId,
    ownerName = ownerName,
    ownerImageUrl = ownerImageUrl,
    contentText = contentText,
    content = content,
    haveReposts = haveReposts
)

fun stubGroup(
    groupId: Long = 1L,
    name: String = "name",
    screenName: String = "screenName",
    avatarUrl: String = "avatarUrl",
    lastFetchDate: Long = 1L,
): Group = Group(
    groupId = groupId,
    name = name,
    screenName = screenName,
    avatarUrl = avatarUrl,
    lastFetchDate = lastFetchDate
)

fun stubContent(
    contentId: Long = 1L,
    ownerId: Long = 1L,
    type: String = "photo",
    isLiked : Boolean = false,
    urlSmall: String = "url",
    urlMedium: String = "url",
    urlBig: String = "url",
    title: String = "title",
) : Content = Content(
    contentId = contentId,
    ownerId = ownerId,
    type = type,
    isLiked = isLiked,
    urlSmall = urlSmall,
    urlMedium = urlMedium,
    urlBig = urlBig,
    title = title
)

fun stubComment(
    commentId: Long = 1L,
    ownerId: Long = 1L,
    ownerName: String = "ownerName",
    ownerImageUrl: String = "url",
    publicationDate: Long = 1L,
    contentText: String = "contentText",
    content: List<Content> = emptyList(),
    parentStack: Long? = 1L,
) : Comment = Comment(
    commentId = commentId,
    ownerId = ownerId,
    ownerName = ownerName,
    ownerImageUrl = ownerImageUrl,
    publicationDate = publicationDate,
    contentText = contentText,
    content = content,
    parentStack = parentStack
)

// responses
fun stubGroupResponse() = GroupResponse(
    id = 1L,
    name = "name",
    photo50 = "photo"
)

fun stubLikeResponse() = LikesResponse(
    userLikes = 1
)

fun stubItemResponse() = ItemResponse(
    attachments = emptyList(),
    date = 1L,
    id = 1L,
    likes = stubLikeResponse(),
    ownerId = 1L,
    postId = 1L,
    text = "text",
    copyHistory = null
)