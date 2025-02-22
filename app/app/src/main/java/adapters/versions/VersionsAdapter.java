/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package adapters.versions;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;

import org.unfoldingword.mobile.R;

import java.util.List;

/**
 * Created by PJ Fechner
 * Adapter for Versions
 */
public class VersionsAdapter implements ExpandableListAdapter {

//    private final static String TAG = "CollapseVersionAdapter";

    private Fragment parentFragment;

    private List<VersionViewModel> models;
//    private VersionAdapterListener listener;
    private long selectedVersionId;

    //region setup

    public VersionsAdapter(Fragment fragment, List<VersionViewModel> models, long selectedVersionId) {
        this.parentFragment = fragment;
        this.models = models;
        this.selectedVersionId = selectedVersionId;
//        this.listener = listener;
    }

    public void updateModels(){

        for(VersionViewModel model : models){
            model.updateContent();
        }
    }

    public int getIndexOfChosenVersion(){

        int i = 0;
        for (VersionViewModel model : models){
            if(model.getVersion().getId() == selectedVersionId){
                return i;
            }
            i++;
        }
        return -1;
    }


    @Override
    public VersionViewModel getGroup(int groupPosition) {
        return models.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return models.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return getCombinedGroupId(groupPosition);
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return groupId;
    }

    //endregion

    //region adapter methods

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        VersionTitleViewHolder holder;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_version_group, parent, false);
            holder = new VersionTitleViewHolder(convertView, groupPosition);
            convertView.setTag(holder);
        }
        else{
            holder = (VersionTitleViewHolder) convertView.getTag();
        }
        VersionViewModel model = getGroup(groupPosition);
        holder.updateWithModel(getContext(), model, model.getVersion().getId() == selectedVersionId);
        holder.setExpanded(isExpanded);
        return convertView;
    }

//    @Override
//    public void onGroupCollapsed(int groupPosition) {
//        super.onGroupCollapsed(groupPosition);
//    }
//
//    @Override
//    public void onGroupExpanded(int groupPosition) {
//        super.onGroupExpanded(groupPosition);
//    }

    @Override
    public VersionViewModel.ResourceViewModel getChild(int groupPosition, int childPosition) {
        return models.get(groupPosition).getResources().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getCombinedChildId(groupPosition, childPosition);
    }

    @Override
    public long getCombinedChildId(long groupPosition, long childPosition) {
        return (groupPosition * 1000) + childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return models.get(groupPosition).getResources().size();
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

        VersionViewHolder holder;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_version, parent, false);
            holder = new VersionViewHolder(convertView);
            convertView.setTag(holder);
        }
        else{
            holder = (VersionViewHolder) convertView.getTag();
        }

        holder.updateViews(getChild(groupPosition, childPosition));
        return convertView;
    }

    //endregion

    //region helpers

    private Context getContext(){
        return this.parentFragment.getActivity();
    }


    //endregion

    //region reloading

    private void reload(List<VersionViewModel> models){
        this.models = models;
    }

    //endregion

    //region group methods


    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    //endregion

//    public interface VersionAdapterListener{
//        /**
//         * User chose a version
//         * @param version Version that was chosen
//         */
//        void versionWasSelected(Version version);
//
//        /**
//         * The adapter is loading
//         * @param loading true if the adapter is loading
//         */
//        void isLoading(boolean loading);
//    }
}

