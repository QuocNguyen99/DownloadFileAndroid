package com.hqnguyen.weetest.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hqnguyen.weetest.adapter.MediaAdapter
import com.hqnguyen.weetest.data.MediaRepository
import com.hqnguyen.weetest.databinding.ActivityMainBinding
import com.hqnguyen.weetest.network.MediaService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var adapterMedia: MediaAdapter? = null

    lateinit var mediaViewModel: MediaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofitService = MediaService.getInstance()
        val mainRepository = MediaRepository(retrofitService)
        mediaViewModel = ViewModelProvider(
            this,
            MediaViewModelFactory(mainRepository, application)
        )[MediaViewModel::class.java]

        initView()
        initObserve()
    }

    private fun initObserve() {
        mediaViewModel.getMediaListApi()

        mediaViewModel.mediaListResponse.observe(this) {
            if (it.isNotEmpty()) {
                adapterMedia?.setData(it)
            } else {
                binding.tvNoItem.visibility = View.VISIBLE
                binding.rcMedia.visibility = View.GONE
            }
        }

        mediaViewModel.errorMessage.observe(this) {
            Log.d("TAG", "initObserve: $it")
        }
    }

    private fun initView() {
        adapterMedia = MediaAdapter(this)

        binding.rcMedia.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = adapterMedia
        }
    }

}