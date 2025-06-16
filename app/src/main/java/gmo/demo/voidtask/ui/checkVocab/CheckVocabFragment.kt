package gmo.demo.voidtask.ui.checkVocab

import android.os.Bundle
import android.view.View
import gmo.demo.voidtask.R
import gmo.demo.voidtask.databinding.FragmentCheckVocabBinding
import gmo.demo.voidtask.ui.base.BaseFragment
import gmo.demo.voidtask.BR
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import androidx.lifecycle.ViewModelProvider
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.widget.Toast

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CheckVocabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CheckVocabFragment : BaseFragment<FragmentCheckVocabBinding, CheckVocabViewModel>(), KodeinAware {
    override val kodein by kodein()

    private val factory: CheckVocabViewModelFactory by instance()

    override val layoutId: Int
        get() = R.layout.fragment_check_vocab
    override val bindingVariable: Int
        get() = BR.viewModel
    override val viewModel: CheckVocabViewModel by lazy {
        ViewModelProvider(this, factory)[CheckVocabViewModel::class.java]
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadVocabFromFile() // Tải dữ liệu khi Fragment được tạo

        mViewDataBinding?.cardVocab?.setOnClickListener {
            viewModel.flipCard()
        }

        viewModel.showWaveAnimation.observe(viewLifecycleOwner) { show ->
            mViewDataBinding?.ivWaveAnimation?.let { imageView ->
                if (show) {
                    (imageView.drawable as? AnimationDrawable)?.start()
                } else {
                    (imageView.drawable as? AnimationDrawable)?.stop()
                }
            }
        }


        viewModel.isCardFlipped.observe(viewLifecycleOwner) {
            if (it) {
                // Hiển thị mặt sau (tiếng Việt)
                mViewDataBinding?.tvFrontText?.visibility = View.GONE
                mViewDataBinding?.tvBackText?.visibility = View.VISIBLE
            } else {
                // Hiển thị mặt trước (tiếng Anh)
                mViewDataBinding?.tvFrontText?.visibility = View.VISIBLE
                mViewDataBinding?.tvBackText?.visibility = View.GONE
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CheckVocabViewModel.PERMISSION_REQUEST_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Quyền được cấp, bắt đầu ghi âm
                    viewModel.startRecording(requireContext())
                } else {
                    // Quyền bị từ chối, hiển thị thông báo
                    Toast.makeText(
                        requireContext(),
                        "Cần quyền ghi âm để sử dụng tính năng này",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CheckVocabFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CheckVocabFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}