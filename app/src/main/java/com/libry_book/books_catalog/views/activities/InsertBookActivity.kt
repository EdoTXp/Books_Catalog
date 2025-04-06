/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.libry_book.books_catalog.views.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.libry_book.books_catalog.R
import com.libry_book.books_catalog.services.app_services.VibratorService
import com.libry_book.books_catalog.storage.SharedPreferencesTheme
import com.libry_book.books_catalog.viewmodel.InsertBookViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InsertBookActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var edtTitleBook: EditText
    private lateinit var edtAuthorBook: EditText
    private lateinit var edtYearBook: EditText
    private lateinit var btnSave: Button
    private lateinit var insertBookViewModel: InsertBookViewModel
    private lateinit var sharedPreferencesTheme: SharedPreferencesTheme

    //variável utilizada para saber se já foi clicado mais de uma vez
    private var saveEventIsClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_book)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.title_activity_tela_cadastrar)

        init()
    }

    private fun init() {
        sharedPreferencesTheme = SharedPreferencesTheme(this)
        sharedPreferencesTheme.setAppTheme()

        //Binding
        btnSave = findViewById(R.id.btnSalvar)
        edtTitleBook = findViewById(R.id.edtTitulo)
        edtAuthorBook = findViewById(R.id.edtAutor)
        edtYearBook = findViewById(R.id.edtAno)
        insertBookViewModel = ViewModelProvider(this)[InsertBookViewModel::class.java]

        //EVENTOS
        /*
         * Método para fazer o cadastro usando o teclado do dispositivo
         *
         * */
        edtYearBook.setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent ->
            /*
             * Ao envocar o evento, será feito uma filtragem capturando somente a ação ACTION_UP.
             * Em seguida, será capturada a tecla enter para chamar a ação de onClick*/
            if (event.action == KeyEvent.ACTION_UP) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    onClick(btnSave)
                    return@setOnKeyListener true
                }
            }
            return@setOnKeyListener false
        }

        btnSave.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (view.id == btnSave.id && !saveEventIsClicked) {
            //clickEventIsClicked vira true
            saveEventIsClicked = true

            /* removendo os espaços brancos ao final da string
             e caso a string tiver só espaços brancos, a verificação de string vazias
             nas instruções abaixo dara um valor false */
            edtTitleBook.setText(edtTitleBook.text.toString().trim { it <= ' ' })
            edtAuthorBook.setText(edtAuthorBook.text.toString().trim { it <= ' ' })

            //Valores para saber se o campo respectivo estiver vazio
            val title = insertBookViewModel.checkEditText(edtTitleBook)
            val author = insertBookViewModel.checkEditText(edtAuthorBook)
            val year = insertBookViewModel.checkEditText(edtYearBook)

            if (title && author && year) {
                val titleBook = edtTitleBook.text.toString()
                val authorBook = edtAuthorBook.text.toString()
                val yearBook = edtYearBook.text.toString()
                if (insertBookViewModel.insertBook(titleBook, authorBook, yearBook)) {
                    //imprimir a notificação
                    // printNotification()
                    insertBookViewModel.pushNotification(
                        this,
                        "Livro inserido",
                        getString(R.string.Notification_Text_1) + " \"" + edtTitleBook.text.toString() + "\" " + getString(
                            R.string.Notification_Text_2
                        )
                    )

                    // limpando os campos e passando o foco ao edtTitleBook
                    edtTitleBook.setText("")
                    edtAuthorBook.setText("")
                    edtYearBook.setText("")
                    edtTitleBook.requestFocus()
                } else {
                    val msg = getString(R.string.error_msg)
                    VibratorService.vibrate(
                        view.context,
                        100L,
                    )
                    Toast.makeText(
                        this,
                        msg,
                        Toast.LENGTH_LONG,
                    ).show()
                }
            } else {
                VibratorService.vibrate(
                    view.context,
                    100L,
                )
            }

            // handler utilizado para dar um delay ao evento de clicar para previnir multiplos cliques
            Handler(Looper.getMainLooper()).postDelayed({ saveEventIsClicked = false }, 1000)
        }
    }

    /*  private fun printNotification() {

          // TODO Correge l'errore della notifica quando usa Android 13

          val bodyTextNotification =
              getString(R.string.Notification_Text_1) + " \"" + edtTitleBook.text.toString() + "\" " + getString(
                  R.string.Notification_Text_2
              )
          val notification = GestorNotification(
              this,
              R.drawable.transparent_icon_app,
              getString(R.string.Notification_Title),
              bodyTextNotification,
              2
          )
          notification.setColor(R.color.colorPrimary)
          notification.setDurationVibrate(longArrayOf(0L, 200L, 150L, 200L))
          notification.setSound(
              Uri.parse(
                  "android.resource://"
                          + baseContext.packageName
                          + "/"
                          + R.raw.recycle
              )
          )
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              notification.createChannelId(
                  getString(R.string.Notification_Channel),
                  getString(R.string.Notification_Description),
                  getSystemService(NotificationManager::class.java)
              )
          }
          notification.printNotification()
      }*/
}