package jp.tominaga.atsushi.todoaplication

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_edit.*
import org.w3c.dom.Text

class MainActivity : AppCompatActivity(), EditFragment.OnFragmentInteractionListener, DatePickerDialogFragment.OnDateSetListener {

    var isTwoPane: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //スマホタブレットかを判断する
        if(findViewById<FrameLayout>(R.id.contener_detail) != null) isTwoPane = true

        supportFragmentManager.beginTransaction()
            .add(R.id.contener_master,MasterFragment.newInstance(1),FragmentTag.MASTER.toString()).commit()

        fab.setOnClickListener { view ->
            goEditScreen("","", "", false, ModeInEdit.NEW_ENTRY)

        }



    }


    private fun goEditScreen(title : String, deadline :String, taskDetail : String, isComplete : Boolean, mode: ModeInEdit) {
        if (isTwoPane){
            //タブレットの場合
//            var fragmentManager = supportFragmentManager
//            var fragmentTransaction = fragmentManager.beginTransaction()
//            fragmentTransaction.add(R.id.contener_detail,EditFragment.newInstance("1","1"))
//            fragmentTransaction.commit()

            supportFragmentManager.beginTransaction()
                .add(R.id.contener_detail,EditFragment
                    .newInstance(title, deadline, taskDetail, isComplete, mode),
                    FragmentTag.EDIT.toString()).commit()

            return  //タブレットの処理終了
        }

        //スマホの場合(isTwoPaneがfalse,trueの場合はreturnでここまでこない)
        var intent = Intent(this@MainActivity,EditActivity::class.java).apply {
            putExtra(IntentKey.TITLE.name, title)
            putExtra(IntentKey.DEADLINE.name, deadline)
            putExtra(IntentKey.TASK_DETAIL.name, taskDetail)
            putExtra(IntentKey.IS_COMPLETED.name, isComplete)
            putExtra(IntentKey.MODE_IN_EDIT.name, mode)
        }
        startActivity(intent)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.apply {
            findItem(R.id.menu_delete).isVisible = false
            findItem(R.id.menu_edit).isVisible = false
            findItem(R.id.menu_register).isVisible = false

        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        //スマホのEditActivityから戻ってきたらリストの更新
        super.onResume()
        updateTodoList()
    }

    private fun updateTodoList() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contener_master,MasterFragment.newInstance(1),FragmentTag.MASTER.toString()).commit()
    }

    // EditFragment.OnFragmentInteractionListener
    override fun onDatePikerLaunched() {
        DatePickerDialogFragment().show(supportFragmentManager, FragmentTag.DATE_PICKER.toString())
    }



    //EditFragment.OnFragmentInteractionListener
    override fun onDataEdited() {
        //タブレットのリストの更新
        updateTodoList()

    }

    //DatePickerDialogFragment.OnDateSetListener
    override fun onDateSelected(dateString: String) {
        val inputDdateText = findViewById<EditText>(R.id.inputDateText)
        inputDdateText.setText(dateString)
    }
}
