package com.fffonoff.swipeback

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_second.*


class SecondFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let { args ->
            if (args.containsKey(EDGE_ONLY)) {
                swipeBackLayout.isEdgeOnly = args.getBoolean(EDGE_ONLY)
            }
            if (args.containsKey(EDGE_SIZE)) {
                var edgeSize = args.getFloat(EDGE_SIZE)
                if (edgeSize > 100) {
                    edgeSize = 1f
                } else if (edgeSize > 1) {
                    edgeSize /= 100
                }
                swipeBackLayout.edgeSize = edgeSize
            }
            if (args.containsKey(PERCENT_TO_RELEASE)) {
                var percent = args.getFloat(PERCENT_TO_RELEASE)
                if (percent > 100) {
                    percent = 1f
                } else if (percent > 1) {
                    percent /= 100
                }
                swipeBackLayout.percentToRelease = percent
            }
        }

        swipeBackLayout.addListener(object : SwipeBackLayout.Listener {
            override fun onClose() {
                close()
            }
        })

        toolbar.setNavigationOnClickListener { close() }
    }

    private fun close() {
        fragmentManager?.popBackStack()
    }


    companion object {
        const val FRAGMENT_TAG = "blue"

        private const val EDGE_ONLY = "edgeOnly"
        private const val EDGE_SIZE = "edgeSize"
        private const val PERCENT_TO_RELEASE = "release"

        fun newInstance(edgeOnly: Boolean, edgeSize: Float, percentToRelease: Float): Fragment {
            val fragment = SecondFragment()
            val args = Bundle()
            args.putBoolean(EDGE_ONLY, edgeOnly)
            args.putFloat(EDGE_SIZE, edgeSize)
            args.putFloat(PERCENT_TO_RELEASE, percentToRelease)
            fragment.arguments = args
            return fragment
        }
    }
}
