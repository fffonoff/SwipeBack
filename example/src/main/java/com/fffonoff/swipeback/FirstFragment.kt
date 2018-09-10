package com.fffonoff.swipeback

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_first.*


class FirstFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btn.setOnClickListener { v ->
            fragmentManager?.let {
                try {
                    val edgeOnly = edgeOnly.isChecked
                    val edgeSize = edgeSize.text.toString().toFloat()
                    val percentToRelease = percentToRelease.text.toString().toFloat()
                    val fragment = SecondFragment.newInstance(edgeOnly, edgeSize, percentToRelease)
                    SwipeBackHelper.createTransactionWithAnimation(it)
                            .add(R.id.fragment_container, fragment, SecondFragment.FRAGMENT_TAG)
                            .addToBackStack(SecondFragment.FRAGMENT_TAG)
                            .commit()

                    hideKeyboard(activity)
                } catch (e: NumberFormatException) {
                    Toast.makeText(v.context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun hideKeyboard(activity: Activity?) {
        val inputMethodManager = activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.let {
            val view = activity!!.currentFocus ?: View(activity)
            it.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    companion object {
        const val FRAGMENT_TAG = "orange"
    }
}
