package com.example.meuscontatos

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar

class ContatoDetailActivity : AppCompatActivity() {

    private  var contato : Contato? = null
    private lateinit var btnsave : Button

    companion object{
        private const val CONTATO_DETAIL_EXTRA = "contato.extra.detail"

        //Garantir que quem for abrir a ContatoDetailActivity passe um contato para ser mostrado
        //Context é quem vai me abrir
        fun start(context : Context, contato: Contato?): Intent {
            val intent = Intent(context,ContatoDetailActivity::class.java)
                .apply {
                    putExtra(CONTATO_DETAIL_EXTRA, contato) // Como a data class Contato é um serializable agora, o putExtra aceita
                }
            return intent
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contato_detail)
        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))

        //recuperar o contato da tela anterior                 // caso não venha nenhum contato
        // já foi criada em cima com o lateinit var
        contato = intent.getSerializableExtra(CONTATO_DETAIL_EXTRA) as Contato?

        val edtNome = findViewById<EditText>(R.id.edt_contato_nome)
        val edtCelular = findViewById<EditText>(R.id.edt_contato_cel)
        btnsave = findViewById(R.id.btn_save)

        if (contato != null){
            edtNome.setText(contato!!.nome)
            edtCelular.setText(contato!!.celular)
        }
        btnsave.setOnClickListener {
            val nome = edtNome.text.toString()
            val celular = edtCelular.text.toString()

            if (nome.isNotEmpty() && celular.isNotEmpty()){
                //Veio do Floating Action Button, não tem contato ainda
                if(contato == null){
                    addOrUpdateContato(0,nome, celular,ActionType.CREATE)
                }else{
                    // Assim ele irá pegar o texto atualizado no título e descrição (não fazer task!!.title)
                    addOrUpdateContato(contato!!.id,nome, celular,ActionType.UPDATE)
                }

            }else{
                showMessage(it,"Preencha os campos!")
            }
        }

    }
    // Adicionar ou atualizar um contato
    private fun addOrUpdateContato(id: Int, nome: String, celular : String, actionType: ActionType){
        val contato = Contato(id,nome, celular)
        returnAction(contato,actionType)
    }

    private fun returnAction(contato: Contato, actionType: ActionType){
        val intent = Intent()
            .apply {

                val contatoAction = ContatoAction(contato,actionType.name)
                //a const val que está na MainActivity e qual ação tenho que executar(actionType)
                putExtra(CONTATO_ACTION_RESULT,contatoAction)
            }
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

    private fun showMessage(view: View, message: String){
        Snackbar.make(view,message, Snackbar.LENGTH_SHORT)
            .setAction("Action",null)
            .show()
    }
    // Criar um Menu mas ainda nada acontece se clicar
    //Essa função  é usada para inflar o menu de opções a partir de um arquivo XML
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_contato_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.delete_contato -> {

                if (contato != null){
                    returnAction(contato!!,ActionType.DELETE)
                } else{
                    showMessage(btnsave,"Contato não encontrado")
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}