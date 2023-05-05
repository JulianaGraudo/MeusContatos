package com.example.meuscontatos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ContatoAdapter(private val openContatoDetail: (contato: Contato) -> Unit) :ListAdapter<Contato,ContatoViewHolder>(ContatoAdapter) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatoViewHolder {
        val view : View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_contato,parent,false)
        return ContatoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContatoViewHolder, position: Int) {
        val contato = getItem(position)
        holder.bind(contato,openContatoDetail)
    }
    companion object : DiffUtil.ItemCallback<Contato>() {
        override fun areItemsTheSame(oldItem: Contato, newItem: Contato): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Contato, newItem: Contato): Boolean {
            return oldItem.nome == newItem.nome &&
                    oldItem.celular == newItem.celular
        }
    }
}

class ContatoViewHolder(private val view: View) : RecyclerView.ViewHolder(view){
    private val nomeContato = view.findViewById<TextView>(R.id.tv_nome_contato)
    private val celularContato = view.findViewById<TextView>(R.id.tv_celular_contato)

    fun bind(contato: Contato, openContatoDetail: (contato: Contato) -> Unit){
        nomeContato.text = contato.nome
        celularContato.text = contato.celular

        view.setOnClickListener {
            openContatoDetail.invoke(contato)
        }
    }
}