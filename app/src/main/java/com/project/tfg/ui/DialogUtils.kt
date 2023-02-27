package com.project.tfg.ui

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.project.tfg.R

class DialogUtils {
        fun showAlertDialog(context: Context, title: String, message: String,
                            positiveButtonClickListener: DialogInterface.OnClickListener?,
                            negativeButtonClickListener: DialogInterface.OnClickListener?,
                            neutralButtonClickListener: DialogInterface.OnClickListener?) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.texto_dialog_opcion_galeria, positiveButtonClickListener)
                .setNegativeButton(R.string.texto_dialog_opcion_camara, negativeButtonClickListener)
                .setNeutralButton(R.string.texto_dialog_opcion_cancelar, neutralButtonClickListener)
            val alertDialog = builder.create()
            alertDialog.show()
        }

}