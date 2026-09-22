package com.groupeight.safezonesa.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.groupeight.safezonesa.model.MissingPerson
import java.net.URLEncoder

/**
 * FR11 — lets a user share a missing-person alert with any app on the phone (WhatsApp,
 * Facebook, SMS, email...) through Android's share sheet, to raise awareness.
 */
object ShareHelper {

    /** A Google Maps link that opens on the last-seen area. */
    fun mapLink(person: MissingPerson): String {
        val query = URLEncoder.encode("${person.lastSeenLocation}, South Africa", "UTF-8")
            .replace("+", "%20")
        return "https://www.google.com/maps/search/?api=1&query=$query"
    }

    /** The plain-text alert that gets shared or copied. */
    fun missingPersonMessage(person: MissingPerson): String = buildString {
        appendLine("\uD83D\uDEA8 MISSING PERSON - please help share")
        appendLine()
        appendLine("Name: ${person.name}")
        appendLine("Last seen: ${person.lastSeenLocation}")
        appendLine("When: ${person.lastSeenTime}")
        appendLine("Description: ${person.description}")
        appendLine("Contact: ${person.contactNumber}")
        appendLine()
        appendLine("Last seen area: ${mapLink(person)}")
        appendLine()
        appendLine("If you have seen this person, call the number above or SAPS on 10111.")
        append("Shared via SafeZone SA")
    }

    /** Opens the Android share sheet. */
    fun shareMissingPerson(context: Context, person: MissingPerson) {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "MISSING PERSON: ${person.name}")
            putExtra(Intent.EXTRA_TEXT, missingPersonMessage(person))
        }
        val chooser = Intent.createChooser(send, "Share missing person alert")
        if (context !is Activity) chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    /** Copies the same message to the clipboard. */
    fun copyMissingPerson(context: Context, person: MissingPerson) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Missing person", missingPersonMessage(person)))
        Toast.makeText(context, "Alert copied", Toast.LENGTH_SHORT).show()
    }
}