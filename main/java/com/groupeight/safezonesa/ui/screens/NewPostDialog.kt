package com.groupeight.safezonesa.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown

@Composable
fun NewPostDialog(
    defaultAnonymous: Boolean = false,
    isSubmitting: Boolean = false,
    errorMessage: String? = null,
    onDismiss: () -> Unit,
    onSubmit: (
        category: String,
        title: String,
        description: String,
        anonymous: Boolean
    ) -> Unit
) {

    var category by remember {
        mutableStateOf("Crime")
    }

    var title by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var anonymous by remember {
        mutableStateOf(defaultAnonymous)
    }

    val categories = listOf(
        "Crime",
        "Suspicious Activity",
        "Accident",
        "Fire",
        "Theft",
        "Safety Hazard",
        "Other"
    )

    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = {
            if (!isSubmitting) {
                onDismiss()
            }
        },

        title = {
            Text("Report an Incident")
        },

        text = {

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // -------------------------------------------------
// CATEGORY
// -------------------------------------------------

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedButton(
                        onClick = {
                            if (!isSubmitting) {
                                categoryExpanded = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSubmitting
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(category)
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Select category"
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = {
                            categoryExpanded = false
                        }
                    ) {

                        categories.forEach { item ->

                            DropdownMenuItem(
                                text = {
                                    Text(item)
                                },
                                onClick = {
                                    category = item
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // -------------------------------------------------
                // TITLE
                // -------------------------------------------------

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    label = {
                        Text("Incident title")
                    },
                    placeholder = {
                        Text("e.g. Suspicious activity near the school")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSubmitting
                )

                // -------------------------------------------------
                // DESCRIPTION
                // -------------------------------------------------

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = {
                        Text("Description")
                    },
                    placeholder = {
                        Text("Describe what happened...")
                    },
                    minLines = 4,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSubmitting
                )

                // -------------------------------------------------
                // ANONYMOUS REPORTING
                // -------------------------------------------------

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Report anonymously",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Hide your name from the community feed.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Switch(
                        checked = anonymous,
                        onCheckedChange = {
                            anonymous = it
                        },
                        enabled = !isSubmitting
                    )
                }

                // -------------------------------------------------
                // ERROR
                // -------------------------------------------------

                if (errorMessage != null) {

                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },

        confirmButton = {

            Button(
                enabled = !isSubmitting &&
                        title.isNotBlank() &&
                        description.isNotBlank(),

                onClick = {

                    onSubmit(
                        category,
                        title.trim(),
                        description.trim(),
                        anonymous
                    )
                }
            ) {

                if (isSubmitting) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text("Submitting...")
                } else {

                    Text("Submit Report")
                }
            }
        },

        dismissButton = {

            TextButton(
                enabled = !isSubmitting,
                onClick = onDismiss
            ) {

                Text("Cancel")
            }
        }
    )
}