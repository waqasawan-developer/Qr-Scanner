package com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.utilitytoolbox.qrscanner.barcodescanner.R
import com.utilitytoolbox.qrscanner.barcodescanner.adapter.CreateQRGridAdapter
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.BarcodeFragmentBinding
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.LayoutFragmentCreateBinding
import com.utilitytoolbox.qrscanner.barcodescanner.databinding.QrCodeFragBinding
import com.utilitytoolbox.qrscanner.barcodescanner.model
.QRMainType
import com.utilitytoolbox.qrscanner.barcodescanner.utils.changeStatusBarClr
import com.utilitytoolbox.qrscanner.barcodescanner.utils.clipboardManager
import com.utilitytoolbox.qrscanner.barcodescanner.utils.orZero
import com.utilitytoolbox.qrscanner.barcodescanner.userInterface.activities.MainBarcodeCreateActivity
import com.utilitytoolbox.qrscanner.barcodescanner.utils.MainConstantsss
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateFrag : Fragment() {
    lateinit var binding: LayoutFragmentCreateBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutFragmentCreateBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().changeStatusBarClr(isTransparent = false)
        //supportEdgeToEdge()
        onClickListeners()

        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "QR Code"
                1 -> "Barcode"
                else -> null
            }
        }.attach()
    }

    private inner class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> FirstTabFragmentQRCode()
                1 -> SecondTabFragmentBarCode()
                else -> throw IllegalStateException("Unexpected position: $position")
            }
        }
    }

    private fun onClickListeners() {
        // QR code
        /*iv_clipboard.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.QR_CODE,
                BarcodeSchema.OTHER,
                getClipboardContent()
            )
        }
        iv_text.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.QR_CODE,
                BarcodeSchema.OTHER
            )
        }
        iv_url.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.QR_CODE,
                BarcodeSchema.URL
            )
        }
        iv_wifi.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.QR_CODE,
                BarcodeSchema.WIFI
            )
        }
        iv_location.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.QR_CODE,
                BarcodeSchema.GEO
            )
        }



        iv_contact.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.QR_CODE,
                BarcodeSchema.MECARD
            )
        }

        iv_phone.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.QR_CODE,
                BarcodeSchema.PHONE
            )
        }
        iv_email.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.QR_CODE,
                BarcodeSchema.EMAIL
            )
        }
        iv_sms.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.QR_CODE,
                BarcodeSchema.SMS
            )
        }

        iv_app.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.QR_CODE,
                BarcodeSchema.APP
            )
        }

        iv_data_matrix.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.DATA_MATRIX
            )
        }
        iv_aztec.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.AZTEC
            )
        }
        iv_pdf_417.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.PDF_417
            )
        }
        iv_codabar.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.CODABAR
            )
        }
        iv_code_39.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.CODE_39
            )
        }
        iv_code_93.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.CODE_93
            )
        }
        iv_code_128.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.CODE_128
            )
        }
        iv_ean_8.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.EAN_8
            )
        }
        iv_ean_13.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.EAN_13
            )
        }
        iv_itf_14.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.ITF
            )
        }
        iv_upc_a.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.UPC_A
            )
        }
        iv_upc_e.setOnClickListener {
            CreateBarcodeActivity.start(
                requireActivity(),
                BarcodeFormat.UPC_E
            )
        }*/



    }
}


class FirstTabFragmentQRCode : Fragment(R.layout.qr_code_frag) {
    // Your code here
    lateinit var binding: QrCodeFragBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = QrCodeFragBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val sectionAdapter = CreateQRGridAdapter(requireContext(),
            MainConstantsss.getQRCreateGeneral(requireContext()),
            object : CreateQRGridAdapter.Listener {
                override fun onCreateQrTypeClick(qrMainType: com.utilitytoolbox.qrscanner.barcodescanner.model.QRMainType) {
                    MainBarcodeCreateActivity.startUp(
                        requireActivity(),
                        qrMainType.barcodeFormat,
                        qrMainType.barcodeSchema
                    )
                }
            })

        val layoutManager = GridLayoutManager(requireActivity(), 3)
        //val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvCreateQRGeneral.layoutManager = layoutManager
        binding.rvCreateQRGeneral.adapter = sectionAdapter


        val sectionAdapter2 = CreateQRGridAdapter(requireContext(),
            MainConstantsss.getQRCreateOther(requireContext()),
            object : CreateQRGridAdapter.Listener {
                override fun onCreateQrTypeClick(qrMainType: QRMainType) {
                    if (qrMainType.qrType == "CLIPBOARD") {
                        MainBarcodeCreateActivity.startUp(
                            requireActivity(),
                            qrMainType.barcodeFormat,
                            qrMainType.barcodeSchema,
                            getClipboardContent()
                        )
                    } else {
                        MainBarcodeCreateActivity.startUp(
                            requireActivity(),
                            qrMainType.barcodeFormat,
                            qrMainType.barcodeSchema
                        )
                    }
                }
            })

        val layoutManager2 = GridLayoutManager(requireActivity(), 3)
        //val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvCreateQROther.layoutManager = layoutManager2
        binding.rvCreateQROther.adapter = sectionAdapter2
    }

    private fun getClipboardContent(): String {
        val clip = requireActivity().clipboardManager?.primaryClip ?: return ""
        return when (clip.itemCount.orZero()) {
            0 -> ""
            else -> clip.getItemAt(0).text.toString()
        }
    }
}

class SecondTabFragmentBarCode : Fragment(R.layout.barcode_fragment) {
    // Your code here
    lateinit var binding: BarcodeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BarcodeFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val sectionAdapter = CreateQRGridAdapter(requireContext(),
            MainConstantsss.getBarcodeCreateGeneral(requireContext()),
            object : CreateQRGridAdapter.Listener {
                override fun onCreateQrTypeClick(qrMainType: QRMainType) {
                    MainBarcodeCreateActivity.startUp(
                        requireActivity(),
                        qrMainType.barcodeFormat,
                        qrMainType.barcodeSchema
                    )
                }
            })

        val layoutManager = GridLayoutManager(requireActivity(), 3)
        //val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvCreateBarcodeGeneral.layoutManager = layoutManager
        binding.rvCreateBarcodeGeneral.adapter = sectionAdapter


        val sectionAdapter2 = CreateQRGridAdapter(requireContext(),
            MainConstantsss.getBarcodeCreateOther(requireContext()),
            object : CreateQRGridAdapter.Listener {
                override fun onCreateQrTypeClick(qrMainType: QRMainType) {

                    MainBarcodeCreateActivity.startUp(
                        requireActivity(),
                        qrMainType.barcodeFormat,
                        qrMainType.barcodeSchema
                    )
                }
            })

        val layoutManager2 = GridLayoutManager(requireActivity(), 3)
        //val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvCreateBarcodeOther.layoutManager = layoutManager2
        binding.rvCreateBarcodeOther.adapter = sectionAdapter2
    }


}

