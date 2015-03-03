package example.com.sdi_mrdd.activities;

import android.app.Activity;
import android.app.Dialog;
 import android.app.ProgressDialog;
import android.content.Intent;
 import android.content.SharedPreferences;
 import android.net.Uri;
 import android.os.AsyncTask;
 import android.support.v7.app.ActionBarActivity;
 import android.os.Bundle;
 import android.util.Log;
import android.view.Menu;
 import android.view.MenuItem;
 import android.view.View;
 import android.view.WindowManager;
 import android.webkit.WebView;
 import android.webkit.WebViewClient;
 import android.widget.Button;
import android.widget.Toast;
 import android.graphics.Bitmap;

 import org.apache.http.HttpResponse;
 import org.apache.http.NameValuePair;
 import org.apache.http.client.ClientProtocolException;
 import org.apache.http.client.HttpClient;
 import org.apache.http.client.entity.UrlEncodedFormEntity;
 import org.apache.http.client.methods.HttpPost;
 import org.apache.http.impl.client.DefaultHttpClient;
 import org.apache.http.message.BasicNameValuePair;
 import org.apache.http.util.EntityUtils;
 import org.json.JSONException;
 import org.json.JSONObject;

 import java.io.IOException;
 import java.io.UnsupportedEncodingException;
 import java.util.ArrayList;
 import java.util.List;

import example.com.sdi_mrdd.R;

/**
 * This class handles all login details. A request is made to SDI's
 * authentication server to retrieve a code. A second request is then made with
 * this code to retrieve an authentication token. This token is sent to MRDD's backend
 * so permission can be granted to the application.
 */
public class LoginActivity extends ActionBarActivity {

     /* Client ID for Oauth. Registered in SDI's ADFS server */
     private static String CLIENT_ID = "7e3e1419-204e-4038-b594-80e812d20c6f";

     /* Redirect to same screen. This redirect url is also specifed in SDI's ADFS server.
      * Contains access code after first request to SDI's authentication server is made.
      */
     private static String REDIRECT_URI="https://localhost:8000/callback";

     /* Resource to our app provided by SDI. Used as a parameter to request access code */
     private static String RESOURCE_URL="https://mrdd";

     /* Always this value in grant_type parameter for retrieving token */
     private static String GRANT_TYPE="authorization_code";

     /* Base URL to retrieve token. Requires parameters */
     private static String TOKEN_URL ="https://capstone2015federation.scientificdrilling.com/adfs/oauth2/token";

     /* Base URL to retrieve access code. Requires parameters */
     private static String OAUTH_URL ="https://capstone2015federation.scientificdrilling.com/adfs/oauth2/authorize";

     /* Contains the entire url string for authorization  */
     private String authUrl;

     /* The webview to show the SDI authentication page */
     private WebView web;

     /* Shared preferences to relay the code to the token retrieval asynchronous class */
     private SharedPreferences pref;

     /* Button to sign in */
     private Button signInBtn;

     /* Button to sign out. Currently this starts SDI authentication */
     private Button signOutBtn;

     /* Button to view the Web View */
     private Button webViewBtn;

