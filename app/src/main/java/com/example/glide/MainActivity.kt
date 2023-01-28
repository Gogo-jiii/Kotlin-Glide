package com.example.glide

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.example.glide.databinding.ActivityMainBinding
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.bumptech.glide.request.target.Target

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnLoadImage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                binding.progressBar.visibility = View.VISIBLE
                Glide.with(this@MainActivity)
                    .load(
                        "https://images.unsplash.com/photo-1607252650355-f7fd0460ccdb?ixlib" +
                                "=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto" +
                                "=format&fit=crop&w=750&q=80"
                    )
                    .placeholder(R.drawable.baseline_account_circle)
                    .error(R.drawable.baseline_error)
                    .fitCenter()
                    .centerCrop()
                    .override(500, Target.SIZE_ORIGINAL)
                    .transform(CircleCrop())
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.d("TAG", "Error loading image")
                            binding.progressBar.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.progressBar.visibility = View.GONE
                            return false
                        }
                    })
                    .into(binding.imageView)
            }
        })

        binding.btnClearImageview.setOnClickListener {
            Glide.with(this@MainActivity).clear(binding.imageView)
        }

        binding.btnLoadImageAsBitmap.setOnClickListener {
            val target = Glide.with(this@MainActivity)
                .asBitmap()
                .load(
                    "https://images.unsplash.com/photo-1607252650355-f7fd0460ccdb?ixlib" +
                            "=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto" +
                            "=format&fit=crop&w=750&q=80"
                )
                .submit()

            //background task
            val bitmap = arrayOf<Bitmap?>(null)
            val executorService: ExecutorService = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            executorService.execute {
                try {
                    bitmap[0] = target.get()
                    handler.post { binding.imageView.setImageBitmap(bitmap[0]) }
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }
}