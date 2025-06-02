package ru.nyxsed.postscan.core.data.mapper

import ru.nyxsed.postscan.core.data.models.response.groupsget.GroupsGetResponse
import ru.nyxsed.postscan.core.data.models.response.newsfeedget.AttachmentResponse
import ru.nyxsed.postscan.core.data.models.response.newsfeedget.WallGetResponse
import ru.nyxsed.postscan.core.data.models.response.wallgetcomments.ItemResponse
import ru.nyxsed.postscan.core.data.models.response.wallgetcomments.ProfilesResponse
import ru.nyxsed.postscan.core.data.models.response.wallgetcomments.WallGetCommentsResponse
import ru.nyxsed.postscan.core.domain.models.Comment
import ru.nyxsed.postscan.core.domain.models.Content
import ru.nyxsed.postscan.core.domain.models.Group
import ru.nyxsed.postscan.core.domain.models.Post
import ru.nyxsed.postscan.core.util.Constants.findOrFirst
import ru.nyxsed.postscan.core.util.Constants.findOrLast
import kotlin.math.absoluteValue

class VkMapper {
    fun mapWallGetResponseToPosts(response: WallGetResponse): List<Post> {
        val result = mutableListOf<Post>()

        val posts = response.content?.items
        val groups = response.content?.groups

        posts?.let { posts ->
            for (post in posts) {
                val group = groups?.find { it.id == post.ownerId.absoluteValue } ?: continue

                val listContent: MutableList<Content> = mutableListOf()

                post.attachments?.forEach { attachment ->
                    val content = getContentEntity(attachment)
                    if (content != null) {
                        listContent.add(content)
                    }
                }

                val copyHistory = post.copyHistory
                copyHistory?.forEach { repost ->
                    val listRepostContent: MutableList<Content> = mutableListOf()

                    repost.attachments?.forEach { attachment ->
                        val content = getContentEntity(attachment)
                        if (content != null) {
                            listRepostContent.add(content)
                        }
                    }
                    listContent.addAll(listRepostContent)
                }

                val postEnt = Post(
                    postId = post.id,
                    ownerId = post.ownerId,
                    ownerName = group.name,
                    ownerImageUrl = group.photo50,
                    publicationDate = post.date * 1000,
                    contentText = post.text,
                    isLiked = post.likes.userLikes > 0,
                    content = listContent,
                    haveReposts = if (copyHistory.isNullOrEmpty()) false else true
                )

                result.add(postEnt)
            }
        }

        return result
    }

    fun mapGroupsGetResponseToGroups(response: GroupsGetResponse): List<Group> {
        val result = mutableListOf<Group>()

        val items = response.response?.items
        val groups = response.response?.groups

        items?.forEach { groupItem ->
            val group = Group(
                groupId = groupItem.id,
                name = groupItem.name,
                screenName = groupItem.screenName,
                avatarUrl = groupItem.photo50,
                lastFetchDate = System.currentTimeMillis()
            )
            result.add(group)
        }

        groups?.forEach { groupItem ->
            val group = Group(
                groupId = groupItem.id,
                name = groupItem.name,
                screenName = groupItem.screenName,
                avatarUrl = groupItem.photo50,
                lastFetchDate = System.currentTimeMillis()
            )
            result.add(group)
        }

        return result
    }

    fun mapWallGetCommentsResponseToComments(response: WallGetCommentsResponse): List<Comment> {
        val result = mutableListOf<Comment>()

        val comments = response.content?.items
        val profiles = response.content?.profiles

        comments?.let { comments ->
            for (comment in comments) {

                val threadComments = comment.thread?.items
                threadComments?.let { threadComments ->

                    for (threadComment in threadComments) {
                        val threadProfile = profiles?.find { it.id == threadComment.fromId.absoluteValue } ?: continue
                        val threadCommentEntity = getCommentEntity(
                            comment = threadComment,
                            profile = threadProfile,
                        )
                        result.add(threadCommentEntity)
                    }
                }

                val profile = profiles?.find { it.id == comment.fromId.absoluteValue } ?: continue
                val commentEntity = getCommentEntity(
                    comment = comment,
                    profile = profile,
                )

                result.add(commentEntity)
            }
        }

        return result
    }

    private fun getCommentEntity(comment: ItemResponse, profile: ProfilesResponse): Comment {
        val listContent: MutableList<Content> = mutableListOf()

        comment.attachments?.forEach { attachment ->
            val content = getContentEntity(attachment)
            if (content != null) {
                listContent.add(content)
            }
        }

        return Comment(
            commentId = comment.commentId,
            ownerId = comment.ownerId,
            ownerName = "${profile.firstName} ${profile.lastName}",
            ownerImageUrl = profile.photo50,
            publicationDate = comment.date,
            contentText = comment.text,
            content = listContent,
            parentStack = comment.parentsStack.firstOrNull()
        )
    }

    private fun getContentEntity(attachment: AttachmentResponse): Content? {
        var contentId: Long = 0
        var ownerId: Long = 0
        var type: String = ""
        var urlSmall: String = ""
        var urlMedium: String = ""
        var urlBig: String = ""
        var title: String = ""

        when (attachment.type) {
            "photo" -> {
                val attachmentPhoto = attachment.photo
                attachmentPhoto?.let { photo ->
                    contentId = photo.id
                    ownerId = photo.ownerId
                    type = attachment.type
                    urlSmall = photo.sizes.findOrFirst { it.type == "s" }.url
                    urlMedium = photo.sizes.findOrLast { it.type == "x" }.url
                    urlBig = photo.sizes.findOrLast { it.type == "z" }.url
                    contentId = photo.id
                }
            }

            "video" -> {
                val attachmentVideo = attachment.video
                attachmentVideo?.let { video ->
                    contentId = video.id
                    ownerId = video.ownerId
                    type = attachment.type
                    urlSmall = video.image.findOrFirst { it.url.takeLast(5) == "vid_s" }.url
                    urlMedium = video.image.findOrLast { it.url.takeLast(5) == "vid_l" }.url
                    urlBig = video.image.findOrLast { it.url.takeLast(5) == "vid_x" }.url
                }
            }

            "album" -> {
                val attachmentAlbum = attachment.album
                attachmentAlbum?.let { album ->

                    album.thumb.sizes.let {
                        if (it.isNotEmpty()) {
                            urlSmall = album.thumb.sizes.findOrFirst { it.type == "s" }.url
                            urlMedium = album.thumb.sizes.findOrLast { it.type == "x" }.url
                            urlBig = album.thumb.sizes.findOrLast { it.type == "z" }.url
                        }
                    }
                    contentId = album.thumb.id
                    ownerId = album.thumb.ownerId
                    type = attachment.type
                    title = album.title
                }
            }

            "doc" -> {
                val attachmentDoc =  attachment.doc
                attachmentDoc?.let { doc ->
                    contentId = doc.id
                    ownerId = doc.ownerId
                    type = attachment.type
                    urlSmall = doc.preview?.photo?.sizes?.findOrFirst { it.type == "s" }?.src ?: ""
                    urlMedium =  doc.preview?.photo?.sizes?.findOrLast { it.type == "x" }?.src ?: ""
                    urlBig =  doc.preview?.photo?.sizes?.findOrLast { it.type == "z" }?.src ?: ""
                }
            }
        }

        return if (contentId != 0L) {
            Content(
                contentId = contentId,
                ownerId = ownerId,
                type = type,
                isLiked = false,
                urlSmall = urlSmall,
                urlMedium = urlMedium,
                urlBig = urlBig,
                title = title
            )
        } else {
            null
        }
    }
}