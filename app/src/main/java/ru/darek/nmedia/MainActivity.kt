package ru.darek.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dto.Post
import kotlinx.android.synthetic.main.activity_main.*
import ru.darek.nmedia.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_main)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likedByMe = false,
            likes = 999,
            share = 9997,
            views = 32
        )
        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            likeCount.text = getStr(post.likes)
            shareCount.text = getStr(post.share)
            viewsCount.text = getStr(post.views)

            if (post.likedByMe) {
                like?.setImageResource(R.drawable.ic_liked_24)
            }
            likeCount?.text = post.likes.toString()

            root.setOnClickListener {
                Log.d("stuff", "stuff")
            }

            avatar.setOnClickListener {
                Log.d("stuff", "avatar")
            }

            like?.setOnClickListener {
                Log.d("stuff", "like")
                post.likedByMe = !post.likedByMe
                like.setImageResource(
                    if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_like_24
                )
                if (post.likedByMe) post.likes++ else post.likes--
                likeCount?.text = getStr(post.likes)
            }
            share?.setOnClickListener {
                Log.d("stuff", "share")
                post.share++
                shareCount?.text = getStr(post.share)
            }
        }
    }
}
fun getStr(inCnt:Int):String{
    if (inCnt >= 1000000) return String.format("%.2f M",(inCnt/1000000).toFloat())
    if (inCnt >= 10000) return String.format("%d K",inCnt/1000)
    if (inCnt >= 1000) return String.format("%.1f K",(inCnt/1000).toFloat())
    return inCnt.toString()
/*
        1.1К отображается по достижении 1100
        После 10К сотни перестают отображаться
        После 1M сотни тысяч отображаются в формате 1.3M
        если количество перевалило за 999, то должно отображаться 1K и т.д., а не 1000 (при этом предыдущие функции должны работать:
        если у поста было 999 лайков и вы нажали like, то должно стать 1К, если убрали лайк, то снова 999) */
}