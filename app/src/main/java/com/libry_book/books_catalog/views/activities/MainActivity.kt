/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.libry_book.books_catalog.views.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.view.Gravity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat.IntentBuilder
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.libry_book.books_catalog.BuildConfig
import com.libry_book.books_catalog.R
import com.libry_book.books_catalog.gestores.GestorVibrator
import com.libry_book.books_catalog.storage.DatabaseHelper
import com.libry_book.books_catalog.storage.SharedPreferencesTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(), View.OnClickListener,
    RadioGroup.OnCheckedChangeListener {
    //ATRIBUTOS
    private lateinit var rdgSearchBy: RadioGroup
    private lateinit var edtSearch: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        //Configurando o tema
        val preferencesTheme = SharedPreferencesTheme(this)
        preferencesTheme.setAppTheme()
        super.onCreate(savedInstanceState)
        //iniciando a splashscreen
        installSplashScreen()

        setContentView(R.layout.activity_tela_principal)

        //ATRIBUTOS LOCAIS
        val btnRegister = findViewById<Button>(R.id.btnCadastrar)
        val btnSearch = findViewById<Button>(R.id.btnPesquisar)
        rdgSearchBy = findViewById(R.id.rdgPesquisarPor)
        edtSearch = findViewById(R.id.edtPesquisar)

        //EVENTOS
        /*
         * Método para fazer a pesquisa usando o teclado do dispositivo
         * */edtSearch.setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent ->
            /*
             * Ao envocar o evento, será feito uma filtragem capturando somente a ação ACTION_UP.
             * Em seguida, será capturada a tecla enter para chamar a ação de onClick*/
            if (event.action == KeyEvent.ACTION_UP) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    onClick(btnSearch)
                    return@setOnKeyListener true
                }
            }
            false
        }
        btnRegister.setOnClickListener(this)
        btnSearch.setOnClickListener(this)
        rdgSearchBy.setOnCheckedChangeListener(this)
    }

    override fun onClick(v: View) {
        // criação do Intent para iniciar uma nova Activity
        var it: Intent? = null
        val id = v.id // Criar a Intent para a nova Tela Cadastrar
        if (id == R.id.btnCadastrar) {
            it = Intent(this, RegistryActivity::class.java)

            /*
             * Abrir a nova Tela Pesquisar se o campo "edtPesquisar" estiver preenchido.
             * Caso contrário será exibido na tela um Toast pedindo pra preencher o campo vazio.
             * Caso for selecionado o radiobutton "rbPesquisarPorTodos" não será necessário preencher algum campo.
             * */
        } else if (id == R.id.btnPesquisar) { // verificando se algum campo está vazio e o radiobutton não for "rbPesquisarPorTodos"
            if (edtSearch.text.toString() == "" && rdgSearchBy.checkedRadioButtonId != R.id.rbPesquisarPorTodos) {
                GestorVibrator.vibrate(100L, v.context)
                Toast.makeText(this, getString(R.string.FieldEmpty), Toast.LENGTH_LONG).show()
                return
            }

            //Criar a nova Intent para a nova Tela Pesquisar se existir algum dado
            if (DatabaseHelper(this).tableIsExist()) {
                it = Intent(this, SearchActivity::class.java)
                it.putExtra("tipo", rdgSearchBy.checkedRadioButtonId)
                it.putExtra("chave", edtSearch.text.toString())
            } else {
                GestorVibrator.vibrate(100L, this)
                Toast.makeText(this, getString(R.string.FieldNotFound), Toast.LENGTH_LONG).show()
                return
            }
        }
        //Iniciando a nova Intent
        startActivity(it)
    }

    //Preenchimento do menuBar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menubar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> { //Iniciando a nova Tela Impostações
                val it = Intent(this, SettingsActivity::class.java)
                startActivity(it)
                return true
            }

            R.id.menu_feedback -> { // Criação do AlertDialog para cadastrar o e-mail
                val emailDialog = AlertDialog.Builder(this)

                // Adição do icone e o título do email dialog
                emailDialog.setIcon(R.drawable.transparent_icon_app)
                emailDialog.setTitle(getString(R.string.email_title))
                val emailBodyText = EditText(this)

                // Configurando o emailBodyText
                emailBodyText.inputType = InputType.TYPE_CLASS_TEXT
                emailBodyText.isSingleLine = false
                emailBodyText.hint = getString(R.string.email_textHint)
                emailBodyText.setHintTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.colorTextHint,
                        theme
                    )
                )
                emailBodyText.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.colorPrimaryText,
                        theme
                    )
                )
                emailBodyText.gravity = Gravity.START or Gravity.TOP
                emailBodyText.isHorizontalScrollBarEnabled = false

                //Adicionando o emailBodyText ao emailDialog
                emailDialog.setView(emailBodyText)

                //Configurando o botão positivo
                emailDialog.setPositiveButton(getString(R.string.email_btn_send)) { _: DialogInterface?, _: Int ->

                    /*Se o "emailBodyText" não for vazio,
                     * Será criado o cabeçario do e-mail com um título,
                     *  um número random para o código da messagem.
                     * O corpo da mensagem com o "emailBodyText" juntamente com a data "local" do dispositivo*/if (emailBodyText.text.toString() != "") {

                    //geração do número random
                    val random = (Math.random() * 1.0E14 + 1.0E9).toLong()

                    //montando o cabeçario
                    val subject =
                        getString(R.string.email_subject) + "#" + random.toString()

                    // receber a data local
                    val time = localeTime

                    // montando o e-mail e escolher qual app para enviar
                    val shareEmail = IntentBuilder(emailDialog.context)
                        .setType("message/rfc822")
                        .addEmailTo(getString(R.string.developer_email))
                        .setSubject(subject)
                        .setText(
                            """
        ${emailBodyText.text}
        
        ${getString(R.string.email_timegenerated)}$time
        """.trimIndent()
                        )
                        .setChooserTitle(getString(R.string.email_chooseapp))
                    shareEmail.startChooser()
                } // caso o emailBodyText for vazio sera impressa uma mensagem mais uma vibração
                else {
                    GestorVibrator.vibrate(100L, baseContext)
                    Toast.makeText(
                        baseContext,
                        getString(R.string.email_notextinsert),
                        Toast.LENGTH_LONG
                    ).show()
                }
                }.setNegativeButton(getString(R.string.email_btn_cancel), null)
                emailDialog.show()
                return true

                // exibindo a versão do app
            }

            R.id.menu_app_version -> {
                GestorVibrator.vibrate(100L, this)
                Toast.makeText(this, BuildConfig.VERSION_NAME, Toast.LENGTH_LONG).show()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    // método utilizado para escolher o formato da data em base o local do dispositivo
    private val localeTime: String
        get() {
            val dateFormat: SimpleDateFormat =
                if (Locale.getDefault().displayLanguage == "English") {
                    SimpleDateFormat("hh:mm a - MM/dd/yyyy", Locale.getDefault())
                } else {
                    SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
                }
            return dateFormat.format(Calendar.getInstance().time)
        }

    override fun onCheckedChanged(radioGroup: RadioGroup, checkedId: Int) {
        edtSearch.isEnabled = true
        edtSearch.setText("")
        when (checkedId) {
            R.id.rbPesquisarPorAno -> {
                edtSearch.inputType = InputType.TYPE_CLASS_NUMBER
                edtSearch.filters = arrayOf<InputFilter>(LengthFilter(4))
                edtSearch.setHint(R.string.hint_ano)
                edtSearch.contentDescription = getString(R.string.txt_AccessDescriptionYear)
            }

            R.id.rbPesquisarPorAutor -> {
                edtSearch.filters = arrayOf<InputFilter>(LengthFilter(100))
                edtSearch.setHint(R.string.hint_autor)
                edtSearch.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
                edtSearch.contentDescription = getString(R.string.txt_AccessDescriptionAuthor)
            }

            R.id.rbPesquisarPorTitulo -> {
                edtSearch.filters = arrayOf<InputFilter>(LengthFilter(100))
                edtSearch.setHint(R.string.hint_titulo)
                edtSearch.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
                edtSearch.contentDescription = getString(R.string.txt_AccessDescriptionTitle)
            }

            R.id.rbPesquisarPorTodos -> {
                edtSearch.isEnabled = false
                edtSearch.hint = ""
                edtSearch.filters = arrayOf<InputFilter>(LengthFilter(0))
                edtSearch.inputType = InputType.TYPE_NULL
                edtSearch.contentDescription = getString(R.string.txt_AccessDescriptionAll)
            }
        }
    }
}