package com.hqnguyen.weetest.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hqnguyen.weetest.R
import com.hqnguyen.weetest.data.Media
import com.hqnguyen.weetest.data.MediaResponse
import com.hqnguyen.weetest.data.StatusDownload
import com.hqnguyen.weetest.data.TypeMedia
import com.hqnguyen.weetest.databinding.ItemMediaBinding

class MediaAdapter(val context: Context) : RecyclerView.Adapter<MediaAdapter.ViewHolder>() {

    private var listMedia: MutableList<Media> = mutableListOf()

    inner class ViewHolder(private val binding: ItemMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(media: Media) {
            binding.tvItem.text = media.fileName
            Glide.with(context).load(media.url).placeholder(R.drawable.ic_downloading).into(binding.imgItem)
            if (media.type == TypeMedia.VIDEO_MP3 ) {
                binding.progress.visibility = View.VISIBLE
            }

            if ( media.downloaded == StatusDownload.SUCCESS){
                binding.progress.visibility = View.GONE
            }else if (media.downloaded == StatusDownload.FAIL){
                binding.progress.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMediaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(listMedia[position])
    }

    override fun getItemCount(): Int {
        return if (listMedia.size == null) 0 else listMedia.size
    }

    fun setData(newListMedia: List<Media>) {
        listMedia = newListMedia as MutableList<Media>
    }
}