package com.example.apppractica2.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.apppractica2.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

private const val ARG_VIDEO_URL = "video_url"
private const val YT_PARAM_V = "v="
private const val YT_PARAM_SHORT = "youtu.be/"
private const val YT_PARAM_EMBED = "embed/"

class AutosVideoFragment : Fragment() {

    private var videoUrl: String? = null
    private var youtubePlayerView: YouTubePlayerView? = null
    private var videoDuration = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            videoUrl = it.getString(ARG_VIDEO_URL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_autos_video, container, false)
        youtubePlayerView = view.findViewById(R.id.ytVideo)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        youtubePlayerView = view.findViewById(R.id.ytVideo)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)

        // Botón para regresar
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        youtubePlayerView?.let { ytView ->
            lifecycle.addObserver(ytView)

            val videoId = extractYoutubeVideoId(videoUrl ?: "")
            if (videoId != null) {
                ytView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(videoId, 0f)
                    }

                    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                        videoDuration = duration
                    }

                    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                        if (videoDuration > 0 && second >= videoDuration - 0.5f) {
                            parentFragmentManager.popBackStack()
                        }
                    }
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        youtubePlayerView?.release()
        youtubePlayerView = null
    }

    companion object {
        fun newInstance(videoUrl: String) = AutosVideoFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_VIDEO_URL, videoUrl)
            }
        }

        fun extractYoutubeVideoId(url: String): String? {
            return when {
                YT_PARAM_V in url -> url.substringAfter(YT_PARAM_V).substringBefore("&")
                YT_PARAM_SHORT in url -> url.substringAfter(YT_PARAM_SHORT).substringBefore("?")
                YT_PARAM_EMBED in url -> url.substringAfter(YT_PARAM_EMBED).substringBefore("?")
                else -> null
            }
        }
    }
}
