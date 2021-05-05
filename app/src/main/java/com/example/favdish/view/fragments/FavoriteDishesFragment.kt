package com.example.favdish.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.favdish.R
import com.example.favdish.application.FavDishApplication
import com.example.favdish.databinding.FragmentAllDishesBinding
import com.example.favdish.databinding.FragmentFavoriteDishesBinding
import com.example.favdish.model.entities.FavDish
import com.example.favdish.view.activites.MainActivity
import com.example.favdish.view.adapters.FavDishAdapter
import com.example.favdish.view.adapters.FavDishFavoriteAdapter
import com.example.favdish.viewModel.DashboardViewModel
import com.example.favdish.viewModel.FavDishViewModel
import com.example.favdish.viewModel.FavDishViewModelFactory

class FavoriteDishesFragment : Fragment() {



    private lateinit var mBinding : FragmentFavoriteDishesBinding

    private val mFAvDishViewModel : FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {


        mBinding  = FragmentFavoriteDishesBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mBinding.ivFavoriteDishRecyclerView.layoutManager = GridLayoutManager(requireActivity(),2)
        val favDishFavoriteAdapter = FavDishFavoriteAdapter(this@FavoriteDishesFragment)
        mBinding.ivFavoriteDishRecyclerView.adapter = favDishFavoriteAdapter

        mFAvDishViewModel.favoriteDishes.observe(viewLifecycleOwner){
            dishes ->
            dishes.let{
                    if(it.isNotEmpty()){
                        for(dish in it){
                            mBinding.ivFavoriteDishRecyclerView.visibility = View.VISIBLE
                            mBinding.ivFavoriteDishText.visibility = View.GONE
                            favDishFavoriteAdapter.dishesList(it)
                        }
                    }else{
                        mBinding.ivFavoriteDishRecyclerView.visibility = View.GONE
                        mBinding.ivFavoriteDishText.visibility = View.VISIBLE

                    }
            }

        }
    }

    fun disDetails(favDish : FavDish){
        findNavController().navigate(FavoriteDishesFragmentDirections.actionFavoriteDishesToDishDetail(favDish))

        if (requireActivity() is MainActivity){
            (activity as MainActivity)!!.hideBottomNavigationView()
        }
    }

    override fun onResume() {
        super.onResume()
        if(requireActivity() is MainActivity){
            (activity as  MainActivity?)?.showBottomNavigationView()
        }
    }


}