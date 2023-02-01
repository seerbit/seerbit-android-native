package com.example.seerbitsdk.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.seerbitsdk.PaymentTypeData
import com.example.seerbitsdk.SeerBitDestination
import com.example.seerbitsdk.BottomSeerBitWaterMark
import com.example.seerbitsdk.ui.theme.LighterGray
import com.example.seerbitsdk.ui.theme.SeerBitTheme


@Composable
fun SeerBitNavButtons(
    text: String,
    attachedDescription: String,
    onSelected: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(

        shape = RoundedCornerShape(8.dp),

        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .selectable(
                selected = selected,
                onClick = onSelected,

                )
            .padding(4.dp),
        color = LighterGray,

        ) {

        Row(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text)
            Text(text = attachedDescription)
        }
    }

}

@Composable
fun SeerBitNavButtonsColumn(
    allButtons: List<SeerBitDestination>,
    onButtonSelected: (SeerBitDestination) -> Unit,
    currentButtonSelected: SeerBitDestination
) {

    Surface(Modifier.fillMaxWidth().padding(8.dp)) {

        Column(Modifier.selectableGroup()) {
            
            allButtons.forEach { navButtons ->
                SeerBitNavButtons(
                    text = navButtons.name,
                    attachedDescription = navButtons.attachedDescription,
                    onSelected = { onButtonSelected(navButtons) },
                    selected = currentButtonSelected == navButtons
                )
            }
            Spacer(modifier = Modifier.padding(top = 8.dp, bottom= 8.dp ))
            Row(
                Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                BottomSeerBitWaterMark()
            }
        }

    }


}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun paymentOptionsButtonPreview() {
    SeerBitTheme {
        LazyColumn(modifier = Modifier.padding(0.dp)) {
            items(PaymentTypeData.paymentData) { item ->
                com.example.seerbitsdk.PaymentOptionButtons(
                    paymentName = item.name,
                    paymentDescription = item.Desc,
                    onCardClick = { item.name }
                )
            }
        }
    }
}
