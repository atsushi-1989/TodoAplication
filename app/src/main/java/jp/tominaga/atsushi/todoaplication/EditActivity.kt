package jp.tominaga.atsushi.todoaplication

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity(),
    EditFragment.OnFragmentInteractionListener,
    DatePickerDialogFragment.OnDateSetListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setSupportActionBar(toolbar)


        //戻るアイコン
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener {
                finish()
            }
        }

        val bundle = intent.extras
        val title = bundle!!.getString(IntentKey.TITLE.name)
        val deadline = bundle.getString(IntentKey.DEADLINE.name)
        val taskDetail = bundle.getString(IntentKey.TASK_DETAIL.name)
        val isCompleted = bundle.getBoolean(IntentKey.IS_COMPLETED.name)
        val mode = bundle.getSerializable(IntentKey.MODE_IN_EDIT.name) as ModeInEdit

        //EditFragmentを開く
        supportFragmentManager.beginTransaction().add(R.id.contener_detail,EditFragment
            .newInstance(title!!, deadline!!, taskDetail!!, isCompleted, mode), FragmentTag.EDIT.toString())
            .commit()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        menu!!.apply {
            findItem(R.id.menu_delete).isVisible = false
            findItem(R.id.menu_edit).isVisible = false
            findItem(R.id.menu_register).isVisible = false
        }
        return true
    }


    //EditFragment.OnFragmentInteractionListener
    override fun onDatePikerLaunched() {
        DatePickerDialogFragment().show(supportFragmentManager, FragmentTag.DATE_PICKER.toString())

    }


    //EditFragment.OnFragmentInteractionListener
    override fun onDataEdited() {
        finish()
    }


    //DatePickerDialogFragment.OnDateSetListener
    override fun onDateSelected(dateString: String) {
       val inputDdateText = findViewById<EditText>(R.id.inputDateText)
       inputDdateText.setText(dateString)
    }


}
