package com.dubedivine.samples.features.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.dubedivine.samples.R
import javax.inject.Inject

class PokemonAdapter @Inject
constructor() : RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    private var mPokemon: List<String>? = null
    private var mClickListener: ClickListener? = null

    init {
        mPokemon = emptyList<String>()
    }

    fun setPokemon(pokemon: List<String>) {
        this.mPokemon = pokemon
    }

    fun setClickListener(clickListener: ClickListener) {
        mClickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_tag, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = mPokemon!!.get(position)
        holder.mPokemon = pokemon
        holder.nameText?.text = String.format("%s%s", pokemon.substring(0, 1).toUpperCase(),
                pokemon.substring(1))
    }

    override fun getItemCount(): Int {
        return mPokemon!!.size
    }

    interface ClickListener {
        fun onPokemonClick(pokemon: String)
    }

    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mPokemon: String? = null
        @BindView(R.id.tag_name) @JvmField var nameText: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
            itemView.setOnClickListener { if (mClickListener != null) mClickListener?.onPokemonClick(mPokemon as String) }
        }
    }
}
