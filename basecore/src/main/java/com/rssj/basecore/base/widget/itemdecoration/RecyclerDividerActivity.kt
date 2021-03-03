//package com.chainspower.itemdecoration
//
//import android.showContentView.Context
//import android.graphics.Color
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.core.showContentView.ContextCompat
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.chainspower.mytab.R
//import com.qs.qsnetwork.mvvm_base_lib.utils.itemdecoration.Divider
//import com.qs.qsnetwork.mvvm_base_lib.utils.itemdecoration.SideLine
//import kotlinx.android.synthetic.main.activity_recycler_divider.*
//
//class RecyclerDividerActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_recycler_divider)
//        mRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
//        mRecyclerView.adapter = MyAdapter(this)
//        mRecyclerView.addItemDecoration(object : ItemDivider(this) {
//            override fun getDivider(position: Int): Divider {
//                return when (position) {
//                    0 -> DividerBuilder()
//                        .setLeftSideLine(SideLine(true, Color.RED, ContextCompat.getDrawable(this@RecyclerDividerActivity,
//                            R.drawable.item_h_divider), 10f, -10f, -6f))
//                        .setRightSideline(SideLine(true, Color.GREEN, ContextCompat.getDrawable(this@RecyclerDividerActivity,
//                            R.drawable.item_h_divider), 10f, -10f, -6f))
//                        .setTopSideLine(SideLine(true, Color.GRAY, ContextCompat.getDrawable(this@RecyclerDividerActivity,
//                            R.drawable.item_divider), 10f, 0f, 0f))
//                        .setBottomSideLine(SideLine(true, Color.GRAY, ContextCompat.getDrawable(this@RecyclerDividerActivity,
//                            R.drawable.item_divider), 5f, 0f, 0f))
//                        .create()
//                    mRecyclerView.adapter!!.itemCount - 1 -> DividerBuilder()
//                        .setLeftSideLine(SideLine(true, Color.RED, ContextCompat.getDrawable(this@RecyclerDividerActivity,
//                            R.drawable.item_h_divider), 10f, -6f, -10f))
//                        .setRightSideline(SideLine(true, Color.GREEN, ContextCompat.getDrawable(this@RecyclerDividerActivity,
//                            R.drawable.item_h_divider), 10f, -6f, -10f))
//                        .setTopSideLine(SideLine(true, Color.GRAY, ContextCompat.getDrawable(this@RecyclerDividerActivity,
//                            R.drawable.item_divider), 5f, 0f, 0f))
//                        .setBottomSideLine(SideLine(true, Color.GRAY, ContextCompat.getDrawable(this@RecyclerDividerActivity,
//                            R.drawable.item_divider), 10f, 0f, 0f))
//                        .create()
//                    else -> DividerBuilder()
//                        .setLeftSideLine(SideLine(true, Color.RED, ContextCompat.getDrawable(this@RecyclerDividerActivity,
//                            R.drawable.item_h_divider), 10f, -6f, -6f))
//                        .setRightSideline(SideLine(true, Color.GREEN, ContextCompat.getDrawable(this@RecyclerDividerActivity,
//                            R.drawable.item_h_divider), 10f, -6f, -6f))
//                        .setTopSideLine(SideLine(true, Color.GRAY, ContextCompat.getDrawable(this@RecyclerDividerActivity,
//                            R.drawable.item_divider),
//                            5f, 0f, 0f))
//                        .setBottomSideLine(SideLine(true, Color.GRAY, ContextCompat.getDrawable(this@RecyclerDividerActivity,
//                            R.drawable.item_divider), 5f, 0f, 0f))
//                        .create()
//                }
//
//
//            }
//
//        })
//    }
//}
//
//class MyAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.item_recycler, parent, false)
//        return MyViewHolder(view)
//    }
//
//    override fun getItemCount() = 20
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val mViewHolder = holder as MyViewHolder
//        mViewHolder.mTvText.text = "Position is $position"
//    }
//
//    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val mTvText = itemView.findViewById<TextView>(R.id.mTvItem)
//    }
//
//}
