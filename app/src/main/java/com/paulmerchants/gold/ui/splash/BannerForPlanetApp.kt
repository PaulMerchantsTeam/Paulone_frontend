/*
package com.paulmerchants.gold.ui.splash

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.FragmentBannerForPlanetAppBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BannerForPlanetApp :
    BaseFragment<FragmentBannerForPlanetAppBinding>(FragmentBannerForPlanetAppBinding::inflate) {

    override fun FragmentBannerForPlanetAppBinding.initialize() {}

    override fun onStart() {
        super.onStart()

        // Set button listeners
        binding.clickButton.setOnClickListener {
            openUrl("https://planet.ltfinance.com/?quickpay-goldloan")
        }

        binding.clickButtonHin.setOnClickListener {
            openUrl("https://planet.ltfinance.com/?quickpay-goldloan")
        }

        setupClickableText(
            textView = binding.visitSite,
            fullText = getString(R.string.visit_site),
            clickablePart = "www.LTFINANCE.com",
            url = "https://www.ltfinance.com"
        )

        setupClickableText(
            textView = binding.dwnldAppText,
            fullText = getString(R.string.dwld_planet_app),
            clickablePart = "Planet App by L&T Finance",
            url = "https://planetbyltf.onelink.me/ffgr/uvfmbbj5"
        )
        setupClickableText(
            textView = binding.webLnt,
            fullText =  getString(R.string.visit_lnt),
            clickablePart = "www.LTFINANCE.com",
            url = "https://www.ltfinance.com"
        )
        setupClickableText(
            textView = binding.visitSiteHin,
            fullText = getString(R.string.visit_site),
            clickablePart = "www.LTFINANCE.com",
            url = "https://www.ltfinance.com"
        )

        setupClickableText(
            textView = binding.dwnldAppTextHin,
            fullText = getString(R.string.dwld_planet_app),
            clickablePart = "Planet App by L&T Finance",
            url = "https://planetbyltf.onelink.me/ffgr/uvfmbbj5"
        )
        setupClickableText(
            textView = binding.webLntHin,
            fullText =  getString(R.string.visit_lnt),
            clickablePart = "www.LTFINANCE.com",
            url = "https://www.ltfinance.com"
        )

    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        requireContext().startActivity(intent)
    }

    private fun setupClickableText(
        textView: View,
        fullText: String,
        clickablePart: String,
        url: String
    ) {
        val spannableString = SpannableString(fullText)
        val startIndex = fullText.indexOf(clickablePart)
        val endIndex = startIndex + clickablePart.length

        if (startIndex < 0) return // Prevent crash if clickablePart not found

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openUrl(url)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = Color.BLACK
                ds.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
        }

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        (textView as? android.widget.TextView)?.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }
}
*/
package com.paulmerchants.gold.ui.splash

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.FragmentBannerForPlanetAppBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BannerForPlanetApp :
    BaseFragment<FragmentBannerForPlanetAppBinding>(FragmentBannerForPlanetAppBinding::inflate) {

    override fun FragmentBannerForPlanetAppBinding.initialize() {}

    override fun onStart() {
        super.onStart()

        // Open Planet App URL when buttons clicked
        val planetUrl = "https://planet.ltfinance.com/?quickpay-goldloan"
        binding.clickButton.setOnClickListener { openUrl(planetUrl) }
        binding.clickButtonHin.setOnClickListener { openUrl(planetUrl) }

        // Setup all clickable texts
        setupTextLinks()
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        requireContext().startActivity(intent)
    }

    private fun setupTextLinks() {
        val mappings = listOf(
            Triple(binding.visitSite, getString(R.string.visit_site), "www.LTFINANCE.com"),
            Triple(binding.dwnldAppText, getString(R.string.dwld_planet_app), "Planet App by L&T Finance"),
            Triple(binding.webLnt, getString(R.string.visit_lnt), "www.LTFINANCE.com"),
            Triple(binding.visitSiteHin, getString(R.string.visit_site), "www.LTFINANCE.com"),
            Triple(binding.dwnldAppTextHin, getString(R.string.dwld_planet_app), "Planet App by L&T Finance"),
            Triple(binding.webLntHin, getString(R.string.visit_lnt), "www.LTFINANCE.com")
        )

        val urlMap = mapOf(
            "www.LTFINANCE.com" to "https://www.ltfinance.com",
            "Planet App by L&T Finance" to "https://planetbyltf.onelink.me/ffgr/uvfmbbj5"
        )

        for ((textView, fullText, clickablePart) in mappings) {
            val url = urlMap[clickablePart] ?: continue
            setSpannableLink(textView, fullText, clickablePart, url)
        }
    }

    private fun setSpannableLink(
        textView: View,
        fullText: String,
        clickablePart: String,
        url: String
    ) {
        val startIndex = fullText.indexOf(clickablePart)
        val endIndex = startIndex + clickablePart.length

        if (startIndex < 0 || textView !is TextView) return

        val spannableString = SpannableString(fullText).apply {
            setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    openUrl(url)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                    ds.color = Color.BLACK
                    ds.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
            }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        textView.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }
}
