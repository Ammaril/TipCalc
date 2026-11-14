package com.example.tipcalc

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.tipcalc.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isShowingTotal = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // TextWatcher на поле "Количество блюд"
        binding.etDishesCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val itemsCount = s?.toString()?.toIntOrNull() ?: 0
                val discountPercent = getDiscountPercent(itemsCount)

                updateRadioGroup(discountPercent)
                recalculate()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Слушатель изменения суммы заказа
        binding.etOrderAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                recalculate()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Слушатель слайдера чаевых
        binding.sliderTip.addOnChangeListener { slider, value, _ ->
            val tipPercent = value.toInt()
            binding.tvTipPercent.text = getString(R.string.tip_percent_format, tipPercent)

            val bill = binding.etOrderAmount.text.toString().toDoubleOrNull() ?: 0.0
            val tipAmount = calculateTip(bill, tipPercent)

            val snackbarText = String.format(Locale.getDefault(), "Чаевые: %.2f ₽", tipAmount)
            Snackbar.make(slider, snackbarText, Snackbar.LENGTH_SHORT).show()

            recalculate()
        }

        // Кнопка переключения "Итого" / "Скидка"
        binding.btnTotal.setOnClickListener {
            isShowingTotal = !isShowingTotal
            recalculate()
        }
    }

    private fun updateRadioGroup(discountPercent: Int) {
        val buttonId = when (discountPercent) {
            3 -> R.id.rbDiscount3
            5 -> R.id.rbDiscount5
            7 -> R.id.rbDiscount7
            10 -> R.id.rbDiscount10
            else -> -1
        }

        if (buttonId != -1) {
            binding.rgDiscount.check(buttonId)
        } else {
            binding.rgDiscount.clearCheck()
        }
    }

    private fun recalculate() {
        val bill = binding.etOrderAmount.text.toString().toDoubleOrNull() ?: 0.0
        val tipPercent = binding.sliderTip.value.toInt()
        val itemsCount = binding.etDishesCount.text.toString().toIntOrNull() ?: 0
        val discountPercent = getDiscountPercent(itemsCount)

        val tip = calculateTip(bill, tipPercent)
        val discount = calculateDiscount(bill, discountPercent)
        val total = calculateTotal(bill, tip, discount)

        binding.tvTipPercent.text = getString(R.string.tip_percent_format, tipPercent)
        binding.tvDiscountAmount.text = getString(R.string.discount_amount_format, discount)

        if (isShowingTotal) {
            binding.tvTotalResult.text = getString(R.string.total_amount_format, total)
            binding.btnTotal.text = getString(R.string.btn_show_discount)
        } else {
            binding.tvTotalResult.text = getString(R.string.discount_amount_format, discount)
            binding.btnTotal.text = getString(R.string.btn_total)
        }
    }

    private fun calculateTip(bill: Double, tipPercent: Int): Double {
        return bill * tipPercent / 100.0
    }

    private fun getDiscountPercent(itemsCount: Int): Int {
        return when {
            itemsCount > 10 -> 10
            itemsCount in 6..10 -> 7
            itemsCount in 3..5 -> 5
            itemsCount in 1..2 -> 3
            else -> 0
        }
    }

    private fun calculateDiscount(bill: Double, discountPercent: Int): Double {
        return bill * discountPercent / 100.0
    }

    private fun calculateTotal(bill: Double, tip: Double, discount: Double): Double {
        return bill + tip - discount
    }
}
