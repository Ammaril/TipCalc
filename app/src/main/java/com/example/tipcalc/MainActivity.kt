package com.example.tipcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.tipcalc.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
