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
 */

package org.forgerock.contactmanager;

import java.util.LinkedList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * The 'settings' class activity.
 */
public class SettingsActivity extends AugmentedActivity {
    /**
     * The selected and current server configuration.
     */
    private ServerConfiguration serverConfiguration;

    private boolean isNoServerConfigured;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        final Spinner serverSpinner = (Spinner) findViewById(R.id.settings_spinner_servers);
        final ToggleButton toogleSSL = (ToggleButton) findViewById(R.id.settings_toggle_ssl);

        final LinkedList<ServerConfiguration> servers = Utils.loadRegisteredServerList();
        final TextView tvSrvAddress = (TextView) findViewById(R.id.settings_selected_server_address_content);
        final TextView tvAuthentication = (TextView) findViewById(R.id.settings_selected_authentication_content);

        final SpinnerAdapter adapter = new ServerListAdapter(this, servers);
        serverSpinner.setAdapter(adapter);

        if (servers != null && servers.size() > 0) {
            serverListViewMode();
            serverConfiguration = (ServerConfiguration) adapter.getItem(ServerListAdapter.getSelectedIndex());
            serverSpinner.setSelection(ServerListAdapter.getSelectedIndex());
            tvSrvAddress.setText(serverConfiguration.getAddress());
        } else {
            emptyServerListViewMode();
        }

        serverSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(final AdapterView<?> arg0, final View arg1, final int arg2, final long arg3) {
                serverConfiguration = (ServerConfiguration) serverSpinner.getAdapter().getItem(
                        serverSpinner.getSelectedItemPosition());
                AppContext.setServerConfiguration(serverConfiguration);
                Utils.saveActiveServer(serverConfiguration);
                ServerListAdapter.setSelectedIndex(arg0.getSelectedItemPosition());

                Toast.makeText(
                        getApplicationContext(),
                        String.format(getResources().getString(R.string.warning_selected_server),
                                serverConfiguration.getServerName()), Toast.LENGTH_SHORT).show();

                tvSrvAddress.setText(serverConfiguration.getAddress());
                toogleSSL.setChecked(serverConfiguration.isSSL());
                tvAuthentication.setText(getResources().getString(R.string.settings_authentication_basic));

            }

            @Override
            public void onNothingSelected(final AdapterView<?> arg0) {
                // Nothing to do.
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            refreshSpinner();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.settings_action_add) {
            final Intent intent = new Intent(this, ServerWizardPart1Activity.class);
            startActivityForResult(intent, 1);
            return true;
        } else if (itemId == R.id.settings_action_edit) {
            final Intent intentEdit = new Intent(SettingsActivity.this, ServerWizardPart1Activity.class);
            if (serverConfiguration != null) {
                intentEdit.putExtra("isEdit", true);
                startActivityForResult(intentEdit, 1);
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "No selected server", Toast.LENGTH_SHORT).show();
            }

        } else if (itemId == R.id.settings_action_delete) {
            if (serverConfiguration != null) {
                deleteSelectedServerWarning();
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "No selected server", Toast.LENGTH_SHORT).show();
            }

        } else if (itemId == R.id.settings_action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            final MenuItem mi = menu.getItem(i);
            if (mi.getItemId() == R.id.settings_action_edit) {
                mi.setEnabled(!isNoServerConfigured);
            } else if (mi.getItemId() == R.id.settings_action_delete) {
                mi.setEnabled(!isNoServerConfigured);
            }
        }
        return true;
    }

    /**
     * The delete action. Displays a pop-up before final choice.
     */
    private void deleteSelectedServerWarning() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.warning_delete_server, serverConfiguration.getServerName()))
                .setMessage(getResources().getString(R.string.warning_approval))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        /* When yes is clicked, proceed to deletion of the selected server. */
                        final boolean isOk = Utils.deleteServerConfigurationFromPreferences(serverConfiguration
                                .getServerName());
                        if (isOk) {
                            refreshSpinner();
                        } else {
                            Toast.makeText(getApplicationContext(), "Could not retrieve data ", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                }).setNegativeButton("No", null).show();
    }

    /**
     * Refreshes the spinner.
     */
    private void refreshSpinner() {
        final Spinner serverSpinner = (Spinner) findViewById(R.id.settings_spinner_servers);
        final ServerListAdapter adapter = new ServerListAdapter(this, Utils.loadRegisteredServerList());
        final ServerConfiguration activeServer = Utils.loadActiveServer();
        serverSpinner.setAdapter(adapter);
        serverSpinner.setSelection(adapter.getPosition(activeServer.getServerName()));
        if (serverSpinner.getCount() == 0) {
            emptyServerListViewMode();
        } else {
            serverListViewMode();
        }
    }

    /**
     * Sets an empty view if no server list configurations found.
     */
    private void emptyServerListViewMode() {
        final ScrollView settingsScrollview = (ScrollView) findViewById(R.id.settingsScrollview);
        final TextView tvEmptyServerList = (TextView) findViewById(R.id.settings_empty_server_list);
        settingsScrollview.setVisibility(View.GONE);
        tvEmptyServerList.setVisibility(View.VISIBLE);
        isNoServerConfigured = true;
    }

    /**
     * Displays the server list configurations.
     */
    private void serverListViewMode() {
        final ScrollView settingsScrollview = (ScrollView) findViewById(R.id.settingsScrollview);
        final TextView tvEmptyServerList = (TextView) findViewById(R.id.settings_empty_server_list);
        tvEmptyServerList.setVisibility(View.GONE);
        settingsScrollview.setVisibility(View.VISIBLE);
        isNoServerConfigured = false;
    }
}
