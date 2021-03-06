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
 *       Copyright 2013-2015 ForgeRock AS.
 */

package org.forgerock.contactmanager;

import static android.speech.RecognizerIntent.*;
import static android.view.KeyEvent.*;
import static android.view.ViewGroup.LayoutParams.*;
import static java.lang.String.*;
import static java.text.Normalizer.*;
import static java.util.Arrays.*;
import static org.forgerock.contactmanager.Constants.*;
import static org.forgerock.contactmanager.MapperConstants.*;
import static org.forgerock.contactmanager.PagedResultCookie.*;
import static org.forgerock.contactmanager.Utils.*;

import java.text.Normalizer.Form;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * This class is main activity of this application.
 */
public class SearchActivity extends AugmentedActivity {

    private static final int SPEECH_REQUEST_CODE = 0;

    /**
     * The progress bar linked to the search.
     */
    ProgressBar progressBar;

    /**
     * The main list where results are displayed.
     */
    ListView lvSearchResult;

    /**
     * Index of the current page displayed.
     */
    static int currentPage;

    /**
     * Search bar.
     */
    EditText searchText;

    /**
     * List view footer.
     */
    LinearLayout footer;

    /** The filters alert dialog. */
    AlertDialog fDialog;

    private List<Filter> filters;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Sets the context.
        new AppContext(getApplicationContext());

        searchText = (EditText) findViewById(R.id.list_search_text);
        progressBar = (ProgressBar) findViewById(R.id.list_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.YELLOW, android.graphics.PorterDuff.Mode.MULTIPLY);

        lvSearchResult = (ListView) findViewById(R.id.lvSearchResult);

        ImageButton speakSearchButton = (ImageButton) findViewById(R.id.btn_search_speech);

        displayContactList(null);

        searchText.addTextChangedListener(new PendingTextWatcher(700) {
            @Override
            public void afterTextChangedDelayed(final Editable s) {
                if (s.length() > 0) {
                    initializePageCounter();
                    new AsyncServerRequest(SearchActivity.this, progressBar).execute(getFilterRequest(s.toString()));
                } else {
                    displayContactList(null);
                }
            }
        });

        searchText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (event.getAction() == ACTION_DOWN && keyCode == KEYCODE_ENTER
                        && searchText.getText() != null && searchText.getText().length() > 0) {
                    initializePageCounter();
                    new AsyncServerRequest(SearchActivity.this, progressBar).execute(getFilterRequest(searchText
                            .getText().toString()));
                    return true;
                }
                return false;
            }
        });

        speakSearchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                speechRecognizer();
            }
        });
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
        } else if (itemId == R.id.search_action_filters) {
            fDialog = new AlertDialog.Builder(this).create();
            fDialog.setTitle(R.string.menu_filter_label);
            fDialog.setView(createFiltersSubMenuView());
            fDialog.show();
        }
        return true;
    }

    private View createFiltersSubMenuView() {
        final RadioGroup filterGroup = new RadioGroup(this);
        filterGroup.setOrientation(RadioGroup.VERTICAL);
        if (filters == null) {
            filters = asList(Filter.values());
        }
        for (final Filter f : filters) {
            final RadioButton rb = new RadioButton(this);
            rb.setTextSize(10);
            rb.setText(f.getDescription());
            rb.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    filterGroup.clearCheck();
                    rb.setChecked(true);
                    saveSearchFilter(f.getName());
                    fDialog.dismiss();
                }
            });

            filterGroup.addView(rb);
            if (loadSearchFilter().equals(f.getName())) {
                filterGroup.check(rb.getId());
            }
        }

        final LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(filterGroup);

        return layout;
    }

    void displayContactList(final JSONObject contactList) {
        final List<JSONObject> contacts = Utils.read(contactList != null ? contactList.toString() : null);
        final SearchListAdapter adapter = new SearchListAdapter(SearchActivity.this, contacts);
        if (contacts == null || (contacts != null && contacts.isEmpty())) {
            lvSearchResult.setEmptyView(findViewById(R.id.empty_list_item));
        } else {
            if (contacts.size() < PAGED_RESULT && currentPage == 0) {
                lvSearchResult.removeFooterView(footer);
            } else if (lvSearchResult.getFooterViewsCount() == 0) {
                displayFooterList();
            }
            // Sets the pagedResultsCookie used to manage pagination.
            setCookie(contactList.optString(PAGED_RESULT_COOKIE));
        }

        lvSearchResult.setAdapter(adapter);

        lvSearchResult.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> arg0, final View v, final int position, final long id) {
                final Object o = lvSearchResult.getItemAtPosition(position);
                final Intent myIntent = new Intent(SearchActivity.this, ContactActivity.class);
                myIntent.putExtra("contact", o.toString());
                startActivity(myIntent);
            }
        });
    }

    /** TODO to remove with listview endless implementation. */
    void displayFooterList() {
        footer = new LinearLayout(this);
        footer.setOrientation(LinearLayout.HORIZONTAL);

        final ImageButton btnLoadLess = new ImageButton(this);
        btnLoadLess.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 0.5f));
        btnLoadLess.setImageResource(R.drawable.ic_action_previous);
        btnLoadLess.setBackgroundColor(getResources().getColor(R.color.transparent));
        btnLoadLess.setContentDescription(getResources().getText(R.string.search_previous));
        footer.addView(btnLoadLess);

        final ImageButton btnLoadMore = new ImageButton(this);
        btnLoadMore.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 0.5f));
        btnLoadMore.setImageResource(R.drawable.ic_action_next);
        btnLoadMore.setBackgroundColor(getResources().getColor(R.color.transparent));
        btnLoadMore.setContentDescription(getResources().getText(R.string.search_next));
        footer.addView(btnLoadMore);

        lvSearchResult.addFooterView(footer);

        btnLoadLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                if (currentPage > 0) {
                    currentPage--;
                    new AsyncServerRequest(SearchActivity.this, progressBar, currentPage)
                            .execute(getFilterRequest(searchText.getText().toString()));
                }
            }
        });

        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                currentPage++;
                new AsyncServerRequest(SearchActivity.this, progressBar).execute(getFilterRequest(searchText.getText()
                        .toString()));
            }
        });
    }

    private String getFilterRequest(final String searchValue) {
        final String encodedValue = Utils.getURLEncoded(searchValue);
        final Filter filter = Filter.forName(loadSearchFilter());
        return format(filter.getExpression(), encodedValue, encodedValue);
    }

    private void speechRecognizer() {
        final Intent intent = new Intent(ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(EXTRA_LANGUAGE_MODEL, LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            final List<String> results = data.getStringArrayListExtra(EXTRA_RESULTS);
            // Canonical decomposition of the accent marks && replaces all non ascii char by an empty string.
            final String spokenText = normalize(results.get(0), Form.NFD)
                    .replaceAll("[^\\p{ASCII}]", "");
            searchText.setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    static void initializePageCounter() {
        initialize();
        currentPage = 0;
    }
}
