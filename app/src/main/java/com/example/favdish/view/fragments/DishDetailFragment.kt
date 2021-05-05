package com.example.favdish.view.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.favdish.R
import com.example.favdish.application.FavDishApplication
import com.example.favdish.databinding.FragmentDishDetailBinding
import com.example.favdish.model.entities.FavDish
import com.example.favdish.utils.Constants
import com.example.favdish.viewModel.FavDishViewModel
import com.example.favdish.viewModel.FavDishViewModelFactory
import java.lang.Exception
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DishDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DishDetailFragment : Fragment() {

    private var mFavDishDetails: FavDish? = null
     private var mBinding : FragmentDishDetailBinding? = null

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share,menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when(item.itemId){
            R.id.action_share_dish -> {
                val type = "text/plain"
                val subject = "Checkout  this dish recipe"
                var extratText = ""
                val shareWith = "Share with"

                mFavDishDetails?.let {
                    var image = ""
                    if(it.imageSource  == Constants.DISH_IMAGE_SOURCE_ONLINE){
                        image = it.image
                    }

                    var cookingInstruction = ""
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        cookingInstruction = Html.fromHtml(
                            it.directionToCook,
                            Html.FROM_HTML_MODE_COMPACT
                        ).toString()
                    }else{
                        @Suppress("DEPRECATION")
                        cookingInstruction = Html.fromHtml(it.directionToCook).toString()
                    }

                    extratText =
                        "$image \n" +
                                "\n Title:  ${it.title} \n\n Type: ${it.type} \n\n Category: ${it.category}" +
                                "\n\n Ingredients: \n ${it.ingredients} \n\n Instructions To Cook: \n $cookingInstruction" +
                                "\n\n Time required to cook the dish approx ${it.cookingTime} minutes."
                }

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = type
                intent.putExtra(Intent.EXTRA_TEXT,extratText)
                intent.putExtra(Intent.EXTRA_TEXT,extratText)
                startActivity(Intent.createChooser(intent,shareWith))

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentDishDetailBinding.inflate(inflater,container,false)



        return mBinding!!.root
    }

    @SuppressLint("StringFormatInvalid")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: DishDetailFragmentArgs by navArgs()

        mFavDishDetails = args.dishDetails


        args.let {
            try{
                Glide.with(requireActivity())
                    .load(it.dishDetails.image)
                    .centerCrop()
                    .listener(object : RequestListener<Drawable>{
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("Tag","Error to load the Image")
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            resource.let {
                                Palette.from(resource!!.toBitmap()).generate(){
                                        palette ->
                                    val intColor = palette?.vibrantSwatch?.rgb ?: 0
                                    mBinding!!.rlDishDetailMain.setBackgroundColor(intColor)

                                }
                            }

                            return false
                        }

                    })
                    .into(mBinding!!.ivDishImage)
            }catch (e : Exception){
                e.printStackTrace()
            }

            mBinding!!.tvTitle.text  = it.dishDetails.title
            mBinding!!.tvType.text  = it.dishDetails.type.capitalize(Locale.ROOT)

            mBinding!!.tvCategory.text  = it.dishDetails.category
            mBinding!!.tvIngredients.text  = it.dishDetails.ingredients
            mBinding!!.tvCookingDirection.text  = it.dishDetails.directionToCook
         //   mBinding!!.tvCookingTime.text  = resources.getString(R.string.lbl_estimate_cooking_time,it.dishDetails.cookingTime)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mBinding!!.tvCookingDirection.text = Html.fromHtml(
                    it.dishDetails.directionToCook,
                    Html.FROM_HTML_MODE_COMPACT
                )
            } else {
                @Suppress("DEPRECATION")
                mBinding!!.tvCookingDirection.text = Html.fromHtml(it.dishDetails.directionToCook)
            }



            if(args.dishDetails.favoriteDish){
                mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_favorite_selected
                ))

            }

            mBinding!!.ivFavoriteDish.setOnClickListener{
                args.dishDetails.favoriteDish  = !args.dishDetails.favoriteDish

                mFavDishViewModel.update(args.dishDetails)

                if(args.dishDetails.favoriteDish){
                    mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    ))

                    Toast.makeText(requireActivity(),resources.getString(R.string.msg_added_to_favorites),Toast.LENGTH_LONG).show()
                }else{
                    mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite
                    ))
                    Toast.makeText(requireActivity(),resources.getString(R.string.msg_removed_to_favorites),Toast.LENGTH_LONG).show()

                }
            }

        }




    }


    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

}