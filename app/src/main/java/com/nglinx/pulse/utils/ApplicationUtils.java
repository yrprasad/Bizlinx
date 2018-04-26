package com.nglinx.pulse.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.nglinx.pulse.constants.ApplicationConstants;
import com.nglinx.pulse.models.UserLoginModel;
import com.nglinx.pulse.models.UserType;
import com.nglinx.pulse.session.DataSession;
import com.nglinx.pulse.session.SharedPrefUtility;
import com.nglinx.pulse.utils.retrofit.RetroUtils;

import java.util.List;

import retrofit.client.Header;


/**
 * Created by yelisetr on 1/26/2017.
 */

public class ApplicationUtils {
    public static void clearLoginRelatedInfo(Context context) {
        SharedPrefUtility.clearprofile(context);
        DataSession.getInstance().clearSessionDetails();
        DataSession.getInstance().clearSelectedGroupMember();
        DataSession.getInstance().clearLoginDetails();
    }


    public static void setLoginRelatedInfoToDataSession(Context context) {
        DataSession.getInstance().setUsername(SharedPrefUtility.getUserName(context));
        DataSession.getInstance().setPassword(SharedPrefUtility.getPassword(context));

        String selectedGroupId = SharedPrefUtility.getSelectedGroupId(context);
        String selectedGroupName = SharedPrefUtility.getSelectedGroupName(context);

        if (!selectedGroupId.equalsIgnoreCase("") && !selectedGroupName.equalsIgnoreCase("")) {
            DataSession.getInstance().setSelectedGroup(selectedGroupId, selectedGroupName);
        }
    }


    public static boolean saveSessionDetails(List<Header> headers) {
        String jSessionId = RetroUtils.getJSessionId(headers);
        String rememberMeId = RetroUtils.getRememberMe(headers);

        if ((jSessionId == null) || (rememberMeId == null) || (jSessionId.trim().length() == 0) || (rememberMeId.trim().length() == 0)) {
            return false;
        } else {
            DataSession.getInstance().setjSessionId(jSessionId);
            DataSession.getInstance().setRememberMeId(rememberMeId);
            return true;
        }
    }


    public static UserLoginModel getUserLoginModelForAuthenticate() {
        final UserLoginModel userLoginModel = new UserLoginModel();
        userLoginModel.setUsername(DataSession.getInstance().getUsername());
        userLoginModel.setPassword(DataSession.getInstance().getPassword());
        userLoginModel.setAppKey(DataSession.getInstance().getAppKey());
        userLoginModel.setType(UserType.android);
        return userLoginModel;
    }

    public static String getJSessionId(List<Header> headers) {

        Header jSessHeader = getHeader(headers, ApplicationConstants.JSESSION_ID_HEADER);

        if (jSessHeader == null) {
            return null;
        }

        return getCookieValue(jSessHeader, ApplicationConstants.JSESSION_ID_HEADER);
    }


    private static Header getHeader(List<Header> headers, String headerStr) {
        Header header = null;

        for (Header tmp : headers) {
            if (tmp.getName().equalsIgnoreCase(ApplicationConstants.COOKIE_HEADER)) {
                if (tmp.getValue().contains(headerStr)) {
                    header = tmp;
                    break;
                }
            }
        }
        return header;
    }

    private static String getCookieValue(Header header, String cookieKey) {
        String[] cookies = header.getValue().split(ApplicationConstants.RESPONSE_HEADER_SEPARATOR);

        if (cookies.length == 0)
            return null;

        for (String cookie :
                cookies) {
            if (cookie.contains(cookieKey)) {
                String[] tmp = cookie.split(ApplicationConstants.COOKIE_KEY_VALUE_SEPARATOR);
                if (tmp.length != 2) {
                    return null;
                }
                return tmp[1];
            }
        }

        return null;
    }


    public static String getRememberMe(List<Header> headers) {

        Header rememberMeHeader = getHeader(headers, ApplicationConstants.REMEMBER_ME_HEADER);

        if (rememberMeHeader == null) {
            return null;
        }

        return getCookieValue(rememberMeHeader, ApplicationConstants.REMEMBER_ME_HEADER);
    }

}