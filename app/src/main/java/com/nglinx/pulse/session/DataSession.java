package com.nglinx.pulse.session;

import android.content.Context;

import com.nglinx.pulse.models.GroupMemberModel;
import com.nglinx.pulse.models.GroupModel;
import com.nglinx.pulse.models.UserModel;

import java.util.TimeZone;

public class DataSession {

    private static DataSession myObj;

    public static DataSession getInstance() {
        if (myObj == null) {
            myObj = new DataSession();
        }
        return myObj;
    }

    private DataSession() {
        selected_group_id = "";
        selected_group_member_id = "";
        zoom = 16;
        sharing_group_position_open_status = new boolean[2];
        sharing_group_position_open_status[0] = false;
        sharing_group_position_open_status[1] = false;
        //appKey = FirebaseInstanceId.getInstance().getToken();
    }

    private String username;

    private String password;

    private UserModel userModel;

    private TimeZone localTimeZone;

    private String selected_group_id;

    private String selected_group_name;

    private String selected_group_member_id;

    private float zoom;

    //private GroupAdapter groupAdapter;

    private String selected_group_member_name;

    private boolean[] sharing_group_position_open_status;

    private String jSessionId;

    private String rememberMeId;

    public static String version;

    public static String location;

    public String gcmId;

    public String appKey;

    private Context context;

    private boolean showMyLocation;

    public String getSelectedDeviceId() {
        return selectedDeviceId;
    }

    public void setSelectedDeviceId(String selectedDeviceId) {
        this.selectedDeviceId = selectedDeviceId;
    }

    private String selectedDeviceId;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSelected_group_name() {
        return selected_group_name;
    }

    public void setSelected_group_name(String selected_group_name) {
        this.selected_group_name = selected_group_name;
    }

    public String getSelected_group_member_name() {
        return selected_group_member_name;
    }

    public void setSelected_group_member_name(String selected_group_member_name) {
        this.selected_group_member_name = selected_group_member_name;
    }

    public boolean isSharingGroupOpened(int groupPosition) {
        return sharing_group_position_open_status[groupPosition];
    }

    public void setSharingGroupOpenStatus(int groupPosition, boolean openStatus) {
        this.sharing_group_position_open_status[groupPosition] = openStatus;
    }

    public boolean[] getSharingGroupPositions() {
        return sharing_group_position_open_status;
    }


    /*public GroupAdapter getGroupAdapter() {
        return groupAdapter;
    }

    public void setGroupAdapter(Context context, ArrayList<GroupModel> groups) {
        this.groupAdapter = new GroupAdapter(context, groups);
    }*/

    public String getSelected_group_id() {
        return selected_group_id;
    }

    public void setSelected_group_id(String selected_group_id) {
        this.selected_group_id = selected_group_id;
    }

    public String getSelected_group_member_id() {
        return selected_group_member_id;
    }

    public void setSelected_group_member_id(String selected_group_member_id) {
        this.selected_group_member_id = selected_group_member_id;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public float getZoom() {
        return zoom;
    }

    public void setSelectedGroup(String selected_group_id, String selected_group_name) {
        this.selected_group_id = selected_group_id;
        this.selected_group_name = selected_group_name;
    }

    public void setSelectedGroupMember(String selected_group_member_id, String selected_group_member_name) {
        this.selected_group_member_id = selected_group_member_id;
        this.selected_group_member_name = selected_group_member_name;
    }

    public void clearSelectedGroupMember() {
        this.selected_group_member_id = "";
        this.selected_group_member_name = "";
    }

    public GroupMemberModel getMemberDetails(final String groupId, final String memberId) {
        for (GroupModel group :
                getUserModel().getGroups()) {
            if (group.getId().equals(groupId)) {
                for (GroupMemberModel member :
                        group.getMembers()) {
                    if (member.getId().equals(memberId)) {
                        return member;
                    }
                }
            }
        }
        return null;
    }

    public void setLocalTimeZone(TimeZone localTimeZone) {
        this.localTimeZone = localTimeZone;
    }


    public String getjSessionId() {
        return jSessionId;
    }

    public void setjSessionId(String jSessionId) {
        this.jSessionId = jSessionId;
    }

    public void clearSessionDetails() {
        this.jSessionId = null;
        this.rememberMeId = null;
    }

    public void clearLoginDetails() {
        this.username = null;
        this.password = null;
    }

    public String getRememberMeId() {
        return rememberMeId;
    }

    public void setRememberMeId(String rememberMeId) {
        this.rememberMeId = rememberMeId;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public void clearDataSession() {
        this.myObj = null;
    }

    public boolean isShowMyLocation() {
        return showMyLocation;
    }

    public void setShowMyLocation(boolean showMyLocation) {
        this.showMyLocation = showMyLocation;
    }
}
