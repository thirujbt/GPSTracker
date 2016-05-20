package com.pickzy.moresdk;

import java.util.Arrays;

import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

//import com.facebook.FacebookRequestError;
//import com.facebook.HttpMethod;
//import com.facebook.Request;
//import com.facebook.RequestAsyncTask;
//import com.facebook.Response;
//import com.facebook.Session;

public class MoreSocial {
//	public static String name="",link="",caption="",description="",picture="";
//	public static void publishStory( Activity a) {
//		try{
//			try{
//		if(name.equals("")){
//		name="FxCamera+";
//		Toast.makeText(a, "pub name null", Toast.LENGTH_SHORT).show();
//		}
//			}catch(Exception e){
//				Log.e("name null", "name null");
//			}
//			try{
//				if(link.equals("")){
//					link="https://play.google.com/store/apps/details?id=com.fxcamera&feature=search_result#?t=W251bGwsMSwxLDEsImNvbS5meGNhbWVyYSJd";
//					Toast.makeText(a, "link name null", Toast.LENGTH_SHORT).show();
//				}
//					}catch(Exception e){
//						Log.e("link null", "link null");
//					}
//			try{
//				if(caption.equals("")){
//					caption="FxCamera+";
//					Toast.makeText(a, "caption name null", Toast.LENGTH_SHORT).show();
//				}
//					}catch(Exception e){
//						Log.e("caption null", "caption null");
//					}
//			try{
//			if(description.equals("")){
//				description="FxCamera+";
//				Toast.makeText(a, "description name null", Toast.LENGTH_SHORT).show();
//			}
//				}catch(Exception e){
//					Log.e("description null", "description null");
//				}
//			try{
//				if(picture.equals("")){
//					picture="https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png";
//				}
//					}catch(Exception e){
//						Log.e("picture null", "picture null");
//					}
//		
//		final List<String> PERMISSIONS = Arrays.asList("publish_actions");
//		final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
//		boolean pendingPublishReauthorization = false;
//		 Log.e("publishStory", "publishStory");
//		    Session session = Session.getActiveSession();
//		    if (session != null){
//		        // Check for publish permissions   
//		        List<String> permissions = session.getPermissions();
//		        if (!isSubsetOf(PERMISSIONS, permissions)) {
//		            pendingPublishReauthorization = true;
//		            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(a , PERMISSIONS);
//		            session.requestNewPublishPermissions(newPermissionsRequest);
//		            return;
//		        }
//		        Bundle postParams = new Bundle();
////		        postParams.putString("name", "FxCamera+");
////		        postParams.putString("caption", "FXCamera+");
////		        postParams.putString("description", "FxCamera+");
////		        postParams.putString("link", "https://play.google.com/store/apps/details?id=com.fxcamera&feature=search_result#?t=W251bGwsMSwxLDEsImNvbS5meGNhbWVyYSJd");
////		        postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
//		        postParams.putString("name", name);
//		        postParams.putString("caption", caption);
//		        postParams.putString("description", description);
//		        postParams.putString("link", link);
//		        postParams.putString("picture", picture);
//		        Log.e("publishStory", "publishStory");
//		       
//		        Request.Callback callback= new Request.Callback() {
//		            public void onCompleted(Response response) {
//		                JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
//		                String postId = null;
//		                try {
//		                   postId = graphResponse.getString("id");
//		                } catch (JSONException e) {
//		                   Log.i("","JSON error"+ e.getMessage());
//		                }
//		                FacebookRequestError error = response.getError();
//		                if (error != null){
//		                    } else {
//		                }
//		            }
//		        };
//		        Log.e("publishStory", "publishStory");
//		        Request request = new Request(session, "me/feed", postParams,HttpMethod.POST, callback);
//		        RequestAsyncTask task = new RequestAsyncTask(request);
//		        task.execute();
//		        Log.e("publishStory", "publishStory");
//		    }
//		}catch(NoClassDefFoundError e){
//			Toast.makeText(a, "Developer error : include Facebook SDK in your project", Toast.LENGTH_SHORT).show();
//		}catch(Exception e){
//			Toast.makeText(a, "Error", Toast.LENGTH_SHORT).show();
//		}
//		}
//	 private static boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
//		    for (String string : subset) {
//		        if (!superset.contains(string)) {
//		            return false;
//		        }
//		    }
//		    return true;
//		}
}
