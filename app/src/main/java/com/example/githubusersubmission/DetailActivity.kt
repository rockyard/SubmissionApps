package com.example.githubusersubmission

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.githubusersubmission.adapter.ViewPagerDetailAdapter
import com.example.githubusersubmission.data.DataUsers
import com.example.githubusersubmission.data.Favorite
import com.example.githubusersubmission.db.DatabaseContract.FavColumns.Companion.AVATAR
import com.example.githubusersubmission.db.DatabaseContract.FavColumns.Companion.COMPANY
import com.example.githubusersubmission.db.DatabaseContract.FavColumns.Companion.FAVORITE
import com.example.githubusersubmission.db.DatabaseContract.FavColumns.Companion.LOCATION
import com.example.githubusersubmission.db.DatabaseContract.FavColumns.Companion.NAME
import com.example.githubusersubmission.db.DatabaseContract.FavColumns.Companion.REPOSITORY
import com.example.githubusersubmission.db.DatabaseContract.FavColumns.Companion.USERNAME
import com.example.githubusersubmission.db.FavoriteHelper
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.item_row_git.username

class DetailActivity : AppCompatActivity() , View.OnClickListener{

    companion object{
        const val EXTRA_DATA = "extra_data"
        const val EXTRA_FAV = "extra_data"
        const val EXTRA_NOTE = "extra_note"
        const val EXTRA_POSITION = "extra_position"
    }

    private var isFavorite = false
    private lateinit var gitHelper: FavoriteHelper
    private var favorite: Favorite? = null
    private lateinit var imgAvatar : String


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        gitHelper = FavoriteHelper.getInstance(applicationContext)
        gitHelper.open()

        favorite = intent.getParcelableExtra(EXTRA_NOTE)
        if (favorite != null){
            setDataObject()
            isFavorite = true
            val checked: Int = R.drawable.ic_baseline_favorite_24
            btn_favorite.setImageResource(checked)
        }else {
            setData()
        }

        viewPagerConfig()
        btn_favorite.setOnClickListener(this)

    }

    private fun viewPagerConfig(){
        val viewPagerDetailAdapter = ViewPagerDetailAdapter(this, supportFragmentManager)
        view_pager.adapter = viewPagerDetailAdapter
        tabs.setupWithViewPager(view_pager)

        supportActionBar?.elevation = 0f
    }

    private fun setActionBarTitle(title:String){
        if (supportActionBar != null){
            this.title =title
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(){
        val dataUser = intent.getParcelableExtra<DataUsers>(EXTRA_DATA) as DataUsers
        dataUser.name?.let { setActionBarTitle(it) }
        name.text = dataUser.name
        username.text = "(" + dataUser.username.toString() + ")"
        company.text = getString(R.string.company, dataUser.company)
        location.text = getString(R.string.location, dataUser.location)
        repo.text = getString(R.string.repositories, dataUser.repository)
        followerss.text = dataUser.followers.toString()
        followings.text = dataUser.following.toString()
        Glide.with(this)
                .load(dataUser.avatar)
                .into(avatars)
        imgAvatar = dataUser.avatar.toString()
    }

    @SuppressLint("SetText18n")
    private fun setDataObject(){
        val favoriteUser = intent.getParcelableExtra<Favorite>(EXTRA_NOTE) as Favorite
        favoriteUser.name?.let { setActionBarTitle(it) }
        name.text = favoriteUser.name
        username.text = favoriteUser.username
        company.text = favoriteUser.company
        location.text = favoriteUser.location
        repo.text = favoriteUser.repository
        Glide.with(this)
            .load(favoriteUser.avatar)
            .into(avatars)
        imgAvatar = favoriteUser.avatar.toString()
    }


    override fun onClick(view: View) {
        val checked: Int = R.drawable.ic_baseline_favorite_24
        val unchecked : Int = R.drawable.ic_baseline_favorite_border_24
        if (view.id == R.id.btn_favorite){
            if (isFavorite){
                gitHelper.deleteById(favorite?.username.toString())
                Toast.makeText(this, getString(R.string.delete_favorite),Toast.LENGTH_SHORT).show()
                btn_favorite.setImageResource(unchecked)
                isFavorite = false

            } else {
                val dataUsername = username.text.toString()
                val dataName = name.text.toString()
                val dataAvatar = imgAvatar
                val dataCompany = company.text.toString()
                val dataLocation = location.text.toString()
                val dataRepository = repo.text.toString()
                val dataFavorite = "1"

                val values = ContentValues()
                values.put(USERNAME, dataUsername)
                values.put(NAME, dataName)
                values.put(AVATAR, dataAvatar)
                values.put(COMPANY, dataCompany)
                values.put(LOCATION, dataLocation)
                values.put(REPOSITORY, dataRepository)
                values.put(FAVORITE, dataFavorite)

                isFavorite = true


            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gitHelper.close()
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_change_setting -> {
                val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(mIntent)
            }
             R.id.action_change_notification -> {
                val mIntent = Intent(this, NotificationSettings::class.java)
                startActivity(mIntent)
            }
            R.id.action_favorite -> {
                val mIntent = Intent(this, UserFavorite::class.java)
                startActivity(mIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}