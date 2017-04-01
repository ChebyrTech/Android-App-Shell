package com.chebyr.appshell.contact;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.InputStream;

public class ContactAccessor
{
    private static final String TAG = "ContactAccessor";

    Context context;
    private ContentResolver contentResolver;

    public ContactAccessor(Context context)
    {
        this.context = context;
        contentResolver = context.getContentResolver();
    }

    public Contact lookupNumber(String incomingNumber)
    {
        Log.d(TAG, "Lookup incomingNumber: " + incomingNumber);

        Contact contact = new Contact();

        contact.incomingNumber = incomingNumber;

        String contactID = getContactProfile(incomingNumber, contact);
        if(contactID == null)
            return null;

        Log.d(TAG, "Contact found. Retrieving additional information");

        getOrganization(contactID, contact);

        contact.photo = getPhoto(contactID);
        contact.phoneNumbers = getPhoneNumbers(contactID);
        contact.eMails = getEmailAddresses(contactID);
        contact.IMs = getIM(contactID);
        contact.nickName = getNickName(contactID);
        contact.groups = getGroups(contactID);
        contact.address = getAddress(contactID);
        contact.webSite = getWebsite(contactID);
        contact.notes = getNotes(contactID);

        return contact;
    }

    public Bitmap getPhoto(String contactIDStr)
    {
        Bitmap photo = null;
        long contactID = Long.parseLong(contactIDStr);
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactID);
        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri, true);

        if(inputStream != null)
            photo = BitmapFactory.decodeStream(inputStream);

        try
        {
            inputStream.close();
        }
        catch (Exception e)
        {
            Log.d(TAG, e.toString());
        }
        return photo;
    }

    public String getContactProfile(String incomingNumber, Contact contact)
    {
        String contactID = null;

        Uri profileUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(incomingNumber));
        String[] projection = new String[]
                {
                        ContactsContract.Profile._ID,
                        ContactsContract.Profile.DISPLAY_NAME,
                        ContactsContract.Profile.PHOTO_THUMBNAIL_URI
                };

        Cursor contactsCursor = contentResolver.query(profileUri, projection, null, null, null);

        if (contactsCursor.moveToFirst())
        {
            // Get Contact ID

            int contactIDIndex = contactsCursor.getColumnIndex(ContactsContract.Profile._ID);
            if (contactIDIndex >= 0)
                contactID = contactsCursor.getString(contactIDIndex);

            // Get Display Name
            int displayNameIndex = contactsCursor.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME);
            if (displayNameIndex >= 0)
            {
                contact.displayName = contactsCursor.getString(displayNameIndex);
            }
            // Get thumbnail
            int photoThumbnailUriIndex = contactsCursor.getColumnIndex(ContactsContract.Profile.PHOTO_THUMBNAIL_URI);
            if (photoThumbnailUriIndex >= 0)
            {
                String uriString = contactsCursor.getString(photoThumbnailUriIndex);
                if(uriString != null)
                {
                    contact.photoURI = Uri.parse(uriString);
                }
            }
        }
        contactsCursor.close();
        return contactID;
    }

    public String getPhoneNumbers(String contactID)
    {
        String phones = new String();

        Cursor pCur = this.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{contactID}, null);

        for(boolean recordsAvailable = pCur.moveToFirst(); recordsAvailable; )
        {
            String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(phone != null)
                phones = phones.concat(phone);

            recordsAvailable = pCur.moveToNext();

            if(recordsAvailable)
                phones = phones.concat(", ");
            //pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))
        }
        pCur.close();

        return phones;
    }

    public String getEmailAddresses(String contactID)
    {
        String emails = new String();

        Uri uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String[] projection = new String[]
                {
                        ContactsContract.CommonDataKinds.Email.DATA
//                        ContactsContract.CommonDataKinds.Email.TYPE
                };
        String selection = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?";
        String[] selectionArgs = new String[]{contactID};

        Cursor emailCur = contentResolver.query(uri, projection, selection, selectionArgs, null);

        for(boolean recordsAvailable = emailCur.moveToFirst(); recordsAvailable; )
        {
            String eMail = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
            emails = emails.concat(eMail);

            recordsAvailable = emailCur.moveToNext();

            if(recordsAvailable)
                emails = emails.concat(", ");
//            Email e = new Email(emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
  //                  ,emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE)));
        }
        emailCur.close();
        return emails;

    }

    public String getAddress(String contactID)
    {
        String address = new String();

        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{contactID,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};

        Cursor addressCursor = this.contentResolver.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
        if(addressCursor.moveToFirst())
        {
            address = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
        }
        addressCursor.close();

        return address;
    }

    public String getIM(String contactID)
    {
        String imList = new String();
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{contactID,
                ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};

        Cursor imCur = this.contentResolver.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
        if (imCur.moveToFirst())
        {
            String imName = imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
            if (imName.length() > 0)
            {
                imList = imList.concat(imName);
            }
        }
        imCur.close();

        return imList;
    }

    public void getOrganization(String contactID, Contact contact)
    {
        String company = null;
        String title = null;

        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{contactID,
                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};

        Cursor orgCur = this.contentResolver.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);

        if (orgCur.moveToFirst())
        {
//            orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
            company = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
            title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
        }
        orgCur.close();
        contact.organization = company;
        contact.jobTitle = title;
    }

    public String getNickName(String contactID)
    {
        String nickName = new String();
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{contactID,
                ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE};

        Cursor nickNameCursor = this.contentResolver.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
        if (nickNameCursor.moveToFirst())
        {
            nickName = nickNameCursor.getString(nickNameCursor.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
        }
        nickNameCursor.close();
        return nickName;
    }

    public String getGroups(String contactID)
    {
        String groups = new String();
        String groupRowID = null;

        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID};

        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{contactID, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE};

        Cursor groupMembershipCursor = contentResolver.query(uri, projection, where, whereParameters, null);
        for(boolean recordsAvailable = groupMembershipCursor.moveToFirst(); recordsAvailable; )
        {
            groupRowID = groupMembershipCursor.getString(groupMembershipCursor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID));

            // Get Titles from Groups table using groupIDs from groupRowID
            if(groupRowID != null)
            {
                uri = ContactsContract.Groups.CONTENT_URI;
                projection = new String[] {ContactsContract.Groups.TITLE};
                where = ContactsContract.Groups._ID + " = ?";
                whereParameters = new String[] {groupRowID};

                Cursor groupCursor = contentResolver.query(uri, projection, where, whereParameters, null);

                if(groupCursor.moveToFirst())
                {
                    String groupTitle = groupCursor.getString(groupCursor.getColumnIndex(ContactsContract.Groups.TITLE));

                    if(groupTitle != null)
                        groups = groups.concat(groupTitle);
                }
                groupCursor.close();
            }

            recordsAvailable = groupMembershipCursor.moveToNext();
            if(recordsAvailable)
                groups = groups.concat(", ");
        }
        groupMembershipCursor.close();

        return groups;
    }

    public String getWebsite(String contactID)
    {
        String website = null;
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{contactID,
                ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE};

        Cursor websiteCursor = this.contentResolver.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
        if (websiteCursor.moveToFirst())
        {
            website = websiteCursor.getString(websiteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
        }
        websiteCursor.close();
        return website;
    }

    public String getNotes(String contactID)
    {
        String notes = null;
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{contactID,
                ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};

        Cursor notesCursor = this.contentResolver.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
        if (notesCursor.moveToFirst())
        {
            notes = notesCursor.getString(notesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
        }
        notesCursor.close();
        return notes;
    }

    public String[] getContactNumberFromUri(Uri contactUri)
    {
        try
        {
            String[] contactDetails = new String[]{null, null};
            Cursor cursor = contentResolver.query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            contactDetails[0] = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactDetails[1] = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Profile.DISPLAY_NAME));
            return contactDetails;
        }
        catch (Exception e)
        {
            Log.d(TAG, e.toString());
            return null;
        }
    }
}