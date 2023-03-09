package com.example.seerbitsdk.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.seerbitsdk.SeerBitDestination
import com.example.seerbitsdk.ui.theme.LighterGray
import com.example.seerbitsdk.ui.theme.SeerBitTheme
import com.example.seerbitsdk.R

@Composable
fun SeerBitNavButtons(
    text: String,
    attachedDescription: String,
    onSelected: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
    enabled : Boolean
) {
    Surface(

        shape = RoundedCornerShape(8.dp),

        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onSelected,
                enabled = enabled

                )
            .padding(4.dp),
        color = LighterGray,

        ) {
        Row(
            modifier = modifier
                .padding(8.dp)
                .height(40.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text)
            Row() {
                Text(text = attachedDescription)
                if (text == "Debit/Credit Card") {
                    Image(
                        painter = painterResource(id = R.drawable.mastercard),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Image(
                        painter = painterResource(id = R.drawable.verve_logo),
                        contentDescription = null
                    )
                }
            }
        }
    }

}

@Composable
fun SeerBitNavButtonsColumn(
    allButtons: List<SeerBitDestination>,
    onButtonSelected: (SeerBitDestination) -> Unit,
    currentButtonSelected: SeerBitDestination,
    enable : Boolean
) {

    Surface(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Column(Modifier.selectableGroup()) {

            allButtons.forEach { navButtons ->
                SeerBitNavButtons(
                    text = navButtons.name,
                    attachedDescription = navButtons.attachedDescription,
                    onSelected = { onButtonSelected(navButtons) },
                    selected = currentButtonSelected == navButtons,
                    enabled = enable
                )
            }
            Spacer(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
            Row(
                Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
            }
        }

    }


}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun paymentOptionsButtonPreview() {
    SeerBitTheme {
        SeerBitNavButtons(
            text = "Bank",
            attachedDescription = "",
            onSelected = { /*TODO*/ },
            selected = true,
            enabled = true
        )
    }
}
