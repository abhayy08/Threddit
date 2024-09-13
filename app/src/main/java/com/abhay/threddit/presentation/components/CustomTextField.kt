package com.abhay.threddit.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    isError: Boolean = false,
    maxLines: Int = 1,
    supportingText: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {

    val borderColor = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray

    val textFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        focusedLabelColor = borderColor,
        unfocusedLabelColor = borderColor.copy(0.7f),
        cursorColor = borderColor,
        errorCursorColor = Color.Red,
        errorLabelColor = Color.Red,
        errorSupportingTextColor = Color.Red,
        errorTrailingIconColor = Color.Red,
        errorContainerColor = Color.Transparent,
        errorLeadingIconColor = Color.Red,
        selectionColors = TextSelectionColors(
            handleColor = MaterialTheme.colorScheme.onSurface,
            backgroundColor = MaterialTheme.colorScheme.onSurface
        )
    )

    TextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        modifier = modifier.border(
            1.dp,
            shape = RoundedCornerShape(8.dp),
            color = if (isError) Color.Red else MaterialTheme.colorScheme.onSecondary.copy(0.3f)
        ),
        isError = isError,
        label = {
            Text(text = label)
        },
        colors = textFieldColors,
        supportingText = supportingText,
        maxLines = maxLines,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation
    )
}
