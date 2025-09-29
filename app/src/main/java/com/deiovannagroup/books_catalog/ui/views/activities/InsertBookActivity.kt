package com.deiovannagroup.books_catalog.ui.views.activities

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.deiovannagroup.books_catalog.R
import com.deiovannagroup.books_catalog.ui.extensions.setSupportActionBar
import com.deiovannagroup.books_catalog.ui.extensions.showToastAndVibrate
import com.deiovannagroup.books_catalog.ui.viewmodels.InsertBookViewModel
import com.deiovannagroup.books_catalog.ui.viewmodels.InsertBookState
import com.deiovannagroup.books_catalog.databinding.ActivityInsertBookBinding
import com.deiovannagroup.books_catalog.ui.extensions.handleNotificationPermissionRequest
import com.deiovannagroup.books_catalog.ui.extensions.setEdgeToEdgeLayout
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InsertBookActivity : AppCompatActivity() {
    private val binding by lazy { ActivityInsertBookBinding.inflate(layoutInflater) }
    private val insertBookViewModel: InsertBookViewModel by viewModels()

    private var notificationPermission = false
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean -> notificationPermission = isGranted }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setEdgeToEdgeLayout(binding.root, binding.main)
        setSupportActionBar()
        initListeners()
        notificationPermission = handleNotificationPermissionRequest(requestPermissionLauncher)
        observeViewModelState()
    }

    private fun initListeners() {
        binding.edtBookYear.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                insertBook()
                return@setOnKeyListener true
            }
            false
        }
        binding.btnSaveBook.setOnClickListener { insertBook() }
    }

    private fun insertBook() {
        if (validateAllFields()) {
            val titleBook = binding.edtBookTitle.text.toString().trim()
            val authorBook = binding.edtBookAuthor.text.toString().trim()
            val yearBook = binding.edtBookYear.text.toString().trim()
            insertBookViewModel.insertBook(
                titleBook,
                authorBook,
                yearBook,
            )
        }
    }

    private fun observeViewModelState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                insertBookViewModel.insertBookState.collect { state ->
                    when (state) {
                        is InsertBookState.Initial -> {
                            binding.apply {
                                btnSaveBook.visibility = View.VISIBLE
                                edtBookTitle.visibility = View.VISIBLE
                                edtBookAuthor.visibility = View.VISIBLE
                                edtBookYear.visibility = View.VISIBLE
                                insertBookProgressBar.visibility = View.GONE
                            }
                        }

                        is InsertBookState.Loading -> {
                            binding.apply {
                                btnSaveBook.visibility = View.GONE
                                edtBookTitle.visibility = View.GONE
                                edtBookAuthor.visibility = View.GONE
                                edtBookYear.visibility = View.GONE
                                insertBookProgressBar.visibility = View.VISIBLE
                            }
                        }

                        is InsertBookState.Success -> {
                            handleSuccess()
                            insertBookViewModel.resetState()
                        }

                        is InsertBookState.Error -> {
                            handleError()
                            insertBookViewModel.resetState()
                        }
                    }
                }
            }
        }
    }

    private fun handleSuccess() {
        showNotification()
        binding.apply {
            edtBookTitle.text?.clear()
            edtBookAuthor.text?.clear()
            edtBookYear.text?.clear()
            edtBookTitle.requestFocus()
        }
    }

    private fun handleError() = showToastAndVibrate(getString(R.string.error_msg))


    private fun showNotification() {
        if (notificationPermission) {
            val intent = Intent(this, SearchActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val channelId = getString(R.string.Notification_Channel)
            val notificationId = 1
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    getString(R.string.Notification_Channel),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = getString(R.string.Notification_Description)
                })
            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.transparent_icon_app)
                .setContentTitle(getString(R.string.Notification_Title)).setContentText(
                    getString(R.string.Notification_Text_1) + " " + binding.edtBookTitle.text.toString() + " " + getString(
                        R.string.Notification_Text_2
                    )
                ).setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(("android.resource://" + this + "/" + R.raw.recycle).toUri())
                .setContentIntent(pendingIntent).setAutoCancel(true)

            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            NotificationManagerCompat.from(this).notify(notificationId, builder.build())
        }
    }

    private fun validateAllFields(): Boolean {
        val isTitleValid = validateField(binding.layoutBookTitle)
        val isAuthorValid = validateField(binding.layoutBookAuthor)
        val isYearValid = validateField(binding.layoutBookYear)
        return isTitleValid && isAuthorValid && isYearValid
    }

    private fun validateField(textLayout: TextInputLayout): Boolean {
        return if (textLayout.editText?.text.toString().isEmpty()) {
            textLayout.error = getString(R.string.empty_field_error)
            false
        } else {
            textLayout.error = null
            true
        }
    }
}