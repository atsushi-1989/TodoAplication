package jp.tominaga.atsushi.todoaplication

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_edit.*
import java.lang.RuntimeException
import java.text.ParseException
import java.text.SimpleDateFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private val ARG_title = IntentKey.TITLE
private val ARG_deadline = IntentKey.DEADLINE
private val ARG_taskDetail = IntentKey.TASK_DETAIL
private val ARG_isCompleted = IntentKey.IS_COMPLETED
private val ARG_mode = IntentKey.MODE_IN_EDIT



/**
 * A simple [Fragment] subclass.
 * Use the [EditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var title: String? = null
    private var deadline: String? = null
    private var taskDetail: String? = null
    private var isCompleted: Boolean? = false
    private var mode: ModeInEdit? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_title.name)
            deadline = it.getString(ARG_deadline.name)
            taskDetail = it.getString(ARG_taskDetail.name)
            isCompleted = it.getBoolean(ARG_isCompleted.name)
            mode = it.getSerializable(ARG_mode.name) as ModeInEdit


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        updateUi(mode!!)
        imageButton.setOnClickListener {
            mListener!!.onDatePikerLaunched()
        }
    }

    private fun updateUi(mode: ModeInEdit) {
        when(mode){
            ModeInEdit.NEW_ENTRY ->{
                checkBox.visibility = View.INVISIBLE
            }
            ModeInEdit.EDIT ->{
                inputTitleText.setText(title)
                inputDateText.setText(deadline)
                inputDetailText.setText(taskDetail)
                if (isCompleted!!) checkBox.isChecked = true else checkBox.isChecked = false
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu!!.apply {
            findItem(R.id.menu_delete).isVisible = false
            findItem(R.id.menu_edit).isVisible = false
            findItem(R.id.menu_register).isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item!!.itemId == R.id.menu_register) recordToRealmDB(mode)

        return super.onOptionsItemSelected(item)

    }

    private fun recordToRealmDB(mode: ModeInEdit?) {
        //タイトルと期日両方がセットされていないと登録できない
        val isRequiredItemsFilled = isRequireFilledCheck()
        if (!isRequiredItemsFilled) return

        when(mode){
            ModeInEdit.NEW_ENTRY -> addNewTodo()
            ModeInEdit.EDIT -> editExistingTodo()
        }

        mListener?.onDataEdited()
        requireFragmentManager()!!.beginTransaction().remove(this).commit()

    }

    private fun isRequireFilledCheck(): Boolean {
        if(inputTitleText.text.toString() == ""){
            inputTitle.error = getString(R.string.error)
            return false
        }

        if(!inputDateCheck(inputDateText.text.toString())){
            inputDate.error = getString(R.string.error)
            return false
        }

//        if (inputDateText.text.toString() == ""){
//            inputDate.error = getString(R.string.error)
//            return false
//        }
        return true
    }

    private fun inputDateCheck(inputDate: String): Boolean {
        if(inputDate == "")return false
        try{
            val format = SimpleDateFormat("yyyy/MM/dd")
            format.isLenient = false
            format.parse(inputDate)
        }catch (e: ParseException){
            return false
        }
        return true

    }

    private fun editExistingTodo() {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val selectedTodo = realm.where(TodoModel::class.java)
            .equalTo(TodoModel::title.name,title)
            .equalTo(TodoModel::deadLine.name, deadline)
            .equalTo(TodoModel::taskDetail.name,taskDetail)
            .findFirst()

        selectedTodo?.apply {
            title = inputTitleText.text.toString()
            deadLine = inputDateText.text.toString()
            taskDetail = inputDetailText.text.toString()
            isConpleted = if (checkBox.isChecked) true else false
        }
        realm.commitTransaction()

        realm.close()

    }

    private fun addNewTodo() {
        //新しい項目をDBに登録
        val realm  = Realm.getDefaultInstance()
        realm.beginTransaction()
        val newTodo = realm.createObject(TodoModel::class.java)
        newTodo.apply {
            title = inputTitleText.text.toString()
            deadLine = inputDateText.text.toString()
            taskDetail = inputDetailText.text.toString()
            isCompleted = if(checkBox.isChecked) true else false
        }
        realm.commitTransaction()

        realm.close()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener){
            mListener = context
        }else{
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }



    interface OnFragmentInteractionListener{
        fun onDatePikerLaunched()
        fun onDataEdited()
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditFragment.
         */
        // TODO: Rename and gchange types and number of parameters
        @JvmStatic
        fun newInstance(title: String, deadline: String, taskDetail: String, isCompleted : Boolean, mode : ModeInEdit) =
            EditFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_title.name, title)
                    putString(ARG_deadline.name, deadline)
                    putString(ARG_taskDetail.name,taskDetail)
                    putBoolean(ARG_isCompleted.name,isCompleted)
                    putSerializable(ARG_mode.name,mode)
                }
            }
    }
}
