package com.examtracker.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.examtracker.app.R
import com.examtracker.app.ui.theme.ExamTrackerTheme

@Composable
fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var subjectName by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.exam_detail_add_subject_dialog_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = subjectName,
                    onValueChange = {
                        subjectName = it
                        if (showError && it.isNotBlank()) showError = false
                    },
                    label = { Text(text = stringResource(id = R.string.exam_detail_subject_name_label)) },
                    isError = showError,
                    supportingText = {
                        if (showError) {
                            Text(text = stringResource(id = R.string.error_subject_name_blank))
                        }
                    },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (subjectName.isBlank()) {
                        showError = true
                    } else {
                        onConfirm(subjectName.trim())
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.action_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.action_cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun AddSubjectDialogPreview() {
    ExamTrackerTheme {
        AddSubjectDialog(onDismiss = {}, onConfirm = {})
    }
}