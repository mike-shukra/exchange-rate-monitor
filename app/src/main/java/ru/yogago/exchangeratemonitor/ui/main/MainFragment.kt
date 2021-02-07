package ru.yogago.exchangeratemonitor.ui.main

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.yogago.exchangeratemonitor.MyService
import ru.yogago.exchangeratemonitor.R


class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv)

        recyclerView.layoutManager = LinearLayoutManager(context)

        val serviceClass = MyService::class.java
        val intent = Intent(context, serviceClass)

        viewModel.course.observe(viewLifecycleOwner,{
            recyclerView.adapter = CustomRecyclerAdapter(it)
        })

        viewModel.data.observe(viewLifecycleOwner,{
            Toast
                .makeText(context, it.s, Toast.LENGTH_SHORT)
                .show()
        })

        viewModel.go()


    }

}