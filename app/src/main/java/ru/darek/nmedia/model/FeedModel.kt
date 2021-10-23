package ru.darek.nmedia.model

import ru.darek.nmedia.dto.Post
/*
data class FeedModel(
    val posts: List<Post> = emptyList(),
    val loading: Boolean = false,
    val error: Boolean = false,
    val empty: Boolean = false,
    val refreshing: Boolean = false,
) */


data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false,
)


data class FeedModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
)
