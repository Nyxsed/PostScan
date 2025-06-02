package ru.nyxsed.postscan.core.data.mapper

import ru.nyxsed.postscan.core.data.models.response.groupsget.GroupsGetResponse
import ru.nyxsed.postscan.core.data.models.response.newsfeedget.AttachmentResponse
import ru.nyxsed.postscan.core.data.models.response.newsfeedget.WallGetResponse
import ru.nyxsed.postscan.core.data.models.response.wallgetcomments.ItemResponse
import ru.nyxsed.postscan.core.data.models.response.wallgetcomments.ProfilesResponse
import ru.nyxsed.postscan.core.data.models.response.wallgetcomments.WallGetCommentsResponse
import ru.nyxsed.postscan.core.domain.models.entity.CommentEntity
import ru.nyxsed.postscan.core.domain.models.entity.ContentEntity
import ru.nyxsed.postscan.core.domain.models.entity.GroupEntity
import ru.nyxsed.postscan.core.domain.models.entity.PostEntity
import ru.nyxsed.postscan.core.util.Constants.findOrFirst
import ru.nyxsed.postscan.core.util.Constants.findOrLast
import kotlin.math.absoluteValue

class VkMapper {
    fun mapWallGetResponseToPosts(response: WallGetResponse): List<PostEntity> {
        val result = mutableListOf<PostEntity>()

        val posts = response.content?.items
        val groups = response.content?.groups

        posts?.let { posts ->
            for (post in posts) {
                val group = groups?.find { it.id == post.ownerId.absoluteValue } ?: continue

                val listContentEntity: MutableList<ContentEntity> = mutableListOf()

                post.attachments?.forEach { attachment ->
                    val content = getContentEntity(attachment)
                    if (content != null) {
                        listContentEntity.add(content)
                    }
                }

                val copyHistory = post.copyHistory
                copyHistory?.forEach { repost ->
                    val listRepostContentEntity: MutableList<ContentEntity> = mutableListOf()

                    repost.attachments?.forEach { attachment ->
                        val content = getContentEntity(attachment)
                        if (content != null) {
                            listRepostContentEntity.add(content)
                        }
                    }
                    listContentEntity.addAll(listRepostContentEntity)
                }

                val postEnt = PostEntity(
                    postId = post.id,
                    ownerId = post.ownerId,
                    ownerName = group.name,
                    ownerImageUrl = group.photo50,
                    publicationDate = post.date * 1000,
                    contentText = post.text,
                    isLiked = post.likes.userLikes > 0,
                    content = listContentEntity,
                    haveReposts = if (copyHistory.isNullOrEmpty()) false else true
                )

                result.add(postEnt)
            }
        }

        return result
    }

    fun mapGroupsGetResponseToGroups(response: GroupsGetResponse): List<GroupEntity> {
        val result = mutableListOf<GroupEntity>()

        val items = response.response?.items
        val groups = response.response?.groups

        items?.forEach { group ->
            val groupEntity = GroupEntity(
                groupId = group.id,
                name = group.name,
                screenName = group.screenName,
                avatarUrl = group.photo50,
                lastFetchDate = System.currentTimeMillis()
            )
            result.add(groupEntity)
        }

        groups?.forEach { group ->
            val groupEntity = GroupEntity(
                groupId = group.id,
                name = group.name,
                screenName = group.screenName,
                avatarUrl = group.photo50,
                lastFetchDate = System.currentTimeMillis()
            )
            result.add(groupEntity)
        }

        return result
    }

    fun mapWallGetCommentsResponseToComments(response: WallGetCommentsResponse): List<CommentEntity> {
        val result = mutableListOf<CommentEntity>()

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

    private fun getCommentEntity(comment: ItemResponse, profile: ProfilesResponse): CommentEntity {
        val listContentEntity: MutableList<ContentEntity> = mutableListOf()

        comment.attachments?.forEach { attachment ->
            val content = getContentEntity(attachment)
            if (content != null) {
                listContentEntity.add(content)
            }
        }

        return CommentEntity(
            commentId = comment.commentId,
            ownerId = comment.ownerId,
            ownerName = "${profile.firstName} ${profile.lastName}",
            ownerImageUrl = profile.photo50,
            publicationDate = comment.date,
            contentText = comment.text,
            content = listContentEntity,
            parentStack = comment.parentsStack.firstOrNull()
        )
    }

    private fun getContentEntity(attachment: AttachmentResponse): ContentEntity? {
        var contentId: Long = 0
        var ownerId: Long = 0
        var type: String = ""
        var isLiked: Boolean = false
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
            ContentEntity(
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