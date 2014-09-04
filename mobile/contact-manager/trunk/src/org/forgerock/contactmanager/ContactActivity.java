/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 *       Copyright 2013-2014 ForgeRock AS.
 *       Fancy Avatars, © 2009 Brandon Mathis, http://brandonmathis.com/projects/fancy-avatars/
 */

package org.forgerock.contactmanager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Launches the Contact activity screen.
 */
public class ContactActivity extends AugmentedActivity {

    /**
     * The current history. Used to go back when this activity is reloaded.
     */
    static LinkedList<JSONObject> historyBack;

    /**
     * The current displayed contact.
     */
    Contact myDisplayedContact;

    /**
     * Actions are linked to image buttons.
     */
    public enum Action {
        /**
         * Phone call .
         */
        CALL,
        /**
         * Short text message action.
         */
        SEND_SMS,
        /**
         * Email action.
         */
        MAIL,
        /**
         * Geolocation action.
         */
        GEOLOCATION,
        /**
         * Add contact to phone address book action.
         */
        ADD_CONTACT,
        /**
         * See linked manager action.
         */
        SEE_MANAGER
    }

    /**
     * The section represents information groups which need to be displayed.
     */
    public enum SECTION {
        /**
         * Home phone(s).
         */
        HOME_PHONE,
        /**
         * Mobile phone(s).
         */
        MOBILE_PHONE,
        /**
         * Email address(es).
         */
        MAIL,
        /**
         * Postal address.
         */
        ADDRESS,
        /**
         * Manager.
         */
        MANAGER
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Retrieves elements from layout.
        final TextView tvContactName = (TextView) findViewById(R.id.ctName);
        final TextView tvOrganization = (TextView) findViewById(R.id.ctOrganization);
        final TextView tvFunction = (TextView) findViewById(R.id.ctFunction);
        final ImageView ivPhoto = (ImageView) findViewById(R.id.ctPhoto);

        final ImageButton ctArrowRightHeaderBtn = (ImageButton) findViewById(R.id.ctArrowRightHeaderBtn);
        final ImageButton ctAddBtn = (ImageButton) findViewById(R.id.ctAddBtn);

        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.ctMainLayout);

        // Retrieve result from previous activity.
        final String contactFromIntent = getIntent().getStringExtra("contact");

        if (contactFromIntent != null) {

            myDisplayedContact = new Contact();

            try {
                myDisplayedContact.fillDetailsFromJSON(contactFromIntent);
                tvContactName.setText(myDisplayedContact.getDisplayName());
                tvFunction.setText(myDisplayedContact.getFunction());
                tvOrganization.setText(myDisplayedContact.getOrganization());

                display(SECTION.MOBILE_PHONE, false);
                display(SECTION.HOME_PHONE);
                display(SECTION.MAIL);
                display(SECTION.ADDRESS);

                final ImageLoader imageLoader = new ImageLoader(AppContext.getContext());
                imageLoader.displayIn(myDisplayedContact.getPhotoLink(), ivPhoto);

                display(SECTION.MANAGER);

            } catch (final JSONException e) {
                Toast.makeText(getApplicationContext(), "Unable to load the contact data.", Toast.LENGTH_SHORT).show();
                Log.w("Json error", "Unable to load contact data : " + e.getMessage());
            }
        } else {
            finish();
        }

