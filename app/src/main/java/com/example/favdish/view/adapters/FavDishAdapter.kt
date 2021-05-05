package com.example.favdish.view.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.favdish.R
import com.example.favdish.application.FavDishApplication
import com.example.favdish.databinding.ItemDishLayoutBinding
import com.example.favdish.model.entities.FavDish
import com.example.favdish.utils.Constants
import com.example.favdish.view.activites.AddUpdateDish
import com.example.favdish.view.fragments.AllDishesFragment
import com.example.favdish.viewModel.FavDishViewModel
import com.example.favdish.viewModel.FavDishViewModelFactory

class FavDishAdapter(private val fragment: Fragment): RecyclerView.Adapter<FavDishAdapter.ViewHolder>() {

    private var dishes: List<FavDish> = listOf()






    class  ViewHolder(view :ItemDishLayoutBinding) : RecyclerView.ViewHolder(view.root){
        val ivDishImage = view.ivDishImage
        val ivTitle = view.ivDishTitle
        val ibMore = view.ibMore
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding : ItemDishLayoutBinding = ItemDishLayoutBinding
            .inflate(LayoutInflater.from(fragment.context),parent,false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val dish  = dishes[position]
        Glide.with(fragment)
            .load(dish.image)
            .into(holder.ivDishImage)

        holder.ivTitle.text =  dish.title





        holder.itemView.setOnClickListener{
            Log.e("Adapter", "yes")
            Log.e("Adapter", dish.title)
            if(fragment is AllDishesFragment){
                fragment.dishDetails(dish)

            }
        }

        holder.ibMore.setOnClickListener{
          val popUp  =  PopupMenu(fragment.context,holder.ibMore)
            popUp.menuInflater.inflate(R.menu.menu_adapter,popUp.menu)


            popUp.setOnMenuItemClickListener {
                if(it.itemId == R.id.action_edit_dish){

                val intent = Intent(fragment.requireActivity() , AddUpdateDish::class.java)
                    intent.putExtra(Constants.EXTRA_DISH_DETAIL,dish)
                    fragment.requireActivity().startActivity(intent)

                    }else if(it.itemId == R.id.action_delete_dish){

                        if(fragment is AllDishesFragment){
                            fragment.deleteDish(dish)

                        }

                }
                true
            }
            popUp.show()
        }

        if(fragment is AllDishesFragment){
            holder.ibMore.visibility  = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    fun dishesList(list : List<FavDish>){
        dishes = list
        notifyDataSetChanged()
    }

}