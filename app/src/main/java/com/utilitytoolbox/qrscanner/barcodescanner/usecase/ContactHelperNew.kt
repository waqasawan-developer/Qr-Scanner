package com.utilitytoolbox.qrscanner.barcodescanner.usecase


import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.ContactsContract
import com.utilitytoolbox.qrscanner.barcodescanner.model.MainContactN
import com.utilitytoolbox.qrscanner.barcodescanner.utils.orZero


object ContactHelperNew {
    private val PHONE_PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
    private val CONTACT_PROJECTION = arrayOf(ContactsContract.Data.LOOKUP_KEY)

    fun getPhone(context: Context, result: Intent?): String? {
        val uri = result?.data ?: return null
        val contentResolver = context.contentResolver

        val cursor = contentResolver.query(uri, PHONE_PROJECTION, null, null, null)
            ?: return null

        if (cursor.moveToNext().not()) {
            cursor.close()
            return null
        }

        val phone = cursor.getStringOrNull(ContactsContract.CommonDataKinds.Phone.NUMBER)
        cursor.close()
        return phone
    }

    fun getContact(context: Context, result: Intent?): MainContactN? {
        val uri = result?.data ?: return null
        val contentResolver = context.contentResolver

        val cursor = contentResolver.query(uri, CONTACT_PROJECTION, null, null, null)
            ?: return null

        if (cursor.moveToNext().not()) {
            cursor.close()
            return null
        }

        val lookupKey = cursor.getStringOrNull(ContactsContract.Data.LOOKUP_KEY)
        if (lookupKey == null) {
            cursor.close()
            return null
        }
        
        return MainContactN().also { contact ->
            buildContactPhoneDetails(contentResolver, lookupKey, contact)
            buildEmailDetails(contentResolver, lookupKey, contact)
            buildAddressDetails(contentResolver, lookupKey, contact)
            
            cursor.close()
        }
    }

    private fun buildContactPhoneDetails(contentResolver: ContentResolver, lookupKey: String, MainContactN: MainContactN) {
        val contactWhere = ContactsContract.Data.LOOKUP_KEY + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
        val contactWhereParams = arrayOf(lookupKey, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        
        val cursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            contactWhere,
            contactWhereParams,
            null
        ) ?: return
        
        if (cursor.count <= 0) {
            cursor.close()
            return
        }
        
        if (cursor.moveToNext().not()) {
            cursor.close()
            return
        }

        if (cursor.getStringOrNull(ContactsContract.Contacts.HAS_PHONE_NUMBER)?.toInt().orZero() <= 0) {
            cursor.close()
            return
        }
        
        MainContactN.firstName = cursor.getStringOrNull(ContactsContract.Contacts.DISPLAY_NAME)
        MainContactN.middleName = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME)
        MainContactN.lastName = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)
        MainContactN.phone = cursor.getStringOrNull(ContactsContract.CommonDataKinds.Phone.NUMBER)
        MainContactN.contactType = cursor.getIntOrNull(ContactsContract.CommonDataKinds.Phone.TYPE)
        
        cursor.close()
    }

    private fun buildEmailDetails(contentResolver: ContentResolver, lookupKey: String, MainContactN: MainContactN) {
        val emailWhere = ContactsContract.Data.LOOKUP_KEY + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
        val emailWhereParams = arrayOf(lookupKey, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)

        val cursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            emailWhere,
            emailWhereParams,
            null
        ) ?: return

        if (cursor.moveToNext().not()) {
            cursor.close()
            return
        }

        MainContactN.email = cursor.getStringOrNull(ContactsContract.CommonDataKinds.Email.DATA)

        cursor.close()
    }

    private fun buildAddressDetails(contentResolver: ContentResolver, lookupKey: String, MainContactN: MainContactN) {
        val addressWhere = ContactsContract.Data.LOOKUP_KEY + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
        val addressWhereParams = arrayOf(lookupKey, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)

        val cursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            addressWhere,
            addressWhereParams,
            null
        ) ?: return

        if (cursor.moveToNext().not()) {
            cursor.close()
            return
        }

        MainContactN.poBox = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.POBOX)
        MainContactN.street = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.STREET)
        MainContactN.city = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.CITY)
        MainContactN.state = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.REGION)
        MainContactN.zipcode = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.REGION)
        MainContactN.country = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY)
        MainContactN.street = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.STREET)
        MainContactN.neighborhood = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD)
        MainContactN.formattedAddress =  cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)

        cursor.close()
    }
    
    private fun Cursor.getStringOrNull(columnName: String): String? {
        return try {
            getString(getColumnIndex(columnName))
        } catch (ex: Exception) {
            MainLogger.log(ex)
            null
        }
    }

    private fun Cursor.getIntOrNull(columnName: String): Int? {
        return try {
            getInt(getColumnIndex(columnName))
        } catch (ex: Exception) {
            MainLogger.log(ex)
            null
        }
    }
}