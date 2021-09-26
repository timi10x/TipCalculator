/*
 * Copyright (c) 2021 Razeware LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 * 
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.tipcalculator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.raywenderlich.android.tipper.R
import com.raywenderlich.android.tipper.databinding.ActivityMainBinding
import java.text.NumberFormat

/**
 * Main Screen
 */
class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  private var isTextChanged = true

  private var bill: Int? = null
  private var billValue: String? = null

  private val defaultTipPercent = 15
  private var tipPercent: Int? = null
  private var tipPercentValue: String? = null

  private var tipTotal: Int? = null
  private var tipTotalValue: String? = null

  private var totalAmount: Int? = null
  private var totalAmountValue: String? = null


  override fun onCreate(savedInstanceState: Bundle?) {
    // Switch to AppTheme for displaying the activity
    setTheme(R.style.AppTheme)

    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)

    setContentView(binding.root)

    bill = 0
    tipPercent = defaultTipPercent
    tipTotal = 0
    totalAmount = 0

    binding.tipAmountValue.background = null
    binding.billEditTxtValue.isFocusable = true

    setupClickListeners()
    setupTextWatchers()
    setBillValueText()
    setTipPercentageValueText()
    setTipTotalValueText()
    setTotalAmountText()

  }

  /**
   * Add textwatcher functionality for edittexts
   */
  private fun setupTextWatchers() {
    binding.billEditTxtValue.addTextChangedListener(billTextWatcher)
    binding.tipPercent.addTextChangedListener(tipPercentTextWatcher)
    binding.tipAmountValue.addTextChangedListener(tipTotalTextWatcher)

  }

  /**
   * billTextWatcher
   */
  private val billTextWatcher: TextWatcher = object : TextWatcher {
    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
      if (isTextChanged) {
        isTextChanged = false
        return
      } else {
        isTextChanged = true
      }
      try {
        var billEditTextContent = binding.billEditTxtValue.text.toString()
        val replaceable =
          String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance().currency.symbol)
        billEditTextContent = billEditTextContent.replace(replaceable.toRegex(), "")
        bill = if (billEditTextContent.isEmpty()) {
          0
        } else {
          billEditTextContent.toInt()
        }
      } catch (e: Exception) {
        Toast.makeText(this@MainActivity, "error", Toast.LENGTH_LONG)
          .show()
        bill = 0
      }
      setBillValueText()
      if (billValue!!.substring(billValue!!.length - 1)
        == NumberFormat.getCurrencyInstance().currency.symbol
      ) {
        binding.billEditTxtValue.setSelection(binding.billEditTxtValue.text!!.length - 2)
      } else {
        binding.billEditTxtValue.setSelection(binding.billEditTxtValue.text!!.length)
      }
    }

    override fun afterTextChanged(editable: Editable) {
      if (binding.billEditTxtValue.hasFocus()) {
        calculateTipTotal()
        setTipTotalValueText()
        calculateTotal()
        setTotalAmountText()
      }
    }
  }

  /**
   * tipPercentTextWatcher
   */
  private val tipPercentTextWatcher: TextWatcher = object : TextWatcher {
    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun afterTextChanged(editable: Editable) {
      if (!binding.tipAmountValue.hasFocus()) {
        calculateTipTotal()
        calculateTotal()
        setTipTotalValueText()
        setTotalAmountText()
      }
    }
  }

  /**
   * tipTotalTextWatcher
   */
  private val tipTotalTextWatcher: TextWatcher = object : TextWatcher {
    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
      if (binding.tipAmountValue.hasFocus()) {
        if (isTextChanged) {
          isTextChanged = false
          return
        } else {
          isTextChanged = true
        }
        try {
          var tipTotalEditTextContent = binding.tipAmountValue.text.toString()
          val replaceable = String.format(
            "[%s,.\\s]",
            NumberFormat.getCurrencyInstance().currency.symbol
          )
          tipTotalEditTextContent =
            tipTotalEditTextContent.replace(replaceable.toRegex(), "")
          tipTotal = if (tipTotalEditTextContent.isEmpty()) {
            0
          } else {
            tipTotalEditTextContent.toInt()
          }
        } catch (e: Exception) {
          Toast.makeText(
            this@MainActivity,
            "error",
            Toast.LENGTH_LONG
          ).show()
          tipTotal = 0
        }
        setTipTotalValueText()
        if (tipTotalValue!!.substring(tipTotalValue!!.length - 1)
          == NumberFormat.getCurrencyInstance().currency.symbol
        ) {
          binding.tipAmountValue.setSelection(binding.tipAmountValue.text.length - 2)
        } else {
          binding.tipAmountValue.setSelection(binding.tipAmountValue.text.length)
        }
        calculateTipPercentage()
        setTipPercentageValueText()
        calculateTotal()
        setTotalAmountText()
      }
    }

    override fun afterTextChanged(editable: Editable) {}
  }


  /**
   * Add click functionality to buttons
   */
  private fun setupClickListeners() {
    binding.buttonDecreaseTipPercent.setOnClickListener {
      binding.tipAmountValue.clearFocus()
      if (tipPercent!! > 0) {
        tipPercent = tipPercent!! - 1
        setTipPercentageValueText()
      }
    }

    binding.buttonIncreaseTipPercent.setOnClickListener {
      binding.tipAmountValue.clearFocus()
      if (tipPercent!! < 100) {
        tipPercent = tipPercent!! + 1
        setTipPercentageValueText()
      }
    }
  }

  /**
   * Calculate tip percentage based on tipTotal
   */
  private fun calculateTipPercentage() {
    tipPercent = if (bill == 0) {
      defaultTipPercent
    } else {
      (tipTotal!! * 1.0 / bill!! * 100).toInt()
    }
  }

  /**
   * Calculate tip total
   */
  private fun calculateTipTotal() {
    tipTotal = bill!! * tipPercent!! / 100
  }

  /**
   * Calculate totalAmount
   */
  private fun calculateTotal() {
    totalAmount = bill!! + tipTotal!!
  }

  /**
   * Set totalAmountTextView to current value of totalAmountValue
   */
  private fun setTotalAmountText() {
    val format = NumberFormat.getCurrencyInstance()
    totalAmountValue = format.format(totalAmount!! / 100.0)
    binding.totalAmountValue.text = totalAmountValue
  }

  /**
   * Set tipTotalEditText to current value of tipTotalValue
   */
  private fun setTipTotalValueText() {
    val format = NumberFormat.getCurrencyInstance()
    tipTotalValue = format.format(tipTotal!! / 100.0)
    binding.tipAmountValue.setText(tipTotalValue)
  }

  /**
   * Set billEditText to current value of billValue
   */
  private fun setBillValueText() {
    val format = NumberFormat.getCurrencyInstance()
    billValue = format.format(bill!! / 100.0)
    binding.billEditTxtValue.setText(billValue)
  }

  /**
   * Set tipPercentTextView to current value of tipPercentValue
   */
  private fun setTipPercentageValueText() {
    tipPercentValue = tipPercent.toString() + "%"
    binding.tipPercent.text = tipPercentValue
  }
}
