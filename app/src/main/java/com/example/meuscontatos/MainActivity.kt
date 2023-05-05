package com.example.meuscontatos

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    private val adapter: ContatoAdapter = ContatoAdapter(::onListItemClicked)

    //Criar a base de dados(Tabela)
    private val dataBase by lazy {
        Room.databaseBuilder(
            applicationContext, AppDataBase::class.java, "contatos-database"
        ).build()
    }

    // Criar o DAO
    private val dao by lazy {
        dataBase.contatoDao()
    }

    // iniciar uma activity e obter um resultado de retorno
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                //Recuperar a ação que vai ser feita dentro do contato
                val data = result.data
                //Pegar resultado dentro do data
                val contatoAction = data?.getSerializableExtra(CONTATO_ACTION_RESULT) as ContatoAction
                val contato: Contato = contatoAction.contato

                when (contatoAction.actionType) {
                    ActionType.DELETE.name -> deleteById(contato.id)

                    ActionType.CREATE.name -> insertIntoDatabase(contato)

                    ActionType.UPDATE.name -> updateIntoDatabase(contato)

                }

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        listFromDataBase()

        val rvContato = findViewById<RecyclerView>(R.id.rv_contatos)

        rvContato.adapter = adapter

        /*val c1 = Contato(0,"Juliana Graúdo","(32)9 8869-6782")
        val c2 = Contato(1,"Lucas Chevitarese","(32)9 5643-4595")
        val c3 = Contato(2,"Letícia Graúdo","(32)9 5456-8767")

        val listaContatos = listOf<Contato>(c1,c2,c3)
        adapter.submitList(listaContatos)
        */

        val fab = findViewById<FloatingActionButton>(R.id.fab_add)
        fab.setOnClickListener {
            // null pois não há contato, vai ser criado ainda
            openContatoDetail(null)
        }

    }

    private fun insertIntoDatabase(contato: Contato){
        CoroutineScope(IO).launch {
            dao.insert(contato)
            listFromDataBase()
        }
    }

    private fun updateIntoDatabase(contato: Contato){
        CoroutineScope(IO).launch {
            dao.update(contato)
            listFromDataBase()
        }
    }

    private fun deleteById(id : Int){
        CoroutineScope(IO).launch{
            dao.deleteById(id)
            listFromDataBase()
        }
    }

    private fun deleteAll(){
        CoroutineScope(IO).launch{
            dao.deleteAll()
            listFromDataBase()
        }
    }
    private fun listFromDataBase(){
        CoroutineScope(IO).launch{
            val myDataBaseList : List<Contato> = dao.getAll()
            adapter.submitList(myDataBaseList)
        }
    }

    private fun openContatoDetail(contato: Contato?) {
        val intent = ContatoDetailActivity.start(this, contato)
        // Dar start na Activity pedindo um resultado
        startForResult.launch(intent)

    }

    // Abrir próxima tela, a ContatoDetailActivity, quando já tem uma tarefa
    private fun onListItemClicked(contato: Contato) {
        openTaskListDetail(contato)
    }

    private fun openTaskListDetail(contato: Contato?) {
        val intent = ContatoDetailActivity.start(this, contato)
        // Dar start na Activity pedindo um resultado
        startForResult.launch(intent)
    }

    // Criar um Menu mas ainda nada acontece se clicar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_lista_contato, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.delete_all_contatos -> {
                //Deletar todas as tarefas
                deleteAll()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}

enum class ActionType {
    DELETE,
    UPDATE,
    CREATE
}

// Vai segurar o contato
// Serializable pois vai passar de uma tela pra outra
data class ContatoAction(val contato: Contato, val actionType: String) : Serializable

// Está aqui pois a Main Activity que é a responsável por fazer a ação
const val CONTATO_ACTION_RESULT = "CONTATO_ACTION_RESULT"