     /**
      * Set view of the page to the activity login template.
      * Add listeners to sign in button and sign out button.
      * Declare webview attributes for SDI authentication
      *
      * @param savedInstanceState
      */
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_login);
         pref = getSharedPreferences("AppPref", MODE_PRIVATE);

         webViewBtn =  (Button) findViewById(R.id.btn_webview);
         webViewBtn.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
                 startActivity(intent);
             }
         });

         /* Currently sign in button just goes to well view page.
          * TODO: Change to same listener as sign out button once SDI authentication is fully working
          */
         signInBtn =  (Button) findViewById(R.id.btn_sign_in);
         signInBtn.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 Intent intent = new Intent(LoginActivity.this, WellListActivity.class);
                 startActivity(intent);
             }
         });

         /* Currently acts as the SDI authentication button */
         signOutBtn = (Button)findViewById(R.id.btn_sign_out);
         signOutBtn.setOnClickListener(new View.OnClickListener() {

         @Override
         public void onClick(View arg0) {
             /* Dialog which will display SDI's authentication page */
             final Dialog dialog = new Dialog(LoginActivity.this);
             /* Set view of dialog to webview */
             dialog.setContentView(R.layout.activity_auth_dialog);
             /* Full screen dialog */
             dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

             web = (WebView) dialog.findViewById(R.id.webv);
             web.getSettings().setJavaScriptEnabled(true);
             /* Entire URL used to display authentication page and retrieve access code */
             authUrl = OAUTH_URL + "?response_type=code" + "&client_id=" + CLIENT_ID
                     + "&redirect_uri="+ REDIRECT_URI + "&resource=" + RESOURCE_URL;

             web.loadUrl(authUrl);
             web.setWebViewClient(new WebViewClient() {
                 boolean authComplete = false;
                 Intent resultIntent = new Intent();

                 @Override
                 public void onPageStarted(WebView view, String url, Bitmap favicon){
                     super.onPageStarted(view, url, favicon);
                 }
                 /* Used to temporarily store the access code and stores it in preferences */
                 String authCode;

                 /**
                  * Runs after the user has inputted their credentials.
                  * On success, url will contain the access code.
                  * On failure, method will notify user of failed login
                  *
                  * @param view
                  * @param url
                  */
                 @Override
                 public void onPageFinished(WebView view, String url) {
                     super.onPageFinished(view, url);
                     /* Run on login success */
                     if (url.contains("?code=") && authComplete != true) {
                         Uri uri = Uri.parse(url);
                         authCode = uri.getQueryParameter("code");
                         Log.i("", "CODE : " + authCode);
                         authComplete = true;
                         resultIntent.putExtra("code", authCode);

                         LoginActivity.this.setResult(Activity.RESULT_OK, resultIntent);
                         setResult(Activity.RESULT_CANCELED, resultIntent);
                         SharedPreferences.Editor edit = pref.edit();
                         edit.putString("Code", authCode);
                         edit.commit();
                         dialog.dismiss();
                         /*Creates Asynchronous class to retrieve the actual token using the access code*/
                         new TokenGet().execute();
                     }
                     /* Run on login failure */
                     else if(url.contains("error=access_denied")){
                         Log.i("", "ACCESS_DENIED_HERE");
                         resultIntent.putExtra("code", authCode);
                         authComplete = true;
                         setResult(Activity.RESULT_CANCELED, resultIntent);
                         Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                     }
                 }
                 });
                 dialog.setTitle("Authorize SDI");
                 dialog.setCancelable(true);
                 dialog.show();
             }
         });
     }

     /**
      * Specifies which menu to use for this page
      *
      * @param menu
      * @return
      */
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         /*Inflate the menu; this adds items to the action bar if it is present. */
         getMenuInflater().inflate(R.menu.menu_login, menu);
         return true;
     }

     /**
      * Handle action bar item clicks here. There shouldn't be much for the login page
      *
      * @param item
      * @return
      */
     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         /* Gets the id of which menu item was clicked */
         int id = item.getItemId();

         /* Settings menu item was clicked */
         if (id == R.id.action_settings) {
             return true;
         }

         return super.onOptionsItemSelected(item);
     }

     /**
      * Asynchronous class that retrieves the token from SDI authentication server.
      * This class shows a progress dialog while the token is being retrieved. The
      * token retrieved is a JSON Object and must be POSTED to SDI-MRDD-Android's
      * back-end service (Daniel's mock server)
      */
     private class TokenGet extends AsyncTask<String, String, JSONObject> {
         private ProgressDialog pDialog;
         String accessCode;

         /**
          * This method sets up a progress dialog to show while the JSON token
          * is being retrieved
          */
         @Override
         protected void onPreExecute() {
             super.onPreExecute();
             pDialog = new ProgressDialog(LoginActivity.this);
             pDialog.setMessage("Contacting SDI's server ...");
             pDialog.setIndeterminate(false);
             pDialog.setCancelable(true);
             accessCode = pref.getString("Code", "");
             pDialog.show();
         }

         /**
          * This method sets up the URL for the POST request. Parameters
          * are added to the token URL and a post is made.
          *
          * @param args
          * @return  JSONObject  A JSONObject representing the authentication token
          */
         @Override
         protected JSONObject doInBackground(String... args) {
             /* Creating HTTP client */
             HttpClient httpClient = new DefaultHttpClient();

             /* Creating HTTP Post with token URL */
             HttpPost httpPost = new HttpPost(TOKEN_URL);

             /* List to hold parameters to append to our POST request */
             List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);

             /* Add all appropriate parameters to POST request */
             nameValuePair.add(new BasicNameValuePair("grant_type", GRANT_TYPE));
             nameValuePair.add(new BasicNameValuePair("code", accessCode));
             nameValuePair.add(new BasicNameValuePair("client_id", CLIENT_ID));
             nameValuePair.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI));

             try {
                 httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
             } catch (UnsupportedEncodingException e) {
                 e.printStackTrace();
             }

             /* Making the actual HTTP Request and returning the JSON object */
             try {
                 HttpResponse response = httpClient.execute(httpPost);
                 Log.d("Http Response:", response.toString());
                 return new JSONObject(EntityUtils.toString(response.getEntity()));
             } catch (ClientProtocolException e) {
                 e.printStackTrace();
             } catch (IOException e) {
                 e.printStackTrace();

             } catch (JSONException e) {
                 e.printStackTrace();
             }
             /* Return null if no valid JSONObject was retrieved */
             return null;
         }

         /**
          * This method is called after doInBackground(...) is finished.
          * Check that a valid JSON object was received, and if so, log all of the
          * attributes from that object. We need to pass this object to our mock server.
          *
          * @param json
          */
         @Override
         protected void onPostExecute(JSONObject json) {
             pDialog.dismiss();
             if (json != null){
                 try {
                     String tok = json.getString("access_token");
                     String expire = json.getString("expires_in");
                     String refresh = json.getString("token_type");
                     Log.d("Token Access", tok);
                     Log.d("Expire", expire);
                     Log.d("Token Type", refresh);

                     new TokenPost(tok).execute();
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             }else{
                 Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                 pDialog.dismiss();
             }
         }
     }

     /**
      * Asynchronous class that posts the token from SDI authentication server to our
      * external server.
      */
     private class TokenPost extends AsyncTask<String, String, JSONObject> {
         private String token;

         private TokenPost(String token) {
             this.token = token;
         }

         @Override
         protected void onPreExecute() {
             super.onPreExecute();
             System.out.println("Pre Execute");
         }

         /**
          * This method sets up the URL for the POST request. Parameters
          * are added to the token URL and a post is made.
          *
          * @param args
          * @return JSONObject  A JSONObject representing the authentication token
          */
         @Override
         protected JSONObject doInBackground(String... args) {
             /* Creating HTTP client */
             HttpClient httpClient = new DefaultHttpClient();

             /* Creating HTTP Post with token URL */
             HttpPost httpPost = new HttpPost("http://10.0.2.2:5000/authenticate");

             /* List to hold parameters to append to our POST request */
             List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);

             /* Add all appropriate parameters to POST request */
             nameValuePair.add(new BasicNameValuePair("token", token));

             try {
                 httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
             } catch (UnsupportedEncodingException e) {
                 e.printStackTrace();
             }

             /* Making the actual HTTP Request and returning the JSON object */
             try {
                 HttpResponse response = httpClient.execute(httpPost);
                 Log.d("Http Response:", response.toString());
                 return new JSONObject(EntityUtils.toString(response.getEntity()));
             } catch (ClientProtocolException e) {
                 e.printStackTrace();
             } catch (IOException e) {
                 e.printStackTrace();
             } catch (JSONException e) {
                 e.printStackTrace();
             }
             return null;
         }

         @Override
         protected void onPostExecute(JSONObject json) {
             System.out.println("Post Execute");
             Toast.makeText(getApplicationContext(),"Sign in Successful", Toast.LENGTH_SHORT).show();
             Intent intent = new Intent(LoginActivity.this, WellListActivity.class);
             startActivity(intent);
         }
     }
 }
