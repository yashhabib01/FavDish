package com.example.favdish.view.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.favdish.R
import com.example.favdish.application.FavDishApplication
import com.example.favdish.databinding.FragmentRandomDishBinding
import com.example.favdish.model.entities.FavDish
import com.example.favdish.model.entities.RandomDish
import com.example.favdish.utils.Constants
import com.example.favdish.viewModel.FavDishViewModel
import com.example.favdish.viewModel.FavDishViewModelFactory
import com.example.favdish.viewModel.NotificationsViewModel
import com.example.favdish.viewModel.RandomDishViewModel

class RandomDishFragment : Fragment() {

    private lateinit var  mRandomDishViewModel: RandomDishViewModel

    private lateinit var notificationsViewModel: NotificationsViewModel

    private var mProgressBar: Dialog?= null

    private  var mBinding:FragmentRandomDishBinding? = null
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentRandomDishBinding.inflate(inflater,container,false)

        return mBinding!!.root
    }

    private fun showCustomProgressDialog(){
        mProgressBar = Dialog(requireActivity())
        mProgressBar?.let {
            it.setContentView(R.layout.dialog_custom_progress)
            it.show()
        }
    }

    private fun hideProgressDialog(){
        mProgressBar?.let {
            it.dismiss()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRandomDishViewModel = ViewModelProvider(this).get(RandomDishViewModel::class.java)
        mRandomDishViewModel.getRandomRecipeFromApi()

        randomDishViewModeObserver()


        mBinding!!.srlRandomDish.setOnRefreshListener {
            mRandomDishViewModel.getRandomRecipeFromApi()
        }
    }

    private  fun randomDishViewModeObserver(){
        mRandomDishViewModel.randomDishResponse.observe(viewLifecycleOwner,
            {randomDishResponse ->
                randomDishResponse?.let {
                    Log.i("Random Dish Response" , "${randomDishResponse.recipes[0]}")
                    if(mBinding!!.srlRandomDish.isRefreshing){
                        mBinding!!.srlRandomDish.isRefreshing = false
                    }

                    setRandomDishResponseUi(randomDishResponse.recipes[0])
                }
            }
            )
        mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner,{
            dataError->
            dataError?.let {
                Log.i("Data Error", "$dataError")
                if(mBinding!!.srlRandomDish.isRefreshing){
                    mBinding!!.srlRandomDish.isRefreshing = false
                }
            }
        })

        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner,
            {
                loadData->
                loadData?.let {
                    Log.i("Random Dish Loading" , "$loadData")

                    if(loadData && !mBinding!!.srlRandomDish.isRefreshing){
                        showCustomProgressDialog()
                    }else{
                        hideProgressDialog()
                    }
                }
            })
    }


    @SuppressLint("StringFormatInvalid")
    private fun  setRandomDishResponseUi(recipe: RandomDish.Recipe){
        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(mBinding!!.ivDishImage)

        mBinding!!.tvTitle.text = recipe.title

        var dishType:String = "other"
        if(recipe.dishTypes.isNotEmpty()){
            dishType = recipe.dishTypes[0]
            mBinding!!.tvType.text = dishType
        }

        mBinding!!.tvCategory.text = "Other"

        var ingredient = ""
        for(value in recipe.extendedIngredients){
            if(ingredient.isEmpty()){
                ingredient = value.original
            }else{
                ingredient = ingredient + ", \n"  + value.original
            }
        }
        mBinding!!.tvIngredients.text = ingredient

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            mBinding!!.tvCookingDirection.text = Html.fromHtml(
                recipe.instructions,
                Html.FROM_HTML_MODE_COMPACT
            )
        }else{
            @Suppress("DEPRECATION")
            mBinding!!.tvCookingDirection.text = Html.fromHtml(recipe.instructions)
        }

        mBinding!!.ivFavoriteDish.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_favorite
            )
        )
        var addedToFavorite = false

        mBinding!!.tvCookingTime.text  =
            resources.getString(
                R.string.lbl_estimate_cooking_time,
                recipe.readyInMinutes.toString()
            )

        mBinding!!.ivFavoriteDish.setOnClickListener{

            if(addedToFavorite){
                Toast.makeText(requireActivity(),resources.getString(R.string.msg_already_added_to_favorite), Toast.LENGTH_SHORT).show()

            }else{
                val randomDishDetails = FavDish(
                    recipe.image,
                    Constants.DISH_IMAGE_SOURCE_ONLINE,
                    recipe.title,
                    dishType,
                    "Other",
                    ingredient,
                    recipe.readyInMinutes.toString(),
                    recipe.instructions,
                    true
                )

                val mFavDishViewModel:FavDishViewModel by viewModels{
                    FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)

                }
                mFavDishViewModel.insert(randomDishDetails)

                addedToFavorite = true

                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )
                Toast.makeText(requireActivity(), resources.getString(R.string.msg_added_to_favorites), Toast.LENGTH_LONG).show()
            }



        }





    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

    
}

