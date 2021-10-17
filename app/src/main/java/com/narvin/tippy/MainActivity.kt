package com.narvin.tippy

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat

import android.widget.Button
import java.lang.Math.ceil


private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercent: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var tvMoneyEmoji: TextView
    private lateinit var buttonRound: Button
    private var round = false
    private lateinit var tvRoundEmoji: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercent = findViewById(R.id.tvTipPercent)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        tvMoneyEmoji = findViewById(R.id.tvMoneyEmoji)
        buttonRound = findViewById(R.id.buttonRound)
        tvRoundEmoji = findViewById(R.id.tvRoundEmoji)
        round = false


        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)
        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                this@MainActivity.round = false
                Log.i(TAG, "onProgressChanged $progress")
                tvTipPercent.text = "$progress%"
                tvRoundEmoji.setVisibility(View.INVISIBLE)
                computeTipAndTotal()
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
        etBaseAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                this@MainActivity.round = false
                tvRoundEmoji.setVisibility(View.INVISIBLE)
                Log.i(TAG, "afterTextChanged$s")
                computeTipAndTotal()
            }

        })

        buttonRound.setOnClickListener(View.OnClickListener {
            if (tvTotalAmount.text.isEmpty()) {
                return@OnClickListener
            }
//            val roundedTotal = ceil(tvTotalAmount.toString().toDouble())
//            val newPercent = roundedTotal / tvTotalAmount.toString().toDouble()
//            seekBarTip.setProgress(newPercent.toInt())
            this.round = true
            tvRoundEmoji.setVisibility(View.VISIBLE)
            computeTipAndTotal()
        })

    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }
        tvTipDescription.text = tipDescription
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.color_worst_tip),
            ContextCompat.getColor(this, R.color.color_best_tip)
        ) as Int
        tvTipDescription.setTextColor(color)
        val size = tipPercent.toFloat() * 7
        tvMoneyEmoji.setTextSize(1, size)
    }

    private fun computeTipAndTotal() {
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }
        // Get value
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        // Compute tip/total
        val tipAmount = baseAmount * tipPercent / 100
        var totalAmount = baseAmount + tipAmount
        if (this.round) {
            totalAmount = ceil(totalAmount)
        }
        // update ui
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
    }
}