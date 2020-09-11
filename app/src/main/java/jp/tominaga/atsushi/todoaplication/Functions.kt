package jp.tominaga.atsushi.todoaplication

import android.content.Context
import android.widget.Toast

fun makeToask(context: Context, message: String){
    Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
}