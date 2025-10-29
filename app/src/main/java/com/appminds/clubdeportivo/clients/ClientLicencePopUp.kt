package com.appminds.clubdeportivo.clients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.appminds.clubdeportivo.R

class ClientLicencePopUp : DialogFragment() {
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.popup_client_licence, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        // Aquí puedes configurar botones, texto o interactividad dentro del pop-up
//        view.findViewById<Button>(R.id.btn_cerrar).setOnClickListener {
//            // Lógica para cerrar el pop-up
//            dismiss()
//        }
    }
}