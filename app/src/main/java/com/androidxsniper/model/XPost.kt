package com.androidxsniper.model

data class XPost(
    val id: String,
    val text: String,
    val authorId: String,
    val authorUsername: String,
    val createdAt: String,
    val url: String
)

data class XPostResponse(
    val data: List<XPostData>?,
    val includes: XIncludes?,
    val meta: XMeta?
)

data class XPostData(
    val id: String,
    val text: String,
    val author_id: String,
    val created_at: String
)

data class XIncludes(
    val users: List<XUser>?
)

data class XUser(
    val id: String,
    val username: String,
    val name: String
)

data class XMeta(
    val newest_id: String?,
    val oldest_id: String?,
    val result_count: Int?
)
