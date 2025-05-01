/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.deiovannagroup.books_catalog.views.activities

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import com.deiovannagroup.books_catalog.R
import com.deiovannagroup.books_catalog.utils.setSupportActionBar
import com.deiovannagroup.books_catalog.utils.showToastAndVibrate
import com.deiovannagroup.books_catalog.viewmodel.InsertBookViewModel
import com.deiovannagroup.books_catalog.databinding.ActivityInsertBookBinding
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InsertBookActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityInsertBookBinding.inflate(layoutInflater)
    }
    private val insertBookViewModel: InsertBookViewModel by viewModels()

    //variável utilizada para saber se já foi clicado mais de uma vez
    private var saveEventIsClicked = false

    private var notificationPermission = false
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        notificationPermission = isGranted
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setEdgeToEdgeLayout()
        setSupportActionBar()
        initListeners()
        getNotificationPermission()
    }

    private fun initListeners() {
        binding.edtBookYear.setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_UP) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    insertBook()
                    return@setOnKeyListener true
                }
            }
            return@setOnKeyListener false
        }

        binding.btnSaveBook.setOnClickListener { view ->
            insertBook()

        }
    }

    private fun setEdgeToEdgeLayout() {
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom,
            )
            insets
        }
    }


    private fun insertBook() {
        saveEventIsClicked = true
        if (validateAllFields()) {

            val titleBook = binding.edtBookTitle.text.toString().trim()
            val authorBook = binding.edtBookAuthor.text.toString().trim()
            val yearBook = binding.edtBookYear.text.toString().trim()

            if (insertBookViewModel.insertBook(titleBook, authorBook, yearBook)) {
                showNotification()

                binding.edtBookTitle.text?.clear()
                binding.edtBookAuthor.text?.clear()
                binding.edtBookYear.text?.clear()
                binding.edtBookTitle.requestFocus()
            } else {
                showToastAndVibrate(
                    getString(
                        R.string.error_msg
                    ),
                )
            }
        }

        Handler(Looper.getMainLooper()).postDelayed(
            { saveEventIsClicked = false },
            1000,
        )
    }


    private fun showNotification() {
        if (notificationPermission) {
            val intent = Intent(this, SearchActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE,
            )

            val channelId = getString(R.string.Notification_Channel)
            val notificationId = 1

            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    getString(R.string.Notification_Channel),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = getString(R.string.Notification_Description)
                }
            )

            val builder = NotificationCompat.Builder(
                this,
                channelId,
            )
                .setSmallIcon(R.drawable.transparent_icon_app)
                .setContentTitle(getString(R.string.Notification_Title))
                .setContentText(
                    getString(R.string.Notification_Text_1) +
                            " " +
                            binding.edtBookTitle.text.toString() +
                            " " +
                            getString(R.string.Notification_Text_2)
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(
                    ("android.resource://" + this + "/" + R.raw.recycle).toUri()
                )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)


            with(NotificationManagerCompat.from(this)) {
                if (ActivityCompat.checkSelfPermission(
                        this@InsertBookActivity,
                        Manifest.permission.POST_NOTIFICATIONS,
                    ) != PackageManager.PERMISSION_GRANTED
                ) return@with

                notify(
                    notificationId,
                    builder.build(),
                )
            }
        }
    }


    private fun getNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            notificationPermission = true
            return
        } else if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermission = true
            return
        }

        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

    }


    private fun validateAllFields(): Boolean {
        val isNameValid = validateField(binding.edtBookTitle)
        val isEmailValid = validateField(binding.edtBookAuthor)
        val isPasswordValid = validateField(binding.edtBookYear)

        return isNameValid && isEmailValid && isPasswordValid
    }

    private fun validateField(editText: TextInputEditText): Boolean {
        return if (editText.text.toString().isEmpty()) {
            editText.error = getString(R.string.empty_field_error)
            editText.background = AppCompatResources.getDrawable(
                this,
                R.drawable.layout_border_error,
            )
            editText.setPadding(10)
            false
        } else {
            editText.error = null
            editText.background = AppCompatResources.getDrawable(
                this,
                R.drawable.layout_border,
            )
            editText.setPadding(10)
            true
        }
    }
}