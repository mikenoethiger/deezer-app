package fhnw.emoba.freezerapp.ui

import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import fhnw.emoba.freezerapp.model.FreezerModel


@Composable
fun AppUI(model : FreezerModel){
    with(model){
        Text(text = title, style = TextStyle(fontSize = 28.sp))
    }
}
