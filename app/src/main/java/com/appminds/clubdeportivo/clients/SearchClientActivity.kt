package com.appminds.clubdeportivo.clients

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.data.dao.ClientDao
import com.appminds.clubdeportivo.models.ClientDto
import com.appminds.clubdeportivo.models.enums.ClientStatusEnum
import com.appminds.clubdeportivo.models.enums.ClientTypeEnum

class SearchClientActivity : AppCompatActivity() {
    private lateinit var clientDao: ClientDao

    // Views
    private lateinit var searchInput: EditText
    private lateinit var searchBtn: AppCompatButton
    private lateinit var tlClientData: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_client)

        clientDao = ClientDao(this)

        initViews()
        setUpListeners()
    }

    private fun initViews() {
        searchInput = findViewById(R.id.inputSearchClient)
        searchBtn = findViewById(R.id.btnSearchClient)
        tlClientData = findViewById(R.id.tlClientData)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun setUpListeners() {
        val textWatcher: (Editable?) -> Unit = { searchBtn.isEnabled = searchInput.text.toString().trim().isNotEmpty() }
        searchInput.addTextChangedListener(afterTextChanged = textWatcher)


        searchBtn.setOnClickListener { showDataClient() }
    }

    private fun showDataClient() {
        val client = clientDao.getClientByDNI(searchInput.text.toString().trim())

        if(client == null) {
            tlClientData.isVisible = false
            Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        findViewById<TextView>(R.id.tvClientId).text = client.id.toString()
        findViewById<TextView>(R.id.tvClientName).text = "${client.firstname} ${client.lastname}"
        findViewById<TextView>(R.id.tvClientType).text = client.type.toString()
        findViewById<TextView>(R.id.tvClientStatus).text = client.status.toString()
        tlClientData.isVisible = true
    }
}