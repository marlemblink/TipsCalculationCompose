package com.example.tipscalculation.util

fun calculateTotalTip(totalBill: Double, tipPercentage: Int): Double {
    var totalTip = 0.0
    if(totalBill > 1 && totalBill.toString().isNotEmpty()) {
        totalTip = (totalBill * tipPercentage) / 100
    }
   return totalTip
}

fun calculateTotalPerPerson(
    totalBill: Double,
    splitBy: Int,
    tipPercentage: Int
): Double {
    val tip = calculateTotalTip(totalBill = totalBill, tipPercentage = tipPercentage) + totalBill
    return (tip / splitBy)
}


fun formatDouble(number: Double): String {
    return ("%.2f".format(number))
}