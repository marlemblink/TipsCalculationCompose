package com.example.tipscalculation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tipscalculation.components.InputField
import com.example.tipscalculation.ui.theme.TipsCalculationTheme
import com.example.tipscalculation.util.calculateTotalPerPerson
import com.example.tipscalculation.util.calculateTotalTip
import com.example.tipscalculation.util.formatDouble
import com.example.tipscalculation.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipsCalculationTheme {
                val splitByState = remember {
                    mutableIntStateOf(1)
                }
                val tipAmountState = remember {
                    mutableDoubleStateOf(0.0)
                }
                val totalPerPersonState = remember {
                    mutableDoubleStateOf(0.0)
                }
                Column(
                    modifier = Modifier
                    .padding(all = 12.dp)
                ) {
                    BillForm(
                        splitByState = splitByState,
                        tipAmountState = tipAmountState,
                        totalPerPersonState = totalPerPersonState
                    )
                }
            }
        }
    }
}

//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 134.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(150.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
            color = Color(0xFFF8A9F3)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "$${formatDouble(totalPerPerson)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValChange: (String) -> Unit = { }
) {
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNullOrEmpty().not()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableFloatStateOf(0f)
    }

    var tipPercentage = (sliderPositionState.value * 100).toInt()

    TopHeader(totalPerPerson = totalPerPersonState.value)
    Surface(
        modifier = modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                valueState = totalBillState,
                labelId = "Enter total Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if(validState.not()) {
                        //reset values
                        keyboardController?.hide()
                        totalPerPersonState.value = 0.0
                        tipAmountState.value = 0.0
                        tipPercentage = 0
                        splitByState.value = range.first
                        sliderPositionState.value = 0f
                        return@KeyboardActions
                    }
                    onValChange(totalBillState.value.trim())
                    totalPerPersonState.value = calculateTotalPerPerson(totalBillState.value.toDouble(), splitByState.value, sliderPositionState.value)
                    keyboardController?.hide()
                }
            )
            if(validState) {
                Row(
                    modifier = modifier
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split",
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                splitByState.value =
                                    if(splitByState.value > 1) splitByState.value - 1 else 1
                                totalPerPersonState.value = calculateTotalPerPerson(totalBillState.value.toDouble(), splitByState.value, sliderPositionState.value)
                            }
                        )
                        Text(
                            text = "${splitByState.value}",
                            modifier = modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if(splitByState.value < range.last) {
                                    splitByState.value = splitByState.value + 1
                                    totalPerPersonState.value = calculateTotalPerPerson(totalBillState.value.toDouble(), splitByState.value, sliderPositionState.value)
                                }
                            })
                    }
                }
            //Tip Row
            Row(
                modifier = modifier
                    .padding(horizontal = 3.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Tip",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(170.dp))
                Text(
                    text = "$ ${formatDouble(tipAmountState.value)}",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                )
            }

            //Slider amount
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "$tipPercentage %")
                Spacer(modifier = Modifier.height(14.dp))

                //slider interaction
                Slider(
                    value = sliderPositionState.value,
                    onValueChange = { newVal->
                        sliderPositionState.value = newVal
                        if(sliderPositionState.value>=0.1){
                            tipAmountState.value =
                                calculateTotalTip(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPercentage = (sliderPositionState.value * 100).toInt()
                                )
                        } else {
                            tipAmountState.value = 0.0
                        }
                        totalPerPersonState.value = calculateTotalPerPerson(totalBillState.value.toDouble(), splitByState.value, sliderPositionState.value)
                    },
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp),
                    steps = 5,
                    onValueChangeFinished = {

                    }
                )
            }
            } else Box {}
        }
    }
}

private fun calculateTotalPerPerson(totalBillState: Double, splitByState: Int, sliderPositionState: Float): Double {
    return calculateTotalPerPerson(
        totalBill = totalBillState,
        splitBy = splitByState,
        tipPercentage = (sliderPositionState * 100).toInt()
    )
}
