package ru.vaxoid.qualitysurvey


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import ru.vaxoid.qualitysurvey.databinding.WeekItemBinding
import ru.vaxoid.qualitysurvey.db.CollTextAnswerCntInDay
import java.text.SimpleDateFormat
import java.util.*


data class AnswerWeek (val Day: Date, val NegVal:Int, val PosVal:Int, var expandable : Boolean = false, var listType:ArrayList<CollTextAnswerCntInDay>)
val ArrayNumDays: Array<String> = arrayOf("Воскресенье","Понедельник", "Вторник", "Среда","Четверг", "Пятница","Суббота")

class WeekAdapter: RecyclerView.Adapter<WeekAdapter.WeekHolder>() {
    val answerWeekList = ArrayList<AnswerWeek>()
    var adapterLV: MyAdapter? = null

    class WeekHolder(item:View): RecyclerView.ViewHolder(item) {
        val binding = WeekItemBinding.bind(item)
        var linearLayout : ConstraintLayout = item.findViewById(R.id.linearLayout)
        var expendableLayout : RelativeLayout = item.findViewById(R.id.expandable_layout)
        var lvTypes: ListView = item.findViewById(R.id.lv_Types)

        fun bind(answerWeek:AnswerWeek) = with(binding){
            //val numDay = java.util.Date(answerWeek.Day).day
            val numDay = answerWeek.Day.day
            imDay.text = ArrayNumDays[numDay]
            imNegative.text = answerWeek.NegVal.toString()
            imPositive.text = answerWeek.PosVal.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.week_item,parent,false)
        Log.i(TAG,"onCreateViewHolder")
        return WeekHolder(view)
    }


    override fun onBindViewHolder(holder: WeekHolder, position: Int) {
        holder.bind(answerWeekList[position])
        val answer : AnswerWeek = answerWeekList[position]
        val isExpandable: Boolean = answerWeekList[position].expandable
        Log.i(TAG,"onBindViewHolder")
        holder.expendableLayout.visibility = if (isExpandable) View.VISIBLE else View.GONE

        if (isExpandable) {//Заполняем lvTypes
            adapterLV = MyAdapter(holder.lvTypes.context, answer.listType, SimpleDateFormat("yyyy-MM-dd").format(answer.Day))
            holder.lvTypes.adapter = adapterLV

            val dp : Float = (24 * answer.listType.size).toFloat()
            val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,holder.lvTypes.context.resources.displayMetrics)
            holder.lvTypes.layoutParams.height = px.toInt()
        }



        holder.expendableLayout.setOnTouchListener(object : View.OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                Log.i(TAG,"Touch clMain.setOnTouchListener (CHILD-1)")
                var lvTypeItem : ConstraintLayout = v!!.findViewById(R.id.lv_Types_Item)
                lvTypeItem.parent.requestDisallowInterceptTouchEvent(true)
                //findViewById(R.id.child_scroll).getParent().requestDisallowInterceptTouchEvent(false);
                return v.onTouchEvent(event)
            }
        })

        holder.linearLayout.setOnClickListener{
            val version = answerWeekList[position]
            version.expandable = !answer.expandable
            Log.i(TAG,"Clicked holder.linearLayout")
            notifyItemChanged(position)
        }
        holder.expendableLayout.setOnClickListener {
            //Toast.makeText(it.context, answerWeekList[position].Day.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return answerWeekList.size
    }

    fun addAnswerWeek(answerWeek: AnswerWeek){
        answerWeekList.add(answerWeek)
        notifyDataSetChanged()
    }
    fun clearAnswerWeek(){
        answerWeekList.clear()
        notifyDataSetChanged()
    }

}


//Class MyAdapter
class MyAdapter(private val context: Context, private val arrayList: ArrayList<CollTextAnswerCntInDay>, private val _day: String) : BaseAdapter() {

    override fun getCount(): Int {
        return arrayList.size
    }
    override fun getItem(position: Int): Any {
        return position
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView: View

        convertView = LayoutInflater.from(context).inflate(R.layout.week_item_expand_day, parent, false)

        (convertView.findViewById(R.id.tvValue) as TextView).text = arrayList[position].answerTxt
        (convertView.findViewById(R.id.tvCount) as TextView).text = arrayList[position].answerCnt.toString()
        (convertView.findViewById(R.id.textView3) as TextView).text = arrayList[position].answerType.toString()
        if (arrayList[position].answerType == 1) {//Подсветка строки
            convertView.setBackgroundResource(R.color.bgItem_Good)
        }else{
            convertView.setBackgroundResource(R.color.bgItem_Bad)
        }

        convertView.setOnClickListener{
           // Toast.makeText(it.context, arrayList[position].answerTxt, Toast.LENGTH_SHORT).show()
            // создание объекта Intent для запуска SecondActivity
            val intentList :Intent = Intent(this.context, ListAnswerActivity::class.java)
            // передача объекта с ключом "hello" и значением "Hello World"
            intentList.putExtra("AnswerDay", _day)
            intentList.putExtra("AnswerVal", arrayList[position].answerTxt)
            intentList.putExtra("AnswerType", arrayList[position].answerType)
            // запуск SecondActivity
            startActivity(this.context,intentList,null)
        }




        return convertView
    }
}

