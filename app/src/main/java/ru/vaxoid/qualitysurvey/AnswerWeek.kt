package ru.vaxoid.qualitysurvey

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.vaxoid.qualitysurvey.databinding.WeekItemBinding
import java.util.*

data class AnswerWeek (val Day: Date, val NegVal:Int, val PosVal:Int)
val ArrayNumDays: Array<String> = arrayOf("Воскресенье","Понедельник", "Вторник", "Среда","Четверг", "Пятница","Суббота")

class WeekAdapter: RecyclerView.Adapter<WeekAdapter.WeekHolder>() {
    val answerWeekList = ArrayList<AnswerWeek>()

    class WeekHolder(item:View): RecyclerView.ViewHolder(item) {
        val binding = WeekItemBinding.bind(item)
        fun bind(answerWeek:AnswerWeek) = with(binding){
            //val numDay = java.util.Date(answerWeek.Day).day
            val numDay = answerWeek.Day.day
            imDay.text = ArrayNumDays[numDay]
            imNegative.text = answerWeek.NegVal.toString()
            imPositive.text = answerWeek.PosVal.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.week_item,parent,false)
        return WeekHolder(view)
    }

    override fun onBindViewHolder(holder: WeekHolder, position: Int) {
        holder.bind(answerWeekList[position])
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