package com.example.contactssampleapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class ContactPickerActivity : AppCompatActivity() {
  private val REQUEST_READ_CONTACTS_PERMISSION = 0
  private val REQUEST_CONTACT = 1

  private var mContactPick: Button? = null
  private var contactDetails: TextView? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_contact_picker)
    val pickContent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
    mContactPick = findViewById(R.id.pick_contact)
    contactDetails = findViewById(R.id.contact_name_phone_number)

    mContactPick?.setOnClickListener(object : View.OnClickListener {
      override fun onClick(view: View?) {
        startActivityForResult(pickContent, REQUEST_CONTACT)
      }
    })
    requestContactsPermission()
    updateButton(hasContactsPermission())
  }

  private fun updateButton(enable: Boolean) {
    mContactPick?.isEnabled = enable
  }

  private fun hasContactsPermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
        PackageManager.PERMISSION_GRANTED
  }

  // Request contact permission if it
  // has not been granted already
  private fun requestContactsPermission() {
    if (!hasContactsPermission()) {
      ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.READ_CONTACTS),
        REQUEST_READ_CONTACTS_PERMISSION
      )
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQUEST_READ_CONTACTS_PERMISSION && grantResults.isNotEmpty())  {
      updateButton(grantResults[0] == PackageManager.PERMISSION_GRANTED);
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode != Activity.RESULT_OK) return;

    if (requestCode == REQUEST_CONTACT && data != null) {
      val contactUri: Uri? = data.data
      val projection =
        arrayOf<String>(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME)
      val cursor: Cursor? = this.contentResolver
        .query(contactUri!!, projection, null, null, null)
      try {
        // Double-check that you
        // actually got results
        if (cursor!!.count === 0) return

        // Pull out the first column of
        // the first row of data
        // that is your contact's name
        cursor?.moveToFirst()
        val id = cursor?.getLong(0)
        val name = cursor?.getString(1)
        val projection2 =
          arrayOf<String>(ContactsContract.Data.MIMETYPE, ContactsContract.Contacts.Data.DATA1)
        val selection2: String =
          ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id + " AND " + ContactsContract.Data.MIMETYPE + " IN ('" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "', '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "')"
        val cursor2 = contentResolver.query(
          ContactsContract.Data.CONTENT_URI,
          projection2,
          selection2,
          null,
          null
        )
        while (cursor2 != null && cursor2.moveToNext()) {
          val mimetype = cursor2.getString(0)
          val data = cursor2.getString(1)
          if (mimetype == ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE) {
            Log.i("Picker", "got a phone: $data")
            contactDetails!!.text = "${name} (${data})"
          } else {
            Log.i("Picker", "got an email: $data")
          }
        }
      } finally {
        cursor?.close()
      }
    }
  }
}