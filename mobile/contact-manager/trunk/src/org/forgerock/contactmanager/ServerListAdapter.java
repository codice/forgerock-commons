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
 *       Copyright 2013 ForgeRock AS.
 */

package org.forgerock.contactmanager;

import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * This class, used by the list contained in the settings activity, manage the display of the list containing server
 * configuration. (spinner)
 */
public class ServerListAdapter extends BaseAdapter {

    private LinkedList<ServerConfiguration> listData = new LinkedList<ServerConfiguration>();
    private final LayoutInflater layoutInflater;
    private static int selectedIndex = 0;

    /**
     * Constructor.
     *
     * @param context
     *            The context linked to this adapter.
     * @param listData
     *            The adapter's data.
     */
    public ServerListAdapter(final Context context, final LinkedList<ServerConfiguration> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData != null ? listData.size() : 0;
    }

    @Override
    public Object getItem(final int position) {
        return listData != null ? listData.get(position) : -1;
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
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_servers_row, null);
            holder = new ViewHolder();
            holder.tvServerName = (TextView) convertView.findViewById(R.id.ls_server_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (!listData.isEmpty()) {
            final String name = listData.get(position).getServerName();
            holder.tvServerName.setText(name);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvServerName;
    }

    /**
     * Sets the selected index.
     *
     * @param index
     *            The selected index.
     */
    public static void setSelectedIndex(final int index) {
        selectedIndex = index;
    }

    /**
     * Returns the selected index.
     *
     * @return The selected index.
     */
    public static int getSelectedIndex() {
        return selectedIndex;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    /**
     * Refreshes the list with new data.
     *
     * @param newServerList
     *            The new list of server configurations.
     */
    public void refresh(final LinkedList<ServerConfiguration> newServerList) {
        this.listData = newServerList;
        if (listData != null && !listData.isEmpty()) {
            setSelectedIndex(0);
        }
        this.notifyDataSetChanged();
    }
}
