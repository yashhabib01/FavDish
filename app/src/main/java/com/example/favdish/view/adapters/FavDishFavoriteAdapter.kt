package com.example.favdish.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.favdish.databinding.ItemDishLayoutBinding
import com.example.favdish.model.entities.FavDish
import com.example.favdish.view.activites.MainActivity
import com.example.favdish.view.fragments.AllDishesFragment
import com.example.favdish.view.fragments.FavoriteDishesFragment

class FavDishFavoriteAdapter(private val fragment : Fragment) : RecyclerView.Adapter<FavDishFavoriteAdapter.ViewHolder>() {

    private var dishes : List<FavDish> = listOf()

    class ViewHolder(view :ItemDishLayoutBinding) : RecyclerView.ViewHolder(view.root){
        val ivDishImage = view.ivDishImage
        val ivTitle = view.ivDishTitle

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding :ItemDishLayoutBinding = ItemDishLayoutBinding
            .inflate(LayoutInflater.from(fragment.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]

        Glide.with(fragment)
            .load(dish.image)
            .into(holder.ivDishImage)

        holder.ivTitle.text =  dish.title


        holder.itemView.setOnClickListener{
            Log.e("Adapter", "yes")
            Log.e("Adapter", dish.title)
            if(fragment is FavoriteDishesFragment){
                fragment.disDetails(dish)

            }
        }
    }

    override fun getItemCount(): Int {
      return  dishes.size
    }

    fun dishesList(list : List<FavDish>){
        dishes = list
        notifyDataSetChanged()
    }


}
