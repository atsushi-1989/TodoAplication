package jp.tominaga.atsushi.todoaplication

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.realm.RealmResults


import jp.tominaga.atsushi.todoaplication.MasterFragment.OnListFragmentInteractionListener


import kotlinx.android.synthetic.main.fragment_master.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyMasterRecyclerViewAdapter(
    private val mValues: RealmResults<TodoModel>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyMasterRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as TodoModel
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onItemClecked(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_master, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.textViewTitle.text = item!!.title
        holder.textViewDeadline.text = MyApplication.appContext.getString(R.string.deadline)+ " : "+ item.deadLine
        //Todo Cardの先頭画像(期限切れの場合とまだの場合でわける
        val changedDeadline = SimpleDateFormat("yyyy/MM/dd").parse(item.deadLine)
        val today = Date()
        if (today >= changedDeadline){
            holder.imageStatus.setImageResource(R.drawable.ic_warning_black_24dp)
        }else{
            holder.imageStatus.setImageResource(R.drawable.ic_work_black_24dp)
        }
        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val textViewTitle : TextView = mView.findViewById(R.id.textViewTitle)
        val textViewDeadline : TextView = mView.findViewById(R.id.textViewDeadline)
        val imageStatus : ImageView = mView.findViewById(R.id.imageView)


    }
}
