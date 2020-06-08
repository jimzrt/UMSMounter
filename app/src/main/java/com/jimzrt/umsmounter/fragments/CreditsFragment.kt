package com.jimzrt.umsmounter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jimzrt.umsmounter.BuildConfig
import com.jimzrt.umsmounter.R

class CreditsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (activity != null) {
            ((activity as AppCompatActivity?)!!.supportActionBar)?.title = "Credits"
        }
        val view = inflater.inflate(R.layout.fragment_credits, container, false)
        val versionView = view.findViewById<TextView>(R.id.versionView)
        val librariesView = view.findViewById<TextView>(R.id.librariesView)
        val ackView = view.findViewById<TextView>(R.id.ackView)
        val sourceView = view.findViewById<TextView>(R.id.sourceView)
        versionView.text = "Version: " + BuildConfig.VERSION_NAME + "-" + BuildConfig.BUILD_TYPE
        librariesView.text = """
            libsu - https://github.com/topjohnwu/libsu
            google-gson - https://github.com/google/gson
            Fetch - https://github.com/tonyofrancis/Fetch

            """.trimIndent()
        ackView.text = """
            DriveDroid
            https://softwarebakery.com/projects/drivedroid
            Main inspiration
            """.trimIndent()
        sourceView.text = "https://github.com/jimzrt/UMSMounter"
        return view
    }
}