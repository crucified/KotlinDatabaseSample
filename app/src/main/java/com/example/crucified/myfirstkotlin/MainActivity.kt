package com.example.crucified.myfirstkotlin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.crucified.myfirstkotlin.database.Art
import com.example.crucified.myfirstkotlin.database.Database
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val db by lazy { Database(applicationContext) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_art, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        val names = db.artNames()
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, names)

        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedName = names[position]
            val art = db.artDetails(selectedName)
            if (art == null) {
                showAlert("Invalid activity. Some error occurred")
            } else {
                goToArtActivity(art)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.addArtItem -> {
                goToArtActivity(null)
            }
        }


        return super.onOptionsItemSelected(item)
    }

    private fun goToArtActivity(art: Art?) {
        val intent = Intent(this, AddArtActivity::class.java)
        if (art != null) {
            intent.putExtra(AddArtActivity.IS_MUTABLE, false)
            AddArtActivity.selectedArt = art
        } else {
            intent.putExtra(AddArtActivity.IS_MUTABLE, true)
        }
        startActivity(intent)
    }

}

fun android.app.Activity.showAlert(text: String, title: String = "Error") {
    AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(text)
            .setNeutralButton("Okay", null)
            .show()
}