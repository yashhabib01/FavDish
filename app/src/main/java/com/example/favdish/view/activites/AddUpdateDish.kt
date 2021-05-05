package com.example.favdish.view.activites

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.favdish.R
import com.example.favdish.databinding.ActivityAddUpdateDishBinding
import com.example.favdish.databinding.DialogCustomImageSelectionBinding
import com.karumi.dexter.Dexter
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Camera
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.favdish.application.FavDishApplication
import com.example.favdish.databinding.DialogCustomListBinding
import com.example.favdish.model.entities.FavDish
import com.example.favdish.utils.Constants
import com.example.favdish.view.adapters.CustomListItemAdapter
import com.example.favdish.viewModel.FavDishViewModel
import com.example.favdish.viewModel.FavDishViewModelFactory
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.util.*

class AddUpdateDish : AppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityAddUpdateDishBinding
    private var mImagePath : String = "";
    private lateinit var  mCustomListDialog: Dialog
    private var mFavDishDetails:FavDish? = null

    private val mFavDishViewModel : FavDishViewModel by viewModels{
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }


        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)



            if(intent.hasExtra(Constants.EXTRA_DISH_DETAIL)){
                mFavDishDetails = intent.getParcelableExtra(Constants.EXTRA_DISH_DETAIL)
            }


            setupActionBar()

            mFavDishDetails?.let {
                if(it.id != 0){
                    mImagePath = it.image
                    Glide.with(this@AddUpdateDish)
                        .load(mImagePath)
                        .centerCrop()
                        .into(mBinding.ivDishImage)

                    mBinding.etTitle.setText(it.title)
                    mBinding.etType.setText(it.type)
                    mBinding.etCategory.setText(it.category)
                    mBinding.etIngredients.setText(it.ingredients)
                    mBinding.etCookingTime.setText(it.cookingTime)
                    mBinding.etDirectionToCook.setText(it.directionToCook)

                    mBinding.btnAddDish.text  = resources.getString(R.string.lbl_update_dishes)
                }
            }


        mBinding.ivAddDishImage.setOnClickListener(this)
        mBinding.etType.setOnClickListener(this)
        mBinding.etCategory.setOnClickListener(this)
        mBinding.etCookingTime.setOnClickListener(this)
        mBinding.btnAddDish.setOnClickListener(this)


    }



    private fun setupActionBar() {
        setSupportActionBar(mBinding.toolbarAddDishActivity)

        if(mFavDishDetails != null && mFavDishDetails!!.id != 0){
            supportActionBar?.let {
                it.title  = resources.getString(R.string.title_edit_dishes)
            }
        }else{
            supportActionBar?.let {
                it.title  = resources.getString(R.string.title_add_dish)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mBinding.toolbarAddDishActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
     if(v != null){
         when(v.id){
            R.id.iv_add_dish_image->{
                customImageSelectDialog()
                return

            }
             R.id.et_type->{
                 customsItemsDialog(resources.getString(R.string.type_selected_dish_type),
                 Constants.disTypes(),Constants.DISH_TYPE)
                 return
             }
             R.id.et_category->{
                 customsItemsDialog(resources.getString(R.string.type_selected_dish_category),
                     Constants.disCategories(),Constants.DISH_CATEGORY)
                 return
             }
             R.id.et_cooking_time->{
                 customsItemsDialog(resources.getString(R.string.type_selected_dish_time),
                     Constants.dishCookTime(),Constants.DISH_COOKING_TIME)
                 return
             }
             R.id.btn_add_dish -> {
                 val title = mBinding.etTitle.text.toString().trim { it <= ' ' }
                 val type = mBinding.etType.text.toString().trim { it <= ' ' }
                 val category = mBinding.etCategory.text.toString().trim { it <= ' ' }
                 val ingredients = mBinding.etIngredients.text.toString().trim { it <= ' ' }
                 val cookingTimeInMinute = mBinding.etCookingTime.text.toString().trim { it <= ' ' }
                 val cookingDirection =
                     mBinding.etDirectionToCook.text.toString().trim { it <= ' ' }

                 when {
                     TextUtils.isEmpty(mImagePath) -> {
                         Toast.makeText(this@AddUpdateDish, "", Toast.LENGTH_SHORT).show()
                     }
                     TextUtils.isEmpty(title) -> {
                         Toast.makeText(this@AddUpdateDish, "Title Field is Empty", Toast.LENGTH_SHORT).show()
                     }
                     TextUtils.isEmpty(type) -> {
                         Toast.makeText(this@AddUpdateDish, "Type Field is Empty", Toast.LENGTH_SHORT).show()
                     }
                     TextUtils.isEmpty(category) -> {
                         Toast.makeText(this@AddUpdateDish, "Category Field is Empty", Toast.LENGTH_SHORT).show()
                     }
                     TextUtils.isEmpty(ingredients) -> {
                         Toast.makeText(this@AddUpdateDish, "Ingredients Field is Empty", Toast.LENGTH_SHORT).show()
                     }
                     TextUtils.isEmpty(cookingTimeInMinute) -> {
                         Toast.makeText(this@AddUpdateDish, "Cooking Time Field is Empty", Toast.LENGTH_SHORT).show()
                     }
                     TextUtils.isEmpty(cookingDirection) -> {
                         Toast.makeText(this@AddUpdateDish, "Cooking Direction Field is Empty", Toast.LENGTH_SHORT).show()
                     }
                     else -> {

                         var dishId = 0
                         var imageSource  = Constants.DISH_IMAGE_SOURCE_LOCAL
                         var favoriteDish = false

                         mFavDishDetails?.let {
                             if(it.id != 0){
                                 dishId = it.id
                                 imageSource = it.imageSource
                                 favoriteDish = it.favoriteDish
                             }
                         }


                         val favDishDetails : FavDish = FavDish(
                             mImagePath,
                             imageSource,
                             title,
                             type,
                             category,
                             ingredients,
                             cookingTimeInMinute,
                             cookingDirection,
                             favoriteDish,
                             dishId
                         )


                         if(dishId == 0){
                             mFavDishViewModel.insert(favDishDetails)
                             Toast.makeText(this,"Data Added ",Toast.LENGTH_SHORT).show()
                         }else{
                             mFavDishViewModel.update(favDishDetails)
                             Toast.makeText(this,"Data Updated ",Toast.LENGTH_SHORT).show()
                         }
                         finish()

                     }
                 }
             }
         }

     }

    }

    private fun customImageSelectDialog(){
        val dialog = Dialog(this)
        val binding  : DialogCustomImageSelectionBinding = DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root);
        binding.tvCamera.setOnClickListener{
           Dexter.withContext(this).withPermissions(
               Manifest.permission.READ_EXTERNAL_STORAGE,
        //       Manifest.permission.WRITE_EXTERNAL_STORAGE,
               Manifest.permission.CAMERA
           ).withListener(object : MultiplePermissionsListener {
               override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                   report?.let {
                       if(report.areAllPermissionsGranted()){
                           Log.i("Permission" , "Camera")
                       val intent =  Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                           startActivityForResult(intent,CAMERA)

                       }
                   }

               }

               override fun onPermissionRationaleShouldBeShown(
                   p0: MutableList<PermissionRequest>?,
                   p1: PermissionToken?
               ) {
                   showRationalDialogPermission()
               }

           }).onSameThread().check()

            dialog.dismiss()
        }
        binding.tvGallery.setOnClickListener {
           Dexter.withContext(this@AddUpdateDish).withPermission(
               Manifest.permission.READ_EXTERNAL_STORAGE,
               //       Manifest.permission.WRITE_EXTERNAL_STORAGE,
           ).withListener(object : PermissionListener {
               override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                   val galleryInent = Intent(Intent.ACTION_PICK,
                   MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                   startActivityForResult(galleryInent, GALLERY)
               }

               override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                   Toast.makeText(this@AddUpdateDish,"You Have Denied Camera Permission Now" , Toast.LENGTH_SHORT).show()

               }

               override fun onPermissionRationaleShouldBeShown(
                   p0: PermissionRequest?,
                   p1: PermissionToken?
               ) {
                   showRationalDialogPermission()
               }


           }).onSameThread().check()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun selectedListItem(item:String, selection:String){
        when(selection){
            Constants.DISH_TYPE->{
                mCustomListDialog.dismiss()
                mBinding.etType.setText(item)
            }
            Constants.DISH_CATEGORY->{
                mCustomListDialog.dismiss()
                mBinding.etCategory.setText(item)
            }
            Constants.DISH_COOKING_TIME->{
                mCustomListDialog.dismiss()
                mBinding.etCookingTime.setText(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if(requestCode  == CAMERA){
                data?.extras?.let {
                    val thumbnail : Bitmap = data.extras!!.get("data") as Bitmap
                //    mBinding.ivDishImage.setImageBitmap(thumbnail)
                    Glide.with(this)
                        .load(thumbnail)
                        .centerCrop()
                        .into(mBinding.ivDishImage)

                    mImagePath = saveImageToInternalStorage(thumbnail)
                    Log.i("imagePath", mImagePath)
                    mBinding.ivAddDishImage.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit))

                }

            }

            if(requestCode  == GALLERY){
                data?.let {
                  val selected = data.data
                 //   mBinding.ivDishImage.setImageURI(selected)
                    Glide.with(this)
                        .load(selected)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable>{
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {

                                Log.e("error","something went wrong")
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                               resource?.let {
                                   val bitmap : Bitmap = resource.toBitmap()
                                   mImagePath = saveImageToInternalStorage(bitmap)
                                   Log.i("ImagePath", mImagePath)

                               }
                                return false
                            }

                        })
                        .into(mBinding.ivDishImage)
                    mBinding.ivAddDishImage.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit))

                }

            }


        }else if (resultCode == Activity.RESULT_CANCELED){
            Log.e("cancelled" , "User cancelled User Selection")
        }
    }

    private fun showRationalDialogPermission(){
        Log.i("Print","something")
        AlertDialog.Builder(this@AddUpdateDish).setMessage("Permissions Denied They are required")
            .setPositiveButton("Go To Settings")
            {_,_ ->
                try{
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e : Exception){
                    e.printStackTrace()
                }

            }
            .setNegativeButton("Cancel"){dialog,_ ->
                dialog.dismiss()
            }.show()

    }
    private fun saveImageToInternalStorage(bitmap: Bitmap):String{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY,Context.MODE_PRIVATE)
        file =File(file,"${UUID.randomUUID()}.jpg")

        try {
            val stream : OutputStream = FileOutputStream( file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e: Exception){
            e.printStackTrace()
        }

        return file.absolutePath
    }

    private fun customsItemsDialog(title:String,itemsList:List<String>,selection : String ){
         mCustomListDialog = Dialog(this)
        val binding : DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = title
        binding.rvList.layoutManager  = LinearLayoutManager(this)
        val adapter  = CustomListItemAdapter(this,null,itemsList,selection)
        binding.rvList.adapter = adapter
        mCustomListDialog.show()
    }
    companion object{
        private const val CAMERA = 1
        private const val GALLERY = 2
        private const val IMAGE_DIRECTORY = "FavDishImages"
    }
}