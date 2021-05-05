package com.example.favdish.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.favdish.R
import com.example.favdish.application.FavDishApplication
import com.example.favdish.databinding.DialogCustomListBinding
import com.example.favdish.databinding.FragmentAllDishesBinding
import com.example.favdish.model.database.FavDishRepository
import com.example.favdish.model.entities.FavDish
import com.example.favdish.utils.Constants
import com.example.favdish.view.activites.AddUpdateDish
import com.example.favdish.view.activites.MainActivity
import com.example.favdish.view.adapters.CustomListItemAdapter
import com.example.favdish.view.adapters.FavDishAdapter
import com.example.favdish.viewModel.FavDishViewModel
import com.example.favdish.viewModel.FavDishViewModelFactory
import com.example.favdish.viewModel.HomeViewModel

class AllDishesFragment : Fragment() {

    private lateinit var mBinding : FragmentAllDishesBinding

    private  val  mFavDishViewModel1 : FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    private lateinit var  mFavDishAdapter:FavDishAdapter
    private lateinit var mCustomListDilaog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(),2)
        mFavDishAdapter =  FavDishAdapter(this@AllDishesFragment)


        mBinding.rvDishesList.adapter = mFavDishAdapter

        mFavDishViewModel1.allDishesList.observe(viewLifecycleOwner){
                dishes ->
            dishes.let {
                if(it.isNotEmpty()){
                    mBinding.rvDishesList.visibility = View.VISIBLE
                    mBinding.tvNoDishesAddedYet.visibility = View.GONE
                    mFavDishAdapter.dishesList(it)
                }else{
                    mBinding.rvDishesList.visibility = View.GONE
                    mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                }
            }

        }
    }

    fun  dishDetails(favDish: FavDish){
        findNavController().navigate(AllDishesFragmentDirections.actionAllDishesToDishDetail(favDish))

            if(requireActivity() is MainActivity){
                (activity as  MainActivity?)?.hideBottomNavigationView()
            }
    }

    fun deleteDish(favDish: FavDish){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.title_delete_dish))
        builder.setMessage(resources.getString(R.string.msg_delete_dish_dialog,favDish.title))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.lbl_yes)){ dialogInterface,_ ->
            mFavDishViewModel1.delete(favDish)
            dialogInterface.dismiss()
        }

        builder.setNegativeButton(resources.getString(R.string.lbl_no)){dialogInterface,_->
            dialogInterface.dismiss()

        }

        val  alertDialog : AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()



    }



    override fun onResume() {
        super.onResume()
        if(requireActivity() is MainActivity){
            (activity as  MainActivity?)?.showBottomNavigationView()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
      mBinding  = FragmentAllDishesBinding.inflate(inflater,container,false);

        return mBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when(item.itemId){
                R.id.action_add -> {
                    startActivity(Intent(requireActivity(),AddUpdateDish::class.java))
               return true
                }
                R.id.action_filter_dishes -> {
                    filterDishes()
                    return true
                }
            }

       
        return super.onOptionsItemSelected(item)
    }

    fun filterSelection(filterItemSelection:String){
        mCustomListDilaog.dismiss()
        if(filterItemSelection == Constants.ALL_ITEMS){
            mFavDishViewModel1.allDishesList.observe(viewLifecycleOwner){
                    dishes ->
                dishes.let {
                    if(it.isNotEmpty()){
                        mBinding.rvDishesList.visibility = View.VISIBLE
                        mBinding.tvNoDishesAddedYet.visibility = View.GONE
                        mFavDishAdapter.dishesList(it)
                    }else{
                        mBinding.rvDishesList.visibility = View.GONE
                        mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                    }
                }
            }
        }else{
            mFavDishViewModel1.getFilterList(filterItemSelection).observe(viewLifecycleOwner){
                dishes->
                dishes.let {
                    if(it.isNotEmpty()){
                        mBinding.rvDishesList.visibility = View.VISIBLE
                        mBinding.tvNoDishesAddedYet.visibility = View.GONE
                        mFavDishAdapter.dishesList(it)
                    }else{
                        mBinding.rvDishesList.visibility = View.GONE
                        mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun filterDishes(){
         mCustomListDilaog = Dialog(requireActivity())
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDilaog.setContentView(binding.root)
        binding.tvTitle.text = resources.getString(R.string.title_Select_item_to_filter)
        val dishTypes =  Constants.disTypes()
        dishTypes.add(0,Constants.ALL_ITEMS)
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = CustomListItemAdapter(requireActivity(),this@AllDishesFragment,dishTypes,Constants.FILTER_SELECTION)

        binding.rvList.adapter  = adapter
        mCustomListDilaog.show()
    }
}