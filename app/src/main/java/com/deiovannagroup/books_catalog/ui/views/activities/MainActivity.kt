/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.deiovannagroup.books_catalog.ui.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.deiovannagroup.books_catalog.BuildConfig
import com.deiovannagroup.books_catalog.R
import com.deiovannagroup.books_catalog.databinding.ActivityMainBinding
import com.deiovannagroup.books_catalog.domain.services.email_service.EmailService
import com.deiovannagroup.books_catalog.shared.utils.setEdgeToEdgeLayout
import com.deiovannagroup.books_catalog.shared.utils.showToastAndVibrate
import com.deiovannagroup.books_catalog.ui.fragments.EmailFeedbackDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setEdgeToEdgeLayout(binding.root, binding.main)
        initMenuBar()
        initListeners()
        setupFragmentResultListener()
    }

    private fun setupFragmentResultListener() {
        supportFragmentManager.setFragmentResultListener(
            REQUEST_KEY_EMAIL_FEEDBACK, this
        ) { requestKey, bundle ->
            val feedbackText = bundle.getString(EmailFeedbackDialogFragment.RESULT_FEEDBACK_TEXT)

            if (feedbackText != null) sendFeedbackEmail(feedbackText)
        }
    }

    private fun initListeners() {
        binding.btnInsert.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    InsertBookActivity::class.java,
                )
            )
        }

        binding.btnSearch.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SearchActivity::class.java,
                )
            )
        }


    }

    private fun initMenuBar() {
        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menubar, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.menu_settings -> {
                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    SettingsActivity::class.java,
                                )
                            )
                            true
                        }

                        R.id.menu_feedback -> {
                            EmailFeedbackDialogFragment.newInstance(
                                REQUEST_KEY_EMAIL_FEEDBACK,
                            )
                                .show(
                                    supportFragmentManager,
                                    "EmailFeedbackDialog",
                                )
                            true
                        }

                        R.id.menu_app_version -> {
                            showToastAndVibrate(BuildConfig.VERSION_NAME)
                            true
                        }

                        else -> false
                    }
                }
            },
            this, Lifecycle.State.RESUMED,
        )
    }

    private fun sendFeedbackEmail(feedbackText: String) {
        if (feedbackText.isNotBlank()) {
            val random = (Math.random() * 1.0E14 + 1.0E9).toLong()
            val subjectTemplate = application.getString(R.string.email_subject)
            val subject = "$subjectTemplate#$random"
            val timeGeneratedTemplate = application.getString(R.string.email_timegenerated)
            val time = getLocaleTime()

            EmailService.sendEmail(
                context = this,
                emailBody = "$feedbackText\n$timeGeneratedTemplate$time".trimIndent(),
                emailSubject = subject,
            )
        }
    }

    private fun getLocaleTime(): String {
        val userLocale = Locale.getDefault()
        val dateFormat: SimpleDateFormat = if (userLocale.language == Locale.ENGLISH.language) {
            SimpleDateFormat("hh:mm a - MM/dd/yyyy", userLocale)
        } else {
            SimpleDateFormat("HH:mm - dd/MM/yyyy", userLocale)
        }
        return dateFormat.format(Calendar.getInstance(userLocale).time)
    }

    companion object {
        const val REQUEST_KEY_EMAIL_FEEDBACK = "request_key_email_feedback"
    }
}