        // Header return arrow click.
        ctArrowRightHeaderBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                // Returns to previous activity.
                onBackPressed();
            }
        });

        // Add to contact button click.
        ctAddBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                launchAddContact();
            }
        });

        // Swipe right on the image layout.
        mainLayout.setOnTouchListener(new AbstractSwipeTouchListener() {

            @Override
            boolean onSwipeRight() {
                onBackPressed();
                return true;
            }
        });
    }

    /**
     * Restarts the activity with the selected manager contact (from previous screen).
     *
     * @param contactManager
     *            The manager the user clicked on.
     */
    void setDataAndRestart(final JSONObject contactManager) {
        final Intent intent = getIntent();
        intent.putExtra("contact", contactManager.toString());
        onRestart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        final Intent i = getIntent();
        startActivity(i);
    }

    /**
     * Opens the Android used application to send an SMS to the specified number.
     *
     * @param number
     *            The SMS will be sent to this telephone number.
     */
    private void launchSMSAction(final String number) {
        final Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("sms:" + number));
        startActivity(intent);
    }

    /**
     * Opens a new activity to call the selected phone number.
     *
     * @param number
     *            The phone number to call.
     */
    private void launchCallPhoneAction(final String number) {
        final Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        startActivity(callIntent);
    }

    /**
     * Launches the new activity to locate the specified address.
     *
     * @param address
     *            The address to locate.
     */
    private void launchLocationAction(final String address) {
        try {
            // Checks if Google Maps is supported on given device
            Class.forName("com.google.android.maps.MapActivity");
            final Intent i = new Intent("android.intent.action.VIEW", Uri.parse("geo:0,0?q="
                    + address.replace(' ', '+')));
            startActivity(i);
        } catch (final Exception e) {
            Toast.makeText(getApplicationContext(), "Google Maps is not installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Displays an alert dialog. If 'yes' is answered, adds the contact as the android provider always add it to phone
     * address book even if you select discard.
     */
    private void launchAddContact() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(
                String.format(getResources().getString(R.string.contact_warning_add_contact),
                        myDisplayedContact.getDisplayName())).setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        // Adds the contact
                        addContact();
                    }
                }).setNegativeButton("No", null).show();
    }

    /**
     * Opens user favorite mailer to send an email to the specified email address.
     *
     * @param email
     *            The email address to send the message.
     */
    private void launchMailAction(final String email) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
        startActivity(Intent.createChooser(intent, "Send an email to " + email));
    }

    private void launchSeeManagerAction(final String managerId) {
        if (historyBack == null) {
            historyBack = new LinkedList<JSONObject>();
        }
        historyBack.add(myDisplayedContact.getContact());
        new AsyncServerRequest(ContactActivity.this, false).execute(String.format(Constants.SEARCH_SPECIFICUSER_BY_ID,
                managerId));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.search_action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (itemId == R.id.search_action_about) {
            startActivity(new Intent(this, AboutActivity.class));
        }
        return true;
    }

    /**
     * Displays an alert dialog. If 'yes' is answered, launch the call.
     *
     * @param phoneNumber
     *            The number to call.
     */
    private void launchCall(final String phoneNumber) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(String.format(getResources().getString(R.string.warning_call_number), phoneNumber))
                .setIcon(android.R.drawable.ic_dialog_dialer)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {

                        launchCallPhoneAction(phoneNumber);
                    }
                }).setNegativeButton("No", null).show();
    }

    /**
     * This function add a contact to the phone address book contact. Adding a contact is managed by the content
     * provider (android) and on Froyo(2.2), when it tries to add an already registered contact, the process fails.
     */
    private void addContact() {
        final ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        final int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI).withValue(RawContacts.ACCOUNT_TYPE, null)
                .withValue(RawContacts.ACCOUNT_NAME, null).build());

        // Photo
        try {
            final ImageView ivPhoto = (ImageView) findViewById(R.id.ctPhoto);

            final ContentProviderOperation.Builder photoBuilder = ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
            final ByteArrayOutputStream image = new ByteArrayOutputStream();
            ((BitmapDrawable) ivPhoto.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, image);
            photoBuilder.withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, image.toByteArray());

            ops.add(photoBuilder.build());
        } catch (final Exception ex) {
            Log.w("Unable to export contact photo", ex.getMessage());
        }

        // Phone Number
        if (myDisplayedContact.getHomePhoneNumbers().size() > 0) {
            for (final String homePhone : myDisplayedContact.getHomePhoneNumbers()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE).withValue(Phone.NUMBER, homePhone)
                        .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                        .withValue(Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME).build());
            }
        }

        // Mobile Number
        if (myDisplayedContact.getHomePhoneNumbers().size() > 0) {
            for (final String mobile : myDisplayedContact.getMobilePhoneNumbers()) {
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobile)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
            }
        }

        // Display contact name
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, getContent((TextView) findViewById(R.id.ctName))).build());

        // Email details
        if (myDisplayedContact.getMails().size() > 0) {
            for (final String email : myDisplayedContact.getMails()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                        .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, "2").build());
            }
        }

        // Postal Address
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POBOX, myDisplayedContact.getAddress())

                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, myDisplayedContact.getLocation())

                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
                        myDisplayedContact.getPostalCode())

                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, myDisplayedContact.getState())

                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, "3")

                .build());

        // Organization details
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY,
                        getContent((TextView) findViewById(R.id.ctOrganization)))
                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Organization.TITLE,
                        getContent((TextView) findViewById(R.id.ctFunction)))
                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, "0")

                .build());

        final ContentResolver cr = getContentResolver();
        ContentProviderResult[] res = null;
        try {
            res = cr.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (final RemoteException e) {
            Log.w("RemoteException", e.getMessage());
            e.printStackTrace();
        } catch (final OperationApplicationException e) {
            Log.w("OperationApplicationException", e.getMessage());
        }
        final Uri uri = ContactsContract.RawContacts.getContactLookupUri(cr, res[0].uri);
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_EDIT);
        intent.setData(uri);
        startActivityForResult(intent, RESULT_OK);

    }

    private String getContent(final TextView textView) {
        if (textView != null) {
            if (textView.getText() != null) {
                return textView.getText().toString();
            }
        }
        return "";
    }

    /**
     * Called when the activity has detected the user's press of the back key. As we reload the same activity for
     * manager display, we need to keep an history of the contact activity.
     */
    @Override
    public void onBackPressed() {
        // Contact would be reloaded on back button.
        // On manager click, the contact is added to history.
        if (historyBack != null && historyBack.size() >= 1) {
            final JSONObject contact = historyBack.getLast();
            historyBack.remove(historyBack.getLast());
            setDataAndRestart(contact);
        } else {
            if (historyBack != null) {
                historyBack.clear();
            }
            finish();
        }
    }

    /**
     * Displays the selected section.
     *
     * @param section
     *            The section to display.
     */
    private void display(final SECTION section) {
        display(section, true);
    }

    /**
     * Displays the selected section.
     *
     * @param section
     *            The section to display.
     * @param isEndingWithASeparator
     *            {@code true} if a separation is needed at the end of the section.
     */
    private void display(final SECTION section, final boolean isEndingWithASeparator) {
        LinkedList<String> data = new LinkedList<String>();
        LinkedList<String[]> managers = null;
        if (section == SECTION.MOBILE_PHONE) {
            data = myDisplayedContact.getMobilePhoneNumbers();
        } else if (section == SECTION.HOME_PHONE) {
            data = myDisplayedContact.getHomePhoneNumbers();
        } else if (section == SECTION.MAIL) {
            data = myDisplayedContact.getMails();
        } else if (section == SECTION.ADDRESS) {
            if (!TextUtils.isEmpty(myDisplayedContact.getFullAddress())) {
                data.add(myDisplayedContact.getFullAddress());
            }
        } else if (section == SECTION.MANAGER) {
            managers = myDisplayedContact.getManagers();
        }

        if (data != null && data.size() > 0 || managers != null && managers.size() > 0) {
            final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ctDetailsLayout);

            final TableLayout tableLayout = getTableLayout();
            linearLayout.addView(tableLayout);

            if (section == SECTION.MOBILE_PHONE) {
                tableLayout.addView(getSectionLineTitle(R.string.contact_mobile_title));
            } else if (section == SECTION.HOME_PHONE) {
                tableLayout.addView(getSectionLineTitle(R.string.contact_home_phone_title));
            } else if (section == SECTION.MAIL) {
                tableLayout.addView(getSectionLineTitle(R.string.contact_mail_title));
            } else if (section == SECTION.ADDRESS) {
                tableLayout.addView(getSectionLineTitle(R.string.contact_address_title));
            }

            if (section == SECTION.MANAGER) {
                tableLayout.addView(getSectionLineTitle(R.string.contact_manager_title));
                for (final String[] val : managers) {
                    createRow(tableLayout, section, val[1], val[0]);
                }
            } else {
                for (final String val : data) {
                    createRow(tableLayout, section, val, null);
                }
            }

            if (isEndingWithASeparator) {
                linearLayout.addView(getSectionSeparator());
            }

        }
    }

    /**
     * The section separator. Usually a simple line.
     *
     * @return The section separator.
     */
    private View getSectionSeparator() {
        final View view = new View(this);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
        view.setPadding(0, 5, 0, 0);
        view.setBackgroundColor(getResources().getColor(R.color.separator));
        return view;
    }

    /**
     * Returns the table layout to put the new graphical elements.
     *
     * @return The table layout to put the new graphical elements.
     */
    private TableLayout getTableLayout() {
        final TableLayout tableLayout = new TableLayout(this);
        tableLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        tableLayout.setShrinkAllColumns(true);
        tableLayout.setStretchAllColumns(true);
        return tableLayout;
    }

    /**
     * Returns the line where the section title is displayed.
     *
     * @param resId
     *            The id of the string title.
     * @return The text view representing the section title.
     */
    private TextView getSectionLineTitle(final int resId) {
        final TextView header = new TextView(this);
        header.setText(resId);
        header.setTextSize(12);
        header.setTextAppearance(getApplicationContext(), R.style.contactTitle);
        return header;
    }

    /**
     * Creates a row on a selected table layout.
     *
     * @param tableLayout
     *            The table layout where to insert the row.
     * @param section
     *            The selected section.
     * @param val1
     *            The value to apply to main text.
     * @param val2
     *            The optional value to apply to link (manager only here).
     */
    private void createRow(final TableLayout tableLayout, final SECTION section, final String val1, final String val2) {
        final TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        final TextView tvManager = new TextView(this);
        final LayoutParams params = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.8f);
        tvManager.setLayoutParams(params);
        tvManager.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        tvManager.setText(val1);
        tvManager.setTextAppearance(getApplicationContext(), R.style.contactDetails);
        tvManager.setPadding(5, 5, 0, 0);

        tableRow.addView(tvManager, 0);

        final LinearLayout actionsLayout = new LinearLayout(this);
        actionsLayout.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        actionsLayout.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.2f));

        if (section == SECTION.MOBILE_PHONE) {
            actionsLayout.addView(action(Action.SEND_SMS, val1));
            actionsLayout.addView(action(Action.CALL, val1));
        } else if (section == SECTION.HOME_PHONE) {
            actionsLayout.addView(action(Action.CALL, val1));
        } else if (section == SECTION.MAIL) {
            actionsLayout.addView(action(Action.MAIL, val1));
        } else if (section == SECTION.ADDRESS) {
            actionsLayout.addView(action(Action.GEOLOCATION, val1));
        } else if (section == SECTION.MANAGER) {
            tvManager.setText(val1);
            actionsLayout.addView(action(Action.SEE_MANAGER, val2));
        }

        tableRow.addView(actionsLayout, 1);

        tableLayout
                .addView(tableRow, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    /**
     * Returns the image buttons for the selected action.
     *
     * @param choice
     *            The action selected.
     * @param data
     *            The data linked to this action.
     * @return An image button for the selected action.
     */
    private ImageButton action(final Action choice, final String data) {
        final ImageButton actionButton = new ImageButton(this);
        final LayoutParams actionLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        actionButton.setLayoutParams(actionLayoutParams);
        actionButton.setBackgroundResource(R.drawable.common_buttons_actions);
        actionButton.setPadding(0, 0, 0, 0);
        OnClickListener myActionListener = null;
        if (choice == Action.CALL) {
            actionButton.setImageResource(R.drawable.ic_action_call);
            actionButton.setContentDescription(getResources().getText(R.string.act_call_description));
            myActionListener = new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    launchCall(data);
                }
            };
        } else if (choice == Action.SEND_SMS) {
            actionButton.setImageResource(R.drawable.ic_action_chat);
            actionButton.setContentDescription(getResources().getText(R.string.act_sms_description));
            myActionListener = new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    launchSMSAction(data);
                }
            };
        } else if (choice == Action.GEOLOCATION) {
            actionButton.setImageResource(R.drawable.ic_action_location_found);
            actionButton.setContentDescription(getResources().getText(R.string.act_geolocation_description));
            myActionListener = new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    launchLocationAction(data);
                }
            };
        } else if (choice == Action.MAIL) {
            actionButton.setImageResource(R.drawable.ic_action_email);
            actionButton.setContentDescription(getResources().getText(R.string.act_mail_description));
            myActionListener = new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    launchMailAction(data);
                }
            };
        } else if (choice == Action.SEE_MANAGER) {
            actionButton.setImageResource(R.drawable.ic_action_person);
            actionButton.setContentDescription(getResources().getText(R.string.act_manager_description));
            myActionListener = new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    launchSeeManagerAction(data);
                }
            };
        }
        actionButton.setOnClickListener(myActionListener);
        return actionButton;
    }

}
