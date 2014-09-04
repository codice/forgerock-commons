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

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * The adapter class sets for the search result list from search result activity.
 */
public class SearchListAdapter extends BaseAdapter {
    private final LinkedList<JSONObject> listData;

    private final LayoutInflater layoutInflater;

    /**
     * Constructor.
     *
     * @param context
     *            The context of the adapter.
     * @param listData
     *            The data to display.
     */
    public SearchListAdapter(final Context context, final LinkedList<JSONObject> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData != null ? listData.size() : 0;
    }

    @Override
    public Object getItem(final int position) {
        return listData != null ? listData.get(position) : 0;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    /**
     * Gets a View that displays the data at the specified position in the data set. You can either create a View
     * manually or inflate it from an XML layout file. When the View is inflated, the parent View will apply default
     * layout parameters unless you use inflate to specify a root view and to prevent attachment to the root.
     *
     * @param position
     *            The position of the item within the adapter's data set of the item whose view we want.
     * @param convertView
     *            The old view to reuse, if possible.
     * @param parent
     *            The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;

        if (listData != null) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_search_layout, null);
                holder = new ViewHolder();
                holder.tvContactName = (TextView) convertView.findViewById(R.id.contactName);
                holder.ivJpegPhoto = (ImageView) convertView.findViewById(R.id.ivJpeg);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Loads data from JSON.
            final JSONObject contact = listData.get(position);
            final JSONObject contactDetails = contact.optJSONObject(MapperConstants.CONTACT_INFORMATION);

            holder.tvContactName.setText(contact.optString(MapperConstants.DISPLAY_NAME));
            final String strBMP = !TextUtils.isEmpty(contactDetails.optString(MapperConstants.JPEG_PHOTO))
                    ? contactDetails.optString(MapperConstants.JPEG_PHOTO)
                            : contactDetails.optString(MapperConstants.JPEG_URL);

            final ImageLoader imageLoader = new ImageLoader(AppContext.getContext());
            imageLoader.displayIn(strBMP, holder.ivJpegPhoto);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView tvContactName;
        ImageView ivJpegPhoto;
    }
}
